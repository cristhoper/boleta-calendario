package com.example.boletacalendario.picker;

import android.app.Dialog;
import android.app.DialogFragment;
import android.app.TimePickerDialog;
import android.app.TimePickerDialog.OnTimeSetListener;
import android.os.Bundle;
import android.widget.TimePicker;

public class TimePickerFragment extends DialogFragment implements OnTimeSetListener {

	TimeSelectedListener timeListener;
	
	
	public interface TimeSelectedListener {
		void afterTimeSelectedListener(int hour, int minute);
	}
	
	public void setTimeListener(TimeSelectedListener listener){
		this.timeListener = listener;
	}
	
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		return new TimePickerDialog(getActivity(), this, 0, 0, true);
	}

	@Override
	public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
		timeListener.afterTimeSelectedListener(hourOfDay,minute);
	}
}
