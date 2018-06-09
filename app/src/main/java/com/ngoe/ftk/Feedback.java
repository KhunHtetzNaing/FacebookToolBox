package com.ngoe.ftk;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class Feedback extends AppCompatActivity implements View.OnClickListener {
    EditText edName,edSubject,edMessage;
    FloatingActionButton fab;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        edName = findViewById(R.id.edName);
        edSubject = findViewById(R.id.edSubject);
        edMessage = findViewById(R.id.edMessage);
        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(this);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public void onClick(View view) {
        String name = edName.getText().toString();
        String subject = edSubject.getText().toString();
        String message = edMessage.getText().toString();

        if (name.equals("") || name.isEmpty() || name.equals(null)){
            Toast.makeText(this, "Please fill your Name!", Toast.LENGTH_SHORT).show();
        }else{
            if (message.equals("") || name.isEmpty() || name.equals(null)){
                Toast.makeText(this, "Please type your Message!", Toast.LENGTH_SHORT).show();
            }else{

                if (subject.equals("") || name.isEmpty() || name.equals(null)){
                    subject = "Facebook Toolbox";
                }

                String text = "My name is "+name+"\n"+message;

                Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
                        "mailto","7boykhai@gmail.com", null));
                emailIntent.putExtra(Intent.EXTRA_SUBJECT, subject);
                emailIntent.putExtra(Intent.EXTRA_TEXT, text);
                startActivity(Intent.createChooser(emailIntent, "Send email..."));
            }
        }
    }
}
