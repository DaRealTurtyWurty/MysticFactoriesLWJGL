package dev.turtywurty.mysticfactories.util;

import com.google.gson.JsonObject;

public class GsonReader {
    public static int readIntSafe(JsonObject json, String key, int defaultValue) {
        try {
            return json.get(key).getAsInt();
        } catch (Exception e) {
            return defaultValue;
        }
    }

    public static boolean readBooleanSafe(JsonObject json, String key, boolean defaultValue) {
        try {
            return json.get(key).getAsBoolean();
        } catch (Exception e) {
            return defaultValue;
        }
    }

    public static String readStringSafe(JsonObject json, String key, String defaultValue) {
        try {
            return json.get(key).getAsString();
        } catch (Exception e) {
            return defaultValue;
        }
    }

    public static double readDoubleSafe(JsonObject json, String key, double defaultValue) {
        try {
            return json.get(key).getAsDouble();
        } catch (Exception e) {
            return defaultValue;
        }
    }

    public static float readFloatSafe(JsonObject json, String key, float defaultValue) {
        try {
            return json.get(key).getAsFloat();
        } catch (Exception e) {
            return defaultValue;
        }
    }

    public static long readLongSafe(JsonObject json, String key, long defaultValue) {
        try {
            return json.get(key).getAsLong();
        } catch (Exception e) {
            return defaultValue;
        }
    }

    public static short readShortSafe(JsonObject json, String key, short defaultValue) {
        try {
            return json.get(key).getAsShort();
        } catch (Exception e) {
            return defaultValue;
        }
    }

    public static byte readByteSafe(JsonObject json, String key, byte defaultValue) {
        try {
            return json.get(key).getAsByte();
        } catch (Exception e) {
            return defaultValue;
        }
    }

    public static char readCharSafe(JsonObject json, String key, char defaultValue) {
        try {
            String str = json.get(key).getAsString();
            return !str.isEmpty() ? str.charAt(0) : defaultValue;
        } catch (Exception e) {
            return defaultValue;
        }
    }

    public static byte[] readByteArraySafe(JsonObject json, String key, byte[] defaultValue) {
        try {
            String base64 = json.get(key).getAsString();
            return java.util.Base64.getDecoder().decode(base64);
        } catch (Exception e) {
            return defaultValue;
        }
    }

    public static <T extends Enum<T>> T readEnumSafe(JsonObject json, String key, Class<T> enumClass, T defaultValue) {
        try {
            String enumName = json.get(key).getAsString();
            return Enum.valueOf(enumClass, enumName);
        } catch (Exception e) {
            return defaultValue;
        }
    }
}
