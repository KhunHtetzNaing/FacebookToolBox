package com.ngoe.ftk;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.TypefaceSpan;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.webkit.DownloadListener;
import android.webkit.URLUtil;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.ads.Ad;
import com.facebook.ads.AdError;
import com.facebook.ads.AdSize;
import com.facebook.ads.InterstitialAdListener;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

public class Start extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    WebView webView;
    AdRequest adRequest;
    InterstitialAd interstitialAd;
    private com.facebook.ads.InterstitialAd FbinterstitialAd;
    AdView banner;
    String currentURL = "https://";
    ProgressBar progressBar;
    private DownloadManager mDownloadManager;
    private long mDownloadedFileID;
    private DownloadManager.Request mRequest;
    String currentVersion = "1.0",newVersion = "1.0";
    String check = "http://myappserver.blogspot.com/2018/05/facebook-toolkit.html";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("ေဖ့စ္ဘုတ္ကိရိယာ");
        changeToolbarFont(toolbar,this);
        setSupportActionBar(toolbar);

        PackageManager manager = this.getPackageManager();
        PackageInfo info;
        try {
            info = manager.getPackageInfo(getApplicationContext().getPackageName(), 0);
            currentVersion = info.versionName;
            newVersion = info.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        if (new CheckInternet(this).isInternetOn()==true){
            new MyTask().execute();
        }

        mDownloadManager = (DownloadManager) getSystemService(getApplicationContext().DOWNLOAD_SERVICE);
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                share();
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        View headerView = navigationView.getHeaderView(0);
        TextView navTitle = headerView.findViewById(R.id.navTitle);
        TextView navDescription = headerView.findViewById(R.id.navDescription);
        Typeface typeface = Typeface.createFromAsset(getAssets(),"paoh.ttf");
        navTitle.setTypeface(typeface);
        navDescription.setTypeface(typeface);
        navigationView.setNavigationItemSelectedListener(this);
        progressBar = findViewById(R.id.progressBar);

        webView = findViewById(R.id.webView);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.setWebViewClient(new myWeb());

        if (savedInstanceState == null) {
            if (checkInternet()==true){
                webView.loadUrl("https://myfirstapp2018.blogspot.com/p/home.html");
            }
        }

        adRequest = new AdRequest.Builder().build();
        banner = (AdView) findViewById(R.id.adView);
        banner.loadAd(adRequest);

        interstitialAd = new InterstitialAd(this);
        interstitialAd.setAdUnitId("ca-app-pub-1325188641119577/4833957034");
        interstitialAd.loadAd(adRequest);
        interstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdClosed() {
                loadAD();
            }

            @Override
            public void onAdOpened() {
                loadAD();
            }

            @Override
            public void onAdLeftApplication() {
                loadAD();
            }

            @Override
            public void onAdFailedToLoad(int i) {
                loadAD();
            }
        });
        fbAd();

        webView.setDownloadListener(new DownloadListener() {
            @Override
            public void onDownloadStart(String s, String s1, String s2, String s3, long l) {
                String fileName= URLUtil.guessFileName(s,s2,s3);
                Log.d("FileName",fileName);
                dlFile(s,fileName);
            }
        });
    }

    public void dlFile(String url,String fileName){
        try {
            String mBaseFolderPath = android.os.Environment.getExternalStorageDirectory()+ File.separator+ "Download" + File.separator;
            if (!new File(mBaseFolderPath).exists()) {
                new File(mBaseFolderPath).mkdir();
            }

            String mFilePath = "file://" + mBaseFolderPath + "/" + fileName;
            Uri downloadUri = Uri.parse(url);
            mRequest = new DownloadManager.Request(downloadUri);
            mRequest.setDestinationUri(Uri.parse(mFilePath));
            mRequest.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
            mDownloadedFileID = mDownloadManager.enqueue(mRequest);
            IntentFilter filter = new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE);
            registerReceiver(downloadReceiver, filter);
            Toast.makeText(this, "Starting Download", Toast.LENGTH_SHORT).show();
            showAD();
        } catch (Exception e) {
            startActivity(new Intent(Intent.ACTION_VIEW,Uri.parse(url)));
        }
    }

    private BroadcastReceiver downloadReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            //check if the broadcast message is for our enqueued download
            final Uri uri = mDownloadManager.getUriForDownloadedFile(mDownloadedFileID);
            String apk = getRealPathFromURI(uri);
            if (apk.endsWith(".apk")) {
                AlertDialog.Builder builder = new AlertDialog.Builder(Start.this)
                        .setTitle("Download Completed!")
                        .setMessage("Do you want to install "+new File(apk).getName()+"?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                try {
                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                                        Intent intent1 = new Intent(Intent.ACTION_INSTALL_PACKAGE);
                                        intent1.setData(uri);
                                        intent1.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                                        startActivity(intent1);
                                    } else {
                                        Intent intent2 = new Intent(Intent.ACTION_VIEW);
                                        intent2.setDataAndType(uri, "application/vnd.android.package-archive");
                                        intent2.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                        startActivity(intent2);
                                    }
                                } catch (Exception e) {

                                }
                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                            }
                        });
                AlertDialog dialog = builder.create();
                dialog.show();
            }else{
                Toast.makeText(context, "Downloaded : "+new File(apk).getName(), Toast.LENGTH_SHORT).show();
            }
        }
    };

    public String getRealPathFromURI (Uri contentUri) {
        String path = null;
        String[] proj = { MediaStore.MediaColumns.DATA };
        Cursor cursor = getContentResolver().query(contentUri, proj, null, null, null);
        if (cursor.moveToFirst()) {
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);
            path = cursor.getString(column_index);
        }
        cursor.close();
        return path;
    }

    private void share() {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_TEXT,"Myanmar Facebook ToolBox Download Free at Google Play Store : play.google.com/store/apps/details?id="+getPackageName()+"\n\nDirect Download : http://bit.ly/2FLfqG8\n#mmFacebookToolBox");
        startActivity(Intent.createChooser(intent,"Share App Via..."));
    }

    class myWeb extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            Log.d("URL",url);
            if (url.startsWith("https://play.google.com") || url.startsWith("http://play.google.com")){
                Log.d("URL","YES");
                startActivity(new Intent(Intent.ACTION_VIEW,Uri.parse(url)));
                return true;
            }else{
                Log.d("URL","No");
                if (checkInternet()==true){
                    double nInt = Double.parseDouble(newVersion);
                    double cInt = Double.parseDouble(currentVersion);
                    if (newVersion.equals(currentVersion) == true || nInt<cInt) {
                        progressBar.setVisibility(View.VISIBLE);
                        if (url.contains("facebook.com")){
                        view.setVisibility(View.GONE);
                        }
                    }else {
                        newVersionReleased();
                        return true;
                    }
                }else{
                    return true;
                }
            }
            return super.shouldOverrideUrlLoading(view, url);
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            if (checkInternet()==true) {
                view.loadUrl("javascript:(function(){document.getElementsByClassName('_129-')[0].style.display='none'; })()");
                progressBar.setVisibility(View.GONE);
                view.setVisibility(View.VISIBLE);
            }else{
                view.setVisibility(View.GONE);
            }
            currentURL = url;
            if (new CheckInternet(Start.this).isInternetOn()==true){
                new MyTask().execute();
            }
        }
    }

    public boolean checkInternet(){
        boolean what = false;
        CheckInternet checkNet = new CheckInternet(this);
        if (checkNet.isInternetOn()==true){
            what = true;
        }else{
            what = false;
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Attention!");
            builder.setMessage("No internet connection :(");
            builder.setPositiveButton("Try again", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    showAD();
                    if (checkInternet()==true){
                        webView.loadUrl(webView.getUrl());
                    }
                }
            });
            builder.setNegativeButton("Exit", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    showAD();
                    finish();
                }
            });
            AlertDialog dialog = builder.create();
            dialog.show();
        }

        return what;
    }

    @Override
    public void onBackPressed() {
        currentURL = "https://";
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            if (webView.canGoBack()==true){
                webView.goBack();
            }else{
                exitApp();
            }
        }
    }

    private void exitApp() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this)
                .setTitle("Attention!")
                .setMessage("Do you want to exit ?")
                .setIcon(R.drawable.icon)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        showAD();
                        finish();
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        showAD();
                    }
                });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState )
    {
        super.onSaveInstanceState(outState);
        webView.saveState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState)
    {
        super.onRestoreInstanceState(savedInstanceState);
        webView.restoreState(savedInstanceState);
    }

    public void loadAD(){
        if (!interstitialAd.isLoaded()){
            interstitialAd.loadAd(adRequest);
        }
    }

    public void showAD(){
        if (FbinterstitialAd.isAdLoaded()){
            FbinterstitialAd.show();
        }else {
            FbinterstitialAd.loadAd();
            if (interstitialAd.isLoaded()) {
                interstitialAd.show();
            } else {
                interstitialAd.loadAd(adRequest);
            }
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.home) {
            if (checkInternet() == true) {
                webView.loadUrl("https://myfirstapp2018.blogspot.com/p/home.html");
            }
        } else if (id == R.id.password) {
            showAD();
            startActivity(new Intent(this, Key.class));
        } else if (id == R.id.security) {
            if (checkInternet() == true) {
                webView.loadUrl("http://myfirstapp77.blogspot.com/p/security.html");
            }
        } else if (id == R.id.privacy) {
            if (checkInternet() == true) {
                webView.loadUrl("http://myfirstapp77.blogspot.com/p/facebook-privacy.html");
            }
        } else if (id == R.id.other) {
            if (checkInternet() == true) {
                webView.loadUrl("http://myfirstapp77.blogspot.com/p/other-tools.html");
            }
        } else if (id == R.id.about) {
            showAbout();
        }else if (id == R.id.update){
            double nInt = Double.parseDouble(newVersion);
            double cInt = Double.parseDouble(currentVersion);
            if (newVersion.equals(currentVersion) == true || nInt<cInt) {
                AlertDialog.Builder builder = new AlertDialog.Builder(this)
                        .setTitle("Congratulations!")
                        .setMessage("You are on latest version!")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                showAD();
                            }
                        });
                AlertDialog dialog = builder.create();
                dialog.show();
            }else{
                AlertDialog.Builder builder = new AlertDialog.Builder(this)
                        .setTitle("Notice!")
                        .setMessage("New update version "+newVersion+" is released!")
                        .setPositiveButton("Update Now", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                showAD();
                                startActivity(new Intent(Start.this,Updater.class));
                            }
                        });
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        } else if (id == R.id.share) {
            share();
        }else if (id == R.id.feedback) {
            showAD();
            startActivity(new Intent(this,Feedback.class));
        } else if (id == R.id.rate) {
            rate();
        } else if (id == R.id.more) {
            if (checkInternet() == true) {
                webView.loadUrl("http://myfirstapp2.blogspot.com/p/hi.html");
            }
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void rate(){
        ImageView imageView = new ImageView(this);
        imageView.setPadding(5,0,5,0);
        imageView.setImageResource(R.drawable.rateme);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String appPackageName = getPackageName(); // getPackageName() from Context or Activity object
                try {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
                } catch (android.content.ActivityNotFoundException anfe) {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
                }
            }
        });
        AlertDialog.Builder builder = new AlertDialog.Builder(this)
                .setTitle("ံHelp Us")
                .setView(imageView)
                .setPositiveButton("Rate Now", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        showAD();
                        final String appPackageName = getPackageName(); // getPackageName() from Context or Activity object
                        try {
                            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
                        } catch (android.content.ActivityNotFoundException anfe) {
                            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
                        }
                    }
                });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public void showAbout(){
        View view = getLayoutInflater().inflate(R.layout.about,null);
        view.setPadding(20,0,20,0);
        TextView web = view.findViewById(R.id.tvWebDev);
        web.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setData(Uri.parse("fb://profile/100013433347720"));
                    startActivity(intent);
                }catch (Exception e){
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setData(Uri.parse("https://m.facebook.com/GSKhai"));
                    startActivity(intent);
                }
            }
        });

        TextView dev = view.findViewById(R.id.AndroidDev);
        dev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setData(Uri.parse("fb://profile/100011339710114"));
                    startActivity(intent);
                }catch (Exception e){
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setData(Uri.parse("https://m.facebook.com/KHtetzNaing"));
                    startActivity(intent);
                }
            }
        });
        AlertDialog.Builder builder = new AlertDialog.Builder(this)
                .setTitle("About App")
                .setView(view)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        showAD();
                    }
                });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (checkInternet()==false){
            webView.setVisibility(View.GONE);
        }
    }

    public void fbAd(){
        FbinterstitialAd = new com.facebook.ads.InterstitialAd(Start.this, "153175382202625_153178305535666");
        FbinterstitialAd.setAdListener(new InterstitialAdListener() {
            @Override
            public void onInterstitialDisplayed(Ad ad) {
                // Interstitial displayed callback
            }

            @Override
            public void onInterstitialDismissed(Ad ad) {
                FbinterstitialAd.loadAd();
            }

            @Override
            public void onError(Ad ad, AdError adError) {
            }

            @Override
            public void onAdLoaded(Ad ad) {
                // Show the ad when it's done loading.
            }

            @Override
            public void onAdClicked(Ad ad) {
                // Ad clicked callback
                FbinterstitialAd.loadAd();
            }

            @Override
            public void onLoggingImpression(Ad ad) {
                // Ad impression logged callback
            }
        });
        // For auto play video ads, it's recommended to load the ad
        // at least 30 seconds before it is shown
        FbinterstitialAd.loadAd();
    }

    private class MyTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String ... urls) {
            try {
                URL url = new URL(check);
                URLConnection uc = url.openConnection();
                //String j = (String) uc.getContent();
                uc.setDoInput(true);
                BufferedReader in = new BufferedReader(new InputStreamReader(uc.getInputStream()));
                String inputLine;
                StringBuilder a = new StringBuilder();
                while ((inputLine = in.readLine()) != null)
                    a.append(inputLine);
                in.close();

                return a.toString();
            } catch (Exception e) {
                return e.getMessage();
            }
        }

        @Override
        protected void onPostExecute(String result)
        {
            try {
                result = result.substring(result.indexOf("<script type='application/ld+json'>"),result.indexOf("</script>",result.indexOf("<script type='application/ld+json'>")));
                result = result.substring(result.indexOf("description"),result.indexOf("datePublished"));
                result = result.replace("description","");
                result = result.replace("\"","");
                result = result.replace(",","");
                result = result.replace(":","");
                result = result.replace(" ","");
                newVersion = result;
            }catch (Exception e){

            }
        }
    }

    private void newVersionReleased() {
        android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(this)
                .setCancelable(false)
                .setTitle("ERROR!")
                .setIcon(R.drawable.icon)
                .setMessage("ဗားရွင္းအသစ္ထြက္ရွိလို႔လာပါၿပီ။\n" +
                        "လက္ရွိဗားရွင္းကိုအသုံးျပဳ၍မရႏိုင္ေတာ့ပါ။\n" +
                        "ေအာက္ပါ \"ရယူမည္\" ခလုတ္ကိုႏွိပ္ၿပီး\n" +
                        "ဗားရွင္းအသစ္ကိုေဒါင္းယူအသုံးျပဳပါ။")
                .setPositiveButton("ရယူမည္", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        showAD();
                        startActivity(new Intent(Start.this,Updater.class));
                        finish();
                    }
                })
                .setNegativeButton("ထြက္မည္", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        showAD();
                        finish();
                    }
                });
        android.support.v7.app.AlertDialog dialog = builder.create();
        dialog.show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.exit:
                exitApp();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    public static void changeToolbarFont(Toolbar toolbar, Activity context) {
        for (int i = 0; i < toolbar.getChildCount(); i++) {
            View view = toolbar.getChildAt(i);
            if (view instanceof TextView) {
                TextView tv = (TextView) view;
                if (tv.getText().equals(toolbar.getTitle())) {
                    applyFont(tv, context);
                    break;
                }
            }
        }
    }

    public static void applyFont(TextView tv, Activity context) {
        tv.setTextSize(15);
        tv.setTypeface(Typeface.createFromAsset(context.getAssets(), "paoh.ttf"));
    }
}
