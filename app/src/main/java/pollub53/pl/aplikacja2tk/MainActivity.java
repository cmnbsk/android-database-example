package pollub53.pl.aplikacja2tk;

import android.app.ListActivity;
import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SimpleCursorAdapter;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;

public class MainActivity extends ListActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private SimpleCursorAdapter mSimpleCursorAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar); //TODO

//        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//               // Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                       // .setAction("Action", null).show();
//                tworzElement();
//            }
//        });

        fillPhoneList();
    }

    private void fillPhoneList(){

        getLoaderManager().initLoader(0, null, this);

        // utworzenie mapowania między kolumnami tabeli a kolumnami wyświetlanej listy
        String[] mapFrom = new String[] { PhonesDbHelper.COLUMN_BRAND, PhonesDbHelper.COLUMN_MODEL };
        int[] mapTo = new int[] { R.id.brandLabel, R.id.modelLabel };

        // adapter wymaga aby wyniku zapytania znajdowała się kolumna _id
        mSimpleCursorAdapter = new SimpleCursorAdapter(this, R.layout.list_row, null, mapFrom, mapTo, 0);
        setListAdapter(mSimpleCursorAdapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_add) {
            tworzElement();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void tworzElement() {
        Intent zamiar = new Intent(this, EditPhoneActivity.class);
        zamiar.putExtra(PhonesDbHelper.ID, (long) -1);
        startActivityForResult(zamiar, 0);
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        Intent intent = new Intent(this, EditPhoneActivity.class);
        intent.putExtra(PhonesDbHelper.ID, id);
        startActivityForResult(intent, 0);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        getLoaderManager().restartLoader(0, null, this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public android.content.Loader<Cursor> onCreateLoader(int id, Bundle args) {

        String[] projection = { PhonesDbHelper.ID, PhonesDbHelper.COLUMN_BRAND, PhonesDbHelper.COLUMN_MODEL };
        CursorLoader cursorLoader = new CursorLoader(this, PhonesProvider.URI_CONTENT, projection, null, null, null);
        return cursorLoader;
    }

    @Override
    public void onLoadFinished(android.content.Loader<Cursor> loader, Cursor cursor) {

        mSimpleCursorAdapter.swapCursor(cursor);
    }

    @Override
    public void onLoaderReset(android.content.Loader<Cursor> loader) {

        mSimpleCursorAdapter.swapCursor(null);
    }
}
