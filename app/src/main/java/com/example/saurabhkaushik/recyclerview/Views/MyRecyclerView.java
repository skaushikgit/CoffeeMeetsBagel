package com.example.saurabhkaushik.recyclerview.Views;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.Log;
import android.util.LruCache;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.saurabhkaushik.recyclerview.Models.TeamModel;
import com.example.saurabhkaushik.recyclerview.R;
import com.example.saurabhkaushik.recyclerview.Services.AppInstance;
import com.example.saurabhkaushik.recyclerview.Services.BitmapWorkerTask;
import com.example.saurabhkaushik.recyclerview.Services.PersistenceService;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by saurabhkaushik on 12/02/17.
 */

public class MyRecyclerView extends RecyclerView{
    LruCache<Integer, Bitmap> mMemoryCache;
    private ArrayList<TeamModel> teamModelArrayList = new ArrayList<>();
    MyRecyclerViewAdapter adapter;
    PersistenceService service;
    BitmapWorkerTask.AddBitmapToMemoryCache addBitmapToMemoryCacheDelegate;

    public MyRecyclerView(Context context) {
        super(context);
        init();
    }

    public MyRecyclerView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public MyRecyclerView(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }


    private void init() {
        final int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);
        // Use 1/8th of the available memory for this memory cache.
        final int cacheSize = maxMemory / 8;
        mMemoryCache = new android.util.LruCache<Integer, Bitmap>(cacheSize) {
            @Override
            protected int sizeOf(Integer key, Bitmap bitmap) {
                // The cache size will be measured in kilobytes rather than
                // number of items.
                return bitmap.getByteCount() / 1024;
            }
        };
        addBitmapToMemoryCacheDelegate = new BitmapWorkerTask.AddBitmapToMemoryCache() {
            @Override
            public void addBitmapToCache(int key, Bitmap bitmap) {
                mMemoryCache.put(key, bitmap);
            }
        };
        service = AppInstance.getPersistenceService(getContext());
        this.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new MyRecyclerViewAdapter();
        this.setAdapter(adapter);
        update();
    }


    public void update() {
        teamModelArrayList.clear();
        teamModelArrayList.addAll(service.getTeamModelArrayList());
        adapter.notifyDataSetChanged();
    }

    public class MyRecyclerViewAdapter extends RecyclerView.Adapter<MyRecyclerViewAdapter.ViewHolder>{

        public class ViewHolder extends RecyclerView.ViewHolder {
            protected ImageView imageView;
            protected TextView tvBio;
            protected TextView tvFirstName;
            protected TextView tvMember;
            protected TextView tvLastName;
            protected TextView tvTitle;
            public ViewHolder(View itemView) {
                super(itemView);
                this.imageView = (ImageView) itemView.findViewById(R.id.imageViewId);
                this.tvBio = (TextView) itemView.findViewById(R.id.bioId);
                this.tvFirstName = (TextView) itemView.findViewById(R.id.firstNameId);
                this.tvMember = (TextView) itemView.findViewById(R.id.memberId);
                this.tvLastName = (TextView) itemView.findViewById(R.id.lastNameId);
                this.tvTitle = (TextView) itemView.findViewById(R.id.titleId);
            }
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(getContext()).inflate(R.layout.recycler_view_item, null);
            ViewHolder viewHolder = new ViewHolder(view);
            return viewHolder;
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            holder.tvBio.setText(teamModelArrayList.get(position).getBio());
            holder.tvFirstName.setText(teamModelArrayList.get(position).getFirstName());
            holder.tvMember.setText(teamModelArrayList.get(position).getId());
            holder.tvLastName.setText(teamModelArrayList.get(position).getLastName());
            holder.tvTitle.setText(teamModelArrayList.get(position).getTitle());
            Bitmap bitmap = getBitmapFromMemCache(position);
            if (bitmap != null) {
                holder.imageView.setImageBitmap(bitmap);
            } else {
                if (cancelPotentialWorker(holder.imageView, position)) {
                    final BitmapWorkerTask bitmapWorkerTask = new BitmapWorkerTask(getContext(), holder.imageView, position);
                    bitmapWorkerTask.setAddBitmapToMemoryCacheDelegate(addBitmapToMemoryCacheDelegate);
                    final BitmapWorkerTask.AsyncDrawable asyncDrawable = new BitmapWorkerTask.AsyncDrawable(getResources(), null, bitmapWorkerTask);
                    holder.imageView.setImageDrawable(asyncDrawable);
                    bitmapWorkerTask.execute(teamModelArrayList.get(position).getAvatar());
                }
            }
        }

        @Override
        public int getItemCount() {
            return (null != teamModelArrayList ? teamModelArrayList.size() : 0);
        }
    }

    private boolean cancelPotentialWorker(ImageView imageView, int key) {
        BitmapWorkerTask bitmapWorkerTask = BitmapWorkerTask.getBitmapWorkerTaskFromImageView(imageView);
        if (bitmapWorkerTask != null) {
            //If the bitmapWorkerTask.key is not yet set or it differs from the new one
            if (bitmapWorkerTask.imageKey < 0 || bitmapWorkerTask.imageKey != key){
                //Cancel previous task
                bitmapWorkerTask.cancel(true);
            } else {
                //The same work is already in progress
                return false;
            }
        }
        //No task was associated with imageView or no existing task was cancelled
        return true;
    }

    private Bitmap getBitmapFromMemCache(int key){
        return mMemoryCache.get(key);
    }
}
