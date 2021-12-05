package com.example.safespace;

import android.app.AlertDialog;
import android.content.Context;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.List;

public class ImageAdapter extends RecyclerView.Adapter<ImageAdapter.ImageViewHolder> {
    private Context mContext;

    private List<putImage> muploads;
    private OnItemClickListner mListner;
    public ImageAdapter(Context context,List<putImage> uploads){
        muploads=uploads;
        mContext=context;

    }
    @NonNull
    @Override
    public ImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v= LayoutInflater.from(mContext).inflate(R.layout.image_item,parent,false);
        return new ImageViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ImageViewHolder holder, int position) {
        putImage uploadCurrent=muploads.get(position);
        holder.textViewName.setText(uploadCurrent.getName());
        Picasso.with(mContext)
                .load(uploadCurrent.getUrl())
                .placeholder(R.mipmap.ic_launcher)
                .fit()
                .centerCrop()
                .into(holder.imageView);
    }

    @Override
    public int getItemCount() {
        return muploads.size();
    }

    public  class ImageViewHolder extends  RecyclerView.ViewHolder implements View.OnClickListener,View.OnCreateContextMenuListener,MenuItem.OnMenuItemClickListener{
        public TextView textViewName;
        public ImageView imageView;
        public ImageViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewName=itemView.findViewById(R.id.text_view_name);
            imageView=itemView.findViewById(R.id.image_view_upload);
            itemView.setOnClickListener(this);
            itemView.setOnCreateContextMenuListener(this);
        }

        @Override
        public void onClick(View view) {
            if(mListner !=null)
            {
                int position=getAdapterPosition();
                if(position!=RecyclerView.NO_POSITION){
                    mListner.onItemClick(position);
                }
            }
        }

        @Override
        public void onCreateContextMenu(ContextMenu menu, View view, ContextMenu.ContextMenuInfo contextMenuInfo) {
            menu.setHeaderTitle("Select");
            MenuItem doWhatever=menu.add(Menu.NONE,1,1,"Download");
            MenuItem delete=menu.add(Menu.NONE,2,2,"Delete");
            MenuItem share=menu.add(Menu.NONE,3,3,"Share");
            doWhatever.setOnMenuItemClickListener(this);
            delete.setOnMenuItemClickListener(this);
            share.setOnMenuItemClickListener(this);


        }

        @Override
        public boolean onMenuItemClick(MenuItem item) {
            if(mListner !=null)
            {
                int position=getAdapterPosition();
                if(position!=RecyclerView.NO_POSITION){
                    switch(item.getItemId()){
                        case 1:
                            mListner.onWhatEverClick(position);
                            return true;
                        case 2:

                            mListner.onDeleteClick(position);
                            return true;
                        case 3:
                            Toast.makeText(imageView.getContext(), "sharing",Toast.LENGTH_SHORT).show();
                            mListner.onShareClick(position);
                            return true;
                    }
                }
            }
            return false;
        }
    }
    public interface OnItemClickListner{
        void onItemClick(int position);
        void onWhatEverClick(int position);
        void onDeleteClick(int position);

        void onShareClick(int position);
    }
    public void setOnItemClickListner(OnItemClickListner listner){
        mListner=listner;
    }
}

