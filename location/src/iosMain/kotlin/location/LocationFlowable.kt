package com.splendo.kaluga.location

/*

Copyright 2019 Splendo Consulting B.V. The Netherlands

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

     http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.

*/

import com.splendo.kaluga.utils.byOrdinalOrDefault
import kotlinx.coroutines.runBlocking
import platform.CoreLocation.CLAuthorizationStatus
import platform.CoreLocation.CLLocation
import platform.CoreLocation.CLLocationManager
import platform.CoreLocation.CLLocationManagerDelegateProtocol
import platform.Foundation.NSError
import platform.darwin.NSObject

actual class LocationFlowable : BaseLocationFlowable() {

    class Builder(private val locationManager: CLLocationManager = CLLocationManager()) : BaseLocationFlowable.Builder {
        override fun create() = LocationFlowable().addCLLocationManager(locationManager)
    }

    @Suppress("EnumEntryName") // we are modeling an iOS construct so we will stick as close to it as possible
    private enum class CLAuthorizationStatusKotlin {
        // https://developer.apple.com/documentation/corelocation/clauthorizationstatus
        notDetermined,
        restricted,
        denied,
        authorizedAlways,
        authorizedWhenInUse
    }

    private val locationManagerDelegate = object : NSObject(), CLLocationManagerDelegateProtocol {

        override fun locationManager(
            manager: CLLocationManager,
            didUpdateLocations: List<*>
        ) {
            runBlocking {
                (didUpdateLocations.last() as? CLLocation)?.knownLocation?.also { location ->
                    set(location)
                }
            }
        }

        override fun locationManager(manager: CLLocationManager, didUpdateToLocation: CLLocation, fromLocation: CLLocation) = runBlocking {
            set(didUpdateToLocation.knownLocation)
        }

        override fun locationManager(manager: CLLocationManager, didFinishDeferredUpdatesWithError: NSError?) = runBlocking {
            setUnknownLocation()
        }

        override fun locationManager(
            manager: CLLocationManager,
            didChangeAuthorizationStatus: CLAuthorizationStatus
        ) = runBlocking {
            when (Enum.byOrdinalOrDefault(
                didChangeAuthorizationStatus,
                CLAuthorizationStatusKotlin.notDetermined
            )) {
                CLAuthorizationStatusKotlin.restricted -> setUnknownLocation(Location.UnknownReason.NO_PERMISSION_GRANTED)
                CLAuthorizationStatusKotlin.denied -> setUnknownLocation(Location.UnknownReason.PERMISSION_DENIED)
                CLAuthorizationStatusKotlin.authorizedAlways, CLAuthorizationStatusKotlin.authorizedWhenInUse -> {
                    manager.startUpdatingLocation()
                } // do nothing, a location should be published on it's own
                else -> setUnknownLocation()
            }
        }
    }

    private fun addCLLocationManager(locationManager: CLLocationManager = CLLocationManager()) = apply {
        locationManager.delegate = locationManagerDelegate
    }
}
