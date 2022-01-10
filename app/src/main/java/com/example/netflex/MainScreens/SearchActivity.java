package com.example.netflex.MainScreens;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.SearchView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.netflex.Adapters.SearchRecyclerAdapter;
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

public class SearchActivity extends AppCompatActivity {
    BottomNavigationView bottomNavigationView;

    SearchRecyclerAdapter searchRecyclerAdapter;
    RecyclerView searchRecycler;
    List<AllCategory> allCategoryList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        bottomNavigationView = findViewById(R.id.bottom_navigationViewID);
        Menu navigationMenu = bottomNavigationView.getMenu();

        // To make Home tab selected by default in navigation bar
        navigationMenu.getItem(1).setChecked(true);

        // Defines the what to do when nav bar icons are clicked
        navigationBarFunctionality();

        Toast.makeText(this, "Search", Toast.LENGTH_SHORT).show();

        //TODO CONTINUE THIS|||
//        toolbar = findViewById(R.id.searchToolBarID);

        allCategoryList = new ArrayList<>();
        getMovieData();
    }

    private void navigationBarFunctionality() {
        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();

                if (R.id.homeIcon == id) {
                    Intent i = new Intent(SearchActivity.this, MainActivity.class);
                    startActivity(i);
                    finish();

                } else if (R.id.settingsIcon == id) {
                    Intent i = new Intent(SearchActivity.this, Settings.class);
                    startActivity(i);
                    finish();
                }

                return false;
            }
        });
    }

    private void setSearchRecycler(List<AllCategory> allCategoryList) {
        searchRecycler = findViewById(R.id.searchRecyclerView);
        searchRecyclerAdapter = new SearchRecyclerAdapter(this, allCategoryList);

        searchRecycler.setLayoutManager(new LinearLayoutManager(this));
        searchRecycler.setAdapter(searchRecyclerAdapter);
    }

    private void getMovieData() {
        CompositeDisposable compositeDisposable = new CompositeDisposable();
        compositeDisposable.add(RetrofitClient.getRetrofitClient().getAllCategoryMovies()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableObserver<List<AllCategory>>() {

                    @Override
                    public void onNext(@NonNull List<AllCategory> allCategoryList) {
                        setSearchRecycler(allCategoryList);
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.search_menu, menu);

        MenuItem searchItem = menu.findItem(R.id.searchView);

        SearchView searchView = (SearchView) searchItem.getActionView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                searchRecyclerAdapter.getFilter().filter(s);
                return false;
            }
        });

        return true;
    }
}