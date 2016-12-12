package ui;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import java.util.List;

import bs.dbHelper.DBHelper;
import bs.dbHelper.R;

public class MainActivity extends AppCompatActivity {
    public static final String TAG = "Database";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        init(this);
    }

    //データベースを初期する
    public static void init(Context c) {
        DBHelper db;
        //データベース名
        String dbname = "db_name";
        //バージョンは１以上を指定してください
        int version = 1;

        //以下の宣言が必須です
        //テブール一覧を入力する
        db = new DBHelper(c, dbname, version, DBBuilder.getBuilder());
        //此処まで

        for(int i = 0; i < 10; i++) {
            String sql = "INSERT INTO t_person (t_username, t_address, dt_birthday, dt_created, t_email, t_gender, " +
                    "t_password, t_sport) " +
                    "VALUES (\"thanhbui" + i + "\", \"Tokyo\", 1991/01/01, 2016/12/09, \"t.buiquang\", \"true\", \"password\", 11)";
            db.execute(sql);
        }

        //DBHelper API
        List results = db.rawQuery("SELECT t_id, t_password, t_username, dt_birthday FROM t_person WHERE t_id IN ( 1, 6, 2, 4, 8, 9, 10 )", null);
        Log.d(TAG, "queryWithNoKey: " + results);

        db.execute("UPDATE t_person SET t_username = 'dbhelper' WHERE t_id = 1");

        results = db.noKeyQuery("SELECT t_username FROM t_person", null);
        Log.d(TAG, "rawQuery: " + results);

        //t_id, t_password, t_username, dt_birthday
        db.close();
    }
}