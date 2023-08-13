package com.example.duan1bookapp.adapters;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.example.duan1bookapp.MyApplication;
import com.example.duan1bookapp.R;
import com.example.duan1bookapp.activities.PdfDetailActivity;
import com.example.duan1bookapp.models.ModelSlideShow;
import com.github.barteksc.pdfviewer.PDFView;
import com.google.android.play.integrity.internal.m;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.smarteist.autoimageslider.SliderViewAdapter;

import java.util.ArrayList;
import java.util.List;

public class SliderAdapterExample extends SliderViewAdapter<SliderAdapterExample.SliderAdapterVH> {
    private Context context;
    private List<ModelSlideShow> mSliderItems = new ArrayList<>();

    public SliderAdapterExample(Context context,List<ModelSlideShow> mSliderItems ) {
        this.context = context;
        this.mSliderItems=mSliderItems;
    }

    public void renewItems(List<ModelSlideShow> sliderItems) {
        this.mSliderItems = sliderItems;
        notifyDataSetChanged();
    }

    public void deleteItem(int position) {
        this.mSliderItems.remove(position);
        notifyDataSetChanged();
    }

    public void addItem(ModelSlideShow sliderItem) {
        this.mSliderItems.add(sliderItem);
        notifyDataSetChanged();
    }

    @Override
    public SliderAdapterVH onCreateViewHolder(ViewGroup parent) {
        View inflate = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_item_slide_show, null);
        return new SliderAdapterVH(inflate);
    }

    @Override
    public void onBindViewHolder(SliderAdapterVH viewHolder, final int position) {

        ModelSlideShow model = mSliderItems.get(position);
        loadSlideShow(model,viewHolder);

        viewHolder.pdfView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(context, PdfDetailActivity.class);
                intent.putExtra("bookId",model.getId());
                context.startActivity(intent);
            }
        });

        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(context, PdfDetailActivity.class);
                intent.putExtra("bookId",model.getId());
                context.startActivity(intent);
            }
        });

    }

    private void loadSlideShow(ModelSlideShow model, SliderAdapterVH viewHolder) {
        String bookId=model.getId();
        Log.d("TAG", "loadBooksPdfFragmentHome: Book Reading of Book ID:"+bookId);

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
                        String viewCount=""+snapshot.child("viewsCount").getValue();
                        String downloadsCount=""+snapshot.child("downloadsCount").getValue();

                        //set to model
                        model.setTitle(bookTitle);
                        model.setDescription(description);
                        model.setTimestamp(Long.parseLong(timestamp));
                        model.setCategoryId(categoryId);
                        model.setUid(uid);
                        model.setUrl(bookUrl);
                        //format Data

                        String date= MyApplication.formatTimestamp(Long.parseLong(timestamp));

                        MyApplication.loadPdfFromUrlSinglePage(""+bookUrl,""+bookTitle,viewHolder.pdfView,viewHolder.progressBar,null);

                        //set Data to views
                        viewHolder.tv_title.setText(bookTitle);
                        viewHolder.tv_view.setText("Views:"+viewCount);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    @Override
    public int getCount() {
        //slider view count could be dynamic size
        return mSliderItems.size();
    }

    class SliderAdapterVH extends SliderViewAdapter.ViewHolder {

        PDFView pdfView;
        ProgressBar progressBar;
        TextView tv_title,tv_view;

        public SliderAdapterVH(View itemView) {
            super(itemView);
            pdfView=itemView.findViewById(R.id.pdfView);
            progressBar=itemView.findViewById(R.id.progressBar);
            tv_title=itemView.findViewById(R.id.tv_title);
            tv_view=itemView.findViewById(R.id.tv_view);
        }
    }
}
