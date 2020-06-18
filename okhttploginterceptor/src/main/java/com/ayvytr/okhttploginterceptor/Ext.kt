package com.ayvytr.okhttploginterceptor

import com.google.gson.GsonBuilder
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import okhttp3.MediaType
import okhttp3.RequestBody
import okhttp3.ResponseBody
import okio.Buffer
import java.io.StringReader
import java.io.StringWriter
import java.nio.charset.Charset
import javax.xml.transform.OutputKeys
import javax.xml.transform.Source
import javax.xml.transform.TransformerException
import javax.xml.transform.TransformerFactory
import javax.xml.transform.stream.StreamResult
import javax.xml.transform.stream.StreamSource


/**
 * @author Ayvytr ['s GitHub](https://github.com/Ayvytr)
 * @since 3.0.0
 */

internal fun String.appendLine(): String {
    if (length >= LoggingInterceptor.MAX_LINE_LENGTH) {
        return this
    }

    val sb = StringBuilder(this)
    repeat(LoggingInterceptor.MAX_LINE_LENGTH - length) {
        sb.append(LoggingInterceptor.CLINE)
    }
    return sb.toString()
}

/**
 * 是否可以解析
 * @return `true` 可以解析
 */
fun MediaType.isParsable(): Boolean {
    return (isText() || isPlain() || isJson() || isForm() || isHtml() || isXml())
}

fun MediaType.isText(): Boolean {
    return subtype.toLowerCase().contains("text")
}

fun MediaType.isPlain(): Boolean {
    return subtype.toLowerCase().contains("plain")
}

fun MediaType.isJson(): Boolean {
    return subtype.toLowerCase().contains("json")
}

fun MediaType.isXml(): Boolean {
    return subtype.toLowerCase().contains("xml")
}

fun MediaType.isHtml(): Boolean {
    return subtype.toLowerCase().contains("html")
}

fun MediaType.isForm(): Boolean {
    return subtype.toLowerCase().contains("x-www-form-urlencoded")
}

fun RequestBody.bodyString(charset: Charset = Charsets.UTF_8): String {
    val buffer = Buffer()
    writeTo(buffer)
    return buffer.readString(charset)
}

fun ResponseBody.bodyString(charset: Charset = Charsets.UTF_8): String {
    val buffer = source().buffer.clone()
    return buffer.readString(charset)
}

fun RequestBody.formatAsPossible(visualFormat: Boolean = true,
                                 maxLineLength: Int = LoggingInterceptor.MAX_LINE_LENGTH): List<String> {
    val contentType = contentType()
    val charset: Charset = contentType?.charset(Charsets.UTF_8) ?: Charsets.UTF_8
    return if (visualFormat) {
        bodyString(charset).formatAsPossible(contentType, maxLineLength)
    } else {
        bodyString(charset).separateByLength(maxLineLength)
    }
}

fun ResponseBody.formatAsPossible(visualFormat: Boolean = true,
                                  maxLineLength: Int = LoggingInterceptor.MAX_LINE_LENGTH): List<String> {
    val contentType = contentType()
    val charset: Charset = contentType?.charset(Charsets.UTF_8) ?: Charsets.UTF_8
    return if (visualFormat) {
        bodyString(charset).formatAsPossible(contentType, maxLineLength)
    } else {
        bodyString(charset).separateByLength(maxLineLength)
    }
}

fun String.formatAsPossible(contentType: MediaType?,
                            maxLineLength: Int = LoggingInterceptor.MAX_LINE_LENGTH): List<String> {
    if (isNullOrEmpty()) {
        return listOf("[Empty]")
    }

    if (contentType == null) {
        if (isGuessJson()) {
            try {
                return jsonFormat()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    } else {
        try {
            if (contentType.isJson()) {
                return jsonFormat()
            } else if (isGuessJson()) {
                return jsonFormat()
            } else if (contentType.isXml() && startsWith("<") && endsWith(">")) {
                return xmlFormat()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    return separateByLength(maxLineLength)
}

fun String.separateByLength(maxLineLength: Int = LoggingInterceptor.MAX_LINE_LENGTH): List<String> {
    if (isNullOrEmpty()) {
        return emptyList()
    }
    if (length <= maxLineLength) {
        return listOf(this)
    }

    var lineNum = length / maxLineLength
    if (length % maxLineLength != 0) {
        lineNum++
    }

    val list = mutableListOf<String>()
    for (i in 1..lineNum) {
        if (i < lineNum) {
            list.add(substring((i - 1) * maxLineLength, i * maxLineLength))
        } else {
            list.add(substring((i - 1) * maxLineLength, length))
        }
    }

    return list
}

fun String.jsonFormat(): List<String> {
    val jsonParser = JsonParser()
    val jsonObject: JsonObject = jsonParser.parse(this).asJsonObject
    val gson = GsonBuilder().setPrettyPrinting().create()
    return StringReader(gson.toJson(jsonObject)).readLines()
}

/**
 * xml格式化不完善，暂时不改了
 */
fun String.xmlFormat(): List<String> {
    if (isNullOrEmpty()) {
        return listOf("[Empty]")
    }

    try {
        val xmlInput: Source = StreamSource(StringReader(this))
        val xmlOutput = StreamResult(StringWriter())
        val transformer = TransformerFactory.newInstance().newTransformer()
        transformer.setOutputProperty(OutputKeys.INDENT, "yes")
        transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2")
        transformer.transform(xmlInput, xmlOutput)
        return StringReader(xmlOutput.writer.toString().replaceFirst(">".toRegex(), ">\n"))
            .readLines()
    } catch (e: TransformerException) {
        e.printStackTrace()
    }

    return separateByLength()
}

fun String.isGuessJson(): Boolean {
    val trim = trim()
    return (trim.startsWith("{") && trim.endsWith("}")) ||
            ((trim.startsWith("[") && trim.endsWith("]")))
}
