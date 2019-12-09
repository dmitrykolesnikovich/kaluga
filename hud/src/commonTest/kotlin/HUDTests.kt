package com.splendo.kaluga.hud

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

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

class HUDTests {

    class MockHUD: HUD {
        lateinit var onPresentCalled: () -> Unit
        lateinit var onDismissCalled: () -> Unit

        override val isVisible: Boolean = false
        override fun present(animated: Boolean, completion: () -> Unit): HUD {
            onPresentCalled()
            return this
        }

        override fun dismiss(animated: Boolean, completion: () -> Unit) {
            onDismissCalled()
        }

        override fun dismissAfter(timeMillis: Long, animated: Boolean) = apply {
            onDismissCalled()
        }

        override fun setTitle(title: String?) { }
    }

    class MockBuilder: HUD.Builder {
        override var style = HUD.Style.SYSTEM
        override var title: String? = null
        override fun create(): HUD = MockHUD()
    }

    @Test
    fun testBuilder() {
        val builder = MockBuilder()
        assertEquals(builder.style, HUD.Style.SYSTEM)
        assertNull(builder.title)
        builder.build {
            setStyle(HUD.Style.CUSTOM)
            setTitle("Title")
        }
        assertEquals(builder.style, HUD.Style.CUSTOM)
        assertEquals(builder.title, "Title")
    }
}
