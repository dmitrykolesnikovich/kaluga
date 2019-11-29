package com.splendo.kaluga.beacons

import framework.eddystone.BeaconInfo
import framework.eddystone.EddystoneScanner
import framework.eddystone.EddystoneScannerDelegateProtocol
import platform.CoreLocation.*
import platform.Foundation.*
import platform.darwin.NSInteger
import platform.darwin.NSObject

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

actual class BeaconScanner(private val locationManager: CLLocationManager) {

    private val listeners = mutableMapOf<String, Listener>()
    private val lastKnownState = mutableMapOf<String, Boolean>()
    private val eddystoneScanner = EddystoneScanner()

    init {
        locationManager.delegate = LocationDelegate()
    }

    actual fun addListener(beaconId: String, listener: Listener) {
        println("BeaconScanner.addListener on iOS")
        listeners[beaconId.toLowerCase()] = listener
        if (CLLocationManager.isMonitoringAvailableForClass(CLBeaconRegion)) {
            val regionUUID = NSUUID(beaconId)
            val region = CLBeaconRegion(regionUUID, beaconId)
            locationManager.startMonitoringForRegion(region)
            locationManager.startRangingBeaconsInRegion(region)
        }
        if (listeners.size == 1) {
            eddystoneScanner.setDelegateWithDelegate(EddystoneScannerDelegate())
        }
    }

    actual fun removeListener(beaconId: String) {
        println("BeaconScanner.removeListener on iOS")
        val regionUUID = NSUUID(beaconId)
        val region = CLBeaconRegion(regionUUID, beaconId)
        listeners.remove(beaconId.toLowerCase())
        locationManager.stopRangingBeaconsInRegion(region)
        locationManager.stopMonitoringForRegion(region)
        if (listeners.isEmpty()) {
            eddystoneScanner.setDelegateWithDelegate(null)
        }
    }

    inner class EddystoneScannerDelegate : NSObject(), EddystoneScannerDelegateProtocol {

        override fun didFindBeaconWithBeaconScanner(beaconScanner: EddystoneScanner, beaconInfo: BeaconInfo) {
            println("EddystoneScannerDelegate.didFindBeaconWithBeaconScanner $beaconInfo")
            val beaconId = beaconInfo.getBeaconID().getHexString()
            listeners[beaconId]?.onStateUpdate(beaconId, true)
        }

        override fun didLoseBeaconWithBeaconScanner(beaconScanner: EddystoneScanner, beaconInfo: BeaconInfo) {
            println("EddystoneScannerDelegate.didLoseBeaconWithBeaconScanner $beaconInfo")
            val beaconId = beaconInfo.getBeaconID().getHexString()
            listeners[beaconId]?.onStateUpdate(beaconId, false)
        }

        override fun didObserveURLBeaconWithBeaconScanner(beaconScanner: EddystoneScanner, URL: NSURL, RSSI: NSInteger) {

        }

        override fun didUpdateBeaconWithBeaconScanner(beaconScanner: EddystoneScanner, beaconInfo: BeaconInfo) {

        }

    }

    @Suppress("CONFLICTING_OVERLOADS")
    inner class LocationDelegate : NSObject(), CLLocationManagerDelegateProtocol {

        override fun locationManager(manager: CLLocationManager, didRangeBeacons: List<*>, inRegion: CLBeaconRegion) {
            println("LocationDelegate.locationManager didRangeBeacons $didRangeBeacons inRegion $inRegion")
            val ids = didRangeBeacons.filterIsInstance<CLBeacon>().map { it.proximityUUID.toString().toLowerCase() }
            ids.subtract(lastKnownState.keys).forEach {
                if (lastKnownState[it] != true) {
                    lastKnownState[it] = true
                    listeners[it]?.onStateUpdate(it, true)
                }
            }
            lastKnownState.keys.subtract(ids).forEach {
                if (lastKnownState[it] != false) {
                    lastKnownState[it] = false
                    listeners[it]?.onStateUpdate(it, false)
                }
            }
        }
    }
}