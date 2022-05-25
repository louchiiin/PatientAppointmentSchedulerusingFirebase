package com.example.patientappointmentscheduler_usingfirebase.model;

public class ReservationList {
    String reservationID;
    String loggedInUid;
    String appointmentCategory;
    String patientsName;
    String doctorsName;
    String appointmentDateTime;
    String currentDate;
    String status;

    public ReservationList() {
    }

    public ReservationList(String reservationID, String loggedInUid, String appointmentCategory, String patientsName, String doctorsName, String appointmentDateTime, String currentDate, String status) {
        this.reservationID = reservationID;
        this.loggedInUid = loggedInUid;
        this.appointmentCategory = appointmentCategory;
        this.patientsName = patientsName;
        this.doctorsName = doctorsName;
        this.appointmentDateTime = appointmentDateTime;
        this.currentDate = currentDate;
        this.status = status;
    }

    public String getPatientsName() {
        return patientsName;
    }

    public void setPatientsName(String patientsName) {
        this.patientsName = patientsName;
    }

    public String getReservationID() {
        return reservationID;
    }

    public void setReservationID(String reservationID) {
        this.reservationID = reservationID;
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
