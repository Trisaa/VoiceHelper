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
import com.voice.android.reminder.HistoryActivity;
import com.voice.android.reminder.ReminderFragment;
import com.voice.android.translate.TranslateFragment;

import org.greenrobot.eventbus.EventBus;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import rx.functions.Action1;

public class MainActivity extends BaseActivity {
    public static final String EVENT_CLEAR_TRANSLATE_DATA = "EVENT_CLEAR_TRANSLATE_DATA";
    private static final String[] TOOLBAR_TITLES = new String[]{"提醒", "速记", "翻译"};
    @BindView(R.id.main_content_bottombar)
    BottomNavigationView mBottomBar;
    @BindView(R.id.main_content_viewpager)
    ViewPager mViewPager;

    private MainPagerAdapter mMainPagerAdapter;
    private Menu mMenu;
    private MenuItem prevMenuItem;

    private BottomNavigationView.OnNavigationItemSelectedListener mListener = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.main_bottom_nav_home:
                    mViewPager.setCurrentItem(0);
                    mMenu.getItem(1).setVisible(true);
                    mMenu.getItem(2).setVisible(false);
                    mMenu.getItem(3).setVisible(false);
                    mToolbar.setTitle(TOOLBAR_TITLES[0]);
                    break;
                case R.id.main_bottom_nav_subscribe:
                    mViewPager.setCurrentItem(1);
                    mMenu.getItem(2).setVisible(true);
                    mMenu.getItem(1).setVisible(false);
                    mMenu.getItem(3).setVisible(false);
                    mToolbar.setTitle(TOOLBAR_TITLES[1]);
                    break;
                case R.id.main_bottom_nav_account:
                    mViewPager.setCurrentItem(2);
                    mMenu.getItem(3).setVisible(true);
                    mMenu.getItem(1).setVisible(false);
                    mMenu.getItem(2).setVisible(false);
                    mToolbar.setTitle(TOOLBAR_TITLES[2]);
                    break;
            }
            return true;
        }
    };

    private ViewPager.OnPageChangeListener mChangeListener = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        @Override
        public void onPageSelected(int position) {
            mToolbar.setTitle(TOOLBAR_TITLES[position]);
            for (int i = 0; i < TOOLBAR_TITLES.length; i++) {
                if (i == position) {
                    mMenu.getItem(i + 1).setVisible(true);
                } else {
                    mMenu.getItem(i + 1).setVisible(false);
                }
            }
            if (prevMenuItem != null) {
                prevMenuItem.setChecked(false);
            } else {
                mBottomBar.getMenu().getItem(0).setChecked(false);
            }
            mBottomBar.getMenu().getItem(position).setChecked(true);
            prevMenuItem = mBottomBar.getMenu().getItem(position);
        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        checkPermission();
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        getSupportActionBar().setTitle(TOOLBAR_TITLES[0]);

        mMainPagerAdapter = new MainPagerAdapter(getSupportFragmentManager());
        mViewPager.setAdapter(mMainPagerAdapter);
        mViewPager.setOffscreenPageLimit(2);
        mViewPager.addOnPageChangeListener(mChangeListener);
        mBottomBar.setOnNavigationItemSelectedListener(mListener);
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
            HistoryActivity.start(this);
        } else if (item.getItemId() == R.id.action_add) {
            new RxPermissions(MainActivity.this).request(Manifest.permission.RECORD_AUDIO).subscribe(new Action1<Boolean>() {
                @Override
                public void call(Boolean grant) {
                    if (grant) {
                        AddNoteActivity.start(MainActivity.this);
                    } else {
                        Toast.makeText(MainActivity.this, "未获取所需权限", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        } else if (item.getItemId() == R.id.action_clear) {
            EventBus.getDefault().post(EVENT_CLEAR_TRANSLATE_DATA);
        }else if(item.getItemId() == R.id.action_voice){
            VoiceControlActivity.start(this);
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
            return TOOLBAR_TITLES.length;
        }

        public Fragment getFragment(int key) {
            return mPageReferenceMap.get(key);
        }
    }


    private void checkPermission() {
        new RxPermissions(this).request(Manifest.permission.WRITE_EXTERNAL_STORAGE).subscribe(new Action1<Boolean>() {
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
