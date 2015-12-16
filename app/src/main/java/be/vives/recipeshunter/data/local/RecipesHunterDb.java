package be.vives.recipeshunter.data.local;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import be.vives.recipeshunter.data.local.DbContract;

public abstract class RecipesHunterDb {
    private static final String DROP_RECIPE_TABLE = "DROP TABLE IF EXISTS " + DbContract.RECIPE_TABLE;
    private static final String DROP_INGREDIENT_TABLE = "DROP TABLE IF EXISTS " + DbContract.INGREDIENT_TABLE;

    protected SQLiteDatabase database;
    private DbContract dbHelper;
    private Context mContext;
    private boolean isOpen = false;

    public RecipesHunterDb(Context context) {
        this.mContext = context;
        dbHelper = DbContract.getHelper(mContext);
        open();
    }

    public void open() throws SQLException {
        if(dbHelper == null)
            dbHelper = DbContract.getHelper(mContext);
        database = dbHelper.getWritableDatabase();
        isOpen = true;
    }

    public void close() {
        if (isOpen) {
            dbHelper.close();
            database = null;
            isOpen = false;
        }
    }

    private void reset() {
        database.execSQL(DROP_RECIPE_TABLE);
        database.execSQL(DROP_INGREDIENT_TABLE);
        database.execSQL(DbContract.CREATE_RECIPE_TABLE);
        database.execSQL(DbContract.CREATE_INGREDIENT_TABLE);
    }
}


