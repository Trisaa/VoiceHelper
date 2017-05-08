package com.voice.android;

import android.Manifest;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.Toast;

import com.tbruyelle.rxpermissions.RxPermissions;
import com.voice.android.common.base.BaseActivity;
import com.voice.android.quicknote.AddNoteActivity;
import com.voice.android.quicknote.NoteFragment;
import com.voice.android.reminder.ReminderFragment;
import com.voice.android.translate.TranslateFragment;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import rx.functions.Action1;

public class MainActivity extends BaseActivity {
    @BindView(R.id.main_content_bottombar)
    BottomNavigationView mBottomBar;
    @BindView(R.id.main_content_viewpager)
    ViewPager mViewPager;

    private MainPagerAdapter mMainPagerAdapter;
    private Menu mMenu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        mToolbar.setTitle("提醒");
        mMainPagerAdapter = new MainPagerAdapter(getSupportFragmentManager());
        mViewPager.setAdapter(mMainPagerAdapter);
        mViewPager.setOffscreenPageLimit(2);
        mBottomBar.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.main_bottom_nav_home:
                        mViewPager.setCurrentItem(0);
                        mMenu.getItem(0).setVisible(false);
                        mMenu.getItem(1).setVisible(false);
                        mMenu.getItem(2).setVisible(false);
                        mToolbar.setTitle("提醒");
                        break;
                    case R.id.main_bottom_nav_subscribe:
                        mViewPager.setCurrentItem(1);
                        mMenu.getItem(1).setVisible(true);
                        mMenu.getItem(0).setVisible(false);
                        mMenu.getItem(2).setVisible(false);
                        mToolbar.setTitle("速记");
                        break;
                    case R.id.main_bottom_nav_account:
                        mViewPager.setCurrentItem(2);
                        mMenu.getItem(2).setVisible(true);
                        mMenu.getItem(0).setVisible(false);
                        mMenu.getItem(1).setVisible(false);
                        mToolbar.setTitle("翻译");
                        break;
                }
                return true;
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_toolbar_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        mMenu = menu;
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_history) {

        } else if (item.getItemId() == R.id.action_add) {
            AddNoteActivity.start(this);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected int getContentViewResId() {
        return R.layout.activity_main;
    }

    private class MainPagerAdapter extends FragmentStatePagerAdapter {
        private Map<Integer, Fragment> mPageReferenceMap = new HashMap<>();

        public MainPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            Fragment fragment = null;
            switch (position) {
                case 0:
                    fragment = new ReminderFragment();
                    break;
                case 1:
                    fragment = new NoteFragment();
                    break;
                case 2:
                    fragment = new TranslateFragment();
                    break;
                default:
                    break;
            }
            mPageReferenceMap.put(position, fragment);
            return fragment;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            super.destroyItem(container, position, object);
            mPageReferenceMap.remove(position);
        }

        @Override
        public int getCount() {
            return 3;
        }

        public Fragment getFragment(int key) {
            return mPageReferenceMap.get(key);
        }
    }


    private void checkPermission() {
        new RxPermissions(this).request(Manifest.permission.RECORD_AUDIO).subscribe(new Action1<Boolean>() {
            @Override
            public void call(Boolean grant) {
                if (grant) {

                } else {
                    Toast.makeText(MainActivity.this, "未获取所需权限", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
