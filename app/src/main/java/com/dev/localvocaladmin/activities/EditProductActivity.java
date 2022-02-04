package com.dev.localvocaladmin.activities;

import android.Manifest;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.dev.localvocaladmin.Constants;
import com.dev.localvocaladmin.R;
import com.facebook.shimmer.Shimmer;
import com.facebook.shimmer.ShimmerDrawable;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.annotations.Nullable;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;

public class EditProductActivity extends AppCompatActivity {

    //permission constants
    private static final int CAMERA_REQUEST_CODE = 100;
    private static final int STORAGE_REQUEST_CODE = 101;
    //image pick constants
    private static final int IMAGE_PICK_GALLERY_CODE = 100;
    private static final int IMAGE_PICK_CAMERA_CODE = 400;

    //permission arrays
    private String[] cameraPermissions;
    private String[] storagePermissions;

    //UI Views
    private CardView AddImage;
    private ImageView productImage;
    private TextView productCategory, disc;
    private EditText productName, productDesc, productPrice, productQuantity, productDiscount;
    private Switch discount;
    private RelativeLayout add;
    private ProgressBar pd;
    private TextView text;
    private BottomSheetDialog bottomSheetDialog1;
    //Firebase
    private FirebaseAuth firebaseAuth;
    //Extras
    private boolean discountAvailable = false;
    private Uri image_uri;
    private String shopId, productId, pDiscount, pDiscountN, timestamp, pImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.black_trans80));
        setContentView(R.layout.activity_edit_product);

        //Getting data from Intent
        shopId = getIntent().getStringExtra("shopId");
        productId = getIntent().getStringExtra("productId");
        //init Permissions arrays
        cameraPermissions = new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};
        storagePermissions = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};
        //init UI Views
        AddImage = findViewById(R.id.productImageIv);
        productImage = findViewById(R.id.productimageIv);
        productName = findViewById(R.id.productName);
        productDesc = findViewById(R.id.productDesc);
        productCategory = findViewById(R.id.productCategory);
        productPrice = findViewById(R.id.productPrice);
        productQuantity = findViewById(R.id.productQuantityTv);
        discount = findViewById(R.id.open);
        productDiscount = findViewById(R.id.productDiscount);
        add = findViewById(R.id.ratingBtn);
        text = findViewById(R.id.text);
        pd = findViewById(R.id.progressBar);
        //init Firebase
        firebaseAuth = FirebaseAuth.getInstance();

        loadProductDetails();

        discount.setOnCheckedChangeListener((compoundButton, b) -> {
            if (b) {
                productDiscount.setVisibility(View.VISIBLE);
            } else {
                productDiscount.setVisibility(View.GONE);
            }
        });

        AddImage.setOnClickListener(v -> {
            bottomSheetDialog1 = new BottomSheetDialog(EditProductActivity.this, R.style.BottomSheetTheme);
            View sheetView = LayoutInflater.from(EditProductActivity.this).inflate(R.layout.bottom_sheet_camera, v.findViewById(R.id.bottom_sheet_camera_1));
            sheetView.findViewById(R.id.gallery).setOnClickListener(view -> {
                if (!checkStoragePermission()) {
                    requestStoragePermission();
                } else {
                    pickFromGallery();
                }
                bottomSheetDialog1.dismiss();
            });
            sheetView.findViewById(R.id.camera).setOnClickListener(view -> {
                if (!checkCameraPermission()) {
                    requestCameraPermission();
                } else {
                    pickFromCamera();
                }
                bottomSheetDialog1.dismiss();
            });
            bottomSheetDialog1.setContentView(sheetView);
            bottomSheetDialog1.show();
        });

        add.setOnClickListener(v -> {
            String pName = productName.getText().toString().trim();
            String pDesc = productDesc.getText().toString().trim();
            String pCat = productCategory.getText().toString().trim();
            String pQty = productQuantity.getText().toString().trim();
            String pPrice = productPrice.getText().toString().trim();
            discountAvailable = discount.isChecked();

            if (TextUtils.isEmpty(pName) || TextUtils.isEmpty(pDesc) || TextUtils.isEmpty(pCat) || TextUtils.isEmpty(pQty) || TextUtils.isEmpty(pPrice)) {
                Toast.makeText(EditProductActivity.this, "Please enter all details.", Toast.LENGTH_SHORT).show();
            } else if (discountAvailable) {
                pDiscount = productDiscount.getText().toString().trim();

                if (TextUtils.isEmpty(pDiscount)) {
                    Toast.makeText(EditProductActivity.this, "Discount details are required.", Toast.LENGTH_SHORT).show();
                } else {
                    float p = Float.parseFloat(pPrice);
                    float d = Float.parseFloat(pDiscount);
                    pd.setVisibility(View.VISIBLE);
                    text.setVisibility(View.INVISIBLE);
                    String filePathName = "Product_Images/" + "" + timestamp;
                    if (image_uri == null) {
                        Float price = (((p - d) / p) * 100);
                        int pp = Math.round(price);

                        HashMap<String, Object> hashMap = new HashMap<>();
                        hashMap.put("productName", pName);
                        hashMap.put("productDesc", pDesc);
                        hashMap.put("productCategory", pCat);
                        hashMap.put("productQuantity", pQty);
                        hashMap.put("productPrice", pPrice);
                        hashMap.put("productImage", pImage);
                        hashMap.put("discountAvailable", String.valueOf(discountAvailable));
                        hashMap.put("productDiscount", pDiscount);
                        hashMap.put("productDiscountNote", String.valueOf(pp));

                        DatabaseReference firebaseDatabase = FirebaseDatabase.getInstance().getReference("Products");
                        firebaseDatabase.child(productId).updateChildren(hashMap)
                                .addOnSuccessListener(aVoid -> {
                                    pd.setVisibility(View.INVISIBLE);
                                    text.setVisibility(View.VISIBLE);
                                    Toast.makeText(EditProductActivity.this, "Product Updated Successfully.", Toast.LENGTH_SHORT).show();
                                    EditProductActivity.super.onBackPressed();
                                })
                                .addOnFailureListener(e -> {
                                    pd.setVisibility(View.INVISIBLE);
                                    text.setVisibility(View.VISIBLE);
                                    Toast.makeText(EditProductActivity.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                                });
                    } else {
                        StorageReference storageReference = FirebaseStorage.getInstance().getReference(filePathName);
                        storageReference.putFile(image_uri)
                                .addOnSuccessListener(taskSnapshot -> {
                                    Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                                    while (!uriTask.isSuccessful()) ;

                                    String downloadUri = "" + uriTask.getResult();

                                    if (uriTask.isSuccessful()) {
                                        float price = (((p - d) / p) * 100);
                                        int pp = Math.round(price);

                                        HashMap<String, Object> hashMap = new HashMap<>();
                                        hashMap.put("productName", pName);
                                        hashMap.put("productDesc", pDesc);
                                        hashMap.put("productCategory", pCat);
                                        hashMap.put("productQuantity", pQty);
                                        hashMap.put("productPrice", pPrice);
                                        hashMap.put("productImage", downloadUri);
                                        hashMap.put("discountAvailable", String.valueOf(discountAvailable));
                                        hashMap.put("productDiscount", pDiscount);
                                        hashMap.put("productDiscountNote", String.valueOf(pp));

                                        DatabaseReference firebaseDatabase = FirebaseDatabase.getInstance().getReference("Products");
                                        firebaseDatabase.child(productId).updateChildren(hashMap)
                                                .addOnSuccessListener(aVoid -> {
                                                    pd.setVisibility(View.INVISIBLE);
                                                    text.setVisibility(View.VISIBLE);
                                                    Toast.makeText(EditProductActivity.this, "Product Updated Successfully.", Toast.LENGTH_SHORT).show();
                                                    EditProductActivity.super.onBackPressed();
                                                })
                                                .addOnFailureListener(e -> {
                                                    pd.setVisibility(View.INVISIBLE);
                                                    text.setVisibility(View.VISIBLE);
                                                    Toast.makeText(EditProductActivity.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                                                });
                                    }
                                })
                                .addOnFailureListener(e -> {
                                    pd.setVisibility(View.INVISIBLE);
                                    text.setVisibility(View.VISIBLE);
                                    Toast.makeText(EditProductActivity.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                                });
                    }

                }
            } else {
                pDiscount = "0";
                pDiscountN = "";
                pd.setVisibility(View.VISIBLE);
                text.setVisibility(View.INVISIBLE);
                String filePathName = "Product_Images/" + "" + timestamp;
                if (image_uri == null) {
                    HashMap<String, Object> hashMap = new HashMap<>();
                    hashMap.put("productName", pName);
                    hashMap.put("productDesc", pDesc);
                    hashMap.put("productCategory", pCat);
                    hashMap.put("productQuantity", pQty);
                    hashMap.put("productPrice", pPrice);
                    hashMap.put("productImage", pImage);
                    hashMap.put("discountAvailable", String.valueOf(discountAvailable));
                    hashMap.put("productDiscount", pDiscount);
                    hashMap.put("productDiscountNote", pDiscountN);

                    DatabaseReference firebaseDatabase = FirebaseDatabase.getInstance().getReference("Products");
                    firebaseDatabase.child(productId).updateChildren(hashMap)
                            .addOnSuccessListener(aVoid -> {
                                pd.setVisibility(View.INVISIBLE);
                                text.setVisibility(View.VISIBLE);
                                Toast.makeText(EditProductActivity.this, "Product Updated Successfully.", Toast.LENGTH_SHORT).show();
                                EditProductActivity.super.onBackPressed();
                            })
                            .addOnFailureListener(e -> {
                                pd.setVisibility(View.INVISIBLE);
                                text.setVisibility(View.VISIBLE);
                                Toast.makeText(EditProductActivity.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                            });
                } else {
                    StorageReference storageReference = FirebaseStorage.getInstance().getReference(filePathName);
                    storageReference.putFile(image_uri)
                            .addOnSuccessListener(taskSnapshot -> {
                                Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                                while (!uriTask.isSuccessful()) ;

                                String downloadUri = uriTask.getResult().toString();

                                if (uriTask.isSuccessful()) {
                                    HashMap<String, Object> hashMap = new HashMap<>();
                                    hashMap.put("productName", pName);
                                    hashMap.put("productDesc", pDesc);
                                    hashMap.put("productCategory", pCat);
                                    hashMap.put("productQuantity", pQty);
                                    hashMap.put("productPrice", pPrice);
                                    hashMap.put("productImage", downloadUri);
                                    hashMap.put("discountAvailable", String.valueOf(discountAvailable));
                                    hashMap.put("productDiscount", pDiscount);
                                    hashMap.put("productDiscountNote", pDiscountN);

                                    DatabaseReference firebaseDatabase = FirebaseDatabase.getInstance().getReference("Products");
                                    firebaseDatabase.child(productId).updateChildren(hashMap)
                                            .addOnSuccessListener(aVoid -> {
                                                pd.setVisibility(View.INVISIBLE);
                                                text.setVisibility(View.VISIBLE);
                                                Toast.makeText(EditProductActivity.this, "Product updated successfully.", Toast.LENGTH_SHORT).show();
                                                EditProductActivity.super.onBackPressed();
                                            })
                                            .addOnFailureListener(e -> {
                                                pd.setVisibility(View.INVISIBLE);
                                                text.setVisibility(View.VISIBLE);
                                                Toast.makeText(EditProductActivity.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                                            });
                                }
                            })
                            .addOnFailureListener(e -> {
                                pd.setVisibility(View.INVISIBLE);
                                text.setVisibility(View.VISIBLE);
                                Toast.makeText(EditProductActivity.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                            });
                }
            }
        });

        productCategory.setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(EditProductActivity.this);
            builder.setTitle("Product Category")
                    .setItems(Constants.options, (dialogInterface, i) -> {
                        String category = Constants.options[i];
                        productCategory.setText(category);
                    }).show();
        });

    }

    private void loadProductDetails() {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Products");
        databaseReference.child(productId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String pId = "" + snapshot.child("productId").getValue();
                String pName = "" + snapshot.child("productName").getValue();
                String uid = "" + snapshot.child("uid").getValue();
                String pDesc = "" + snapshot.child("productDesc").getValue();
                String pCategory = "" + snapshot.child("productCategory").getValue();
                String pQuantity = "" + snapshot.child("productQuantity").getValue();
                String pPrice = "" + snapshot.child("productPrice").getValue();
                pImage = "" + snapshot.child("productImage").getValue();
                timestamp = "" + snapshot.child("timestamp").getValue();
                String discountAvailable = "" + snapshot.child("discountAvailable").getValue();
                String pDiscount = "" + snapshot.child("productDiscount").getValue();
                String pDiscountNote = "" + snapshot.child("productDiscountNote").getValue();
                String shopId = "" + snapshot.child("shopId").getValue();

                if (discountAvailable.equals("true")) {
                    discount.setChecked(true);
                    productDiscount.setVisibility(View.VISIBLE);
                } else {

                    discount.setChecked(false);
                    productDiscount.setVisibility(View.GONE);
                }

                productName.setText(pName);
                productCategory.setText(pCategory);
                productQuantity.setText(pQuantity);
                productPrice.setText(pPrice);
                productDiscount.setText(pDiscount);
                productDesc.setText(pDesc);

                Shimmer shimmer = new Shimmer.ColorHighlightBuilder().setBaseColor(Color.parseColor("#F3F3F3")).setBaseAlpha(1).setHighlightColor(Color.parseColor("#E7E7E7")).setHighlightAlpha(1).setDropoff(50).build();
                ShimmerDrawable shimmerDrawable = new ShimmerDrawable();
                shimmerDrawable.setShimmer(shimmer);


                try {
                    Glide.with(getApplicationContext()).load(pImage).placeholder(shimmerDrawable).into(productImage);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void pickFromCamera() {
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.TITLE, "Temp Pic");
        values.put(MediaStore.Images.Media.DESCRIPTION, "Temp Description");

        image_uri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);


        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, image_uri);
        startActivityForResult(cameraIntent, IMAGE_PICK_CAMERA_CODE);
    }

    private void pickFromGallery() {
        Intent galleryIntent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        galleryIntent.setType("image/*");
        startActivityForResult(galleryIntent, IMAGE_PICK_GALLERY_CODE);
    }

    private boolean checkStoragePermission() {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == (PackageManager.PERMISSION_GRANTED);
    }

    private void requestStoragePermission() {
        ActivityCompat.requestPermissions(this, storagePermissions, STORAGE_REQUEST_CODE);
    }

    private boolean checkCameraPermission() {
        boolean result = ContextCompat.checkSelfPermission(EditProductActivity.this, Manifest.permission.CAMERA) == (PackageManager.PERMISSION_GRANTED);

        boolean result1 = ContextCompat.checkSelfPermission(EditProductActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == (PackageManager.PERMISSION_GRANTED);

        return result && result1;
    }

    private void requestCameraPermission() {
        ActivityCompat.requestPermissions(this, cameraPermissions, CAMERA_REQUEST_CODE);
    }

    public void back(View view) {
        EditProductActivity.super.onBackPressed();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull @NotNull String[] permissions, @NonNull @NotNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case CAMERA_REQUEST_CODE: {
                if (grantResults.length > 0) {
                    boolean cameraAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    boolean writeStorageAccepted = grantResults[1] == PackageManager.PERMISSION_GRANTED;
                    if (cameraAccepted && writeStorageAccepted) {
                        pickFromCamera();
                    } else {
                        Toast.makeText(this, "Please enable permission", Toast.LENGTH_SHORT).show();
                    }
                }
            }
            break;
            case STORAGE_REQUEST_CODE: {
                if (grantResults.length > 0) {
                    boolean writeStorageAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    if (writeStorageAccepted) {
                        pickFromGallery();
                    } else {
                        Toast.makeText(this, "Please enable permission", Toast.LENGTH_SHORT).show();
                    }
                }
            }
            break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case IMAGE_PICK_GALLERY_CODE: {
                    image_uri = data.getData();

                    productImage.setImageURI(image_uri);
                }
                break;
                case IMAGE_PICK_CAMERA_CODE: {
                    productImage.setImageURI(image_uri);
                }
                break;

            }
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

}