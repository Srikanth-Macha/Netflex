package com.example.netflex.MainScreens;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.netflex.Adapters.MainRecyclerAdapter;
import com.example.netflex.Model.AllCategory;
import com.example.netflex.R;
import com.example.netflex.Retrofit.RetrofitClient;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;

public class TvSeries extends AppCompatActivity {
    BottomNavigationView bottomNavigationView;

    MainRecyclerAdapter mainRecyclerAdapter;
    RecyclerView movieRecycler;
    List<AllCategory> allCategoryList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tv_series);

        bottomNavigationView = findViewById(R.id.bottom_navigationViewID);
        Menu navigationMenu = bottomNavigationView.getMenu();

        // To make Home tab selected by default in navigation bar
        navigationMenu.getItem(0).setChecked(true);

        // Defines the what to do when nav bar icons are clicked
        navigationBarFunctionality();

        allCategoryList = new ArrayList<>();
        getMovieData();
    }

    private void navigationBarFunctionality() {
        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();

                if (R.id.searchIcon == id) {
                    Intent i = new Intent(TvSeries.this, SearchActivity.class);
                    startActivity(i);
                    finish();

                } else if (R.id.settingsIcon == id) {
                    Intent i = new Intent(TvSeries.this, Settings.class);
                    startActivity(i);
                    finish();
                }

                return false;
            }
        });
    }

    private void setMovieRecycler(List<AllCategory> allCategoryList) {
        movieRecycler = findViewById(R.id.tvSeriesRecyclerView);
        mainRecyclerAdapter = new MainRecyclerAdapter(this, allCategoryList);

        movieRecycler.setLayoutManager(new LinearLayoutManager(this));
        movieRecycler.setAdapter(mainRecyclerAdapter);
    }

    private void getMovieData() {
        CompositeDisposable compositeDisposable = new CompositeDisposable();
        compositeDisposable.add(RetrofitClient.getRetrofitClient().getAllCategoryMovies()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableObserver<List<AllCategory>>() {

                    @Override
                    public void onNext(@NonNull List<AllCategory> allCategoryList) {
                        setMovieRecycler(allCategoryList);
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                    }

                    @Override
                    public void onComplete() {
                    }
                })

        );
    }
}