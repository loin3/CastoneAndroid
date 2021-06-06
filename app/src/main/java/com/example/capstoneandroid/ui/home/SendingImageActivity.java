package com.example.capstoneandroid.ui.home;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.error.VolleyError;
import com.example.capstoneandroid.util.ByteArrayMultiPartRequest;
import com.example.capstoneandroid.R;
import com.example.capstoneandroid.util.VolleySingletonRQ;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class SendingImageActivity extends AppCompatActivity {
    //------------------------------여기 IP를 본인 아이피랑 포트로 설명하시고 뒤에 url도 완성해주세요
    private final String AiServerUrl = "http://218.157.43.244:5000/fileUpload";

    private TextView diseaseTextView;
    private Button goSiteButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sending_image);

        diseaseTextView = findViewById(R.id.diseaseTextView);

        Intent intent = getIntent();
        Uri uri = Uri.parse(intent.getStringExtra("uri"));
        String imgPath = intent.getStringExtra("imgPath");
        final ImageButton thumbnailImageButton = findViewById(R.id.thumbnailImageButton);

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = 8;
        Bitmap src = BitmapFactory.decodeFile(imgPath, options);
        Bitmap resized = Bitmap.createScaledBitmap(src, 1800, 1200, true);
        Matrix matrix = new Matrix();
        matrix.preRotate(90, 0, 0);
        Bitmap created = Bitmap.createBitmap(resized, 0, 0, resized.getWidth(), resized.getHeight(), matrix, false);
        thumbnailImageButton.setImageBitmap(created);

        Bitmap doubleResized = Bitmap.createScaledBitmap(src, 900, 600, false);
        Bitmap doubleRotated = Bitmap.createBitmap(doubleResized, 0, 0, doubleResized.getWidth(), doubleResized.getHeight(), matrix, false);

        goSiteButton = findViewById(R.id.goSiteButton);
        goSiteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent goToSiteIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.cheongdo.go.kr/open.content/" +
                        "farm/cyber.plant.hospital/pest.map/oriental.melon/blight/08/default.aspx"));
                startActivity(goToSiteIntent);
            }
        });

        final Button submitToServerButton = findViewById(R.id.submitToServerButton);
        submitToServerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("Aiserver", "서버 제출 버튼");

                ByteArrayMultiPartRequest byteArrayMultiPartRequest = new ByteArrayMultiPartRequest(Request.Method.POST, AiServerUrl, new Response.Listener<byte[]>() {
                    @Override
                    public void onResponse(byte[] response) {
                        Log.d("Aiserver", "ai 서버 성공" + response);
                        Bitmap bitmap = byteArrayToBitMap(response);
                        Bitmap sizeChangedBitmap = Bitmap.createScaledBitmap(bitmap, bitmap.getWidth()*2, bitmap.getHeight()*2, false);
                        thumbnailImageButton.setImageBitmap(sizeChangedBitmap);

                        diseaseTextView.setText("흰가루병");
                        submitToServerButton.setVisibility(View.INVISIBLE);
                        goSiteButton.setVisibility(View.VISIBLE);
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("AIserver", error.toString());
                    }
                });

                String imageFileName = saveBitmapToCache(doubleRotated);
                String imageFilePath = getFilePathFromCache(imageFileName);

                byteArrayMultiPartRequest.addFile("file", imageFilePath);

                VolleySingletonRQ.getInstance(getApplicationContext()).addToRequestQueue(byteArrayMultiPartRequest);
            }
        });
    }

    private Bitmap byteArrayToBitMap(byte[] bytes) {
        try {
            Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
            Log.d("aiserver", "byte to bitmap");
            return bitmap;
        } catch (Exception e) {
            e.getMessage();
            return null;
        }
    }

    public String saveBitmapToCache(Bitmap bitmap){
        File storage = getCacheDir();
        Date date = new Date();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy_MM_dd'T'HH_mm_ss");
        String fileName = simpleDateFormat.format(date)+".jpg";
        File tempFile = new File(storage, fileName);

        try {
            // 자동으로 빈 파일을 생성합니다.
            tempFile.createNewFile();

            // 파일을 쓸 수 있는 스트림을 준비합니다.
            FileOutputStream out = new FileOutputStream(tempFile);

            // compress 함수를 사용해 스트림에 비트맵을 저장합니다.
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);

            // 스트림 사용후 닫아줍니다.
            out.close();

        } catch (FileNotFoundException e) {
            Log.e("MyTag","FileNotFoundException : " + e.getMessage());
        } catch (IOException e) {
            Log.e("MyTag","IOException : " + e.getMessage());
        }
        return fileName;
    }

    public String getFilePathFromCache(String fileName){
        File file = new File(getCacheDir().toString());
        String filePath = file.getAbsolutePath() + "/" + fileName;
        Log.d("imagePath", filePath);
        return filePath;
    }
}