package net.peterme.contare;

import android.os.Bundle;
import android.app.Activity;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class MainActivity extends Activity {
	
	private TextView number;
	private SharedPreferences settings;
	private Editor editor;
	private int num;
	private String PREFS = "ContarePrefs";
	private String TAG = "Contare";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		number = (TextView) findViewById(R.id.number);
//		((ImageView) findViewById(R.id.plus))
		
		settings = getSharedPreferences(PREFS,0);
		editor = settings.edit();
		num = settings.getInt("number",0);
		
		number.setText(Integer.toString(num));
	}
	protected void onPause(){
		super.onPause();
		editor.putInt("number", num);
		editor.commit();
	}
	protected void onResume(){
		super.onResume();
		settings = getSharedPreferences(PREFS,0);
		editor = settings.edit();
		num = settings.getInt("number",0);
		number.setText(Integer.toString(num));
	}
	public void onClicked(View view){
		if (((ImageView) view).getId()==R.id.minus){
			num--;
		}else{
			num++;
		}
		number.setText(Integer.toString(num));
	}
}
