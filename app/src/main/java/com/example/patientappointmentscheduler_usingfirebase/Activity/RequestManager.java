package com.example.patientappointmentscheduler_usingfirebase.Activity;

import android.content.Context;
import android.widget.Toast;

import com.example.patientappointmentscheduler_usingfirebase.Interfaces.OnFetchDataListener;
import com.example.patientappointmentscheduler_usingfirebase.R;
import com.example.patientappointmentscheduler_usingfirebase.model.NewsApiResponse;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Query;

public class RequestManager {
    private final String BASE_URL = "https://newsapi.org/v2/";
    Context context;

    Retrofit retrofit = new Retrofit.Builder().baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build();

    public void getNewsHeadLines(OnFetchDataListener listener, String category, String query){
        CallNewsApi callNewsApi = retrofit.create(CallNewsApi.class);
        Call<NewsApiResponse> call = callNewsApi.callHeadlines("ph", category, query, context.getString(R.string.health_news_api));

        try{
            call.enqueue(new Callback<NewsApiResponse>() {
                @Override
                public void onResponse(Call<NewsApiResponse> call, Response<NewsApiResponse> response) {
                    if (!response.isSuccessful()){
                        Toast.makeText(context, "Error has occurred", Toast.LENGTH_SHORT).show();
                    }
                    listener.onFetchData(response.body().getArticles(),response.message());
                }

                @Override
                public void onFailure(Call<NewsApiResponse> call, Throwable t) {
                    listener.onError("Request Failed!");
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public RequestManager(Context context) {
        this.context = context;
    }

    public interface CallNewsApi {
        @GET("top-headlines")
        Call<NewsApiResponse> callHeadlines(
                @Query("country") String country,
                @Query("category") String category,
                @Query("q") String query,
                @Query("apiKey") String apiKey
        );
    }
}
