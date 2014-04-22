package fasino.passwordkeeper;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class MyDatabase {

	SQLiteDatabase mDb;
	DbHelper mDbHelper;
	Context mContext;
	private static final String DB_NAME="passwordDB";	//nome del db
	private static final int DB_VERSION=1;	//numero di versione del nostro db

	public MyDatabase(Context ctx){
		mContext=ctx;
		mDbHelper=new DbHelper(ctx, DB_NAME, null, DB_VERSION);	//quando istanziamo questa classe, istanziamo anche l'helper (vedi sotto)
	}

	public void open(){	//il database su cui agiamo è leggibile/scrivibile
		mDb=mDbHelper.getWritableDatabase();

	}

	public void close(){ //chiudiamo il database su cui agiamo
		mDb.close();
	}


	public void insertData(String Service, String Username, String Password){ //metodo per inserire i dati
		ContentValues cv=new ContentValues();
		cv.put(MetaData1.SERVICE, Service);
		cv.put(MetaData1.USRNAME, Username);
		cv.put(MetaData1.PASSWORD, Password);
		mDb.insert(MetaData1.TABLE, null, cv);
	}
	
	public Cursor fetchProducts(){ //metodo per fare la query di tutti i dati
			return mDb.query(MetaData1.TABLE, null,null,null,null,null,null);
		}

	public void deleteRow(int row){
		mDb.execSQL("DELETE FROM "+ MetaData1.TABLE + 
				" WHERE " + MetaData1.ID + " = " + Integer.toString(row));
	}

	public void deleteAll(){
		mDb.delete(MetaData1.TABLE, null, null);
	}
		
	//TIMETAB
	static class MetaData1 {  // i metadati della tabella, accessibili ovunque
		static final String TABLE = "data";
		static final String ID = "id";
		static final String SERVICE = "service";
		static final String USRNAME = "username";
		static final String PASSWORD = "password";
	}
	//TIMETAB
	private static final String TABLE1_CREATE = "CREATE TABLE IF NOT EXISTS "  //codice sql di creazione della tabella
			+ MetaData1.TABLE + " ("
			+ MetaData1.ID + " integer primary key autoincrement, "
			+ MetaData1.SERVICE + " text, "
			+ MetaData1.USRNAME + " text, "
			+ MetaData1.PASSWORD + " text);";
		
	private class DbHelper extends SQLiteOpenHelper { //classe che ci aiuta nella creazione del db

		public DbHelper(Context context, String name, CursorFactory factory,int version) {
			super(context, name, factory, version);
		}

		@Override
		public void onCreate(SQLiteDatabase _db) { //solo quando il db viene creato, creiamo la tabella
			_db.execSQL(TABLE1_CREATE); //TIMETAB
		}

		@Override
		public void onUpgrade(SQLiteDatabase _db, int oldVersion, int newVersion) {
			//qui mettiamo eventuali modifiche al db, se nella nostra nuova versione della app, il db cambia numero di versione
		}
	}
}