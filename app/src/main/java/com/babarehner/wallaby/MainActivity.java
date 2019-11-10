package com.babarehner.wallaby;

import android.content.ContentUris;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.CursorLoader;
import androidx.loader.content.Loader;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.babarehner.wallaby.data.WallabyContract;

import static com.babarehner.wallaby.data.WallabyContract.WallabyTableConstants.WALLABY_URI;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>,
        WallabyCursorAdapter.RecyclerViewClickListener {

    public static final String TAG = "MainActivity";

    RecyclerView mRecyclerView;
    private static final int WALLABY_LOADER_ID = 1;
    WallabyCursorAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // bind view
        mRecyclerView = findViewById(R.id.recycler_view);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        // set layout manager
        mRecyclerView.setLayoutManager(mLayoutManager);
        // set default animator
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());

        mAdapter = new WallabyCursorAdapter(this );

        mRecyclerView.setAdapter(mAdapter);


        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent( MainActivity.this, AddEditWallabyActivity.class);
                startActivity(intent);
            }
        });

        LoaderManager.getInstance(MainActivity.this).initLoader(WALLABY_LOADER_ID, null, MainActivity.this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @NonNull
    @Override
    public Loader<Cursor> onCreateLoader(int id, @Nullable Bundle args) {
        if (id == WALLABY_LOADER_ID) {
            Uri wallabyUri = WALLABY_URI;

            String[] projection = {WallabyContract.WallabyTableConstants.C_CARD_N,
                WallabyContract.WallabyTableConstants.C_IMAGE_FN};

            String selection = null;
            String[] selectionArgs = {};
            String sortOrder = null;

            return new CursorLoader(
                    getApplicationContext(),
                    wallabyUri,
                    projection,
                    selection,
                    selectionArgs,
                    sortOrder);
        }
        return null;
    }

    @Override
    public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor data) {
        mAdapter.swapCursor(data);

    }

    @Override
    public void onLoaderReset(@NonNull Loader<Cursor> loader) {
        mAdapter.swapCursor(null);

    }

    @Override
    public void onItemClick(int pos) {
        //Toast.makeText(v.getContext(), "Position " + pos, Toast.LENGTH_LONG).show();
        // 'pos' is the position of the view in the parent. For a ListView it is the row number
        // for a CursorAdapter the long id returns the row of the table
        // had to add 1 to pos to get correct record to show. No Zero in id????
        long id = pos + 1;
        Log.d(TAG, "id is : " + id);
        Intent intent = new Intent(this, ScanPictureActivity.class);
        Uri currentRecyclerUri = ContentUris.withAppendedId(WALLABY_URI, id);
        intent.setData(currentRecyclerUri);
        //Toast.makeText(v.getContext(), "id: " + id, Toast.LENGTH_LONG).show();
        //Toast.makeText(v.getContext(), "Uri: " + currentRecyclerUri, Toast.LENGTH_LONG).show();
        startActivity(intent);
    }


    @Override
    public void onButtonClick(int pos){
        long id = pos + 1;
        Log.d(TAG, "id is : " + id);
        Intent intent = new Intent(this, AddEditWallabyActivity.class);
        Uri currentRecyclerUri = ContentUris.withAppendedId(WALLABY_URI, id);
        intent.setData(currentRecyclerUri);
        //Toast.makeText(v.getContext(), "id: " + id, Toast.LENGTH_LONG).show();
        //Toast.makeText(v.getContext(), "Uri: " + currentRecyclerUri, Toast.LENGTH_LONG).show();
        startActivity(intent);
    }

}
