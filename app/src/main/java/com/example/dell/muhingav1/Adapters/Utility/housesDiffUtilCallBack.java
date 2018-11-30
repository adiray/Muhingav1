package com.example.dell.muhingav1.Adapters.Utility;

import android.support.v7.util.DiffUtil;

import java.util.ArrayList;


/* this class is the template for the diffUtil object that compares the original list of houses and
the list of houses returned when a filter is applied and then updates the adapter */

public class housesDiffUtilCallBack extends DiffUtil.Callback {

    ArrayList<HousesResponse> filteredHousesResponseArray = new ArrayList<HousesResponse>(),allHousesResponseArray = new ArrayList<HousesResponse>();

    public housesDiffUtilCallBack(ArrayList<HousesResponse> filteredHousesResponseArray, ArrayList<HousesResponse> allHousesResponseArray) {
        this.filteredHousesResponseArray = filteredHousesResponseArray;
        this.allHousesResponseArray = allHousesResponseArray;
    }

    @Override
    public int getOldListSize() {
        return allHousesResponseArray.size();
    }

    @Override
    public int getNewListSize() {
        return filteredHousesResponseArray.size();
    }

    @Override
    public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
        return allHousesResponseArray.get(oldItemPosition).getObjectId()== filteredHousesResponseArray.get(newItemPosition).getObjectId();
    }

    @Override
    public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
        return allHousesResponseArray.get(oldItemPosition).getTitle().equals(filteredHousesResponseArray.get(newItemPosition).getTitle());
    }



}