package com.dev.localvocaladmin.filters;

import android.widget.Filter;

import com.dev.localvocaladmin.adapters.AdapterShops;
import com.dev.localvocaladmin.models.ModelShop;

import java.util.ArrayList;

public class FilterShops extends Filter {

    private final AdapterShops adapter;
    private final ArrayList<ModelShop> filterList;

    public FilterShops(AdapterShops adapter, ArrayList<ModelShop> filterList) {
        this.adapter = adapter;
        this.filterList = filterList;
    }

    @Override
    protected FilterResults performFiltering(CharSequence constraint) {
        FilterResults results = new FilterResults();
        //validate data for search query
        if (constraint != null && constraint.length() > 0) {
            //search field is not empty, perform search
            //change to uppercase, to make case insensitive
            constraint = constraint.toString().toUpperCase();
            //store Filtered List
            ArrayList<ModelShop> filterModels = new ArrayList<>();
            for (int i = 0; i < filterList.size(); i++) {
                //check
                if (filterList.get(i).getShopname().toUpperCase().contains(constraint)) {
                    //add filtered Data to List
                    filterModels.add(filterList.get(i));
                }
            }
            results.count = filterModels.size();
            results.values = filterModels;
        } else {
            //search field is empty, return original
            results.count = filterList.size();
            results.values = filterList;
        }
        return results;
    }

    @Override
    protected void publishResults(CharSequence constraint, FilterResults results) {
        adapter.shopArrayList = (ArrayList<ModelShop>) results.values;
        //refresh
        adapter.notifyDataSetChanged();
    }
}
