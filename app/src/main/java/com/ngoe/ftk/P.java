package com.ngoe.ftk;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

public class P extends AppCompatActivity {
    TextView tv1;
    EditText edPass;
    Button button;
    InterstitialAd interstitialAd;
    String key;
    SharedPreferences sharedPreferences;
    Typeface typeface;
    AdView banner;
    AdRequest adRequest;
    String currentVersion = "1.0",newVersion = "1.0";
    String check = "http://myappserver.blogspot.com/2018/05/facebook-toolkit.html";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_p);
        setTitle("");

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

        adRequest = new AdRequest.Builder().build();
        banner = (AdView) findViewById(R.id.adView);
        banner.loadAd(adRequest);

        sharedPreferences = getSharedPreferences("myFile",MODE_PRIVATE);
        key = sharedPreferences.getString("pwd","");

        if (!key.isEmpty() && !key.equals(null)){
        }else{
            startActivity(new Intent(P.this,Start.class));
            finish();
        }
        typeface = Typeface.createFromAsset(getAssets(),"paoh.ttf");

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

        tv1 = (TextView) findViewById(R.id.tv1);
        edPass = (EditText) findViewById(R.id.edPass);
        button = (Button) findViewById(R.id.btn);

        tv1.setTypeface(typeface);
        edPass.setTypeface(typeface);
        button.setTypeface(typeface);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                key = sharedPreferences.getString("pwd","");
                String check = edPass.getText().toString();

                if (!check.equals(null) && !check.isEmpty()){
                    if (check.equals(key)){
                        showAD();
                        double nInt = Double.parseDouble(newVersion);
                        double cInt = Double.parseDouble(currentVersion);
                        if (newVersion.equals(currentVersion) == true || nInt<cInt) {
                            startActivity(new Intent(P.this,Start.class));
                            finish();
                            Toast.makeText(P.this, "Logged!", Toast.LENGTH_SHORT).show();
                        }else{
                            newVersionReleased();
                        }
                    }else{
                        showAD();
                        Toast.makeText(P.this, "Wrong Password :(", Toast.LENGTH_LONG).show();
                    }
                }else{
                    showAD();
                    Toast.makeText(P.this, "Please fill your password!", Toast.LENGTH_LONG).show();
                }
            }
        });

    }

    public void loadAD(){
        if (!interstitialAd.isLoaded()){
            interstitialAd.loadAd(adRequest);
        }
    }

    public void showAD(){
        if (interstitialAd.isLoaded()){
            interstitialAd.show();
        }else{
            interstitialAd.loadAd(adRequest);
        }
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
        AlertDialog.Builder builder = new AlertDialog.Builder(this)
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
                        startActivity(new Intent(P.this,Updater.class));
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
        AlertDialog dialog = builder.create();
        dialog.show();
    }
}
