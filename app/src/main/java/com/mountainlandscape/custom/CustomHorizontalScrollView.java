package com.mountainlandscape.custom;

import android.content.Context;
import android.widget.HorizontalScrollView;

import com.mountainlandscape.interfaces.HorizontalScrollViewScrollEvents;

public class CustomHorizontalScrollView extends HorizontalScrollView {
    HorizontalScrollViewScrollEvents callback;

    public CustomHorizontalScrollView(Context context) {
        super(context);
    }

    public CustomHorizontalScrollView(Context context, HorizontalScrollViewScrollEvents events) {
        super(context);
        this.callback = events;
    }


    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        super.onScrollChanged(l, t, oldl, oldt);
        this.callback.onScrollChanged(this);
    }
}
