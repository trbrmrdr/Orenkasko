package orenkasko.ru;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.ThemedSpinnerAdapter;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Random;

import orenkasko.ru.Utils.Helpers;

import static orenkasko.ru.Data.Order.data_order_count;

/**
 * Created by admin on 24.11.2017.
 */

public class Data {

    private static Context mContext;

    public static void setContext(Context context) {
        mContext = context;
    }

    public static final String key_oreder_id = "order_id";

    static String ORDER_TMP_ID = "order_tmp_id";

    static String TMP_OSAGO_DAT = "osago_dat_";
    static String TMP_ORDERS_TID = "tmp_osago_tid";
    static String TMP_TYPE = "tmp_osago";
    static String TMP_NAVIGATORS = "temp_osago_navigators";


    static String ORDERS = "orders";//int array from ids order
    static String OSAGO_DAT_ = "osago_dat_";

    static String OSAGO_DOCS_ = "osago_docs_";
    static String ORDER_TID_ = "osago_tid_";//
    static String TYPE_ = "osago_type";
    static String NAVIGATORS_ = "osago_navigators_";

    static String TIME_DOCS_ = "osago_date_docs";
    static String NAME_DOCS_ = "osago_name_docs";


    private Data() {
    }


    public static int saveOsagoDat(
            int oreder_id,
            int navigators,
            String type,
            String save_dat) {

        int new_order = oreder_id;
        if (-1 == new_order) {
            new_order = getNewOrderId();
            Helpers.SaveInt(mContext, ORDER_TMP_ID, new_order);

            int rand_id = 10000 + (new Random()).nextInt(99999);

            Helpers.SaveInt(mContext, TMP_ORDERS_TID, rand_id);
            Helpers.SaveString(mContext, TMP_OSAGO_DAT, save_dat);
            Helpers.SaveInt(mContext, TMP_NAVIGATORS, navigators);
            Helpers.SaveString(mContext, TMP_TYPE, type);

            return new_order;
        }

        Helpers.SaveString(mContext, OSAGO_DAT_ + new_order, save_dat);
        Helpers.SaveInt(mContext, NAVIGATORS_ + new_order, navigators);
        Helpers.SaveString(mContext, TYPE_ + new_order, type);
        return new_order;
    }

    public static String getOsagoDat(int order_id) {
        if (order_id == -1) {
            return Helpers.GetString(mContext, TMP_OSAGO_DAT);
        }

        return Helpers.GetString(mContext, OSAGO_DAT_ + order_id);
    }

    public static int getNavigators(int order_id) {
        if (-1 == order_id)
            return Helpers.GetInt(mContext, TMP_NAVIGATORS, 0);
        return Helpers.GetInt(mContext, NAVIGATORS_ + order_id, 0);
    }

    public static String getDocs(int order_id) {
        if (order_id == -1) return "";
        return Helpers.GetString(mContext, OSAGO_DOCS_ + order_id);
    }

    public static void saveDocs(int order_id, String name, String time_docs, String data) {
        if (order_id == -1) return;

        if (order_id == Helpers.GetInt(mContext, ORDER_TMP_ID, -1)) {

            addOrderId(order_id);

            Helpers.SaveString(mContext, OSAGO_DAT_ + order_id, Helpers.GetString(mContext, TMP_OSAGO_DAT));
            Helpers.SaveInt(mContext, NAVIGATORS_ + order_id, Helpers.GetInt(mContext, TMP_NAVIGATORS));
            Helpers.SaveInt(mContext, ORDER_TID_ + order_id, Helpers.GetInt(mContext, TMP_ORDERS_TID));
            Helpers.SaveString(mContext, TYPE_ + order_id, Helpers.GetString(mContext, TMP_TYPE));

            Helpers.DelInt(mContext, ORDER_TMP_ID);
            Helpers.DelString(mContext, TMP_OSAGO_DAT);
            Helpers.DelInt(mContext, TMP_NAVIGATORS);
            Helpers.DelString(mContext, TMP_TYPE);
        }
        Helpers.SaveString(mContext, NAME_DOCS_ + order_id, name);
        Helpers.SaveString(mContext, TIME_DOCS_ + order_id, time_docs);
        Helpers.SaveString(mContext, OSAGO_DOCS_ + order_id, data);
    }

    public static String getTimeDocs(int order_id) {
        String ret = Helpers.GetString(mContext, TIME_DOCS_ + order_id);
        if (ret.length() <= 0) {
            ret = formatTimeDocs(Calendar.getInstance().getTime());
        }
        return ret;
    }

    //##############################################################################################

    static String login_phone = "login_phone";

    public static void savePhone(String phone) {
        Helpers.SaveString(mContext, login_phone, phone);
    }

    public static String getPhone() {
        return Helpers.GetString(mContext, login_phone);
    }

    public static void clear() {
        Helpers.Delete(mContext);
    }

    //##############################################################################################
    private static int getNewOrderId() {
        //todo tests
        String[] array_orders = Helpers.GetStringArray(mContext, ORDERS);
        int new_id = 0;
        for (String str : array_orders) {
            if (Integer.parseInt(str) == new_id) {
                new_id++;
            }
        }
        return new_id;
    }

    private static void addOrderId(int order_id) {
        Helpers.AddInArray(mContext, ORDERS, order_id);
    }

    public static void rmOrder(int order_id) {
        if (order_id == -1) return;
        Helpers.RmInArray(mContext, ORDERS, order_id);
    }

    //##############################################################################################

    public static class Order {
        public static int data_order_count = -1;
        public static int[] ids;
        public static String[] tids;
        public static String[] names;
        public static String[] avto_types;
        public static String[] avto_time;
    }

    public static void preparedata() {
        String[] orders_id = Helpers.GetStringArray(mContext, ORDERS);
        data_order_count = orders_id.length;
        if (data_order_count <= 0) return;

        Order.ids = new int[data_order_count];
        Order.tids = new String[data_order_count];
        Order.names = new String[data_order_count];
        Order.avto_types = new String[data_order_count];
        Order.avto_time = new String[data_order_count];


        int i = -1;
        for (String str_id : orders_id) {
            i++;
            int id = Integer.parseInt(str_id);

            Order.ids[i] = id;
            Order.tids[i] = String.valueOf(Helpers.GetInt(mContext, ORDER_TID_ + id));

            Order.names[i] = Helpers.GetString(mContext, NAME_DOCS_ + id);
            Order.avto_types[i] = Helpers.GetString(mContext, TYPE_ + id);
            Order.avto_time[i] = Helpers.GetString(mContext, TIME_DOCS_ + id);
        }
    }

    //##############################################################################################

    public static String formatTimeDocs(Date time) {
        DateFormat df = new SimpleDateFormat("d:MM:YYYY");
        return df.format(time);
    }

    //##############################################################################################

}
