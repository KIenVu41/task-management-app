package com.kma.taskmanagement.data.local;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.kma.taskmanagement.data.model.Task;

public class DatabaseHelper extends SQLiteOpenHelper {

    public static final String DB_NAME = "TaskDB";

    public static final String TABLE_NAME = "tasks";
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_ASSIGN = "assigner_name";
    public static final String COLUMN_CODE = "code";
    public static final String COLUMN_DESCRIPTION = "description";
    public static final String COLUMN_END_DATE = "end_date";
    public static final String COLUMN_NAME = "name";
    public static final String COLUMN_PERFORMER_NAME = "performer_name";
    public static final String COLUMN_PRIORITY = "priority";
    public static final String COLUMN_START_DATE = "start_date";
    public static final String COLUMN_STATUS = "status";
    public static final String COLUMN_CATEGORY_ID = "category_id";
    public static final String COLUMN_GROUP_ID = "group_id";
    public static final String COLUMN_SYNC = "sync";
    //database version
    private static final int DB_VERSION = 1;
    private static final String DATABASE_CREATE_TASKS =
            "create table " +  TABLE_NAME + "(" + COLUMN_ID + " integer, "
                    + COLUMN_ASSIGN + " text," + COLUMN_CODE + " text," + COLUMN_DESCRIPTION + " text,"
                    + COLUMN_END_DATE + " text, " + COLUMN_NAME +  " text," + COLUMN_PERFORMER_NAME + " text, "
                    +  COLUMN_PRIORITY + " text," + COLUMN_START_DATE + " text, " + COLUMN_STATUS + " text, " + COLUMN_CATEGORY_ID + " integer, "
                    +  COLUMN_GROUP_ID + " integer, " + COLUMN_SYNC +   " TINYINT);";


    public DatabaseHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(DATABASE_CREATE_TASKS);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        String sql = "DROP TABLE IF EXISTS " + TABLE_NAME;
        sqLiteDatabase.execSQL(sql);
        onCreate(sqLiteDatabase);
    }

    public boolean addTask(Task task, int sync) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        contentValues.put(COLUMN_ASSIGN, task.getAssigner_name());
        contentValues.put(COLUMN_CODE, task.getCode());
        contentValues.put(COLUMN_DESCRIPTION, task.getDescription());
        contentValues.put(COLUMN_END_DATE, task.getEnd_date());
        contentValues.put(COLUMN_NAME, task.getName());
        contentValues.put(COLUMN_PERFORMER_NAME, task.getPerformer_name());
        contentValues.put(COLUMN_PRIORITY, task.getPriority());
        contentValues.put(COLUMN_START_DATE, task.getStart_date());
        contentValues.put(COLUMN_STATUS, task.getStatus());
        contentValues.put(COLUMN_CATEGORY_ID, task.getCategory_id());
        if(task.getGroup_output_dto() != null) {
            contentValues.put(COLUMN_GROUP_ID, task.getGroup_output_dto().getId());
        }
        contentValues.put(COLUMN_SYNC, sync);


        db.insert(TABLE_NAME, null, contentValues);
        db.close();
        return true;
    }

    public boolean updateTaskStatus(String name, int sync) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_SYNC, sync);
        db.update(TABLE_NAME, contentValues, COLUMN_NAME + "= ?", new String[]{name});
        db.close();
        return true;
    }

    public Cursor getTasks() {
        SQLiteDatabase db = this.getReadableDatabase();
        String sql = "SELECT * FROM " + TABLE_NAME + ";";
        Cursor c = db.rawQuery(sql, null);
        return c;
    }

    public Cursor getUnsyncedTasks() {
        SQLiteDatabase db = this.getReadableDatabase();
        String sql = "SELECT * FROM " + TABLE_NAME + " WHERE " + COLUMN_SYNC + " = 0;";
        Cursor c = db.rawQuery(sql, null);
        return c;
    }
}
