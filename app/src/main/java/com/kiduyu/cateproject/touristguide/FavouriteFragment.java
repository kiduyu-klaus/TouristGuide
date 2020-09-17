package com.kiduyu.cateproject.touristguide;

import android.os.Bundle;;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class FavouriteFragment extends Fragment {

    ArrayList<ItemPlaceList> mListItem;
    public RecyclerView recyclerView;
    FavAdapter adapter;
    private LinearLayout lyt_not_found;
    DatabaseHelper databaseHelper;
    TextView no_fav;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_category, container, false);

        mListItem = new ArrayList<>();
        databaseHelper = new DatabaseHelper(getActivity());
        lyt_not_found = rootView.findViewById(R.id.lyt_not_found);
        recyclerView = rootView.findViewById(R.id.vertical_courses_list);
        no_fav = rootView.findViewById(R.id.no_fav);

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 1));
        recyclerView.setFocusable(false);

        return rootView;
    }

    private void displayData() {

        adapter = new FavAdapter(getActivity(), mListItem);
        recyclerView.setAdapter(adapter);

        if (adapter.getItemCount() == 0) {
            lyt_not_found.setVisibility(View.VISIBLE);
            no_fav.setText(getString(R.string.no_favorite));
        } else {
            lyt_not_found.setVisibility(View.GONE);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        mListItem = databaseHelper.getFavourite();
        displayData();
    }
}
