package com.geocento.webapps.eobroker.common.server.Utils;

import java.security.SecureRandom;
import java.util.Random;

public final class KeyGenerator {
        private static final String symbols = "abcdefghijklmnopqrstuvwxyzABCDEFGJKLMNPRSTUVWXYZ0123456789";
        private final Random secureRandomProvider = new SecureRandom();
        private final char[] buffer;

        public KeyGenerator(int length)
        {
            if (length < 1)
                throw new IllegalArgumentException("length < 1: " + length);
            buffer = new char[length];
        }

        public String CreateKey()
        {
            for (int idx = 0; idx < buffer.length; ++idx)
                buffer[idx] = symbols.charAt(secureRandomProvider.nextInt(symbols.length()));
            return new String(buffer);
        }
    }