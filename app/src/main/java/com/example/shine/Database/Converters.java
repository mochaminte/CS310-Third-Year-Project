package com.example.shine.Database;

import androidx.room.TypeConverter;
import java.util.UUID;

/**
 * Necessary for Room to convert between UUID and String without having
 * to manually do it oneself.
 */
public class Converters {
    @TypeConverter
    public static String fromUUID(UUID uuid) {
        return uuid.toString();
    }

    @TypeConverter
    public static UUID uuidFromString(String string) {
        return UUID.fromString(string);
    }
}
