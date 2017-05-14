package com.example.thienvu.inventoryapp;

import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.example.thienvu.inventoryapp.data.InventoryDbHelper;

public class EditorActivity extends AppCompatActivity {

    private static final String LOG_TAG = EditorActivity.class.getName();
    private static final int PERMISSION_REQUEST_EXTERNAL_STORAGE = 1;
    private static final int CHOOSE_IMAGE_REQUEST = 0;
    private InventoryDbHelper dbHelper;
    private EditText productNameEdit;
    private EditText priceEdit;
    private EditText quantityEdit;
    private EditText supplierNameEdit;
    private EditText supplierPhoneEdit;
    private EditText supplierEmailEdit;
    private ImageButton decreaseQuantity;
    private ImageButton increaseQuantity;
    private Button imageButton;
    private ImageView imageView;
    private long currentItemId;
    private Uri uri;
    private Boolean infoChanged = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);

        dbHelper = new InventoryDbHelper(this);

        if (getSupportActionBar() != null)
        {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        productNameEdit = (EditText) findViewById(R.id.product_name_edit_text);
        priceEdit = (EditText) findViewById(R.id.price_edit_text);
        quantityEdit = (EditText) findViewById(R.id.quantity_edit_text);
        supplierNameEdit = (EditText) findViewById(R.id.supplier_name_edit);
        supplierPhoneEdit = (EditText) findViewById(R.id.phone_edit);
        supplierEmailEdit = (EditText) findViewById(R.id.email_edit);
        decreaseQuantity = (ImageButton) findViewById(R.id.quantity_decrease);
        increaseQuantity = (ImageButton) findViewById(R.id.quantity_increase);
        imageButton = (Button) findViewById(R.id.image_select);
        imageView = (ImageView) findViewById(R.id.image_view);

        currentItemId = getIntent().getLongExtra("itemId", 0);
    }
}
