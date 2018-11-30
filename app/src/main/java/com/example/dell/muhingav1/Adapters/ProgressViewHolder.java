package com.example.dell.muhingav1.Adapters;

import android.view.View;
import android.widget.ProgressBar;

import com.example.dell.muhingav1.R;

/* this is the viewholder class for the progress bar.it is shown when the infinite
load is still loading the next batch of items in the list */
public class ProgressViewHolder extends HousesActivityMainRecViewAdapterClassVH {

    public ProgressBar progressBar;

    public ProgressViewHolder(View itemView) {
        super(itemView);

        progressBar = (ProgressBar)itemView.findViewById(R.id.progressBar);

    }



}
