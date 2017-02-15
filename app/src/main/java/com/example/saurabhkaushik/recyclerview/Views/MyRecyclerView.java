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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.saurabhkaushik.recyclerview.Models.TeamModel;
import com.example.saurabhkaushik.recyclerview.R;
import com.example.saurabhkaushik.recyclerview.Services.AppInstance;
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
    private ArrayList<TeamModel> teamModelArrayList = new ArrayList<>();
    MyRecyclerViewAdapter adapter;
    PersistenceService service;

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
            new DowloadImageTask(holder.imageView).execute(teamModelArrayList.get(position).getAvatar());
        }

        @Override
        public int getItemCount() {
            return (null != teamModelArrayList ? teamModelArrayList.size() : 0);
        }
    }

    private class DowloadImageTask extends AsyncTask<String, Void, Bitmap>{
        ImageView imageView;

        public DowloadImageTask(ImageView imageView) {
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
    }
}
