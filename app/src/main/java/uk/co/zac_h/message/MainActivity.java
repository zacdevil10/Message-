package uk.co.zac_h.message;

import android.Manifest;
import android.animation.LayoutTransition;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Telephony;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.appcompat.widget.SearchView;
import android.view.View;
import com.google.android.material.navigation.NavigationView;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.LinearLayout;
import android.widget.TextView;

import uk.co.zac_h.message.conversations.ConversationsFragment;

public class MainActivity extends AppCompatActivity {

    private final ConversationsFragment conversationsFragment = new ConversationsFragment();

    private static final int GET_PERMS = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //First run, open intro
        //firstRun();
        if (!Telephony.Sms.getDefaultSmsPackage(this).equals(this.getPackageName())) {
            Intent intent = new Intent(Telephony.Sms.Intents.ACTION_CHANGE_DEFAULT);
            intent.putExtra(Telephony.Sms.Intents.EXTRA_PACKAGE_NAME, this.getPackageName());
            startActivity(intent);
        }

        getPermissions();
        //getContactPerms();

        //Set the action bar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        final DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);

        navigationView.getMenu().getItem(0).setChecked(true);
        View header = navigationView.getHeaderView(0);

        //Change username in header of navigation view
        TextView usernameHeader = (TextView) header.findViewById(R.id.username);
        //TODO: Ask for username input on first opening of app
        usernameHeader.setText("Unknown");

        getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, conversationsFragment).commit();
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                item.setChecked(true);
                drawer.closeDrawers();

                //Switch fragments on nav view item selected
                switch (item.getItemId()) {
                    case R.id.nav_conversations:
                        getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, conversationsFragment).commit();
                        return true;
                    case R.id.nav_archive:
                        return true;
                    case R.id.nav_schedule:
                        return true;
                    case R.id.nav_invite:
                        return true;
                    case R.id.nav_settings:
                        item.setChecked(false);
                        return true;
                    case R.id.nav_blacklist:
                        item.setChecked(false);
                        return true;
                    case R.id.nav_feedback:
                        item.setChecked(false);
                        Intent emailIntent = new Intent(Intent.ACTION_SENDTO);
                        emailIntent.setType("text/plain");
                        emailIntent.setData(Uri.parse("mailto:support@appsbystudio.co.uk"));
                        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Message+ Feedback");
                        if (emailIntent.resolveActivity(getPackageManager()) != null) {
                            startActivity(Intent.createChooser(emailIntent, "Send email via"));
                        } else {
                            //TODO: Make toast.
                            System.out.println("No email applications found on this device!");
                        }
                        return true;
                    case R.id.nav_info:
                        item.setChecked(false);
                        return true;
                }

                return true;
            }
        });
    }

    private void getPermissions() {
        //Make sure we don't have permissions yet
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_SMS) + ContextCompat.checkSelfPermission(this, Manifest.permission.READ_SMS) != PackageManager.PERMISSION_GRANTED) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (shouldShowRequestPermissionRationale(Manifest.permission.READ_SMS)) {

                }
                //If we don't have permission, request permissions
                requestPermissions(new String[]{Manifest.permission.READ_SMS, Manifest.permission.READ_CONTACTS}, GET_PERMS);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case GET_PERMS:
                if (grantResults.length > 0 && grantResults[0] + grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                    //If we are given permission, get messages and refresh inbox
                }
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("fragment", 1);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);

        final MenuItem search = menu.findItem(R.id.search);
        SearchView searchView = (SearchView) search.getActionView();
        LinearLayout searchBar = (LinearLayout) searchView.findViewById(R.id.search_bar);
        searchBar.setLayoutTransition(new LayoutTransition());

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();



        return super.onOptionsItemSelected(item);
    }



}
