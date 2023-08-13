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
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.duan1bookapp.MyApplication;
import com.example.duan1bookapp.activities.PdfDetailActivity;
import com.example.duan1bookapp.databinding.RowPdfTrendingBooksBinding;
import com.example.duan1bookapp.databinding.RowPdfUserBinding;
import com.example.duan1bookapp.filters.FilterPdfTrendingBooks;
import com.example.duan1bookapp.filters.FilterPdfUser;
import com.example.duan1bookapp.models.ModelPdf;
import com.example.duan1bookapp.models.ModelPdfTrendingBooks;
import com.github.barteksc.pdfviewer.PDFView;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;

public class AdapterPdfTrendingBooks extends RecyclerView.Adapter<AdapterPdfTrendingBooks.HolderPdfUser> implements Filterable {

    private Context context;
    public ArrayList<ModelPdfTrendingBooks> pdfArrayList, filterList;
    private FilterPdfTrendingBooks filter;
    private RowPdfTrendingBooksBinding binding;
    private FirebaseAuth firebaseAuth;

    private static final String TAG = "ADAPTER_PDF_USER_TAG";

    public AdapterPdfTrendingBooks(Context context, ArrayList<ModelPdfTrendingBooks> pdfArrayList) {
        this.context = context;
        this.pdfArrayList = pdfArrayList;
        this.filterList = pdfArrayList;
    }

    @NonNull
    @Override
    public HolderPdfUser onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // bind the view
        binding = RowPdfTrendingBooksBinding.inflate(LayoutInflater.from(context),parent,false);
        return new HolderPdfUser(binding.getRoot());
    }

    @Override
    public void onBindViewHolder(@NonNull AdapterPdfTrendingBooks.HolderPdfUser holder, int position) {
        firebaseAuth=FirebaseAuth.getInstance();
        ModelPdfTrendingBooks model = pdfArrayList.get(position);
        String bookId = model.getId();
        String title = model.getTitle();
        String description = model.getDescription();;
        String pdfUrl = model.getUrl();
        String categoryId = model.getCategoryId();
        String id= model.getId();
        String uid=model.getUid();
        String viewsCount=""+model.getViewsCount();
        long timestamp = model.getTimestamp();

        // convert time
        String date = MyApplication.formatTimestamp(timestamp);

        // set data
        holder.titleTv.setText(title);
        holder.viewCountTv.setText("Views:"+viewsCount);
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

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
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
            filter = new FilterPdfTrendingBooks(filterList, this);
        }
        return filter;
    }

    class HolderPdfUser extends RecyclerView.ViewHolder {

        TextView titleTv, viewCountTv;
        PDFView pdfView;
        ProgressBar progressBar;
        public HolderPdfUser(@NonNull View itemView) {
            super(itemView);
            titleTv = binding.titleTv;
            viewCountTv = binding.viewCountTv;
            pdfView = binding.pdfView;
            progressBar = binding.progressBar;
        }
    }


}
