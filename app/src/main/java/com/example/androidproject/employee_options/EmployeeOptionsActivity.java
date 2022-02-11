package com.example.androidproject.employee_options;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;

import com.example.androidproject.R;
import com.example.androidproject.employee_options.fragments.EmployeeMenuFragment;
import com.example.androidproject.manager_options.fragments.ManagerMenuFragment;

public class EmployeeOptionsActivity extends AppCompatActivity
{
    private EmployeeMenuFragment employeeMenuFragment;
    private String company_name, fullName, department, email, salary;
    private int isManager;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        employeeMenuFragment = new EmployeeMenuFragment();

        Bundle extras = getIntent().getExtras();

        if (extras != null)
        {
            company_name = extras.getString("company");
            isManager = extras.getInt("isManager");
            department = extras.getString("department");
            fullName = extras.getString("name");
            email = extras.getString("email");
            salary = extras.getString("salary");

            Bundle bundle = new Bundle();
            bundle.putString("company", company_name);
            bundle.putString("name", fullName);
            bundle.putString("department", department);
            bundle.putString("email", email);
            bundle.putString("salary", salary);


            employeeMenuFragment.setArguments(bundle);
        }
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.frameLayoutMenu, employeeMenuFragment)
                    .commit();
    }
}