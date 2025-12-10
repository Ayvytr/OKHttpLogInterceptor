package com.ayvytr.okhttploginterceptor

import org.junit.Test

/**
 * @author Do
 */
class JsonTest {
    @Test
    fun testParse() {
        val str = "[{\"detailList\":[{\"actionCode\":\"doubleClick\",\"executeCategoryCode\":\"vehicleControl\",\"executeCode\":\"prevSong\"}],\"deviceCode\":\"device1\"}]"
        for (s in str.jsonFormat()) {
            println(s)
        }
        val str2 = "{\"detailList\":[{\"actionCode\":\"doubleClick\",\"executeCategoryCode\":\"vehicleControl\",\"executeCode\":\"prevSong\"}],\"deviceCode\":\"device1\"}"
        for (s in str2.jsonFormat()) {
            println(s)
        }
        val str3 = ""
        println(str3.jsonFormat())
        val str4 = "\"actionCode\":1"
        println(str4.jsonFormat())
    }
}