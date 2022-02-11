package com.example.androidproject.calendar_shifts.fragments;

import static java.lang.Math.abs;
import static java.lang.Math.log;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
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
import com.example.androidproject.classes.Employee;
import com.example.androidproject.employee_options.EmployeeOptionsActivity;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class EmployeeShiftsFragment extends Fragment //fragment responsible for all shifts-handling for employee
{
    private ArrayList<String> employeesList = new ArrayList<>(), departmentsList = new ArrayList<>(), shiftsList = new ArrayList<>();
    private String currDate = "NULL";
    private String company_name, department, fullName, email, salary;
    private Map<String, String> emailToName = new HashMap<String, String>();
    private DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference().child("Companies");
    private LinearLayout linearLayout;
    private LinearLayout.LayoutParams linearLayoutParams;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_shifts, container, false);

        linearLayout = view.findViewById(R.id.shiftsFragLinearLayout);
        linearLayoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);

        linearLayoutParams.weight=1;
        linearLayoutParams.topMargin = 10;
        linearLayoutParams.bottomMargin = 5;
        linearLayoutParams.leftMargin = 5;
        linearLayoutParams.rightMargin = 5;
        linearLayoutParams.gravity = Gravity.CENTER;

        return view;
    }

    public void getShifts() //collect all available shifts for the employee's department on current date
    {
        shiftsList.clear();

        dbRef.child(company_name).child("Departments").child(department).child("Schedule").child(currDate.toString()).addValueEventListener(new ValueEventListener()
        {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot)
            {
                for(DataSnapshot dSnapshot : snapshot.getChildren())
                {
                    shiftsList.add(dSnapshot.getKey().toString());
                }
                makeButtonsFromList(shiftsList, "shifts");
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    public void getEmployees(String shift) //collects all employees signed up to a certain shift
    {
        employeesList.clear();
        emailToName.clear();

        dbRef.child(company_name).child("Departments").child(department).child("Schedule").child(currDate.toString()).child(shift).addValueEventListener(new ValueEventListener()
        {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot)
            {
                for(DataSnapshot dSnapshot : snapshot.getChildren())
                {
                    //key = user_apple@gmail_com ; value = user apple
                    emailToName.put(dSnapshot.getKey().toString(), dSnapshot.getValue().toString());
                }
                makeTextViewFromEmployees(shift);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void makeButtonsFromList(ArrayList<String> list, String context) //dynamically creates buttons
    {
        linearLayout.removeAllViews();
        Button btnReturn = new Button(getActivity());
        btnReturn.setText("Return To Menu");
        btnReturn.setBackgroundResource(R.drawable.button_third_design);
        btnReturn.setTextSize(25);
        btnReturn.setTextColor(Color.BLACK);
        btnReturn.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
        btnReturn.setWidth(800);
        btnReturn.setHeight(100);
        Typeface typeface = getResources().getFont(R.font.jmhbold);
        btnReturn.setTypeface(typeface);
        linearLayout.addView(btnReturn, linearLayoutParams);

        btnReturn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                Intent intentMenu = new Intent(getActivity(), EmployeeOptionsActivity.class);
                intentMenu.putExtra("email", email);
                intentMenu.putExtra("company",company_name);
                intentMenu.putExtra("department", department);
                intentMenu.putExtra("name", fullName);
                intentMenu.putExtra("isManager",0);
                intentMenu.putExtra("salary",salary);
                startActivity(intentMenu);
            }
        });

        if(list.size() == 0)
        {
            TextView error = new TextView(getActivity());
            String text = ("No available shifts");
            error.setText(text);
            error.setTextSize(25);
            error.setTextColor(Color.BLACK);
            error.setGravity(Gravity.CENTER);
            error.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
            typeface = getResources().getFont(R.font.jmhbold);
            error.setTypeface(typeface);
            linearLayout.addView(error, linearLayoutParams);
        }
        else
        {
            for(int i = 0 ; i < list.size(); i++)
            {
                Button btn = new Button(getActivity());
                btn.setTag("Button "+i);
                btn.setText(list.get(i));
                btn.setTextSize(25);
                btn.setPadding(15,15,15, 0);
                btn.setBackgroundResource(R.drawable.button_hollow_design);
                typeface = getResources().getFont(R.font.jmhbold);
                btn.setTypeface(typeface);

                linearLayout.addView(btn, linearLayoutParams);

                btn.setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View view)
                    {
                        if(context.equals("shifts"))
                        {
                            getEmployees(btn.getText().toString());
                        }
                        else
                        if(context.equals("departments"))
                        {
                            getShifts();
                        }
                    }
                });
            }
        }
        list.clear();
    }

    public void joinShift(String shift, Employee employee)//allows employee to join an availble shift
    {
        dbRef.child(company_name).child("Departments").child(department).child("Schedule").child(currDate.toString()).child(shift).child(employee.getMail()).setValue(fullName);

        String range = createSalaryMonthRange();
        float hoursInShift = calcHoursInShift(shift);

        dbRef.child(company_name).child("Employees").child(email).child("Shifts History").child(range).child("Date: "+ currDate + ", Shift: " +shift).setValue("Hours worked: " + String.format("~%.2f", hoursInShift));

        getShifts();
    }

    public void removeShift(String shift, String emailToRemove)//allows employee to remove themselves from shift they're in
    {
        dbRef.child(company_name).child("Departments").child(department).child("Schedule").child(currDate.toString()).child(shift).addValueEventListener(new ValueEventListener()
        {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot)
            {
               int count = (int)(snapshot.getChildrenCount());

                if(count == 0)
                {
                    dbRef.child(company_name).child("Departments").child(department).child("Schedule").child(currDate.toString()).child(shift).setValue("Empty");
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        dbRef.child(company_name).child("Departments").child(department).child("Schedule").child(currDate.toString()).child(shift).child(emailToRemove).removeValue();

        String range = createSalaryMonthRange();

        dbRef.child(company_name).child("Employees").child(email).child("Shifts History").child(range).child("Date: "+ currDate + ", Shift: " +shift).removeValue();

        getShifts();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void makeTextViewFromEmployees(String shift)//dynamically creates TextView from employees list
    {
        try {
            linearLayout.removeAllViews();
        }catch (Exception e)
        {
            Log.d("error", "error = " + e.toString());
        }

        LinearLayout linearLayout2 = (LinearLayout) (getActivity().findViewById(R.id.shiftsFragLinearLayout));
        LinearLayout.LayoutParams linearLayoutParams2 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);

        linearLayoutParams2.topMargin = 10;
        linearLayoutParams2.bottomMargin = 5;
        linearLayoutParams2.leftMargin = 5;
        linearLayoutParams2.rightMargin = 5;
        linearLayoutParams2.gravity = Gravity.CENTER;

        TextView textViewShift = new TextView(getActivity());
        String text = (currDate + ", " + shift + ":");
        textViewShift.setText(text);
        textViewShift.setTextColor(Color.BLACK);
        textViewShift.setTextSize(15);
        Typeface typeface = getResources().getFont(R.font.jmhbold);
        textViewShift.setTypeface(typeface);
        linearLayout2.addView(textViewShift, linearLayoutParams2);

        Button returnBtn = new Button(getActivity());
        returnBtn.setText("RETURN");
        returnBtn.setTextColor(Color.BLACK);
        returnBtn.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
        returnBtn.setTextSize(20);
        returnBtn.setBackgroundResource(R.drawable.background_design);
        returnBtn.setGravity(Gravity.CENTER);
        typeface = getResources().getFont(R.font.jmhbold);
        returnBtn.setTypeface(typeface);
        linearLayout2.addView(returnBtn, linearLayoutParams2);

        returnBtn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                getShifts();
            }
        });

        LinearLayout linearLayout3 = (LinearLayout) (getActivity().findViewById(R.id.shiftsFragLinearLayout));
        LinearLayout.LayoutParams linearLayoutParams3 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);

        linearLayoutParams3.topMargin = 10;
        linearLayoutParams3.bottomMargin = 5;
        linearLayoutParams3.leftMargin = 5;
        linearLayoutParams3.rightMargin = 30;
        linearLayoutParams3.gravity = Gravity.RIGHT;

        TextView add_removeTextView = new TextView(getActivity());
        add_removeTextView.setText("");
        add_removeTextView.setTextColor(Color.BLACK);
        add_removeTextView.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
        add_removeTextView.setTextSize(35);
        add_removeTextView.setBackgroundResource(R.drawable.background_design);
        add_removeTextView.setGravity(Gravity.CENTER);
        add_removeTextView.setHeight(120);
        add_removeTextView.setWidth(120);
        typeface = getResources().getFont(R.font.road);
        add_removeTextView.setTypeface(typeface);
        linearLayout3.addView(add_removeTextView, linearLayoutParams3);

        dbRef.child(company_name).child("Departments").child(department).child("Schedule").child(currDate.toString()).child(shift).addValueEventListener(new ValueEventListener()
        {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot)
            {
                if (snapshot.child(email).exists()) {
                    add_removeTextView.setText("-"); //employee is in shift
                } else {
                    add_removeTextView.setText("+"); //else
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error)
            {

            }
        });

        add_removeTextView.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                if (add_removeTextView.getText().toString().equals("+"))
                {
                    try {
                        joinShift(shift, new Employee(department, email, fullName, "0"));
                    }catch (Exception e)
                    {
                        Log.d("EmployeeCrash", "joinShift: e = " + e.toString());
                    }
                }
                else
                {
                    try {
                        removeShift(shift, email);
                    }catch (Exception e)
                    {
                        Log.d("EmployeeCrash", "joinShift: e = " + e.toString());
                    }
                }
            }
        });

        for (Map.Entry mapElement : emailToName.entrySet())
        {
            TextView textView = new TextView(getActivity());
            textView.setTag(mapElement.getKey().toString());//Map<KEY Michael, VALUE Michael@gmail.com>
            textView.setText(mapElement.getValue().toString());
            textView.setTextSize(25);
            textView.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
            textView.setTextColor(Color.BLACK);
            typeface = getResources().getFont(R.font.jmhbold);
            textView.setTypeface(typeface);

            linearLayout.addView(textView, linearLayoutParams);
        }

        employeesList.clear();
    }

  public String createSalaryMonthRange() //creates a From: 1-M-YEAR  TO: MAX_DAY_OF_MONTH-M-YEAR string
    {
        String temp = currDate, year, month, res;
        int indexOfFirstDash = currDate.indexOf('-'), monthNum, indexOfSecondDash, maxDay;

        temp = currDate.replaceFirst("-", "@");
        indexOfSecondDash = temp.indexOf('-');
        year = currDate.substring(indexOfSecondDash+1);
        month = currDate.substring(indexOfFirstDash+1, indexOfSecondDash);

        Calendar calendar = Calendar.getInstance();
        calendar.set(Integer.parseInt(year), Integer.parseInt(month), 1);
        maxDay = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);

        res  = "From:  1" + "-" + month + "-" +year + "   To:  " + maxDay+"-"+month+"-"+year;

        return res;
    }

    public float calcHoursInShift(String shift) //calculates the amount of hours in a shift (i.e 12:00-13:30 = 1.5 hours)
    {
        String modifiedShift = shift.replace(':', '.'), stringStart, stringFinish;
        int indexOfDash = modifiedShift.indexOf('-');
        float start, finish, startDecimalPoints, finishDecimalPoints, res;

        start = Float.parseFloat(modifiedShift.substring(0, indexOfDash));
        finish = Float.parseFloat(modifiedShift.substring(indexOfDash+1));

        startDecimalPoints = start-((int)start);
        finishDecimalPoints = finish -((int)finish);

        start = (int)start + (startDecimalPoints*100)/60;
        finish = (int)finish + (finishDecimalPoints*100)/60;

        res = abs(finish-start);

        if(start > finish)
            res = 24-res;

        return res;
    }

    public void setCompanyName(String c_name)
    {
        company_name = c_name;
    }
    public void setDepartmentName(String c_department)
    {
        department = c_department;
    }
    public void setEmail(String c_email)
    {
        email = c_email;
    }
    public void setName(String c_name)
    {
        fullName = c_name;
    }
    public void setSalary(String c_salary) {salary = c_salary;}
    public void setTime(String c_time)
    {
        currDate = c_time;
    }

    /////////////////////////AUTO-BUILT METHODS AND SETTINGS/////////////////////////

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public void EmployeeShiftsFragment()
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
    public static EmployeeShiftsFragment newInstance(String param1, String param2)
    {
        EmployeeShiftsFragment fragment = new EmployeeShiftsFragment();
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