package bs.dbHelper;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by thanhbui on 2016/11/17.
 */

public class DBHelper {
    public static Database database;
    public static SQLiteDatabase db;

    public DBHelper(Context c, String dbname, int version, List<List<String>> dbBuilder) {
        database = new Database(c, dbname, version, dbBuilder);
        db = open();
    }

    public List rawQuery(String sql, String[] params) {
        List toRet = new ArrayList<>();
        int cnt;
        Map<String, String> row;

        Cursor c;
        try {
            c = db.rawQuery(sql, params);
        } catch (Exception e) {
            DBLog.log(e.getLocalizedMessage());
            return null;
        }
        cnt = c.getColumnCount();

        while(c.moveToNext()) {
            row = new HashMap<String, String>();
            for (int i = 0; i < cnt; i++) {
                row.put(c.getColumnName(i), c.getString(i));
            }
            toRet.add(row);
        }

        return toRet;
    }

    public List noKeyQuery(String sql, String[] params) {
        List toRet = new ArrayList<>();
        int cnt;
        List<String> row;

        Cursor c;
        try {
            c = db.rawQuery(sql, params);
        } catch (Exception e) {
            DBLog.log(e.getLocalizedMessage());
            return null;
        }
        cnt = c.getColumnCount();

        while(c.moveToNext()) {
            if(cnt == 1) {
                toRet.add(c.getString(0));
            } else {
                row = new ArrayList<>();
                for (int i = 0; i < cnt; i++) {
                    row.add(c.getString(i));
                }
                toRet.add(row);
            }
        }

        return toRet;
    }

    //クリエする
    public void execute(String sql) {
        db.execSQL(sql);
    }

    public boolean transactionWithSQL(List<String> sqlList) {
        boolean toRet = false;

        db.beginTransaction();
        try {
            for (String sql : sqlList) {
                db.execSQL(sql);
            }
            db.setTransactionSuccessful();
            toRet = true;

        } catch (Exception e) {
            DBLog.log(e.getLocalizedMessage());
        }
        finally {
            db.endTransaction();
        }
        return toRet;
    }

    public synchronized SQLiteDatabase open() {
        return database.getWritableDatabase();
    }

    public synchronized void close() {
        database.close();
    }

    private static class Database extends SQLiteOpenHelper {
        private int version;
        private List<List<String>> dbBuilder = new ArrayList<>();

        public Database(Context c, String dbname, int version, List<List<String>> dbBuilder) {
            super(c, dbname, null, version);
            this.version = version;
            this.dbBuilder = dbBuilder;
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            DBLog.log("onCreate !");
            upgradeFromSQLScript(db, -1, version);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            DBLog.log("onUpgrade !");
            upgradeFromSQLScript(db, oldVersion, newVersion);
        }

        //SQLスクリプからアップグレード
        private void upgradeFromSQLScript(SQLiteDatabase db, int oldVersion, int newVersion) {
            int index = 1;

            db.beginTransaction();
            try {
                for (List<String> builder : dbBuilder) {
                    if (index > oldVersion && index <= newVersion) {
                        for (String sql : builder) {
                            DBLog.log("SQLCreate: " + sql);
                            try {
                                db.execSQL(sql);
                            } catch (Exception e) {
                                DBLog.log(e.getLocalizedMessage());
                            }
                        }
                    }
                    index++;
                }

                db.setTransactionSuccessful();
            } catch (Exception e) {
                DBLog.log(e.getLocalizedMessage());
            }
            db.endTransaction();
        }

        public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            DBLog.log("onDowngrade !");
        }
    }
}
