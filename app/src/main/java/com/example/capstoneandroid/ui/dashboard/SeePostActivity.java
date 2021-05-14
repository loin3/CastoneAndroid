package com.example.capstoneandroid.ui.dashboard;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.android.volley.Request;
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
import java.util.List;

public class SeePostActivity extends AppCompatActivity {

    private Button addCommentButton;
    private EditText editTextTextMultiLine;
    private RecyclerView recyclerView;
    private CommentRecyclerViewAdapter recyclerViewAdapter;
    private int scrollPosition = 0;
    private Comment[] commentDataSet;
    private Post postData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_see_post);
        editTextTextMultiLine = findViewById(R.id.editTextTextMultiLine);
        addCommentButton = findViewById(R.id.comment_button);
        addCommentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //댓글로 등록
                String comment = String.valueOf(editTextTextMultiLine.getText());
            }
        });

        Intent intent = getIntent();
        postData = (Post)intent.getSerializableExtra("post");
        commentDataSet = getCommentDataSetByPostID(postData);

        recyclerView = findViewById(R.id.comment_list_view);
        if(recyclerView.getLayoutManager() != null){
            scrollPosition = ((LinearLayoutManager) recyclerView.getLayoutManager()).findFirstCompletelyVisibleItemPosition();
        }
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        recyclerView.scrollToPosition(scrollPosition);
        recyclerViewAdapter = new CommentRecyclerViewAdapter(commentDataSet);
        recyclerView.setAdapter(recyclerViewAdapter);
    }

    private Comment[] getCommentDataSetByPostID(Post post){
        List<Comment> list = new ArrayList<>();
        String url = getString(R.string.IP) + ":" + getString(R.string.port) + getString(R.string.getCommentList) + "?id=" + post.id;
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(url, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {

                if(response.length() > 0){
                    for(int i = 0; i < response.length(); i++){
                        try {
                            JSONObject jsonObject = response.getJSONObject(i);
                            Comment comment = new Comment(jsonObject.getLong("id"), jsonObject.getString("createdTime"), jsonObject.getString("content"), jsonObject.getInt("likes"));
                            list.add(comment);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }
                setDataSet(list.toArray(new Comment[list.size()]));
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("server", error.toString());
            }
        });


//        Comment[] comments = new Comment[5];
//        for(int i = 0; i < 5; i++){
//            Long dateTime = System.currentTimeMillis();
//            Timestamp timestamp = new Timestamp(dateTime);
//            comments[i] = new Comment(Long.valueOf(i), timestamp.toString(), "content" + i, i);
//        }


        return list.toArray(new Comment[list.size()]);
    }

    private void setDataSet(Comment[] commentDataSet){
        this.commentDataSet = commentDataSet;
    }
}