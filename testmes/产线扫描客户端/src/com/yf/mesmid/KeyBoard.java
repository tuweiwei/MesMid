package com.yf.mesmid;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class KeyBoard  extends Activity implements OnClickListener{
	private final String KEYBOARD_MSG="mes.keyboardmsg";
	private final int  DELETE_KEY=101;
	private final int ENTER_KEY=102;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.keyboard);
		Button Btn1 = (Button) findViewById(R.id.button_1);
		Button Btn2 = (Button) findViewById(R.id.button_2);
		Button Btn3 = (Button) findViewById(R.id.button_3);
		Button Btn4 = (Button) findViewById(R.id.button_4);
		Button Btn5 = (Button) findViewById(R.id.button_5);
		Button Btn6 = (Button) findViewById(R.id.button_6);
		Button Btn7 = (Button) findViewById(R.id.button_7);
		Button Btn8 = (Button) findViewById(R.id.button_8);
		Button Btn9 = (Button) findViewById(R.id.button_9);
		Button Btn0 = (Button) findViewById(R.id.button_0);
		Button BtnDelete = (Button) findViewById(R.id.button_delete);
		Button BtnEnter = (Button) findViewById(R.id.button_enter);
		Btn1.setOnClickListener(this);
		Btn2.setOnClickListener(this);
		Btn3.setOnClickListener(this);
		Btn4.setOnClickListener(this);
		Btn5.setOnClickListener(this);
		Btn6.setOnClickListener(this);
		Btn7.setOnClickListener(this);
		Btn8.setOnClickListener(this);
		Btn9.setOnClickListener(this);
		Btn0.setOnClickListener(this);
		BtnDelete.setOnClickListener(this);
		BtnEnter.setOnClickListener(this);
	}

	@Override
	public void onClick(View arg0) {
		int id =arg0.getId();
		int key=0;
		switch (id) {
		case R.id.button_0:
			key=0;
			break;
		case R.id.button_1:
			key=1;		
			break;
		case R.id.button_2:
			key=2;
			break;
		case R.id.button_3:
			key=3;		
			break;
		case R.id.button_4:
			key=4;
			break;
		case R.id.button_5:
			key=5;
			break;
		case R.id.button_6:
			key=6;
			break;
		case R.id.button_7:
			key=7;
			break;
		case R.id.button_8:
			key=8;
			break;
		case R.id.button_9:
			key=9;
			break;
		case R.id.button_delete:
			key=DELETE_KEY;
			break;
		case R.id.button_enter:
			key=ENTER_KEY;
			break;

		default:
			return;
		}
		Intent intent = new Intent();
		intent.setAction(KEYBOARD_MSG);
		intent.putExtra("key", key);
		sendBroadcast(intent);
		if(id==R.id.button_enter) finish();
	}
	
}
