package com.kma.taskmanagement.ui.adapter;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.kma.taskmanagement.ui.main.fragments.AssignedTaskFragment;
import com.kma.taskmanagement.ui.main.fragments.GroupTaskFragment;
import com.kma.taskmanagement.ui.main.fragments.MyTaskFragment;

public class FragmentGroupTaskAdapter  extends FragmentPagerAdapter {
    private static int NUM_ITEMS = 2;
    private final FragmentManager fragmentManager;
    private Fragment mFragmentAtPos0;

    public FragmentGroupTaskAdapter(FragmentManager fragmentManager) {
        super(fragmentManager);
        this.fragmentManager = fragmentManager;
    }

    @Override
    public int getCount() {
        return NUM_ITEMS;
    }

    // Returns the fragment to display for that page
    @Override
    public Fragment getItem(int position) {
        if(position == 0) {
            if(mFragmentAtPos0 == null) {
                mFragmentAtPos0 = GroupTaskFragment.newInstance(new FirstPageFragmentListener()
                {
                    public void onSwitchToNextFragment()
                    {
                        fragmentManager.beginTransaction().remove(mFragmentAtPos0).commit();
                        mFragmentAtPos0 = AssignedTaskFragment.newInstance();
                        notifyDataSetChanged();
                    }
                });
            }
            return mFragmentAtPos0;
        } else {
            return MyTaskFragment.newInstance();
        }
//        switch (position) {
//            case 0: // Fragment # 0 - This will show FirstFragment
//                return GroupTaskFragment.newInstance();
//            case 1: // Fragment # 0 - This will show FirstFragment different title
//                return MyTaskFragment.newInstance();
//            default:
//                return null;
//        }
    }

    // Returns the page title for the top indicator
    @Override
    public CharSequence getPageTitle(int position) {
        return null;
    }

    public interface FirstPageFragmentListener
    {
        void onSwitchToNextFragment();
    }
}
