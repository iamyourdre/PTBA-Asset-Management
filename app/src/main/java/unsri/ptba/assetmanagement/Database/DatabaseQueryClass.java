package unsri.ptba.assetmanagement.Database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.os.Build;
import android.widget.Toast;

import androidx.annotation.RequiresApi;

import com.orhanobut.logger.AndroidLogAdapter;
import com.orhanobut.logger.Logger;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import unsri.ptba.assetmanagement.Features.CreateAsset.Asset;
import unsri.ptba.assetmanagement.Util.Config;

@RequiresApi(api = Build.VERSION_CODES.O)
public class DatabaseQueryClass {

    private Context context;
    private DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public DatabaseQueryClass(Context context){
        this.context = context;
        Logger.addLogAdapter(new AndroidLogAdapter());
    }

    public long insertAsset(Asset asset){

        long id = -1;
        DatabaseHelper databaseHelper = DatabaseHelper.getInstance(context);
        SQLiteDatabase sqLiteDatabase = databaseHelper.getWritableDatabase();

        ContentValues contentValues = new ContentValues();
        contentValues.put(Config.COLUMN_ASSET_NAME, asset.getName());
        contentValues.put(Config.COLUMN_ASSET_SN, asset.getAssetSn());
        contentValues.put(Config.COLUMN_ASSET_TAG, asset.getAssetTag());
        contentValues.put(Config.COLUMN_ASSET_CREATED_AT, asset.getCreatedAt());
        contentValues.put(Config.COLUMN_ASSET_UPDATED_AT, asset.getUpdatedAt());
        contentValues.put(Config.COLUMN_ASSET_DELETED_AT, asset.getDeletedAt());

        try {
            id = sqLiteDatabase.insertOrThrow(Config.TABLE_ASSET, null, contentValues);
        } catch (SQLiteException e){
            Logger.d("Exception: " + e.getMessage());
            Toast.makeText(context, "Operation failed: " + e.getMessage(), Toast.LENGTH_LONG).show();
        } finally {
            sqLiteDatabase.close();
        }

        return id;
    }

    public List<Asset> getAllAsset(){

        DatabaseHelper databaseHelper = DatabaseHelper.getInstance(context);
        SQLiteDatabase sqLiteDatabase = databaseHelper.getReadableDatabase();

        Cursor cursor = null;
        try {

            cursor = sqLiteDatabase.query(Config.TABLE_ASSET, null, null, null, null, null, null, null);

            /**
                 // If you want to execute raw query then uncomment below 2 lines. And comment out above line.

                 String SELECT_QUERY = String.format("SELECT %s, %s, %s, %s, %s FROM %s", Config.COLUMN_ASSET_ID, Config.COLUMN_ASSET_NAME, Config.COLUMN_ASSET_SN, Config.COLUMN_ASSET_CREATED_AT, Config.COLUMN_ASSET_TAG, Config.TABLE_ASSET);
                 cursor = sqLiteDatabase.rawQuery(SELECT_QUERY, null);
             */

            if(cursor!=null)
                if(cursor.moveToLast()){
                    List<Asset> assetList = new ArrayList<>();
                    do {
                        if(cursor.getString(cursor.getColumnIndex(Config.COLUMN_ASSET_DELETED_AT))==null) {
                            int id = cursor.getInt(cursor.getColumnIndex(Config.COLUMN_ASSET_ID));
                            String assetName = cursor.getString(cursor.getColumnIndex(Config.COLUMN_ASSET_NAME));
                            String assetSn = cursor.getString(cursor.getColumnIndex(Config.COLUMN_ASSET_SN));
                            String assetTag = cursor.getString(cursor.getColumnIndex(Config.COLUMN_ASSET_TAG));
                            String createdAt = cursor.getString(cursor.getColumnIndex(Config.COLUMN_ASSET_CREATED_AT));
                            String updatedAt = cursor.getString(cursor.getColumnIndex(Config.COLUMN_ASSET_UPDATED_AT));
                            String deletedAt = cursor.getString(cursor.getColumnIndex(Config.COLUMN_ASSET_DELETED_AT));

                            assetList.add(new Asset(id, assetName, assetSn, assetTag, createdAt, updatedAt, deletedAt));
                        }
                    }   while (cursor.moveToPrevious());

                    return assetList;
                }
        } catch (Exception e){
            Logger.d("Exception: " + e.getMessage());
            Toast.makeText(context, "Operation failed", Toast.LENGTH_SHORT).show();
        } finally {
            if(cursor!=null)
                cursor.close();
            sqLiteDatabase.close();
        }

        return Collections.emptyList();
    }

    public Asset getAssetById(long assetId){

        DatabaseHelper databaseHelper = DatabaseHelper.getInstance(context);
        SQLiteDatabase sqLiteDatabase = databaseHelper.getReadableDatabase();

        Cursor cursor = null;
        Asset asset = null;
        try {

            cursor = sqLiteDatabase.query(Config.TABLE_ASSET, null,
                    Config.COLUMN_ASSET_ID + " = ? ", new String[]{String.valueOf(assetId)},
                    null, null, null);

            /**
                 // If you want to execute raw query then uncomment below 2 lines. And comment out above sqLiteDatabase.query() method.

                 String SELECT_QUERY = String.format("SELECT * FROM %s WHERE %s = %s", Config.TABLE_ASSET, Config.COLUMN_ASSET_SN, String.valueOf(assetSn));
                 cursor = sqLiteDatabase.rawQuery(SELECT_QUERY, null);
             */

            if(cursor.moveToFirst()){
                int id = cursor.getInt(cursor.getColumnIndex(Config.COLUMN_ASSET_ID));
                String assetName = cursor.getString(cursor.getColumnIndex(Config.COLUMN_ASSET_NAME));
                String assetSerialNo = cursor.getString(cursor.getColumnIndex(Config.COLUMN_ASSET_SN));
                String assetTag = cursor.getString(cursor.getColumnIndex(Config.COLUMN_ASSET_TAG));
                String createdAt = cursor.getString(cursor.getColumnIndex(Config.COLUMN_ASSET_CREATED_AT));
                String updatedAt = cursor.getString(cursor.getColumnIndex(Config.COLUMN_ASSET_UPDATED_AT));
                String deletedAt = cursor.getString(cursor.getColumnIndex(Config.COLUMN_ASSET_DELETED_AT));

                asset = new Asset(id, assetName, assetSerialNo, assetTag, createdAt, updatedAt, deletedAt);
            }
        } catch (Exception e){
            Logger.d("Exception: " + e.getMessage());
            Toast.makeText(context, "Operation failed", Toast.LENGTH_SHORT).show();
        } finally {
            if(cursor!=null)
                cursor.close();
            sqLiteDatabase.close();
        }

        return asset;
    }


    public List<Asset> getAssetDeleted(){

        DatabaseHelper databaseHelper = DatabaseHelper.getInstance(context);
        SQLiteDatabase sqLiteDatabase = databaseHelper.getReadableDatabase();

        Cursor cursor = null;
        try {

            cursor = sqLiteDatabase.query(Config.TABLE_ASSET, null, null, null, null, null, null, null);

            /**
             // If you want to execute raw query then uncomment below 2 lines. And comment out above line.

             String SELECT_QUERY = String.format("SELECT %s, %s, %s, %s, %s FROM %s", Config.COLUMN_ASSET_ID, Config.COLUMN_ASSET_NAME, Config.COLUMN_ASSET_SN, Config.COLUMN_ASSET_CREATED_AT, Config.COLUMN_ASSET_TAG, Config.TABLE_ASSET);
             cursor = sqLiteDatabase.rawQuery(SELECT_QUERY, null);
             */

            if(cursor!=null)
                if(cursor.moveToFirst()){
                    List<Asset> assetDeletedList = new ArrayList<>();
                    do {
                        if(cursor.getString(cursor.getColumnIndex(Config.COLUMN_ASSET_DELETED_AT))!=null) {
                            int id = cursor.getInt(cursor.getColumnIndex(Config.COLUMN_ASSET_ID));
                            String assetName = cursor.getString(cursor.getColumnIndex(Config.COLUMN_ASSET_NAME));
                            String assetSn = cursor.getString(cursor.getColumnIndex(Config.COLUMN_ASSET_SN));
                            String assetTag = cursor.getString(cursor.getColumnIndex(Config.COLUMN_ASSET_TAG));
                            String createdAt = cursor.getString(cursor.getColumnIndex(Config.COLUMN_ASSET_CREATED_AT));
                            String updatedAt = cursor.getString(cursor.getColumnIndex(Config.COLUMN_ASSET_UPDATED_AT));
                            String deletedAt = cursor.getString(cursor.getColumnIndex(Config.COLUMN_ASSET_DELETED_AT));

                            assetDeletedList.add(new Asset(id, assetName, assetSn, assetTag, createdAt, updatedAt, deletedAt));
                        }
                    }   while (cursor.moveToNext());

                    return assetDeletedList;
                }
        } catch (Exception e){
            Logger.d("Exception: " + e.getMessage());
            Toast.makeText(context, "Operation failed", Toast.LENGTH_SHORT).show();
        } finally {
            if(cursor!=null)
                cursor.close();
            sqLiteDatabase.close();
        }

        return Collections.emptyList();
    }


    public long updateAssetInfo(Asset asset){

        long rowCount = 0;
        DatabaseHelper databaseHelper = DatabaseHelper.getInstance(context);
        SQLiteDatabase sqLiteDatabase = databaseHelper.getWritableDatabase();

        ContentValues contentValues = new ContentValues();
        contentValues.put(Config.COLUMN_ASSET_NAME, asset.getName());
        contentValues.put(Config.COLUMN_ASSET_SN, asset.getAssetSn());
        contentValues.put(Config.COLUMN_ASSET_TAG, asset.getAssetTag());
        contentValues.put(Config.COLUMN_ASSET_CREATED_AT, asset.getCreatedAt());
        contentValues.put(Config.COLUMN_ASSET_UPDATED_AT, asset.getUpdatedAt());
        contentValues.put(Config.COLUMN_ASSET_DELETED_AT, asset.getDeletedAt());

        try {
            rowCount = sqLiteDatabase.update(Config.TABLE_ASSET, contentValues,
                    Config.COLUMN_ASSET_ID + " = ? ",
                    new String[] {String.valueOf(asset.getId())});
        } catch (SQLiteException e){
            Logger.d("Exception: " + e.getMessage());
            Toast.makeText(context, e.getMessage(), Toast.LENGTH_LONG).show();
        } finally {
            sqLiteDatabase.close();
        }

        return rowCount;
    }


    public String getLastAssetName(){

        DatabaseHelper databaseHelper = DatabaseHelper.getInstance(context);
        SQLiteDatabase sqLiteDatabase = databaseHelper.getReadableDatabase();
        String lastAsset = "";

        Cursor cursor = null;
        try {

            cursor = sqLiteDatabase.query(Config.TABLE_ASSET, null, null, null, null, null, null, null);

            if(cursor!=null)
                if(cursor.moveToFirst()){
                    do {
                        lastAsset = cursor.getString(1);
                    }   while (cursor.moveToNext());

                    return lastAsset;
                }
        } catch (Exception e){
            Logger.d("Exception: " + e.getMessage());
            Toast.makeText(context, "Operation failed", Toast.LENGTH_SHORT).show();
        } finally {
            if(cursor!=null)
                cursor.close();
            sqLiteDatabase.close();
        }

        return lastAsset;
    }

    public long deleteAssetById(Asset asset) {

        long rowCount = 0;
        DatabaseHelper databaseHelper = DatabaseHelper.getInstance(context);
        SQLiteDatabase sqLiteDatabase = databaseHelper.getWritableDatabase();

        ContentValues contentValues = new ContentValues();
        contentValues.put(Config.COLUMN_ASSET_NAME, asset.getName());
        contentValues.put(Config.COLUMN_ASSET_SN, asset.getAssetSn());
        contentValues.put(Config.COLUMN_ASSET_TAG, asset.getAssetTag());
        contentValues.put(Config.COLUMN_ASSET_CREATED_AT, asset.getCreatedAt());
        contentValues.put(Config.COLUMN_ASSET_UPDATED_AT, asset.getUpdatedAt());
        contentValues.put(Config.COLUMN_ASSET_DELETED_AT, dtf.format(LocalDateTime.now()));

        try {
            rowCount = sqLiteDatabase.update(Config.TABLE_ASSET, contentValues,
                    Config.COLUMN_ASSET_ID + " = ? ",
                    new String[] {String.valueOf(asset.getId())});
        } catch (SQLiteException e){
            Logger.d("Exception: " + e.getMessage());
            Toast.makeText(context, e.getMessage(), Toast.LENGTH_LONG).show();
        } finally {
            sqLiteDatabase.close();
        }

        return rowCount;
    }

    public boolean deleteAllAssets(){
        boolean deleteStatus = false;
        DatabaseHelper databaseHelper = DatabaseHelper.getInstance(context);
        SQLiteDatabase sqLiteDatabase = databaseHelper.getWritableDatabase();

        try {
            //for "1" delete() method returns number of deleted rows
            //if you don't want row count just use delete(TABLE_NAME, null, null)
            sqLiteDatabase.delete(Config.TABLE_ASSET, null, null);

            long count = DatabaseUtils.queryNumEntries(sqLiteDatabase, Config.TABLE_ASSET);

            if(count==0)
                deleteStatus = true;

        } catch (SQLiteException e){
            Logger.d("Exception: " + e.getMessage());
            Toast.makeText(context, e.getMessage(), Toast.LENGTH_LONG).show();
        } finally {
            sqLiteDatabase.close();
        }

        return deleteStatus;
    }

}