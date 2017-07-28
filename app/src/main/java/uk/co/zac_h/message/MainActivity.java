package uk.co.zac_h.message;

import android.Manifest;
import android.animation.LayoutTransition;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.SearchView;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.LinearLayout;
import android.widget.TextView;

import uk.co.zac_h.message.conversations.ConversationsFragment;
import uk.co.zac_h.message.database.MessageSync;
import uk.co.zac_h.message.database.ReturnData;

public class MainActivity extends AppCompatActivity {

    private ProgressDialog progressDialog;

    private final ConversationsFragment conversationsFragment = new ConversationsFragment();

    private static final int READ_SMS_PERM_REQUEST = 1;
    private static final int READ_CONTACTS_PERM_REQUEST = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //First run, open intro
        //firstRun();

        getSmsPermissions();
        getContactPerms();

        //Set the action bar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        final DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);

        navigationView.getMenu().getItem(0).setChecked(true);
        View header = navigationView.getHeaderView(0);

        //Change username in header of navigation view
        TextView usernameHeader = (TextView) header.findViewById(R.id.username);
        //TODO: Ask for username input on first opening of app
        usernameHeader.setText("Unknown");

        getSupportFragmentManager().beginTransaction().add(R.id.content_frame, conversationsFragment).commit();
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

        progressDialog = new ProgressDialog(this, R.style.LoadingTheme);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Loading messages...");
    }

    public void firstRun() {
        if (new ReturnData().isProfileEmpty(this) == 0 || new ReturnData().firstRun(this) == 0) {
            //Intent intent = new Intent(this, FirstRun.class);
            //startActivity(intent);
            new MessageSync(this, progressDialog).execute();
            new ReturnData().setFirstRun(this);
        } else {
            System.out.println("Already done first run!");
        }
    }

    public void getSmsPermissions() {
        //Make sure we don't have permissions yet
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_SMS) != PackageManager.PERMISSION_GRANTED) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (shouldShowRequestPermissionRationale(Manifest.permission.READ_SMS)) {

                }
                //If we don't have permission, request permissions
                requestPermissions(new String[]{Manifest.permission.READ_SMS}, READ_SMS_PERM_REQUEST);
            }
        }
    }

    public void getContactPerms() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (shouldShowRequestPermissionRationale(Manifest.permission.READ_CONTACTS)) {

                }
                //If we don't have permission, request permissions
                requestPermissions(new String[]{Manifest.permission.READ_CONTACTS}, READ_CONTACTS_PERM_REQUEST);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case READ_SMS_PERM_REQUEST:
                if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //If we are given permission, get messages and refresh inbox
                    //Run first run setup
                    //firstRun();
                }
                return;
            case READ_CONTACTS_PERM_REQUEST:
                if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //If we are given permission, get messages and refresh inbox
                    //Run first run setup
                    firstRun();
                }
        }
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
