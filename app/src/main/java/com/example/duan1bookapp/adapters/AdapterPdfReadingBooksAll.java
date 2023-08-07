package com.example.duan1bookapp.adapters;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.duan1bookapp.MyApplication;
import com.example.duan1bookapp.activities.PdfDetailActivity;
import com.example.duan1bookapp.databinding.RowPdfReadingBooksAllBinding;
import com.example.duan1bookapp.filters.FilterPdfViewHistory;
import com.example.duan1bookapp.models.ModelPdf;
import com.github.barteksc.pdfviewer.PDFView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;

public class AdapterPdfReadingBooksAll extends RecyclerView.Adapter<AdapterPdfReadingBooksAll.HolderPdfReadingBooks> implements Filterable {
    private Context context;
    public ArrayList<ModelPdf> pdfArrayList,filterList;
    //view binding
    private RowPdfReadingBooksAllBinding binding;

    private FilterPdfViewHistory filter;

    private static final String TAG="REA_BOOK_TAG";


    public AdapterPdfReadingBooksAll(Context context, ArrayList<ModelPdf> pdfArrayList) {
        this.context = context;
        this.pdfArrayList = pdfArrayList;
        this.filterList=pdfArrayList;
    }

    public void loaddata(ArrayList<ModelPdf> pdfArrayList){
        ArrayList<ModelPdf> list = pdfArrayList;
        Collections.reverse(list);
    }

    @NonNull
    @Override
    public HolderPdfReadingBooks onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        binding=RowPdfReadingBooksAllBinding.inflate(LayoutInflater.from(context),parent,false);
        return new HolderPdfReadingBooks(binding.getRoot());
    }

    @Override
    public void onBindViewHolder(@NonNull HolderPdfReadingBooks holder, int position) {
        ModelPdf model=pdfArrayList.get(position);
        //loadReadingBooks FragmentHome
        loadBooksPdfFragmentHome(model,holder);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(context, PdfDetailActivity.class);
                intent.putExtra("bookId",model.getId());
                context.startActivity(intent);
            }
        });
    }

    private void loadBooksPdfFragmentHome(ModelPdf model, HolderPdfReadingBooks holder) {
        String bookId=model.getId();
        Log.d(TAG, "loadBooksPdfFragmentHome: Book Reading of Book ID:"+bookId);

        DatabaseReference ref= FirebaseDatabase.getInstance().getReference("Books");
        ref.child(bookId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        //get Book info
                        String bookTitle=""+snapshot.child("title").getValue();
                        String description=""+snapshot.child("description").getValue();
                        String categoryId=""+snapshot.child("categoryId").getValue();
                        String bookUrl=""+snapshot.child("url").getValue();
                        String timestamp=""+snapshot.child("timestamp").getValue();
                        String uid=""+snapshot.child("uid").getValue();
                        String viewCount=""+snapshot.child("viewCount").getValue();
                        String downloadsCount=""+snapshot.child("downloadsCount").getValue();

                        //set to model
                        model.setReadingBooks(true);
                        model.setTitle(bookTitle);
                        model.setDescription(description);
                        model.setTimestamp(Long.parseLong(timestamp));
                        model.setCategoryId(categoryId);
                        model.setUid(uid);
                        model.setUrl(bookUrl);
                        //format Data

                        String date= MyApplication.formatTimestamp(Long.parseLong(timestamp));

                        MyApplication.loadPdfFromUrlSinglePage(""+bookUrl,""+bookTitle,holder.pdfView,holder.progressBar,null);

                        //set Data to views
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
        return pdfArrayList.size();
    }

    @Override
    public Filter getFilter() {
        if (filter == null){
            filter = new FilterPdfViewHistory(filterList, this);
        }
        return filter;
    }


    class HolderPdfReadingBooks extends RecyclerView.ViewHolder{
        private PDFView pdfView;
        private ProgressBar progressBar;
        private TextView titleTv,descriptionTv;
        public HolderPdfReadingBooks(@NonNull View itemView) {
            super(itemView);
            //init ui views of row_pdf_favorite.xml
            pdfView=binding.pdfView;
            progressBar=binding.progressBar;
            titleTv=binding.titleTv;
            descriptionTv=binding.descriptionTv;
        }
    }
}
