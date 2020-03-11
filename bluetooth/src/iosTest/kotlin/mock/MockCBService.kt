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

package com.splendo.kaluga.bluetooth.mock

import com.splendo.kaluga.bluetooth.UUID
import com.splendo.kaluga.bluetooth.randomUUID
import platform.CoreBluetooth.CBCharacteristic
import platform.CoreBluetooth.CBService
import platform.CoreBluetooth.CBUUID

class MockCBService(private val uuid: UUID = randomUUID()) : CBService() {

    external override fun UUID(): CBUUID {
        return uuid
    }

    external override fun characteristics(): List<CBCharacteristic>? {
        return super.characteristics()
    }

}
