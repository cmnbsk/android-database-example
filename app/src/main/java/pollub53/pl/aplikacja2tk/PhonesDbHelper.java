package pollub53.pl.aplikacja2tk;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Tobiasz on 2017-03-28.
 */

public class PhonesDbHelper extends SQLiteOpenHelper {

    public final static int DB_VERSION = 5;
    public final static String ID = "_id";
    public final static String DB_NAME = "smartphones_db";
    public final static String TABLE_NAME = "smartphone";
    public final static String COLUMN_BRAND = "brand";
    public final static String COLUMN_MODEL = "model";
    public final static String COLUMN_ANDROID = "android";
    public final static String COLUMN_WWW = "www";
    public final static String DB_CREATE = "CREATE TABLE " + TABLE_NAME +
            "(" + ID + " integer primary key autoincrement, " +
            COLUMN_BRAND + " text not null," +
            COLUMN_MODEL + " text not null," +
            COLUMN_ANDROID + " text," +
            COLUMN_WWW + " text);";
    public static final String DELETE_TABLE = "DROP TABLE IF EXISTS " + TABLE_NAME; //TODO to private


    public PhonesDbHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(DB_CREATE);
        //execSQL - polecenia nie zwracające danych
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL(DELETE_TABLE);
        sqLiteDatabase.execSQL(DB_CREATE);
    }


}
