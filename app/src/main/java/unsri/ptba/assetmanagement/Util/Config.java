package unsri.ptba.assetmanagement.Util;

public class Config {

    public static final String DATABASE_NAME = "asset-db";

    //column names of asset table
    public static final String TABLE_ASSET = "asset";
    public static final String COLUMN_ASSET_ID = "_id";
    public static final String COLUMN_ASSET_NAME = "assetName";
    public static final String COLUMN_ASSET_SN = "serial_no";
    public static final String COLUMN_ASSET_TAG = "assetTag";
    public static final String COLUMN_ASSET_CREATED_AT = "createdAt";
    public static final String COLUMN_ASSET_UPDATED_AT = "updatedAt";
    public static final String COLUMN_ASSET_DELETED_AT = "deletedAt";

    //others for general purpose key-value pair data
    public static final String TITLE = "title";
    public static final String CREATE_ASSET = "create_asset";
    public static final String FILTER_ASSET = "filter_asset";
    public static final String UPDATE_ASSET = "update_asset";
}
