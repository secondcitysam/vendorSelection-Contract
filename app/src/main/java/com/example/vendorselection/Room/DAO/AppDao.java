package com.example.vendorselection.Room.DAO;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;
import androidx.room.Delete;
import androidx.room.Transaction;

import com.example.vendorselection.Room.Entity.Contract;
import com.example.vendorselection.Room.Entity.Performance;
import com.example.vendorselection.Room.Entity.User;
import com.example.vendorselection.Room.Entity.Vendor;

import java.util.List;


@Dao
public interface AppDao {


    @Query("SELECT * FROM User WHERE username = :username AND password = :password")
    User loginUser(String username, String password);


    @Query("SELECT * FROM User WHERE username = :username")
    User getUserByUsername(String username);


    //insert ops
    @Insert
    void insertContract(Contract contract);

    @Insert
    void insertVendor(Vendor vendor);

    @Insert
    void insertUser(User user);

    @Insert
    void insertPerformance(Performance performance);

    //update ops
    @Update
    void updateContract(Contract contract);

    @Update
    void updateVendor(Vendor vendor);

    @Update
    void updateUser(User user);

    @Update
    void updatePerformance(Performance performance);

    //delete ops
    @Delete
    void deleteContract(Contract contract);

    @Delete
    void deleteVendor(Vendor vendor);

    @Delete
    void deleteUser(User user);

    @Delete
    void deletePerformance(Performance performance);

    // Select Ops
    @Query("SELECT * FROM Contract WHERE contractId = :contractId")
    Contract getContractById(int contractId);

    @Query("SELECT * FROM Vendor WHERE vendorId = :vendorId")
    Vendor getVendorById(int vendorId);

    @Query("SELECT * FROM User WHERE userId = :userId")
    User getUserById(int userId);

    @Query("SELECT * FROM Performance WHERE performanceId = :performanceId")
    Performance getPerformanceById(int performanceId);

    @Query("SELECT * FROM Contract")
    List<Contract> getAllContracts();

    @Query("SELECT * FROM Vendor")
    List<Vendor> getAllVendors();

    @Query("SELECT vendorId,vendorName FROM Vendor")
    List<Vendor> getAllVendorsB();
    @Query("SELECT * FROM User")
    List<User> getAllUsers();

    @Query("SELECT * FROM Performance")
    List<Performance> getAllPerformances();

    // Join Ops
    @Query("SELECT * FROM Contract JOIN Vendor ON Contract.vendorId = Vendor.vendorId WHERE Vendor.vendorId = :vendorId")
    List<Contract> getContractsByVendorId(int vendorId);



    // Aggregate Ops
    @Query("SELECT SUM(salesAmount) FROM Performance WHERE vendorId = :vendorId")
    double getTotalSalesByVendor(int vendorId);

    // Subqueries
    @Query("SELECT * FROM Vendor WHERE vendorId IN (SELECT vendorId FROM Contract WHERE userId = :userId)")
    List<Vendor> getVendorsByUserId(int userId);

    // Transactions (Atomic operations to ensure consistency)
    @Transaction
    @Query("SELECT * FROM Contract WHERE contractId = :contractId")
    List<Contract> getContractAndVendorDetails(int contractId);

    @Transaction
    @Query("SELECT * FROM Vendor WHERE vendorId = :vendorId")
    List<Vendor> getVendorAndPerformanceDetails(int vendorId);

    // Cursors (Efficiently handling large queries)
    @Query("SELECT * FROM Performance WHERE vendorId = :vendorId LIMIT 1000")
    List<Performance> getPerformanceCursor(int vendorId);


    @Query("Select avg(rating) from performance where vendorId = :vendorID ")
    double getAvgRatingOfVendor(int vendorID);



    @Query("SELECT sum(contractId) FROM Contract JOIN Vendor ON Contract.vendorId = Vendor.vendorId WHERE Vendor.vendorId = :vendorId")
    int getTotalContractsByVendorId(int vendorId);



}
