package com.example.duan1bookapp.filters;

import android.widget.Filter;

import com.example.duan1bookapp.adapters.AdapterCategory;
import com.example.duan1bookapp.models.ModelCategory;

import java.util.ArrayList;

public class FilterCategory extends Filter {
    //arraylist in which filter we want to search
    ArrayList<ModelCategory> filterList;
    //adapter in which filter need to be implemented
    AdapterCategory adapterCategory;

    //constructor
    public FilterCategory(ArrayList<ModelCategory> filterList, AdapterCategory adapterCategory) {
        this.filterList = filterList;
        this.adapterCategory = adapterCategory;
    }

    @Override
    protected FilterResults performFiltering(CharSequence constraint) {
        FilterResults results = new FilterResults();
        //value should not be null empty
        if (constraint != null && constraint.length() > 0) {
            //change to upper case , or lower case to avoid case sensitivity
            constraint = constraint.toString().toUpperCase();
            ArrayList<ModelCategory> filterModels = new ArrayList<>();

            for (int i = 0; i < filterList.size(); i++) {
                //validate
                if (filterList.get(i).getCategory().toUpperCase().contains(constraint)) {
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
        adapterCategory.categoryArrayList = (ArrayList<ModelCategory>) results.values;

        //notify
        adapterCategory.notifyDataSetChanged();
    }
}
