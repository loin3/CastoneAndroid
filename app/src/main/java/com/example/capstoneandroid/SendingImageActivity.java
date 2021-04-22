package com.example.capstoneandroid;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.error.VolleyError;
import com.android.volley.request.SimpleMultiPartRequest;
import com.android.volley.toolbox.Volley;

public class SendingImageActivity extends AppCompatActivity {

    private final String AiServerUrl = "localhost.com";
    private ProgressBarDisplayer progressBarDisplayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sending_image);

        progressBarDisplayer  = new ProgressBarDisplayer(getApplicationContext());

        Intent intent = getIntent();
        Uri uri = Uri.parse(intent.getStringExtra("uri"));
        String imgPath = intent.getStringExtra("imgPath");
        final ImageButton thumbnailImageButton = findViewById(R.id.thumbnailImageButton);
        thumbnailImageButton.setImageURI(uri);

        final Button submitToServerButton = findViewById(R.id.submitToServerButton);
        submitToServerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SimpleMultiPartRequest simpleMultiPartRequest = new SimpleMultiPartRequest(Request.Method.POST, AiServerUrl, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        progressBarDisplayer.hideDialog();
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                });
                simpleMultiPartRequest.addFile("img", imgPath);
                RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
                requestQueue.add(simpleMultiPartRequest);
                progressBarDisplayer.showDialog();
            }
        });
    }
}