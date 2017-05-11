package com.example.lab4.lab4android;

import android.Manifest;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.lab4.lab4android.utils.BTUtils;
import com.google.firebase.iid.FirebaseInstanceId;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.listener.single.PermissionListener;
import com.karumi.dexter.listener.single.SnackbarOnDeniedPermissionListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MyActivity";
    String token;
    @BindView(R.id.container) ViewPager viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // If BLE support isn't there, quit the app
        BTUtils.checkBluetoothSupport(this);
        setContentView(R.layout.activity_main);

        ViewGroup rootView = (ViewGroup) findViewById(android.R.id.content);
        System.out.println("main activity");


        //generating a registration token which is unique to the device

       /* token= FirebaseInstanceId.getInstance().getToken();
        System.out.println("token is "+token);
        Log.w(TAG,"token is "+token);*/
        //Toast.makeText(MainActivity.this,token,Toast.LENGTH_SHORT).show();
        PermissionListener snackbarPermissionListener =
                SnackbarOnDeniedPermissionListener.Builder
                        .with(rootView, "Location is needed to detect BLE devices near you")
                        .withOpenSettingsButton("Settings")
                        .withCallback(new Snackbar.Callback() {
                            @Override
                            public void onShown(Snackbar snackbar) {
                                // Event handler for when the given Snackbar is visible
                            }
                            @Override
                            public void onDismissed(Snackbar snackbar, int event) {
                                // Event handler for when the given Snackbar has been dismissed
                            }
                        }).build();

        Dexter.withActivity(this)
                .withPermission(Manifest.permission.ACCESS_FINE_LOCATION)
                .withListener(snackbarPermissionListener)
                .check();

        ButterKnife.bind(this);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);



        setUpViewPager();
    }

    private void setUpViewPager() {
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);
        System.out.println("setupviewpager");
        Log.w(TAG,"setupviewpager");
        SectionsPagerAdapter sectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
        sectionsPagerAdapter.addFragment(new ScannerFragment(), getString(R.string.scanner));
       // sectionsPagerAdapter.addFragment(new EventLoggerFragment(), getString(R.string.event_log));
        viewPager.setAdapter(sectionsPagerAdapter);
    }

    @Override
    public void onPause() {
        super.onPause();
        //BTUtils.stopScanning(this);
    }
    private class SectionsPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> fragmentList = new ArrayList<>();
        private final List<String> fragmentTitleList = new ArrayList<>();

         void addFragment(Fragment fragment, String title) {
            fragmentList.add(fragment);
            fragmentTitleList.add(title);
        }

        SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {return fragmentList.get(position); }

        @Override
        public int getCount() { return fragmentList.size(); }

        @Override
        public CharSequence getPageTitle(int position) { return fragmentTitleList.get(position); }
    }

}
