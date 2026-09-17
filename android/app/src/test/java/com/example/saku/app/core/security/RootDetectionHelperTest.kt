package com.example.saku.app.core.security

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class RootDetectionHelperTest {

    private lateinit var context: Context

    @Before
    fun setUp() {
        context = ApplicationProvider.getApplicationContext()
    }

    @Test
    fun `checkDeviceSecurity returns non-null result and valid list`() {
        val result = RootDetectionHelper.checkDeviceSecurity(context)
        assertNotNull(result)
        assertNotNull(result.reasons)
    }
}
