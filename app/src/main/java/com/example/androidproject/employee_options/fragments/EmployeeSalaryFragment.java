package com.example.androidproject.employee_options.fragments;

import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.androidproject.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class EmployeeSalaryFragment extends Fragment//fragment responsible for displaying employee's hourly wage along with the calculated salary in chosen month
{
    private LinearLayout linearLayout;
    private LinearLayout.LayoutParams linearLayoutParams;
    private DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference().child("Companies");
    private String company_name, email, fullName, department, salary;
    private float accumulatedHours = 0;
    private ArrayList<String> months = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_employee_salary, container, false);

        linearLayout = view.findViewById(R.id.linearLayoutEmployeeSalary);
        linearLayoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        linearLayoutParams.setMargins(0, 5, 0 , 5);

        if(!getArguments().isEmpty())
        {
            company_name = getArguments().getString("company");
            department = getArguments().getString("department");
            fullName = getArguments().getString("name");
            salary = getArguments().getString("salary");
            email = getArguments().getString("email");
        }

        getMonths();

        return view;
    }

    public void getMonths()//we collect all the month ranges (i.e. 1.1.2020-31.1.2020) from employees shift history
    {
        dbRef.child(company_name).child("Employees").child(email).child("Shifts History").addValueEventListener(new ValueEventListener()
        {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot)
            {
               try{
                   months.clear();

                   for(DataSnapshot child : snapshot.getChildren())
                   {
                       months.add(child.getKey().toString());
                   }

                   displayMonths();
               }
               catch (Exception e)
               {
                   Log.d("EmployeeCrash", "getMonths: e = " + e.toString());
                   return;
               }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error)
            {

            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void displayMonths()//after collecting all the months we display them as buttons
    {
        try {
            linearLayout.removeAllViews();
        } catch (Exception e)
        {
            Log.d("removeAllViews", "EmployeeSalaryFragment, DisplayMonths: " + e.toString());
            return;
        }

        TextView intro = new TextView(getActivity());
        intro.setText("Your hourly wage is: " + salary);
        intro.setTextSize(20);
        intro.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
        intro.setTextColor(Color.BLACK);
        Typeface typeface = getResources().getFont(R.font.road);
        intro.setTypeface(typeface);

        linearLayout.addView(intro, linearLayoutParams);

        for(int i = 0 ; i < months.size(); i++)
        {
            Button btnMonth = new Button(getActivity());

            btnMonth.setText(months.get(i));
            btnMonth.setBackgroundResource(R.drawable.button_hollow_design);
            btnMonth.setTextColor(Color.BLACK);
            btnMonth.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
            btnMonth.setTextSize(15);
            btnMonth.setGravity(Gravity.CENTER);
            btnMonth.setWidth(1000);
            btnMonth.setHeight(100);
            typeface = getResources().getFont(R.font.road);
            btnMonth.setTypeface(typeface);

            linearLayout.addView(btnMonth, linearLayoutParams);

            btnMonth.setOnClickListener(new View.OnClickListener()//once clicked we calculate the earned amount in that month
            {
                @Override
                public void onClick(View view)
                {
                    calcAndDisplaySalaryInMonth(btnMonth.getText().toString());
                }
            });
        }

        Button buttonRet = new Button(getContext());
        buttonRet.setText("Return");
        buttonRet.setBackgroundResource(R.drawable.button_third_design);
        buttonRet.setTextSize(20);
        buttonRet.setTextColor(Color.BLACK);
        buttonRet.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
        typeface = getResources().getFont(R.font.jmhbold);
        buttonRet.setTypeface(typeface);
        typeface = getResources().getFont(R.font.jmhbold);
        buttonRet.setTypeface(typeface);
        linearLayout.addView(buttonRet, linearLayoutParams);

        buttonRet.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                Bundle bundle = new Bundle();
                bundle.putString("company", company_name);
                bundle.putString("department", department);
                bundle.putString("name", fullName);
                bundle.putString("salary", salary);
                bundle.putString("email", email);

                EmployeeMenuFragment employeeMenuFragment = new EmployeeMenuFragment();
                employeeMenuFragment.setArguments(bundle);

                getActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.frameLayoutMenu, employeeMenuFragment).commit();
            }
        });
    }

    public void calcAndDisplaySalaryInMonth(String date)
    {
        dbRef.child(company_name).child("Employees").child(email).child("Shifts History").child(date).addValueEventListener(new ValueEventListener()
        {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot)
            {
                accumulatedHours = 0;

                for(DataSnapshot child : snapshot.getChildren())//collect the hours from all shifts in a month chosen
                {
                    String temp = child.getValue().toString();
                    int indexOfTilde = temp.indexOf('~');
                    float hours = Float.parseFloat(temp.substring(indexOfTilde+1));
                    accumulatedHours+=hours;
                }

                try //try-catch in case of a listener interrupting switching account on the same run
                {
                    linearLayout.removeAllViews();
                    TextView salaryText = new TextView(getActivity());
                    salaryText.setText(date + ":\nAccumulated Hours = " + accumulatedHours + "\nHourly wage = " + salary +
                            "\n"+"----------------------------------"
                            +"\nSalary = " +String.format("%.2f",(accumulatedHours* Float.parseFloat(salary))));
                    salaryText.setTextSize(20);
                    salaryText.setTextColor(Color.BLACK);
                    salaryText.setGravity(Gravity.CENTER);
                    Typeface typeface = getResources().getFont(R.font.road);
                    salaryText.setTypeface(typeface);
                    linearLayout.addView(salaryText, linearLayoutParams);

                    Button button = new Button(getActivity());
                    button.setText("Return");
                    button.setBackgroundResource(R.drawable.button_third_design);
                    button.setTextColor(Color.BLACK);
                    button.setTextSize(20);
                    button.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
                    typeface = getResources().getFont(R.font.jmhbold);
                    button.setTypeface(typeface);
                    linearLayout.addView(button, linearLayoutParams);

                    button.setOnClickListener(new View.OnClickListener()
                    {
                        @Override
                        public void onClick(View view)
                        {
                            Bundle bundle = new Bundle();
                            bundle.putString("company", company_name);
                            bundle.putString("department", department);
                            bundle.putString("name", fullName);
                            bundle.putString("salary", salary);
                            bundle.putString("email", email);

                            EmployeeMenuFragment employeeMenuFragment = new EmployeeMenuFragment();
                            employeeMenuFragment.setArguments(bundle);

                            getActivity().getSupportFragmentManager().beginTransaction()
                                    .replace(R.id.frameLayoutMenu, employeeMenuFragment).commit();
                        }
                    });


                }
                catch (Exception e)
                {
                    Log.d("EmployeeCrash", "EmployeeSalary CalcHours: e = " + e.toString());
                    return;
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    ///////////////////PRE-BUILT SYSTEM METHODS/////////////////////////////

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public EmployeeSalaryFragment()
    {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment EmployeeSalaryFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static EmployeeSalaryFragment newInstance(String param1, String param2)
    {
        EmployeeSalaryFragment fragment = new EmployeeSalaryFragment();
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