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

package com.splendo.kaluga.bluetooth.device

import android.bluetooth.*
import android.content.Context
import com.splendo.kaluga.base.ApplicationHolder
import com.splendo.kaluga.bluetooth.DefaultGattServiceWrapper
import com.splendo.kaluga.bluetooth.Service
import com.splendo.kaluga.bluetooth.uuidString
import com.splendo.kaluga.state.StateRepo
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch


internal actual class DeviceConnectionManager(private val context: Context,
                                              connectionSettings: ConnectionSettings,
                                              deviceHolder: DeviceHolder,
                                              stateRepo: StateRepo<DeviceState>)
    : BaseDeviceConnectionManager(connectionSettings, deviceHolder, stateRepo), CoroutineScope by stateRepo  {

    class Builder(private val context: Context = ApplicationHolder.applicationContext) : BaseDeviceConnectionManager.Builder {
        override fun create(connectionSettings: ConnectionSettings, deviceHolder: DeviceHolder, repoAccessor: StateRepo<DeviceState>): BaseDeviceConnectionManager {
            return DeviceConnectionManager(context, connectionSettings, deviceHolder, repoAccessor)
        }
    }

    private val device = deviceHolder.device
    private var gatt: CompletableDeferred<BluetoothGattWrapper> = CompletableDeferred()
    private val callback = object : BluetoothGattCallback() {

        override fun onReadRemoteRssi(gatt: BluetoothGatt?, rssi: Int, status: Int) {
            launch {
                stateRepo.takeAndChangeState { it.rssiDidUpdate(rssi) }
            }
        }

        override fun onCharacteristicRead(gatt: BluetoothGatt?, characteristic: BluetoothGattCharacteristic?, status: Int) {
            characteristic ?: return

            updateCharacteristic(characteristic)
        }

        override fun onCharacteristicWrite(gatt: BluetoothGatt?, characteristic: BluetoothGattCharacteristic?, status: Int) {
            characteristic ?: return

            updateCharacteristic(characteristic)
        }

        override fun onServicesDiscovered(gatt: BluetoothGatt?, status: Int) {
            launch {
                val services = gatt?.services?.map { Service(DefaultGattServiceWrapper(it), stateRepo) } ?: emptyList()
                handleScanCompleted(services)
            }
        }

        override fun onDescriptorWrite(gatt: BluetoothGatt?, descriptor: BluetoothGattDescriptor?, status: Int) {
            descriptor?.let {
                updateDescriptor(it)
            }
        }

        override fun onCharacteristicChanged(gatt: BluetoothGatt?, characteristic: BluetoothGattCharacteristic?) {
            characteristic ?: return
            updateCharacteristic(characteristic)
        }

        override fun onDescriptorRead(gatt: BluetoothGatt?, descriptor: BluetoothGattDescriptor?, status: Int) {
            descriptor ?: return
            updateDescriptor(descriptor)
        }

        override fun onConnectionStateChange(gatt: BluetoothGatt?, status: Int, newState: Int) {
            launch {
                when (newState) {
                    BluetoothProfile.STATE_DISCONNECTED -> {
                        handleDisconnect {
                            gatt?.close()
                            this@DeviceConnectionManager.gatt = CompletableDeferred()
                        }
                    }
                    BluetoothProfile.STATE_CONNECTED -> {
                        handleConnect()
                    }
                }
            }
        }
    }

    override suspend fun connect() {
        unpair()
        if (gatt.isCompleted) {
            gatt.getCompleted().connect()
        } else {
            gatt.complete(device.connectGatt(context, false, callback))
        }
    }

    override suspend fun discoverServices() {
        gatt.await().discoverServices()
    }

    override suspend fun disconnect() {
        gatt.await().disconnect()
    }

    override suspend fun readRssi() {
        gatt.await().readRemoteRssi()
    }

    override suspend fun performAction(action: DeviceAction): Boolean {
        currentAction = action
        return when(action) {
            is DeviceAction.Read.Characteristic -> gatt.await().readCharacteristic(action.characteristic.characteristic)
            is DeviceAction.Read.Descriptor -> gatt.await().readDescriptor(action.descriptor.descriptor)
            is DeviceAction.Write.Characteristic -> {
                action.characteristic.characteristic.value = action.newValue
                gatt.await().writeCharacteristic(action.characteristic.characteristic)
            }
            is DeviceAction.Write.Descriptor -> {
                action.descriptor.descriptor.value = action.newValue
                gatt.await().writeDescriptor(action.descriptor.descriptor)
            }
            is DeviceAction.Notification -> {
                val uuid = action.characteristic.uuid.uuidString
                if (action.enable) {
                    notifyingCharacteristics[uuid] = action.characteristic
                } else notifyingCharacteristics.remove(uuid)
                val result = gatt.await().setCharacteristicNotification(action.characteristic.characteristic, action.enable)
                // Action always completes. Launch in separate coroutine to make sure this action can be completed
                launch {
                    handleCurrentActionCompleted()
                }
                result
            }
        }
    }

    private fun updateCharacteristic(characteristic: BluetoothGattCharacteristic) {
        launch {
            handleUpdatedCharacteristic(characteristic.uuid) {
                it.characteristic.value = characteristic.value
            }
        }
    }

    private fun updateDescriptor(descriptor: BluetoothGattDescriptor) {
        launch {
            handleUpdatedDescriptor(descriptor.uuid) {
                it.descriptor.value = descriptor.value
            }
        }
    }

    private fun unpair() {
        // unpair to prevent connection problems
        if (device.bondState != BluetoothDevice.BOND_NONE) {
            device.removeBond()
        }
    }
}