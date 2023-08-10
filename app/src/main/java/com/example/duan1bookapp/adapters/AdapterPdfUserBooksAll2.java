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
import com.example.duan1bookapp.databinding.RowPdfUserBooksAll2Binding;
import com.example.duan1bookapp.databinding.RowPdfUserBooksAllBinding;
import com.example.duan1bookapp.filters.FilterPdfUserBooksAll;
import com.example.duan1bookapp.filters.FilterPdfUserBooksAll2;
import com.example.duan1bookapp.models.ModelPdfBooksAll;
import com.example.duan1bookapp.models.ModelPdfBooksAll2;
import com.github.barteksc.pdfviewer.PDFView;

import java.util.ArrayList;

public class AdapterPdfUserBooksAll2 extends RecyclerView.Adapter<AdapterPdfUserBooksAll2.HolderPdfUser> implements Filterable {

    private Context context;
    public ArrayList<ModelPdfBooksAll2> pdfArrayList, filterList;

    private FilterPdfUserBooksAll2 filter;

    private RowPdfUserBooksAll2Binding binding;

    private static final String TAG = "ADAPTER_PDF_USER_TAG";

    public AdapterPdfUserBooksAll2(Context context, ArrayList<ModelPdfBooksAll2> pdfArrayList) {
        this.context = context;
        this.pdfArrayList = pdfArrayList;
        this.filterList = pdfArrayList;
    }

    @NonNull
    @Override
    public HolderPdfUser onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // bind the view
        binding = RowPdfUserBooksAll2Binding.inflate(LayoutInflater.from(context),parent,false);
        return new HolderPdfUser(binding.getRoot());
    }

    @Override
    public void onBindViewHolder(@NonNull AdapterPdfUserBooksAll2.HolderPdfUser holder, int position) {


        ModelPdfBooksAll2 model = pdfArrayList.get(position);
        String bookId = model.getId();
        String title = model.getTitle();
        String description = model.getDescription();;
        String pdfUrl = model.getUrl();
        String categoryId = model.getCategoryId();
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

        //addReadingBooks

        binding.pdfView.setOnClickListener(new View.OnClickListener() {
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
            filter = new FilterPdfUserBooksAll2(filterList, this);
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
//            categoryTv = binding.categoryTv;
//            sizeTv = binding.sizeTv;
//            dateTv = binding.dateTv;

            pdfView = binding.pdfView;
            progressBar = binding.progressBar;
        }
    }
}
