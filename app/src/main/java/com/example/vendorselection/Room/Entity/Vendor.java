package com.example.vendorselection.Room.Entity;


import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "Vendor")
public class Vendor {
    @PrimaryKey(autoGenerate = true)
    public int vendorId;

    public String vendorName;
    public String contactPerson;
    public String phoneNumber;
    public String email;
    public String address;

    @Override
    public String toString() {
        return "Vendor{" +
                "vendorId=" + vendorId +
                ", vendorName='" + vendorName + '\'' +
                ", contactPerson='" + contactPerson + '\'' +
                ", phoneNumber='" + phoneNumber + '\'' +
                '}';
    }
}
