package com.example.luisafarias.myapplication.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;

import com.example.luisafarias.myapplication.R;
import com.example.luisafarias.myapplication.util.Constants;
import com.wedeploy.android.Callback;
import com.wedeploy.android.WeDeploy;
import com.wedeploy.android.exception.WeDeployException;
import com.wedeploy.android.transport.Response;

public class NewUserActivity extends AppCompatActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_new_user);
	}

	public void createUser(View view) throws WeDeployException {
		EditText editTextNome = (EditText) findViewById(R.id.caixanomee);
		String nome = editTextNome.getText().toString();
		EditText editTextEmail = (EditText) findViewById(R.id.caixaemaill);
		String email = editTextEmail.getText().toString();
		EditText editTextSenha = (EditText) findViewById(R.id.caixaSenha);
		String senha = editTextSenha.getText().toString();

		if (_isValidEmail(email)){
			_weDeploy.auth(Constants.AUTH_URL)
					.createUser(email, senha, nome)
					.execute(new Callback() {
						public void onSuccess(Response response) {
							_openFeedListActivity();
						}

						public void onFailure(Exception e) {
							Log.e(NewUserActivity.class.getName(), e.getMessage());
						}
					});
		}else {
			Snackbar.make(view, "E-mail invalido!",
					Snackbar.LENGTH_LONG).show();
		}

	}

	private void _openFeedListActivity() {
		Intent intent = new Intent(NewUserActivity.this, MainActivity.class);
		startActivity(intent);
	}

	private boolean _isValidEmail(String email){
		if (email!= null && email != null){
			return false;
		}else {
			return Patterns.EMAIL_ADDRESS.matcher(email).matches();
		}

	}

	private WeDeploy _weDeploy = new WeDeploy.Builder().build();
}
