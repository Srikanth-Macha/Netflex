package com.example.netflex.Activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.example.netflex.Adapters.ViewPageAdapter;
import com.example.netflex.R;

public class SwipeScreen extends AppCompatActivity {

    private TextView signIn, privacy, help;
    private Button getStarted;
    private ViewPager viewPagerSwipe;

    private LinearLayout dotsLayout;
    private ImageView[] dots;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_swipe_screen);

        //Method call to assign views & layouts their respective IDs
        assignIDs();

        ViewPageAdapter viewPageAdapter = new ViewPageAdapter(this);
        viewPagerSwipe.setAdapter(viewPageAdapter);

        int dotsCount = viewPageAdapter.getCount();

        //Sliding dots functionality
        slidingDots(dotsCount);

        viewPagerSwipe.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                for (int j = 0; j < dotsCount; j++) {
                    if (j == position)
                        dots[position].setImageResource(R.drawable.active_dots);
                    else
                        dots[j].setImageResource(R.drawable.inactive_dots);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });

        //Method call to set OnClickListeners on required Views
        setClickListeners();
    }

    private void setClickListeners() {
        signIn.setOnClickListener(v -> {
            Intent intent = new Intent(SwipeScreen.this, SignInActivity.class);
            startActivity(intent);
        });

        getStarted.setOnClickListener(v -> {
            Intent intent = new Intent(SwipeScreen.this, StepOne.class);
            startActivity(intent);
        });

        privacy.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://help.netflix.com/en/node/100628"));
            startActivity(intent);
        });

        help.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://help.netflix.com/en/"));
            startActivity(intent);
        });
    }


    private void slidingDots(int dotsCount) {
        dots = new ImageView[dotsCount];

        for (int i = 0; i < dotsCount; i++) {
            dots[i] = new ImageView(this);

            if (i != 0)
                dots[i].setImageResource(R.drawable.inactive_dots);
            else
                dots[i].setImageResource(R.drawable.active_dots);

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);
            params.setMargins(8, 0, 8, 0);

            dotsLayout.addView(dots[i], params);
        }
    }

    private void assignIDs() {
        signIn = findViewById(R.id.SignInID);
        privacy = findViewById(R.id.privacyID);
        help = findViewById(R.id.helpID);

        getStarted = findViewById(R.id.getstartedID);

        viewPagerSwipe = findViewById(R.id.viewPagerID);

        dotsLayout = findViewById(R.id.dotslayoutID);
    }
}