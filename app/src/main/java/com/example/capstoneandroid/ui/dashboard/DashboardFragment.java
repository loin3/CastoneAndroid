package com.example.capstoneandroid.ui.dashboard;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Response;
import com.android.volley.error.VolleyError;
import com.android.volley.request.JsonArrayRequest;
import com.example.capstoneandroid.R;
import com.example.capstoneandroid.domain.Comment;
import com.example.capstoneandroid.domain.Post;
import com.example.capstoneandroid.util.VolleySingletonRQ;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class DashboardFragment extends Fragment {

    private DashboardViewModel dashboardViewModel;
    private RecyclerView recyclerView;
    private PostRecyclerViewAdapter recyclerViewAdapter;
    private int scrollPosition = 0;
    private Button writeButton;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        dashboardViewModel =
                new ViewModelProvider(this).get(DashboardViewModel.class);
        View root = inflater.inflate(R.layout.fragment_dashboard, container, false);

        setDataSet();

        //recyclerView
        recyclerView = root.findViewById(R.id.recyclerView);

        if(recyclerView.getLayoutManager() != null){
            scrollPosition = ((LinearLayoutManager) recyclerView.getLayoutManager()).findFirstCompletelyVisibleItemPosition();
        }

        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.scrollToPosition(scrollPosition);


        recyclerViewAdapter = new PostRecyclerViewAdapter(dashboardViewModel.getDataSet(), new PostRecyclerViewAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View v, int position) {
                Intent intent = new Intent(getContext(), SeePostActivity.class);
                Post post = (dashboardViewModel.getDataSet().getValue())[position];
                intent.putExtra("post", post);
                startActivity(intent);
            }
        });
        recyclerView.setAdapter(recyclerViewAdapter);

        //button
        writeButton = root.findViewById(R.id.write_button);
        writeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //post하기 버튼
            }
        });

        return root;
    }

    private void setDataSet(){
        List<Post> list = new ArrayList<>();
        String url = getString(R.string.IP) + ":" + getString(R.string.port) + getString(R.string.getPostList);
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(url, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                if(response.length() > 0){
                    for(int i = 0; i < response.length(); i++){
                        try {
                            JSONObject jsonObject = response.getJSONObject(i);
                            Post post = new Post(jsonObject.getLong("id"), jsonObject.getString("title"), jsonObject.getString("content"), jsonObject.getInt("likes")
                                                    , jsonObject.getLong("imageId"), jsonObject.getString("createdTime"));
                            list.add(post);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    dashboardViewModel.setDataSet(list.toArray(new Post[list.size()]));
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("server", error.toString());
            }
        });
        VolleySingletonRQ.getInstance(getContext()).addToRequestQueue(jsonArrayRequest);
        dashboardViewModel.setDataSet(list.toArray(new Post[list.size()]));
    }
}