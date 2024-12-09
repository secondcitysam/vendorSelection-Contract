package com.example.vendorselection.Room.Database;

import androidx.room.TypeConverter;

import java.util.Date;

public class DateTypeConverter {

    @TypeConverter
    public static Long fromDate(Date date) {
        return date == null ? null : date.getTime();
    }

    @TypeConverter
    public static Date toDate(Long timestamp) {
        return timestamp == null ? null : new Date(timestamp);
    }
}
