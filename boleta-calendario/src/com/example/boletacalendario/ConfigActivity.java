package com.example.boletacalendario;

import com.example.boletacalendario.picker.TimePickerFragment;
import com.example.boletacalendario.picker.TimePickerFragment.TimeSelectedListener;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class ConfigActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_config);

		Button b1 = (Button) findViewById(R.id.button1);
		b1.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v) {
				TimeSelectedListener timeListener = new TimeSelectedListener(){

					@Override
					public void afterTimeSelectedListener(int hour, int minute) {
						SharedPreferences mPrefs = getSharedPreferences(getPackageName(),Context.MODE_PRIVATE);
						Editor editor = mPrefs.edit();
						editor.putInt("hour_pre", hour);
						editor.putInt("min_pre", minute);
						
					}
					
				};
				TimePickerFragment newFragment = new TimePickerFragment();
				newFragment.setTimeListener(new TimeSelectedListener() {

					@Override
					public void afterTimeSelectedListener(int hour, int minute) {
						
					}
				});
				newFragment.show(ConfigActivity.this.getFragmentManager(), "datePicker");
			}  
		});
		
		Button b2 = (Button) findViewById(R.id.button2);
		b2.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v) {
				TimeSelectedListener timeListener = new TimeSelectedListener(){

					@Override
					public void afterTimeSelectedListener(int hour, int minute) {
						SharedPreferences mPrefs = getSharedPreferences(getPackageName(),Context.MODE_PRIVATE);
						Editor editor = mPrefs.edit();
						editor.putInt("hour_fin", hour);
						editor.putInt("min_fin", minute);
						
					}
					
				};
				TimePickerFragment newFragment = new TimePickerFragment();
				newFragment.setTimeListener(new TimeSelectedListener() {

					@Override
					public void afterTimeSelectedListener(int hour, int minute) {
						
					}
				});
				newFragment.show(ConfigActivity.this.getFragmentManager(), "datePicker");
			}  
		});
		
		findViewById(R.id.button_back).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				finish();
			}
		});
	}
	
//	public boolean saveData(String name, String value){
//		SharedPreferences mPrefs = getSharedPreferences(getPackageName(),Context.MODE_PRIVATE);
//		Editor editor = mPrefs.edit();
//		editor.putString(name, value);
//		return editor.commit();
//	}
//
//	public String loadData(String name){
//		SharedPreferences mPrefs = getSharedPreferences(getPackageName(),Context.MODE_PRIVATE);
//		return mPrefs.getString(name, null);
//	}
}
