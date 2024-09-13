package com.yj.peuteu.common.util;

import java.nio.ByteBuffer;
import java.util.UUID;

public class CodeGenerator {

    public synchronized static String generateCode() {
        long uuid = ByteBuffer.wrap(UUID.randomUUID().toString().getBytes()).getLong();
        return Long.toString(uuid, Character.MAX_RADIX);
    }

    public static String generateWithPrefix(String prefix) {
        String uniqueStr = generateCode();
        return String.format("%s-%s", prefix, uniqueStr).toUpperCase();
    }
}
