package com.example.duan1bookapp.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.duan1bookapp.Constants;
import com.example.duan1bookapp.R;
import com.example.duan1bookapp.databinding.ActivityPdfViewContinueBinding;
import com.github.barteksc.pdfviewer.listener.OnErrorListener;
import com.github.barteksc.pdfviewer.listener.OnPageChangeListener;
import com.github.barteksc.pdfviewer.listener.OnPageErrorListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class PdfViewActivityContinue extends AppCompatActivity {
    // View biding
    private ActivityPdfViewContinueBinding binding;
    private String bookId;
    private static final String TAG = "PDF_VIEW_TAG";

    int currentPage;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivityPdfViewContinueBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // get bookId from intent that we passed in intent
        Intent intent = getIntent();
        bookId = intent.getStringExtra("bookId");
        Log.d(TAG,"onCreate: BookId: "+bookId);
        //get currentPage from intent that we passed in intent
        Intent intent1=getIntent();
        currentPage=intent1.getIntExtra("currentPage",-1);
        loadBookDetail();
        // handle click, go back

        binding.backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }
    private void loadBookDetail() {
        Log.d(TAG,"loadBookDetails: Get Pdf URL from db... ");
        // Database Reference to get book details e.g. book url using book id
        // Step (1) Get Book Url using Book Id
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Books");
        ref.child(bookId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        // get book url
                        String pdfUrl = "" + snapshot.child("url").getValue();
                        Log.d(TAG,"onDataChange: PDF URL: " + pdfUrl);
                        // Step (2) Load Pdf using that url from firebase storage
                        loadBookFromUrl(pdfUrl);
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }
    private void loadBookFromUrl(String pdfUrl) {
        Log.d(TAG, "loadBookFromUrl: Get PDF from storage");
        StorageReference reference = FirebaseStorage.getInstance().getReferenceFromUrl(pdfUrl);
        reference.getBytes(Constants.MAX_BYTES_PDF)
                .addOnSuccessListener(new OnSuccessListener<byte[]>() {
                    @Override
                    public void onSuccess(byte[] bytes) {

                        // load pdf using bytes
                        binding.pdfView.fromBytes(bytes)
                                .swipeHorizontal(false) // set false to scroll vertical, set true to swipe horizontal
                                .onPageChange(new OnPageChangeListener() {
                                    @Override
                                    public void onPageChanged(int page, int pageCount) {
                                        // set current and total pages in toolbar subtitle
                                        int currentPagee = (page + 1); // do + 1 because page starts from 0
                                        binding.toolbarSubtitleTv.setText(currentPagee + "/" + pageCount);// e.g. 3/290
                                        //save page start
                                        Toast.makeText(PdfViewActivityContinue.this, "loc"+currentPage, Toast.LENGTH_SHORT).show();



                                        Log.d(TAG, "onPageChanged: " + currentPagee + "/" + pageCount);
                                    }
                                })
                                .onError(new OnErrorListener() {
                                    @Override
                                    public void onError(Throwable t) {
                                        Log.d(TAG, "onError: " + t.getMessage());
                                        Toast.makeText(PdfViewActivityContinue.this, " " + t.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                })
                                .onPageError(new OnPageErrorListener() {
                                    @Override
                                    public void onPageError(int page, Throwable t) {
                                        Log.d(TAG, "onPageError: " + t.getMessage());
                                        Toast.makeText(PdfViewActivityContinue.this, "Error on page" + page + " " + t.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                })
                                .load();

                        binding.progressBar.setVisibility(View.GONE);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG, "onFailure: " + e.getMessage());
                        // failed to load book
                        binding.progressBar.setVisibility(View.GONE);
                    }
                });
    }
}