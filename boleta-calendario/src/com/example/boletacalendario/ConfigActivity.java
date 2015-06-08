package com.example.boletacalendario;

import com.example.boletacalendario.picker.ColorPickerDialog;
import com.example.boletacalendario.picker.ColorPickerDialog.OnColorChangedListener;
import com.example.boletacalendario.picker.TimePickerFragment;
import com.example.boletacalendario.picker.TimePickerFragment.TimeSelectedListener;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;

public class ConfigActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_config);

		SharedPreferences mPrefs = getSharedPreferences(getPackageName(),Context.MODE_PRIVATE);

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
						editor.commit();
					}

				};
				TimePickerFragment newFragment = new TimePickerFragment();
				newFragment.setTimeListener(timeListener);
				newFragment.show(ConfigActivity.this.getFragmentManager(), "datePicker");
			}  
		});
		int hour_pre = mPrefs.getInt("hour_pre", 0);
		int min_pre = mPrefs.getInt("min_pre", 0);
		if(hour_pre+min_pre > 0)
			b1.setText(getString(R.string.conf_button1)+ " ("+hour_pre+":"+min_pre+")");
		else
			b1.setText(R.string.conf_button1);

		
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
						editor.commit();
					}

				};
				TimePickerFragment newFragment = new TimePickerFragment();
				newFragment.setTimeListener(timeListener);
				newFragment.show(ConfigActivity.this.getFragmentManager(), "datePicker");
			}  
		});
		int hour_fin = mPrefs.getInt("hour_fin", 0);
		int min_fin = mPrefs.getInt("min_fin", 0);
		if(hour_fin+min_fin > 0)
			b2.setText(getString(R.string.conf_button2)+ " ("+hour_fin+":"+min_fin+")");
		else
			b2.setText(R.string.conf_button2);

		
		CheckBox alarms = (CheckBox)findViewById(R.id.check_alarm);
		alarms.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				SharedPreferences mPrefs = getSharedPreferences(getPackageName(),Context.MODE_PRIVATE);
				Editor editor = mPrefs.edit();
				editor.putBoolean("has_alarm", isChecked);
				editor.commit();

			}
		});
		if(mPrefs.getBoolean("has_alarm", false))
			alarms.setChecked(true);

		
		Button b3 = (Button) findViewById(R.id.button3);
		b3.setOnClickListener(new OnClickListener() { 
			@Override
			public void onClick(View v) {
				ColorPickerDialog picker = new ColorPickerDialog(ConfigActivity.this, 0);
				picker.setOnColorChangedListener(new OnColorChangedListener() {

					@Override
					public void colorChanged(int color) {
						SharedPreferences mPrefs = getSharedPreferences(getPackageName(),Context.MODE_PRIVATE);
						Editor editor = mPrefs.edit();
						editor.putInt("color", color);
						editor.commit();
					}
				});
				picker.show();
			}
		});
		b3.setCompoundDrawablesWithIntrinsicBounds(null, null, new ColorDrawable(mPrefs.getInt("color", 0xff00ff00)), null);

		
		findViewById(R.id.button_back).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				finish();
			}
		});

	}

	@Override
	public void onBackPressed() {
		finish();
	}
}
