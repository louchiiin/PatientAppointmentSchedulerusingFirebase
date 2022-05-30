package com.example.patientappointmentscheduler_usingfirebase;

import com.example.patientappointmentscheduler_usingfirebase.model.NewsHeadlines;

import java.util.List;

public interface OnFetchDataListener<NewsApiResponse> {
    void onFetchData(List<NewsHeadlines> list, String message);
    void onError(String message);
}
