package com.kma.taskmanagement.ui.main;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.kma.taskmanagement.R;
import com.kma.taskmanagement.ui.intro.IntroActivity;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.tab_layout)
    public TabLayout tabLayout;
    @BindView(R.id.view_pager2)
    public ViewPager2 viewPager2;
    public TaskPagerAdapter adapter;

//    @BindView(R.id.btnExit)
//    Button btnExit;
//    @OnClick(R.id.btnExit) void exit() {
//
//        SharedPreferences pref = getApplicationContext().getSharedPreferences("myPrefs",MODE_PRIVATE);
//        SharedPreferences.Editor editor = pref.edit();
//        editor.putBoolean("isIntroOpnend",false);
//        editor.commit();
//        Intent mainActivity = new Intent(getApplicationContext(), IntroActivity.class );
//        startActivity(mainActivity);
//        finish();
//    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);
        String[] labels = getResources().getStringArray(R.array.mainTab);
        adapter = new TaskPagerAdapter(this);
        viewPager2.setAdapter(adapter);

        new TabLayoutMediator(tabLayout, viewPager2, (tab, position) -> {
            tab.setText(labels[position]);
        }).attach();

        viewPager2.setCurrentItem(0, false);
    }
}