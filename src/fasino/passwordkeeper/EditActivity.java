package fasino.passwordkeeper;

import fasino.passwordkeeper.R;
import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class EditActivity extends Activity {

	private String password;
	private String id;
	
	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_edit);
		Intent intent = getIntent();
		id = intent.getStringExtra("ID");
		password = intent.getStringExtra("password");
	
		if(!id.contains("NULL"))
		{
			EditText serviceE = (EditText) findViewById(R.id.editTextService);
			EditText userE = (EditText) findViewById(R.id.editTextUser);
			EditText passE = (EditText) findViewById(R.id.editTextPass);
			serviceE.setText(intent.getStringExtra("service"));
			userE.setText(intent.getStringExtra("username"));
			passE.setText(intent.getStringExtra("pass"));				
		}
		
		if (Build.VERSION.SDK_INT > 11)
		{
			ActionBar actionBar = getActionBar();
			actionBar.setDisplayHomeAsUpEnabled(true);
			if(id.contains("NULL"))
				actionBar.setTitle(getString(R.string.New));
			else
				actionBar.setTitle(intent.getStringExtra("service"));
		}
	}
	
	public void save(View Button)
	{
		EditText serviceE = (EditText) findViewById(R.id.editTextService);
		EditText userE = (EditText) findViewById(R.id.editTextUser);
		EditText passE = (EditText) findViewById(R.id.editTextPass);
		String service = serviceE.getText().toString();
		String user = userE.getText().toString();
		String pass= passE.getText().toString();
		
		if((service.length() < 1 ) || 
				(user.length() < 1 ) || (pass.length() < 1 ))
		{
			Toast.makeText(getBaseContext(), getString(R.string.fieldM), Toast.LENGTH_LONG).show();
			return;
		}
		
		pass = Encrypt.encryptString(pass, password);
		MyDatabase Mdb = new MyDatabase(this);
		Mdb.open();
		Mdb.insertData(service, user, pass);
		if(!id.contains("NULL"))
			Mdb.deleteRow(Integer.parseInt(id));
		Mdb.close();
		setResult(0);
		finish();
		// return con update
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