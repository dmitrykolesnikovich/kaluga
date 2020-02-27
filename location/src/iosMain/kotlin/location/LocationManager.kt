/*
 Copyright (c) 2020. Splendo Consulting B.V. The Netherlands

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

package com.splendo.kaluga.location

import com.splendo.kaluga.permissions.location.CLAuthorizationStatusKotlin
import com.splendo.kaluga.permissions.location.LocationPermissionStateRepo
import com.splendo.kaluga.utils.byOrdinalOrDefault
import kotlinx.coroutines.runBlocking
import platform.CoreLocation.CLAuthorizationStatus
import platform.CoreLocation.CLLocation
import platform.CoreLocation.CLLocationManager
import platform.CoreLocation.CLLocationManagerDelegateProtocol
import platform.Foundation.NSError
import platform.darwin.NSObject

actual class LocationManager(
    private val locationManager: CLLocationManager,
    locationPermissionRepo: LocationPermissionStateRepo,
    autoRequestPermission: Boolean,
    autoEnableLocations: Boolean,
    locationStateRepo: LocationStateRepo
) : BaseLocationManager(
    locationPermissionRepo,
    autoRequestPermission,
    autoEnableLocations,
    locationStateRepo
) {

    class Builder(private val locationManager: CLLocationManager = CLLocationManager()) : BaseLocationManager.Builder {
        override fun create(
            locationPermissionRepo: LocationPermissionStateRepo,
            autoRequestPermission: Boolean,
            autoEnableLocations: Boolean,
            locationStateRepo: LocationStateRepo
        ): BaseLocationManager = LocationManager(
            locationManager,
            locationPermissionRepo,
            autoRequestPermission,
            autoEnableLocations,
            locationStateRepo
        )
    }

    private val locationManagerDelegate = object : NSObject(), CLLocationManagerDelegateProtocol {

        override fun locationManager(manager: CLLocationManager, didUpdateLocations: List<*>) =  runBlocking {
            val locations = didUpdateLocations.mapNotNull { (it as? CLLocation)?.knownLocation }
            if (locations.isNotEmpty()) {
                handleLocationChanged(locations)
            }
        }

        override fun locationManager(manager: CLLocationManager, didUpdateToLocation: CLLocation, fromLocation: CLLocation) = runBlocking {
            handleLocationChanged(didUpdateToLocation.knownLocation)
        }

        override fun locationManager(manager: CLLocationManager, didFinishDeferredUpdatesWithError: NSError?) = runBlocking {
        }

        override fun locationManager(manager: CLLocationManager, didChangeAuthorizationStatus: CLAuthorizationStatus) = runBlocking {
            when (authorizationStatus) {
                CLAuthorizationStatusKotlin.restricted,
                CLAuthorizationStatusKotlin.denied -> handleLocationEnabledChanged(false)
                CLAuthorizationStatusKotlin.authorizedAlways,
                CLAuthorizationStatusKotlin.authorizedWhenInUse -> handleLocationEnabledChanged(true)
                CLAuthorizationStatusKotlin.notDetermined -> {}
            }
        }
    }

    init {
        locationManager.delegate = locationManagerDelegate
    }

    private val authorizationStatus: CLAuthorizationStatusKotlin
        get() = Enum.byOrdinalOrDefault(
            CLLocationManager.authorizationStatus(),
            CLAuthorizationStatusKotlin.notDetermined
        )

    override suspend fun startMonitoringLocationEnabled() {
        locationManager.startUpdatingLocation()
    }

    override suspend fun stopMonitoringLocationEnabled() {
        locationManager.stopUpdatingLocation()
    }

    override suspend fun isLocationEnabled(): Boolean = when (authorizationStatus) {
        // Enabled
        CLAuthorizationStatusKotlin.authorizedAlways,
        CLAuthorizationStatusKotlin.authorizedWhenInUse -> true
        // Disabled
        CLAuthorizationStatusKotlin.restricted,
        CLAuthorizationStatusKotlin.denied,
        CLAuthorizationStatusKotlin.notDetermined -> false
    }

    override suspend fun requestLocationEnable() {
        // No access to UIApplication.openSettingsURLString
        // We have to fallback to alert then?
    }

    override suspend fun startMonitoringLocation() {
        locationManager.startUpdatingLocation()
    }

    override suspend fun stopMonitoringLocation() {
        locationManager.stopUpdatingLocation()
    }
}