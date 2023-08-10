package com.example.duan1bookapp.adapters;

import static com.example.duan1bookapp.MyApplication.deleteBook;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.duan1bookapp.MyApplication;
import com.example.duan1bookapp.activities.PdfDetailActivity;
import com.example.duan1bookapp.activities.PdfEditActivity;
import com.example.duan1bookapp.databinding.RowPdfAdminBinding;
import com.example.duan1bookapp.filters.FilterPdfAdmin;
import com.example.duan1bookapp.models.ModelPdf;
import com.github.barteksc.pdfviewer.PDFView;

import java.util.ArrayList;

public class AdapterPdfAdmin extends RecyclerView.Adapter<AdapterPdfAdmin.HolderPdfAdmin> implements Filterable {

    //context
    private Context context;
    // Arraylist to hold list of data of type model pdf

    public ArrayList<ModelPdf> pdfArrayList,filterList;

    //view binding row_pdf_admin.xml
    private RowPdfAdminBinding binding;
    private FilterPdfAdmin filter;

    private static final String TAG = "PDF_ADAPTER_TAG";
    //progress
    private ProgressDialog progressDialog;


    //contructor
    public AdapterPdfAdmin(Context context, ArrayList<ModelPdf> pdfArrayList) {
        this.context = context;
        this.pdfArrayList = pdfArrayList;
//        this.filterList= pdfArrayList;
        this.filterList = new ArrayList<>(pdfArrayList);

        //init progress dialog
        progressDialog= new ProgressDialog(context);
        progressDialog.setTitle("Please wait");
        progressDialog.setCanceledOnTouchOutside(false);
    }

    @NonNull
    @Override
    public HolderPdfAdmin onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //bind layout using view binding
        binding = RowPdfAdminBinding.inflate(LayoutInflater.from(context), parent, false);
        return new HolderPdfAdmin(binding.getRoot());
    }

    @Override
    public void onBindViewHolder(@NonNull HolderPdfAdmin holder, int position) {
        //bind layout using view binding
        binding = RowPdfAdminBinding.bind(holder.itemView);

        //get data ,set data, handle click etc

        //get data
        ModelPdf model = pdfArrayList.get(position);
        // Lấy thông tin từ ModelPdf và gắn nó vào các view tương ứng trong HolderPdfAdmin
        // Load các thông tin khác như category, pdf từ url, pdf size trong các phương thức tương ứng
        // Xử lý sự kiện click nút "More" và hiển thị dialog chọn tùy chọn
        String title = model.getTitle();
        String description = model.getDescription();
        String pdfUrl= model.getUrl();
        String pdfId = model.getId();
        String categoryId = model.getCategoryId();
        long timestamp = model.getTimestamp();
        //convert timestamp to dd/MM/yyyy
        String formattedDate = MyApplication.formatTimestamp(timestamp);

        //set data
        holder.titleTv.setText(title);
        holder.descriptionTv.setText(description);
        holder.dateTv.setText(formattedDate);

        //load further details like category, pdf from url, pdf size in seprate funtions
        MyApplication.loadCategory2(""+categoryId,holder.categoryTv);
        MyApplication.loadPdfFromUrlSinglePage(
                ""+pdfUrl
                ,""+title
                ,holder.pdfView
                ,holder.progressBar
        ,null);
        MyApplication.loadPdfSize2(""+pdfUrl,""+title,holder.sizeTv);

        //handel click , show dialog with option 1\edit, 2\delete
        holder.moreBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                moreOptionsDialog(model,holder);
            }
        });

        //handle click , open pdf detail page
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, PdfDetailActivity.class);
                intent.putExtra("bookId",pdfId);
                context.startActivity(intent);
            }
        });

    }

    private void moreOptionsDialog(ModelPdf model, HolderPdfAdmin holder) {
        // Lấy thông tin về id, url và title của sách
        // Tạo một dialog AlertDialog để hiển thị các tùy chọn "Edit" và "Delete"
        // Xử lý sự kiện khi người dùng chọn tùy chọn
        String bookId = model.getId();
        String bookUrl = model.getUrl();
        String bookTitle = model.getTitle();

        //options to show in dialog
        String[] options = {"Edit", "Delete"};

        //AlertDialog
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Choose Options")
                .setItems(options, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int which) {
                        if (which == 0) {
                            //edit click, open pdf edit activity to edit book info

                            Intent intent = new Intent(context, PdfEditActivity.class);
                            intent.putExtra("bookId", bookId);
                            context.startActivity(intent);

                        } else if (which==1) {
                            //delete click
                            deleteBook(context,""+bookId,""+bookUrl,""+bookTitle);
                            //deleteBook(model, holder);
                        }
                    }
                })
                .show();
    }









    @Override
    public int getItemCount() {
        return pdfArrayList.size();//return number of records\ list size
    }

    @Override
    public Filter getFilter() {
        if (filter ==null){
            filter = new FilterPdfAdmin(filterList,this);
        }
        return filter;
    }

    /*View Holder class for row_pdf_admin.xml*/
    class HolderPdfAdmin extends RecyclerView.ViewHolder {

        //Ui View of the row_pdf_admin.xml
        PDFView pdfView;
        ProgressBar progressBar;
        TextView titleTv, descriptionTv, categoryTv, sizeTv, dateTv;
        ImageButton moreBtn;


        public HolderPdfAdmin(@NonNull View itemView) {
            super(itemView);

            //init ui view
            pdfView = binding.pdfView;
            progressBar = binding.progressBar;
            titleTv = binding.titleTv;
            descriptionTv = binding.descriptionTv;
            categoryTv = binding.categoryTv;
            sizeTv = binding.sizeTv;
            dateTv = binding.dateTv;
            moreBtn=binding.moreBtn;

        }
    }
}
