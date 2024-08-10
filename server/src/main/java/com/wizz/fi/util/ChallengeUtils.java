package com.wizz.fi.util;

import org.apache.commons.lang3.RandomStringUtils;

import java.security.SecureRandom;

public class ChallengeUtils {

    private static final SecureRandom random = new SecureRandom();

    public static String challenge() {
        return RandomStringUtils.random(32, true, true);
    }

    public static String challenge(int length) {
        return RandomStringUtils.random(length, true, true);
    }
}
