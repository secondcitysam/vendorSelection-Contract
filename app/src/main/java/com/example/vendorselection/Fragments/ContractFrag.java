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
import com.example.vendorselection.Room.Entity.Contract;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ContractFrag extends Fragment {

    private AppDao appDao;
    private ExecutorService executorService;
    private EditText cid, cvid, cuid, ctype, csdate, cedate, cvalue, cstatus;
    private AppCompatButton add, upd, del;

    public ContractFrag() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_contract, container, false);


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


        cid = v.findViewById(R.id.cid);
        cvid = v.findViewById(R.id.cvid);
        cuid = v.findViewById(R.id.cuid);
        ctype = v.findViewById(R.id.ctype);
        csdate = v.findViewById(R.id.csdate);
        cedate = v.findViewById(R.id.cedate);
        cvalue = v.findViewById(R.id.cvalue);
        cstatus = v.findViewById(R.id.cstatus);

        add = v.findViewById(R.id.addC);
        upd = v.findViewById(R.id.updC);
        del = v.findViewById(R.id.delC);


        loadListeners();

        return v;
    }

    private void loadListeners() {
        add.setOnClickListener(v -> executorService.execute(() -> {
            if (validateInput()) {
                Contract contract = getContractFromInputs();
                if (contract != null) {
                    appDao.insertContract(contract);
                    showDialog("Contract Signed", "Valid until " + contract.endDate);
                }
            }
        }));

        upd.setOnClickListener(v -> executorService.execute(() -> {
            if (validateInput()) {
                int id = Integer.parseInt(cid.getText().toString().trim());
                Contract existingContract = appDao.getContractById(id);
                if (existingContract != null) {
                    Contract updatedContract = getContractFromInputs();
                    if (updatedContract != null) {
                        updatedContract.contractId = id;
                        appDao.updateContract(updatedContract);
                        showDialog("Contract Updated", "Details have been successfully updated.");
                    }
                } else {
                    showDialog("Update Failed", "No contract found for ID: " + id);
                }
            }
        }));

        del.setOnClickListener(v -> executorService.execute(() -> {
            String idStr = cid.getText().toString().trim();
            if (!idStr.isEmpty()) {
                int id = Integer.parseInt(idStr);
                Contract contract = appDao.getContractById(id);
                if (contract != null) {
                    appDao.deleteContract(contract);
                    showDialog("Contract Terminated", "Successfully removed.");
                } else {
                    showDialog("Delete Failed", "No contract found for ID: " + id);
                }
            } else {
                showDialog("Delete Failed", "Contract ID is required.");
            }
        }));
    }

    private boolean validateInput() {
        return validateField(cid, "Contract ID")
                && validateField(cvid, "Vendor ID")
                && validateField(cuid, "User ID")
                && validateField(ctype, "Contract Type")
                && validateField(csdate, "Start Date")
                && validateField(cedate, "End Date")
                && validateField(cvalue, "Contract Value")
                && validateField(cstatus, "Status");
    }

    private boolean validateField(EditText field, String fieldName) {
        String value = field.getText().toString().trim();
        if (value.isEmpty()) {
            field.setError(fieldName + " is required");
            return false;
        }
        return true;
    }

    private Contract getContractFromInputs() {
        try {
            Contract contract = new Contract();
            contract.contractId = Integer.parseInt(cid.getText().toString().trim());
            contract.vendorId = Integer.parseInt(cvid.getText().toString().trim());
            contract.userId = Integer.parseInt(cuid.getText().toString().trim());
            contract.contractType = ctype.getText().toString().trim();
            contract.startDate = csdate.getText().toString().trim();
            contract.endDate = cedate.getText().toString().trim();
            contract.contractValue = Double.parseDouble(cvalue.getText().toString().trim());
            contract.status = cstatus.getText().toString().trim();
            return contract;
        } catch (NumberFormatException e) {
            showDialog("Input Error", "Ensure numeric fields are correctly entered.");
            return null;
        }
    }

    private void showDialog(String title, String message) {
        requireActivity().runOnUiThread(() -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            builder.setTitle(title)
                    .setMessage(message)
                    .setPositiveButton("OK", null);

            AlertDialog dialog = builder.create();
            dialog.getWindow().setBackgroundDrawableResource(R.drawable.rounded_dialog_background);
            dialog.show();
        });
    }
}
