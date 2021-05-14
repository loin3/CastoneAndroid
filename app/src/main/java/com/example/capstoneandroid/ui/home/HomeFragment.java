package com.example.capstoneandroid.ui.home;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.loader.content.CursorLoader;

import com.example.capstoneandroid.R;

import static android.app.Activity.RESULT_OK;

public class HomeFragment extends Fragment {

    private HomeViewModel homeViewModel;
    private final int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 1001;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
//        homeViewModel =
//                new ViewModelProvider(this).get(HomeViewModel.class);
        View root = inflater.inflate(R.layout.fragment_home, container, false);
//        final TextView textView = root.findViewById(R.id.text_home);
//        homeViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
//            @Override
//            public void onChanged(@Nullable String s) {
//                textView.setText(s);
//            }
//        });

        final Button getImageButton = root.findViewById(R.id.getImageButton);
        getImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int permissionCheck = ContextCompat.checkSelfPermission(getContext(), Manifest.permission.READ_EXTERNAL_STORAGE);
                if(permissionCheck != PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(getContext(), "권한 승인이 필요합니다.", Toast.LENGTH_LONG).show();

                    if (!ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), Manifest.permission.READ_EXTERNAL_STORAGE)) {
                        ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
                    }
                    Toast.makeText(getContext(), "외부 저장소 접근을 위해 저장소 접근 권한이 필요합니다.", Toast.LENGTH_LONG).show();
                }


                //갤러리 or 사진 앱 실행하여 사진을 선택하도록
                Intent intent= new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(intent,10);

            }
        });
        return root;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case 10:
                if (resultCode == RESULT_OK) {
                    //선택한 사진의 경로(Uri)객체 얻어오기
                    Uri uri = data.getData();
                    if (uri != null) {

                        //갤러리앱에서 관리하는 DB정보가 있는데, 그것이 나온다 [실제 파일 경로가 아님!!]
                        //얻어온 Uri는 Gallery앱의 DB번호임. (content://-----/2854)
                        //업로드를 하려면 이미지의 절대경로(실제 경로: file:// -------/aaa.png 이런식)가 필요함
                        //Uri -->절대경로(String)로 변환
                        String imgPath = getRealPathFromUri(uri);   //임의로 만든 메소드 (절대경로를 가져오는 메소드)

                        //이미지 경로 uri 확인해보기
                        //new AlertDialog.Builder(this).setMessage(uri.toString()+"\n"+imgPath).create().show();
                        Log.d("이미지 경로", uri.toString() + "\n" + imgPath);

                        Intent intent = new Intent(getContext(), SendingImageActivity.class);
                        intent.putExtra("uri", uri.toString());
                        intent.putExtra("imgPath", imgPath);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                        startActivity(intent);

                    }

                } else {
                    Toast.makeText(getContext(), "이미지가 선택되지 않았습니다.", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    //Uri -- > 절대경로로 바꿔서 리턴시켜주는 메소드
    private String getRealPathFromUri(Uri uri){
        String[] proj= {MediaStore.Images.Media.DATA};
        CursorLoader loader= new CursorLoader(getContext(), uri, proj, null, null, null);
        Cursor cursor= loader.loadInBackground();
        int column_index= cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        String result= cursor.getString(column_index);
        cursor.close();
        return result;
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults){
        switch (requestCode){
            case MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE: {
                if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    Toast.makeText(getContext(), "권한이 허가되어 있습니다.", Toast.LENGTH_LONG).show();
                }
                else{
                    Toast.makeText(getContext(), "아직 승인받지 않았습니다.", Toast.LENGTH_LONG).show();
                }
                return;
            }
        }
    }

}