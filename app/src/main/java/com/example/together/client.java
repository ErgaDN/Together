package com.example.together;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.os.Bundle;
import com.google.android.material.tabs.TabLayout;

// copy from agricultural to running! you can chnge the code!

public class client extends AppCompatActivity {
    private TabLayout tabLayout;
    private ViewPager viewPager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_agricultural);

        tabLayout = findViewById(R.id.tablayout);
        viewPager = findViewById(R.id.viewpager);

        tabLayout.setupWithViewPager(viewPager);

        VPAdapter vpAdapter = new VPAdapter(getSupportFragmentManager(), FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        vpAdapter.addFragment(new DistributionCenter(), "נקודת חלוקה");
        vpAdapter.addFragment(new EditingProducts(), "עריכת מוצרים");
        vpAdapter.addFragment(new OrderSummary(), "סיכום הזמנות");

        viewPager.setAdapter(vpAdapter);



    }
}