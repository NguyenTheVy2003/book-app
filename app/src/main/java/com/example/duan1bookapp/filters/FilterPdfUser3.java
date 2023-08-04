package com.example.duan1bookapp.filters;

import android.widget.Filter;

import com.example.duan1bookapp.adapters.AdapterPdfAdmin;
import com.example.duan1bookapp.adapters.AdapterPdfUser3;
import com.example.duan1bookapp.models.ModelPdf;

import java.util.ArrayList;

public class FilterPdfUser3 extends Filter {
    //arraylist in which filter we want to search
    ArrayList<ModelPdf> filterList;
    //adapter in which filter need to be implemented
    AdapterPdfUser3 adapterPdfUser3;

    //constructor
    public FilterPdfUser3(ArrayList<ModelPdf> filterList, AdapterPdfUser3 adapterPdfUser3) {
        this.filterList = filterList;
        this.adapterPdfUser3 = adapterPdfUser3;
    }

    @Override
    protected FilterResults performFiltering(CharSequence constraint) {
        FilterResults results = new FilterResults();
        //value should not be null empty
        if (constraint != null && constraint.length() > 0) {
            //change to upper case , or lower case to avoid case sensitivity
            constraint = constraint.toString().toUpperCase();
            ArrayList<ModelPdf> filterModels = new ArrayList<>();

            for (int i = 0; i < filterList.size(); i++) {
                //validate
                if (filterList.get(i).getTitle().toUpperCase().contains(constraint)) {
                    //add to filtered list
                    filterModels.add(filterList.get(i));
                }
            }
            results.count = filterModels.size();
            results.values = filterModels;
        } else {

            results.count = filterList.size();
            results.values = filterList;
        }
        return results;
    }

    @Override
    protected void publishResults(CharSequence constraint, FilterResults results) {
        //apply filter
        adapterPdfUser3.pdfArrayList = (ArrayList<ModelPdf>) results.values;

        //notify
       adapterPdfUser3.notifyDataSetChanged();
    }
}
