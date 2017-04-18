package pollub53.pl.aplikacja2tk;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

/**
 * Created by Tobiasz on 2017-04-14.
 */

public class PhonesProvider extends ContentProvider {

    private PhonesDbHelper mPhonesDbHelper;
    private static final String AUTHORITY = "pollub53.pl.aplikacja2tk.PhonesProvider";
    public static final Uri URI_CONTENT = Uri.parse("content://" + AUTHORITY + "/" + PhonesDbHelper.TABLE_NAME);

    private static final int WHOLE_TABLE = 1;
    private static final int SELECTED_ROW = 2;

    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
    static {
        sUriMatcher.addURI(AUTHORITY, PhonesDbHelper.TABLE_NAME, WHOLE_TABLE);
        sUriMatcher.addURI(AUTHORITY, PhonesDbHelper.TABLE_NAME + "/#", SELECTED_ROW);
    }

    @Override
    public boolean onCreate() { //ta metoda powinna wykonywać się możliwie szybko
        mPhonesDbHelper = new PhonesDbHelper(getContext());
        return false;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection,
                        @Nullable String[] selectionArgs, @Nullable String sortOrder) {

        int uriType = sUriMatcher.match(uri);
        SQLiteDatabase db = mPhonesDbHelper.getWritableDatabase();
        Cursor cursor = null;
        switch (uriType) {
            case WHOLE_TABLE:
                cursor = db.query(false, PhonesDbHelper.TABLE_NAME, projection,
                        selection, selectionArgs, null, null, sortOrder, null, null);
                break;
            case SELECTED_ROW:
                cursor = db.query(false, PhonesDbHelper.TABLE_NAME, projection,
                        addIdToSelection(selection, uri),
                        selectionArgs, null, null, sortOrder, null, null);
                break;
            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }
        //db.close();
        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    private String addIdToSelection(String selection, Uri uri){

        if (selection!=null && !selection.equals(""))
            selection = selection + " and " + PhonesDbHelper.ID + "=" + uri.getLastPathSegment();
        else
            selection = PhonesDbHelper.ID + "=" + uri.getLastPathSegment();
        return selection;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues contentValues) {

        int uriType = sUriMatcher.match(uri);
        Uri rowUri = null;
        SQLiteDatabase db = mPhonesDbHelper.getWritableDatabase();
        switch (uriType) {
            case WHOLE_TABLE:
                long rowId = db.insert(PhonesDbHelper.TABLE_NAME, null, contentValues);
                rowUri = Uri.parse("content://" + AUTHORITY + "/" + PhonesDbHelper.TABLE_NAME + "/" + rowId);
                break;
            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return rowUri;
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String s, @Nullable String[] strings) {

        int uriType = sUriMatcher.match(uri);
        SQLiteDatabase db = mPhonesDbHelper.getWritableDatabase();
        int numberDeletedRows = 0;
        switch (uriType) {
            case WHOLE_TABLE:
                numberDeletedRows = db.delete(PhonesDbHelper.TABLE_NAME, null, null);
                break;
            case SELECTED_ROW:
                numberDeletedRows = db.delete(PhonesDbHelper.TABLE_NAME, addIdToSelection(s, uri), strings);
                break;
            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return numberDeletedRows;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues contentValues, @Nullable String selection, @Nullable String[] selectionArgs) {

        int uriType = sUriMatcher.match(uri);
        SQLiteDatabase db = mPhonesDbHelper.getWritableDatabase();
        int numberUpdatedRows = 0;
        switch (uriType) {
            case WHOLE_TABLE:
                numberUpdatedRows = db.update(PhonesDbHelper.TABLE_NAME, contentValues,
                        selection, selectionArgs);
                break;
            case SELECTED_ROW:
                numberUpdatedRows = db.update(PhonesDbHelper.TABLE_NAME, contentValues,
                        addIdToSelection(selection, uri), selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return numberUpdatedRows;
    }
}
