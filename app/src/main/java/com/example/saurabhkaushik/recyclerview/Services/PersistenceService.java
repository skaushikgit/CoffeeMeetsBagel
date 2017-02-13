package com.example.saurabhkaushik.recyclerview.Services;

import android.content.Context;
import android.util.Log;

import com.example.saurabhkaushik.recyclerview.Models.TeamModel;
import com.example.saurabhkaushik.recyclerview.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

/**
 * Created by saurabhkaushik on 11/02/17.
 */

public class PersistenceService {
    Context context;
    private ArrayList<TeamModel> teamModelArrayList;

    public PersistenceService(Context context) {
        this.context = context;
        teamModelArrayList = new ArrayList<>();
    }

    public int loadAllData() {
        int result = 0;
        String str = "";
        StringBuilder builder = new StringBuilder();
        InputStream inputStream = context.getResources().openRawResource(R.raw.team);
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        if (inputStream != null) {
            try {
                while((str = bufferedReader.readLine()) != null){
                    builder.append(str);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                inputStream.close();
                bufferedReader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

            try {
                JSONArray jsonArray = new JSONArray(String.valueOf(builder));
                result = convertJSONToModel(jsonArray);
                Log.i("Saurabh", jsonArray.toString());
            } catch (JSONException e) {
                e.printStackTrace();
                result = 0;
            }

        }
        return result;
    }

    private int convertJSONToModel(JSONArray jsonArray){
        for (int i=0; i<jsonArray.length(); i++) {
            try {
                JSONObject object = jsonArray.getJSONObject(i);
                TeamModel teamModel = new TeamModel();
                teamModel.setAvatar(object.getString("avatar"));
                teamModel.setBio(object.getString("bio"));
                teamModel.setFirstName(object.getString("firstName"));
                teamModel.setId(object.getString("id"));
                teamModel.setLastName(object.getString("lastName"));
                teamModel.setTitle(object.getString("title"));
                teamModelArrayList.add(teamModel);
            } catch (JSONException e) {
                e.printStackTrace();
                return 0;
            }
        }
        return 1;
    }

    public ArrayList<TeamModel> getTeamModelArrayList() {
        return teamModelArrayList;
    }
}
