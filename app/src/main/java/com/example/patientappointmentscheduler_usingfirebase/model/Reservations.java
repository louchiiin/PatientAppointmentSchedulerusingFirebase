package com.example.patientappointmentscheduler_usingfirebase.model;

public class Reservations {
    String loggedInUid;
    String appointmentCategory;
    String doctorsName;
    String patientsName;
    String appointmentDateTime;
    String currentDate;
    String status;

    public Reservations() {
    }

    public Reservations(String loggedInUid, String appointmentCategory, String doctorsName, String patientsName, String appointmentDateTime, String currentDate, String status) {
        this.loggedInUid = loggedInUid;
        this.appointmentCategory = appointmentCategory;
        this.doctorsName = doctorsName;
        this.patientsName = patientsName;
        this.appointmentDateTime = appointmentDateTime;
        this.currentDate = currentDate;
        this.status = status;
    }

    public String getLoggedInUid() {
        return loggedInUid;
    }

    public void setLoggedInUid(String loggedInUid) {
        this.loggedInUid = loggedInUid;
    }

    public String getAppointmentCategory() {
        return appointmentCategory;
    }

    public void setAppointmentCategory(String appointmentCategory) {
        this.appointmentCategory = appointmentCategory;
    }

    public String getDoctorsName() {
        return doctorsName;
    }

    public void setDoctorsName(String doctorsName) {
        this.doctorsName = doctorsName;
    }

    public String getPatientsName() {
        return patientsName;
    }

    public void setPatientsName(String patientsName) {
        this.patientsName = patientsName;
    }

    public String getAppointmentDateTime() {
        return appointmentDateTime;
    }

    public void setAppointmentDateTime(String appointmentDateTime) {
        this.appointmentDateTime = appointmentDateTime;
    }

    public String getCurrentDate() {
        return currentDate;
    }

    public void setCurrentDate(String currentDate) {
        this.currentDate = currentDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
