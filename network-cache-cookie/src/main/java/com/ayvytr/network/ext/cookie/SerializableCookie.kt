package com.ayvytr.network.ext.cookie

import okhttp3.Cookie
import java.io.*

/**
 * @author Ayvytr ['s GitHub](https://github.com/Ayvytr)
 * @since 3.0.0
 */
class SerializableCookie(@Transient var cookie: Cookie) : Serializable {

    fun encode(): ByteArray? {
        return try {
            val bos = ByteArrayOutputStream()
            val oos = ObjectOutputStream(bos)
            oos.writeObject(this)
            val bytes = bos.toByteArray()
            oos.close()
            bos.close()
            bytes
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    @Throws(IOException::class)
    private fun writeObject(out: ObjectOutputStream) {
        out.writeObject(cookie.name)
        out.writeObject(cookie.value)
        out.writeLong(if (cookie.persistent) cookie.expiresAt else NON_VALID_EXPIRES_AT)
        out.writeObject(cookie.domain)
        out.writeObject(cookie.path)
        out.writeBoolean(cookie.secure)
        out.writeBoolean(cookie.httpOnly)
        out.writeBoolean(cookie.hostOnly)
    }

    @Throws(IOException::class, ClassNotFoundException::class)
    private fun readObject(ois: ObjectInputStream) {
        val builder = Cookie.Builder()
        builder.name((ois.readObject() as String))
        builder.value((ois.readObject() as String))
        val expiresAt = ois.readLong()
        if (expiresAt != NON_VALID_EXPIRES_AT) {
            builder.expiresAt(expiresAt)
        }
        val domain = ois.readObject() as String
        builder.domain(domain)
        builder.path((ois.readObject() as String))
        if (ois.readBoolean()) builder.secure()
        if (ois.readBoolean()) builder.httpOnly()
        if (ois.readBoolean()) builder.hostOnlyDomain(domain)
        cookie = builder.build()
    }


    override fun equals(other: Any?): Boolean {
        if (other !is SerializableCookie) return false
        return cookie == other.cookie
    }

    override fun hashCode(): Int {
        return cookie.hashCode()
    }

    companion object {
        private val TAG = SerializableCookie::class.java.simpleName
        private const val serialVersionUID = -8594045714036645534L
        private const val NON_VALID_EXPIRES_AT = -1L

        @JvmStatic
        fun decode(bytes: ByteArray): Cookie? {
            return try {
                val bis = ByteArrayInputStream(bytes)
                val ois = ObjectInputStream(bis)
                val cookie = (ois.readObject() as SerializableCookie).cookie
                ois.close()
                bis.close()
                cookie
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        }

        fun decorateAll(cookies: Collection<Cookie>): List<SerializableCookie> {
            val identifiableCookies: MutableList<SerializableCookie> = ArrayList(cookies.size)
            for (cookie in cookies) {
                identifiableCookies.add(SerializableCookie(cookie))
            }
            return identifiableCookies
        }
    }
}