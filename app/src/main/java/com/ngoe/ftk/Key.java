package com.ngoe.ftk;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;

public class Key extends AppCompatActivity {
    TextView tv1;
    EditText edPass;
    Button btnLock,btnRemove;
    Typeface typeface;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    String key;
    AdRequest adRequest;
    InterstitialAd interstitialAd;
    AdView banner;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_key);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        sharedPreferences = getSharedPreferences("myFile",MODE_PRIVATE);
        editor = sharedPreferences.edit();
        key = sharedPreferences.getString("pwd","");

        typeface = Typeface.createFromAsset(getAssets(),"paoh.ttf");

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

        tv1 = (TextView) findViewById(R.id.tv1);
        edPass = (EditText) findViewById(R.id.edPass);
        btnLock = (Button) findViewById(R.id.btnLock);
        btnRemove = (Button) findViewById(R.id.btnRemove);

        tv1.setTypeface(typeface);
        edPass.setTypeface(typeface);
        btnLock.setTypeface(typeface);
        btnRemove.setTypeface(typeface);

        btnLock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                key = sharedPreferences.getString("pwd","");
                final String kkk = edPass.getText().toString();
                if (!kkk.isEmpty() && !kkk.equals(null)){
                    AlertDialog.Builder builder = new AlertDialog.Builder(Key.this);
                    builder.setTitle("Attention!");
                    builder.setMessage("Do you want to set Password ?");
                    builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            editor.putString("pwd",kkk);
                            editor.commit();
                            showAD();
                            if (!key.equals(kkk) && !key.isEmpty()){
                                Toast.makeText(Key.this, "Password Changed:)", Toast.LENGTH_LONG).show();
                            }else{
                                if (kkk.equals(sharedPreferences.getString("pwd",""))){
                                    Toast.makeText(Key.this, "Password Mode On :)", Toast.LENGTH_LONG).show();
                                }
                            }
                        }
                    });
                    builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            showAD();
                        }
                    });
                    AlertDialog dialog = builder.create();
                    dialog.show();
                }else{
                    showAD();
                    Toast.makeText(Key.this, "Please fill your password!", Toast.LENGTH_LONG).show();
                }
            }
        });

        btnRemove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                key = sharedPreferences.getString("pwd","");
                if (!key.equals(null) && !key.isEmpty()){
                    AlertDialog.Builder builder = new AlertDialog.Builder(Key.this);
                    builder.setTitle("Attention!");
                    builder.setMessage("Do you want to REMOVE Password ?");
                    builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        String kkk = "";
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            editor.putString("pwd","");
                            editor.commit();

                            if (kkk.equals(sharedPreferences.getString("pwd",""))){
                                showAD();
                                Toast.makeText(Key.this, "Password REMOVED :(", Toast.LENGTH_LONG).show();
                            }
                        }
                    });
                    builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            showAD();
                        }
                    });
                    AlertDialog dialog = builder.create();
                    dialog.show();
                }else{
                    showAD();
                    Toast.makeText(Key.this, "You need to Set Password Mode :)", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
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
}
