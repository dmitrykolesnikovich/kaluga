package com.splendo.kaluga.keyboard

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

actual class KeyboardHostingView

actual class KeyboardManagerBuilder : BaseKeyboardManagerBuilder() {
    override fun create() = KeyboardManager()
}

actual class KeyboardManager : BaseKeyboardManager {

    override fun show(keyboardHostingView: KeyboardHostingView) {
        TODO("not implemented") // To change body of created functions use File | Settings | File Templates.
    }

    override fun hide() {
        TODO("not implemented") // To change body of created functions use File | Settings | File Templates.
    }
}
