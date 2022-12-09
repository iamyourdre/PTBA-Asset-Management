package unsri.ptba.assetmanagement.Database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import unsri.ptba.assetmanagement.Util.Config;
import com.orhanobut.logger.AndroidLogAdapter;
import com.orhanobut.logger.Logger;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static DatabaseHelper databaseHelper;

    // All Static variables
    private static final int DATABASE_VERSION = 1;

    // Database Name
    private static final String DATABASE_NAME = Config.DATABASE_NAME;

    // Constructor
    private DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        Logger.addLogAdapter(new AndroidLogAdapter());
    }

    public static synchronized DatabaseHelper getInstance(Context context){
        if(databaseHelper==null){
            databaseHelper = new DatabaseHelper(context);
        }
        return databaseHelper;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        // Create tables SQL execution
        String CREATE_ASSET_TABLE = "CREATE TABLE " + Config.TABLE_ASSET + "("
                + Config.COLUMN_ASSET_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + Config.COLUMN_ASSET_NAME + " TEXT NOT NULL, "
                + Config.COLUMN_ASSET_SN + " TEXT, "
                + Config.COLUMN_ASSET_TAG + " TEXT, " //nullable
                + Config.COLUMN_ASSET_CREATED_AT + " DATETIME, " //yyyy-MM-dd HH:mm:ss
                + Config.COLUMN_ASSET_UPDATED_AT + " DATETIME, " //yyyy-MM-dd HH:mm:ss
                + Config.COLUMN_ASSET_DELETED_AT + " DATETIME " //yyyy-MM-dd HH:mm:ss
                + ")";

        Logger.d("Table create SQL: " + CREATE_ASSET_TABLE);

        db.execSQL(CREATE_ASSET_TABLE);

        Logger.d("DB created!");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + Config.TABLE_ASSET);

        // Create tables again
        onCreate(db);
    }

}
