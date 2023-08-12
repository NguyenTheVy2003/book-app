package com.example.duan1bookapp.filters;

import android.widget.Filter;
import android.widget.TextView;

import com.example.duan1bookapp.adapters.AdapterPdfViewsHistoryBooks;
import com.example.duan1bookapp.models.ModelPdfViewsHistoryBooks;

import java.util.ArrayList;

public class FilterPdfViewHistoryBooks extends Filter {

    // arraylist in which we want to search
    ArrayList<ModelPdfViewsHistoryBooks> filterList;
    AdapterPdfViewsHistoryBooks adapterPdfViewsHistoryBooks;

    public FilterPdfViewHistoryBooks(ArrayList<ModelPdfViewsHistoryBooks> filterList, AdapterPdfViewsHistoryBooks adapterPdfViewsHistoryBooks) {
        this.filterList = filterList;
        this.adapterPdfViewsHistoryBooks = adapterPdfViewsHistoryBooks;
    }

    @Override
    protected FilterResults performFiltering(CharSequence constraint) {
        FilterResults results = new FilterResults();
        // values to be searched should not be null/empty
        if (constraint != null || constraint.length() > 0){
            // not null nar empty
            // change to uppercase to lower case to avoid case sensitivity
            constraint = constraint.toString().toUpperCase();
            ArrayList<ModelPdfViewsHistoryBooks> filteredModels = new ArrayList<>();

            for (int i = 0; i< filterList.size(); i++){
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
        adapterPdfViewsHistoryBooks.pdfArrayList = (ArrayList<ModelPdfViewsHistoryBooks>)results.values;
        // notify changes
        adapterPdfViewsHistoryBooks.notifyDataSetChanged();
    }
}
