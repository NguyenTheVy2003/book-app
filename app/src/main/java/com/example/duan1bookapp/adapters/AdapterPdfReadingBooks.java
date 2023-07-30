package com.example.duan1bookapp.adapters;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.duan1bookapp.MyApplication;
import com.example.duan1bookapp.activities.PdfDetailActivity;
import com.example.duan1bookapp.databinding.RowPdfFavoriteBinding;
import com.example.duan1bookapp.databinding.RowPdfReadingBooksBinding;
import com.example.duan1bookapp.models.ModelPdf;
import com.example.duan1bookapp.models.ModelPdfReadingBooks;
import com.github.barteksc.pdfviewer.PDFView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class AdapterPdfReadingBooks extends RecyclerView.Adapter<AdapterPdfReadingBooks.HolderPdfFavorite>{
    private Context context;
    private ArrayList<ModelPdfReadingBooks> pdfArrayList;
    //view binding for row_pdf_favorite.xml
    private RowPdfReadingBooksBinding binding;
    private static final String TAG="FAV_BOOK_TAG";

    //constructor
    public AdapterPdfReadingBooks(Context context, ArrayList<ModelPdfReadingBooks> pdfArrayList) {
        this.context = context;
        this.pdfArrayList = pdfArrayList;
    }

    @NonNull
    @Override
    public HolderPdfFavorite onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //bind/inflate row_pdf_favorite.xml layout
        binding =RowPdfReadingBooksBinding.inflate(LayoutInflater.from(context),parent,false);
        return new HolderPdfFavorite(binding.getRoot());
    }

    @Override
    public void onBindViewHolder(@NonNull HolderPdfFavorite holder, int position) {
        //get Data,set Data,handle click
        //handle click,open pdf details page,already done in previous videis
        ModelPdfReadingBooks model=pdfArrayList.get(position);

        loadBookPdfDatails(model,holder);


        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(context, PdfDetailActivity.class);
                intent.putExtra("bookId",model.getId());//pass book id not category id
                context.startActivity(intent);

            }
        });
    }

    private void loadBookPdfDatails(ModelPdfReadingBooks model, HolderPdfFavorite holder) {
        String bookId=model.getId();
        Log.d(TAG, "loadBookPdfDatails: Book Details of Book ID:"+bookId);

        DatabaseReference ref= FirebaseDatabase.getInstance().getReference("Books");
        ref.child(bookId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        //get book info
                        String bookTitle=""+snapshot.child("title").getValue();
                        String description=""+snapshot.child("description").getValue();
                        String categoryId=""+snapshot.child("categoryId").getValue();
                        String bookUrl=""+snapshot.child("url").getValue();
                        String timestamp=""+snapshot.child("timestamp").getValue();
                        String uid=""+snapshot.child("uid").getValue();
                        String viewCount=""+snapshot.child("viewCount").getValue();
                        String downloadsCount=""+snapshot.child("downloadsCount").getValue();

                        //set to model
                        model.setFavorite(true);
                        model.setTitle(bookTitle);
                        model.setDescription(description);
                        model.setTimestamp(Long.parseLong(timestamp));
                        model.setCategoryId(categoryId);
                        model.setUid(uid);
                        model.setUrl(bookUrl);
                        //format Data
                        String date= MyApplication.formatTimestamp(Long.parseLong(timestamp));

                        MyApplication.loadPdfFromUrlSinglePage(""+bookUrl,""+bookTitle,holder.pdfView,holder.progressBar,null);

                        //set data to views
                        holder.titleTv.setText(bookTitle);
                        holder.descriptionTv.setText(description);

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    @Override
    public int getItemCount() {
        return pdfArrayList.size();//return list size || nuber of records
    }

    //ViewHolder class
    class HolderPdfFavorite extends RecyclerView.ViewHolder{
        private PDFView pdfView;
        private ProgressBar progressBar;
        private TextView titleTv,descriptionTv;
        public HolderPdfFavorite(@NonNull View itemView) {
            super(itemView);

            //init ui views of row_pdf_favorite.xml
            pdfView=binding.pdfView;
            progressBar=binding.progressBar;
            titleTv=binding.titleTv;
            descriptionTv=binding.descriptionTv;
        }
    }
}
