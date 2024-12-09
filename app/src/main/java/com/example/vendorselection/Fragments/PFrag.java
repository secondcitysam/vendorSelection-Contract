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

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link PFrag#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PFrag extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public PFrag() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment PFrag.
     */
    // TODO: Rename and change types and number of parameters
    public static PFrag newInstance(String param1, String param2) {
        PFrag fragment = new PFrag();
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
    private AppCompatButton b1,b2,b3;
    private EditText e1;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v= inflater.inflate(R.layout.fragment_p, container, false);

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

        e1 = v.findViewById(R.id.searchv2);
        b1 = v.findViewById(R.id.tc1);
        b2 = v.findViewById(R.id.tc2);
        b3 = v.findViewById(R.id.tc3);

        String e11 = e1.getText().toString().trim();

        if(!e11.isEmpty())
        {
            loadData(e11);
        }
        return v;
    }

    private void loadData(String e11) {

        b1.setOnClickListener( v -> executorService.execute(()->{

            try {


                int total = appDao.getTotalContractsByVendorId(Integer.parseInt(e11));

                getActivity().runOnUiThread(()->{
                    showDialog("Total Contracts ", String.valueOf(total));

                });


            }catch (Exception e)
            {

                getActivity().runOnUiThread(()->showDialog("Error","Not found"));

            }



        }));

        b2.setOnClickListener( v -> executorService.execute(()->{

            try {


                double avg = appDao.getAvgRatingOfVendor(Integer.parseInt(e11));


                getActivity().runOnUiThread(()->{
                    showDialog("Total Avg Rating ", String.valueOf(avg));

                });


            }catch (Exception e)
            {

                getActivity().runOnUiThread(()->showDialog("Error","Not found"));

            }



        }));

        b3.setOnClickListener( v -> executorService.execute(()->{

            try {


                double sales = appDao.getTotalSalesByVendor(Integer.parseInt(e11));


                getActivity().runOnUiThread(()->{
                    showDialog("Total Avg Rating ", String.valueOf(sales));

                });


            }catch (Exception e)
            {

                getActivity().runOnUiThread(()->showDialog("Error","Not found"));

            }



        }));

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