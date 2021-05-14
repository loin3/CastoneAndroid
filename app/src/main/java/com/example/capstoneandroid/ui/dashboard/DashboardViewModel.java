package com.example.capstoneandroid.ui.dashboard;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.android.volley.Response;
import com.android.volley.error.VolleyError;
import com.android.volley.request.JsonArrayRequest;
import com.example.capstoneandroid.R;
import com.example.capstoneandroid.domain.Comment;
import com.example.capstoneandroid.domain.Post;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class DashboardViewModel extends ViewModel {

    private MutableLiveData<Post[]> dataSet;

    public DashboardViewModel() {
        dataSet = new MutableLiveData<>();
        initDataSet();
    }

    public LiveData<Post[]> getDataSet() {
        return dataSet;
    }

    public void setDataSet(Post[] posts){
        this.dataSet.setValue(posts);
    }

    private void initDataSet(){
        Post[] temp = new Post[30];
        for(int i = 0; i < temp.length; i++){
            Long dateTime = System.currentTimeMillis();
            Timestamp timestamp = new Timestamp(dateTime);
            temp[i] = new Post(Long.valueOf(i), "title" + i, "content" + i, i, Long.valueOf(i), timestamp.toString());
        }
        dataSet.setValue(temp);
    }
}