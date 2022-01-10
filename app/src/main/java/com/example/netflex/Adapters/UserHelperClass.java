package com.example.netflex.Adapters;

import android.widget.EditText;

public class UserHelperClass {
    String username, password, plan, firstName, lastName, pnumber, vdate;

    public UserHelperClass(String username, String password, String planname, EditText fn, EditText ln, EditText mb) {
    }

    public UserHelperClass(String username, String password, String plan, String firstName, String lastName, String pnumber, String vdate) {
        this.username = username;
        this.password = password;
        this.plan = plan;
        this.firstName = firstName;
        this.lastName = lastName;
        this.pnumber = pnumber;
        this.vdate = vdate;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPlan() {
        return plan;
    }

    public void setPlan(String plan) {
        this.plan = plan;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getPnumber() {
        return pnumber;
    }

    public void setPnumber(String pnumber) {
        this.pnumber = pnumber;
    }

    public String getVdate() {
        return vdate;
    }

    public void setVdate(String vdate) {
        this.vdate = vdate;
    }
}

