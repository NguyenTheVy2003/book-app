package com.example.duan1bookapp;

import static com.example.duan1bookapp.Constants.MAX_BYTES_PDF;

import android.app.Application;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Environment;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.github.barteksc.pdfviewer.PDFView;
import com.github.barteksc.pdfviewer.listener.OnErrorListener;
import com.github.barteksc.pdfviewer.listener.OnLoadCompleteListener;
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
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;

public class MyApplication extends Application {

    private static final String TAG_DOWNLOAD = "DOWNLOAD_TAG";
    @Override
    public void onCreate() {
        super.onCreate();
    }

    // created a static method to convert timestamp to proper date format, so we can use it everywhere in project
    public static final String formatTimestamp(long timestamp) {
        Calendar cal = Calendar.getInstance(Locale.ENGLISH);
        cal.setTimeInMillis(timestamp);
        //format timestamp to dd/MM/yyyy
        String date = DateFormat.format("dd/MM/yyyy", cal).toString();

        return date;
    }

    public static void deleteBook(Context context, String bookId, String bookUrl, String bookTitle) {
        // Lấy thông tin về id, url và title của sách
        // Hiển thị ProgressDialog để thông báo việc xóa sách
        // Xóa sách khỏi Storage và sau đó xóa thông tin sách khỏi Database

        String TAG = "DELETE_BOOK_TAG";


        Log.d(TAG, "deleteBook: Deleting...");
        ProgressDialog progressDialog = new ProgressDialog(context);
        progressDialog.setTitle("Please wait...");
        progressDialog.setMessage("Deleting" + bookTitle + "...");
        progressDialog.show();

        Log.d(TAG, "deleteBook: Deleting from storage");
        StorageReference storageReference = FirebaseStorage.getInstance().getReferenceFromUrl(bookUrl);
        storageReference.delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Log.d(TAG, "onSuccess: Deleted from storage");
                        Log.d(TAG, "onSuccess: Now deleting info from db");

                        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Books");
                        reference.child(bookId)
                                .removeValue()
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                        Log.d(TAG, "onSuccess: Deleted from db too");
                                        progressDialog.dismiss();
                                        Toast.makeText(context, "Book Deleted Successfully ....", Toast.LENGTH_SHORT).show();
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Log.d(TAG, "onFailure: Failed to deletefrom db too" + e.getMessage());
                                        progressDialog.dismiss();
                                        Toast.makeText(context, "" + e.getMessage(), Toast.LENGTH_SHORT).show();

                                    }
                                });
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG, "onFailure: Failed to delete info from storage due to" + e.getMessage());
                        progressDialog.dismiss();
                        Toast.makeText(context, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    public static void loadPdfSize(String pdfUrl, String pdfTitle, TextView sizeTv) {
        // Lấy url của sách từ model
        // Sử dụng FirebaseStorage và StorageReference để lấy kích thước của file PDF
        // Chuyển đổi kích thước từ bytes sang đơn vị đo lường phù hợp (KB hoặc MB)
        // Hiển thị kích thước trong TextView trong HolderPdfAdmin

        //using url can get file and its metadata from firebase storage

        String TAG = "PDF_SIZE_TAG";


        StorageReference ref = FirebaseStorage.getInstance().getReferenceFromUrl(pdfUrl);
        ref.getMetadata()
                .addOnSuccessListener(new OnSuccessListener<StorageMetadata>() {
                    @Override
                    public void onSuccess(StorageMetadata storageMetadata) {
                        //get size in the bytes
                        double bytes = storageMetadata.getSizeBytes();
                        Log.d(TAG, "onSuccess" + pdfTitle + "" + bytes);

                        // convert bytes to KB,Mb
                        double kb = bytes / 1024;
                        double mb = kb / 1024;

                        if (mb >= 1) {
                            sizeTv.setText(String.format("%.2f", mb) + " MB");
                        } else if (kb >= 1) {
                            sizeTv.setText(String.format("%.2f", kb) + " KB");
                        } else {
                            sizeTv.setText(String.format("%.2f", bytes) + " bytes");
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        //failed getting metadata
                        Log.d(TAG, "onFailure" + e.getMessage());
                    }
                });
    }

    public static void loadPdfFromUrlSinglePage(String pdfUrl, String pdfTitle, PDFView pdfView, ProgressBar progressBar,TextView pagesTv) {
        // Lấy url của sách từ model
        // Sử dụng FirebaseStorage và StorageReference để tải file PDF từ url
        // Khi tải xong, hiển thị file PDF trong PDFView
        // Xử lý lỗi và hiển thị ProgressBar trong quá trình tải

        String TAG = "PDF_LOAD_SINGLE_TAG";


        StorageReference ref = FirebaseStorage.getInstance().getReferenceFromUrl(pdfUrl);
        ref.getBytes(MAX_BYTES_PDF)
                .addOnSuccessListener(new OnSuccessListener<byte[]>() {

                    @Override
                    public void onSuccess(byte[] bytes) {
                        Log.d(TAG, "onSuccess: " + pdfTitle + "successfully got the title");

                        //set to pdfview
                        pdfView.fromBytes(bytes)
                                .pages(0)//show only first page
                                .spacing(0)
                                .swipeHorizontal(false)
                                .enableSwipe(false)
                                .onError(new OnErrorListener() {

                                    @Override
                                    public void onError(Throwable t) {
                                        //hide progress
                                        progressBar.setVisibility(View.INVISIBLE);
                                        Log.d(TAG, "onError: " + t.getMessage());
                                    }
                                })
                                .onPageError(new OnPageErrorListener() {
                                    @Override
                                    public void onPageError(int page, Throwable t) {
                                        //hide progress
                                        progressBar.setVisibility(View.INVISIBLE);
                                        Log.d(TAG, "onPageError: " + t.getMessage());
                                    }
                                }).onLoad(new OnLoadCompleteListener() {
                                    @Override
                                    public void loadComplete(int nbPages) {
                                        //pdf loaded
                                        //hide progress
                                        progressBar.setVisibility(View.INVISIBLE);
                                        Log.d(TAG, "loadComplete: pdf loaded");

                                        //if pagesTv param is not null then set page numbers
                                        if(pagesTv !=null){
                                            pagesTv.setText(""+nbPages);//concatnate with double quotes because cant set int in TextView
                                        }
                                    }
                                })
                                .load();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        //hide progress
                        progressBar.setVisibility(View.INVISIBLE);
                        Log.d(TAG, "onFailure : failed to getting file from url due to" + e.getMessage());
                    }
                });
    }

    public static void loadCategory(String categoryId, TextView categoryTv) {
        // Lấy categoryId từ model và truy vấn database để lấy thông tin về danh mục
        // Gán danh mục vào TextView trong HolderPdfAdmin

        //get category using categoryId


        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Categories");
        ref.child(categoryId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        //get category
                        String category = "" + snapshot.child("category").getValue();

                        //set to category text view
                        categoryTv.setText(category);

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

    }

    public static void incrementBookViewCount(String bookId){
        //get book view count
        DatabaseReference ref=FirebaseDatabase.getInstance().getReference("Books");
        ref.child(bookId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        //get views count
                        String viewsCount = ""+snapshot.child("viewsCount").getValue();
                        //in case of null replace with 0
                        if(viewsCount.equals("")||viewsCount.equals("null")){
                            viewsCount="0";
                        }

                        // increment views count
                        long newViewsCount=Long.parseLong(viewsCount)+1;
                        HashMap<String,Object> hashMap=new HashMap<>();
                        hashMap.put("viewsCount",newViewsCount);

                        DatabaseReference reference=FirebaseDatabase.getInstance().getReference("Books");
                        reference.child(bookId)
                                .updateChildren(hashMap);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    public static void downloadBook(Context context, String bookId, String bookTitle, String bookUrl){
        Log.d(TAG_DOWNLOAD, "downloadBook: downloading book...");
        String nameWithExtension = bookTitle + ".pdf";
        Log.d(TAG_DOWNLOAD, "downloadBook: NAME: " + nameWithExtension);

        // progress dialog
        ProgressDialog progressDialog = new ProgressDialog(context);
        progressDialog.setTitle("Please wait");
        progressDialog.setMessage("Downloading " + nameWithExtension + "..."); // e.g. Downloading ABC_Book.pdf
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();

        // download from firebase storage using url
        StorageReference storageReference = FirebaseStorage.getInstance().getReferenceFromUrl(bookUrl);
        storageReference.getBytes(MAX_BYTES_PDF)
                .addOnSuccessListener(new OnSuccessListener<byte[]>() {
                    @Override
                    public void onSuccess(byte[] bytes) {
                        Log.d(TAG_DOWNLOAD, "onSuccess: Book Downloaded");
                        saveDownloadedBook(context, progressDialog, bytes, nameWithExtension, bookId);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG_DOWNLOAD, "onFailure: Failed to download due to " + e.getMessage());
                        progressDialog.dismiss();
                        Toast.makeText(context, "Failed to download due to " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private static void saveDownloadedBook(Context context, ProgressDialog progressDialog, byte[] bytes, String nameWithExtension, String bookId) {
        Log.d(TAG_DOWNLOAD, "saveDownloadedBook: Saving downloaded book...");
        try {
            File downloadsFolder = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
            downloadsFolder.mkdirs();

            String filePath = downloadsFolder.getPath() + "/" + nameWithExtension;
            FileOutputStream out = new FileOutputStream(filePath);
            out.write(bytes);
            out.close();

            Toast.makeText(context, "Saved to Download Folder", Toast.LENGTH_SHORT).show();
            Log.d(TAG_DOWNLOAD, "saveDownloadedBook: Saved to Download Folder");
            progressDialog.dismiss();

            incrementBookDownloadCount(bookId);

        }
        catch (Exception e){
            Log.d(TAG_DOWNLOAD, "saveDownloadedBook: Failed saving to Download Folder due to " + e.getMessage());
            Toast.makeText(context, "Failed saving to Download Folder due to " + e.getMessage(), Toast.LENGTH_SHORT).show();
            progressDialog.dismiss();
        }
    }

    private static void incrementBookDownloadCount(String bookId) {

        Log.d(TAG_DOWNLOAD, "incrementBookDownloadCount: Incrementing Book Download Count");

        // Step 1: Get previous download count
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Books");
        ref.child(bookId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        String downloadsCount = "" + snapshot.child("downloadsCount").getValue();
                        Log.d(TAG_DOWNLOAD, "onDataChange: Downloads Count: " + downloadsCount);

                        if (downloadsCount.equals("") || downloadsCount.equals("null")){
                            downloadsCount = "@";
                        }

                        // convert to long and increment 1
                        long newDownloadsCount = Long.parseLong(downloadsCount) + 1;
                        Log.d(TAG_DOWNLOAD, "onDataChange: New Download Count " + newDownloadsCount);

                        // setup data to update
                        HashMap<String, Object> hashMap = new HashMap<>();
                        hashMap.put("downloadsCount", newDownloadsCount);

                        // Step 2: Update new incremented downloads count to db
                        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Books");
                        reference.child(bookId).updateChildren(hashMap)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                        Log.d(TAG_DOWNLOAD, "onSuccess: Downloads Count update...");
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Log.d(TAG_DOWNLOAD, "onFailure: Failed to update Downloads Count due to " + e.getMessage());
                                    }
                                });

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    public static void addToFavorite(Context context,String bookId){
        //we can add only if user is logged in
        //1) check if user is logged in
        FirebaseAuth firebaseAuth=FirebaseAuth.getInstance();
        if(firebaseAuth.getCurrentUser() == null){
            //not logged in,cant add to favorite
            Toast.makeText(context, "You're not logged in", Toast.LENGTH_SHORT).show();
        }else {
            long timestamp =System.currentTimeMillis();

            //setup data to add in firebase db of current user for favorite book
            HashMap<String,Object> hashMap=new HashMap<>();
            hashMap.put("bookId",""+bookId);
            hashMap.put("timestamp",""+timestamp);

            //save to db
            DatabaseReference ref=FirebaseDatabase.getInstance().getReference("Users");
            ref.child(firebaseAuth.getUid()).child("Favorites").child(bookId)
                    .setValue(hashMap)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            Toast.makeText(context, "Added to your favorites list...", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(context, "Failed to add to favorites due to"+e.getMessage(),Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }

    public static void removeFromFavorite(Context context,String bookId){
        //we can add remove if user is logged in
        //1) check if user is logged in
        FirebaseAuth firebaseAuth=FirebaseAuth.getInstance();
        if(firebaseAuth.getCurrentUser() == null){
            //not logged in,cant remove from favorite
            Toast.makeText(context, "You're not logged in", Toast.LENGTH_SHORT).show();
        }else {


            //remove to db
            DatabaseReference ref=FirebaseDatabase.getInstance().getReference("Users");
            ref.child(firebaseAuth.getUid()).child("Favorites").child(bookId)
                    .removeValue()
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            Toast.makeText(context, "Remove from your favorites list...", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(context, "Failed to remove from favorites due to"+e.getMessage(),Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }


}
