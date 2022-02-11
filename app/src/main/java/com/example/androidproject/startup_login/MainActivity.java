package com.example.androidproject.startup_login;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.Toast;

import com.example.androidproject.employee_options.EmployeeOptionsActivity;
import com.example.androidproject.classes.Employee;
import com.example.androidproject.classes.FirebaseUpdate;
import com.example.androidproject.R;
import com.example.androidproject.manager_options.ManagerOptionsActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Locale;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener
{
    private EditText editTextEmail, editTextPassword;
    private FirebaseAuth mAuth;
    private Button btnUpdate;
    private Employee employee;
    private String convertedEmail;
    private Switch switchUserAdmin;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();
        editTextEmail = findViewById(R.id.editTextEmail);
        editTextPassword = findViewById(R.id.editTextPassword);
        switchUserAdmin = findViewById(R.id.switchUserAdmin);
        btnUpdate = findViewById(R.id.btnUpdate);
        Spinner spinner = findViewById(R.id.spinner1);
        progressBar = findViewById(R.id.progressBar);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.companies, R.layout.color_spinner_layout);
        adapter.setDropDownViewResource(R.layout.spinner_dropdown_layout);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(this);

        switchUserAdmin.setOnClickListener(new View.OnClickListener()//helps to quickly switch between user and admin account for testing
        {
            @Override
            public void onClick(View view)
            {
                String temp = editTextEmail.getText().toString(), temp2;
                int index = temp.indexOf('.');
                temp2 = temp.substring(index);

                if(temp.contains("user"))
                    editTextEmail.setText("admin"+temp2);
                else
                    editTextEmail.setText("user"+temp2);
            }
        });

        btnUpdate.setOnClickListener(new View.OnClickListener()//updates the database from FirebaseUpdate class for testing purposes
        {
            @Override
            public void onClick(View view)
            {
                FirebaseUpdate.update();
            }
        });
    }

    //notifies if item at opening screen spinner changes
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
    {
        String text = parent.getItemAtPosition(position).toString();
    }
    @Override
    public void onNothingSelected(AdapterView<?> parent) {
    }

    private String convertEmail(String email_dot)//converts emails to firebase-friendly format
    {
        String temp = email_dot;
        temp = temp.replace("_", "__");
        temp = temp.replace('.', '_');

        return temp;
    }

    private int isManager(String convertedLoginEmail, String company) //default admin account is admin.CompanyName@gmail.com
    {
        String adminEmail = "admin_"+company.toLowerCase(Locale.ROOT)+"@gmail_com"; //preset default admin account
        if(adminEmail.equals(convertedLoginEmail.toLowerCase(Locale.ROOT)))
            return 1;
        return 0;
    }

    public void onClickedLogin(View view)
    {
        String company_name = ((Spinner) findViewById(R.id.spinner1)).getSelectedItem().toString();
        String email = editTextEmail.getText().toString().toLowerCase(Locale.ROOT);
        String password = editTextPassword.getText().toString();
        Intent intentMenu;

        progressBar.setVisibility(View.VISIBLE);
        convertedEmail = convertEmail(email);

        if(isManager(convertedEmail, company_name) == 1) //user is manager
            intentMenu = new Intent(this, ManagerOptionsActivity.class);
        else
            intentMenu = new Intent(this, EmployeeOptionsActivity.class);

        if (company_name.equals("Select Company"))//user didn't select company from the spinner
        {
            Toast.makeText(this, "Please Select a Company", Toast.LENGTH_SHORT).show();
            progressBar.setVisibility(View.INVISIBLE);
        }
        else
        {
            mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>()
            {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task)
                {
                    if (task.isSuccessful())
                    {
                        DatabaseReference dbRefSignIn = FirebaseDatabase.getInstance().getReference().child("Companies").child(company_name).child("Employees");

                        dbRefSignIn.addValueEventListener(new ValueEventListener()
                        {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot)
                            {
                                if(snapshot.child(convertedEmail).exists())
                                {
                                    for(DataSnapshot dSnapshot : snapshot.getChildren())
                                    {
                                        if(dSnapshot.getKey().toString().equals(convertedEmail))
                                        {
                                           employee = dSnapshot.getValue(Employee.class);
                                        }
                                    }
                                    intentMenu.putExtra("email", employee.getMail());
                                    intentMenu.putExtra("company",company_name);
                                    intentMenu.putExtra("department", employee.getDepartment());
                                    intentMenu.putExtra("name", employee.getName());
                                    intentMenu.putExtra("isManager",isManager(convertedEmail, company_name));
                                    intentMenu.putExtra("salary",employee.getSalary());
                                    startActivity(intentMenu);
                                }
                                else
                                {
                                    Toast.makeText(MainActivity.this, "Failed to login after auth", Toast.LENGTH_SHORT).show();
                                    progressBar.setVisibility(View.INVISIBLE);
                                }
                            }
                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                            }
                        });

                    } else
                        {
                            Toast.makeText(MainActivity.this, "Failed to login", Toast.LENGTH_SHORT).show();
                            progressBar.setVisibility(View.INVISIBLE);
                        }
                }
            });
        }
    }
}