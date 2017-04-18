package pollub53.pl.aplikacja2tk;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Patterns;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class EditPhoneActivity extends AppCompatActivity {

    private long mRowId;
    private EditText mBrandEdit;
    private EditText mModelEdit;
    private EditText mAndroidEdit;
    private EditText mWWWEdit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_phone);

        initializeComponents();

        //new phone or load from database
        mRowId = -1;
        if (savedInstanceState != null)
            mRowId = savedInstanceState.getLong(PhonesDbHelper.ID);
        else {
            Bundle bundle = getIntent().getExtras();
            if (bundle != null)
                mRowId = bundle.getLong(PhonesDbHelper.ID);
        }
        if (mRowId != -1)
            fillEditBox();

    }

    private void initializeComponents(){

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbarEdit);
        mBrandEdit = (EditText) findViewById(R.id.brandEdit);
        mModelEdit = (EditText) findViewById(R.id.modelEdit);
        mAndroidEdit = (EditText) findViewById(R.id.androidEdit);
        mWWWEdit = (EditText) findViewById(R.id.wwwEdit);
        Button save = (Button) findViewById(R.id.saveButton);
        Button cancel = (Button) findViewById(R.id.cancelButton);
        Button www = (Button) findViewById(R.id.wwwButton);

        setSupportActionBar(toolbar);

        // AutoCompleteTextView for android_version input box
        AutoCompleteTextView textView = (AutoCompleteTextView) findViewById(R.id.androidEdit);
        String[] androidVersions = getResources().getStringArray(R.array.android_versions);
        // Create the adapter and set it to the AutoCompleteTextView
        ArrayAdapter<String> adapter =
                new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, androidVersions);
        textView.setAdapter(adapter);

        // Create button click listeners
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveInDatabase();
            }
        });
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cancel();
            }
        });
        www.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openInWebBrowser(mWWWEdit.getText().toString());
            }
        });
    }

    //create toolbar menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_edit_phone, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //back to main activity
        if (id == R.id.action_back) {
            cancel();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    //fill text input box if there are in database
    private void fillEditBox() {
        String projection[] = { PhonesDbHelper.COLUMN_BRAND,
                PhonesDbHelper.COLUMN_MODEL,
                PhonesDbHelper.COLUMN_ANDROID,
                PhonesDbHelper.COLUMN_WWW
        };
        //load from database
        Cursor cursor = getContentResolver().query(
                ContentUris.withAppendedId(PhonesProvider.URI_CONTENT, mRowId),
                projection, null, null, null);
        cursor.moveToFirst();
        //fill input box
        mBrandEdit.setText(cursor.getString(cursor.getColumnIndexOrThrow(PhonesDbHelper.COLUMN_BRAND)));
        mModelEdit.setText(cursor.getString(cursor.getColumnIndexOrThrow(PhonesDbHelper.COLUMN_MODEL)));
        mAndroidEdit.setText(cursor.getString(cursor.getColumnIndexOrThrow(PhonesDbHelper.COLUMN_ANDROID)));
        mWWWEdit.setText(cursor.getString(cursor.getColumnIndexOrThrow(PhonesDbHelper.COLUMN_WWW)));
        cursor.close();
    }

    //send id of edited row
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putLong(PhonesDbHelper.ID, mRowId);
    }

    //open url in default browser
    private void openInWebBrowser(String uriString){
        if(validateURL(uriString)) {
            // add http:// if not exists
            if(!uriString.startsWith("http://") && !uriString.startsWith("https://"))
                uriString = "http://" + uriString;
            Uri uri = Uri.parse(uriString);

            //open web browser
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            startActivity(intent);
        }
        else Toast.makeText(this,
                getString(R.string.validate_toast),
                Toast.LENGTH_SHORT).show();
    }

    //make sure the input boxes are not empty
    private boolean validateTextBox() {

        return !(mBrandEdit.getText().toString().equals("")
                || mModelEdit.getText().toString().equals("")
                || mAndroidEdit.getText().toString().equals("")
                || !validateURL(mWWWEdit.getText().toString()));
    }

    private boolean validateURL(String url){
        return Patterns.WEB_URL.matcher(url).matches();
    }

    //save values from text input to database
    private void saveInDatabase() {
        if (validateTextBox()) {
            ContentValues values = new ContentValues();
            values.put(PhonesDbHelper.COLUMN_BRAND, mBrandEdit.getText().toString());
            values.put(PhonesDbHelper.COLUMN_MODEL, mModelEdit.getText().toString());
            values.put(PhonesDbHelper.COLUMN_ANDROID, mAndroidEdit.getText().toString());
            values.put(PhonesDbHelper.COLUMN_WWW, mWWWEdit.getText().toString());
            if (mRowId == -1) {
                Uri newUri = getContentResolver().insert(PhonesProvider.URI_CONTENT, values);
                mRowId = Integer.parseInt(newUri.getLastPathSegment());
            }
            else {
                int updatedRows = getContentResolver().update(
                        ContentUris.withAppendedId(PhonesProvider.URI_CONTENT, mRowId),
                        values, null, null);
            }
            setResult(RESULT_OK);
            finish();
        }
        else Toast.makeText(this,
                getString(R.string.validate_toast),
                Toast.LENGTH_SHORT).show();
    }

    //exit activity without saving
    private void cancel() {
        setResult(RESULT_CANCELED);
        finish();
    }


}
