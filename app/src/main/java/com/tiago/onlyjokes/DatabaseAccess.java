package com.tiago.onlyjokes;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class DatabaseAccess {
    private SQLiteOpenHelper openHelper;
    private SQLiteDatabase database;
    private static DatabaseAccess instance;

    public static final String DB_TABLE = "jokesTable";
    public static final String KEY_ID = "_id";
    public static final String KEY_QUESTION = "question";
    public static final String KEY_JOKE = "joke";
    public static final String KEY_PRO = "pro";
    public static final String KEY_LEVEL = "level";
    public static final String KEY_CATEGORY = "category";
    public static final String KEY_TAGS = "tags";
    public static final String KEY_TENSE = "tense";
    public static final String KEY_GRAMMAR = "grammar";
    public static final String KEY_PASSIVE = "passive";


    /**
     * Private constructor to avoid object creation from outside classes.
     *
     * @param context
     */
    private DatabaseAccess(Context context) {
        this.openHelper = new DatabaseOpenHelper(context);
    }

    /**
     * Return a singleton instance of DatabaseAccess.
     *
     * @param context the Context
     * @return the instance of DabaseAccess
     */
    public static DatabaseAccess getInstance(Context context) {
        if (instance == null) {
            instance = new DatabaseAccess(context);
        }
        return instance;
    }

    /**
     * Open the database connection.
     */
    public void open() {
        this.database = openHelper.getWritableDatabase();
    }

    /**
     * Close the database connection.
     */
    public void close() {
        if (database != null) {
            this.database.close();
        }
    }

    /**
     * Read all quotes from the database.
     *
     * @return a List of questions
     */

    /* Insert into database*/
    /**public void insertIntoDB(String name,String email,String roll,String address,String branch){
        Log.d("insert", "before insert");

        // 1. get reference to writable DB
        SQLiteDatabase db = this.getWritableDatabase();

        // 2. create ContentValues to add key "column"/value
        ContentValues values = new ContentValues();
        values.put("name", name);
        values.put("email", email);
        values.put("roll", roll);
        values.put("address", address);
        values.put("branch", branch);

        // 3. insert
        db.insert(STUDENT_TABLE, null, values);
        // 4. close
        db.close();
        Toast.makeText(context, "insert value", Toast.LENGTH_LONG);
        Log.i("insert into DB", "After insert");
    }*/
    /* Retrieve  data from database */
    /**public List<DatabaseModel> getDataFromDB(String tag)throws SQLException{
        List<DatabaseModel> modelList = new ArrayList<DatabaseModel>();
        String query = "select * from "+DB_TABLE+" where "+KEY_TAGS+" LIKE '%" + tag + "%'";
        Cursor cursor = database.rawQuery(query,null);

        if (cursor.moveToFirst()){
            do {
                DatabaseModel model = new DatabaseModel();
                model.setJoke(cursor.getString(0));
                model.setEmail(cursor.getString(1));
                model.setTags(cursor.getString(2));
                model.setSource(cursor.getString(3));
                model.setCategory(cursor.getString(4));

                modelList.add(model);
            }while (cursor.moveToNext());
        }
        //Log.d("student data", modelList.toString());
        return modelList;
    }*/

    /* Retrieve  data from database */
    public List<DatabaseModel> getDataFromDBnew(String tag, String inputText)throws SQLException{
        List<DatabaseModel> modelList = new ArrayList<DatabaseModel>();
        Cursor cursor;
        if("favorites".equals(MainActivity.screen)){
            String[] names = MainActivity.favsList.toArray(new String[MainActivity.favsList.size()]); // do whatever is needed first
            //System.out.println("cheguei getDataFromDBnew - favs string array: "+names);
            String query = "SELECT * FROM "  + DB_TABLE +  " WHERE "+KEY_ID+" IN (" + makePlaceholders(names.length) + ")";
            cursor = database.rawQuery(query, names);
        }else {//it is not favorites
            //System.out.println("cheguei getDataFromDBnew - not favs - screen: "+MainActivity.screen);
            if(MainActivity.isPro){
                String query = "select * from " + DB_TABLE + " where " + KEY_TAGS + " LIKE '%" + tag + "%' AND " + KEY_JOKE + " like '%" + inputText + "%'";
                cursor = database.rawQuery(query, null);
            }else{
                String query = "select * from " + DB_TABLE + " where " + KEY_TAGS + " LIKE '%" + tag + "%' AND " + KEY_JOKE + " like '%" + inputText + "%' AND " + KEY_PRO + " like '%no%'";
                cursor = database.rawQuery(query, null);
            }
            //cursor = database.rawQuery(query, null);
        }

        if (cursor.moveToFirst()){
            do {
                DatabaseModel model = new DatabaseModel();
                model.setJoke(cursor.getString(0));
                model.setEmail(cursor.getString(1));
                model.setTags(cursor.getString(2));
                model.setSource(cursor.getString(3));
                model.setCategory(cursor.getString(4));

                modelList.add(model);
            }while (cursor.moveToNext());
        }
        //Log.d("student data", modelList.toString());
        return modelList;
    }

    String makePlaceholders(int len) {//for favorites
        if (len < 1) {
            // It will lead to an invalid query anyway ..
            throw new RuntimeException("No placeholders");
        } else {
            StringBuilder sb = new StringBuilder(len * 2 - 1);
            sb.append("?");
            for (int i = 1; i < len; i++) {
                sb.append(",?");
            }
            return sb.toString();
        }
    }

    /*delete a row from database*/
    /**public void deleteARow(String email){
        SQLiteDatabase db= this.getWritableDatabase();
        db.delete(STUDENT_TABLE, "email" + " = ?", new String[] { email });
        db.close();
    }*/
}
