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
import android.widget.Toast;

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

    private long mID;

    private RecyclerViewClickListener mListener;


    public WallabyCursorAdapter(RecyclerViewClickListener listener){
        super(null);
        this.mListener = listener;
    }


    @Override
    @NonNull
    public WallabyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View formNameView = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_item, parent, false);
        mContext = parent.getContext();
        return new WallabyViewHolder(formNameView, mListener);
    }


    @Override
    public void onBindViewHolder(WallabyViewHolder holder, Cursor cursor) {

        int mColumnIndex_ID = cursor.getColumnIndex(WallabyContract.WallabyTableConstants._ID);
        int mColumnIndexName = cursor.getColumnIndex(WallabyContract.WallabyTableConstants.C_CARD_N);
        int mColumnIndexFileName = cursor.getColumnIndex(WallabyContract.WallabyTableConstants.C_IMAGE_FN);
        String columnIndex_ID = String.valueOf(mColumnIndex_ID);
        //Toast.makeText(WallabyCursorAdapter.this, "mCurrentRecordUri: " + columnIndex_ID, Toast.LENGTH_LONG).show();
        Log.v(TAG, "Column Index :" + columnIndex_ID);

        // Why does this line cause system to crash ???? Long _ID_ID = cursor.getLong(mColumnIndex_ID);
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
        Long rowID;

        WallabyViewHolder(View v, RecyclerViewClickListener listener) {
            super(v);


            nameTextView = itemView.findViewById(R.id.graphic_name);
            fileNameTextView = itemView.findViewById(R.id.file_name);
            image = itemView.findViewById(R.id.imageView);
            b = itemView.findViewById(R.id.edit_button);

            //TODO get row number (primary id) and pass it through the click listener. Position will not work when DELETE runs
            rowID = mID;


            this.listener = listener;
            ///this.buttonListener = buttonListener;

            image.setOnClickListener(this);
            b.setOnClickListener(this);
        }


        /*
        Seems to not work at times clicking on the image- like click not picked up/dropped???
         */
        @Override
        public void onClick(View view) {
            switch (view.getId()){
                case R.id.imageView:
                    if (listener != null){
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION){
                            listener.onItemClick(position);
                        }
                    }
                    break;
                case R.id.edit_button:
                    if (listener != null){
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION){
                            listener.onButtonClick(position);
                        }
                    }
                    break;
                default:
                    break;
            }
        }

    }

    // Use interface- implemented in MainActivity
    public interface RecyclerViewClickListener{

        void onItemClick(long pos);
        void onButtonClick(int pos);
    }

}
