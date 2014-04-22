package fasino.passwordkeeper;

import fasino.passwordkeeper.R;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class LoginActivity extends Activity {

	final String PREFS_NAME = "PasswordSetting";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);

		SharedPreferences settings = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);

		if (settings.getBoolean("first_start", true)) {
		    //call the registration activity
			Intent intent = new Intent(this, RegistrationActivity.class);
			startActivity(intent);
		}
	}
	

	
	public void enter(View Button)
	{
		SharedPreferences settings = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
		String passPref = "0";
		passPref = settings.getString("password", "1");
		if((passPref.equals("0"))||(passPref.equals("1")))
		{
			Toast.makeText(getBaseContext(), getString(R.string.fatalErr)+passPref,
					Toast.LENGTH_LONG).show();
			return;
		}
		EditText pass = (EditText) findViewById(R.id.editTextPassword);
		String passString = pass.getText().toString();
		String decPass = passString;
		passString = Encrypt.md5(passString);
		if(passString.equals(passPref))
		{
			Intent intent = new Intent(this, DataActivity.class);
			intent.putExtra("password", decPass);
			startActivity(intent);
			finish();
			return;
		}
		pass.setText("");
		Toast.makeText(getBaseContext(), getString(R.string.errPass), Toast.LENGTH_LONG).show();
	}	
}
