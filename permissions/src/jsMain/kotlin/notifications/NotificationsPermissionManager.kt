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

package com.splendo.kaluga.permissions.notifications

import com.splendo.kaluga.permissions.Permission
import com.splendo.kaluga.permissions.PermissionManager
import com.splendo.kaluga.permissions.PermissionState

actual class NotificationOptions

actual class NotificationsPermissionManager(actual val notifications: Permission.Notifications, repo: NotificationsPermissionStateRepo) : PermissionManager<Permission.Notifications>(repo) {

    override suspend fun requestPermission() {
        TODO("not implemented")
    }

    override suspend fun initializeState(): PermissionState<Permission.Notifications> {
        TODO("not implemented")
    }

    override suspend fun startMonitoring(interval: Long) {
        TODO("not implemented")
    }

    override suspend fun stopMonitoring() {
        TODO("not implemented")
    }
}

actual class NotificationsPermissionManagerBuilder {

    actual fun create(notifications: Permission.Notifications, repo: NotificationsPermissionStateRepo): NotificationsPermissionManager {
        return NotificationsPermissionManager(notifications, repo)
    }
}
