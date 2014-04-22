	package fasino.passwordkeeper;

import fasino.passwordkeeper.R;
import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class RegistrationActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_registration);
	}
	
	public void registration(View Button)
	{
		EditText pass = (EditText) findViewById(R.id.editTextPassword);
		EditText conf = (EditText) findViewById(R.id.editTextConfirm);
		String p1 = pass.getText().toString();
		String p2 = conf.getText().toString();
		if(!(p1.equals(p2)))
		{
			Toast.makeText(getBaseContext(),getString(R.string.noPass),
					Toast.LENGTH_LONG).show();
			return;
		}
		if(p1.length() < 3)
		{
			Toast.makeText(getBaseContext(),getString(R.string.lenPass),
					Toast.LENGTH_LONG).show();
			return;
		}
		
		final String PREFS_NAME = "PasswordSetting";
		SharedPreferences settings = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
		settings.edit().putString("password", Encrypt.md5(p1)).commit();
		settings.edit().putBoolean("first_start", false).commit(); 
		finish();
	}
}