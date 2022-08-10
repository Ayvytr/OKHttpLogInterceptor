package com.ayvytr.network.ext.cookie;


import org.junit.Test;

import okhttp3.Cookie;

import static org.junit.Assert.assertEquals;

public class SerializableCookieTest {

    @Test
    public void cookieSerialization() throws Exception {
        Cookie cookie = TestCookieCreator.createPersistentCookie(false);

        byte[] serializedCookie = new SerializableCookie(cookie).encode();
        Cookie deserializedCookie = SerializableCookie.decode(serializedCookie);

        assertEquals(cookie, deserializedCookie);
    }

    @Test
    public void hostOnlyDomainCookieSerialization() throws Exception {
        Cookie cookie = TestCookieCreator.createPersistentCookie(true);

        byte[] serializedCookie = new SerializableCookie(cookie).encode();
        Cookie deserializedCookie = SerializableCookie.decode(serializedCookie);

        assertEquals(cookie, deserializedCookie);
    }

    @Test
    public void nonPersistentCookieSerialization() throws Exception {
        Cookie cookie = TestCookieCreator.createNonPersistentCookie();

        byte[] serializedCookie = new SerializableCookie(cookie).encode();
        Cookie deserializedCookie = SerializableCookie.decode(serializedCookie);

        assertEquals(cookie, deserializedCookie);
    }


}