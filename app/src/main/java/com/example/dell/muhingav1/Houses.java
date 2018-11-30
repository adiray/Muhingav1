package com.example.dell.muhingav1;

import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.example.dell.muhingav1.Adapters.HousesActivityMainRecViewAdapterClass;
import com.example.dell.muhingav1.Adapters.Utility.HousesResponse;
import com.example.dell.muhingav1.Adapters.Utility.RetrofitClient;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import br.vince.easysave.EasySave;
import br.vince.easysave.LoadAsyncCallback;
import br.vince.easysave.SaveAsyncCallback;
import okhttp3.Cache;
import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class Houses extends AppCompatActivity {

    //declare view objects
    EditText housePriceEditText;
    CheckBox forRentCheck, forSaleCheck;
    Button filterHousesButton;
    SwipeRefreshLayout housesSwipeRefresh;

    //declare recycler view objects
    RecyclerView housesMainRecView;
    HousesActivityMainRecViewAdapterClass housesAdapter, housesWithInfiniteLoadAdapter;

    //declare HousesResponse arraylists
    ArrayList<HousesResponse> filteredHousesResponseArray, allHousesResponseArray;
    Integer tableOffset = 0;

    //declare the retrofit objects
    Retrofit.Builder builder;
    Retrofit myRetrofit;
    RetrofitClient myWebClient;
    retrofit2.Call<ArrayList<HousesResponse>> allHousesCall;
    Map <String,String>filterMap = new HashMap<String, String>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_houses);

        //Initialize the views
        housesSwipeRefresh = findViewById(R.id.houses_swipe_refresh);

        //Initialize the main response array
        allHousesResponseArray = new ArrayList<>();

        //build out the main recycler view
        housesMainRecView = findViewById(R.id.houses_activity_rec_view);
        housesMainRecView.setHasFixedSize(true);
        housesMainRecView.setLayoutManager(new GridLayoutManager(Houses.this, 1, 1, false));

        filterMap.put("sortby","created%20desc");
        filterMap.put("pagesize","6");
        filterMap.put("offset",tableOffset.toString());


        requestHouses(false, false);



        housesSwipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                tableOffset = 0;
                requestHouses(true, false);


            }
        });


    }


    void loadMoreHouses(){


        housesWithInfiniteLoadAdapter.setOnLoadMoreListener(new HousesActivityMainRecViewAdapterClass.OnLoadMoreListener() {
            @Override
            public void onLoadMore() {

                tableOffset=allHousesResponseArray.size();
                housesWithInfiniteLoadAdapter.setLoading(true);

                requestHouses(false, true);
            }
        });


    }


    //MY METHODS: ALL THESE METHODS ARE USED ONLY WITHIN THIS ACTIVITY

    void requestHouses(final Boolean onRefresh, final Boolean infiniteLoad) {

        //initialize the retrofit client builder using the backendless.com api
        builder = new Retrofit.Builder();
        builder.baseUrl("http://api.backendless.com/125AF8BD-1879-764A-FF22-13FB1C162400/6F40C4D4-6CFB-E66A-FFC7-D71E4A8BF100/data/")
                .addConverterFactory(GsonConverterFactory.create());

        //use your builder to build a retrofit object
        myRetrofit = builder.build();

        //create a retrofit client using the retrofit object
        myWebClient = myRetrofit.create(RetrofitClient.class);

        //create your call using the retrofit client
        allHousesCall = myWebClient.getQueryHouses(filterMap);

        //make the call
        allHousesCall.clone().enqueue(new Callback<ArrayList<HousesResponse>>() {
            @Override
            public void onResponse(Call<ArrayList<HousesResponse>> call, Response<ArrayList<HousesResponse>> response) {



                if (onRefresh) {

                    allHousesResponseArray.clear();
                    housesWithInfiniteLoadAdapter.notifyDataSetChanged();
                    housesSwipeRefresh.setRefreshing(false);

                }

                //an array to hold the ten objects to be cached
                ArrayList<HousesResponse> objectsToBeCached = new ArrayList<HousesResponse>();

                if (response.body() != null && !infiniteLoad) {

                    //this assigns the first 10 houses objects to the array of objects to be cached
                    if (response.body().size() > 10) {
                        for (int i = 0; i < 10; i++) {
                            objectsToBeCached.add(response.body().get(i));
                        }
                    } else {
                        objectsToBeCached = response.body();
                    }

                    new EasySave(Houses.this).saveListAsync("housesCacheKey", objectsToBeCached, new SaveAsyncCallback<List<HousesResponse>>() {
                        @Override
                        public void onComplete(List<HousesResponse> housesResponses) {

                            Toast.makeText(Houses.this, "The items were saved in cache", Toast.LENGTH_LONG).show();

                        }

                        @Override
                        public void onError(String s) {

                            Toast.makeText(Houses.this, s, Toast.LENGTH_LONG).show();


                        }
                    });


                }

                //an array to hold the houses objects returned
                //allHousesResponseArray = response.body();


                //fill the adapter and assign it to the main recycler view
                //housesAdapter = new HousesActivityMainRecViewAdapterClass(allHousesResponseArray, Houses.this);

                if (response.body() != null) {
                    if (!infiniteLoad) {

                        allHousesResponseArray = response.body();

                        //test adapter for the infinite scroll
                        housesWithInfiniteLoadAdapter = new HousesActivityMainRecViewAdapterClass(Houses.this, allHousesResponseArray, housesMainRecView);
                        housesMainRecView.setAdapter(housesWithInfiniteLoadAdapter);

                        loadMoreHouses();

                    } else {

                        Integer itemCount = response.body().size(), range = allHousesResponseArray.size();

                        allHousesResponseArray.addAll(response.body());

                        housesWithInfiniteLoadAdapter.notifyItemRangeInserted(range, itemCount);

                        housesWithInfiniteLoadAdapter.setLoading(false);

                    }
                }


            }

            @Override
            public void onFailure(Call<ArrayList<HousesResponse>> call, Throwable t) {

                if (onRefresh) {

                    housesSwipeRefresh.setRefreshing(false);

                }


                Toast.makeText(Houses.this, t.getMessage(), Toast.LENGTH_LONG).show();


                if (!infiniteLoad) {

                    new EasySave(Houses.this).retrieveListAsync("housesCacheKey", HousesResponse[].class, new LoadAsyncCallback<List<HousesResponse>>() {
                        @Override
                        public void onComplete(List<HousesResponse> housesResponses) {

                            if (housesResponses != null) {

                                ArrayList<HousesResponse> retrievedHouses = new ArrayList<>(housesResponses);

                                //fill the adapter and assign it to the main recycler view
                                housesAdapter = new HousesActivityMainRecViewAdapterClass(retrievedHouses, Houses.this);
                                housesMainRecView.setAdapter(housesAdapter);

                            }

                        }

                        @Override
                        public void onError(String s) {

                            Log.d("easy cache failure", s + "failure to retrieve items");

                        }
                    });


                }

                housesWithInfiniteLoadAdapter.setLoading(false);
            }
        });

    }


    void filterButtonClicked() {


    }

}
