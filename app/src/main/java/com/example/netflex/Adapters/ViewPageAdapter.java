package com.example.netflex.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.example.netflex.R;

public class ViewPageAdapter extends PagerAdapter {

    int[] images = {R.drawable.imageone, R.drawable.imagetwo, R.drawable.imagethree};
    private final Context context;

    public ViewPageAdapter(Context context) {
        super();
        this.context = context;
    }

    @Override
    public int getCount() {
        return images.length;
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        LayoutInflater layoutInflater = LayoutInflater.from(context);

        View view = layoutInflater.inflate(R.layout.custom_layout, container, false);

        ImageView imageView = view.findViewById(R.id.customlayoutimageID);
        imageView.setImageResource(images[position]);

        ViewPager viewPager = (ViewPager) container;
        viewPager.addView(view, 0);

        return view;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        ViewPager viewPager = (ViewPager) container;
        View view = (View) object;

        viewPager.removeView(view);
    }
}
