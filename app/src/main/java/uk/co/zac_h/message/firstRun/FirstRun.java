package uk.co.zac_h.message.firstRun;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import uk.co.zac_h.message.R;
import uk.co.zac_h.message.firstRun.pages.BackupAd;
import uk.co.zac_h.message.firstRun.pages.CustomiseAd;
import uk.co.zac_h.message.firstRun.pages.MessageAd;

public class FirstRun extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first_run);

        ViewPager viewPager = (ViewPager) findViewById(R.id.view_pager);
        FragmentPagerAdapter fragmentPagerAdapter = new FragmentPagerAdapter(getSupportFragmentManager()) {
            @Override
            public Fragment getItem(int position) {
                switch (position) {
                    case 0:
                        return new MessageAd();
                    case 1:
                        return new CustomiseAd();
                    case 2:
                        return new BackupAd();
                    default:
                        return null;
                }
            }

            @Override
            public int getCount() {
                return 2;
            }

            @Override
            public CharSequence getPageTitle(int position) {
                switch (position) {
                    case 0:
                        return "Messaging Improved";
                    case 1:
                        return "Customize";
                    case 2:
                        return "Backup";
                    default:
                        return null;
                }
            }
        };

        viewPager.setAdapter(fragmentPagerAdapter);
    }

}
