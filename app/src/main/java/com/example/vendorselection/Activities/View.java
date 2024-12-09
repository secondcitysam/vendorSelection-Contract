package com.example.vendorselection.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.os.Bundle;

import com.example.vendorselection.Fragments.CFrag;
import com.example.vendorselection.Fragments.ContractFrag;
import com.example.vendorselection.Fragments.PFrag;
import com.example.vendorselection.Fragments.PerformanceFrag;
import com.example.vendorselection.Fragments.VFrag;
import com.example.vendorselection.Fragments.VendorFrag;
import com.example.vendorselection.R;
import com.ismaeldivita.chipnavigation.ChipNavigationBar;

public class View extends AppCompatActivity {

    private ChipNavigationBar cnb;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view);

        cnb = findViewById(R.id.cnb2);

        cnb.setItemSelected(R.id.vendor2,true);

        getSupportFragmentManager().beginTransaction().replace(R.id.frame2,new VFrag()).commit();

        bottomMenu();
    }

    private void bottomMenu() {
        cnb.setOnItemSelectedListener(new ChipNavigationBar.OnItemSelectedListener() {
            @Override
            public void onItemSelected(int i) {

                Fragment fragment = null;

                if(i==R.id.vendor2)
                {

                    fragment = new VFrag();

                } else if (i==R.id.Contract2) {
                    fragment = new CFrag();

                } else if (i==R.id.Performance2) {
                    fragment = new PFrag();

                }

                getSupportFragmentManager().beginTransaction().replace(R.id.frame2,fragment).commit();
            }
        });
    }
}