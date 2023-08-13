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
import com.example.duan1bookapp.databinding.RowPdfUserBooksAllBinding;
import com.example.duan1bookapp.filters.FilterPdfUserBooksAll;
import com.example.duan1bookapp.models.ModelPdf2;
import com.example.duan1bookapp.models.ModelPdfBooksAll;
import com.github.barteksc.pdfviewer.PDFView;

import java.util.ArrayList;

public class AdapterPdfUserBooksAll extends RecyclerView.Adapter<AdapterPdfUserBooksAll.HolderPdfUser> implements Filterable {

    private Context context;
    public ArrayList<ModelPdfBooksAll> pdfArrayList, filterList;

    private FilterPdfUserBooksAll filter;

    private RowPdfUserBooksAllBinding binding;

    private static final String TAG = "ADAPTER_PDF_USER_TAG";

    public AdapterPdfUserBooksAll(Context context, ArrayList<ModelPdfBooksAll> pdfArrayList) {
        this.context = context;
        this.pdfArrayList = pdfArrayList;
        this.filterList = pdfArrayList;
    }

    @NonNull
    @Override
    public HolderPdfUser onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // bind the view
        binding = RowPdfUserBooksAllBinding.inflate(LayoutInflater.from(context),parent,false);
        return new HolderPdfUser(binding.getRoot());
    }

    @Override
    public void onBindViewHolder(@NonNull AdapterPdfUserBooksAll.HolderPdfUser holder, int position) {


        ModelPdfBooksAll model = pdfArrayList.get(position);
        String bookId = model.getId();
        String title = model.getTitle();
        String description = model.getDescription();;
        String pdfUrl = model.getUrl();
        String viewCount=""+model.getViewsCount();
        long timestamp = model.getTimestamp();

        // convert time
        String date = MyApplication.formatTimestamp(timestamp);

        // set data
        holder.titleTv.setText(title);
        holder.viewCountTv.setText("Views:"+viewCount);
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
            public void onClick(View view) {
                //chuyển Id books Sang PdfDetailActivity
                Intent intent = new Intent(context, PdfDetailActivity.class);
                intent.putExtra("bookId",bookId);
                context.startActivity(intent);
            }
        });
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //chuyển Id books Sang PdfDetailActivity
                Intent intent = new Intent(context, PdfDetailActivity.class);
                intent.putExtra("bookId",bookId);
                context.startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return pdfArrayList.size();// return list size || number of records

    }

    @Override
    public Filter getFilter() {
        if (filter == null){
            filter = new FilterPdfUserBooksAll(filterList, this);
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
//            categoryTv = binding.categoryTv;
//            sizeTv = binding.sizeTv;
//            dateTv = binding.dateTv;

            pdfView = binding.pdfView;
            progressBar = binding.progressBar;
        }
    }
}
