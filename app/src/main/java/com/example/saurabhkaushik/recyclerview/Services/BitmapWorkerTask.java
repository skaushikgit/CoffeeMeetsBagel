package com.example.saurabhkaushik.recyclerview.Services;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.media.Image;
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
    private final WeakReference<ImageView> imageViewWeakReference;
    Context context;
    public int imageKey = -1;
    private AddBitmapToMemoryCache addBitmapToMemoryCacheDelegate;

    public interface AddBitmapToMemoryCache {
        public void addBitmapToCache(int key, Bitmap bitmap);
    }

    public void setAddBitmapToMemoryCacheDelegate(AddBitmapToMemoryCache delegate) {
        this.addBitmapToMemoryCacheDelegate = delegate;
    }

    public BitmapWorkerTask(Context context, ImageView imageView, int imageKey){
        this.context = context;
        imageViewWeakReference = new WeakReference<ImageView>(imageView);
        this.imageKey = imageKey;
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
            addBitmapToMemoryCacheDelegate.addBitmapToCache(imageKey, myBitmap);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return myBitmap;
    }

    @Override
    protected void onPostExecute(Bitmap bitmap) {
        if (isCancelled()){
            bitmap = null;
        }
        if (imageViewWeakReference != null && bitmap != null) {
            final ImageView imageView = imageViewWeakReference.get();
            final BitmapWorkerTask bitmapWorkerTask = getBitmapWorkerTaskFromImageView(imageView);
            if (this == bitmapWorkerTask && imageView !=null) {
                imageView.setImageBitmap(bitmap);
            }
        }
    }

    public static BitmapWorkerTask getBitmapWorkerTaskFromImageView(ImageView imageView){
        if (imageView != null) {
            final BitmapDrawable drawable = (BitmapDrawable) imageView.getDrawable();
            if (drawable instanceof BitmapWorkerTask.AsyncDrawable) {
                BitmapWorkerTask.AsyncDrawable asyncDrawable = (BitmapWorkerTask.AsyncDrawable) drawable;
                return asyncDrawable.getBitmapWorkerTask();
            }
        }
        return null;
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
