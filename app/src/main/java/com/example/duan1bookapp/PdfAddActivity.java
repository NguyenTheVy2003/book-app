package com.example.duan1bookapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.duan1bookapp.databinding.ActivityPdfAddBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.HashMap;

public class PdfAddActivity extends AppCompatActivity {
    //setup view binding
    private ActivityPdfAddBinding binding;
    //firebase auth
    private FirebaseAuth firebaseAuth;
    //progress dialog
    private ProgressDialog progressDialog;
    //arr to to hold
    private ArrayList<ModelCategory>categoryArrayList;
    //uri of picked pdf
    private Uri pdfUri =null;
    private  static final int PDF_PICK_CODE = 1000;

    //TAG for debugging
    private  static final String TAG = "ADD_PDF_TAG";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding =ActivityPdfAddBinding.inflate(getLayoutInflater());
        setContentView(R.layout.activity_pdf_add);
        firebaseAuth =FirebaseAuth.getInstance();
        loadPdfCategories();

        //setup progress dialog
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Please wait");
        progressDialog.setCanceledOnTouchOutside(false);

        //handle click ,go to previous activity
        binding.backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        //handle click, attach pdf
        binding.attachBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pdfPickIntent();
            }
        });

        //handle click, pick category
        binding.categoryTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                categoryPickDialog();
            }
        });

        //handle click , upload pdf
        binding.submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //validate data
                validateData();
                
            }
        });
    }

    private String title ="",descripon ="",catgory="";
    private void validateData() {
        //Step 1 : validate data
        Log.d(TAG,"validateData dat");
        //get data
        title = binding.titleEt.getText().toString().trim();
        descripon=binding.descriptionEt.getText().toString().trim();
        catgory=binding.categoryTv.getText().toString().trim();

        //value data
        if (TextUtils.isEmpty(title)){
            Toast.makeText(this, "Enter Title....", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(descripon)) {
            Toast.makeText(this, "Enter Description", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(catgory)) {
            Toast.makeText(this, "Pick Category", Toast.LENGTH_SHORT).show();
        } else if (pdfUri==null) {
            Toast.makeText(this, "Pick PDF", Toast.LENGTH_SHORT).show();
        }
        else {
            uploadPdfStorage();
        }
    }

    private void uploadPdfStorage() {
        //Step 2 : Upload Pdf to firebase storage
        Log.d(TAG,"UploadPdfToStorage: uploading pdf categories");
        //show progress
        progressDialog.setMessage("Uploading PDF...");
        progressDialog.show();
        //timestamp
        long timestamp =System.currentTimeMillis();
        //path of pdf in firebase storage
        String filePathAndName= "Books/"+timestamp;
        //storage reference
        StorageReference storageReference = FirebaseStorage.getInstance().getReference(filePathAndName);
        storageReference.putFile(pdfUri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        Log.d(TAG,"onSuccess: PDF uploaded to storage");
                        Log.d(TAG,"onSuccess:  getting pdf url");
                        Task<Uri> uriTask=taskSnapshot.getStorage().getDownloadUrl();
                        while (!uriTask.isSuccessful());
                        String uploadPdfUrl =""+uriTask.getResult();
                        uploadPdfInfoToDb(uploadPdfUrl,timestamp);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressDialog.dismiss();
                        Log.d(TAG,"onFailure: PDF upload failed due to"+e.getMessage());
                        Toast.makeText(PdfAddActivity.this, "PDF upload failed due to"+e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void uploadPdfInfoToDb(String uploadPdfUrl, long timestamp) {

        //Step 3 : Upload Pdf  info to firebase db
        Log.d(TAG,"UploadPdfToStorage: uploading pdf  info to firebase db");

        progressDialog.setMessage("Uploading pdf info");
        String uid = firebaseAuth.getUid();
        //set data to upload
        HashMap<String,Object> hashMap= new HashMap<>();
        hashMap.put("uid",""+uid);
        hashMap.put("id",""+timestamp);
        hashMap.put("title ",""+title);
        hashMap.put("description",""+descripon);
        hashMap.put("category",""+catgory);
        hashMap.put("timestamp",""+timestamp);

        //db reference DB > Books
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Books");
        ref.child(""+timestamp)
                .setValue(hashMap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        progressDialog.dismiss();
                        Log.d(TAG,"OnSuccess: Successfully uploaded...");
                        Toast.makeText(PdfAddActivity.this, "Successfully uploaded...", Toast.LENGTH_SHORT).show();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressDialog.dismiss();
                        Log.d(TAG,"onFailure: Failed to upload to db due to"+e.getMessage());
                        Toast.makeText(PdfAddActivity.this, "Failed to upload to db due to"+e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

    }

    private void loadPdfCategories() {

        Log.d(TAG,"loadPdfCategories :Loading pdf categories");
        categoryArrayList= new ArrayList<>();

        //db reference to load
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Categories");
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                categoryArrayList.clear();//clear before adding data
                for (DataSnapshot ds:snapshot.getChildren()){
                    //get data
                    ModelCategory model = ds.getValue(ModelCategory.class);
                    //add to arr
                    categoryArrayList.add(model);

                    Log.d(TAG,"onDataChange:"+model.getCategory());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void categoryPickDialog() {

        Log.d(TAG,"categoryPickDialog: showing category pick dialog");
        //get string arr of categories from arr

        String[]categoriesArray = new String[categoryArrayList.size()];
        for (int i=0;i < categoryArrayList.size();i++){
            categoriesArray[i]=categoryArrayList.get(i).getCategory();

            //alert dialog
            AlertDialog.Builder builder=new AlertDialog.Builder(this);
            builder.setTitle("Pick Category")
                    .setItems(categoriesArray, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            //handle item click
                            //get clicked item from list
                            String category = categoriesArray[i];
                            //set category txt view
                            binding.categoryTv.setText(category);

                            Log.d(TAG,"Selected Category:"+category);
                        }
                    })
                    .show();
        }
    }

    private void pdfPickIntent() {

        Log.d(TAG, "pdfPickIntent: starting pdf pick intent");

        Intent intent = new Intent();
        intent.setType("Application/pdf");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent,"Select Pdf"),PDF_PICK_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode==RESULT_OK){
            if (requestCode==PDF_PICK_CODE){
                Log.d(TAG,"onActivityResult: PDF Picked");

                pdfUri=data.getData();
                Log.d(TAG,"onActivityResult: URI:"+pdfUri);
            }
        }
        else {
            Log.d(TAG,"onActivityResult picking pdf");
            Toast.makeText(this, "Cancelled Picking PDF", Toast.LENGTH_SHORT).show();
        }
    }
}