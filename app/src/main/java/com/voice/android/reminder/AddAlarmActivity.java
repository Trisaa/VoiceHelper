package com.voice.android.reminder;

import android.content.Context;
import android.content.Intent;
import android.view.Menu;
import android.view.MenuItem;

import com.voice.android.R;
import com.voice.android.common.base.BaseActivity;

/**
 * Created by LeBron on 2017/5/2.
 */

public class AddAlarmActivity extends BaseActivity {

    public static void start(Context context) {
        Intent intent = new Intent(context, AddAlarmActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected int getContentViewResId() {
        return R.layout.activity_add_alarm;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.reminder_add_alarm_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_ok) {

        }
        return super.onOptionsItemSelected(item);
    }
}
