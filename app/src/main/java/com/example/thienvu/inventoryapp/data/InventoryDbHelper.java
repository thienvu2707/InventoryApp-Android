package com.example.thienvu.inventoryapp.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by thienvu on 5/13/17.
 */

public class InventoryDbHelper extends SQLiteOpenHelper {

    public final static String DATABASE_NAME = "inventory.db";
    public final static int DATABASE_VERSION = 1;
    public final static String LOG_TAG = InventoryDbHelper.class.getName();

    public InventoryDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(InventoryContact.InventoryEntry.CREATE_TABLE_INVENTORY);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public void insertItem(InventoryItem item) {
        SQLiteDatabase database = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(InventoryContact.InventoryEntry.COLUMN_NAME, item.getProductName());
        values.put(InventoryContact.InventoryEntry.COLUMN_PRICE, item.getPrice());
        values.put(InventoryContact.InventoryEntry.COLUMN_QUANTITY, item.getQuantity());
        values.put(InventoryContact.InventoryEntry.COLUMN_SUPPLIER_NAME, item.getSupplierName());
        values.put(InventoryContact.InventoryEntry.COLUMN_SUPPLIER_PHONE, item.getSupplierPhone());
        values.put(InventoryContact.InventoryEntry.COLUMN_SUPPLIER_EMAIL, item.getSupplierEmail());
        values.put(InventoryContact.InventoryEntry.COLUMN_IMAGE, item.getImage());
        long id = database.insert(InventoryContact.InventoryEntry.TABLE_NAME, null, values);
    }

    public Cursor readInventoryStock() {
        SQLiteDatabase database = getReadableDatabase();
        String[] projection = {
                InventoryContact.InventoryEntry._ID,
                InventoryContact.InventoryEntry.COLUMN_NAME,
                InventoryContact.InventoryEntry.COLUMN_PRICE,
                InventoryContact.InventoryEntry.COLUMN_QUANTITY,
                InventoryContact.InventoryEntry.COLUMN_SUPPLIER_NAME,
                InventoryContact.InventoryEntry.COLUMN_SUPPLIER_PHONE,
                InventoryContact.InventoryEntry.COLUMN_SUPPLIER_EMAIL,
                InventoryContact.InventoryEntry.COLUMN_IMAGE
        };

        Cursor cursor = database.query(
                InventoryContact.InventoryEntry.TABLE_NAME,
                projection,
                null,
                null,
                null,
                null,
                null
        );
        return cursor;
    }

    public Cursor readItem (long itemId)
    {
        SQLiteDatabase db = getReadableDatabase();
        String[] projection = {
                InventoryContact.InventoryEntry._ID,
                InventoryContact.InventoryEntry.COLUMN_NAME,
                InventoryContact.InventoryEntry.COLUMN_PRICE,
                InventoryContact.InventoryEntry.COLUMN_QUANTITY,
                InventoryContact.InventoryEntry.COLUMN_SUPPLIER_NAME,
                InventoryContact.InventoryEntry.COLUMN_SUPPLIER_PHONE,
                InventoryContact.InventoryEntry.COLUMN_SUPPLIER_EMAIL,
                InventoryContact.InventoryEntry.COLUMN_IMAGE
        };

        String selection = InventoryContact.InventoryEntry._ID + "=?";
        String[] selectionArgs = new String[] {String.valueOf(itemId)};

        Cursor cursor = db.query(
                InventoryContact.InventoryEntry.TABLE_NAME,
                projection,
                selection,
                selectionArgs,
                null,
                null,
                null
        );
        return cursor;
    }

    public void updateItem(long currentItemId, int quantity)
    {
        SQLiteDatabase database = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(InventoryContact.InventoryEntry.COLUMN_QUANTITY, quantity);

        String selection = InventoryContact.InventoryEntry._ID + "=?";
        String[] selectionArgs = new String[] {String.valueOf(currentItemId)};
        database.update(InventoryContact.InventoryEntry.TABLE_NAME, values, selection, selectionArgs);
    }

    public void sellOneItem(long itemId, int quantity)
    {
        SQLiteDatabase database = getWritableDatabase();
        int newQuantity = 0;
        if (quantity > 0 )
            newQuantity = quantity - 1;
        ContentValues values = new ContentValues();
        values.put(InventoryContact.InventoryEntry.COLUMN_QUANTITY, newQuantity);

        String selection = InventoryContact.InventoryEntry._ID + "=?";
        String[] selectionArgs = new String[] {String.valueOf(itemId)};

        database.update(InventoryContact.InventoryEntry.TABLE_NAME, values, selection, selectionArgs);
    }
}
