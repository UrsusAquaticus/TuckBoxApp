package com.example.tuckbox.datamodel;

import androidx.room.TypeConverter;

import java.util.Date;

public class DataTypeConverter {
    @TypeConverter
    public static Date dateFromTimestamp(Long value) {
        return value == null ? null : new Date(value);
    }

    @TypeConverter
    public static Long timestampFromDate(Date date) {
        return date == null ? null : date.getTime();
    }
}
