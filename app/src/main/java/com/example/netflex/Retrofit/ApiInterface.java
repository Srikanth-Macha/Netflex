package com.example.netflex.Retrofit;

import static com.example.netflex.Retrofit.RetrofitClient.BASE_URL;

import com.example.netflex.Model.AllCategory;

import java.util.List;

import io.reactivex.Observable;
import retrofit2.http.GET;

public interface ApiInterface {
    @GET(BASE_URL)
    Observable<List<AllCategory>> getAllCategoryMovies();
}
