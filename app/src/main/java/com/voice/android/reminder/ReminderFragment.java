package com.voice.android.reminder;

import android.content.Context;
import android.content.Intent;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;

import com.voice.android.R;
import com.voice.android.common.base.BaseFragment;

import butterknife.BindView;

/**
 * Created by lebron on 17-4-27.
 */

public class ReminderFragment extends BaseFragment {
    @BindView(R.id.tab_layout)
    TabLayout mTabLayout;
    @BindView(R.id.viewpager)
    ViewPager mViewPager;

    private ReminderAdapter mReminderAdapter;

    public static void start(Context context) {
        Intent intent = new Intent(context, ReminderFragment.class);
        context.startActivity(intent);
    }

    @Override
    protected int getContentViewID() {
        return R.layout.activity_reminder;
    }

    @Override
    protected void initViewsAndData(View view) {
        initView();
    }

    private void initView() {
        mReminderAdapter = new ReminderAdapter(getActivity().getSupportFragmentManager(), new String[]{"闹钟提醒", "贴心提醒"});
        mViewPager.setAdapter(mReminderAdapter);
        mTabLayout.setupWithViewPager(mViewPager);
    }

    public class ReminderAdapter extends FragmentPagerAdapter {
        private String[] tabs;

        public ReminderAdapter(FragmentManager fm, String[] tabs) {
            super(fm);
            this.tabs = tabs;
        }

        @Override
        public Fragment getItem(int position) {
            Fragment fragment = null;
            switch (position) {
                case 0:
                    fragment = new AlarmFragment();
                    break;
                case 1:
                    fragment = new WarmFragment();
                    break;
            }
            return fragment;
        }

        @Override
        public int getCount() {
            if (tabs != null) {
                return tabs.length;
            }
            return 0;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return tabs[position];
        }
    }
}
