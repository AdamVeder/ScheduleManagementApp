package com.example.androidproject.manager_options.fragments.employees;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.androidproject.R;
import com.example.androidproject.manager_options.fragments.ManagerMenuFragment;

public class ManagerOptionsEmployeesFragment extends Fragment
{
    private String company_name;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_manager_options_employees, container, false);

        CreateEmployeeFragment createEmployeeFragment = new CreateEmployeeFragment();
        DisplayEmployeesFragment displayEmployeesFragment = new DisplayEmployeesFragment();
        Bundle bundle = new Bundle();

        Button btnCreateNewEmployee = view.findViewById(R.id.btnCreateNewEmployee);
        Button btnDisplayEmployees = view.findViewById(R.id.btnDisplayEmployees);
        Button btnReturn= view.findViewById(R.id.btnManagerOptionsReturn);

        company_name = getArguments().getString("company");
        bundle.putString("company", company_name);

        btnDisplayEmployees.setOnClickListener(new View.OnClickListener() //show all employees
        {
            @Override
            public void onClick(View view)
            {
                displayEmployeesFragment.setArguments(bundle);

                getActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.managerOptionsLayout, displayEmployeesFragment).commit();
            }
        });

        btnCreateNewEmployee.setOnClickListener(new View.OnClickListener()//create new employee
        {
            @Override
            public void onClick(View view)
            {
                createEmployeeFragment.setArguments(bundle);

                getActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.managerOptionsLayout, createEmployeeFragment).commit();
            }
        });

        btnReturn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                Bundle bundle = new Bundle();
                bundle.putString("company", company_name);
                ManagerMenuFragment managerMenuFragment = new ManagerMenuFragment();

                managerMenuFragment.setArguments(bundle);

                getActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.managerOptionsLayout, managerMenuFragment).commit();
            }
        });

        return view;
    }

    ////////////////////PRE-BUILT SYSTEM METHODS///////////////////////

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public ManagerOptionsEmployeesFragment()
    {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment EmployeeOptionsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ManagerOptionsEmployeesFragment newInstance(String param1, String param2)
    {
        ManagerOptionsEmployeesFragment fragment = new ManagerOptionsEmployeesFragment();
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