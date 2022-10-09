package com.kma.taskmanagement.ui.main;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.viewpager2.widget.ViewPager2;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.kma.taskmanagement.R;
import com.kma.taskmanagement.ui.intro.IntroActivity;
import com.kma.taskmanagement.ui.main.fragments.ChartFragment;
import com.kma.taskmanagement.ui.main.fragments.GroupTaskFragment;
import com.kma.taskmanagement.ui.main.fragments.PersonTaskFragment;
import com.kma.taskmanagement.ui.main.fragments.SettingFragment;
import com.kma.taskmanagement.utils.DateUtils;
import com.shrikanthravi.customnavigationdrawer2.data.MenuItem;
import com.shrikanthravi.customnavigationdrawer2.widget.SNavigationDrawer;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity {

//    @BindView(R.id.tab_layout)
//    public TabLayout tabLayout;
//    @BindView(R.id.view_pager2)
//    public ViewPager2 viewPager2;
//    public TaskPagerAdapter adapter;
    SNavigationDrawer sNavigationDrawer;
    int color1=0;
    Class fragmentClass;
    public static Fragment fragment;
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

//        ButterKnife.bind(this);
//        String[] labels = getResources().getStringArray(R.array.mainTab);
//        adapter = new TaskPagerAdapter(this);
//        viewPager2.setAdapter(adapter);
//
//        new TabLayoutMediator(tabLayout, viewPager2, (tab, position) -> {
//            tab.setText(labels[position]);
//        }).attach();
//
//        viewPager2.setCurrentItem(0, false);

        if(getSupportActionBar()!=null) {
            getSupportActionBar().hide();
        }

        sNavigationDrawer = findViewById(R.id.navigationDrawer);
        List<MenuItem> menuItems = new ArrayList<>();
        menuItems.add(new MenuItem(getResources().getString(R.string.personal),R.drawable.one));
        menuItems.add(new MenuItem(getResources().getString(R.string.group),R.drawable.two));
        menuItems.add(new MenuItem(getResources().getString(R.string.chart),R.drawable.three));
        menuItems.add(new MenuItem(getResources().getString(R.string.settingtab),R.drawable.one));
        sNavigationDrawer.setMenuItemList(menuItems);
        sNavigationDrawer.setAppbarTitleTextColor(getResources().getColor(R.color.COLOR_BUTTON));
        sNavigationDrawer.setMenuiconTintColor(getResources().getColor(R.color.COLOR_BUTTON));
        fragmentClass =  PersonTaskFragment.class;
        try {
            fragment = (Fragment) fragmentClass.newInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (fragment != null) {
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction().setCustomAnimations(android.R.animator.fade_in, android.R.animator.fade_out).replace(R.id.frameLayout, fragment).commit();
        }


        sNavigationDrawer.setOnMenuItemClickListener(new SNavigationDrawer.OnMenuItemClickListener() {
            @Override
            public void onMenuItemClicked(int position) {
                System.out.println("Position "+position);

                switch (position){
                    case 0:{
                        color1 = R.color.red;
                        fragmentClass = PersonTaskFragment.class;
                        break;
                    }
                    case 1:{
                        color1 = R.color.orange;
                        fragmentClass = GroupTaskFragment.class;
                        break;
                    }
                    case 2:{
                        color1 = R.color.green;
                        fragmentClass = ChartFragment.class;
                        break;
                    }
                    case 3:{
                        color1 = R.color.COLOR_BUTTON;
                        fragmentClass = SettingFragment.class;
                        break;
                    }
                }
                sNavigationDrawer.setDrawerListener(new SNavigationDrawer.DrawerListener() {

                    @Override
                    public void onDrawerOpened() {

                    }

                    @Override
                    public void onDrawerOpening(){

                    }

                    @Override
                    public void onDrawerClosing(){
                        System.out.println("Drawer closed");

                        try {
                            fragment = (Fragment) fragmentClass.newInstance();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        if (fragment != null) {
                            FragmentManager fragmentManager = getSupportFragmentManager();
                            fragmentManager.beginTransaction().setCustomAnimations(android.R.animator.fade_in, android.R.animator.fade_out).replace(R.id.frameLayout, fragment).commit();

                        }
                    }

                    @Override
                    public void onDrawerClosed() {

                    }

                    @Override
                    public void onDrawerStateChanged(int newState) {
                        System.out.println("State "+newState);
                    }
                });
            }
        });

    }
}