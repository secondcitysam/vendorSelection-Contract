package com.example.vendorselection.Room.Database;


import androidx.room.Database;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

import com.example.vendorselection.Room.DAO.AppDao;
import com.example.vendorselection.Room.Entity.Contract;
import com.example.vendorselection.Room.Entity.Performance;
import com.example.vendorselection.Room.Entity.User;
import com.example.vendorselection.Room.Entity.Vendor;

@Database(entities = {Contract.class, Performance.class, User.class, Vendor.class}, version = 2)
@TypeConverters(DateTypeConverter.class) // Add this line to register the converter
public abstract class AppDatabase extends RoomDatabase {
    public abstract AppDao appDao();
}

