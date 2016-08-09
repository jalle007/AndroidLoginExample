package com.bcg.loginexample;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.bcg.loginexample.Responses.Session;
import com.bcg.loginexample.Rest.ApiInterface;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends AppCompatActivity {
    public Settings settings;

    public static final String BASE_URL = ApiInterface.ENDPOINT;

    // UI references.
    private EditText mEmailView;
    private EditText mPasswordView;
    private TextView failedLoginMessage;

    View focusView = null;
    private String email;
    private String password;

     @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

         settings =new Settings(getApplicationContext());
         settings = settings.getSettings();

     //  settings.saveSettings("","","","");
        // Set up the login form.
        mEmailView = (EditText) findViewById(R.id.email);
        failedLoginMessage = (TextView)findViewById(R.id.failed_login);

        mPasswordView = (EditText) findViewById(R.id.password);
        mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == R.id.login || id == EditorInfo.IME_NULL) {
                    attemptLogin();
                    return true;
                }
                return false;
            }
        });

        Button mEmailSignInButton = (Button) findViewById(R.id.email_sign_in_button);
        mEmailSignInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
              attemptLogin();

            }
        });

         if(settings.token!="")
             startMain();

        // uploadAvatar("my_userid")
       //getuser("my_userid");
    }




    private void startMain() {
        Intent main = new Intent(LoginActivity.this, MainActivity.class);
        startActivity(main);
    }


    private ApiInterface getInterfaceService() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        final ApiInterface mInterfaceService = retrofit.create(ApiInterface.class);
        return mInterfaceService;
    }



    private void loginProcessWithRetrofit(final String email, String password){
      ApiInterface mApiService = ApiInterface.retrofit.create(ApiInterface.class);
          Call<Session> mService=  mApiService.newsession(email,password);
        mService.enqueue(new Callback<Session>() {
            @Override
            public void onResponse(Call<Session> call, Response<Session> response) {
                Session mSession = response.body();
                if (mSession.token!="")
                {
                    settings =new Settings(getApplicationContext(), email, mSession.userid,mSession.token,null);
                    startMain();
                }else
                {
                    //  show error
                    failedLoginMessage.setText("Wrong username or password. Try again");
                    mEmailView.requestFocus();
                }

            }

            @Override
            public void onFailure(Call<Session> call, Throwable t) {
                call.cancel();
                Toast.makeText(LoginActivity.this, "Please check your network connection", Toast.LENGTH_LONG).show();
            }
        });
    }


    private void attemptLogin(){
        email=mEmailView.getText().toString();
        password=mPasswordView.getText().toString();
        loginProcessWithRetrofit(email,password);
    }

}

