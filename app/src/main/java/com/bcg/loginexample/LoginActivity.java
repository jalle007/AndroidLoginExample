package com.bcg.loginexample;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
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
import com.bcg.loginexample.Rest.FakeInterceptor;
import com.bcg.loginexample.Rest.RestClient;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends AppCompatActivity {

    public static final String BASE_URL = ApiInterface.ENDPOINT;

    // UI references.
    private EditText mEmailView;
    private EditText mPasswordView;
    private View mProgressView;
    private View mLoginFormView;
    private TextView failedLoginMessage;

    View focusView = null;
    private String email;
    private String password;

     @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

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
                failedLoginMessage.setText("");
                attemptLogin();
            }
        });

        mLoginFormView = findViewById(R.id.login_form);
        mProgressView = findViewById(R.id.login_progress);


        final SharedPreferences prefs = this.getSharedPreferences("pref", Context.MODE_PRIVATE);
        String email =prefs.getString("email","");
        if(!email.equals(""))
            attemptLogin();
    }

    private void attemptLogin(){
        email=mEmailView.getText().toString();
        password=mPasswordView.getText().toString();

        String isError = checkCredentials(email,password);
        if (isError.equals("")) {
            // redirect to Main Activity page
            Intent main = new Intent(LoginActivity.this, MainActivity.class);
            SharedPreferences prefs = this.getSharedPreferences("pref", Context.MODE_PRIVATE);
            prefs.edit().putString("email", email).apply();
            startActivity(main);

            // Unfortunatelly this part is not  working properly. There is some exception without explanation
            // loginProcessWithRetrofit(email, password);

        } else {
            //  show error
            failedLoginMessage.setText(isError);
            mEmailView.requestFocus();
        }
    }

public  String checkCredentials(String email, String password)
{
 String   testEmail ="jaskobh@hotmail.com";
    String  testPassword = "123456";


    if(email.equals(testEmail))
    {
        if(password.equals(testPassword))
        {
           return ""; //no error credentials are valid
        }else
         {
           // failedLoginMessage.setText("Wrong password. Try again.");
          //  mPasswordView.requestFocus();
             return "Wrong password. Try again.";
        }
        }else
             {
       //failedLoginMessage.setText("Unknown user");
       // mEmailView.requestFocus();
                 return "Unknown user";
    }
  }



    private ApiInterface getInterfaceService() {
        final OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(new FakeInterceptor())
                                .build();
   //     .addConverterFactory(GsonConverterFactory.create())
    //    .addConverterFactory(JacksonConverterFactory.create())

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build();

        final ApiInterface mInterfaceService = retrofit.create(ApiInterface.class);
        return mInterfaceService;
    }


    private void loginProcessWithRetrofit(final String email, String password){
      //  ApiInterface mApiService =   this.getInterfaceService();


         Call<Session> mService=null;
        try {
             mService = RestClient.getClient().newsession(email, password);

        }catch (Exception e)
        {
            Log.v("err", e.getMessage());
        }
       /* Call<Session> mService=null;
        try {
             mService = mApiService.newsession(email, password);

        }catch (Exception e)
        {}
*/
        mService.enqueue(new Callback<Session>() {
            @Override
            public void onResponse(Call<Session> call, Response<Session> response) {

                Session mLoginObject = response.body();
                String returnedResponse = mLoginObject.token;
                Toast.makeText(LoginActivity.this, "Returned " + returnedResponse, Toast.LENGTH_LONG).show();

                //showProgress(false);
                if(returnedResponse.trim().equals("true")){
                    // redirect to Main Activity page
                    Intent loginIntent = new Intent(LoginActivity.this, MainActivity.class);
                    loginIntent.putExtra("email", email);
                    startActivity(loginIntent);
                }
                if(returnedResponse.trim().equals("false")){

                    failedLoginMessage.setText("Login failed. Try again.");
                    mPasswordView.requestFocus();
                }
            }

            @Override
            public void onFailure(Call<Session> call, Throwable t) {
                call.cancel();
                Toast.makeText(LoginActivity.this, "Please check your network connection", Toast.LENGTH_LONG).show();
            }
        });
    }
    }

