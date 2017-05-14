package com.example.thienvu.inventoryapp;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.thienvu.inventoryapp.data.InventoryContact;

/**
 * Created by thienvu on 5/14/17.
 */

public class InventoryCursorAdapter extends CursorAdapter {

    private final MainActivity mainActivity;

    public InventoryCursorAdapter(MainActivity context, Cursor cursor)
    {
        super(context, cursor, 0);
        this.mainActivity = context;
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.list_items, parent, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        TextView productView = (TextView) view.findViewById(R.id.product_name);
        TextView priceView = (TextView) view.findViewById(R.id.price);
        TextView quantityView = (TextView) view.findViewById(R.id.quantity);
        ImageView imageView = (ImageView) view.findViewById(R.id.image_view);
        ImageView saleImageView = (ImageView) view.findViewById(R.id.sale);

        String productName = cursor.getString(cursor.getColumnIndex(InventoryContact.InventoryEntry.COLUMN_NAME));
        String price = cursor.getString(cursor.getColumnIndex(InventoryContact.InventoryEntry.COLUMN_PRICE));
        final int quantity = cursor.getInt(cursor.getColumnIndex(InventoryContact.InventoryEntry.COLUMN_QUANTITY));
        imageView.setImageURI(Uri.parse(cursor.getString(cursor.getColumnIndex(InventoryContact.InventoryEntry.COLUMN_IMAGE))));

        productView.setText(productName);
        priceView.setText(price);
        quantityView.setText(String.valueOf(quantity));

        final long id = cursor.getLong(cursor.getColumnIndex(InventoryContact.InventoryEntry._ID));

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mainActivity.clickOnItem(id);
            }
        });

        saleImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mainActivity.clickOnSale(id, quantity);
            }
        });
    }
}
