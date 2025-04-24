package com.example.chattest.Utils;
import android.content.Context;
import android.graphics.Rect;
import android.view.View;

import androidx.recyclerview.widget.RecyclerView;

public class SpaceItemDecoration extends RecyclerView.ItemDecoration {

    public int spacing_left;
    public int spacing_right;
    public int spacing_top;
    public int spacing_bottom;


    public SpaceItemDecoration(int spacing_left,int spacing_right,int spacing_top,int spacing_bottom) {
        this.spacing_left = spacing_left;
        this.spacing_right = spacing_right;
        this.spacing_bottom = spacing_bottom;
        this.spacing_top = spacing_top;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        outRect.left = this.spacing_left;
        outRect.right = this.spacing_right;
        outRect.top = this.spacing_bottom;
        outRect.bottom = this.spacing_top;
    }
}