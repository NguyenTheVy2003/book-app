package com.example.duan1bookapp.filters;

import android.widget.Filter;

import com.example.duan1bookapp.adapters.AdapterPdfTrendingBooks;
import com.example.duan1bookapp.adapters.AdapterPdfTrendingBooksAll;
import com.example.duan1bookapp.models.ModelPdfTrendingBooks;

import java.util.ArrayList;

public class FilterPdfTrendingBooksAll extends Filter {

    // arraylist in which we want to search
    ArrayList<ModelPdfTrendingBooks> filterList;
    AdapterPdfTrendingBooksAll adapterPdfTrendingBooksAll;

    public FilterPdfTrendingBooksAll(ArrayList<ModelPdfTrendingBooks> filterList, AdapterPdfTrendingBooksAll adapterPdfTrendingBooksAll) {
        this.filterList = filterList;
        this.adapterPdfTrendingBooksAll = adapterPdfTrendingBooksAll;
    }

    @Override
    protected FilterResults performFiltering(CharSequence constraint) {
        FilterResults results = new FilterResults();
        // values to be searched should not be null/empty
        if (constraint != null || constraint.length() > 0){
            // not null nar empty
            // change to uppercase to lower case to avoid case sensitivity
            constraint = constraint.toString().toUpperCase();
            ArrayList<ModelPdfTrendingBooks> filteredModels = new ArrayList<>();

            for (int i = 0; i < filterList.size(); i++){
                // validate
                if (filterList.get(i).getTitle().toUpperCase().contains(constraint)){
                    // search matches, add to list
                    filteredModels.add(filterList.get(i));
                }
            }
            results.count = filteredModels.size();
            results.values = filteredModels;
        }
        else {
            // empty or null make original list/result
            results.count = filterList.size();
            results.values = filterList;
        }
        return results; // dont miss it
    }

    @Override
    protected void publishResults(CharSequence constraint, FilterResults results) {
        adapterPdfTrendingBooksAll.pdfArrayList = (ArrayList<ModelPdfTrendingBooks>)results.values;
        // notify changes
        adapterPdfTrendingBooksAll.notifyDataSetChanged();
    }
}
