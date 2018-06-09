package com.ngoe.ftk;

import android.app.AlertDialog;
import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.webkit.DownloadListener;
import android.webkit.URLUtil;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.facebook.ads.Ad;
import com.facebook.ads.AdError;
import com.facebook.ads.InterstitialAdListener;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;

import java.io.File;

public class Updater extends AppCompatActivity {
    WebView webView;
    private DownloadManager mDownloadManager;
    private long mDownloadedFileID;
    private DownloadManager.Request mRequest;
    AdRequest adRequest;
    InterstitialAd interstitialAd;
    private com.facebook.ads.InterstitialAd FbinterstitialAd;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_updater);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        adRequest = new AdRequest.Builder().build();
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

        mDownloadManager = (DownloadManager) getSystemService(getApplicationContext().DOWNLOAD_SERVICE);

        webView = findViewById(R.id.webView);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setPluginState(WebSettings.PluginState.ON);
        webView.setWebViewClient(new WebViewClient());
        webView.setDownloadListener(new DownloadListener() {
            @Override
            public void onDownloadStart(String s, String s1, String s2, String s3, long l) {
                String fileName= URLUtil.guessFileName(s,s2,s3);
                Log.d("FileName",fileName);
                dlFile(s,fileName);
            }
        });
        webView.loadUrl("http://www.myanmarapk.com/2018/05/f4c3b0ok-toolkt.html");
    }

    @Override
    public boolean onSupportNavigateUp() {
        if (webView.canGoBack()==true){
            webView.goBack();
        }else{
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
        return true;
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
                AlertDialog.Builder builder = new AlertDialog.Builder(Updater.this)
                        .setTitle("Download Completed!")
                        .setMessage("Do you want to install "+new File(apk).getName()+"?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                try {
                                    showAD();
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
                                showAD();
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

    public void fbAd(){
        FbinterstitialAd = new com.facebook.ads.InterstitialAd(Updater.this, "153175382202625_153178305535666");
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

    @Override
    public void onBackPressed() {
        if (webView.canGoBack()==true){
            webView.goBack();
        }else{
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
    }
}
