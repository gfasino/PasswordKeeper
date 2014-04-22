package fasino.passwordkeeper;

import java.util.ArrayList;

import fasino.passwordkeeper.R;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class ChangePasswordActivity extends Activity {
	
	private String password,pNew;
	private final ArrayList<String> service = new ArrayList<String>();
	private final ArrayList<String> usr = new ArrayList<String>();
	private final ArrayList<String> pass = new ArrayList<String>();
	
	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_change_password);

		Intent intent = getIntent();
		password = intent.getStringExtra("password");
		
		if (Build.VERSION.SDK_INT > 11)
		{
			ActionBar actionBar = getActionBar();
			actionBar.setDisplayHomeAsUpEnabled(true);
		}
	}
	
	public void registration(View Button)
	{
		EditText OldPass = (EditText) findViewById(R.id.editTextOld);
		EditText NewPass = (EditText) findViewById(R.id.editTextPassword);
		EditText ConfPass = (EditText) findViewById(R.id.editTextConfirm);
	
		String pOld = OldPass.getText().toString();
		pNew = NewPass.getText().toString();
		String pConf = ConfPass.getText().toString();
		
		if(!(pOld.equals(password)))
		{
			Toast.makeText(getBaseContext(),getString(R.string.errPass),
					Toast.LENGTH_LONG).show();
			return;
		}
		
		if(!(pNew.equals(pConf)))
		{
			Toast.makeText(getBaseContext(),getString(R.string.noPass),
					Toast.LENGTH_LONG).show();
			return;
		}
		
		if(pNew.length() < 3)
		{
			Toast.makeText(getBaseContext(),getString(R.string.lenPass),
					Toast.LENGTH_LONG).show();
			return;
		}
		
		decriptAndRecript();
		
		final String PREFS_NAME = "PasswordSetting";
		SharedPreferences settings = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
		
		SharedPreferences.Editor editor = settings.edit();
		editor.putString("password", Encrypt.md5(pNew));
		editor.commit();
		Toast.makeText(getApplicationContext(), getString(R.string.completed), Toast.LENGTH_SHORT).show();
		finish();
		Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		startActivity(intent);
	}
	
	@SuppressWarnings("deprecation")
	public void decriptAndRecript()
	{
		MyDatabase Mdb = new MyDatabase(this);
		Mdb.open();
		
		final Cursor cursor = Mdb.fetchProducts();
		startManagingCursor(cursor);

		int serviceCol = cursor.getColumnIndex("service"); //indici
		int usrCol = cursor.getColumnIndex("username"); //indici
		int passCol = cursor.getColumnIndex("password"); //indici
		
		if(cursor.moveToFirst()){  //se va alla prima entry, il cursore non è vuoto
			
			do {
	    		service.add(cursor.getString(serviceCol));
	    		usr.add(cursor.getString(usrCol));
	    		String psw = cursor.getString(passCol);
	    		pass.add(Encrypt.decryptString(psw, password));
			} while (cursor.moveToNext()); //iteriamo al prossimo elemento
		}

		stopManagingCursor(cursor);
		cursor.close();
		Mdb.deleteAll();
		
		for(int i=0;i<service.size();i++)
		{
			Mdb.insertData(service.get(i), usr.get(i), Encrypt.encryptString(pass.get(i), pNew));
		}
		
		Mdb.close();
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    switch (item.getItemId()) {
	        case android.R.id.home:
	        	finish();
	        	return true;
	        default:
	            return super.onOptionsItemSelected(item);
	    }
	}
}
