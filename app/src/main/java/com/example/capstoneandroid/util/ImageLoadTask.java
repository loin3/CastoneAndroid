package com.example.capstoneandroid.util;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.widget.ImageView;

import java.net.URL;
import java.util.HashMap;

public class ImageLoadTask extends AsyncTask<Void, Void, Bitmap> {
    private String urlString;
    private ImageView imageView;
    private static HashMap<String, Bitmap> bitmapHash = new HashMap<String, Bitmap>();

    public ImageLoadTask(String urlString, ImageView imageView) {
        this.urlString = urlString;
        this.imageView = imageView;
    }

    @Override
    protected Bitmap doInBackground(Void... voids) {
        Bitmap bitmap = null;
        try {
            // 이미 url을 통해 불러온 적이 있다면 이전 bitmap을 삭제
            if(bitmapHash.containsKey(urlString)) {
                Bitmap oldBitmap = bitmapHash.remove(urlString);
                if(oldBitmap != null){
                    oldBitmap.recycle();
                    oldBitmap = null;
                }
            }

            URL url = new URL(urlString);
            bitmap = BitmapFactory.decodeStream(url.openConnection().getInputStream());

            bitmapHash.put(urlString, bitmap);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return bitmap;
    }

    @Override
    protected void onPostExecute(Bitmap bitmap) {
        super.onPostExecute(bitmap);

        imageView.setImageBitmap(bitmap);
        imageView.invalidate();
    }
}
