package fasino.passwordkeeper;

import java.util.ArrayList;

import fasino.passwordkeeper.R;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class DataActivity extends Activity {

	private ListView listview = null;
	private final ArrayList<String> list = new ArrayList<String>();
	private final ArrayList<String> id = new ArrayList<String>();
	private final ArrayList<String> service = new ArrayList<String>();
	private final ArrayList<String> usr = new ArrayList<String>();
	private final ArrayList<String> pass = new ArrayList<String>();
	private String password;
	
	@SuppressWarnings("deprecation")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_data);
		Intent intent = getIntent();
		password = intent.getStringExtra("password");
		listview = (ListView) findViewById(R.id.listView1);
		
		MyDatabase Mdb = new MyDatabase(this);
		Mdb.open();
		
		final Cursor cursor = Mdb.fetchProducts(); //SUBJECT
		startManagingCursor(cursor);

		int idCol = cursor.getColumnIndex("id"); //indici
		int serviceCol = cursor.getColumnIndex("service"); //indici
		int usrCol = cursor.getColumnIndex("username"); //indici
		int passCol = cursor.getColumnIndex("password"); //indici
		
		if(cursor.moveToFirst()){  //se va alla prima entry, il cursore non è vuoto
			
			do {
				id.add(cursor.getString(idCol));
	    		service.add(cursor.getString(serviceCol));
	    		usr.add(cursor.getString(usrCol));
	    		String psw = cursor.getString(passCol);
	    		pass.add(Encrypt.decryptString(psw, password));
			} while (cursor.moveToNext()); //iteriamo al prossimo elemento
		}
		
		if(id.size()<1)
			list.add(getString(R.string.emptyData));
		else
			for(int i=0;i<id.size();i++)
			{
				String toAdd = service.get(i) + "\n" + usr.get(i);
				String psw = "";
				for(int c=0;c<pass.get(i).length();c++)
					psw= psw + "*";
				list.add(toAdd+"\n"+psw);
			}
		
		Mdb.close();
		stopManagingCursor(cursor);
		cursor.close();
		
		ArrayAdapter<String> arrayAdapter =  
		        new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1, list);
		        listview.setAdapter(arrayAdapter);
		        
		        listview.setOnItemClickListener(new OnItemClickListener() {
		            public void onItemClick(AdapterView<?> parent, View view,
		                    final int position, long idS) {
		            	
		            	if((position == 0) && (list.get(0).equals(getString(R.string.emptyData))))
		            	{
		    	        	Intent intent = new Intent(getBaseContext(), EditActivity.class);
		    	        	intent.putExtra("ID", "NULL");
		    	        	intent.putExtra("password", password);
		            		startActivityForResult(intent, 0);
		            		return;
		            	}
		            	
		            	AlertDialog alertDialog = new AlertDialog.Builder(DataActivity.this).create();
		            	alertDialog.setTitle(service.get(position));
		            	alertDialog.setMessage("username: "+usr.get(position)+
		            			"\npassword: "+pass.get(position));
		            	alertDialog.setButton(getString(R.string.remove), new DialogInterface.OnClickListener() {
		            	public void onClick(DialogInterface dialog, int which) {
		            		removeData(position);
		            	}
		            	});
		            	alertDialog.setButton2(getString(R.string.Ok), new DialogInterface.OnClickListener() {
			            	public void onClick(DialogInterface dialog, int which) {
			            	
			            	}
			            	});
		            	alertDialog.setButton3(getString(R.string.modify), new DialogInterface.OnClickListener() {
			            	public void onClick(DialogInterface dialog, int which) {
			            		Intent intent = new Intent(getBaseContext(), EditActivity.class);
			    	        	intent.putExtra("ID", id.get(position));
			    	        	intent.putExtra("service", service.get(position));
			    	        	intent.putExtra("username", usr.get(position));
			    	        	intent.putExtra("pass", pass.get(position));
			    	        	intent.putExtra("password", password);
			            		startActivityForResult(intent, 0);
			            	}
			            	});
			            
		            	alertDialog.show();
		            }
			   
	       });
	}

	@SuppressLint("NewApi")
	private void removeData(int r)
	{
		MyDatabase Mdb = new MyDatabase(this);
		Mdb.open();
		Mdb.deleteRow(Integer.parseInt(id.get(r)));
		Mdb.close();

		if (Build.VERSION.SDK_INT > 11)
		    recreate();
		else
		{
			Intent intent = new Intent(getBaseContext(), DataActivity.class);
	    	intent.putExtra("password", password);
			startActivity(intent);
			finish();
		}
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.data, menu);
		return true;
	}

	@SuppressWarnings("deprecation")
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		Intent intent = null;
		switch (item.getItemId()) {
	        case R.id.add:
	        	intent = new Intent(getBaseContext(), EditActivity.class);
	        	intent.putExtra("ID", "NULL");
	        	intent.putExtra("password", password);
        		startActivityForResult(intent, 0);
	            return true;
	        case R.id.exp:
				intent = new Intent(getBaseContext(), ExportActivity.class);
		    	intent.putExtra("password", password);
				startActivity(intent);
				return true;
	        case R.id.imp:
	        	intent = new Intent(getBaseContext(), ImportPassActivity.class);
		    	intent.putExtra("password", password);
				startActivityForResult(intent, 0);
        		return true;
	        case R.id.changePassword:
	        	intent = new Intent(getBaseContext(), ChangePasswordActivity.class);
		    	intent.putExtra("password", password);
				startActivity(intent);
				return true;
	        case R.id.info:
	        	AlertDialog alertDialog = new AlertDialog.Builder(DataActivity.this).create();
            	alertDialog.setTitle(getString(R.string.info));
            	alertDialog.setMessage(getString(R.string.credits));
            	alertDialog.setButton(getString(R.string.Ok), new DialogInterface.OnClickListener() {
	            	public void onClick(DialogInterface dialog, int which) {
	            	
	            	}
	            	});
            	alertDialog.show();
	        	return true;
	        case R.id.exit:
	        	intent = new Intent(getBaseContext(), LoginActivity.class);
	        	startActivity(intent);
	        	finish();
	        	return true;
	        default:
	            return super.onOptionsItemSelected(item);
	    }
	}
	
	@SuppressWarnings("deprecation")
	@Override
	public void startManagingCursor(Cursor c) {

	    // To solve the following error for honeycomb:
	    // java.lang.RuntimeException: Unable to resume activity 
	    // java.lang.IllegalStateException: trying to requery an already closed cursor
	    if (Build.VERSION.SDK_INT < 11) {
	        super.startManagingCursor(c);
	    }
	}

	@SuppressLint("NewApi")
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
	    // Check which request we're responding to
		if (Build.VERSION.SDK_INT > 11)
		    recreate();
		else
		{
			Intent intent = new Intent(getBaseContext(), DataActivity.class);
	    	intent.putExtra("password", password);
			startActivity(intent);
			finish();
		}
	}
}
