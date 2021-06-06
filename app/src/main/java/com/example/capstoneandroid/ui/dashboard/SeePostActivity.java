package com.example.capstoneandroid.ui.dashboard;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.error.VolleyError;
import com.android.volley.request.JsonObjectRequest;
import com.example.capstoneandroid.R;
import com.example.capstoneandroid.domain.Comment;
import com.example.capstoneandroid.domain.Post;
import com.example.capstoneandroid.util.ImageLoadTask;
import com.example.capstoneandroid.util.VolleySingletonRQ;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


public class SeePostActivity extends AppCompatActivity {

    private TextView postTitle;
    private TextView postContent;
    private Button addCommentButton;
    private EditText editTextTextMultiLine;
    private RecyclerView recyclerView;
    private CommentRecyclerViewAdapter recyclerViewAdapter;
    private ImageView postImage;
    private int scrollPosition = 0;
    private Comment[] commentDataSet;
    private Post postData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_see_post);
        editTextTextMultiLine = findViewById(R.id.editTextTextMultiLine);
        addCommentButton = findViewById(R.id.comment_button);

        recyclerView = findViewById(R.id.comment_list_view);
        if(recyclerView.getLayoutManager() != null){
            scrollPosition = ((LinearLayoutManager) recyclerView.getLayoutManager()).findFirstCompletelyVisibleItemPosition();
        }
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        recyclerView.scrollToPosition(scrollPosition);
        recyclerViewAdapter = new CommentRecyclerViewAdapter();
        recyclerView.setAdapter(recyclerViewAdapter);

        Intent intent = getIntent();
        postData = (Post)intent.getSerializableExtra("post");
        commentDataSet = postData.comments.toArray(new Comment[postData.comments.size()]);
        recyclerViewAdapter.setDataSet(commentDataSet);
        recyclerViewAdapter.notifyDataSetChanged();

        postContent = findViewById(R.id.post_content);
        postTitle = findViewById(R.id.post_title);
        postContent.setText(postData.content);
        postTitle.setText(postData.title);

        postImage = findViewById(R.id.post_image);
        //String imageUrl = "http://10.0.2.2:8080/image/" + postData.imageId;
        String imageUrl = getString(R.string.IP) + ":" + getString(R.string.port) + getString(R.string.getImage) + "/" + postData.imageId;
        ImageLoadTask imageLoadTask = new ImageLoadTask(imageUrl, postImage);
        imageLoadTask.execute();

        addCommentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //댓글로 등록
                String commentString = String.valueOf(editTextTextMultiLine.getText());
                JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject.put("content", commentString);
                    jsonObject.put("postId", postData.id);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                String url = getString(R.string.IP) + ":" + getString(R.string.port) + getString(R.string.postComment);
                //String url = "http://10.0.2.2:8080/postComment";
                JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, jsonObject, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        getCommentDataSetByPostID(postData);
                        Log.d("server", "댓글 추가 에서 내용" + jsonObject.toString());
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("server", "댓글 추가 에서 에러" + error.toString());
                    }
                });
                VolleySingletonRQ.getInstance(getApplicationContext()).addToRequestQueue(jsonObjectRequest);
            }
        });
    }

    private void getCommentDataSetByPostID(Post post){
        String url = getString(R.string.IP) + ":" + getString(R.string.port) + getString(R.string.post) + "/" + post.id;
        //String url = "http://10.0.2.2:8080/post/" + post.id;
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    Post post = new Post(response.getLong("id"), response.getString("title"), response.getString("content"), response.getInt("likes")
                            , response.getLong("imageId"), response.getString("createdTime"));
                    JSONArray tempComments = response.getJSONArray("comments");
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
                    Log.d("server", "댓글 리셋에서" + post.comments.size());
                    commentDataSet = post.comments.toArray(new Comment[post.comments.size()]);
                    recyclerViewAdapter.setDataSet(commentDataSet);
                    recyclerViewAdapter.notifyDataSetChanged();

                    editTextTextMultiLine.setText("");
                    InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                    inputMethodManager.hideSoftInputFromWindow(editTextTextMultiLine.getWindowToken(), 0);

                }catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.d("server", "댓글 리셋 에서 에러" + error.toString());
                }
            }
        );
        VolleySingletonRQ.getInstance(getApplicationContext()).addToRequestQueue(jsonObjectRequest);
    }
}