package com.example.duan1bookapp.filters;

import android.widget.Filter;

import com.example.duan1bookapp.adapters.AdapterPdfUserBooksAll;
import com.example.duan1bookapp.adapters.AdapterPdfUserBooksAll2;
import com.example.duan1bookapp.models.ModelPdfBooksAll;
import com.example.duan1bookapp.models.ModelPdfBooksAll2;

import java.util.ArrayList;

public class FilterPdfUserBooksAll2 extends Filter {

    // arraylist in which we want to search
    ArrayList<ModelPdfBooksAll2> filterList;
    AdapterPdfUserBooksAll2 adapterPdfUserBooksAll2;

    public FilterPdfUserBooksAll2(ArrayList<ModelPdfBooksAll2> filterList, AdapterPdfUserBooksAll2 adapterPdfUserBooksAll2) {
        this.filterList = filterList;
        this.adapterPdfUserBooksAll2 = adapterPdfUserBooksAll2;
    }

    @Override
    protected FilterResults performFiltering(CharSequence constraint) {
        FilterResults results = new FilterResults();
        // values to be searched should not be null/empty
        if (constraint != null || constraint.length() > 0){
            // not null nar empty
            // change to uppercase to lower case to avoid case sensitivity
            constraint = constraint.toString().toUpperCase();
            ArrayList<ModelPdfBooksAll2> filteredModels = new ArrayList<>();

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
        adapterPdfUserBooksAll2.pdfArrayList = (ArrayList<ModelPdfBooksAll2>)results.values;
        // notify changes
        adapterPdfUserBooksAll2.notifyDataSetChanged();
    }
}
