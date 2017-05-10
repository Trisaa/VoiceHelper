package com.voice.android.quicknote;

import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * Created by lebron on 17-5-9.
 */

public class NoteItemDecoration extends RecyclerView.ItemDecoration {
    private int space;

    public NoteItemDecoration(int space) {
        this.space = space;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        int currentPos = parent.getChildLayoutPosition(view);
        int lastPos = state.getItemCount() - 1;
        if (currentPos == lastPos) {
            outRect.top = space;
            outRect.bottom = space;
        } else {
            outRect.top = space;
        }
    }
}
