package com.sigep.utils;

import java.nio.ByteBuffer;
import java.security.SecureRandom;
import java.util.UUID;

public class UUIDvs7 {
    private static final SecureRandom random = new SecureRandom();

    // Constructor privado para prevenir la instanciación
    private UUIDvs7(){
        // Evita la instanciación de la clase de utilidad
        throw new IllegalStateException("Clase de utilidad, no debe ser instanciada.");
    }

    public static String randomUUID() {
        byte[] value = randomBytes();
        ByteBuffer buf = ByteBuffer.wrap(value);
        long high = buf.getLong();
        long low = buf.getLong();
        return new UUID(high, low).toString();
    }

    private static byte[] randomBytes() {

        byte[] value = new byte[16];
        random.nextBytes(value);

        ByteBuffer timestamp = ByteBuffer.allocate(Long.BYTES);
        timestamp.putLong(System.currentTimeMillis());

        System.arraycopy(timestamp.array(), 2, value, 0, 6);

        value[6] = (byte) ((value[6] & 0x0F) | 0x70);
        value[8] = (byte) ((value[8] & 0x3F) | 0x80);

        return value;
    }
}
