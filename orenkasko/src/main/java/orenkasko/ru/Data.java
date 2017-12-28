package orenkasko.ru;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.Random;

import orenkasko.ru.Utils.AppResource;
import orenkasko.ru.Utils.Helpers;
import orenkasko.ru.ui.base.ImageLoader;

/**
 * Created by admin on 24.11.2017.
 */

public class Data extends AppResource {

    private final static String TAG = "Data";

    private final static void Log(final String str) {
        Log.e(TAG, str);
    }

    public Data(Context context) {
        super(context);
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

    public int saveOsagoDat(
            int oreder_id,
            int navigators,
            String type,
            String save_dat) {

        int new_order = oreder_id;
        if (-1 == new_order) {
            new_order = getNewOrderId();
            SaveInt(ORDER_CURR_ID, new_order);

            int rand_id = 10000 + (new Random()).nextInt(99999);

            SaveInt(TMP_ORDERS_TID, rand_id);
            SaveString(TMP_OSAGO_DAT, save_dat);
            SaveInt(TMP_NAVIGATORS, navigators);
            SaveString(TMP_TYPE, type);


            DelInt(SUCCESS_ + new_order);

            DelString(NAME_DOCS_ + new_order);
            DelString(TIME_DOCS_ + new_order);
            DelString(OSAGO_DOCS_ + new_order);
            DelString(OSAGO_DAT_ + new_order);
            DelInt(NAVIGATORS_ + new_order);
            DelInt(ORDER_TID_ + new_order);
            DelString(TYPE_ + new_order);

            return new_order;
        }

        SaveString(OSAGO_DAT_ + new_order, save_dat);
        SaveInt(NAVIGATORS_ + new_order, navigators);
        SaveString(TYPE_ + new_order, type);
        return new_order;
    }


    public int getTMPorderId() {
        return GetInt(ORDER_CURR_ID, -1);
    }

    public String getOsagoDat(int order_id) {
        if (-1 == order_id)
            return "";
        if (order_id == GetInt(ORDER_CURR_ID, -1))
            return GetString(TMP_OSAGO_DAT);
        return GetString(OSAGO_DAT_ + order_id);
    }

    public int getNavigators(int order_id) {
        if (-1 == order_id)
            return -1;
        if (order_id == GetInt(ORDER_CURR_ID, -1))
            return GetInt(TMP_NAVIGATORS, 0);
        return GetInt(NAVIGATORS_ + order_id, 0);
    }

    public String getDocs(int order_id) {
        if (order_id == -1) return "";
        return GetString(OSAGO_DOCS_ + order_id);
    }

    public void saveDocs(int order_id, String[] img_urls, boolean success, String name, String time_docs,
                         boolean change_owner, String data) {
        if (order_id == -1) return;

        if (null != mCPL) mCPL.save_docs_start();

        if (order_id == GetInt(ORDER_CURR_ID, -1)) {

            addOrderId(order_id);

            SaveString(OSAGO_DAT_ + order_id, GetString(TMP_OSAGO_DAT));
            SaveInt(NAVIGATORS_ + order_id, GetInt(TMP_NAVIGATORS));
            SaveInt(ORDER_TID_ + order_id, GetInt(TMP_ORDERS_TID));
            SaveString(TYPE_ + order_id, GetString(TMP_TYPE));


            DelInt(ORDER_CURR_ID);
            DelString(TMP_OSAGO_DAT);
            DelInt(TMP_NAVIGATORS);
            DelString(TMP_TYPE);
        }

        if (GetInt(SUCCESS_ + order_id) == -1)
            SaveInt(SUCCESS_ + order_id, success ? 1 : -1);

        SaveString(NAME_DOCS_ + order_id, name);
        SaveString(TIME_DOCS_ + order_id, time_docs);
        SaveString(OSAGO_DOCS_ + order_id, data);

        if (success) {

            String[] str_dat = getOsagoDat(order_id).split("\n");
            String[] str_docs = data.split("\n");//getDocs(order_id).split("\n");

            String imgs = "";
            for (String str : img_urls) {
                if (imgs.length() <= 0) {
                    imgs = "[";
                } else {
                    imgs += ",";
                }
                imgs += "\"" + str + "\"";
            }
            imgs += "]";

            String[] params = {
                    "password", getPassword(),
                    "phone", getPhone(),
                    "switch_first", str_dat[0],
                    "navigators", str_dat[1],
                    "item_possessor", str_dat[2],
                    "item_type", str_dat[3],
                    "item_sub_type", str_dat[4],
                    "item_power", str_dat[5],
                    "item_period_ispolzovania", str_dat[6],
                    "item_region", str_dat[7],
                    "item_city", str_dat[8],
                    "item_driverAgeStage", str_dat[9],
                    "item_discount", str_dat[10],
                    //____________________
                    "fio", str_docs[0],
                    "email", str_docs[1],
                    "phone", str_docs[2],
                    "comments", str_docs[3],
                    "name_card", str_docs[4],//карта диагностическая
                    "time_card", str_docs[5],
                    "time_docs", time_docs,//дата начала подачи или начала работы страховки
                    "change_owner", Boolean.toString(change_owner),//страхователь это собственник +- (2 картинки паспорта)
                    //_____________________
                    "imgs", imgs
            };

            Helpers.SendPost(new Helpers.RequestCallback() {
                @Override
                public void callback(String[] result) {
                    Log("after saveDocs " + result[0]);

                    if (null != mCPL) mCPL.save_docs_end(result);
                }
            }, params);
        }
    }

    public String getTimeDocs(int order_id) {
        String ret = GetString(TIME_DOCS_ + order_id);
        if (ret.length() <= 0) {
            ret = formatTimeDocs(Calendar.getInstance().getTime());
        }
        return ret;
    }

    //##############################################################################################

    static String login_phone = "phone";
    static String login_password = "password";
    static String login_name = "name";
    static String login_email = "login_email";
    static String login_image = "login_image";

    public void savePhone(String phone, String pass) {
        SaveString(login_phone, phone);
        SaveString(login_password, pass);
    }

    public String getPhone() {
        return GetString(login_phone);
    }

    public String getPassword() {
        return GetString(login_password);
    }

    public void saveName(String name) {
        SaveString(login_name, name);


        String[] params = {
                login_phone, getPhone(),
                login_name, getName()
        };

        Helpers.SendPost(new Helpers.RequestCallback() {
            @Override
            public void callback(String[] result) {
                Log("after saveName " + result[0]);
            }
        }, params);
    }

    public String getName() {
        return GetString(login_name);
    }

    public void saveEmail(String email) {
        SaveString(login_email, email);
    }

    public String getEmail() {
        return GetString(login_email);
    }

    public void SaveProfileImage(Bitmap bitmap) {
        SaveImage(bitmap, login_image);
    }

    public Bitmap getProfileImage() {
        return GetImage(login_image);
    }

    public void clear() {
        Delete();
        RmImages(login_image);
    }

    //##############################################################################################
    private int getNewOrderId() {
        //todo tests
        String[] array_orders = GetStringArray(ORDERS);
        int new_id = 0;
        for (String str : array_orders) {
            if (Integer.parseInt(str) == new_id) {
                new_id++;
            }
        }
        return new_id;
    }

    private void addOrderId(int order_id) {
        AddInArray(ORDERS, order_id);
    }

    public void rmOrder(int order_id) {
        if (order_id == -1) return;
        RmInArray(ORDERS, order_id);
        RmImages(IMAGE_ + order_id);
    }

    public void deleteImage(int order_id, ArrayList<ImageLoader> images) {
        ClearImages(IMAGE_ + order_id, images);
    }

    public void saveImages(Context context, int order_id, boolean success, ArrayList<ImageLoader> images) {
        if (null != mCPL) mCPL.save_img_start();

        if (!success) {
            SaveImages(IMAGE_ + order_id, images);
            if (null != mCPL) mCPL.save_img_end(null);
        } else {
            Helpers.SendImage(context, new Helpers.RequestCallback() {
                @Override
                public void callback(String[] result) {
                    Log("saveImages = " + result.length);
                    if (null != mCPL) mCPL.save_img_end(result);
                }
            }, images);
        }
    }

    public ArrayList<Bitmap> getImage(int order_id) {
        return GetImages(IMAGE_ + order_id);
    }

    public interface changed_process_load {
        void save_docs_start();

        void save_docs_end(String[] request);

        void save_img_start();

        void save_img_end(String[] urls);
    }

    private changed_process_load mCPL;

    public void setChangedProcessLoad(changed_process_load cpl) {
        mCPL = cpl;
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
        String[] orders_id = GetStringArray(ORDERS);
        int count = orders_id.length;

        mOrder.init(count);
        mOrder_Success.init(count);
        if (count <= 0) return;


        for (String str_id : orders_id) {

            int id = Integer.parseInt(str_id);
            if (GetInt(SUCCESS_ + id) == -1) {
                int i = ++mOrder.i;
                mOrder.ids[i] = id;
                mOrder.data_order_count++;
                mOrder.tids[i] = String.valueOf(GetInt(ORDER_TID_ + id));
                mOrder.names[i] = GetString(NAME_DOCS_ + id);
                mOrder.avto_types[i] = GetString(TYPE_ + id);
                mOrder.avto_time[i] = GetString(TIME_DOCS_ + id);
            } else {
                int i = ++mOrder_Success.i;
                mOrder_Success.ids[i] = id;
                mOrder_Success.data_order_count++;
                mOrder_Success.tids[i] = String.valueOf(GetInt(ORDER_TID_ + id));
                mOrder_Success.names[i] = GetString(NAME_DOCS_ + id);
                mOrder_Success.avto_types[i] = GetString(TYPE_ + id);
                mOrder_Success.avto_time[i] = GetString(TIME_DOCS_ + id);
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
