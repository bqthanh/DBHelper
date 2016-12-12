package bs.dbHelper;

import android.util.Log;


/**
 * Created by thanhbui on 2016/12/10.
 */

public class DBLog {
    public static final String TAG = "Database";

    public static void log(String content) {
        if(BuildConfig.DEBUG) {
            Log.i(TAG, content);
        }
    }
}