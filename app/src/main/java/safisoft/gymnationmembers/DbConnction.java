package safisoft.gymnationmembers;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by root on 9/23/17.
 */

public class DbConnction extends SQLiteOpenHelper {



    String DB_PATH = null;
    private static String DB_NAME = "gym_nation_members_localdatabase";
    private SQLiteDatabase myDataBase;
    private final Context myContext;

    public DbConnction(Context context) {
        super(context, DB_NAME, null, 10);
        this.myContext = context;
        this.DB_PATH = "/data/data/" + context.getPackageName() + "/" + "databases/";
        Log.e("Path 1", DB_PATH);
    }

    public void createDataBase() throws IOException {
        boolean dbExist = checkDataBase();
        if (dbExist) {
        } else {
            this.getReadableDatabase();
            try {
                copyDataBase();
            } catch (IOException e) {
                throw new Error("Error copying database");
            }
        }
    }

    private boolean checkDataBase() {
        SQLiteDatabase checkDB = null;
        try {
            String myPath = DB_PATH + DB_NAME;
            checkDB = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READWRITE);
        } catch (SQLiteException e) {
        }
        if (checkDB != null) {
            checkDB.close();
        }
        return checkDB != null ? true : false;
    }


    private void copyDataBase() throws IOException {
        InputStream myInput = myContext.getAssets().open(DB_NAME);
        String outFileName = DB_PATH + DB_NAME;
        OutputStream myOutput = new FileOutputStream(outFileName);
        byte[] buffer = new byte[10];
        int length;
        while ((length = myInput.read(buffer)) > 0) {
            myOutput.write(buffer, 0, length);
        }
        myOutput.flush();
        myOutput.close();
        myInput.close();
    }

    public void openDataBase() throws SQLException {
        String myPath = DB_PATH + DB_NAME;
        myDataBase = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READONLY);
        super.onOpen(myDataBase);
        myDataBase.disableWriteAheadLogging();

    }

    @Override
    public synchronized void close() {
        if (myDataBase != null)
            myDataBase.close();
        super.close();
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (newVersion > oldVersion)
            try {
                copyDataBase();
            } catch (IOException e) {
                e.printStackTrace();

            }
    }

    @Override
    public void onOpen(SQLiteDatabase db) {//fix database problem on api28+
        super.onOpen(db);
        db.disableWriteAheadLogging();
    }

    public Cursor query_user_data(String table, String[] columns, String selection, String[] selectionArgs, String groupBy, String having, String orderBy) {
        return myDataBase.query(table,null,null,null,null,null,null);
    }


   public Cursor row_query(String Tapel_name, String column_name, String data){

              return getReadableDatabase().rawQuery("SELECT * FROM " + Tapel_name + " WHERE " + column_name + "=?", new String[] { data });
   }

  //ublic void insertRecord(String table_name , String lat, String lng) {
  //   SQLiteDatabase database = this.getReadableDatabase();
  //   ContentValues contentValues = new ContentValues();
  //   contentValues.put("location_name",locName);
  //   contentValues.put("lat",lat);
  //   contentValues.put("long",lng);
  //   database.insert(table_name, null, contentValues);
  //   database.close();
  //

    public void updateRecord(String TABEL_NAME, String COLUMN_NAME, String RECORD_ID, int COLUMN_VAL) {
        SQLiteDatabase database = this.getReadableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_NAME,COLUMN_VAL);
        database.update(TABEL_NAME, contentValues, "_id" + " = ?", new String[]{RECORD_ID});
        database.close();
    }

    public void updateRecord_string(String TABEL_NAME, String COLUMN_NAME, String RECORD_ID, String COLUMN_VAL) {
        SQLiteDatabase database = this.getReadableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_NAME,COLUMN_VAL);
        database.update(TABEL_NAME, contentValues, "_id" + " = ?", new String[]{RECORD_ID});
        database.close();
    }

    public void update_member_data(String member_name , String profile_img) {
        SQLiteDatabase database = this.getReadableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("member_name",member_name);
        contentValues.put("profile_img",profile_img);
        database.update("member_data", contentValues, "_id" + " = ?", new String[]{"1"});
        database.close();
    }



    public void deleteRecordAlternate(String TABLE_NAME, String COLUMN_ID, String ID_NUM) {
       SQLiteDatabase database = this.getReadableDatabase();
        database.execSQL("delete from " + TABLE_NAME + " where " + COLUMN_ID + " = '" + ID_NUM + "'");
        database.close();
    }


                 // myDbHelper.updateRecord("fake_location", "fake_lat", "1", s_map_fake_lat);




    public void update_id_notification_count(int id_update){
        updateRecord("id_notification_check","id_notification_val","1",id_update);
    }


    public void update_id_post_count(int id_update){
        updateRecord("id_post_check","id_post_val","1",id_update);
    }

    public void update_id_member_count(int id_update){
        updateRecord("id_member_check","id_member_val","1",id_update);
    }


    public void update_id_workout_count(int id_update){
        updateRecord("id_workout_check","id_workout_val","1",id_update);
    }


    public void update_notification_statu(String column_name , String statu_val){
        updateRecord_string("notification_check",column_name,"1",statu_val);
    }



    public void update_gym_info_data(String gym_name,String gym_logo,String gym_database_url) {
        SQLiteDatabase database = this.getReadableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("gym_name",gym_name);
        contentValues.put("gym_logo",gym_logo);
        contentValues.put("gym_database_url",gym_database_url);
        database.update("gym_info_local_database", contentValues, "_id" + " = ?", new String[]{"1"});
        database.close();
    }


    public void update_member_rfid_data(String rfid) {
        SQLiteDatabase database = this.getReadableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("rfid",rfid);
        database.update("member_rfid_local_database", contentValues, "_id" + " = ?", new String[]{"1"});
        database.close();
    }




}