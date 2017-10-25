package com.example.luisafarias.myapplication;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.luisafarias.myapplication.model.Feed;
import com.example.luisafarias.myapplication.model.Repositorio;
import com.example.luisafarias.myapplication.model.WeDeployActions;
import com.example.luisafarias.myapplication.util.Constants;
import com.wedeploy.android.Callback;
import com.wedeploy.android.WeDeploy;
import com.wedeploy.android.auth.Authorization;
import com.wedeploy.android.auth.TokenAuthorization;
import com.wedeploy.android.exception.WeDeployException;
import com.wedeploy.android.transport.Response;

import org.json.JSONException;
import org.json.JSONObject;

public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        _sharedPref = getSharedPreferences("user",this.MODE_PRIVATE);
        if (_sharedPref.contains("token")&&_sharedPref.contains("userID")){
            String token = _sharedPref.getString("token","");
            String userID = _sharedPref.getString("userID","");

            Intent intent = new Intent(this, MainActivity.class);
            Bundle extra = new Bundle();
            extra.putString("tokenKey",token);
            extra.putString("userID",userID);
            intent.putExtra("tokenUserId",extra);
            startActivity(intent);

        }
        setContentView(R.layout.activity_login);
    }


    public void loginButton(View view) throws JSONException, WeDeployException {
        EditText editTextLogin = (EditText) findViewById(R.id.emailogin);
        String emailLogin = editTextLogin.getText().toString();
        EditText editTextSenha = (EditText) findViewById(R.id.senhalogin);
        String passwordLogin = editTextSenha.getText().toString();
        login(emailLogin,passwordLogin);
    }

    public void login(String emailLogin, String passwordLogin) throws WeDeployException, JSONException {
        final Intent intent = new Intent(this, MainActivity.class);
        final Bundle extra = new Bundle();
        final int privated = this.MODE_PRIVATE;

//        if (_sharedPref.contains("user")){
//            EditText editTextLogin = (EditText) findViewById(R.id.emailogin);
//            EditText editTextSenha = (EditText) findViewById(R.id.senhalogin);
//            String email = _sharedPref.getString("email","");
//            String senha = _sharedPref.getString("password","");
//            editTextLogin.setText(email);
//            editTextSenha.setText(senha);
//
//
//        }

        _weDeploy.auth(Constants.AUTH_URL)
                .signIn(emailLogin,passwordLogin)
                .execute(new Callback() {
                    public void onSuccess(Response response) {
                        Log.d(TAG,"entrei");

                        final JSONObject jsonBody;
                        try {
                            jsonBody = new JSONObject(response.getBody());
                            _token = jsonBody.getString("access_token");

                            //Log.d("shared token",_sharedPref.getString("token",""));

                            WeDeployActions.getInstance().getCurrentUser(
                                    new TokenAuthorization(_token),
                                    new WeDeployActions.CallbackUserID() {
                                @Override
                                public void onSuccess(String userID) {
                                    _sharedPref = getSharedPreferences("user", privated);
                                    SharedPreferences.Editor editor = _sharedPref.edit();
                                    editor.putString("token",_token);
                                    editor.putString("userID",userID);
                                    editor.apply();
                                    _login = true;

                                    extra.putString("tokenKey", _token);
                                    extra.putString("userID",userID);
                                    intent.putExtra("tokenUserId",extra);

                                    finish();

                                    startActivity(intent);
                                }

                                @Override
                                public void onFailure(Exception e) {
                                    Log.e(LoginActivity.class.getName(),e.getMessage());
                                }
                            });


                        } catch (JSONException e) {
                            Log.e(TAG, e.getMessage());
                        }

                        /*
                         * TODO: Toast, Snackbar
                         */
                    }

                    public void onFailure(Exception e) {
                        Log.e(NewUserActivity.class.getName(),e.getMessage());

                        /*
                         * TODO: Toast, Snackbar
                         * e.getMessage();
                         */
                    }
                });

    }
    public void newAccount(View view) {
        Intent intent = new Intent(this, NewUserActivity.class);
        startActivity(intent);
    }
    private static boolean _login = false;
    private SharedPreferences _sharedPref;
    private static final String TAG = LoginActivity.class.getName();
    private String _token;
    private String _userID;
    private WeDeploy _weDeploy = new WeDeploy.Builder().build();
}
