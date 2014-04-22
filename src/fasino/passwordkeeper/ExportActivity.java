package fasino.passwordkeeper;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import fasino.passwordkeeper.R;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class ExportActivity extends Activity {

	private String password;
	private Boolean isEncry = false;
	
	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_export);
		
		Intent intent = getIntent();
		password = intent.getStringExtra("password");
		
		if (Build.VERSION.SDK_INT > 11)
		{
			ActionBar actionBar = getActionBar();
			actionBar.setDisplayHomeAsUpEnabled(true);
		}
	}
	
	@SuppressWarnings("deprecation")
	public void export(View Button)
	{
		EditText passEnc = (EditText) findViewById(R.id.editText1);
		String passEncryption = passEnc.getText().toString();
		
		if((isEncry == true)&&(passEncryption.length() < 4))
		{
			if(passEncryption.length() < 1)
				Toast.makeText(getBaseContext(), getString(R.string.fieldM), Toast.LENGTH_SHORT).show();
			else
				Toast.makeText(getBaseContext(), getString(R.string.lenPass), Toast.LENGTH_SHORT).show();
			return;
		}
		
		String toFile = "";

		MyDatabase Mdb = new MyDatabase(this);
		Mdb.open();
		
		final Cursor cursor = Mdb.fetchProducts();
		startManagingCursor(cursor);
		
		int serviceCol = cursor.getColumnIndex("service"); //indici
		int usrCol = cursor.getColumnIndex("username"); //indici
		int passCol = cursor.getColumnIndex("password"); //indici
		boolean start = true;

		if(cursor.moveToFirst()){  //se va alla prima entry, il cursore non è vuoto
			
			do {
	    		if(start == false)
	    			toFile += "\n";
				toFile += cursor.getString(serviceCol);
				toFile += "\n";
				toFile += cursor.getString(usrCol);
				toFile += "\n";
				String psw = cursor.getString(passCol);
				toFile += Encrypt.decryptString(psw, password);
				start = false;
			}while(cursor.moveToNext()); //iteriamo al prossimo elemento
		}

		stopManagingCursor(cursor);
		cursor.close();
		Mdb.close();
		if(isEncry == true)
			toFile = Encrypt.encrypt(toFile, passEncryption);
		confirmAlert(toFile);
	}
	
	public void generateNoteOnSD(String sFileName, String sBody){
	    try
	    {
	        File root = new File(Environment.getExternalStorageDirectory(), "KeepPassword");
	        if (!root.exists()) {
	            root.mkdirs();
	        }
	        File gpxfile = new File(root, sFileName);
	        FileWriter writer = new FileWriter(gpxfile);
	        writer.append(sBody);
	        writer.flush();
	        writer.close();
	    }
	    catch(IOException e)
	    {
	         e.printStackTrace();
	         Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
	    }
	   }
	
	@SuppressWarnings("deprecation")
	private final void confirmAlert(final String toFile)
	{
		AlertDialog alertDialog = new AlertDialog.Builder(ExportActivity.this).create();
    	if(isEncry == true)
    		alertDialog.setMessage(getString(R.string.exportEncAlert));
    	else
    		alertDialog.setMessage(getString(R.string.exportClearAlert));
    	
    	alertDialog.setButton(getString(R.string.no), new DialogInterface.OnClickListener() {
    	public void onClick(DialogInterface dialog, int which) {
    	}
    	});
    	alertDialog.setButton2(getString(R.string.yes), new DialogInterface.OnClickListener() {
        	public void onClick(DialogInterface dialog, int which) {
        		
        		generateNoteOnSD("password.txt", toFile);
        		Toast.makeText(getApplicationContext(), getString(R.string.completed), Toast.LENGTH_SHORT).show();
                finish();
        	}
    	});
        
    	alertDialog.show();
	}
	
	public void encryptButton(View button)
	{
		EditText password = (EditText) findViewById(R.id.editText1);
		
		if(isEncry == true)
		{
			isEncry = false;
			password.setVisibility(View.GONE);		
		}else{
			isEncry = true;
			password.setVisibility(View.VISIBLE);
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

}
