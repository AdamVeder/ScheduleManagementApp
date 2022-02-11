package com.example.androidproject.manager_options.fragments.employees;

import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.androidproject.R;
import com.example.androidproject.classes.Employee;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class DisplayEmployeesFragment extends Fragment//fragment responsible for displaying all company's employees
{
    private String company_name;
    private DatabaseReference dbRef;
    private List<String> employees = new ArrayList<>();
    private LinearLayout linearLayout;
    private LinearLayout.LayoutParams linearLayoutParams;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_display_employees_manager, container, false);

        dbRef = FirebaseDatabase.getInstance().getReference().child("Companies");
        linearLayout = view.findViewById(R.id.displayEmployeesLayout);
        linearLayoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        company_name = getArguments().getString("company");

        linearLayout.setGravity(Gravity.CENTER);
        linearLayoutParams.setMargins(0, 5, 0, 0);
        linearLayout.setBackgroundResource(R.drawable.light_blue);

        GetEmployees();

        return view;
    }

    public void GetEmployees()//first we collect all employees into employees list
    {
        dbRef.child(company_name).child("Employees").addValueEventListener(new ValueEventListener()
        {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot)
            {
                try
                {
                    employees.clear();

                    for(DataSnapshot child : snapshot.getChildren())
                    {
                        employees.add(child.getKey().toString());
                    }

                    DisplayEmployees();
                }
                catch (Exception e)
                {
                    Log.d("CrashManager", "DisplayEmployees: e = " + e.toString());
                    return;
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    public void DisplayEmployees()//after collecting employees, we dynamically show them as TextViews
    {
        for(int i =0 ; i < employees.size(); i++)
        {
            TextView employeeEmail = new TextView(getContext());
            employeeEmail.setText(employees.get(i));
            employeeEmail.setTextSize(20);
            employeeEmail.setTextColor(Color.BLACK);
            linearLayout.addView(employeeEmail, linearLayoutParams);
        }

        Button btnReturn = new Button(getActivity());
        btnReturn.setText("Return");
        btnReturn.setBackgroundResource(R.drawable.button_second_design);
        btnReturn.setTextColor(Color.BLACK);
        btnReturn.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
        linearLayout.addView(btnReturn, linearLayoutParams);

        btnReturn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                Bundle bundle = new Bundle();
                bundle.putString("company", company_name);

                ManagerOptionsEmployeesFragment managerOptionsEmployeesFragment = new ManagerOptionsEmployeesFragment();
                managerOptionsEmployeesFragment.setArguments(bundle);

                getActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.managerOptionsLayout, managerOptionsEmployeesFragment).commit();
            }
        });
    }

    ////////////////////PRE-BUILT SYSTEM METHODS////////////////////////

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public DisplayEmployeesFragment()
    {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment DisplayEmployeesManagerFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static DisplayEmployeesFragment newInstance(String param1, String param2)
    {
        DisplayEmployeesFragment fragment = new DisplayEmployeesFragment();
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