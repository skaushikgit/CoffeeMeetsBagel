package com.example.saurabhkaushik.recyclerview.Activities;

import android.net.Uri;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.example.saurabhkaushik.recyclerview.Fragments.MainFragment;
import com.example.saurabhkaushik.recyclerview.R;
import com.example.saurabhkaushik.recyclerview.Services.AppInstance;
import com.example.saurabhkaushik.recyclerview.Services.PersistenceService;

public class MainActivity extends AppCompatActivity implements MainFragment.OnFragmentInteractionListener{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        PersistenceService persistenceService = AppInstance.getPersistenceService(this);
        persistenceService.loadAllData();
        MainFragment mainFragment = new MainFragment();
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.add(R.id.fragment_container, mainFragment);
        fragmentTransaction.commit();
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }
}
