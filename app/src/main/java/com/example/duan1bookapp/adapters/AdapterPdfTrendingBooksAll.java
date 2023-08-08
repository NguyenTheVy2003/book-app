package com.example.duan1bookapp.adapters;

import android.content.Context;
import android.content.Intent;
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
import com.example.duan1bookapp.databinding.RowPdfTrendingBooksAllBinding;
import com.example.duan1bookapp.databinding.RowPdfTrendingBooksBinding;
import com.example.duan1bookapp.filters.FilterPdfTrendingBooks;
import com.example.duan1bookapp.filters.FilterPdfTrendingBooksAll;
import com.example.duan1bookapp.models.ModelPdfTrendingBooks;
import com.github.barteksc.pdfviewer.PDFView;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;

public class AdapterPdfTrendingBooksAll extends RecyclerView.Adapter<AdapterPdfTrendingBooksAll.HolderPdfUser> implements Filterable {

    private Context context;
    public ArrayList<ModelPdfTrendingBooks> pdfArrayList, filterList;
    private FilterPdfTrendingBooksAll filter;
    private RowPdfTrendingBooksAllBinding binding;
    private FirebaseAuth firebaseAuth;

    private static final String TAG = "ADAPTER_PDF_USER_TAG";

    public AdapterPdfTrendingBooksAll(Context context, ArrayList<ModelPdfTrendingBooks> pdfArrayList) {
        this.context = context;
        this.pdfArrayList = pdfArrayList;
        this.filterList = pdfArrayList;
    }

    @NonNull
    @Override
    public HolderPdfUser onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // bind the view
        binding = RowPdfTrendingBooksAllBinding.inflate(LayoutInflater.from(context),parent,false);
        return new HolderPdfUser(binding.getRoot());
    }

    @Override
    public void onBindViewHolder(@NonNull AdapterPdfTrendingBooksAll.HolderPdfUser holder, int position) {
        firebaseAuth=FirebaseAuth.getInstance();
        ModelPdfTrendingBooks model = pdfArrayList.get(position);
        String bookId = model.getId();
        String title = model.getTitle();
        String description = model.getDescription();;
        String pdfUrl = model.getUrl();
        String categoryId = model.getCategoryId();
        String id= model.getId();
        String uid=model.getUid();
        long timestamp = model.getTimestamp();

        // convert time
        String date = MyApplication.formatTimestamp(timestamp);

        // set data
        holder.titleTv.setText(title);
        holder.descriptionTv.setText(description);
//        holder.dateTv.setText(date);

        // we dont need page number here,pass null
        MyApplication.loadPdfFromUrlSinglePage(
                "" + pdfUrl,
                "" + title,
                holder.pdfView,
                holder.progressBar,
                null
        );
        holder.pdfView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, PdfDetailActivity.class);
                intent.putExtra("bookId",bookId);
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return pdfArrayList.size(); // return list size || number of records
    }

    @Override
    public Filter getFilter() {
        if (filter == null){
            filter = new FilterPdfTrendingBooksAll(filterList, this);
        }
        return filter;
    }

    class HolderPdfUser extends RecyclerView.ViewHolder {

        TextView titleTv, descriptionTv;
        PDFView pdfView;
        ProgressBar progressBar;
        public HolderPdfUser(@NonNull View itemView) {
            super(itemView);
            titleTv = binding.titleTv;
            descriptionTv = binding.descriptionTv;
            pdfView = binding.pdfView;
            progressBar = binding.progressBar;
        }
    }


}
