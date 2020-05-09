package com.mountainlandscape.custom;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.view.MotionEvent;
import android.view.View;
import android.widget.HorizontalScrollView;
import android.widget.RelativeLayout;

import androidx.core.content.ContextCompat;

import com.cesards.cropimageview.CropImageView;
import com.mountainlandscape.interfaces.HorizontalScrollViewScrollEvents;
import com.mountainlandscape.utilities.ColorUtils;
import com.mountainlandscape.utilities.ViewUtils;

public class ScrollableLayerView extends RelativeLayout implements View.OnTouchListener {

    private CustomHorizontalScrollView horizontalScrollView;
    private View padddingView;
    private RelativeLayout relLayImageContainer;
    private CropImageView imageView;
    private CropImageView imageViewShadow;

    public ScrollableLayerView(Context context, HorizontalScrollViewScrollEvents scrollListener) {
        super(context);

        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        this.setLayoutParams(params);


        RelativeLayout.LayoutParams paddingViewParams = new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        paddingViewParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        padddingView = new View(context);
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR1) {
            padddingView.setId(ViewUtils.generateViewId());
        } else {
            padddingView.setId(View.generateViewId());
        }
        padddingView.setLayoutParams(paddingViewParams);
        addView(padddingView);

        horizontalScrollView = new CustomHorizontalScrollView(context, scrollListener);
        horizontalScrollView.setOnTouchListener(this);
        RelativeLayout.LayoutParams scrollViewParams = new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        scrollViewParams.addRule(RelativeLayout.ABOVE, padddingView.getId());
        horizontalScrollView.setLayoutParams(scrollViewParams);
        horizontalScrollView.setVerticalScrollBarEnabled(false);
        horizontalScrollView.setHorizontalScrollBarEnabled(false);
        addView(horizontalScrollView);

        relLayImageContainer = new RelativeLayout(context);
        horizontalScrollView.addView(relLayImageContainer);


        padddingView.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                horizontalScrollView.onTouchEvent(motionEvent);
                return true;
            }
        });

    }

    public void setLayerOffset(int offset) {
        padddingView.getLayoutParams().height = offset;
        padddingView.setLayoutParams(padddingView.getLayoutParams());
    }

    public void addImage(Drawable imgDrawable) {
        if(imageView == null) {
            imageView = new CropImageView(getContext());
            imageView.setCropType(CropImageView.CropType.LEFT_CENTER);
            imageView.setImageDrawable(imgDrawable);
            relLayImageContainer.addView(imageView);
        } else {
            imageView.setImageDrawable(imgDrawable);
        }
    }

    public void addImage(int drawableId) {
        addImage(ContextCompat.getDrawable(getContext(), drawableId));
    }

    public void addShadowImage(Drawable imgDrawable) {
        if(imageView == null)
            throw new IllegalArgumentException("Can't add shadow image without layer image.");

        if(imageViewShadow == null) {
            imageViewShadow = new CropImageView(getContext());
            imageViewShadow.setCropType(CropImageView.CropType.LEFT_CENTER);
            imageViewShadow.setImageDrawable(imgDrawable);
            relLayImageContainer.addView(imageViewShadow);
        } else {
            imageViewShadow.setImageDrawable(imgDrawable);
        }
    }

    public void addShadowImage(int drawableId) {
        if(drawableId != 0) {
            addShadowImage(ContextCompat.getDrawable(getContext(), drawableId));
        }
    }

    public void setImageColorFilter(int color) {
        imageView.setColorFilter(color);
        padddingView.setBackgroundColor(color);

        if(imageViewShadow != null) {
            imageViewShadow.setColorFilter(ColorUtils.getColorOfDegradate(color, Color.WHITE, 15));
        }
    }

    public HorizontalScrollView getHorizontalScrollView() {
        return horizontalScrollView;
    }

    //disable the scroll by touch.
    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        return false;
    }

}
