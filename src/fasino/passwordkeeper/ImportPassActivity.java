package fasino.passwordkeeper;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class ImportPassActivity extends Activity {

	private TextView description;
	private EditText password;
	private String result = "";
	private String passwordApp;
	
	private static final int REQUEST_PICK_FILE = 1;
	
	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_import_pass);
		
		Intent intent = getIntent();
		passwordApp = intent.getStringExtra("password");
		
		description = (TextView) findViewById(R.id.textView);
		password = (EditText) findViewById(R.id.editText);
		password.setVisibility(View.GONE);
		
		if (Build.VERSION.SDK_INT > 11)
		{
			ActionBar actionBar = getActionBar();
			actionBar.setDisplayHomeAsUpEnabled(true);
		}
	}
	
	public void chooser(View button)
	{
		// Create a new Intent for the file picker activity
		Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
	    intent.setType("file/*");
	    startActivityForResult(intent, REQUEST_PICK_FILE);
	}
	
	public void loadFile(String filePath)
	{
		//importa il file
		Button b = (Button) findViewById(R.id.buttonExpWithPass);
		b.setText(getString(R.string.loadFile));
		description.setText(getString(R.string.isValid));
			
		//Get the text file
		File file = new File(filePath);
		//Read text from file
		StringBuilder text = new StringBuilder();
		
		try{ //carica il testo su String result
		    @SuppressWarnings("resource")
			BufferedReader br = new BufferedReader(new FileReader(file));
		    String line = br.toString();
		    int rowNum = 0;
		    while ((line = br.readLine()) != null) {
	    		if(rowNum > 0)
	    			text.append("\n");
		    	text.append(line);
		    	rowNum++;
		    }

		    result = text.toString();
		    
		    if(rowNum<2) //the file is crypted
		    {
		    	description.setText(getString(R.string.Encrypted));
		    	Button decBtn = (Button) findViewById(R.id.buttonDecrypt);
		    	decBtn.setVisibility(View.VISIBLE);
		    	password.setVisibility(View.VISIBLE);
		    	b.setVisibility(View.GONE);
		    }else{
		    	Button confirm = (Button) findViewById(R.id.buttonImport);
				confirm.setVisibility(View.VISIBLE);
				b.setVisibility(View.GONE);
				//view data
				showData();
		    }
		}
		catch (IOException e) {
		    //You'll need to add proper error handling here
			description.setText(getString(R.string.notValid));
		}
	}
	
	public void confirmImport(View Button)
	{
	    String[] toAdd = result.split("\n");
		MyDatabase Mdb = new MyDatabase(this);
		Mdb.open();
		for(int i=0;i<toAdd.length;i+=3)
		{
			String newPass = toAdd[i+2];
			Mdb.insertData(toAdd[i],toAdd[i+1], Encrypt.encryptString(newPass, passwordApp));	
		}
		Mdb.close();
		Toast.makeText(getBaseContext(), getString(R.string.completed), Toast.LENGTH_SHORT).show();
		setResult(0);
		finish();
	}
	
	public void showData()
	{
		description.setText(getString(R.string.preview)+"\n\n"+result);
	}
	
	public void decrypt(View button)
	{
		EditText pass = (EditText) findViewById(R.id.editText);
		String passString = pass.getText().toString();
		Button b = (Button) button;
		
		if(passString.length() > 1)
		{
			result = Encrypt.decrypt(result, passString); //DECRIPT IT
			Button confirm = (Button) findViewById(R.id.buttonImport);
			confirm.setVisibility(View.VISIBLE);
			b.setVisibility(View.GONE);
			password.setVisibility(View.GONE);
			//show data
			showData();
		}
		else
		{
			Toast.makeText(getApplicationContext(), getString(R.string.fieldM),
					Toast.LENGTH_SHORT).show();
		}
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
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if(resultCode == RESULT_OK) {
			switch(requestCode) {
			case REQUEST_PICK_FILE:
					Uri selectedImageUri = data.getData();

					String filePath = selectedImageUri.getPath();
					
					// call loadFile with Path
					loadFile(filePath);
				break;
			}
		}
	}
}