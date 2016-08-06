package com.bcg.loginexample;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.bcg.loginexample.Camera.CameraActivity;
import com.squareup.picasso.Picasso;

public class MainActivity extends AppCompatActivity {
    private ImageButton imgAvatar;
    private Button btnLogout, btnUseAvatar, btnTakePhoto;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        String myLoginEmailAddress = getEmail();
        TextView loginInformation = (TextView)findViewById(R.id.login_email);
        if(myLoginEmailAddress != null || !myLoginEmailAddress.equals("")  ){
            loginInformation.setText("Welcome!!! You have logged in as " + myLoginEmailAddress);
        }else {
            loginInformation.setText("Your login email is missing");
        }

        imgAvatar = (ImageButton) findViewById(R.id.imgAvatar);
        imgAvatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(MainActivity.this, CameraActivity.class);
                startActivity(i);
            }
        });

        final SharedPreferences prefs = this.getSharedPreferences("pref", Context.MODE_PRIVATE);

        btnLogout = (Button) findViewById(R.id.btnLogout);
        btnLogout.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                prefs.edit().putString("email", "").apply();

                Intent i = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(i);
            }
        });

        btnUseAvatar = (Button) findViewById(R.id.btnUseAvatar);
        btnUseAvatar.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                setGravatar();
            }
        });

        btnTakePhoto = (Button) findViewById(R.id.btnTakePhoto);
        btnTakePhoto.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                Intent i = new Intent(MainActivity.this, CameraActivity.class);
                startActivity(i);
            }
        });
        LoadImage( );
    }


    public void LoadImage( ) {
       String avatar = getImagePath();

        if (!avatar.equals("")){

        if(avatar.startsWith("http"))
        {
            setGravatar();
        }else
        {
            setAvatarImage();
        }
    }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == 1) {
            if(resultCode == Activity.RESULT_OK){
                String result=data.getStringExtra("result");
            }
            if (resultCode == Activity.RESULT_CANCELED) {
                //Write your code if there's no result
            }
        }
    }

    private String getEmail(){
        SharedPreferences prefs = this.getSharedPreferences("pref", Context.MODE_PRIVATE);
        return prefs.getString("email","");
    }
    private String getImagePath(){
        SharedPreferences prefs = this.getSharedPreferences("pref", Context.MODE_PRIVATE);
        return prefs.getString("avatar","");
    }


    private boolean setGravatar(){
    String hash = MD5Util.md5Hex(getEmail());
    String gravatarUrl = "http://www.gravatar.com/avatar/" + hash + "?s=600&d=600";

    Picasso.with(getApplicationContext())
            .load(gravatarUrl)
    .placeholder(R.drawable.empty_avatar)
    .into(imgAvatar);

        SharedPreferences prefs =getSharedPreferences("pref", Context.MODE_PRIVATE);
        prefs.edit().putString("avatar", gravatarUrl).apply();
        return true;
    }

    private void setAvatarImage(){

      SharedPreferences prefs = this.getSharedPreferences("pref", Context.MODE_PRIVATE);
        String avatarImage =  prefs.getString("avatar","");

        if(!avatarImage.equals("")  ){
            avatarImage = avatarImage;
            Bitmap bmp = BitmapFactory.decodeFile(avatarImage);
            bmp = Bitmap.createScaledBitmap(bmp,600, 600, true);
            imgAvatar.setImageBitmap(bmp);
        }

     }
}
