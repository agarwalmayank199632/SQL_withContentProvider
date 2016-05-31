package com.mayank.newsqltry;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import static com.mayank.newsqltry.DatabaseHelper.TABLE_TUTORIALS;


public class DBContentProvider extends ContentProvider {

    private DatabaseHelper dbHelper;
    private SQLiteDatabase db;
    private static final String AUTHORITY="com.mayank.newsqltry.DBContentProvider";
    private static final int MOVIES=100;
    private static final int MOVIE_ID=110;

    public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/" + TABLE_TUTORIALS);

    //public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/mt-tutorial";
    //public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/mt-tutorial";
    private static final UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    static {
        uriMatcher.addURI(AUTHORITY, TABLE_TUTORIALS, MOVIES);
        uriMatcher.addURI(AUTHORITY, TABLE_TUTORIALS + "/#", MOVIE_ID);
    }

    @Override
    public boolean onCreate() {
        dbHelper = new DatabaseHelper(getContext());

        // permissions to be writable
        db = dbHelper.getWritableDatabase();
        return db != null;
    }

    @SuppressWarnings("ConstantConditions")
    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
        queryBuilder.setTables(TABLE_TUTORIALS);

        int uriType = uriMatcher.match(uri);
        switch (uriType) {
            case MOVIE_ID:
                queryBuilder.appendWhere(DatabaseHelper.ID + "=" + uri.getLastPathSegment());
                break;
            case MOVIES:
                break;
            default:
                throw new IllegalArgumentException("Unknown URI");
        }

        Cursor cursor = queryBuilder.query(dbHelper.getReadableDatabase(),
                projection, selection, selectionArgs, null, null, sortOrder);
        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }

    @SuppressWarnings("ConstantConditions")
    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, ContentValues values) {
        long row = db.insert(TABLE_TUTORIALS, "", values);

        // If record is added successfully
        if (row > 0) {
            Uri newUri = ContentUris.withAppendedId(CONTENT_URI, row);
            getContext().getContentResolver().notifyChange(newUri, null);
            return newUri;
        }
        throw new SQLException("Fail to add a new record into " + uri);

    }

    @SuppressWarnings("ConstantConditions")
    @Override
    public int delete(@NonNull Uri uri, String selection, String[] selectionArgs) {
        int uriType = uriMatcher.match(uri);
        int rowsAffected;
        switch (uriType) {
            case MOVIES:
                rowsAffected = db.delete(TABLE_TUTORIALS, selection, selectionArgs);
                break;
            case MOVIE_ID:
                String id = uri.getLastPathSegment();
                if (TextUtils.isEmpty(selection)) {
                    rowsAffected = db.delete(TABLE_TUTORIALS, DatabaseHelper.ID + "=" + id, null);
                } else {
                    rowsAffected = db.delete(TABLE_TUTORIALS, selection + " and " + DatabaseHelper.ID + "=" + id, selectionArgs);
                }
                break;
            default:
                throw new IllegalArgumentException("Unknown or Invalid URI " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);

        return rowsAffected;
    }

    @Override
    public int update(@NonNull Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        return 0;
    }
}
