package be.vives.recipeshunter.data.local;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DbContract extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "RecipeHunter.db";

    /**
     * RecipeDetailsViewModel table
     */
    public static final String RECIPE_TABLE = "recipe";
    public static final String RECIPE_ID = "id";
    public static final String RECIPE_TITLE = "title";
    public static final String RECIPE_PUBLISHER_NAME = "publisher_name";
    public static final String RECIPE_SRC_URL = "src_url";
    public static final String RECIPE_IMG_URL = "image_url";
    public static final String RECIPE_SOCIAL_RANK = "social_rank";

    /**
     * Ingredient table
     */
    public static final String INGREDIENT_TABLE = "ingredient";
    public static final String INGREDIENT_ID = "id";
    public static final String INGREDIENT_NAME = "name";
    public static final String INGREDIENT_RECIPE_ID = "recipe_id";

    /**
     * SQL statements
     */
    public static final String CREATE_RECIPE_TABLE = "CREATE TABLE "
            + RECIPE_TABLE + " (" + RECIPE_ID + " TEXT PRIMARY KEY, "
            + RECIPE_TITLE + " TEXT, "
            + RECIPE_PUBLISHER_NAME + " TEXT, "
            + RECIPE_SRC_URL + " TEXT, "
            + RECIPE_IMG_URL + " TEXT, "
            + RECIPE_SOCIAL_RANK + " INTEGER)";

    public static final String CREATE_INGREDIENT_TABLE = "CREATE TABLE "
            + INGREDIENT_TABLE + " ("
            + INGREDIENT_ID + " INTEGER PRIMARY KEY, "
            + INGREDIENT_NAME + " TEXT, "
            + INGREDIENT_RECIPE_ID + " TEXT, FOREIGN KEY("
            + RECIPE_ID + ") REFERENCES "
            + RECIPE_TABLE + "(id) )";

    private static DbContract instance;

    public static synchronized DbContract getHelper(Context context) {
        if (instance == null) {
            instance = new DbContract(context);
        }
        return instance;
    }

    private DbContract(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onOpen(SQLiteDatabase db) {
        super.onOpen(db);
        if (!db.isReadOnly()) {
            db.execSQL("PRAGMA foreign_keys=ON;");
        }
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_RECIPE_TABLE);
        db.execSQL(CREATE_INGREDIENT_TABLE);
    }

    @Override public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) { } }

