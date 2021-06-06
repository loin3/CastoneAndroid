package com.example.capstoneandroid.ui.dashboard;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.loader.content.CursorLoader;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;

import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.error.VolleyError;
import com.android.volley.request.SimpleMultiPartRequest;
import com.example.capstoneandroid.R;
import com.example.capstoneandroid.util.VolleySingletonRQ;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;


public class WritePostActivity extends AppCompatActivity {

    private ImageButton exitImageButton;
    private Button postButton;
    private EditText titleEditText;
    private ImageButton flowerImageButton;
    private EditText contentEditText;
    private String imagePath;
    private String cachedBitmapName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_write_post);

        exitImageButton = findViewById(R.id.exitImageButton);
        postButton = findViewById(R.id.postButton);
        titleEditText = findViewById(R.id.titleEditText);
        flowerImageButton = findViewById(R.id.flowerImageButton);
        contentEditText = findViewById(R.id.contentEditText);

        exitImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setResult(0);
                finish();
            }
        });

        postButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String title = String.valueOf(titleEditText.getText());
                String content = String.valueOf(contentEditText.getText());

                postToServer(title, content);
            }
        });

        flowerImageButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                //갤러리 or 사진 앱 실행하여 사진을 선택하도록
                Intent intent= new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(intent,10);
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case 10:
                if (resultCode == RESULT_OK) {
                    Uri uri = data.getData();
                    if (uri != null) {
                        imagePath = getRealPathFromUri(uri);

                        BitmapFactory.Options options = new BitmapFactory.Options();
                        options.inSampleSize = 8;
                        Bitmap src = BitmapFactory.decodeFile(imagePath, options);
                        Bitmap resized = Bitmap.createScaledBitmap(src, 1800, 1200, true);
                        Matrix matrix = new Matrix();
                        matrix.preRotate(90, 0, 0);
                        Bitmap created = Bitmap.createBitmap(resized, 0, 0, resized.getWidth(), resized.getHeight(), matrix, false);
                        flowerImageButton.setImageBitmap(created);

                        cachedBitmapName = saveBitmapToCache(created);
                    }
                } else {
                    Toast.makeText(getApplicationContext(), "이미지가 선택되지 않았습니다.", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    //Uri -- > 절대경로로 바꿔서 리턴시켜주는 메소드
    private String getRealPathFromUri(Uri uri){
        String[] proj= {MediaStore.Images.Media.DATA};
        CursorLoader loader= new CursorLoader(getApplicationContext(), uri, proj, null, null, null);
        Cursor cursor= loader.loadInBackground();
        int column_index= cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        String result= cursor.getString(column_index);
        cursor.close();
        return result;
    }

    private void postToServer(String title, String content){
        String url = getString(R.string.IP) + ":" + getString(R.string.port) + getString(R.string.post);
        //String url = "http://10.0.2.2:8080/post";
        SimpleMultiPartRequest simpleMultiPartRequest = new SimpleMultiPartRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                setResult(RESULT_OK);
                finish();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("Post server", error.toString());
            }
        });

        simpleMultiPartRequest.addFile("image", getFilePathFromCache(cachedBitmapName));
        simpleMultiPartRequest.addStringParam("title", title);
        simpleMultiPartRequest.addStringParam("content", content);
        VolleySingletonRQ.getInstance(getApplicationContext()).addToRequestQueue(simpleMultiPartRequest);
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