package com.video.wimp;

import android.os.Bundle;
import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockPreferenceActivity;
import com.actionbarsherlock.view.MenuItem;

public class UserSettingActivity extends SherlockPreferenceActivity {
    ActionBar actionbar;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        actionbar = getSupportActionBar();
        addPreferencesFromResource(R.layout.activity_setting);
        actionbar.setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // back to previous page
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
