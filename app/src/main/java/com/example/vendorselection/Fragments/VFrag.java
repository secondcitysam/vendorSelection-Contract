package com.example.vendorselection.Fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.Fragment;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.example.vendorselection.R;
import com.example.vendorselection.Room.DAO.AppDao;
import com.example.vendorselection.Room.Database.AppDatabase;
import com.example.vendorselection.Room.Entity.Vendor;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class VFrag extends Fragment {

    private EditText vid, vname, vcontact, vphone, vemail, vaddress;
    private AppCompatButton add, upd, del;
    private AppDao appDao;
    private ExecutorService executorService;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_v, container, false);

        AppDatabase database = Room.databaseBuilder(
                        getContext(),
                        AppDatabase.class,
                        "contract_vendor_database"
                )
                .addCallback(new RoomDatabase.Callback() {
                    @Override
                    public void onCreate(@NonNull SupportSQLiteDatabase db) {
                        super.onCreate(db);
                        db.execSQL("PRAGMA foreign_keys=ON;");
                    }
                })
                .build();

        appDao = database.appDao();
        executorService = Executors.newSingleThreadExecutor();

        initializeViews(v);
        loadListeners();

        return v;
    }

    private void initializeViews(View v) {
        vid = v.findViewById(R.id.vid);
        vname = v.findViewById(R.id.vname);
        vcontact = v.findViewById(R.id.vcontact);
        vphone = v.findViewById(R.id.vphone);
        vemail = v.findViewById(R.id.vemail);
        vaddress = v.findViewById(R.id.vaddress);
        add = v.findViewById(R.id.addV);
        upd = v.findViewById(R.id.updV);
        del = v.findViewById(R.id.delV);
    }

    private void loadListeners() {
        add.setOnClickListener(v -> executorService.execute(() -> {
            if (validateInput()) {
                Vendor vendor = getVendorFromInputs();
                if (vendor != null) {
                    appDao.insertVendor(vendor);
                    showDialog("Success", "Vendor " + vendor.vendorName + " inserted successfully.");
                }
            }
        }));

        upd.setOnClickListener(v -> executorService.execute(() -> {
            if (validateInput()) {
                int id = Integer.parseInt(vid.getText().toString().trim());
                Vendor existingVendor = appDao.getVendorById(id);

                if (existingVendor != null) {
                    Vendor updatedVendor = getVendorFromInputs();
                    if (updatedVendor != null) {
                        updatedVendor.vendorId = id;
                        appDao.updateVendor(updatedVendor);
                        showDialog("Success", "Vendor " + updatedVendor.vendorName + " updated successfully.");
                    }
                } else {
                    showDialog("Error", "No vendor found with ID: " + id);
                }
            }
        }));

        del.setOnClickListener(v -> executorService.execute(() -> {
            String idStr = vid.getText().toString().trim();
            if (!idStr.isEmpty()) {
                int id = Integer.parseInt(idStr);
                Vendor vendor = appDao.getVendorById(id);

                if (vendor != null) {
                    appDao.deleteVendor(vendor);
                    showDialog("Success", "Vendor " + vendor.vendorName + " deleted successfully.");
                } else {
                    showDialog("Error", "No vendor found with ID: " + id);
                }
            } else {
                showDialog("Error", "Vendor ID is required for deletion.");
            }
        }));
    }

    private boolean validateInput() {
        return validateField(vid, "Vendor ID")
                && validateField(vname, "Vendor Name")
                && validateField(vcontact, "Contact Person")
                && validateField(vphone, "Phone Number")
                && validateField(vemail, "Email")
                && validateField(vaddress, "Address");
    }

    private boolean validateField(EditText field, String fieldName) {
        String value = field.getText().toString().trim();
        if (value.isEmpty()) {
            requireActivity().runOnUiThread(() -> {
                field.setError(fieldName + " is required");
            });
            return false;
        }
        return true;
    }


    private Vendor getVendorFromInputs() {
        try {
            Vendor vendor = new Vendor();
            vendor.vendorId = Integer.parseInt(vid.getText().toString().trim());
            vendor.vendorName = vname.getText().toString().trim();
            vendor.contactPerson = vcontact.getText().toString().trim();
            vendor.phoneNumber = vphone.getText().toString().trim();
            vendor.email = vemail.getText().toString().trim();
            vendor.address = vaddress.getText().toString().trim();
            return vendor;
        } catch (NumberFormatException e) {
            showDialog("Error", "Ensure numeric fields are entered correctly.");
            return null;
        }
    }

    private void showDialog(String title, String message) {
        requireActivity().runOnUiThread(() -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
            builder.setTitle(title)
                    .setMessage(message)
                    .setPositiveButton("OK", null);

            AlertDialog dialog = builder.create();
            dialog.getWindow().setBackgroundDrawableResource(R.drawable.rounded_dialog_background);
            dialog.show();
        });
    }
}
