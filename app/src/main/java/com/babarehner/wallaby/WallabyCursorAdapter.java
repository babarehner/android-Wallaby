package com.babarehner.wallaby;

import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
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

import static com.babarehner.wallaby.data.WallabyContract.WallabyTableConstants.WALLABY_URI;


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

    public static final String TAG = "WallabyCursorAdapter";

    private Context mContext;
    private ArrayList<String> mFileNames = new ArrayList<>();
    private Cursor mCursor;

    private RecyclerViewClickListener mListener;


    public WallabyCursorAdapter(RecyclerViewClickListener listener){
        super(null);
        this.mListener = listener;
    }


    @Override
    public WallabyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View formNameView = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_item, parent, false);
        mContext = parent.getContext();
        return new WallabyViewHolder(formNameView, mListener);
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

    }


    @Override
    public void swapCursor(Cursor newCursor) {
        super.swapCursor(newCursor);
    }



    class WallabyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private RecyclerViewClickListener listener;
        //private RecyclerViewClickListener buttonListener;

        TextView nameTextView;
        TextView fileNameTextView;
        ImageView image;
        Button b;

        WallabyViewHolder(View v, RecyclerViewClickListener listener) {
            super(v);


            nameTextView = itemView.findViewById(R.id.graphic_name);
            fileNameTextView = itemView.findViewById(R.id.file_name);
            image = itemView.findViewById(R.id.imageView);
            b =itemView.findViewById(R.id.edit_button);

            this.listener = listener;
            ///this.buttonListener = buttonListener;

            v.setOnClickListener(this);
            b.setOnClickListener(this);

        }


        @Override
        public void onClick(View view) {
            switch (view.getId()){
                case R.id.imageView:
                    listener.onItemClick(this.getLayoutPosition());
                    break;
                case R.id.edit_button:
                    listener.onButtonClick(this.getLayoutPosition());
                    break;
                default:
                    break;
            }
        }

    }

    // Use interface- implemented in MainActivity
    public interface RecyclerViewClickListener{

        void onItemClick(int pos);
        void onButtonClick(int pos);

    }



}
