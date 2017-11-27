package orenkasko.ru;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Calendar;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnCheckedChanged;
import butterknife.OnClick;
import orenkasko.ru.Utils.Helpers;
import orenkasko.ru.ui.base.BaseActivity;
import orenkasko.ru.ui.base.ImageLoader;
import orenkasko.ru.ui.base.ItemDocs;


//image loader
//https://github.com/Mariovc/ImagePicker.git

public class PersonalDataActivity extends BaseActivity {

    @Bind(R.id.fab)
    FloatingActionButton fab;

    @Bind(R.id.pasport_owner)
    ItemDocs pasport_owner;

    @Bind(R.id.text_fio)
    EditText text_fio;
    @Bind(R.id.text_email)
    EditText text_email;
    @Bind(R.id.text_phone)
    EditText text_phone;
    @Bind(R.id.text_time)
    TextView text_time;
    @Bind(R.id.text_comments)
    EditText text_comments;

    @Bind(R.id.text_name_card)
    EditText text_name_card;
    @Bind(R.id.text_time_card)
    TextView text_time_card;

    @Bind(R.id.layout_change_data)
    ViewGroup layout_change_data;
    @Bind(R.id.layout_change_data_card)
    ViewGroup layout_change_data_card;

    ViewGroup layout_select;

    void show_calendar(View view) {
        layout_select = (ViewGroup) view;
        calendarDialog.show();
    }

    @Bind(R.id.layout_from_docs)
    LinearLayout layout_from_docs;

    @OnCheckedChanged(R.id.change_owner)
    void change_owner(CompoundButton button, boolean isChecked) {
        if (isChecked) {
            pasport_owner.setVisibility(View.GONE);
        } else {
            pasport_owner.setVisibility(View.VISIBLE);
        }
    }


    DatePickerDialog calendarDialog;
    ArrayList<ItemDocs> items_docs = new ArrayList<>();

    int order_id = -1;
    int count_docs = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ImageLoader.ClearImages();

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal_data);
        ButterKnife.bind(this);

        order_id = getIntent().getIntExtra(Data.key_oreder_id, -1);

        readData();

        text_phone.addTextChangedListener(new Helpers.TextWatcher_Phone(this, text_phone));
        //__________________

        Calendar calendar = Calendar.getInstance();
        calendarDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                Calendar newDate = Calendar.getInstance();
                newDate.set(year, monthOfYear, dayOfMonth);
                String text = Data.formatTimeDocs(newDate.getTime());

                if (layout_select == layout_change_data)
                    text_time.setText(text);
                else
                    text_time_card.setText(text);
            }

        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));

        //__________________
        count_docs = Data.getNavigators(order_id);
        String driving_permits = getResources().getString(R.string.drivings_permits);
        for (int i = 0; i < count_docs; ++i) {
            ItemDocs docs = new ItemDocs(this);
            docs.setLayoutParams(new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT));

            docs.setText(driving_permits + " " + String.valueOf(i + 1));

            items_docs.add(docs);
            layout_from_docs.addView(docs);
            docs.invalidate();
        }
        //#################################

        readImage();
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        ImageLoader.SetActivity(this);
    }

    @OnClick(R.id.tab)
    public void onTabClicked(View view) {
        action_finish(fab);
    }

    @OnClick(R.id.fab)
    public void onFabClicked(View view) {
        action_finish(view);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        saveData(false);
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, OsagoActivity.class);
        intent.putExtra(Data.key_oreder_id, order_id);
        Helpers.StartClean(this, intent, OsagoActivity.class);
        super.onBackPressed();
    }

    private void action_finish(View view) {

        String fio = text_fio.getText().toString();
        String email = text_email.getText().toString();
        String phone = text_phone.getText().toString();
        //String comments = text_comments.getText().toString();
        String name_card = text_name_card.getText().toString();


        String msg = fio.length() <= 0 ? "Введите имя" :
                !email.contains("@") ? "Некорректный адрес @" :
                        phone.length() != 17 ? "Неверный телефон" :
                                //!pasport_owner.hasLoaded() ? "Незагржены документы" :
                                name_card.length() <= 0 ? "Номер карты невведёт" : "";

        //if (msg.length() <= 0)
        if (false) {
            int i = 0;
            for (ItemDocs docs : items_docs) {
                i++;
                if (!docs.hasLoaded()) {
                    msg = " чтото там - " + i;
                    break;
                }
            }
        }

        if (msg.length() > 0) {
            Snackbar.make(view, msg, Snackbar.LENGTH_LONG).setAction("Error", null).show();
            return;
        }
        saveData(true);
        Helpers.StartClean(this, OrdersActivity.class);
    }


    @Override
    protected int getSelfNavDrawerItem() {
        return R.id.nav_osago;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        ImageLoader.OnActivityResult(requestCode, resultCode, data);
    }

    private void readData() {

        String[] strs = Data.getDocs(order_id).split("\n");

        text_time.setText(Data.getTimeDocs(order_id));
        if (strs.length > 1) {
            text_fio.setText(strs[0]);
            text_email.setText(strs[1]);
            text_phone.setText(strs[2]);
            text_comments.setText(strs[3]);

            text_name_card.setText(strs[4]);
            text_time_card.setText(strs[5]);
        } else {
            text_phone.setText(getText(R.string.phone_start_text));
        }
        //todo photo
        //....
    }

    private void readImage() {
        ImageLoader.ReadImages(Data.getImage(order_id));
    }

    boolean save_not_needed = false;

    private void saveData(boolean success) {
        if (save_not_needed) return;
        save_not_needed = true;
        String fio = text_fio.getText().toString();
        String email = text_email.getText().toString();
        String phone = text_phone.getText().toString();
        String comments = text_comments.getText().toString();

        String name_card = text_name_card.getText().toString();
        String time_card = text_time_card.getText().toString();

        String data = fio + "\n" +
                email + "\n" +
                phone + "\n" +
                comments + "\n" +
                name_card + "\n" +
                time_card;

        String time_docs = text_time.getText().toString();
        Data.saveDocs(order_id, success, fio, time_docs, data);
        Data.saveImages(order_id, ImageLoader._images);
    }
}
