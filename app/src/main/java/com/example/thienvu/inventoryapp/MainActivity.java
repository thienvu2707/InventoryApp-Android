package com.example.thienvu.inventoryapp;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ListView;
import android.widget.Toast;

import com.example.thienvu.inventoryapp.data.InventoryDbHelper;
import com.example.thienvu.inventoryapp.data.InventoryItem;

public class MainActivity extends AppCompatActivity {

    private final static String LOG_TAG = MainActivity.class.getName();
    private InventoryDbHelper dbHelper;
    private int lastVisibleItem = 0;
    private InventoryCursorAdapter adapter;
    private final int GALAXY_QUANTITY = 50;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        dbHelper = new InventoryDbHelper(this);

        final FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, EditorActivity.class);
                startActivity(intent);
            }
        });

        ListView listView = (ListView) findViewById(R.id.list);
        View emptyView = findViewById(R.id.empty_view);
        listView.setEmptyView(emptyView);

        Cursor cursor = dbHelper.readInventoryStock();

        adapter = new InventoryCursorAdapter(this, cursor);
        listView.setAdapter(adapter);
        listView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                if (scrollState == 0)
                    return;
                int currentFirstItem = view.getFirstVisiblePosition();
                if (currentFirstItem > lastVisibleItem) {
                    fab.show();
                } else if (currentFirstItem < lastVisibleItem) {
                    fab.hide();
                }
                lastVisibleItem = currentFirstItem;
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        adapter.swapCursor(dbHelper.readInventoryStock());
    }

    public void clickOnItem(long id) {
        Intent intent = new Intent(this, EditorActivity.class);
        intent.putExtra(getString(R.string.item_id), id);
        startActivity(intent);
    }

    public void clickOnSale(long id, int quantity) {
        dbHelper.sellOneItem(id, quantity);
        adapter.swapCursor(dbHelper.readInventoryStock());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_add_dummy_data:
                //add dummy data
                addDummyData();
                adapter.swapCursor(dbHelper.readInventoryStock());
                Toast.makeText(this, getString(R.string.add_dummy_data_msg), Toast.LENGTH_SHORT).show();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void addDummyData() {
        InventoryItem galaxyS8 = new InventoryItem(
                getString(R.string.galaxyS8_name),
                getString(R.string.galaxyS8_price),
                GALAXY_QUANTITY,
                getString(R.string.galaxyS8_supplier),
                getString(R.string.galaxyS8_supplier_phone),
                getString(R.string.galaxyS8_supplier_email),
                getString(R.string.galaxyS8_image)
        );
        dbHelper.insertItem(galaxyS8);
    }
}
