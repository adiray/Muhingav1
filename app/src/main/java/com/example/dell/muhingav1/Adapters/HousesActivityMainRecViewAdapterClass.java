package com.example.dell.muhingav1.Adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.example.dell.muhingav1.Adapters.Utility.HousesResponse;
import com.example.dell.muhingav1.R;

import java.util.ArrayList;

public class HousesActivityMainRecViewAdapterClass extends RecyclerView.Adapter<HousesActivityMainRecViewAdapterClassVH> {

    //these are arguments you will have to pass in when calling the constructor
    private ArrayList<HousesResponse> dataSource;
    private Context context;

    //other variables
    private Integer totalItemCount, lastVisibleItem;
    private int visibleThreshold = 2;
    private boolean loading ;
    private OnLoadMoreListener onLoadMoreListener;


    public HousesActivityMainRecViewAdapterClass(ArrayList<HousesResponse> dataSource, Context context) {
        this.dataSource = dataSource;
        this.context = context;
    }


    public HousesActivityMainRecViewAdapterClass(Context context, ArrayList<HousesResponse> dataSource, RecyclerView recyclerView) {

        this.dataSource = dataSource;
        this.context = context;

        final GridLayoutManager GridLayoutManager = (GridLayoutManager) recyclerView.getLayoutManager();

            recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {


                @Override
                public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                    super.onScrolled(recyclerView, dx, dy);
                    totalItemCount = GridLayoutManager.getItemCount();
                    lastVisibleItem = GridLayoutManager.findLastVisibleItemPosition();

                    if (!loading && totalItemCount <= (lastVisibleItem + visibleThreshold)) {
                        if (onLoadMoreListener != null) {
                            onLoadMoreListener.onLoadMore();
                        }
                        loading = true;
                    }


                }
            });

    }


    @Override
    public int getItemViewType(int position) {
        return dataSource.get(position) != null ? 1 : 0;
    }

    @NonNull
    @Override
    public HousesActivityMainRecViewAdapterClassVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        if (!loading) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.houses_activity_rec_view_single_object, parent, false);
            return new HousesActivityMainRecViewAdapterClassVH(v);
        } else {

            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.progress_item, parent, false);
            return new ProgressViewHolder(v);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull HousesActivityMainRecViewAdapterClassVH holder, int position) {

        if (holder instanceof ProgressViewHolder) {

            ((ProgressViewHolder) holder).progressBar.setIndeterminate(true);

        }  else{ //set the text that shows the title of the house
            holder.title_vh.setText(dataSource.get(position).getTitle());

            //set the text that describes whether the item is for sale or for rent. Use the resource ids instead of string
            //literals to make it easier to translate
            if (dataSource.get(position).isRent()) {

                holder.rent_vh.setText(R.string.ForRent);

            } else {

                holder.rent_vh.setText(R.string.ForSale);

            }

            //set the text that shows the price of the house
            holder.price_vh.setText(dataSource.get(position).getPrice());

            //set the text that shows the location of the house
            holder.location_vh.setText(dataSource.get(position).getLocation());

            //set the image of the house using the glide image library
            Glide.with(context).load(dataSource.get(position).getMianImageReference()).into(holder.house_main_image_vh);}

    }

    @Override
    public int getItemCount() {
        return dataSource.size();
    }

    public void setLoading(Boolean loading){
        this.loading = loading;
    }

    //this interface contains the methods that determine what happens when it is time to load more items
    public interface OnLoadMoreListener {
        void onLoadMore();

    }

    public void setOnLoadMoreListener(OnLoadMoreListener onLoadMoreListener){
        this.onLoadMoreListener = onLoadMoreListener;
    }

  /*  public void setLoaded() {
        loading = false;
    }*/


}