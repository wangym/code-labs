package com.diaoyumi.android.activity;

import com.diaoyumi.android.etc.AbstractActivity;
import com.diaoyumi.android.etc.Diaoyumi;
import com.diaoyumi.android.etc.Util;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

public class Register extends AbstractActivity {
	public static final String PARAMS_EMAIL_NAME = "email";
	public static final String PARAMS_PASSWORD = "password";
	private EditText edEmail;
	private EditText edPassword;
	private EditText edNick;
	private Button btnOk;
	private Button btnCancel;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.rigister);
        edEmail = (EditText) findViewById(R.id.edEmail);
        edPassword = (EditText) findViewById(R.id.edPassword);
        edNick = (EditText) findViewById(R.id.edNick);
        btnOk = (Button) findViewById(R.id.btnOk);
        btnCancel = (Button) findViewById(R.id.btnCancel);
        btnCancel.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				startActivity(new Intent(Register.this,Login.class));
			}
		});
        
        btnOk.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (doRegister() == true) 	
					startActivity(new Intent(Register.this, Main.class));
			}
		});
       
        
    }
    
    private boolean doRegister(){
    	if (! Util.isNotEmpty(edEmail.getText().toString())){
    		Diaoyumi.info(this, "电子邮件必须填写!");
    	}else if (! Util.isNotEmpty(edPassword.getText().toString())) {
			Diaoyumi.info(this,"密码必须填写!");
    	}else if (! Util.isNotEmpty(edNick.getText().toString())) {
    		Diaoyumi.info(this,"昵称必须填写!");
		}else if ( ! Diaoyumi.getDBAdapter().register(edEmail.getText().toString(), edNick.getText().toString(), edPassword.getText().toString())){
			Diaoyumi.info(this,"注册失败!");
			edPassword.setText("");
		}else{
			return true;
		}
    	return false;
    }
    
	@Override
	protected void onStart() {
		super.onStart();
		edEmail.setText(Diaoyumi.getExtrasAsString(this,PARAMS_EMAIL_NAME));
		edPassword.setText("");
		edNick.setText("");
	}
   

	
}
