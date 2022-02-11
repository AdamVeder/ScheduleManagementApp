package com.example.androidproject.calendar_shifts;

import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.example.androidproject.R;
import com.example.androidproject.calendar_shifts.fragments.CalendarViewFragment;
import com.example.androidproject.calendar_shifts.fragments.EmployeeShiftsFragment;
import com.example.androidproject.calendar_shifts.fragments.ManagerShiftsFragment;

public class CalendarActivity extends AppCompatActivity implements CalendarViewFragment.CalendarFragmentListener
{
    private ManagerShiftsFragment managerShiftsFragment;
    private EmployeeShiftsFragment employeeShiftsFragment;
    private CalendarViewFragment calendarFragment;
    private String company_name, department, fullname, email, salary;
    private int isManager;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar);

        managerShiftsFragment = new ManagerShiftsFragment();
        employeeShiftsFragment = new EmployeeShiftsFragment();
        calendarFragment = new CalendarViewFragment();

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            company_name = extras.getString("company");
            department = extras.getString("department");
            isManager = extras.getInt("isManager");
            fullname = extras.getString("name");
            email = extras.getString("email");
            salary = extras.getString("salary");

            calendarFragment.setIsManager(isManager);
        }

        if(isManager == 1)//user is manager
        {
            managerShiftsFragment.setCompanyName(company_name);

            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragmentContainerView, calendarFragment)
                    .replace(R.id.fragmentContainerView2, managerShiftsFragment).commit();
        }
        else
        {
            //sending data directly to avoid synch-clash
            employeeShiftsFragment.setEmail(email);
            employeeShiftsFragment.setName(fullname);
            employeeShiftsFragment.setCompanyName(company_name);
            employeeShiftsFragment.setDepartmentName(department);
            employeeShiftsFragment.setSalary(salary);

            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragmentContainerView, calendarFragment)
                    .replace(R.id.fragmentContainerView2, employeeShiftsFragment).commit();
        }

    }

    @Override
    public void dateChanged(CharSequence date)//a listener in the calendar fragment will tell us whenever the selected day has changed, we then update the relevant fragments
    {
        Log.d("DATE", "dateChanged date = "+ date);

        if(isManager == 0) //regular employee account
        {
            employeeShiftsFragment.setTime(date.toString());

            employeeShiftsFragment.getShifts();
        }
        else
        {
            managerShiftsFragment.setTime(date.toString());

            managerShiftsFragment.getDepartments();
        }
    }
}