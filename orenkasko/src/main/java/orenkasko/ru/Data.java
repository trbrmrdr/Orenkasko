package orenkasko.ru;

import android.content.Context;
import android.graphics.Bitmap;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.Random;

import orenkasko.ru.Utils.Helpers;
import orenkasko.ru.ui.base.ImageLoader;

/**
 * Created by admin on 24.11.2017.
 */

public class Data {

    private static Context mContext;

    public static void setContext(Context context) {
        if (null == _this_data)
            _this_data = new Data();
        mContext = context;
    }

    private static Data _this_data;

    public static Data getInstance() {
        return _this_data;
    }

    public static final String key_oreder_id = "order_id";

    static String ORDER_CURR_ID = "order_tmp_id";

    static String TMP_OSAGO_DAT = "tmp_osago_dat_";
    static String TMP_ORDERS_TID = "tmp_osago_tid";
    static String TMP_TYPE = "tmp_osago";
    static String TMP_NAVIGATORS = "temp_osago_navigators";


    static String ORDERS = "orders";//int array from ids order
    static String OSAGO_DAT_ = "osago_dat_";

    static String OSAGO_DOCS_ = "osago_docs_";
    static String ORDER_TID_ = "osago_tid_";//
    static String TYPE_ = "osago_type";
    static String NAVIGATORS_ = "osago_navigators_";

    static String IMAGE_ = "image_";

    static String TIME_DOCS_ = "osago_date_docs";
    static String NAME_DOCS_ = "osago_name_docs";

    static String SUCCESS_ = "success_";


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
            Helpers.SaveInt(mContext, ORDER_CURR_ID, new_order);

            int rand_id = 10000 + (new Random()).nextInt(99999);

            Helpers.SaveInt(mContext, TMP_ORDERS_TID, rand_id);
            Helpers.SaveString(mContext, TMP_OSAGO_DAT, save_dat);
            Helpers.SaveInt(mContext, TMP_NAVIGATORS, navigators);
            Helpers.SaveString(mContext, TMP_TYPE, type);


            Helpers.DelInt(mContext, SUCCESS_ + new_order);

            Helpers.DelString(mContext, NAME_DOCS_ + new_order);
            Helpers.DelString(mContext, TIME_DOCS_ + new_order);
            Helpers.DelString(mContext, OSAGO_DOCS_ + new_order);
            Helpers.DelString(mContext, OSAGO_DAT_ + new_order);
            Helpers.DelInt(mContext, NAVIGATORS_ + new_order);
            Helpers.DelInt(mContext, ORDER_TID_ + new_order);
            Helpers.DelString(mContext, TYPE_ + new_order);

            return new_order;
        }

        Helpers.SaveString(mContext, OSAGO_DAT_ + new_order, save_dat);
        Helpers.SaveInt(mContext, NAVIGATORS_ + new_order, navigators);
        Helpers.SaveString(mContext, TYPE_ + new_order, type);
        return new_order;
    }

    public static String getOsagoDat(int order_id) {
        if (-1 == order_id)
            return "";
        if (order_id == Helpers.GetInt(mContext, ORDER_CURR_ID, -1))
            return Helpers.GetString(mContext, TMP_OSAGO_DAT);
        return Helpers.GetString(mContext, OSAGO_DAT_ + order_id);
    }

    public static int getNavigators(int order_id) {
        if (-1 == order_id)
            return -1;
        if (order_id == Helpers.GetInt(mContext, ORDER_CURR_ID, -1))
            return Helpers.GetInt(mContext, TMP_NAVIGATORS, 0);
        return Helpers.GetInt(mContext, NAVIGATORS_ + order_id, 0);
    }

    public static String getDocs(int order_id) {
        if (order_id == -1) return "";
        return Helpers.GetString(mContext, OSAGO_DOCS_ + order_id);
    }

    public static void saveDocs(int order_id, boolean success, String name, String time_docs, String data) {
        if (order_id == -1) return;

        if (order_id == Helpers.GetInt(mContext, ORDER_CURR_ID, -1)) {

            addOrderId(order_id);

            Helpers.SaveString(mContext, OSAGO_DAT_ + order_id, Helpers.GetString(mContext, TMP_OSAGO_DAT));
            Helpers.SaveInt(mContext, NAVIGATORS_ + order_id, Helpers.GetInt(mContext, TMP_NAVIGATORS));
            Helpers.SaveInt(mContext, ORDER_TID_ + order_id, Helpers.GetInt(mContext, TMP_ORDERS_TID));
            Helpers.SaveString(mContext, TYPE_ + order_id, Helpers.GetString(mContext, TMP_TYPE));


            Helpers.DelInt(mContext, ORDER_CURR_ID);
            Helpers.DelString(mContext, TMP_OSAGO_DAT);
            Helpers.DelInt(mContext, TMP_NAVIGATORS);
            Helpers.DelString(mContext, TMP_TYPE);
        }

        if (Helpers.GetInt(mContext, SUCCESS_ + order_id) == -1)
            Helpers.SaveInt(mContext, SUCCESS_ + order_id, success ? 1 : -1);

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
    static String login_name = "login_name";
    static String login_email = "login_email";
    static String login_image = "login_image";

    public static void savePhone(String phone) {
        Helpers.SaveString(mContext, login_phone, phone);
    }

    public static String getPhone() {
        return Helpers.GetString(mContext, login_phone);
    }

    public static void saveName(String name) {
        Helpers.SaveString(mContext, login_name, name);
    }

    public static String getName() {
        return Helpers.GetString(mContext, login_name);
    }

    public static void saveEmail(String email) {
        Helpers.SaveString(mContext, login_email, email);
    }

    public static String getEmail() {
        return Helpers.GetString(mContext, login_email);
    }

    public static void SaveProfileImage(Bitmap bitmap) {
        Helpers.SaveImage(mContext, bitmap, login_image);
    }

    public static Bitmap getProfileImage() {
        return Helpers.GetImage(mContext, login_image);
    }

    public static void clear() {
        Helpers.Delete(mContext);
        Helpers.RmImages(mContext, login_image);
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
        Helpers.RmImages(mContext, IMAGE_ + order_id);
    }

    public static void saveImages(int order_id, ArrayList<ImageLoader> images) {
        Helpers.SaveImages(mContext, IMAGE_ + order_id, images);
    }

    public static ArrayList<Bitmap> getImage(int order_id) {
        return Helpers.GetImages(mContext, IMAGE_ + order_id);
    }

    //##############################################################################################

    public static class Order {
        public int data_order_count;
        public Integer[] ids;
        public String[] tids;
        public String[] names;
        public String[] avto_types;
        public String[] avto_time;
        public int i;

        public Order() {
            data_order_count = 0;
        }

        public Order(final Order mOrder) {
            data_order_count = mOrder.data_order_count;
            ids = mOrder.ids.clone();

            tids = mOrder.tids.clone();
            names = mOrder.names.clone();
            avto_types = mOrder.avto_types.clone();
            avto_time = mOrder.avto_time.clone();
        }

        void init(int count) {
            data_order_count = 0;
            i = -1;
            ids = new Integer[count];
            tids = new String[count];
            names = new String[count];
            avto_types = new String[count];
            avto_time = new String[count];
        }

        public void trim() {
            if (data_order_count == ids.length) return;
            ids = Arrays.copyOfRange(ids, 0, data_order_count);
            tids = Arrays.copyOfRange(tids, 0, data_order_count);
            names = Arrays.copyOfRange(names, 0, data_order_count);
            avto_time = Arrays.copyOfRange(avto_time, 0, data_order_count);
            avto_types = Arrays.copyOfRange(avto_types, 0, data_order_count);
        }

        public void clear() {
            data_order_count = 0;
        }
    }

    public final Order mOrder = new Order();
    public final Order mOrder_Success = new Order();

    public void preparedata() {
        String[] orders_id = Helpers.GetStringArray(mContext, ORDERS);
        int count = orders_id.length;

        mOrder.init(count);
        mOrder_Success.init(count);
        if (count <= 0) return;


        for (String str_id : orders_id) {

            int id = Integer.parseInt(str_id);
            if (Helpers.GetInt(mContext, SUCCESS_ + id) == -1) {
                int i = ++mOrder.i;
                mOrder.ids[i] = id;
                mOrder.data_order_count++;
                mOrder.tids[i] = String.valueOf(Helpers.GetInt(mContext, ORDER_TID_ + id));
                mOrder.names[i] = Helpers.GetString(mContext, NAME_DOCS_ + id);
                mOrder.avto_types[i] = Helpers.GetString(mContext, TYPE_ + id);
                mOrder.avto_time[i] = Helpers.GetString(mContext, TIME_DOCS_ + id);
            } else {
                int i = ++mOrder_Success.i;
                mOrder_Success.ids[i] = id;
                mOrder_Success.data_order_count++;
                mOrder_Success.tids[i] = String.valueOf(Helpers.GetInt(mContext, ORDER_TID_ + id));
                mOrder_Success.names[i] = Helpers.GetString(mContext, NAME_DOCS_ + id);
                mOrder_Success.avto_types[i] = Helpers.GetString(mContext, TYPE_ + id);
                mOrder_Success.avto_time[i] = Helpers.GetString(mContext, TIME_DOCS_ + id);
            }
        }
        mOrder.trim();
        mOrder_Success.trim();
    }

    //##############################################################################################

    public static String formatTimeDocs(Date time) {
        DateFormat df = new SimpleDateFormat("d:MM:yy");
        return df.format(time);
    }

    //##############################################################################################

}
