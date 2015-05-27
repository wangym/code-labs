package com.diaoyumi.android.activity;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

import com.diaoyumi.android.etc.AbstractActivity;
import com.diaoyumi.android.etc.Diaoyumi;
import com.diaoyumi.android.etc.Util;

public class Login extends AbstractActivity {
	private EditText edEmail;
	private EditText edPassword;
	private Button btnRegister;
	private Button btnLogin;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.login);
		edEmail = (EditText) findViewById(R.id.edEmail);
		edPassword = (EditText) findViewById(R.id.edPassword);
		btnRegister = (Button) findViewById(R.id.btnRegister);
		btnLogin = (Button) findViewById(R.id.btnLogin);
		btnRegister.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Diaoyumi.go(Login.this, Register.class,
						Register.PARAMS_EMAIL_NAME, edEmail.getText().toString());
				Login.this.finish();
			}
		});
		btnLogin.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (doLogin() == true)
					Diaoyumi.go(Login.this, Main.class);
			}
		});
	}

	private boolean doLogin() {
		if (!Util.isNotEmpty(edEmail.getText().toString())) {
			Diaoyumi.info(this, "电子邮件必须填写!");
		} else if (!Util.isNotEmpty(edPassword.getText().toString())) {
			Diaoyumi.info(this, "密码必须填写!");
		} else if (!Diaoyumi.getDBAdapter().login(edEmail.getText().toString(),
				edPassword.getText().toString())) {
			Diaoyumi.info(this, "登录验证失败，请检查密码和用户名是否正确!");
			edPassword.setText("");
		} else {
			return true;
		}
		return false;
	}

	@Override
	protected void onStart() {
		super.onStart();
		edEmail.setText("");
		edPassword.setText("");
	}

}
