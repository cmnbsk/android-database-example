package pollub53.pl.aplikacja2tk;

import android.app.LoaderManager;
import android.content.ContentUris;
import android.content.CursorLoader;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.widget.SimpleCursorAdapter;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuInflater;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private SimpleCursorAdapter mSimpleCursorAdapter;
    private ListView listView;
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        toolbar = (Toolbar) findViewById(R.id.toolbarMain);
        setSupportActionBar(toolbar);

        configureListView();

        fillPhoneList();
    }

    //list view with MultiChoiceModeListener
    private void configureListView(){
        listView = (ListView) findViewById(R.id.listView);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long id) {

                Intent intent = new Intent(getApplicationContext(), EditPhoneActivity.class);
                intent.putExtra(PhonesDbHelper.ID, id);
                startActivityForResult(intent, 0);
            }
        });

        listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);
        listView.setMultiChoiceModeListener(new AbsListView.MultiChoiceModeListener() {
            @Override
            public boolean onCreateActionMode(android.view.ActionMode actionMode, Menu menu) {
                //Ca
                // actionMode = startSupportActionMode();
                MenuInflater inflater = actionMode.getMenuInflater();
                inflater.inflate(R.menu.menu_list, menu);
                toolbar.setVisibility(View.GONE);

                return true;
            }

            @Override
            public boolean onPrepareActionMode(android.view.ActionMode actionMode, Menu menu) {
                return false;
            }

            @Override
            public boolean onActionItemClicked(android.view.ActionMode actionMode, MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.action_delete_selected:
                        deleteSelected();
                        return true;
                }
                return false;
            }

            @Override
            public void onDestroyActionMode(android.view.ActionMode actionMode) {
                toolbar.setVisibility(View.VISIBLE);
            }

            @Override
            public void onItemCheckedStateChanged(android.view.ActionMode actionMode, int i, long l, boolean b) {
                actionMode.setTitle(listView.getCheckedItemCount()+ " " + getString(R.string.selected_items));
            }
        });
    }

    private void fillPhoneList(){

        getLoaderManager().initLoader(0, null, this);

        // utworzenie mapowania między kolumnami tabeli a kolumnami wyświetlanej listy
        String[] mapFrom = new String[] { PhonesDbHelper.COLUMN_BRAND, PhonesDbHelper.COLUMN_MODEL };
        int[] mapTo = new int[] { R.id.brandLabel, R.id.modelLabel };

        // adapter wymaga aby wyniku zapytania znajdowała się kolumna _id
        mSimpleCursorAdapter = new SimpleCursorAdapter(this, R.layout.list_row, null, mapFrom, mapTo, 0);
        listView.setAdapter(mSimpleCursorAdapter);
    }

    //create toolbar menu
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

        //new element option clicked
        if (id == R.id.action_add) {
            createNewElement();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    //call activity EditPhone with extra row ID
    private void createNewElement() {
        Intent zamiar = new Intent(this, EditPhoneActivity.class);
        zamiar.putExtra(PhonesDbHelper.ID, (long) -1);
        startActivityForResult(zamiar, 0);
    }

    //delete selected items from database
    private void deleteSelected(){
        long selected[] = listView.getCheckedItemIds();
        for (int i = 0; i < selected.length; ++i) {
            getContentResolver().delete(
                    ContentUris.withAppendedId(PhonesProvider.URI_CONTENT, selected[i]),
                    null, null);
        }
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

    //data loader override methods
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
