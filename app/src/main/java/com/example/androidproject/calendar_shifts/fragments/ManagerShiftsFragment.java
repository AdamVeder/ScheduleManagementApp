package com.example.androidproject.calendar_shifts.fragments;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;

import com.example.androidproject.R;
import com.example.androidproject.manager_options.ManagerOptionsActivity;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class ManagerShiftsFragment extends Fragment //fragment responsible for all shifts-handling for manager
{
    private ArrayList<String> departmentsList = new ArrayList<>(), shiftsList = new ArrayList<>();
    private Map<String, String> eMailNameMap = new HashMap<String, String>();
    private String currDate = "NULL", currDept = "NULL", company_name, todaysDate = "NULL";
    private TextView focused;
    private int countDateCalls = 0;
    private DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference().child("Companies");
    private LinearLayout linearLayout;
    private LinearLayout.LayoutParams linearLayoutParams;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_shifts, container, false);

        linearLayout = (LinearLayout)(view.findViewById(R.id.shiftsFragLinearLayout));
        linearLayoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);

        linearLayoutParams.weight=1;
        linearLayoutParams.topMargin = 5;
        linearLayoutParams.gravity = Gravity.CENTER;
        linearLayoutParams.width = 800;

        Log.d("currDate", "OnViewCreate curr = " + currDate);

        return view;
    }

    public void getDepartments()//collect all departments availble in the company
    {
        dbRef.child(company_name).child("Departments").addValueEventListener(new ValueEventListener()
        {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot)
            {
                departmentsList.clear();

                for(DataSnapshot dSnapshot : snapshot.getChildren())
                {
                    departmentsList.add(dSnapshot.getKey().toString());
                }

                showDepartments();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error)
            {

            }
        });

    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void showDepartments()//display all departments as clickable buttons
    {
        try {
            linearLayout.removeAllViews();
        } catch (Exception e)
        {
            Log.d("removeAllViews", "showDepartments: " + e.toString());
        }

        Button btnReturn = new Button(getActivity());

        btnReturn.setText("Return");
        btnReturn.setBackgroundResource(R.drawable.button_third_design);
        btnReturn.setTextColor(Color.BLACK);
        btnReturn.setTextSize(25);
        btnReturn.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
        Typeface typeface = getResources().getFont(R.font.jmhbold);
        btnReturn.setTypeface(typeface);

        linearLayout.addView(btnReturn, linearLayoutParams);

        btnReturn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
               Intent intentMenu = new Intent(getActivity(), ManagerOptionsActivity.class);
               intentMenu.putExtra("company", company_name);

               startActivity(intentMenu);
            }
        });

        for(int i = 0 ; i < departmentsList.size(); i++)
        {
            Button btn = new Button(getActivity());
            btn.setText(departmentsList.get(i));//text = department names
            btn.setTextSize(25);
            btn.setPadding(15,15,15, 0);

            btn.setBackgroundResource(R.drawable.button_hollow_design);
            typeface = getResources().getFont(R.font.jmhbold);
            btn.setTypeface(typeface);

            String clickedDepartment = btn.getText().toString();

            linearLayout.addView(btn, linearLayoutParams);

            btn.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View view)
                {
                    currDept = clickedDepartment;
                    getShifts();
                }
            });
        }
    }

    public void getShifts()//collect all shifts available for the current date+department selected
    {
        (dbRef.child(company_name).child("Departments").child(currDept).child("Schedule").child(currDate.toString())).addValueEventListener(new ValueEventListener()
        {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot)
            {
                shiftsList.clear();
                for(DataSnapshot dSnapshot : snapshot.getChildren())
                {
                    Log.d("shiftsSize", "snapshot =  " + dSnapshot.getKey().toString());

                    shiftsList.add(dSnapshot.getKey().toString());
                }
                showShifts();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error)
            {

            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void showShifts() //displays all collected shifts as clickable buttons
    {
        try
        {
            linearLayout.removeAllViews();
        }
        catch (Exception e)
        {
            Log.d("removeAllViews", "showShift: " + e.toString());
        }

        Button returnBtn = new Button(getActivity());
        returnBtn.setText("RETURN");
        returnBtn.setTextColor(Color.BLACK);
        returnBtn.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
        returnBtn.setTextSize(25);
        returnBtn.setBackgroundResource(R.drawable.button_third_design);
        returnBtn.setGravity(Gravity.CENTER);
        Typeface typeface = getResources().getFont(R.font.jmhbold);
        returnBtn.setTypeface(typeface);
        linearLayout.addView(returnBtn, linearLayoutParams);

        returnBtn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                getDepartments();
            }
        });

        Button btnAddShift = new Button(getActivity());
        btnAddShift.setText("Add Shift");
        btnAddShift.setTextColor(Color.BLACK);
        btnAddShift.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
        btnAddShift.setTextSize(25);
        btnAddShift.setBackgroundResource(R.drawable.button_third_design);
        btnAddShift.setGravity(Gravity.CENTER);
        typeface = getResources().getFont(R.font.jmhbold);
        btnAddShift.setTypeface(typeface);
        linearLayout.addView(btnAddShift, linearLayoutParams);

        btnAddShift.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                createShift();
            }
        });

        if(shiftsList.size() == 0)
        {
            TextView error = new TextView(getActivity());
            String text = ("No available shifts");
            error.setText(text);
            error.setTextSize(30);
            error.setTextColor(Color.BLACK);
            error.setGravity(Gravity.CENTER);
            error.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
            typeface = getResources().getFont(R.font.jmhbold);
            error.setTypeface(typeface);

            linearLayout.addView(error, linearLayoutParams);
        }
        else
        {
            for(int i = 0 ; i < shiftsList.size(); i++)
            {
                Button btn = new Button(getActivity());
                btn.setTag("Button "+i);
                btn.setText(shiftsList.get(i)); //text = shift i.e. (08:00-16:00)
                btn.setTextSize(25);
                btn.setPadding(15,15,15, 0);
                btn.setBackgroundResource(R.drawable.button_hollow_design);
                typeface = getResources().getFont(R.font.jmhbold);
                btn.setTypeface(typeface);

                String clickedShift = btn.getText().toString();

                linearLayout.addView(btn, linearLayoutParams);

                btn.setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View view)
                    {
                        getEmployees(clickedShift);
                    }
                });
            }
        }

    }

    public void getEmployees(String shift) //collects all employees signed to a chosen shift
    {
        dbRef.child(company_name).child("Departments").child(currDept).child("Schedule").child(currDate.toString()).child(shift).addValueEventListener(new ValueEventListener()
        {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot)

            {
                eMailNameMap.clear();

                for(DataSnapshot dSnapshot : snapshot.getChildren())
                {
                    eMailNameMap.put(dSnapshot.getKey().toString(), dSnapshot.getValue().toString());
                }

                showEmployees(shift);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error)
            {

            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void showEmployees(String shift) //displays all collected employees as TextViews
    {
        try {
            linearLayout.removeAllViews();
        } catch (Exception e)
        {
            Log.d("removeAllViews", "showEmployees: " + e.toString());
        }


        Log.d("emailToName", "emailToName START size = " + eMailNameMap.size());

        Button btnRemoveEmployee = new Button(getActivity()), btnRemoveShift = new Button(getActivity()) ;

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
        // textViewShift.setTextSize(30);
        textViewShift.setTextColor(Color.BLACK);
        Typeface typeface = getResources().getFont(R.font.jmhbold);
        textViewShift.setTypeface(typeface);
        linearLayout2.addView(textViewShift, linearLayoutParams2);

        Button returnBtn = new Button(getActivity());
        returnBtn.setText("RETURN");
        returnBtn.setTextColor(Color.BLACK);
        returnBtn.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
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


        LinearLayout linearLayoutRemove = (LinearLayout)getActivity().findViewById(R.id.shiftsFragLinearLayout);
        LinearLayout.LayoutParams linearLayoutParamsRemove = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        linearLayoutRemove.setOrientation(LinearLayout.VERTICAL);

        linearLayoutParamsRemove.weight=1;
        linearLayoutParamsRemove.topMargin = 10;
        linearLayoutParamsRemove.bottomMargin = 5;
        linearLayoutParamsRemove.leftMargin = 5;
        linearLayoutParamsRemove.rightMargin = 5;
        linearLayoutParamsRemove.gravity = Gravity.CENTER;


        if(eMailNameMap.size() > 0)
        {
            btnRemoveEmployee.setAlpha(.5f);
            btnRemoveEmployee.setClickable(false);
            btnRemoveEmployee.setText("Remove Employee");
            btnRemoveEmployee.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
            btnRemoveEmployee.setTextColor(Color.BLACK);
            typeface = getResources().getFont(R.font.jmhbold);
            btnRemoveEmployee.setTypeface(typeface);
            btnRemoveEmployee.setBackgroundResource(R.drawable.background_design);

            linearLayoutRemove.addView(btnRemoveEmployee, linearLayoutParamsRemove);

            btnRemoveEmployee.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View view)
                {
                    btnRemoveEmployee.setAlpha(.5f);
                    btnRemoveEmployee.setClickable(false);
                    removeEmployeeFromShift(shift, focused.getTag().toString());
                }
            });
        }

        btnRemoveShift.setText("Remove Entire Shift");
        btnRemoveShift.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
        btnRemoveShift.setTextColor(Color.BLACK);
        btnRemoveShift.setBackgroundResource(R.drawable.background_design);
        typeface = getResources().getFont(R.font.jmhbold);
        btnRemoveShift.setTypeface(typeface);
        linearLayoutRemove.addView(btnRemoveShift, linearLayoutParamsRemove);

        btnRemoveShift.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                removeShift(shift);

                shiftsList.remove(shift);
            }
        });


        if(eMailNameMap.size() == 0)//no employees in selected shift
        {
            TextView error2 = new TextView(getActivity());
            String tempText = ("No employees in shift");
            error2.setText(tempText);
            error2.setTextSize(30);
            error2.setTextColor(Color.BLACK);
            error2.setGravity(Gravity.CENTER);
            typeface = getResources().getFont(R.font.jmhbold);
            error2.setTypeface(typeface);
            error2.setTypeface(Typeface.DEFAULT, Typeface.BOLD);

            linearLayout.addView(error2, linearLayoutParams);

            Log.d("emailToName", "emailToName emailToName == 0 size = " + eMailNameMap.size());
        }
        else
        {
            for (Map.Entry mapElement : eMailNameMap.entrySet())
            {
                Log.d("emailToName", "mapElement size = " + eMailNameMap.size());

                TextView textView = new TextView(getActivity());
                textView.setTag(mapElement.getKey().toString());//Map<KEY Michael@gmail.com, VALUE Michael>
                textView.setText(mapElement.getValue().toString());
                textView.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
                textView.setTextColor(Color.BLACK);
                textView.setTextSize(20);
                textView.setGravity(Gravity.CENTER);
                typeface = getResources().getFont(R.font.jmhbold);
                textView.setTypeface(typeface);

                linearLayout.addView(textView, linearLayoutParams);

                Log.d("REMOVE_mapElement",textView.getTag().toString() );


                textView.setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View view)
                    {
                        btnRemoveEmployee.setClickable(true);
                        btnRemoveEmployee.setAlpha(1f);
                        focused = textView;
                    }
                });
            }
        }

    }

    public String createSalaryMonthRange()//creates a "FROM: 1-M-YYYY TO: MAX_DAY_OF_MONTH-M-YYYY" string
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

    public void removeEmployeeFromShift(String shift, String emailToRemove)
    {
        if(isCurrDateBeforeToday() == true) //we don't want to modify shifts from the past
        {
            Toast.makeText(getContext(), "Can't modify old shifts!", Toast.LENGTH_SHORT).show();
            return;
        }

        String range;

        if(eMailNameMap.size() == 1) //if the last employee, save the shift with dummy value
        {
            dbRef.child(company_name).child("Departments").child(currDept).child("Schedule").child(currDate.toString()).child(shift).setValue("Empty");
        }
        else
        {
            dbRef.child(company_name).child("Departments").child(currDept).child("Schedule").child(currDate).child(shift).child(emailToRemove).removeValue();

        }

        range = createSalaryMonthRange();

        dbRef.child(company_name).child("Employees").child(emailToRemove).child("Shifts History").child(range).child("Date: "+ currDate + ", Shift: " +shift).removeValue();

        return;
    }

    public void removeShift(String shift) //removes entire shift from database
    {
        if(isCurrDateBeforeToday() == true) //we don't want to modify shifts from the past
        {
            Toast.makeText(getContext(), "Can't modify old shifts!", Toast.LENGTH_SHORT).show();
            return;
        }

        dbRef.child(company_name).child("Departments").child(currDept).child("Schedule").addValueEventListener(new ValueEventListener()
        {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot)
            {
                if(snapshot.getChildrenCount() == 0)//prevents schedule from ever getting removed from Database
                    dbRef.child(company_name).child("Departments").child(currDept).child("Schedule").setValue("Empty");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error)
            {

            }
        });

        if(eMailNameMap.size() == 0)
        {
            dbRef.child(company_name).child("Departments").child(currDept).child("Schedule").child(currDate.toString()).child(shift).removeValue();
        }
        else
        {
            Toast.makeText(getActivity(), "Please remove employees first", Toast.LENGTH_SHORT).show();
        }
        return;
    }

    public void createShift()//creates a new shift for the current department
    {
        if(isCurrDateBeforeToday() == true) //we don't want to modify shifts from the past
        {
            Toast.makeText(getContext(), "Can't modify old shifts!", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            linearLayout.removeAllViews();
        } catch (Exception e)
        {
            Log.d("removeAllViews", "createShift: " + e.toString());
        }

        TextView textViewStart = (TextView) new TextView(getActivity()), textViewFinish = (TextView) new TextView(getActivity());
        EditText start = (EditText) new EditText(getActivity()), finish = (EditText) new EditText(getActivity());;
        Button btnCreateShift = (Button) new Button(getActivity());
        btnCreateShift.setBackgroundResource(R.drawable.button_third_design);

        textViewStart.setText("START");
        textViewStart.setTextColor(Color.BLACK);
        //textViewStart.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
        textViewStart.setTextSize(15);
        textViewFinish.setText("FINISH");
        textViewFinish.setTextColor(Color.BLACK);
        //textViewFinish.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
        textViewFinish.setTextSize(15);
        btnCreateShift.setText("Create Shift");
        btnCreateShift.setBackgroundResource(R.drawable.background_design);

        linearLayout.addView(textViewStart, linearLayoutParams);
        linearLayout.addView(start, linearLayoutParams);

        linearLayout.addView(textViewFinish, linearLayoutParams);
        linearLayout.addView(finish, linearLayoutParams);

        linearLayout.addView(btnCreateShift, linearLayoutParams);

        Log.d("CreateShift", "1");

        btnCreateShift.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                String floatedStart, floatedFinish, verifyStartSemiCol, verifyFinishSemiCol;
                int startHours, startMinutes, finishHours, finishMinutes;

                if(start.getText().toString().length() == 0 || finish.getText().toString().length() == 0
                        || start.getText().toString().length() != 5 || finish.getText().toString().length() != 5 )
                {
                    Toast.makeText(getContext(), "Wrong input, try HH:MM (i.e. 07:30)", Toast.LENGTH_LONG).show();

                    return;

                }

                floatedStart = start.getText().toString().replace(':', '.');
                floatedFinish = finish.getText().toString().replace(':', '.');
                verifyStartSemiCol = start.getText().toString().substring(2,3);
                verifyFinishSemiCol = finish.getText().toString().substring(2,3);

                Log.d("floatedStart", "verifyStartSemiCol = " + verifyStartSemiCol);

                Log.d("floatedStart", "verifyFinishSemiCol = " + verifyFinishSemiCol);

                if(!(verifyStartSemiCol.equals(":")))
                {
                    Toast.makeText(getContext(), "Please enter appropriate start time (i.e. 07:30)", Toast.LENGTH_LONG).show();
                    return;
                }

                if(!(verifyFinishSemiCol.equals(":")))
                {
                    Toast.makeText(getContext(), "Please enter appropriate finish time (i.e. 07:30)", Toast.LENGTH_LONG).show();
                    return;
                }

                try {
                    Float.parseFloat(floatedStart);
                }
                catch (Exception e)
                {
                    Toast.makeText(getContext(), "Please enter appropriate start time (i.e. 07:30)", Toast.LENGTH_LONG).show();
                    return;
                }

                try {
                    Float.parseFloat(floatedFinish);
                }catch (Exception e)
                {
                    Toast.makeText(getContext(), "Please enter appropriate finish time (i.e. 07:30)", Toast.LENGTH_LONG).show();
                    return;
                }

                startHours = Integer.parseInt(floatedStart.substring(0,2));
                startMinutes = Integer.parseInt(floatedStart.substring(3));
                finishHours = Integer.parseInt(floatedFinish.substring(0,2));
                finishMinutes = Integer.parseInt(floatedFinish.substring(3));


                if( startHours > 23 || startMinutes > 59 || finishHours > 23 || finishMinutes > 59)
                {
                    Toast.makeText(getContext(), "Hours or minutes exceeding limit", Toast.LENGTH_LONG).show();
                    return;
                }

                String newShift = start.getText().toString()+"-"+finish.getText().toString();
                dbRef.child(company_name).child("Departments").child(currDept).child("Schedule").child(currDate).child(newShift).setValue("Empty");
                shiftsList.add(newShift);
            }
        });

    }

    public void setCompanyName(String c_name)
    {
        company_name = c_name;
    }

    public void setTime(String c_time) //updated by CalendarActivity when the calendar view frag listener notifies the date changed
    {
        currDate = c_time;

        if(countDateCalls == 0)
        {
            todaysDate = currDate;
            countDateCalls++;
        }
    }

    public boolean isCurrDateBeforeToday() //returns 1 if the currently selected date is before today's date
    {

        int currDateYear, currDateMonth, currDateDay, todaysYear, todaysMonth, todaysDay, indexOfDash;
        String temp, temp2;

        temp = currDate; //extracting dayOfMonth, month & year from currDate
        indexOfDash = currDate.indexOf('-');
        currDateDay = Integer.parseInt(temp.substring(0, indexOfDash));
        temp2 = temp.substring(indexOfDash+1); //temp2 = 3-2022
        indexOfDash = temp2.indexOf('-');
        currDateMonth = Integer.parseInt(temp2.substring(0, indexOfDash));
        currDateYear = Integer.parseInt(temp2.substring(indexOfDash+1));

        temp = todaysDate; ////extracting dayOfMonth, month & year from todaysDate
        indexOfDash = todaysDate.indexOf('-');
        todaysDay = Integer.parseInt(temp.substring(0, indexOfDash));
        temp2 = temp.substring(indexOfDash+1); //temp2 = 3-2022
        indexOfDash = temp2.indexOf('-');
        todaysMonth = Integer.parseInt(temp2.substring(0, indexOfDash));
        todaysYear = Integer.parseInt(temp2.substring(indexOfDash+1));

        if(todaysYear > currDateYear)
            return true;
        else
        if(todaysMonth > currDateMonth && todaysYear == currDateYear)
            return true;
        else
        if(todaysDay > currDateDay && todaysMonth == currDateMonth)
            return true;

        return false;
    }

    /////////////////////////AUTO-BUILT METHODS AND SETTINGS/////////////////////////

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public void ManagerShiftsFragment()
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
    public static ManagerShiftsFragment newInstance(String param1, String param2)
    {
        ManagerShiftsFragment fragment = new ManagerShiftsFragment();
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