package com.example.vendorselection.Room.Entity;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.ForeignKey;
import androidx.room.ColumnInfo;

import java.util.Date;

@Entity(tableName = "Performance",
        foreignKeys = @ForeignKey(entity = Vendor.class,
                parentColumns = "vendorId",
                childColumns = "vendorId"))
public class Performance {
    @PrimaryKey(autoGenerate = true)
    public int performanceId;

    @ColumnInfo(name = "vendorId")
    public int vendorId;

    public double salesAmount;
    public double rating;
    public String evaluationDate;
    public String performanceCategory;

    @Override
    public String toString() {
        return "Performance{" +
                "performanceId=" + performanceId +
                ", vendorId=" + vendorId +
                ", salesAmount=" + salesAmount +
                ", rating=" + rating +
                ", evaluationDate=" + evaluationDate +
                ", performanceCategory='" + performanceCategory + '\'' +
                '}';
    }
}
