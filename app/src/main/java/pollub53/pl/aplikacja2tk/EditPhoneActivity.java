package pollub53.pl.aplikacja2tk;

import android.content.ContentUris;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
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

        mBrandEdit = (EditText) findViewById(R.id.brandEdit);
        mModelEdit = (EditText) findViewById(R.id.modelEdit);
        mAndroidEdit = (EditText) findViewById(R.id.androidEdit);
        mWWWEdit = (EditText) findViewById(R.id.wwwEdit);

        mRowId = -1;
        if (savedInstanceState != null)
            mRowId = savedInstanceState.getLong(PhonesDbHelper.ID);
        else {
            Bundle tobolek = getIntent().getExtras();
            if (tobolek != null)
                mRowId = tobolek.getLong(PhonesDbHelper.ID);
        }
        if (mRowId != -1)
            fillEditBox();

        Button save = (Button) findViewById(R.id.saveButton);
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                kliknieciePrzyciskuZapisz();
            }
        });

        Button cancel = (Button) findViewById(R.id.cancelButton);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                kliknieciePrzyciskuAnuluj();
            }
        });
    }

    private void fillEditBox() {
        String projection[] = { PhonesDbHelper.COLUMN_BRAND,
                PhonesDbHelper.COLUMN_MODEL,
                PhonesDbHelper.COLUMN_ANDROID,
                PhonesDbHelper.COLUMN_WWW
        };
        Cursor cursor = getContentResolver().query(
                ContentUris.withAppendedId(PhonesProvider.URI_CONTENT, mRowId),
                projection, null, null, null);
        cursor.moveToFirst();
        int colIndex = cursor .getColumnIndexOrThrow(PhonesDbHelper.COLUMN_BRAND);
        String wartosc = cursor.getString(colIndex);
        mBrandEdit.setText(wartosc);
        mModelEdit.setText(cursor.getString(cursor.getColumnIndexOrThrow(PhonesDbHelper.COLUMN_MODEL)));
        mAndroidEdit.setText(cursor.getString(cursor.getColumnIndexOrThrow(PhonesDbHelper.COLUMN_ANDROID)));
        mWWWEdit.setText(cursor.getString(cursor.getColumnIndexOrThrow(PhonesDbHelper.COLUMN_WWW)));
        cursor.close();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putLong(PhonesDbHelper.ID, mRowId);
    }

    private boolean validateTextBox() {
        return !(mBrandEdit.getText().toString().equals("")
                || mModelEdit.getText().toString().equals("")
                || mAndroidEdit.getText().toString().equals("")
                || mWWWEdit.getText().toString().equals(""));
    }

    private void kliknieciePrzyciskuZapisz() {
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
                getString(R.string.fill_box_toast),
                Toast.LENGTH_SHORT).show();
    }
    private void kliknieciePrzyciskuAnuluj() {
        setResult(RESULT_CANCELED);
        finish();
    }


}
