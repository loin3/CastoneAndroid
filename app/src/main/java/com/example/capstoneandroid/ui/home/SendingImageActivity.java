package com.example.capstoneandroid.ui.home;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.error.VolleyError;
import com.android.volley.request.SimpleMultiPartRequest;
import com.android.volley.toolbox.Volley;
import com.example.capstoneandroid.util.ProgressBarDisplayer;
import com.example.capstoneandroid.R;
import com.example.capstoneandroid.util.VolleySingletonRQ;

public class SendingImageActivity extends AppCompatActivity {
    //------------------------------여기 IP를 본인 아이피랑 포트로 설명하시고 뒤에 url도 완성해주세요
    private final String AiServerUrl = "http://122.40.75.17:8080";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sending_image);


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
                        Intent intent1 = new Intent(getApplicationContext(), AiResultActivity.class);
                        startActivity(intent1);
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("AIserver", error.toString());
                    }
                });
                simpleMultiPartRequest.addFile("img", imgPath);
                VolleySingletonRQ.getInstance(getApplicationContext()).addToRequestQueue(simpleMultiPartRequest);
            }
        });
    }
}