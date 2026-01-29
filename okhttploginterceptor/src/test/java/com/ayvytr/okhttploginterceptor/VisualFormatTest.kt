package com.ayvytr.okhttploginterceptor

import org.junit.Test

/**
 * @author Do
 */
class VisualFormatTest {
    val str = "{\n" +
            "    \"code\": \"SUCCESS\",\n" +
            "    \"msg\": \"成功\",\n" +
            "    \"data\": {\n" +
            "        \"id\": 8,\n" +
            "        \"userName\": \"God\",\n" +
            "        \"nickName\": null,\n" +
            "        \"genderDict\": 2,\n" +
            "        \"genderDictText\": \"ft+in\",\n" +
            "        \"lenUnitDict\": 2,\n" +
            "        \"lenUnitDictText\": null,\n" +
            "        \"height\": 70.00,\n" +
            "        \"feHeight\": 5.00,\n" +
            "        \"inHeight\": 10.00,\n" +
            "        \"weightUnitDict\": null,\n" +
            "        \"weightUnitDictText\": null,\n" +
            "        \"weight\": null,\n" +
            "        \"birthday\": \"2016-01-15\"\n" +
            "    }\n" +
            "}"
    @Test
    fun testNull() {
        println(LoggingInterceptor.isSerializeNulls)
        println(LoggingInterceptor.gson.serializeNulls())

        var list = str.formatAsPossible(true, null)
        println(list.toString())

        list = str.formatAsPossible(false, null)
        println(list.toString())
    }
}
