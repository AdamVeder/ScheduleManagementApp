package com.example.androidproject.employee_options.fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.androidproject.R;
import com.example.androidproject.calendar_shifts.CalendarActivity;
import com.example.androidproject.startup_login.MainActivity;

public class EmployeeMenuFragment extends Fragment//fragment that allows employee to choose between getting to shifts or checking his salary
{
    private TextView tViewEmployeeMenu;
    private Button btnShifts, btnSalary, btnReturn;
    private Intent intentShiftsCalendar;
    private String company_name, department, fullName, email, salary;
    private EmployeeSalaryFragment employeeSalaryFragment = new EmployeeSalaryFragment();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_employee_menu, container, false);

        tViewEmployeeMenu = view.findViewById(R.id.tViewEmployeeMenu);
        btnShifts = (Button) (view.findViewById(R.id.btnShiftsEmployee));
        btnSalary = (Button) (view.findViewById(R.id.btnSalaryEmployee));
        btnReturn = view.findViewById(R.id.btnEmployeeMenuReturn);

        intentShiftsCalendar = new Intent(view.getContext(), CalendarActivity.class);

        if(!getArguments().isEmpty())
        {
            company_name = getArguments().getString("company");
            department = getArguments().getString("department");
            fullName = getArguments().getString("name");
            email =  getArguments().getString("email");
            salary = getArguments().getString("salary");
        }

        tViewEmployeeMenu.setText("Welcome, "+fullName+".\n\nWhat would you like to do?");

        btnReturn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                Intent intentHome = new Intent(getActivity(), MainActivity.class);
                startActivity(intentHome);
            }
        });

        btnSalary.setOnClickListener(new View.OnClickListener()//allows user to see hourly wage + accumulated salary in a chosen month
        {
            @Override
            public void onClick(View view)
            {
                Bundle bundle = new Bundle();
                bundle.putString("company", company_name);
                bundle.putString("email", email);
                bundle.putString("salary", salary);
                bundle.putString("name", fullName);
                bundle.putString("department", department);

                employeeSalaryFragment.setArguments(bundle);

                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.frameLayoutMenu,employeeSalaryFragment).commit();
            }
        });

        btnShifts.setOnClickListener(new View.OnClickListener()//takes employee to the shifts activity
        {
            @Override
            public void onClick(View view)
            {
                intentShiftsCalendar.putExtra("company",company_name);
                intentShiftsCalendar.putExtra("department",department);
                intentShiftsCalendar.putExtra("name", fullName);
                intentShiftsCalendar.putExtra("email", email);
                intentShiftsCalendar.putExtra("isManager", 0);
                intentShiftsCalendar.putExtra("salary", salary);

                startActivity(intentShiftsCalendar);
            }
        });
        return view;
    }

    ///////////////PRE-BUILT SYSTEM METHODS/////////////////////////

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public EmployeeMenuFragment()
    {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment EmployeeMenuFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static EmployeeMenuFragment newInstance(String param1, String param2)
    {
        EmployeeMenuFragment fragment = new EmployeeMenuFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }
}

/*

 */