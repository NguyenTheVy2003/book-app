package com.example.duan1bookapp.filters;

import android.widget.Filter;

import com.example.duan1bookapp.adapters.AdapterPdfReadingBooks;
import com.example.duan1bookapp.adapters.AdapterPdfReadingBooksAll;
import com.example.duan1bookapp.models.ModelPdf;

import java.util.ArrayList;

public class FilterPdfViewHistory2 extends Filter {

    // arraylist in which we want to search
    ArrayList<ModelPdf> filterList;
    AdapterPdfReadingBooks adapterPdfReadingBooks;

    public FilterPdfViewHistory2(ArrayList<ModelPdf> filterList, AdapterPdfReadingBooks adapterPdfReadingBooks) {
        this.filterList = filterList;
        this.adapterPdfReadingBooks = adapterPdfReadingBooks;
    }

    @Override
    protected FilterResults performFiltering(CharSequence constraint) {
        FilterResults results = new FilterResults();
        // values to be searched should not be null/empty
        if (constraint != null || constraint.length() > 0){
            // not null nar empty
            // change to uppercase to lower case to avoid case sensitivity
            constraint = constraint.toString().toUpperCase();
            ArrayList<ModelPdf> filteredModels = new ArrayList<>();

            for (int i = 0; i<filterList.size(); i++){
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
        adapterPdfReadingBooks.pdfArrayList = (ArrayList<ModelPdf>)results.values;
        // notify changes
        adapterPdfReadingBooks.notifyDataSetChanged();
    }
}
