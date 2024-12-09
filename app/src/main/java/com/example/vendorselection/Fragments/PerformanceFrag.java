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
import com.example.vendorselection.Room.Entity.Performance;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class PerformanceFrag extends Fragment {

    private EditText pid, pvid, psales, prating, peval, pcategory;
    private AppDao appDao;
    private ExecutorService executorService;
    private AppCompatButton add, upd, del;

    public PerformanceFrag() {
        // Required empty public constructor
    }

    public static PerformanceFrag newInstance(String param1, String param2) {
        PerformanceFrag fragment = new PerformanceFrag();
        Bundle args = new Bundle();
        args.putString("param1", param1);
        args.putString("param2", param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            String mParam1 = getArguments().getString("param1");
            String mParam2 = getArguments().getString("param2");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_performance, container, false);

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


        pid = v.findViewById(R.id.pid);
        pvid = v.findViewById(R.id.pvid);
        psales = v.findViewById(R.id.psales);
        prating = v.findViewById(R.id.prating);
        peval = v.findViewById(R.id.peval);
        pcategory = v.findViewById(R.id.pcategory);

        add = v.findViewById(R.id.addP);
        upd = v.findViewById(R.id.updP);
        del = v.findViewById(R.id.delP);


        setupButtonListeners();

        return v;
    }

    private void setupButtonListeners() {

        add.setOnClickListener(v -> {
            if (validateInput(pid, pvid, psales, prating, peval, pcategory)) {
                Performance performance = new Performance();
                performance.performanceId = Integer.parseInt(pid.getText().toString().trim());
                performance.vendorId = Integer.parseInt(pvid.getText().toString().trim());
                performance.salesAmount = Double.parseDouble(psales.getText().toString().trim());
                performance.rating = Double.parseDouble(prating.getText().toString().trim());
                performance.evaluationDate = peval.getText().toString().trim();
                performance.performanceCategory = pcategory.getText().toString().trim();

                executorService.execute(() -> appDao.insertPerformance(performance));

                showDialog("Performance Added", "Performance assessed with rating: " + performance.rating);
            }
        });

        upd.setOnClickListener(v -> {
            String id = pid.getText().toString().trim();
            if (!id.isEmpty()) {
                executorService.execute(() -> {
                    Performance performance = appDao.getPerformanceById(Integer.parseInt(id));
                    if (performance != null) {
                        performance.vendorId = Integer.parseInt(pvid.getText().toString().trim());
                        performance.rating = Double.parseDouble(prating.getText().toString().trim());
                        performance.salesAmount = Double.parseDouble(psales.getText().toString().trim());
                        performance.evaluationDate = peval.getText().toString().trim();
                        performance.performanceCategory = pcategory.getText().toString().trim();

                        appDao.updatePerformance(performance);

                        getActivity().runOnUiThread(() ->
                                showDialog("Performance Updated", "Updated rating: " + performance.rating));
                    } else {
                        getActivity().runOnUiThread(() ->
                                showDialog("Error", "Performance with ID " + id + " not found."));
                    }
                });
            } else {
                pid.setError("Enter Performance ID");
            }
        });

        del.setOnClickListener(v -> {
            String id = pid.getText().toString().trim();
            if (!id.isEmpty()) {
                executorService.execute(() -> {
                    Performance performance = appDao.getPerformanceById(Integer.parseInt(id));
                    if (performance != null) {
                        appDao.deletePerformance(performance);
                        getActivity().runOnUiThread(() ->
                                showDialog("Performance Deleted", "Performance with ID " + id + " deleted."));
                    } else {
                        getActivity().runOnUiThread(() ->
                                showDialog("Error", "Performance with ID " + id + " not found."));
                    }
                });
            } else {
                pid.setError("Enter Performance ID");
            }
        });
    }

    private boolean validateInput(EditText... fields) {
        boolean isValid = true;
        for (EditText field : fields) {
            if (field.getText().toString().trim().isEmpty()) {
                field.setError("Required");
                isValid = false;
            }
        }
        return isValid;
    }

    private void showDialog(String title, String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle(title)
                .setMessage(message)
                .setPositiveButton("OK", null);

        AlertDialog dialog = builder.create();
        dialog.getWindow().setBackgroundDrawableResource(R.drawable.rounded_dialog_background);
        dialog.show();
    }
}
