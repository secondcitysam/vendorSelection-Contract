package com.example.vendorselection.Room.Entity;



import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.ForeignKey;
import androidx.room.ColumnInfo;
import androidx.room.Index;

import static androidx.room.ForeignKey.CASCADE;

import java.util.Date;

@Entity(
        foreignKeys = {
                @ForeignKey(entity = Vendor.class, parentColumns = "vendorId", childColumns = "vendorId", onDelete = CASCADE),
                @ForeignKey(entity = User.class, parentColumns = "userId", childColumns = "userId", onDelete = CASCADE)
        },
        indices = {@Index("vendorId"), @Index("userId")}
)
public class Contract {
    @PrimaryKey(autoGenerate = true)
    public int contractId;

    @ColumnInfo(name = "vendorId")
    public int vendorId;
    @ColumnInfo(name = "userId")
    public int userId;

    @ColumnInfo(name = "contractType")
    public String contractType;

    @ColumnInfo(name = "startDate")
    public String startDate;

    @ColumnInfo(name = "endDate")
    public String endDate;

    @ColumnInfo(name = "contractValue")
    public double contractValue;

    @ColumnInfo(name = "status")
    public String status;

    @Override
    public String toString() {
        return "Contract{" +
                "contractId=" + contractId +
                ", vendorId=" + vendorId +
                ", userId=" + userId +
                ", contractType='" + contractType + '\'' +
                ", startDate=" + startDate +
                ", endDate=" + endDate +
                ", contractValue=" + contractValue +
                ", status='" + status + '\'' +
                '}';
    }
}
