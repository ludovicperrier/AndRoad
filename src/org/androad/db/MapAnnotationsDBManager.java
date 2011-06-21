package org.androad.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import org.androad.adt.DBPOI;
import org.androad.sys.ors.adt.ds.POIType;
import org.androad.util.constants.Constants;

import org.osmdroid.util.BoundingBoxE6;
import org.osmdroid.util.GeoPoint;

public class MapAnnotationsDBManager {
	// ===========================================================
	// Final Fields
	// ===========================================================

	public static final String T_OSBBUG = "t_osbbug";
	public static final String T_OSBBUG_COL_DESCR = "descr";
	public static final String T_OSBBUG_COL_LAT = "lat";
	public static final String T_OSBBUG_COL_LNG = "lng";
	public static final String T_OSBBUG_COL_UPLOADED = "uploaded";

	public static final String CREATE_OSBBUG_TABLE = "CREATE TABLE IF NOT EXISTS " + T_OSBBUG
	+ " ("
	+ T_OSBBUG_COL_DESCR + " VARCHAR(255),"
	+ T_OSBBUG_COL_LAT + " INTEGER NOT NULL,"
	+ T_OSBBUG_COL_LNG + " INTEGER NOT NULL,"
	+ T_OSBBUG_COL_UPLOADED + " INTEGER NOT NULL,"
	+ " PRIMARY KEY(" + T_OSBBUG_COL_DESCR + "));";

	public static final String T_FTPC = "t_ftpc";
	public static final String T_FTPC_COL_POSTCODE1 = "postcode1";
	public static final String T_FTPC_COL_POSTCODE2 = "postcode2";
	public static final String T_FTPC_COL_LAT = "lat";
	public static final String T_FTPC_COL_LNG = "lng";
	public static final String T_FTPC_COL_UPLOADED = "uploaded";

	public static final String CREATE_FTPC_TABLE = "CREATE TABLE IF NOT EXISTS " + T_FTPC
	+ " ("
	+ T_FTPC_COL_POSTCODE1 + " VARCHAR(255),"
	+ T_FTPC_COL_POSTCODE2 + " VARCHAR(255),"
	+ T_FTPC_COL_LAT + " INTEGER NOT NULL,"
	+ T_FTPC_COL_LNG + " INTEGER NOT NULL,"
	+ T_FTPC_COL_UPLOADED + " INTEGER NOT NULL);";

	public static final String T_POI = "t_poi";
	public static final String T_POI_COL_DESCR = "descr";
	public static final String T_POI_COL_TYPE = "type";
	public static final String T_POI_COL_LAT = "lat";
	public static final String T_POI_COL_LNG = "lng";
	public static final String T_POI_COL_UPLOADED = "uploaded";

	public static final String CREATE_POI_TABLE = "CREATE TABLE IF NOT EXISTS " + T_POI
	+ " ("
	+ T_POI_COL_DESCR + " VARCHAR(255),"
	+ T_POI_COL_TYPE + " VARCHAR(255),"
	+ T_POI_COL_LAT + " INTEGER NOT NULL,"
	+ T_POI_COL_LNG + " INTEGER NOT NULL,"
	+ T_POI_COL_UPLOADED + " INTEGER NOT NULL,"
	+ " PRIMARY KEY(" + T_POI_COL_DESCR + "));";

	private static final String DATABASE_NAME = "mapannotations";
	private static final int DATABASE_VERSION = 1;

	// ===========================================================
	// Fields
	// ===========================================================

	private SQLiteDatabase db;

	// ===========================================================
	// Constructors
	// ===========================================================

    public MapAnnotationsDBManager(final Context context) {
        db = new AndNavDatabaseHelper(context, AndNavSQLTableInfo.values()).getWritableDatabase();
    }

	// ===========================================================
	// Methods from SuperClass/Interfaces
	// ===========================================================

	// ===========================================================
	// Methods
	// ===========================================================

    public List<DBPOI> getAll(final BoundingBoxE6 limits) {
        final ArrayList<DBPOI> list = new ArrayList<DBPOI>();
        list.addAll(getOsbBugs(limits));
        list.addAll(getFtpc(limits));
        list.addAll(getPOIs(limits));
        return list;
    }

    public boolean addOsbBug(final GeoPoint point, final String description) {
        final ContentValues cv = new ContentValues();
        cv.put(T_OSBBUG_COL_DESCR, description);
        cv.put(T_OSBBUG_COL_LAT, point.getLatitudeE6());
        cv.put(T_OSBBUG_COL_LNG, point.getLongitudeE6());
        cv.put(T_OSBBUG_COL_UPLOADED, 0);

        long retVal = db.insert(T_OSBBUG, null, cv);
        if (retVal == -1) {
            return false;
        }
        return true;
    }

    public List<DBPOI> getOsbBugs(final BoundingBoxE6 limits) {
        final ArrayList<DBPOI> list = new ArrayList<DBPOI>();

        final String squery = T_OSBBUG_COL_LAT + " < ? AND ? < " + T_OSBBUG_COL_LAT + " AND " + T_OSBBUG_COL_LNG + " < ? AND ? < " + T_OSBBUG_COL_LNG;

        final Cursor query = db.query(T_OSBBUG, new String[] {
                T_OSBBUG_COL_DESCR, T_OSBBUG_COL_LAT, T_OSBBUG_COL_LNG
            }, squery,
            new String[] { limits.getLatNorthE6() + "",
                           limits.getLatSouthE6() + "",
                           limits.getLonEastE6() + "",
                           limits.getLonWestE6() + "",
            }, null, null, null);

        if(query.moveToFirst()) {
            do {
                final GeoPoint c = new GeoPoint(query.getInt(1), query.getInt(2));
                final DBPOI osbbug = new DBPOI(query.getString(0), c);
                list.add(osbbug);
            } while(query.moveToNext());
        }
        query.close();

        Log.d(Constants.DEBUGTAG, "count dbosbbug = " + list.size());

        return list;
    }

    public List<DBPOI> getOSBBUGsToUpload() {
        final ArrayList<DBPOI> list = new ArrayList<DBPOI>();

        final String squery = T_OSBBUG_COL_UPLOADED + " = ?";

        final Cursor query = db.query(T_OSBBUG, new String[] {
                T_OSBBUG_COL_DESCR, T_OSBBUG_COL_LAT, T_OSBBUG_COL_LNG
            }, squery,
            new String[] { "0"
            }, null, null, null);

        if(query.moveToFirst()) {
            do {
                final GeoPoint c = new GeoPoint(query.getInt(1), query.getInt(2));
                final DBPOI osbbug = new DBPOI(query.getString(0), c);
                list.add(osbbug);
            } while(query.moveToNext());
        }
        query.close();

        Log.d(Constants.DEBUGTAG, "count dbosbbug = " + list.size());

        return list;
    }

    public boolean addFtpc(final GeoPoint point, final String postcode1, final String postcode2) {
        final ContentValues cv = new ContentValues();
        cv.put(T_FTPC_COL_POSTCODE1, postcode1);
        cv.put(T_FTPC_COL_POSTCODE2, postcode2);
        cv.put(T_FTPC_COL_LAT, point.getLatitudeE6());
        cv.put(T_FTPC_COL_LNG, point.getLongitudeE6());
        cv.put(T_FTPC_COL_UPLOADED, 0);

        long retVal = db.insert(T_FTPC, null, cv);
        if (retVal == -1) {
            return false;
        }
        return true;
    }

    public List<DBPOI> getFtpc(final BoundingBoxE6 limits) {
        final ArrayList<DBPOI> list = new ArrayList<DBPOI>();

        final String squery = T_FTPC_COL_LAT + " < ? AND ? < " + T_FTPC_COL_LAT + " AND " + T_FTPC_COL_LNG + " < ? AND ? < " + T_FTPC_COL_LNG;

        final Cursor query = db.query(T_FTPC, new String[] {
                T_FTPC_COL_POSTCODE1, T_FTPC_COL_POSTCODE2, T_FTPC_COL_LAT, T_FTPC_COL_LNG
            }, squery,
            new String[] { limits.getLatNorthE6() + "",
                           limits.getLatSouthE6() + "",
                           limits.getLonEastE6() + "",
                           limits.getLonWestE6() + "",
            }, null, null, null);

        if(query.moveToFirst()) {
            do {
                final GeoPoint c = new GeoPoint(query.getInt(2), query.getInt(3));
                final DBPOI ftpc = new DBPOI(query.getString(0), c);
                ftpc.setSite(query.getString(1));
                list.add(ftpc);
            } while(query.moveToNext());
        }
        query.close();

        Log.d(Constants.DEBUGTAG, "count dbftpc = " + list.size());

        return list;
    }

    public List<DBPOI> getFTPCsToUpload() {
        final ArrayList<DBPOI> list = new ArrayList<DBPOI>();

        final String squery = T_FTPC_COL_UPLOADED + " = ?";

        final Cursor query = db.query(T_FTPC, new String[] {
                T_FTPC_COL_POSTCODE1, T_FTPC_COL_POSTCODE2, T_FTPC_COL_LAT, T_FTPC_COL_LNG
            }, squery,
            new String[] { "0"
            }, null, null, null);

        if(query.moveToFirst()) {
            do {
                final GeoPoint c = new GeoPoint(query.getInt(2), query.getInt(3));
                final DBPOI ftpc = new DBPOI(query.getString(0), c);
                ftpc.setSite(query.getString(1));
                list.add(ftpc);
            } while(query.moveToNext());
        }
        query.close();

        Log.d(Constants.DEBUGTAG, "count dbftpc = " + list.size());

        return list;
    }

    public boolean addPoi(final GeoPoint point, final POIType type, final String description) {
        final ContentValues cv = new ContentValues();
        cv.put(T_POI_COL_DESCR, description);
        cv.put(T_POI_COL_TYPE, type.RAWNAME);
        cv.put(T_POI_COL_LAT, point.getLatitudeE6());
        cv.put(T_POI_COL_LNG, point.getLongitudeE6());
        cv.put(T_POI_COL_UPLOADED, 0);

        long retVal = db.insert(T_POI, null, cv);
        if (retVal == -1) {
            return false;
        }
        return true;
    }

    public List<DBPOI> getPOIs(final BoundingBoxE6 limits) {
        final ArrayList<DBPOI> list = new ArrayList<DBPOI>();

        final String squery = T_POI_COL_LAT + " < ? AND ? < " + T_POI_COL_LAT + " AND " + T_POI_COL_LNG + " < ? AND ? < " + T_POI_COL_LNG;

        final Cursor query = db.query(T_POI, new String[] {
                T_POI_COL_DESCR, T_POI_COL_TYPE, T_POI_COL_LAT, T_POI_COL_LNG
            }, squery,
            new String[] { limits.getLatNorthE6() + "",
                           limits.getLatSouthE6() + "",
                           limits.getLonEastE6() + "",
                           limits.getLonWestE6() + "",
            }, null, null, null);

        if(query.moveToFirst()) {
            do {
                final GeoPoint c = new GeoPoint(query.getInt(2), query.getInt(3));
                final DBPOI poi = new DBPOI(query.getString(0), c);
                poi.setType(query.getString(1));
                list.add(poi);
            } while(query.moveToNext());
        }
        query.close();

        Log.d(Constants.DEBUGTAG, "count dbpoi = " + list.size());

        return list;
    }

    public List<DBPOI> getPOIsToUpload() {
        final ArrayList<DBPOI> list = new ArrayList<DBPOI>();

        final String squery = T_POI_COL_UPLOADED + " = ?";

        final Cursor query = db.query(T_POI, new String[] {
                T_POI_COL_DESCR, T_POI_COL_TYPE, T_POI_COL_LAT, T_POI_COL_LNG
            }, squery,
            new String[] { "0"
            }, null, null, null);

        if(query.moveToFirst()) {
            do {
                final GeoPoint c = new GeoPoint(query.getInt(2), query.getInt(3));
                final DBPOI poi = new DBPOI(query.getString(0), c);
                poi.setType(query.getString(1));
                list.add(poi);
            } while(query.moveToNext());
        }
        query.close();

        Log.d(Constants.DEBUGTAG, "count dbpoi = " + list.size());

        return list;
    }

	// ===========================================================
	// Inner and Anonymous Classes
	// ===========================================================

	private static enum AndNavSQLTableInfo {
		OSBBUG(T_OSBBUG, CREATE_OSBBUG_TABLE),
		FTPC(T_FTPC, CREATE_FTPC_TABLE),
		POI(T_POI, CREATE_POI_TABLE);

		public final String mTableName;
		public final String mCreateCommand;

		private AndNavSQLTableInfo(final String tableName, final String createommand) {
			this.mTableName = tableName;
			this.mCreateCommand = createommand;
		}
	}

	private static class AndNavDatabaseHelper extends SQLiteOpenHelper {

		protected final AndNavSQLTableInfo[] mTableInfo;

		AndNavDatabaseHelper(final Context context, final AndNavSQLTableInfo ... aTableInfo) {
			super(context, DATABASE_NAME, null, DATABASE_VERSION);
			this.mTableInfo = aTableInfo;
		}

		@Override
		public void onCreate(final SQLiteDatabase db) {
			for (final AndNavSQLTableInfo i : this.mTableInfo) {
				db.execSQL(i.mCreateCommand);
			}
		}

		@Override
		public void onUpgrade(final SQLiteDatabase db, final int oldVersion, final int newVersion) {
			Log.w(Constants.DEBUGTAG, "Upgrading database from version " + oldVersion + " to " + newVersion + ", which will destroy all old data");

			for (final AndNavSQLTableInfo i : this.mTableInfo) {
				db.execSQL("DROP TABLE IF EXISTS " + i.mTableName);
			}

			onCreate(db);
		}
	}
}
