package com.example.androidproject.classes;

import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class FirebaseUpdate
{
    FirebaseUpdate(){};

    public static void update()
    {
        FirebaseDatabase db = FirebaseDatabase.getInstance();
        String department = "Programmers", salary = "34.76", day = "10-2-2022", shift = "08:00-17:00", range = "From:  1-2-2022   To:  31-2-2022", dateAndShift = "Date: "+day+", Shift: "+shift+":";

        DatabaseReference dbRef1 = db.getReference().child("Companies").child("Apple").child("Departments").child(department).child("Schedule").child(day).child(shift);
        DatabaseReference dbRef2 = db.getReference().child("Companies").child("Apple").child("Employees");

        for(int i = 0; i < 10; i++)
        {
            //add
            dbRef1.child("user"+i+"_apple@gmail_com").setValue("user"+i+"_apple");
            dbRef2.child("user"+i+"_apple@gmail_com").setValue(new Employee("Programmers","user"+i+"_apple@gmail_com","user"+i+"_apple", salary));
            dbRef2.child("user"+i+"_apple@gmail_com").child("Shifts History").child(range).child("Date: "+ day + ", Shift: " +shift).setValue("Hours worked: "+"~9.00");

            //remove
//            dbRef1.child("user"+i+"_apple@gmail_com").removeValue();
//            dbRef2.child("user"+i+"_apple@gmail_com").removeValue();
        }
    }
}