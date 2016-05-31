package com.mayank.newsqltry;

import android.app.Dialog;
import android.content.ContentValues;
import android.database.Cursor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private List<Friend> friendList;
    private ListViewAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        GridView listView = (GridView) findViewById(R.id.list);
        friendList = new ArrayList<>();

        //set Listview adapter
        adapter = new ListViewAdapter(this, R.layout.item_listview, friendList);
        listView.setAdapter(adapter);
        showAllFriends();
    }

    public void showAllFriends() {
        friendList.clear(); //clear old arraylist data first
        Cursor cursor = getContentResolver().query(DBContentProvider.CONTENT_URI, null, null, null, null);

        if (!cursor.moveToFirst()) {
            Toast.makeText(this, " no record yet!", Toast.LENGTH_SHORT).show();
        } else {
            do {
                String name = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COL_TITLE));
                String job = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COL_URL));

                //Loading to arraylist to set adapter data for ListView
                Friend friend = new Friend(name, job);
                friendList.add(friend);

            } while (cursor.moveToNext());
        }
        adapter.notifyDataSetChanged();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.add:
                showAddingDialog();
                break;

            default:
                break;

        }
        return super.onOptionsItemSelected(item);
    }

    private void showAddingDialog() {
        // custom dialog
        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.layout_dialog_add);
        dialog.setTitle("Adding a new friend");

        final EditText txtName = (EditText) dialog.findViewById(R.id.name);
        final EditText txtJob = (EditText) dialog.findViewById(R.id.job);

        Button btnAdd = (Button) dialog.findViewById(R.id.btn_ok);
        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!hasText(txtJob) || !hasText(txtName)) {
                    Toast.makeText(getBaseContext(), "Please input full information...", Toast.LENGTH_SHORT).show();
                } else {
                    //Adding new record to database with Content provider
                    // Add a new birthday record
                    ContentValues values = new ContentValues();
                    values.put(DatabaseHelper.COL_TITLE, getText(txtName));
                    values.put(DatabaseHelper.COL_URL, getText(txtJob));
                    getContentResolver().insert(DBContentProvider.CONTENT_URI, values);

                    Toast.makeText(getBaseContext(), "Inserted!", Toast.LENGTH_SHORT).show();
                    //reloading data
                    showAllFriends();
                    //dismiss dialog after adding process
                    dialog.dismiss();
                }
            }
        });

        dialog.show();
    }

    private boolean hasText(TextView textView) {
        return !textView.getText().toString().trim().equals("");
    }

    private String getText(TextView textView) {
        return textView.getText().toString().trim();
    }
}