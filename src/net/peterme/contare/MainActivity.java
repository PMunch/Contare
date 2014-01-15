package net.peterme.contare;

import com.google.android.vending.licensing.AESObfuscator;
import com.google.android.vending.licensing.LicenseChecker;
import com.google.android.vending.licensing.LicenseCheckerCallback;
import com.google.android.vending.licensing.ServerManagedPolicy;

import android.os.Bundle;
import android.provider.Settings.Secure;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.text.InputFilter;
import android.text.method.DigitsKeyListener;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;
import android.view.View.OnTouchListener;

public class MainActivity extends Activity {
	
	private TextView number;
	private SharedPreferences settings;
	private Editor editor;
	private int num;
	private EditText input;
	private LicenseCheckerCallback mLicenseCheckerCallback;
	
	private String PREFS = "ContarePrefs";
	private String TAG = "Contare";
	private static final String BASE64_PUBLIC_KEY = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAmfoC2cp/GnuxIk4lDd7wLkYRO/pnh02deWo9QWO97f3oKDc9fEU6mPCSEyXWy1Rg3KAE3GQU21LG9DG387lo2X3Y4D07XZ9VZk53bUGoJT6jvvUOOfEugAfrM3kwZ7Y08vTlIw9Ovry2R7E+4gGCZPQe5DEL7e4cECHAzHw2Lt/tWBcLWGpQWMHuMTJaNCosHHvrnGudSVwQ9l4cbHPQGISQ7igIgW3EvpOlN/S1IrYj9id2HGFdig8zforWFrLGOLY3CI2wWctfBh4+GaG0Ip2ze8AK3SQZXOFX2b4ghfv6d00klOjKz+V2Cugy6Q6W7/2ofeF99nRJGEnuuI4QQQIDAQAB";
	private static final byte[] SALT = new byte[] {
		37, 27, -18, 109, 98, 123, -2, -39, -71, -51, 95, 104, -66, -63, 65, 67, -122, 51, -29, -37
    };
	
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
	
		input = new EditText(this);
		input.setFilters(new InputFilter[] {
			new InputFilter.LengthFilter(5),
			DigitsKeyListener.getInstance()
		});
		input.setKeyListener(DigitsKeyListener.getInstance());
		
		final GestureDetector gestureDetector = new GestureDetector(this,new GestureDetector.SimpleOnGestureListener() {
		    public boolean onDoubleTap(MotionEvent e) {
		        num=0;
		        number.setText(Integer.toString(num));
		        return true;
		    }
		    public void onLongPress(MotionEvent e){
		    	AlertDialog dialog = new AlertDialog.Builder(MainActivity.this)
			    .setTitle("Type new number")
			    .setView(input)
			    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
			        public void onClick(DialogInterface dialog, int whichButton) {
			            num=Integer.parseInt(input.getText().toString());
			            number.setText(Integer.toString(num));
			        }
			    }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
			        public void onClick(DialogInterface dialog, int whichButton) {
			            // Do nothing.
			        }
			    }).create();
		    	dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
			    dialog.show();
		    }
		});
		number.setOnTouchListener(new OnTouchListener() {
		    public boolean onTouch(View v, MotionEvent event) {
		        return gestureDetector.onTouchEvent(event);
		    }
		});
		
		mLicenseCheckerCallback = new MyLicenseCheckerCallback();
		new LicenseChecker(this, 
				new ServerManagedPolicy(this,
	            new AESObfuscator(SALT, getPackageName(), Secure.getString(getContentResolver(), Secure.ANDROID_ID))),BASE64_PUBLIC_KEY)
				.checkAccess(mLicenseCheckerCallback);
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
		if (view.getId()==R.id.minus){
			num--;
		}else if (view.getId()==R.id.plus){
			num++;
		}
		number.setText(Integer.toString(num));
	}
	
	private class MyLicenseCheckerCallback implements LicenseCheckerCallback {
        public void allow(int policyReason) {
            if (isFinishing()) {
                // Don't update UI if Activity is finishing.
                return;
            }
        }

        public void dontAllow(int policyReason) {
            if (isFinishing()) {
                // Don't update UI if Activity is finishing.
                return;
            }
            finish();
        }

        public void applicationError(int errorCode) {
            if (isFinishing()) {
                // Don't update UI if Activity is finishing.
                return;
            }
            finish();
        }
    }
}
