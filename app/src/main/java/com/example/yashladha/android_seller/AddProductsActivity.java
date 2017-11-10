package com.example.yashladha.android_seller;

import android.Manifest;
import android.app.AlertDialog;
import android.content.ClipData;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.yashladha.android_seller.classes.FileUriHelper;
import com.example.yashladha.android_seller.helper.BaseUrlConfig;
import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.koushikdutta.ion.builder.Builders;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

public class AddProductsActivity extends AppCompatActivity {

    private ImageView ivAdd;
    private TextView tvAddPhoto, tvProductName, tvProDes, tvOriginalPrice, tvDiscount, tvCategory;
    private EditText etProductName, etProDes, etOriginalPrice, etDiscount, etCategory;
    private Button btDone;
    private LinearLayout sv1;
    private File wallpaperDirectory;
    private ToggleButton tbOnSale;
    private String productName = "", proDes = "", originalPrice = "", discount = "", category = "";
    private boolean sale, image;
    private SharedPreferences myPrefs;
    private int noOfImages;
    private Uri uriContent;
    private String plan;
    private String UID;
    private RequestQueue rq;
    private Context context;
    private ArrayList<String> dataUri;
    private static SimpleDateFormat formatter = new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss", Locale.getDefault());
    private static Date now = new Date();
    private static final String IMAGE_DIRECTORY = "/hatsphere" + formatter.format(now);
    private int GALLERY = 1, CAMERA = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_products);
        noOfImages = 0;
        dataUri = new ArrayList<>();
        context = this.getBaseContext();
        btDone = findViewById(R.id.btDone);
        ivAdd = findViewById(R.id.ivAdd);
        tvAddPhoto = findViewById(R.id.tvAddPhoto);
        tvProductName = findViewById(R.id.tvProductName);
        tvProDes = findViewById(R.id.tvProDes);
        tvOriginalPrice = findViewById(R.id.tvOriginalPrice);
        tvDiscount = findViewById(R.id.tvDiscount);
        tvCategory = findViewById(R.id.tvCategory);
        etProductName = findViewById(R.id.etProductName);
        etProDes = findViewById(R.id.etProDes);
        etOriginalPrice = findViewById(R.id.etOriginalPrice);
        etDiscount = findViewById(R.id.etDiscount);
        etCategory = findViewById(R.id.etCategory);
        sv1 = findViewById(R.id.sv1);
        tbOnSale = findViewById(R.id.tbOnSale);
        rq = Volley.newRequestQueue(AddProductsActivity.this);

        myPrefs = getSharedPreferences("myprfs", MODE_PRIVATE);
        UID = myPrefs.getString("UID", "");
        plan = myPrefs.getString("Plan", "");
        etProductName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                productName = etProductName.getText().toString();
            }
        });

        etProDes.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                proDes = etProDes.getText().toString();
            }
        });
        etOriginalPrice.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                originalPrice = etOriginalPrice.getText().toString();
            }
        });
        etDiscount.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                discount = etDiscount.getText().toString();
            }
        });
        etCategory.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                category = etCategory.getText().toString();
            }
        });
        tbOnSale.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sale = Objects.equals(tbOnSale.getText().toString(), "Yes");
            }
        });
        ivAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
        tvAddPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPictureDialog();

            }
        });
        btDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                JSONObject obj = new JSONObject();
                if (!productName.equals("") && !originalPrice.equals("") && !discount.equals("") && !proDes.equals("") && !category.equals("")) {
                    try {
                        obj.put("pName", productName);
                        obj.put("pPrice", Integer.toString(Integer.parseInt(originalPrice) - Integer.parseInt(discount)));
                        obj.put("pDescription", proDes);
                        obj.put("pClass", category);
                        obj.put("pSale", sale);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                            Request.Method.POST,
                            "http://10.0.2.2:3000/product/send/" + UID,
                            obj,
                            new Response.Listener<JSONObject>() {
                                @Override
                                public void onResponse(JSONObject response) {
                                    try {
                                        if (response.get("response").toString().equals("200")) {
                                            Toast.makeText(AddProductsActivity.this, "Your Product has been added",
                                                    Toast.LENGTH_LONG).show();
                                            Builders.Any.B builder =  Ion.with(context)
                                                    .load(BaseUrlConfig.getBaseURL() + "product/send/image/" + UID + "/" + productName);
                                            for (String item : dataUri) {
                                                builder.setMultipartFile(UID, new File(item));
                                            }
                                            builder.asJsonObject()
                                                    .setCallback(new FutureCallback<JsonObject>() {
                                                        @Override
                                                        public void onCompleted(Exception e, JsonObject result) {
                                                            if (result != null) {
                                                                Log.d("onCompleted: ", result.toString());
                                                            } else {
                                                                e.printStackTrace();
                                                            }
                                                        }
                                                    });
                                            Intent intent = new Intent(AddProductsActivity.this, HomePageActivity.class);
                                            startActivity(intent);
                                        } else {
                                            Toast.makeText(AddProductsActivity.this, response.get("Something is wrong").toString(), Toast.LENGTH_SHORT).show();
                                        }
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                            },
                            new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError error) {
                                    Log.e("error", error.toString());
                                }
                            });

                    rq.add(jsonObjectRequest);

                }
            }
        });
    }

    private void showPictureDialog() {
        AlertDialog.Builder pictureDialog = new AlertDialog.Builder(this);
        pictureDialog.setTitle("Select Action");
        String[] pictureDialogItems = {
                "Select photo from gallery",
                "Capture photo from camera"};
        pictureDialog.setItems(pictureDialogItems,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0:
                                choosePhotoFromGallery();
                                break;
                            case 1:
                                takePhotoFromCamera();
                                break;
                        }
                    }
                });
        pictureDialog.show();
    }

    public void choosePhotoFromGallery() {
        Intent galleryIntent = new Intent();
        galleryIntent.setType("image/*");
        galleryIntent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(galleryIntent, "Select product images"), GALLERY);
    }

    private void takePhotoFromCamera() {
        Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, CAMERA);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_CANCELED) {
            return;
        }
        if (requestCode == GALLERY) {
            if (data != null) {
                if (data.getClipData() != null) {
                    ClipData contentURI = data.getClipData();
                    Log.d("URI", contentURI.toString());
                    int items = contentURI.getItemCount();
                    for (int i = 0; i < items; ++i) {
                        Uri itemUri = contentURI.getItemAt(i).getUri();
                        Log.d("ItemPath", getFileUri(itemUri));
                        dataUri.add(getFileUri(itemUri));
                    }
                } else if (data.getData() != null) {
                    Uri contentUri = data.getData();
                    try {
                        dataUri.add(getFileUri(contentUri));
                        Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), contentUri);
                        String path = saveImage(bitmap);
                        Toast.makeText(AddProductsActivity.this, "Image Saved!", Toast.LENGTH_SHORT).show();
                        image = true;
                        ImageView image = new ImageView(AddProductsActivity.this);
                        image.setImageBitmap(bitmap);
                        sv1.addView(image);

                    } catch (IOException e) {
                        e.printStackTrace();
                        Toast.makeText(AddProductsActivity.this, "Failed!", Toast.LENGTH_SHORT).show();
                    }
                }
            }

        } else if (requestCode == CAMERA) {
            Bitmap thumbnail = (Bitmap) data.getExtras().get("data");
            image = true;
            saveImage(thumbnail);
            ImageView image = new ImageView(AddProductsActivity.this);
            image.setImageBitmap(thumbnail);
            sv1.addView(image);
        }
    }

    private String getFileUri(Uri itemUri) {
        String fileUri = FileUriHelper.Companion.getFileUri(itemUri, context);
        return fileUri;
    }

    public String saveImage(Bitmap myBitmap) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        myBitmap.compress(Bitmap.CompressFormat.JPEG, 90, bytes);
        File wallpaperDirectory = new File(
                Environment.getExternalStorageDirectory() + IMAGE_DIRECTORY);
        // have the object build the directory structure, if needed.
        if (!wallpaperDirectory.exists()) {
            wallpaperDirectory.mkdirs();
        }

        try {
            if (isStoragePermissionGranted()) {
                if (wallpaperDirectory.createNewFile()) {
                    Log.d("TAG", "File Saved::--->" + wallpaperDirectory.getAbsolutePath());
                    return wallpaperDirectory.getAbsolutePath();
                }
            }
        } catch (IOException e1) {
            e1.printStackTrace();
        }
        return "";
    }

    public boolean isStoragePermissionGranted() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED) {
                Log.v("Tag", "Permission is granted");
                return true;
            } else {

                Log.v("Tag", "Permission is revoked");
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                return false;
            }
        } else { //permission is automatically granted on sdk<23 upon installation
            Log.v("Tag", "Permission is granted");
            return true;
        }
    }

    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            Log.v("Tag", "Permission: " + permissions[0] + "was " + grantResults[0]);
            //resume tasks needing this permission
        }
    }

    public Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }
}
