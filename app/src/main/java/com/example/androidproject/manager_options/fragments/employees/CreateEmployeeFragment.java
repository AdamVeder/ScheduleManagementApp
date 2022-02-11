package com.example.androidproject.manager_options.fragments.employees;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.androidproject.R;
import com.example.androidproject.classes.Employee;
import com.example.androidproject.manager_options.fragments.ManagerMenuFragment;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Locale;

public class CreateEmployeeFragment extends Fragment//fragment responsible for creating a new employee in the company
{
    private String defaultPassword = "admin123";
    private FirebaseAuth mAuth;
    private String company_name;
    private EditText createEmployeeName, createEmployeeEmail, createEmployeeDepartment, createEmployeeSalary;
    private Button btnCreateEmployee, btnReturn;
    private Employee employee;
    private DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference().child("Companies");
    float salary = 0;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_create_employee, container, false);

        mAuth = FirebaseAuth.getInstance();
        company_name = getArguments().getString("company");

        createEmployeeName = view.findViewById(R.id.eTextCreateEmpName);
        createEmployeeEmail = view.findViewById(R.id.eTextCreateEmpEmail);
        createEmployeeDepartment = view.findViewById(R.id.eTextCreateEmpDepartment);
        createEmployeeSalary = view.findViewById(R.id.eTextCreateEmpSalary);
        btnCreateEmployee = view.findViewById(R.id.btnFragCreateEmployee);
        btnReturn = view.findViewById(R.id.fragCreateEmployeeBtnReturn);

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
                        .replace(R.id.managerOptionsLayout, managerMenuFragment)
                        .commit();
            }
        });

        btnCreateEmployee.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)//first we make sure the salary is a float and that no field is empty
            {
                String converted_Email = convertEmailFormat(createEmployeeEmail.getText().toString());
                try {
                     salary = Float.parseFloat( createEmployeeSalary.getText().toString());
                }catch (Exception e)
                {
                    Toast.makeText(getContext(), "Wrong salary format, please try again", Toast.LENGTH_LONG).show();
                    return;
                }

                if (TextUtils.isEmpty(createEmployeeName.getText().toString())) {
                    Toast.makeText(getContext(), "Enter employee name", Toast.LENGTH_LONG).show();
                    return;
                }

                if (TextUtils.isEmpty(createEmployeeEmail.getText().toString())) {
                    Toast.makeText(getContext(), "Enter employee email", Toast.LENGTH_LONG).show();
                    return;
                }

                if (TextUtils.isEmpty(createEmployeeDepartment.getText().toString())) {
                    Toast.makeText(getContext(), "Enter employee department", Toast.LENGTH_LONG).show();
                    return;
                }

                if (TextUtils.isEmpty(createEmployeeSalary.getText().toString())) {
                    Toast.makeText(getContext(), "Enter hourly wage", Toast.LENGTH_LONG).show();
                    return;
                }

                mAuth.createUserWithEmailAndPassword(createEmployeeEmail.getText().toString(), defaultPassword)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>()
                        {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task)
                            {
                                if (task.isSuccessful())//new employee added to firebase auth, now we have to add it to our Employees branch in the database
                                {
                                    Log.d("REGIS_LAND", "company =  " + company_name);

                                    Toast.makeText(getContext(), "Registration successful", Toast.LENGTH_LONG).show();

                                    float OrigSalary = Float.parseFloat(createEmployeeSalary.getText().toString());
                                    String decimalSalary = String.format("%.2f", OrigSalary);

                                    employee = new Employee(createEmployeeDepartment.getText().toString(), converted_Email, createEmployeeName.getText().toString(), decimalSalary);

                                    dbRef.child(company_name).child("Employees").child(employee.getMail()).setValue(employee);
                                    dbRef.child(company_name).child("Employees").child(employee.getMail()).child("Shifts History").setValue("Empty");//dummy value for future calculations

                                    Bundle bundle = new Bundle();
                                    bundle.putString("company", company_name);

                                    ManagerMenuFragment managerMenuFragment = new ManagerMenuFragment();
                                    managerMenuFragment.setArguments(bundle);

                                    getActivity().getSupportFragmentManager().beginTransaction()
                                            .replace(R.id.managerOptionsLayout, managerMenuFragment)
                                            .commit();
                                }
                                else
                                    {
                                        Toast.makeText(getContext(), "Registration failed", Toast.LENGTH_LONG).show();
                                        return;
                                    }
                            }
                        });
            }
        });

        return view;
    }

    public String convertEmailFormat(String emailToConvert)
    {
        String temp = emailToConvert;
        temp = temp.replace("_", "__");
        temp = temp.replace('.', '_');

        return temp.toLowerCase(Locale.ROOT);
    }

    ////////////////////PRE-BUILT SYSTEM METHODS//////////////////////////

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public CreateEmployeeFragment()
    {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment CreateEmployeeFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static CreateEmployeeFragment newInstance(String param1, String param2)
    {
        CreateEmployeeFragment fragment = new CreateEmployeeFragment();
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