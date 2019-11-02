package com.babarehner.wallaby;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.babarehner.wallaby.BaseCursorAdapter;
import com.babarehner.wallaby.data.WallabyContract;
import com.bumptech.glide.Glide;

import java.util.ArrayList;


/**
 * Project Name: Wallaby
 * <p>
 * Copyright 10/16/19 by Mike Rehner
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

public class WallabyCursorAdapter extends BaseCursorAdapter<WallabyCursorAdapter.WallabyViewHolder> {

    private Context mContext;
    private ArrayList<String> mFileNames = new ArrayList<>();
    private Cursor mCursor;

    public interface OnItemClickLIstener{
        void omItemClick(View view, int position);
        void onEditButtonClick(View view, int position);
    }

    private AdapterView.OnItemClickListener mListener;

    //private RecyclerViewClickListener mListener;
    //private List<Data> mDataset = new ArrayList<>();

    public WallabyCursorAdapter(){
        super(null);
         //mListener = listener;
    }

    public void setOnItemClickListener(AdapterView.OnItemClickListener listener){
        mListener = listener;
    }


    @Override
    public WallabyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View formNameView = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_item, parent, false);
        mContext = parent.getContext();
        return new WallabyViewHolder(formNameView, (OnItemClickLIstener) mListener);
    }


    @Override
    public void onBindViewHolder(WallabyViewHolder holder, Cursor cursor) {

        int mColumnIndexName = cursor.getColumnIndex(WallabyContract.WallabyTableConstants.C_CARD_N);
        int mColumnIndexFileName = cursor.getColumnIndex(WallabyContract.WallabyTableConstants.C_IMAGE_FN);
        String cardName = cursor.getString(mColumnIndexName);
        final String fileName = cursor.getString(mColumnIndexFileName);
        holder.nameTextView.setText(cardName);
        holder.fileNameTextView.setText(fileName);
        Glide.with(mContext).load(fileName).into(holder.image);

        //animate holder
    }


    @Override
    public void swapCursor(Cursor newCursor) {
        super.swapCursor(newCursor);
    }



    public static class WallabyViewHolder extends RecyclerView.ViewHolder {

        //private RecyclerViewClickListener mListener;
        //private RecyclerViewClickListener mButtonLIstener;

        TextView nameTextView;
        TextView fileNameTextView;
        ImageView image;
        Button b;

        WallabyViewHolder(View itemView, final OnItemClickLIstener listener) {
            super(itemView);

            nameTextView = itemView.findViewById(R.id.graphic_name);
            fileNameTextView = itemView.findViewById(R.id.file_name);
            image = itemView.findViewById(R.id.imageView);
            b = itemView.findViewById(R.id.button_image);

            // mListener = listener;
            //v.setOnClickListener(this);

            itemView.setOnClickListener(new View.OnClickListener(){
                public void onClick(View v){
                    if (listener != null){
                        int position = getLayoutPosition();
                        if (position != RecyclerView.NO_POSITION){
                            listener.omItemClick(v, position);
                        }
                    }
                }
            });


            b.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (listener != null){
                        int position = getLayoutPosition();
                        if (position != RecyclerView.NO_POSITION){
                            listener.onEditButtonClick(view, position);
                        }
                    }

                }
            });


        }


       // @Override
        //public void onClick(View v) {
        //    mListener.onClick(v, getLayoutPosition());
        //}


    }


    // Use interface- implemented in MainActivity
    //public interface RecyclerViewClickListener{

        //void onClick(View v, int pos);

        //void onButtonClick(int pos);

    //}



}
