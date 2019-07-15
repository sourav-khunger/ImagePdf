package com.example.imagepdf;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListAdapter;

import androidx.recyclerview.widget.RecyclerView;


import java.util.ArrayList;

/**
 * Created by Rohit on 6/17/2018.
 */

public class ImageListAdapter extends RecyclerView.Adapter<ImageListAdapter.ViewHolder> {

    private LayoutInflater inflator;
    private ArrayList<CapturedImageData> itemlist;
    public Context context;

    public ImageListAdapter(Context context, ArrayList<CapturedImageData> itemlist) {
        this.context = context;

        this.itemlist = itemlist;

        inflator = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = inflator.inflate(R.layout.grid_image, null, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        final CapturedImageData data = itemlist.get(position);
        if(data.getImageBitmap()!=null)
        {
holder.timeTexView.setImageBitmap(data.getImageBitmap());
            holder.timeTexView.setVisibility(View.VISIBLE);
            //holder.addButton.setVisibility(View.GONE);
        }
        else
        {
            if(position==itemlist.size()-1) {
                holder.timeTexView.setVisibility(View.GONE);
                //holder.addButton.setVisibility(View.VISIBLE);
                //holder.addButton.setOnClickListener(new AddChimImageButtonClickListener());
            }
            else
            {
                holder.timeTexView.setVisibility(View.GONE);
                //holder.addButton.setVisibility(View.GONE);
            }
        }


    }

    @Override
    public int getItemCount() {
        return itemlist.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {


        private ImageView timeTexView;
       // private Button addButton;

        ViewHolder(View view) {
            super(view);
            timeTexView = (ImageView)view.findViewById(R.id.icon);

           // addButton = (Button) view.findViewById(R.id.addButton);


        }

    }

    class AddChimImageButtonClickListener implements View.OnClickListener
    {

        @Override
        public void onClick(View v) {
           // ((UpdateServiceActivity)context).openPicker();
        }


    }

}
