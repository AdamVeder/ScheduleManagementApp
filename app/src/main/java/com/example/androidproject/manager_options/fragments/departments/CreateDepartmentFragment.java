package com.example.androidproject.manager_options.fragments.departments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.androidproject.R;
import com.example.androidproject.manager_options.fragments.ManagerMenuFragment;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class CreateDepartmentFragment extends Fragment//fragment responsible for creating a new department in the company
{
    private String company_name;
    private DatabaseReference dbRef;
    private EditText editTextNewDepartment;
    private Button btnCreateNewDepartment, btnReturn;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_create_department, container, false);

        company_name = getArguments().getString("company");
        dbRef = FirebaseDatabase.getInstance().getReference().child("Companies");
        editTextNewDepartment =  view.findViewById(R.id.editTextNewDepartment);
        btnCreateNewDepartment = view.findViewById(R.id.btnCreateNewDepartment);
        btnReturn = view.findViewById(R.id.btnCreateNewDeptReturn);

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

        btnCreateNewDepartment.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                CreateDepartment();
            }
        });

        return view;
    }


    public void CreateDepartment()
    {
        String newDeptName = editTextNewDepartment.getText().toString();

        if(newDeptName.length() == 0)
            return;

        if(newDeptName.contains("$") || newDeptName.contains("#") || newDeptName.contains(".") ||newDeptName.contains("[") || newDeptName.contains("]") || newDeptName.contains("/")  )
        {
            Toast.makeText(getContext(), "Department name can't contain the following characters: $, #, . , [ , ] , /", Toast.LENGTH_LONG ).show();
            return;
        }

        dbRef.child(company_name).child("Departments").child(newDeptName).child("Schedule").setValue("Empty");

        Bundle bundle = new Bundle();
        bundle.putString("company", company_name);
        ManagerMenuFragment managerMenuFragment = new ManagerMenuFragment();
        managerMenuFragment.setArguments(bundle);

        Toast.makeText(getContext(), "Department was successfully created", Toast.LENGTH_SHORT).show();

        getActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.managerOptionsLayout, managerMenuFragment)
                .commit();
    }

    /////////////////PRE-BUILT SYSTEM METHODS///////////////////////

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public CreateDepartmentFragment()
    {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment CreateDepartmentFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static CreateDepartmentFragment newInstance(String param1, String param2)
    {
        CreateDepartmentFragment fragment = new CreateDepartmentFragment();
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