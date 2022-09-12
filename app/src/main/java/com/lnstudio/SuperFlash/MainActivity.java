package com.lnstudio.SuperFlash;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.RelativeLayout;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.preference.PreferenceManager;

import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.onesignal.OneSignal;
import com.warkiz.widget.IndicatorSeekBar;
import com.warkiz.widget.OnSeekChangeListener;
import com.warkiz.widget.SeekParams;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private static final int CAMERA_REQUEST = 123;
    public static Boolean switchPref, flashback, onStartFlash, darkBack;
    Float darkness;
    RelativeLayout whiteFlashScreen, mainLayout;
    //SeekBar seekBar;
    IndicatorSeekBar seekBar;
    private InterstitialAd mInterstitialAd;
    AdRequest adRequest;
    //BillingProcessor bp;
    String adStatus;
    Button btnBlinkFlashLight, whiteFlash, rate, option, colorLight;
    ImageButton btnFlashLight;
    AdView adView;
    MediaPlayer mp;
    SharedPreferences sharedPref;
    boolean hasCameraFlash = false;
    boolean blinkNum = false;
    boolean flash = false;
    boolean blinkFlash = false;
    int whiteNum = 0;
    long blinkDelay = 200; //Delay in ms
    FirebaseAnalytics mFirebaseAnalytics;

    DrawerLayout drawer;
    private boolean doubleBackToExitPressedOnce = false;

    @SuppressLint("MissingPermission")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setupDrawer();
        btnFlashLight = (ImageButton) findViewById(R.id.btnFlashLightToggle);
        btnBlinkFlashLight = (Button) findViewById(R.id.btnBlinkFlashLight);
        whiteFlash = (Button) findViewById(R.id.whiteflash);
        whiteFlashScreen = (RelativeLayout) findViewById(R.id.whiteflashscreen);
        rate = (Button) findViewById(R.id.rate);
        option = (Button) findViewById(R.id.option);
        colorLight = (Button) findViewById(R.id.colorlight);


        mainLayout = (RelativeLayout) findViewById(R.id.mainLayout);

        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        switchPref = sharedPreferences.getBoolean(SettingsActivity.KEY_PREF_switch, false);
        onStartFlash = sharedPreferences.getBoolean(SettingsActivity.KEY_PREF_OpenFlash, false);
        flashback = sharedPreferences.getBoolean(SettingsActivity.KEY_PREF_ExitFlash, false);
        darkBack = sharedPreferences.getBoolean(SettingsActivity.KEY_PREF_DarkBack, false);


        PreferenceManager.setDefaultValues(this, R.xml.main_preference, false);

        OneSignal.startInit(this)
                .inFocusDisplaying(OneSignal.OSInFocusDisplayOption.Notification)
                .unsubscribeWhenNotificationsAreDisabled(true)
                .init();

        sharedPref = MainActivity.this.getPreferences(Context.MODE_PRIVATE);
        adStatus = sharedPref.getString("adremovecode", "defaultValue");

        adView = (AdView) findViewById(R.id.adView);
        adRequest = new AdRequest.Builder().build();
        adView.loadAd(adRequest);

        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(@NonNull InitializationStatus initializationStatus) {

            }
        });
        InterstitialAd.load(this, "getResources().getString(R.string.inter_banner_ad)", adRequest,
                new InterstitialAdLoadCallback() {
                    @Override
                    public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {
                        mInterstitialAd = interstitialAd;
                        Log.i("TAG", "onAddLoaded");

                        mInterstitialAd.setFullScreenContentCallback(new FullScreenContentCallback() {

                            @Override
                            public void onAdDismissedFullScreenContent() {
                                Log.d("TAG", "Ad dismissed fullscreen content.");
                                mInterstitialAd = null;
                            }

                            @Override
                            public void onAdFailedToShowFullScreenContent(@NonNull AdError adError) {
                                Log.e("TAG", "Ad failed to show fullscreen content.");
                                mInterstitialAd = null;
                            }

                            @Override
                            public void onAdShowedFullScreenContent() {
                                Log.d("TAG", "Ad showed fullscreen content.");
                            }


                        });

                    }

                    @Override
                    public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                        Log.d("TAG", loadAdError.toString());
                        mInterstitialAd = null;
                    }

                });

        seekBar = findViewById(R.id.seekBar);
        seekBar.setMax(5);
        seekBar.setProgress(3);
        seekBar.setOnSeekChangeListener(new OnSeekChangeListener() {
            @Override
            public void onSeeking(SeekParams seekParams) {
                int i = seekParams.progress;
                if (i == 3) {
                    blinkDelay = 200;
                } else if (i == 1) {
                    blinkDelay = 1000;
                } else if (i == 2) {
                    blinkDelay = 600;
                } else if (i == 4) {
                    blinkDelay = 100;
                } else if (i == 0) {
                    blinkDelay = 2000;
                } else {
                    blinkDelay = 30;
                }
            }

            @Override
            public void onStartTrackingTouch(IndicatorSeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(IndicatorSeekBar seekBar) {
            }
        });


        /*if (!adstatus.equals("ps123")) {
            mInterstitialAd = new InterstitialAd(this);
            mInterstitialAd.setAdUnitId(getResources().getString(R.string.inter_banner_ad));
            AdRequest adRequestInter = new AdRequest.Builder().build();
            mInterstitialAd.setAdListener(new AdListener() {
                @Override
                public void onAdLoaded() {
                    mInterstitialAd.show();
                }

            });
            mInterstitialAd.loadAd(adRequestInter);
        }*/

        if(mInterstitialAd != null){
            mInterstitialAd.show(MainActivity.this);
        }
        else{
            Log.d("TAG", "The interstitial ad wasn't ready yet.");
        }


        ActivityCompat.requestPermissions(MainActivity.this,
                new String[]{Manifest.permission.CAMERA}, CAMERA_REQUEST);

        hasCameraFlash = getPackageManager().
                hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH);

        mp = MediaPlayer.create(this, R.raw.beep2);

        if (onStartFlash) {
            flashLightOn();
        }


        if (blinkFlash) {
            blinkFlash = false;
            flash = false;
        }

        btnFlashLight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                    if (hasCameraFlash) {
                        if (!flash) {
                            flashLightOn();
                            flash = true;
                        } else {
                            flashLightOff();
                            flash = false;
                            blinkFlash = false;
                        }
                    } else {
                        Toast.makeText(MainActivity.this, "No flash available on your device",
                                Toast.LENGTH_SHORT).show();
                    }
                }

        });

        btnBlinkFlashLight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (!blinkFlash) {
                    blinkFlash = true;
                    flash = true;
                    blinkFlash();
                    btnBlinkFlashLight.setBackgroundResource(R.drawable.morse_on);
                    seekBar.setVisibility(View.VISIBLE);
                } else {
                    blinkOff();
                }

            }
        });

        whiteFlash.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                whitefish();
            }
        });

        rate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String appPackageName = "com.lnstudio.SuperFlashlightApp";

                try {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
                } catch (ActivityNotFoundException anfe) {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://play.google.com/store/apps/details?id=" + appPackageName)));
                }
            }
        });

        option.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showPopup(view);
            }
        });
        colorLight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, ColorsActivity.class));
            }
        });

    }

    private void blinkOff() {
        flash = false;
        blinkFlash = false;
        btnBlinkFlashLight.setBackgroundResource(R.drawable.morse_off);
        seekBar.setVisibility(View.INVISIBLE);
    }

    private void setupDrawer() {

        Toolbar toolbar = findViewById(R.id.toolbar_main);
        setSupportActionBar(toolbar);
        drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    public void showPopup(View v) {
        PopupMenu popup = new PopupMenu(MainActivity.this, v);
        popup.setOnMenuItemClickListener(this::onMenuItemClick);
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.optionsmenu, popup.getMenu());
        popup.show();
    }

    @SuppressLint("ResourceType")
    public boolean onMenuItemClick(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.removead:
                if (adStatus.equals("ps123")) {
                    Toast.makeText(MainActivity.this, "Ads are Already Removed", Toast.LENGTH_SHORT).show();
                } else {
//                    bp.purchase(MainActivity.this, "flash_infinity");
                }
                return true;
            case R.id.moreapps:
                Uri uri = Uri.parse("market://search?q=pub:" + "L.N. studio");
                Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);
                try {
                    startActivity(goToMarket);
                } catch (ActivityNotFoundException e) {
                    startActivity(new Intent(Intent.ACTION_VIEW,
                            Uri.parse("http://play.google.com/store/search?q=pub:" + "L.N. studio")));
                }
                return true;
            case R.id.share:
                Intent sharingIntent = new Intent(Intent.ACTION_SEND);
                sharingIntent.setType("text/plain");
                String shareBody = "Best Free FlashLight app download now. https://play.google.com/store/apps/details?id=" + "com.lnstudio.AndroidLight";
                sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Share App");
                sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
                startActivity(Intent.createChooser(sharingIntent, "Share via"));
                return true;
            case R.id.settings:
                startActivityForResult(new Intent(MainActivity.this, SettingsActivity.class), 1010);

            default:
                return false;
        }
    }


    @SuppressLint("ResourceAsColor")
    private void flashLightOn() {
        CameraManager cameraManager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);

        try {
            String cameraId = cameraManager.getCameraIdList()[0];
            cameraManager.setTorchMode(cameraId, true);
            btnFlashLight.setBackgroundResource(R.drawable.power_on);
            if (switchPref) {
                mp.start();
            }

            flash = true;
        } catch (CameraAccessException e) {
        }
    }

    @SuppressLint("ResourceAsColor")
    private void flashLightOff() {
        CameraManager cameraManager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);
        try {
            String cameraId = cameraManager.getCameraIdList()[0];
            cameraManager.setTorchMode(cameraId, false);
            btnFlashLight.setBackgroundResource(R.drawable.power_off);
            seekBar.setVisibility(View.INVISIBLE);
            flash = false;
            blinkFlash = false;
        } catch (CameraAccessException e) {
        }
    }

    private void blinkFlash() {
        CameraManager cameraManager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);
        final Handler handler = new Handler();
        if (blinkFlash) {
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {

                    //for (int i = 0; i < myString.length(); i++) {
                    if (!blinkNum) {
                        try {
                            String cameraId = cameraManager.getCameraIdList()[0];
                            cameraManager.setTorchMode(cameraId, false);
                            blinkNum = true;
                        } catch (CameraAccessException ignored) {
                        }
                    } else {
                        try {
                            String cameraId = cameraManager.getCameraIdList()[0];
                            cameraManager.setTorchMode(cameraId, true);
                            blinkNum = false;
                        } catch (CameraAccessException ignored) {
                        }
                    }
                    blinkFlash();
                }
            }, blinkDelay);
        } else {
            try {
                String cameraId = cameraManager.getCameraIdList()[0];
                cameraManager.setTorchMode(cameraId, false);
                blinkNum = false;
            } catch (CameraAccessException e) {
            }
        }

    }

    public void whitefish() {
        if (whiteNum == 0) {
            whiteFlash.setBackgroundResource(R.drawable.sos_icon);
            WindowManager.LayoutParams layoutParams = getWindow().getAttributes();
            darkness = layoutParams.screenBrightness;

            whiteFlashScreen.setVisibility(View.VISIBLE);
            WindowManager.LayoutParams layout = getWindow().getAttributes();
            layout.screenBrightness = 1f;
            getWindow().setAttributes(layout);
            whiteNum = 1;
            btnFlashLight.setEnabled(false);
            btnBlinkFlashLight.setEnabled(false);
            colorLight.setEnabled(false);
        } else {
            hideWhiteScreen();
        }
    }

    private void hideWhiteScreen() {
        whiteFlash.setBackgroundResource(R.drawable.sos_icon);
        whiteFlashScreen.setVisibility(View.INVISIBLE);
        WindowManager.LayoutParams layout = getWindow().getAttributes();
        layout.screenBrightness = darkness;
        getWindow().setAttributes(layout);
        whiteNum = 0;
        btnFlashLight.setEnabled(true);
        btnBlinkFlashLight.setEnabled(true);
        colorLight.setEnabled(true);
    }

    @Override
    public void onBackPressed() {

        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        }

        else if (whiteNum ==1) {
            hideWhiteScreen();
        }


        else if (flash) {
            //finish();
            flash = false;
            blinkFlash = false;
            flashLightOff();
            doubleBackToExitPressedOnce();


        } else {

            doubleBackToExitPressedOnce();

        }
    }

    private void doubleBackToExitPressedOnce() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            return;
        }

        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, "Please click BACK again to exit", Toast.LENGTH_SHORT).show();

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                doubleBackToExitPressedOnce=false;
            }
        }, 2000);

    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case CAMERA_REQUEST:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    hasCameraFlash = getPackageManager().
                            hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH);
                } else {
                    btnFlashLight.setEnabled(false);
                    btnBlinkFlashLight.setEnabled(false);
                    Toast.makeText(MainActivity.this, "Permission Denied for the Camera", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull @org.jetbrains.annotations.NotNull MenuItem item) {

        int id = item.getItemId();
        ;
        if (id == R.id.nav_rate) {
            rateApp();
        } else if (id == R.id.nav_share) {
            shareApp();

        } else if (id == R.id.nav_privacy) {

            privacyPolicy();

        } else if (id == R.id.moreapps) {

            moreApps();

        }
        return false;
    }

    private void privacyPolicy() {
        String url = "https://listinfoworld.blogspot.com/2018/02/ln-studio-privacy-policy.html?zx=2e50aa2e7c1e8e45";
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setData(Uri.parse(url));
        startActivity(i);
    }

    private void shareApp() {
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT,
                "Hey check out ColorBlink Flash Light app at: https://play.google.com/store/apps/details?id=" + "com.lnstudio.SuperFlash");
        sendIntent.setType("text/plain");
        startActivity(sendIntent);
    }

    private void rateApp() {
        try {
            Uri marketUri = Uri.parse("market://details?id=" + "com.lnstudio.SuperFlash");
            Intent marketIntent = new Intent(Intent.ACTION_VIEW, marketUri);
            startActivity(marketIntent);
        }catch(ActivityNotFoundException e) {
            Uri marketUri = Uri.parse("https://play.google.com/store/apps/details?id=" + "com.lnstudio.SuperFlash");
            Intent marketIntent = new Intent(Intent.ACTION_VIEW, marketUri);
            startActivity(marketIntent);
        }
    }
    private void moreApps() {
        Uri uri = Uri.parse("market://search?q=pub:" + "L.N. studio");
        Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);
        try {
            startActivity(goToMarket);
        } catch (ActivityNotFoundException e) {
            startActivity(new Intent(Intent.ACTION_VIEW,
                    Uri.parse("http://play.google.com/store/search?q=pub:" + "L.N. studio")));
        }
    }


}