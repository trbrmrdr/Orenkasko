package orenkasko.ru;

import android.app.Activity;

import java.util.Random;

import orenkasko.ru.Utils.Helpers;

import static orenkasko.ru.Data.Order.data_order_count;

/**
 * Created by admin on 24.11.2017.
 */

public class Data {

    static final String key_oreder_id = "order_id";

    static String order_temp_count = "order_temp_count";
    static String name_temp = "name_temp_osago";
    static String name_temp_type = "name_temp_osago";
    static String name_temp_date = "name_temp_osago";
    static String name_temp_on = "name_temp_osago_navigators";

    static String name_id = "name_osago_";
    static String name_type = "name_osago";
    static String name_date = "name_osago";
    static String name_id_on = "name_osago_navigators_";
    static String order_count = "order_count";

    private Data() {
    }


    public static int saveOsago(Activity activity,
                                int oreder_id,
                                int navigators,
                                String type,
                                String date,
                                String save_dat) {

        int order = oreder_id;
        if (-1 == order) {
            order = Helpers.GetInt(activity, order_count, -1) + 1;
            Helpers.SaveInt(activity, order_temp_count, order);

            int rand_id = 10000 + (new Random()).nextInt(99999);

            Helpers.SaveInt(activity, "oreders_id_" + order, rand_id);

            Helpers.SaveString(activity, name_temp, save_dat);
            Helpers.SaveInt(activity, name_temp_on, navigators);
            Helpers.SaveString(activity, name_temp_type, type);
            Helpers.SaveString(activity, name_temp_date, date);
            return order;
        }

        Helpers.SaveString(activity, name_id + order, save_dat);
        Helpers.SaveInt(activity, name_id_on + order, navigators);
        Helpers.SaveString(activity, name_type + order, type);
        Helpers.SaveString(activity, name_date + order, date);
        return order;
    }

    public static String getOsago(Activity activity, int order_id) {
        if (order_id == -1) {
            return Helpers.GetString(activity, name_temp);
        }

        return Helpers.GetString(activity, name_id + order_id);
    }

    public static int getNavigators(Activity activity, int order_id) {
        if (-1 == order_id)
            return Helpers.GetInt(activity, name_temp_on, 0);
        return Helpers.GetInt(activity, name_id_on + order_id, 0);
    }


    public static void saveDocs(Activity activity, int order_id, String data) {
        if (order_id == -1) return;

        if (order_id == Helpers.GetInt(activity, order_temp_count, -1)) {
            Helpers.DelInt(activity, order_temp_count);

            Helpers.SaveString(activity, name_id + order_id, Helpers.GetString(activity, name_temp));
            Helpers.SaveInt(activity, name_id_on + order_id, Helpers.GetInt(activity, name_temp_on));
            Helpers.SaveString(activity, name_type + order_id, Helpers.GetString(activity, name_temp_type));
            Helpers.SaveString(activity, name_date + order_id, Helpers.GetString(activity, name_temp_date));

            Helpers.DelString(activity, name_temp);
            Helpers.DelInt(activity, name_temp_on);
            Helpers.DelString(activity, name_temp_type);
            Helpers.DelString(activity, name_temp_date);
        }

        int oreder = Helpers.GetInt(activity, order_count, -1) + 1;
        Helpers.SaveInt(activity, order_count, oreder);

        Helpers.SaveString(activity, "docs_" + oreder, data);
    }


    static String login_phone = "login_phone";

    public static void savePhone(Activity activity, String phone) {
        Helpers.SaveString(activity, login_phone, phone);
    }

    public static String getPhone(Activity activity) {
        return Helpers.GetString(activity, login_phone);
    }

    public static void clear(Activity activity) {
        Helpers.Delete(activity);
    }


    public static class Order {
        static int data_order_count = -1;
        static String[] ids;
        static String[] names;
        static String[] avto_types;
        static String[] avto_dates;
    }

    public static void preparedata(Activity activity) {
        data_order_count = Helpers.GetInt(activity, order_count, -1);

        if (data_order_count <= 0) return;

        Order.ids = new String[data_order_count];
        Order.names = new String[data_order_count];
        Order.avto_types = new String[data_order_count];
        Order.avto_dates = new String[data_order_count];


        for (int i = 0; i < data_order_count; ++i) {
            Order.ids[i] = String.valueOf(Helpers.GetInt(activity, "oreders_id_" + data_order_count));

            Helpers.SaveString(activity, name_temp, save_dat);
            Helpers.SaveInt(activity, name_temp_on, navigators);
            return order;
        }

        Helpers.SaveString(activity, name_id + order, save_dat);
        Helpers.SaveInt(activity, name_id_on + order, navigators);

    }
}
