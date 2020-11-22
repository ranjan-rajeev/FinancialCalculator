package com.financialcalculator.home;

import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.financialcalculator.PrefManager.SharedPrefManager;
import com.financialcalculator.R;
import com.financialcalculator.about.AboutFragment;
import com.financialcalculator.firebase.FirebaseHelper;
import com.financialcalculator.model.MoreInfoEntity;
import com.financialcalculator.newdashboard.NewDashBoardFragment;
import com.financialcalculator.roomdb.RoomDatabase;
import com.financialcalculator.utility.BaseActivity;
import com.financialcalculator.utility.Logger;
import com.financialcalculator.utility.Util;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.play.core.appupdate.AppUpdateManager;
import com.google.android.play.core.appupdate.AppUpdateManagerFactory;
import com.google.android.play.core.install.InstallState;
import com.google.android.play.core.install.InstallStateUpdatedListener;
import com.google.android.play.core.install.model.AppUpdateType;
import com.google.android.play.core.install.model.InstallStatus;
import com.google.android.play.core.install.model.UpdateAvailability;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends BaseActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    boolean doubleBackToExitPressedOnce = false;

    private static final int REQUEST_UPDATE = 11;
    private static boolean updatePopUpShown = false;
    ValueEventListener moreinfoEventListener = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
            Logger.d("Moreinfo firebase  onData");
            List<MoreInfoEntity> list = new ArrayList<>();
            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                try {
                    list.add(snapshot.getValue(MoreInfoEntity.class));
                } catch (Exception e) {
                    Logger.d("Unable to parse");
                }
            }
            if (list != null) {
                new StoreMoreInfo(list).execute();
            }
        }

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {
            Logger.d(databaseError.getMessage());
        }
    };
    private AppUpdateManager mAppUpdateManager;
    InstallStateUpdatedListener installStateUpdatedListener = new
            InstallStateUpdatedListener() {
                @Override
                public void onStateUpdate(InstallState state) {
                    if (state.installStatus() == InstallStatus.DOWNLOADED) {
                        //CHECK THIS if AppUpdateType.FLEXIBLE, otherwise you can skip
                        popupSnackbarForCompleteUpdate();
                    } else if (state.installStatus() == InstallStatus.INSTALLED) {
                        if (mAppUpdateManager != null) {
                            mAppUpdateManager.unregisterListener(installStateUpdatedListener);
                        }
                    } else {
                        Logger.d("InstallStateUpdatedListener: state: " + state.installStatus());
                    }
                }
            };
    private SharedPrefManager sharedPrefManager;
    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private RoomDatabase roomDatabase;

    @Override
    protected void onStart() {
        super.onStart();
        mAppUpdateManager = AppUpdateManagerFactory.create(this);
        mAppUpdateManager.registerListener(installStateUpdatedListener);

        mAppUpdateManager.getAppUpdateInfo().addOnSuccessListener(appUpdateInfo -> {
            if (appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE
                    || appUpdateInfo.updateAvailability() == UpdateAvailability.DEVELOPER_TRIGGERED_UPDATE_IN_PROGRESS
                    && appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.FLEXIBLE /*AppUpdateType.IMMEDIATE*/)) {

                Snackbar.make(findViewById(R.id.fab), "Let us update your app in background while you continue exploring the app !!!", Snackbar.LENGTH_INDEFINITE)
                        .setAction("Update", v -> {
                            try {
                                mAppUpdateManager.startUpdateFlowForResult(
                                        appUpdateInfo, AppUpdateType.FLEXIBLE /*AppUpdateType.IMMEDIATE*/, MainActivity.this, REQUEST_UPDATE);
                            } catch (IntentSender.SendIntentException e) {
                                e.printStackTrace();
                            }
                        }).show();
            }
            if (appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE
                    || appUpdateInfo.updateAvailability() == UpdateAvailability.DEVELOPER_TRIGGERED_UPDATE_IN_PROGRESS
                    && appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.IMMEDIATE /*AppUpdateType.IMMEDIATE*/)) {

                try {
                    mAppUpdateManager.startUpdateFlowForResult(
                            appUpdateInfo, AppUpdateType.IMMEDIATE /*AppUpdateType.IMMEDIATE*/, MainActivity.this, REQUEST_UPDATE);

                } catch (IntentSender.SendIntentException e) {
                    e.printStackTrace();
                }

            } else if (appUpdateInfo.installStatus() == InstallStatus.DOWNLOADED) {
                //CHECK THIS if AppUpdateType.FLEXIBLE, otherwise you can skip
                if (appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.FLEXIBLE))
                    popupSnackbarForCompleteUpdate();
            } else {
                Logger.d("checkForAppUpdateAvailability: something else");
            }
        });
    }

    private void popupSnackbarForCompleteUpdate() {
        Snackbar snackbar = Snackbar.make(findViewById(R.id.fab),
                "New app is ready!", Snackbar.LENGTH_INDEFINITE);

        snackbar.setAction("Install", view -> {
            if (mAppUpdateManager != null) {
                mAppUpdateManager.completeUpdate();
            }
        });
        //snackbar.setActionTextColor(getResources().getColor(R.color.install_color));
        snackbar.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_UPDATE) {
            if (resultCode != RESULT_OK) {
                Logger.d("onActivityResult: app download failed");
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!updatePopUpShown) {
            checkFirebaseConfigUpdate();
        }
    }

    private void checkFirebaseConfigUpdate() {
        if (Util.getVersionCode(this) < FirebaseHelper.getServerVersionCode()) {
            if (FirebaseHelper.getForceUpdate()) {
                showAppUpdateAlert(false);
            } else {
                updatePopUpShown = true;
                showAppUpdateAlert(true);
            }
        }
    }

    private void showAppUpdateAlert(boolean b) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        AlertDialog alertDialog = builder.create();
        builder.setTitle("Update Finance Calculator ?");
        builder.setMessage("Finance Calculator recommends that you update app to the latest version for better performance.");
        builder.setCancelable(b);
        builder.setPositiveButton("Update", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                final String appPackageName = MainActivity.this.getPackageName(); // getPackageName() from Context or Activity object
                try {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
                } catch (android.content.ActivityNotFoundException anfe) {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
                }
            }
        });
        if (b) {
            builder.setNegativeButton("Later", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    alertDialog.dismiss();
                }
            });
        }
        builder.show();
    }

    private void showDashboard() {
        getSupportActionBar().setTitle("HOME");
        Fragment fragment = new NewDashBoardFragment();
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.frame, fragment);
        fragmentTransaction.commit();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            if (doubleBackToExitPressedOnce) {
                super.onBackPressed();
                return;
            }

            this.doubleBackToExitPressedOnce = true;
            Toast.makeText(this, "Please click BACK again to exit", Toast.LENGTH_SHORT).show();

            new Handler().postDelayed(new Runnable() {

                @Override
                public void run() {
                    doubleBackToExitPressedOnce = false;
                }
            }, 2000);
        }
    }

    /*@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }*/

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(final MenuItem item) {

        // Handle navigation view item clicks here.
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                switch (item.getItemId()) {
                    case R.id.nav_home:
                        getSupportActionBar().setTitle("HOME");
                        loadFragment(new NewDashBoardFragment());
                        break;
                    case R.id.nav_about:
                        getSupportActionBar().setTitle("About");
                        loadFragment(new AboutFragment());
                        break;
                    case R.id.nav_share:
                        shareWhatsApp();
                        break;
                    case R.id.nav_rate:
                        launchMarket();
                        break;
                }
            }
        }, 200);


        return true;
    }

    private void launchMarket() {
        Uri uri = Uri.parse("market://details?id=" + getPackageName());
        Intent myAppLinkToMarket = new Intent(Intent.ACTION_VIEW, uri);
        try {
            startActivity(myAppLinkToMarket);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(this, " unable to find market app", Toast.LENGTH_LONG).show();
        }
    }

    private void shareWhatsApp() {
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.setPackage("com.whatsapp");
        sendIntent.putExtra(Intent.EXTRA_TEXT, "https://play.google.com/store/apps/details?id=com.horizonlabs.financialcalculator");
        sendIntent.setType("text/plain");
        if (sendIntent.resolveActivity(getPackageManager()) != null)
            startActivity(sendIntent);
        else {
            sendIntent = new Intent();
            sendIntent.setAction(Intent.ACTION_SEND);
            sendIntent.putExtra(Intent.EXTRA_TEXT, "https://play.google.com/store/apps/details?id=com.horizonlabs.financialcalculator");
            sendIntent.setType("text/plain");
            startActivity(sendIntent);
        }
    }

    public boolean loadFragment(Fragment fragment) {

        if (fragment != null) {
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.frame, fragment);
            fragmentTransaction.commit();
            return true;
        }
        return false;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        showBannerAd();
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        sharedPrefManager = SharedPrefManager.getInstance(this);
        roomDatabase = RoomDatabase.getAppDatabase(this);
       /* if (BuildConfig.FLAVOR.equals("free") && Constants.APP_TYPE == 0) {
            MobileAds.initialize(this, getResources().getString(R.string.ad_app_id));
        }*/

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.setCheckedItem(R.id.nav_home);

        showDashboard();
        new FetchMoreDetails().execute();

    }

    private void fetchMoreInfoFirebase() {
        Logger.d("Fetch moreinfo firebase : ");
        DatabaseReference inputRef = database.getReference(FirebaseHelper.DB_MOREINFO);
        inputRef.addListenerForSingleValueEvent(moreinfoEventListener);
    }

    private class FetchMoreDetails extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            long localCalVersion = sharedPrefManager.getLongValueForKey(FirebaseHelper.MORE_INFO_VERSION, 0L);
            long serverVersion = FirebaseHelper.getMoreInfoVersion();
            if (localCalVersion < serverVersion) {
                fetchMoreInfoFirebase();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void list) {
            super.onPostExecute(list);
        }
    }

    private class StoreMoreInfo extends AsyncTask<Void, Void, Void> {
        private List<MoreInfoEntity> moreInfoEntities;

        public StoreMoreInfo(List<MoreInfoEntity> moreInfoEntities) {
            this.moreInfoEntities = moreInfoEntities;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            roomDatabase.moreInfoDao().insertAll(moreInfoEntities);
            sharedPrefManager.putLongValueForKey(FirebaseHelper.MORE_INFO_VERSION, FirebaseHelper.getMoreInfoVersion());
            return null;
        }

        @Override
        protected void onPostExecute(Void list) {
            super.onPostExecute(list);
        }
    }
}
