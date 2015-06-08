package com.example.boletacalendario;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.GZIPInputStream;

import android.app.Activity;
import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.googlecode.leptonica.android.Pixa;
import com.googlecode.tesseract.android.TessBaseAPI;

public class MainActivity extends Activity {
	public static final String DATA_PATH = Environment.getExternalStorageDirectory().toString() + "/boleta-calendario/";

	// You should have the trained data file in assets folder
	// You can get them at:
	// http://code.google.com/p/tesseract-ocr/downloads/list
	public static final String lang = "eng";

	private static final String TAG = "Boleta/Calendario";

	private Button scanButton;
	private TextView scannedText;
	
	protected String _path;
	protected boolean _taken;

	protected static final String PHOTO_TAKEN = "photo_taken";

	@Override
	public void onCreate(Bundle savedInstanceState) {

		String[] paths = new String[] { DATA_PATH, DATA_PATH + "tessdata/" };

		for (String path : paths) {
			File dir = new File(path);
			if (!dir.exists()) {
				if (!dir.mkdirs()) {
					Log.v(TAG, "ERROR: Creation of directory " + path + " on sdcard failed");
					return;
				} else {
					Log.v(TAG, "Created directory " + path + " on sdcard");
				}
			}

		}

		// lang.traineddata file with the app (in assets folder)
		// You can get them at:
		// http://code.google.com/p/tesseract-ocr/downloads/list
		// This area needs work and optimization
		if (!(new File(DATA_PATH + "tessdata/" + lang + ".traineddata")).exists()) {
			try {

				AssetManager assetManager = getAssets();
				InputStream in = assetManager.open("tessdata/" + lang + ".traineddata");
				//GZIPInputStream gin = new GZIPInputStream(in);
				OutputStream out = new FileOutputStream(DATA_PATH
						+ "tessdata/" + lang + ".traineddata");

				// Transfer bytes from in to out
				byte[] buf = new byte[1024];
				int len;
				//while ((lenf = gin.read(buff)) > 0) {
				while ((len = in.read(buf)) > 0) {
					out.write(buf, 0, len);
				}
				in.close();
				//gin.close();
				out.close();

				Log.v(TAG, "Copied " + lang + " traineddata");
			} catch (IOException e) {
				Log.e(TAG, "Was unable to copy " + lang + " traineddata " + e.toString());
			}
		}

		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_main);

		scannedText = (TextView) findViewById(R.id.field);
		scanButton = (Button) findViewById(R.id.button1);
		scanButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				startCameraActivity();

			}
		});

		_path = DATA_PATH + "/ocr.jpg";
	}


	// Simple android photo capture:
	// http://labs.makemachine.net/2010/03/simple-android-photo-capture/
	protected void startCameraActivity() {
		File file = new File(_path);
		Uri outputFileUri = Uri.fromFile(file);

		final Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		intent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);

		startActivityForResult(intent, 0);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {

		Log.i(TAG, "resultCode: " + resultCode);

		if (resultCode == -1) {
			onPhotoTaken();
		} else {
			Log.v(TAG, "User cancelled");
		}
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		outState.putBoolean(PHOTO_TAKEN, _taken);
	}

	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		Log.i(TAG, "onRestoreInstanceState()");
		if (savedInstanceState.getBoolean(PHOTO_TAKEN)) {
			onPhotoTaken();
		}
	}

	protected void onPhotoTaken() {
		_taken = true;

		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inSampleSize = 4;

		Bitmap bitmap = BitmapFactory.decodeFile(_path, options);

		try {
			ExifInterface exif = new ExifInterface(_path);
			int exifOrientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
			int rotate = 0;
			switch (exifOrientation) {
				case ExifInterface.ORIENTATION_ROTATE_90: rotate = 90; break;
				case ExifInterface.ORIENTATION_ROTATE_180: rotate = 180; break;
				case ExifInterface.ORIENTATION_ROTATE_270: rotate = 270; break;
			}

			if (rotate != 0) {

				int w = bitmap.getWidth();
				int h = bitmap.getHeight();

				Matrix mtx = new Matrix();
				mtx.preRotate(rotate);

				bitmap = Bitmap.createBitmap(bitmap, 0, 0, w, h, mtx, false);
			}
			// Convert to ARGB_8888, required by tess
			bitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true);

		} catch (IOException e) {
			Log.e(TAG, "Couldn't correct orientation: " + e.toString());
		}

		TessBaseAPI baseApi = new TessBaseAPI();
		baseApi.setDebug(true);
		baseApi.init(DATA_PATH, lang);
		baseApi.setVariable("tessedit_char_whitelist","abcdefghijklmnopqrstuvwxyz-ABCDEFGHIJKLMNOPQRSTUVWXYZ 0123456789/");
		baseApi.setImage(bitmap);

		String recognizedText = baseApi.getUTF8Text();
		
		System.out.println(recognizedText);
		
		Pattern p = Pattern.compile("(0?[1-9]|[12][0-9]|3[01])[- /.](0?[1-9]|1[012])[- /.](19|20)\\d\\d");
		Matcher m = p.matcher(recognizedText);
		String matches = "";
		while (m.find()) { // Find each match in turn; String can't do this.
			matches += m.group();
		}
		
		baseApi.end();

		System.out.println("Fecha: " + matches);
		if ( matches.length() > 0 ) {
			scannedText.setText(matches);
			
			Calendar cal = Calendar.getInstance();         
			cal.setTime(new Date(matches));
			Intent intent = new Intent(Intent.ACTION_EDIT);
			intent.setType("vnd.android.cursor.item/event");
			intent.putExtra("beginTime", cal.getTimeInMillis());
			intent.putExtra("allDay", false);
			intent.putExtra("rrule", "FREQ=DAILY");
			intent.putExtra("endTime", cal.getTimeInMillis()+60*60*1000);
			intent.putExtra("title", "Boleta ...");
			startActivity(intent);
			
		}

	}

}
