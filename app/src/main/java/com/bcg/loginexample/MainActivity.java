package com.bcg.loginexample;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.bcg.loginexample.Tools.ImagePicker;
import com.bcg.loginexample.Tools.MD5Util;
import com.bcg.loginexample.Tools.MarshMallowPermission;
import com.bcg.loginexample.Responses.Avatar;
import com.bcg.loginexample.Responses.User;
import com.bcg.loginexample.Rest.ApiInterface;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {
    private ImageButton imgAvatar;
    private de.hdodenhof.circleimageview.CircleImageView profile_image;

    private Button btnLogout, btnUseAvatar, btnTakePhoto;
    public Settings settings;
    MarshMallowPermission marshMallowPermission = new MarshMallowPermission(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        settings =new Settings(getApplicationContext()).getSettings();

        String email =settings.getEmail();
        TextView loginInformation = (TextView)findViewById(R.id.login_email);
        if(email != null || !email.equals("")  ){
            loginInformation.setText("Welcome!!! You have logged in as " + email);
        }else {
            loginInformation.setText("Your login email is missing");
        }
        profile_image =(de.hdodenhof.circleimageview.CircleImageView) findViewById(R.id.profile_image);

        imgAvatar = (ImageButton) findViewById(R.id.imgAvatar);
        imgAvatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getImage();
            }
        });

        btnLogout = (Button) findViewById(R.id.btnLogout);
        btnLogout.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                settings.saveSettings(null,null,"",null);
                Toast.makeText(MainActivity.this, "Signing out! Token deleted.", Toast.LENGTH_LONG).show();

                Intent i = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(i);
            }
        });

        btnUseAvatar = (Button) findViewById(R.id.btnUseAvatar);
        btnUseAvatar.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                String hash = MD5Util.md5Hex(settings.getEmail());
                String gravatarUrl = "http://www.gravatar.com/avatar/" + hash + "?s=600&d=600";
                settings.setAvatar_url(gravatarUrl);
                LoadImage();
            }
        });

        btnTakePhoto = (Button) findViewById(R.id.btnTakePhoto);
        btnTakePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getImage();
            }
        });

        getuser(settings.getUserid());
        //LoadImage( );
    }

    private void getImage() {
        if (!marshMallowPermission.checkPermissionForCamera()) {
            marshMallowPermission.requestPermissionForCamera();
        } else {
            if (!marshMallowPermission.checkPermissionForExternalStorage()) {
                marshMallowPermission.requestPermissionForExternalStorage();
            } else {
                Intent chooseImageIntent = ImagePicker.getPickImageIntent(getApplicationContext());
                startActivityForResult(chooseImageIntent, PICK_IMAGE_ID);
            }}
    }


    private void getuser(final String userId){
         ApiInterface mApiService = ApiInterface.retrofit.create(ApiInterface.class);
        Call<User> mService= mApiService.getuser(userId);
       mService.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                User userObject = response.body();
                settings.setAvatar_url(ApiInterface.ENDPOINT +userObject.avatar_url.replaceFirst("^/", "") );
                LoadImage( );
                Toast.makeText(MainActivity.this, "Returned " + userObject, Toast.LENGTH_LONG).show();
             }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                call.cancel();
                Toast.makeText(MainActivity.this, "Please check your network connection", Toast.LENGTH_LONG).show();
            }
        });
    }

    private static final int PICK_IMAGE_ID = 234; // the number doesn't matter
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch(requestCode) {
            case PICK_IMAGE_ID:
                Bitmap bitmap = ImagePicker.getImageFromResult(this, resultCode, data);

                File file = null;
                try {
                    file = savebitmap(bitmap, "pic.jpeg");
                } catch (IOException e) {
                    e.printStackTrace();
                }
                uploadAvatar("my_userid", file );
                break;
            default:
                super.onActivityResult(requestCode, resultCode, data);
                break;
        }
    }
    public static File savebitmap(Bitmap bmp, String fName) throws IOException {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, 60, bytes);
        File f = new File(Environment.getExternalStorageDirectory()
                + File.separator + fName);
        f.createNewFile();
        FileOutputStream fo = new FileOutputStream(f);
        fo.write(bytes.toByteArray());
        fo.close();
        return f;
    }

    private void uploadAvatar(final String userid,File file){
        //   File file = new File("/storage/emulated/0/Android/data/com.bcg.loginexample/files/pic.jpg");
        // File file = new File("/storage/emulated/0/DCIM/camera/pic.jpg");
       // File file = new File(filePath);
        RequestBody body = RequestBody.create(MediaType.parse("image/*"), file);
        RequestBody description = RequestBody.create(MediaType.parse("text/plain"), "image");

        ApiInterface mApiService = ApiInterface.retrofit.create(ApiInterface.class);
        Call<Avatar> mService=  mApiService.postAvatar(body,description );
        mService.enqueue(new Callback<Avatar>() {
            @Override
            public void onResponse(Call<Avatar> call, Response<Avatar> response) {
                Avatar avatar = response.body();
                settings.setAvatar_url(ApiInterface.ENDPOINT  + avatar.avatar_url);

                LoadImage();
            }

            @Override
            public void onFailure(Call<Avatar> call, Throwable t) {
                call.cancel();
                Toast.makeText(getApplicationContext(), "Error: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    public void LoadImage() {
        String  url=settings.getAvatar_url();
         //load image from server if exists
        if (url!=""){
            Picasso.with(getApplicationContext())
                    .load(url)
                  .networkPolicy(NetworkPolicy.NO_CACHE)
                    .memoryPolicy(MemoryPolicy.NO_CACHE)
                    .placeholder(R.drawable.empty_avatar)
                    .noFade()
                    .into(profile_image);
        }

    }
 }
