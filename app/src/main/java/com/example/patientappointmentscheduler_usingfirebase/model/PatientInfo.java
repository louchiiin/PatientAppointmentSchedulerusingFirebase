package com.example.patientappointmentscheduler_usingfirebase.model;

import com.google.firebase.auth.FirebaseUser;

public class PatientInfo {
    private String uid;
    private String firstName;
    private String lastName;
    private String phone;

    public PatientInfo() {
    }

    public PatientInfo(String uid, String firstName, String lastName, String phone) {
        this.uid = uid;
        this.firstName = firstName;
        this.lastName = lastName;
        this.phone = phone;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
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

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }
}
