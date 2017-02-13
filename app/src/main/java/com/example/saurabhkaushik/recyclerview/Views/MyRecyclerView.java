package com.example.saurabhkaushik.recyclerview.Views;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
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

import java.util.ArrayList;

/**
 * Created by saurabhkaushik on 12/02/17.
 */

public class MyRecyclerView extends RecyclerView{
    ArrayList<TeamModel> teamModelArrayList = new ArrayList<>();
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
            TextView textView;
            ImageView imageView;
            public ViewHolder(View itemView) {
                super(itemView);
                textView = (TextView) itemView.findViewById(R.id.textViewId);
                imageView = (ImageView) itemView.findViewById(R.id.imageViewId);
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
            holder.textView.setText(teamModelArrayList.get(position).getFirstName());
        }

        @Override
        public int getItemCount() {
            return teamModelArrayList.size();
        }
    }
}
