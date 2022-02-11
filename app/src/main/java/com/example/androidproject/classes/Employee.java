package com.example.androidproject.classes;

public class Employee
{
    String mail, department, name, salary;

    public String getSalary()
    {
        return salary;
    }

    public void setSalary(String salary)
    {
        this.salary = salary;
    }


    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public String getDepartment()
    {
        return department;
    }

    public void setDepartment(String department)
    {
        this.department = department;
    }

    public String getMail()
    {
        return mail;
    }

    public void setMail(String mail)
    {
        this.mail = mail;
    }

    public Employee(){}

    public Employee(String department, String mail, String name, String salary)
    {
        this.department = department;
        this.mail = mail;
        this.name = name;
        this.salary = salary;
    }

}
