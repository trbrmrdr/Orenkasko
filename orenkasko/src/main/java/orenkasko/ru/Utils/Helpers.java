package orenkasko.ru.Utils;

import android.content.Context;
import android.widget.Toast;

/**
 * Created by trbrm on 13.11.2017.
 */

public class Helpers {


    public static void Toast(final Context context, final String msg) {
        Toast.makeText(context, msg, Toast.LENGTH_LONG).show();
    }


}
