package com.example.saurabhkaushik.recyclerview.Services;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.widget.ImageView;

import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by saurabhkaushik on 15/02/17.
 */

public class BitmapWorkerTask extends AsyncTask<String, Void, Bitmap>{
    ImageView imageView;
    Context context;

    public BitmapWorkerTask(Context context, ImageView imageView){
        this.context = context;
        this.imageView = imageView;
    }

    @Override
    protected Bitmap doInBackground(String... params) {
        Bitmap myBitmap = null;
        try {
            URL url = new URL(params[0]);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            myBitmap = BitmapFactory.decodeStream(input);

        } catch (IOException e) {
            e.printStackTrace();
        }
        return myBitmap;
    }

    @Override
    protected void onPostExecute(Bitmap bitmap) {
        imageView.setImageBitmap(bitmap);
    }

    public static class AsyncDrawable extends BitmapDrawable{
        private WeakReference<BitmapWorkerTask> bitmapWorkerTaskWeakReference = null;

        public AsyncDrawable(Resources resources, Bitmap bitmap, BitmapWorkerTask task) {
            super(resources, bitmap);
            if (bitmapWorkerTaskWeakReference == null) {
                bitmapWorkerTaskWeakReference = new WeakReference<BitmapWorkerTask>(task);
            }
        }

        public BitmapWorkerTask getBitmapWorkerTask() {
            return bitmapWorkerTaskWeakReference.get();
        }
    }
}
