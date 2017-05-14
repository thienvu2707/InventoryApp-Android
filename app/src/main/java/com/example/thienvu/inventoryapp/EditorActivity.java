package com.example.thienvu.inventoryapp;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NavUtils;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.thienvu.inventoryapp.data.InventoryContact;
import com.example.thienvu.inventoryapp.data.InventoryDbHelper;
import com.example.thienvu.inventoryapp.data.InventoryItem;

public class EditorActivity extends AppCompatActivity {

    private static final String LOG_TAG = EditorActivity.class.getName();
    private static final int PERMISSION_REQUEST_EXTERNAL_STORAGE = 1;
    private static final int CHOOSE_IMAGE_REQUEST = 0;
    private static final int DEFAULT_VALUE = 0;
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
    private Uri actualUri;
    private Boolean infoChanged = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);

        dbHelper = new InventoryDbHelper(this);

        if (getSupportActionBar() != null) {
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

        currentItemId = getIntent().getLongExtra("itemId", DEFAULT_VALUE);

        if (currentItemId == 0) {
            setTitle(getString(R.string.editor_activity_title_new_item));
        } else {
            setTitle(getString(R.string.editor_activity_title_edit_item));
            addValueToEditItem(currentItemId);
        }

        decreaseQuantity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //need function to subtract quantity by 1
                subtractQuantity();
                infoChanged = true;
            }
        });

        increaseQuantity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //need function to add quantity by 1
                addQuantity();
                infoChanged = true;
            }
        });

        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tryToOpenImageSelection();
                infoChanged = true;
            }
        });
    }

    @Override
    public void onBackPressed() {
        if (!infoChanged) {
            super.onBackPressed();
            return;
        }
        DialogInterface.OnClickListener discardButton = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        };
        //show dialog that if there unsaved changes
        showUnsavedChangeDialog(discardButton);
    }

    private void showUnsavedChangeDialog(DialogInterface.OnClickListener discardButton) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.unsaved_dialog_message);
        builder.setPositiveButton(R.string.unsaved_accept, discardButton);
        builder.setNegativeButton(R.string.unsaved_cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void subtractQuantity() {
        String previousStringValue = quantityEdit.getText().toString();
        int previous;
        if (previousStringValue.isEmpty()) {
            return;
        } else if (previousStringValue.equals("0")) {
            return;
        } else {
            previous = Integer.parseInt(previousStringValue);
            quantityEdit.setText(String.valueOf(previous - 1));
        }
    }

    private void addQuantity() {
        String previousStringValue = quantityEdit.getText().toString();
        int previous;
        if (previousStringValue.isEmpty()) {
            previous = DEFAULT_VALUE;
        } else {
            previous = Integer.parseInt(previousStringValue);
        }
        quantityEdit.setText(String.valueOf(previous + 1));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_editor, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        if (currentItemId == 0) {
            MenuItem orderMenuItem = menu.findItem(R.id.action_order);
            MenuItem deleteItem = menu.findItem(R.id.delete_item);
            MenuItem deleteAll = menu.findItem(R.id.delete_all_item);
            deleteItem.setVisible(false);
            deleteAll.setVisible(false);
            orderMenuItem.setVisible(false);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.action_save:
                if (!addItemToDatabase()) {
                    return true;
                }
                Toast.makeText(this, getString(R.string.save_message), Toast.LENGTH_SHORT).show();
                finish();
                return true;

            case android.R.id.home:
                if (!infoChanged) {
                    NavUtils.navigateUpFromSameTask(this);
                    return true;
                }

                DialogInterface.OnClickListener discardButton = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        NavUtils.navigateUpFromSameTask(EditorActivity.this);
                    }
                };
                showUnsavedChangeDialog(discardButton);
                return true;

            case R.id.action_order:
                showOrderConfirmationDialog();
                return true;

            case R.id.delete_item:
                showDeleteDialog(currentItemId);
                return true;
            case R.id.delete_all_item:
                showDeleteDialog(DEFAULT_VALUE);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private boolean addItemToDatabase() {
        boolean addAll = true;
        if (!checkIfValueIsSet(productNameEdit, getString(R.string.check_name))) {
            addAll = false;
        }
        if (!checkIfValueIsSet(priceEdit, getString(R.string.check_price))) {
            addAll = false;
        }
        if (!checkIfValueIsSet(quantityEdit, getString(R.string.check_quantity))) {
            addAll = false;
        }
        if (!checkIfValueIsSet(supplierNameEdit, getString(R.string.check_sup_name))) {
            addAll = false;
        }
        if (!checkIfValueIsSet(supplierPhoneEdit, getString(R.string.check_sub_phone))) {
            addAll = false;
        }
        if (!checkIfValueIsSet(supplierEmailEdit, getString(R.string.check_sub_email))) {
            addAll = false;
        }
        if (actualUri == null && currentItemId == 0) {
            addAll = false;
            imageButton.setError(getString(R.string.missing_image));
        }
        if (!addAll) {
            return false;
        }

        if (currentItemId == 0) {
            InventoryItem item = new InventoryItem(
                    productNameEdit.getText().toString().trim(),
                    priceEdit.getText().toString().trim(),
                    Integer.parseInt(quantityEdit.getText().toString().trim()),
                    supplierNameEdit.getText().toString().trim(),
                    supplierPhoneEdit.getText().toString().trim(),
                    supplierEmailEdit.getText().toString().trim(),
                    actualUri.toString()
            );
            dbHelper.insertItem(item);
        } else {
            int quantity = Integer.parseInt(quantityEdit.getText().toString().trim());
            dbHelper.updateItem(currentItemId, quantity);
        }
        return true;
    }

    private boolean checkIfValueIsSet(EditText text, String description) {
        if (TextUtils.isEmpty(text.getText())) {
            text.setError(R.string.missing_product + description);
            return false;
        } else {
            text.setError(null);
            return true;
        }
    }

    private void addValueToEditItem(long itemId) {
        Cursor cursor = dbHelper.readItem(itemId);
        cursor.moveToFirst();
        productNameEdit.setText(cursor.getString(cursor.getColumnIndex(InventoryContact.InventoryEntry.COLUMN_NAME)));
        priceEdit.setText(cursor.getString(cursor.getColumnIndex(InventoryContact.InventoryEntry.COLUMN_PRICE)));
        quantityEdit.setText(cursor.getString(cursor.getColumnIndex(InventoryContact.InventoryEntry.COLUMN_QUANTITY)));
        supplierNameEdit.setText(cursor.getString(cursor.getColumnIndex(InventoryContact.InventoryEntry.COLUMN_SUPPLIER_NAME)));
        supplierPhoneEdit.setText(cursor.getString(cursor.getColumnIndex(InventoryContact.InventoryEntry.COLUMN_SUPPLIER_PHONE)));
        supplierEmailEdit.setText(cursor.getString(cursor.getColumnIndex(InventoryContact.InventoryEntry.COLUMN_SUPPLIER_EMAIL)));
        imageView.setImageURI(Uri.parse(cursor.getString(cursor.getColumnIndex(InventoryContact.InventoryEntry.COLUMN_IMAGE))));
        productNameEdit.setEnabled(false);
        priceEdit.setEnabled(false);
        supplierNameEdit.setEnabled(false);
        supplierPhoneEdit.setEnabled(false);
        supplierEmailEdit.setEnabled(false);
        imageButton.setEnabled(false);
    }

    private void showOrderConfirmationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.order_message);
        builder.setPositiveButton(R.string.phone, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(Intent.ACTION_DIAL);
                intent.setData(Uri.parse("tel:" + supplierPhoneEdit.getText().toString().trim()));
                startActivity(intent);
            }
        });

        builder.setNegativeButton(R.string.email, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(Intent.ACTION_SENDTO);
                intent.setType("text/plain");
                intent.setData(Uri.parse("mailto:" + supplierEmailEdit.getText().toString().trim()));
                intent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.send_email_title));
                String bodyMessage = getString(R.string.send_email_body_message) + productNameEdit.getText().toString().trim();
                intent.putExtra(Intent.EXTRA_TEXT, bodyMessage);
                startActivity(intent);
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private int deleteAllRow() {
        SQLiteDatabase database = dbHelper.getWritableDatabase();
        return database.delete(InventoryContact.InventoryEntry.TABLE_NAME, null, null);
    }

    private int deleteSingleItem(long itemId) {
        SQLiteDatabase database = dbHelper.getWritableDatabase();
        String selection = InventoryContact.InventoryEntry._ID + "=?";
        String[] selectionArgs = new String[]{String.valueOf(itemId)};
        int rowDeleted = database.delete(InventoryContact.InventoryEntry.TABLE_NAME, selection, selectionArgs);

        return rowDeleted;
    }

    private void showDeleteDialog(final long itemId) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.delete_message);
        builder.setPositiveButton(R.string.delete_accept, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (itemId == 0) {
                    deleteAllRow();
                } else {
                    deleteSingleItem(itemId);
                }
                finish();
            }
        });

        builder.setNegativeButton(R.string.delete_cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    public void tryToOpenImageSelection() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, PERMISSION_REQUEST_EXTERNAL_STORAGE);
            return;
        }
        openImageSelection();
    }

    private void openImageSelection() {
        Intent intent;
        if (Build.VERSION.SDK_INT < 19) {
            intent = new Intent(Intent.ACTION_GET_CONTENT);
        } else {
            intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
            intent.addCategory(Intent.CATEGORY_OPENABLE);
        }

        intent.setType("image/*");
        startActivityForResult(Intent.createChooser(intent, getString(R.string.image_selected)), CHOOSE_IMAGE_REQUEST);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_EXTERNAL_STORAGE: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    openImageSelection();
                }
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CHOOSE_IMAGE_REQUEST && resultCode == Activity.RESULT_OK) {
            if (data != null) {
                actualUri = data.getData();
                imageView.setImageURI(actualUri);
                imageView.invalidate();
            }
        }
    }
}
