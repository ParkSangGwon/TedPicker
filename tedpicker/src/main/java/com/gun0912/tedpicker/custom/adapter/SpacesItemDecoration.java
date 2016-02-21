package com.gun0912.tedpicker.custom.adapter;

import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * Created by TedPark on 15. 11. 24..
 */


public class SpacesItemDecoration extends RecyclerView.ItemDecoration {
    public static final int TYPE_VERTICAL = 0;
    public static final int TYPE_HORIZONTAL = 1;
    private int space;
    private int lastItemInFirstLane = -1;
    private int mode;


    public SpacesItemDecoration(int space, int mode) {
        if (mode != TYPE_HORIZONTAL && mode != TYPE_VERTICAL) {
            new RuntimeException("mode have to  TYPE_VERTICAL or TYPE_VERTICAL");
            return;
        }

        this.space = space;
        this.mode = mode;

    }


    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {


        if (parent.getChildAdapterPosition(view) != parent.getAdapter().getItemCount() - 1) {


            if(mode==TYPE_VERTICAL){
                outRect.bottom = space;
            }else{
                outRect.right = space;
            }



        }

    }
}