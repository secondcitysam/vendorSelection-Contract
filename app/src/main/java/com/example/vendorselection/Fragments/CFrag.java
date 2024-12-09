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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;

import com.example.vendorselection.R;
import com.example.vendorselection.Room.DAO.AppDao;
import com.example.vendorselection.Room.Database.AppDatabase;
import com.example.vendorselection.Room.Entity.Contract;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link CFrag#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CFrag extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public CFrag() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment CFrag.
     */
    // TODO: Rename and change types and number of parameters
    public static CFrag newInstance(String param1, String param2) {
        CFrag fragment = new CFrag();
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

    private ListView listView;

    private List<Contract> contracts;

    private ArrayAdapter<Contract> adapter;

    private AppCompatButton btn;
    private EditText e1;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_c, container, false);


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

        e1 = v.findViewById(R.id.searchv1);
        btn = v.findViewById(R.id.searchb1);

        listView = v.findViewById(R.id.list2);
        contracts = new ArrayList<>();

        String e11 = e1.getText().toString().trim();

        if(!e11.isEmpty())
        {
            loadContracts(e11);
        }


        return v;
    }

    private void loadContracts(String e11) {


        executorService.execute( ()->{

           try{
               List<Contract> fetchedContracts = appDao.getContractsByVendorId(Integer.parseInt(e11));
               getActivity().runOnUiThread( ()-> {

                   contracts.clear();

                   contracts.addAll(fetchedContracts);

                   adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1,contracts);
                   listView.setAdapter(adapter);


               });
           }catch (Exception e)
           {
               getActivity().runOnUiThread(()->{
                   showDialog("Error","unable to load contracts");
               });
           }
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