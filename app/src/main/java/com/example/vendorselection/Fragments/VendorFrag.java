package com.example.vendorselection.Fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.example.vendorselection.R;
import com.example.vendorselection.Room.DAO.AppDao;
import com.example.vendorselection.Room.Database.AppDatabase;
import com.example.vendorselection.Room.Entity.Performance;
import com.example.vendorselection.Room.Entity.Vendor;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link VendorFrag#newInstance} factory method to
 * create an instance of this fragment.
 */
public class VendorFrag extends Fragment {

    // Fragment arguments
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    public VendorFrag() {
        // Required empty public constructor
    }

    public static VendorFrag newInstance(String param1, String param2) {
        VendorFrag fragment = new VendorFrag();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    private AppDao appDao;
    private ExecutorService executorService;
    private ListView vendorListView;

    private List<Vendor> vendorList;
    private ArrayAdapter<Vendor> adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_vendor, container, false);

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

        vendorListView = view.findViewById(R.id.list1);
        vendorList = new ArrayList<>();

        loadVendors();

        return view;
    }

    private void loadVendors() {
        executorService.execute(() -> {
            try {

                List<Vendor> fetchedVendors = appDao.getAllVendorsB();
                getActivity().runOnUiThread(() -> {
                    vendorList.clear();
                    vendorList.addAll(fetchedVendors);


                    adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1, vendorList);
                    vendorListView.setAdapter(adapter);

                    setupListViewClickListener();
                });
            } catch (Exception e) {
                getActivity().runOnUiThread(() -> showDialog("Error", "Unable to load vendors."));
            }
        });
    }

    private void setupListViewClickListener() {
        vendorListView.setOnItemClickListener((parent, view, position, id) -> {
            Vendor selectedVendor = vendorList.get(position);
            int vendorId = selectedVendor.vendorId;

            executorService.execute(() -> {
                try {
                    Vendor vendor = appDao.getVendorById(vendorId);
                    double performance = appDao.getAvgRatingOfVendor(vendorId);

                    getActivity().runOnUiThread(() -> {
                        if (vendor != null && performance!=0) {
                            showDialog(vendor.vendorName + "'s", "Rating: " + performance);
                        } else {
                            showDialog("Vendor Not Found", "Vendor ID: " + vendorId);
                        }
                    });
                } catch (Exception e) {
                    getActivity().runOnUiThread(() -> showDialog("Error", "Unable to fetch vendor details."));
                }
            });
        });
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
