package com.czy.smartmedicine.fragment;


import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.SearchView;

import androidx.annotation.NonNull;

import com.czy.baseUtilsLib.activity.BaseFragment;
import com.czy.smartmedicine.databinding.FragmentSearchBinding;

/**
 * @author 13225
 */
public class SearchFragment extends BaseFragment<FragmentSearchBinding> {


    public SearchFragment() {
        super(SearchFragment.class);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    protected void init() {
        super.init();
        initView();
    }

    private final String[] mStrs = {"aaa", "bbb", "ccc", "abcdefg"};
    private void initView() {
        binding.listView.setAdapter(new ArrayAdapter<>(requireContext(), android.R.layout.simple_list_item_1, mStrs));
        binding.listView.setTextFilterEnabled(true);
    }

    @Override
    protected void setListener() {
        super.setListener();
        binding.searchBar.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (!TextUtils.isEmpty(newText)){
                    binding.listView.setFilterText(newText);
                }else{
                    binding.listView.clearTextFilter();
                }
                return false;
            }
        });
    }
}