package com.example.vendorselection.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.os.Bundle;

import com.example.vendorselection.Fragments.ContractFrag;
import com.example.vendorselection.Fragments.PerformanceFrag;
import com.example.vendorselection.Fragments.VendorFrag;
import com.example.vendorselection.R;
import com.ismaeldivita.chipnavigation.ChipNavigationBar;

public class Home extends AppCompatActivity {

    private ChipNavigationBar cnb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        cnb = findViewById(R.id.cnb);

        cnb.setItemSelected(R.id.vendor,true);

        getSupportFragmentManager().beginTransaction().replace(R.id.frame,new VendorFrag()).commit();

        bottomMenu();
    }

    private void bottomMenu() {

        cnb.setOnItemSelectedListener(new ChipNavigationBar.OnItemSelectedListener() {
            @Override
            public void onItemSelected(int i) {

                Fragment fragment = null;

                if(i==R.id.vendor)
                {

                    fragment = new VendorFrag();

                } else if (i==R.id.Contract) {
                    fragment = new ContractFrag();

                } else if (i==R.id.Performance) {
                    fragment = new PerformanceFrag();

                }

                getSupportFragmentManager().beginTransaction().replace(R.id.frame,fragment).commit();
            }
        });
    }
}