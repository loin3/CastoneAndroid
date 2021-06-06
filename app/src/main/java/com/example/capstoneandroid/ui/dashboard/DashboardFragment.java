package com.example.capstoneandroid.ui.dashboard;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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
                Intent intent = new Intent(getContext(), WritePostActivity.class);
                startActivityForResult(intent, 1000);
            }
        });

        return root;
    }

    private void setDataSet(){
        List<Post> list = new ArrayList<>();
        String url = getString(R.string.IP) + ":" + getString(R.string.port) + getString(R.string.getPostList);
        //String url = "http://10.0.2.2:8080/allPost";
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(url, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                if(response.length() > 0){
                    for(int i = 0; i < response.length(); i++){
                        try {
                            JSONObject jsonObject = response.getJSONObject(i);
                            Post post = new Post(jsonObject.getLong("id"), jsonObject.getString("title"), jsonObject.getString("content"), jsonObject.getInt("likes")
                                                    , jsonObject.getLong("imageId"), jsonObject.getString("createdTime"));
                            JSONArray tempComments = jsonObject.getJSONArray("comments");
                            if(tempComments != null){
                                for(int j = 0; j < tempComments.length(); j++){
                                    JSONObject temp = tempComments.getJSONObject(j);
                                    Comment comment = new Comment();
                                    comment.id = temp.getLong("id");
                                    comment.content = temp.getString("content");
                                    comment.createdTime = temp.getString("createdTime");
                                    comment.likes = temp.getInt("likes");
                                    post.comments.add(comment);
                                }
                            }
                            list.add(post);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    dashboardViewModel.setDataSet(list.toArray(new Post[list.size()]));
                    recyclerViewAdapter.refreshDataSet(dashboardViewModel.getDataSet());
                    recyclerViewAdapter.notifyDataSetChanged();
                }
                Log.d("server", "포스트 가져오기 가져온 결과의 개수는" + list.size());
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("server", "포스트 가져오기 에서 에러" + error.toString());
            }
        });
        jsonArrayRequest.setShouldCache(false);
        VolleySingletonRQ.getInstance(getContext()).addToRequestQueue(jsonArrayRequest);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d("server", "포스팅후 resultCode = " + resultCode);
        if(resultCode == 0){
            //아무것도 안함
        }else if(resultCode == -1){
            //리스트 리프레시
            setDataSet();
        }
    }
}