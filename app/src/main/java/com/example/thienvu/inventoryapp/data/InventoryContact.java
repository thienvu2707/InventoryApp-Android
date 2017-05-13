package com.example.thienvu.inventoryapp.data;

import android.provider.BaseColumns;

/**
 * Created by thienvu on 5/13/17.
 */

public class InventoryContact{

    public InventoryContact(){

    }

    public static final class InventoryEntry implements BaseColumns
    {
        public static final String TABLE_NAME = "stock";

        public static final String _ID = BaseColumns._ID;
        public static final String COLUMN_NAME = "name";
        public static final String COLUMN_PRICE = "price";
        public static final String COLUMN_QUANTITY = "quantity";
        public static final String COLUMN_SUPPLIER_NAME = "supplier_name";
        public static final String COLUMN_SUPPLIER_PHONE = "supplier_phone";
        public static final String COLUMN_SUPPLIER_EMAIL = "supplier_email";
        public static final String COLUMN_IMAGE = "image";

        public static final String CREATE_TABLE_INVENTORY = "CREATE TABLE " +
                InventoryEntry.TABLE_NAME + " (" +
                InventoryEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                InventoryEntry.COLUMN_NAME + " TEXT NOT NULL, " +
                InventoryEntry.COLUMN_PRICE + " TEXT NOT NULL, " +
                InventoryEntry.COLUMN_QUANTITY + " INTEGER NOT NULL DEFAULT 0, " +
                InventoryEntry.COLUMN_SUPPLIER_NAME + " TEXT NOT NULL, " +
                InventoryEntry.COLUMN_SUPPLIER_PHONE + " TEXT NOT NULL, " +
                InventoryEntry.COLUMN_SUPPLIER_EMAIL + " TEXT NOT NULL, " +
                InventoryEntry.COLUMN_IMAGE + " TEXT NOT NULL);";
    }
}
