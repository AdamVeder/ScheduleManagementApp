package com.example.androidproject.manager_options;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.example.androidproject.R;
import com.example.androidproject.manager_options.fragments.ManagerMenuFragment;

public class ManagerOptionsActivity extends AppCompatActivity
{
    private String company_name;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manager_options);

        Bundle bundle = new Bundle();
        ManagerMenuFragment managerMenuFragment = new ManagerMenuFragment();

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            company_name = extras.getString("company");
        }

        bundle.putString("company", company_name);
        managerMenuFragment.setArguments(bundle);

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.managerOptionsLayout, managerMenuFragment).commit();
    }
}