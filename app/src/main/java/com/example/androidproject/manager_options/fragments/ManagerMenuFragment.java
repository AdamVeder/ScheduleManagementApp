package com.example.androidproject.manager_options.fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.androidproject.employee_options.EmployeeOptionsActivity;
import com.example.androidproject.manager_options.fragments.departments.CreateDepartmentFragment;
import com.example.androidproject.R;
import com.example.androidproject.calendar_shifts.CalendarActivity;
import com.example.androidproject.manager_options.fragments.employees.ManagerOptionsEmployeesFragment;
import com.example.androidproject.startup_login.MainActivity;

public class ManagerMenuFragment extends Fragment
{
    private TextView textViewManagerMenu;
    private Button btnShifts, btnEmployees, btnDepartments, btnReturn;
    private Intent intentShiftsCalendar;
    private String company_name, department, fullName, email;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_manager_menu, container, false);

        ManagerOptionsEmployeesFragment managerOptionsEmployeesFragment = new ManagerOptionsEmployeesFragment();
        CreateDepartmentFragment createDepartmentFragment = new CreateDepartmentFragment();
        intentShiftsCalendar = new Intent(view.getContext(), CalendarActivity.class);

        textViewManagerMenu = view.findViewById(R.id.tViewManagerMenu);
        btnShifts = view.findViewById(R.id.btnShiftsManagerMenu);
        btnEmployees = view.findViewById(R.id.btnEmployeesManagerMenu);
        btnDepartments = view.findViewById(R.id.btnNewDepartment);
        btnReturn = view.findViewById(R.id.btnManagerMenuReturn);
        company_name = getArguments().getString("company");

        btnReturn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                Intent intentHome = new Intent(getActivity(), MainActivity.class);
                startActivity(intentHome);
            }
        });

        btnDepartments.setOnClickListener(new View.OnClickListener()//manager selected to create a new department
        {
            @Override
            public void onClick(View view)
            {
                Bundle bundle = new Bundle();
                bundle.putString("company", company_name);

                createDepartmentFragment.setArguments(bundle);

                getActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.managerOptionsLayout, createDepartmentFragment)
                        .commit();
            }
        });

        btnEmployees.setOnClickListener(new View.OnClickListener()//manager selected employees
        {
            @Override
            public void onClick(View view)
            {
                Bundle bundle = new Bundle();
                bundle.putString("company", company_name);

                managerOptionsEmployeesFragment.setArguments(bundle);

                getActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.managerOptionsLayout, managerOptionsEmployeesFragment)
                        .commit();
            }
        });

        btnShifts.setOnClickListener(new View.OnClickListener()//manager selected shifts
        {
            @Override
            public void onClick(View view)
            {
                intentShiftsCalendar.putExtra("company",company_name);
                intentShiftsCalendar.putExtra("department",department);
                intentShiftsCalendar.putExtra("name", fullName);
                intentShiftsCalendar.putExtra("email", email);
                intentShiftsCalendar.putExtra("isManager", 1);

                startActivity(intentShiftsCalendar);
            }
        });

        textViewManagerMenu.setTextSize(30);
        textViewManagerMenu.setText("Welcome, Manager.\n\nWhat would you like to do?");

        return view;
    }


    //////////////////////PRE-BUILT SYSTEM METHODS///////////////////////

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public ManagerMenuFragment()
    {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ManagerMenuFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ManagerMenuFragment newInstance(String param1, String param2)
    {
        ManagerMenuFragment fragment = new ManagerMenuFragment();
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