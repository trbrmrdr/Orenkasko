package orenkasko.ru;

import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.NestedScrollView;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;

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

    @Bind(R.id.root_scroll)
    NestedScrollView root_scroll;

    @Bind(R.id.fab)
    FloatingActionButton fab;

    @Bind(R.id.pasport_owner)
    ItemDocs pasport_owner;

    @Bind(R.id.pasport_change_owner)
    ItemDocs pasport_change_owner;

    @Bind(R.id.pasport_transport)
    ItemDocs pasport_transport;

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

    @OnClick(R.id.layout_change_data)
    void layout_change_data_click(View view) {
        show_calendar(layout_change_data);
    }

    @OnClick(R.id.layout_change_data_card)
    void layout_change_data_cardclick(View view) {
        show_calendar(layout_change_data_card);
    }

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
            pasport_change_owner.setVisibility(View.GONE);
            int size = ImageLoader._images.size();
            if (size > 2) ImageLoader._images.get(2).setLoaded(false, null);
            if (size > 3) ImageLoader._images.get(3).setLoaded(false, null);
        } else {
            pasport_change_owner.setVisibility(View.VISIBLE);
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

        order_id = getIntent().getIntExtra(Data.key_oreder_id, Application.getData().getTMPorderId());

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

        items_docs.add(pasport_owner);
        items_docs.add(pasport_change_owner);
        items_docs.add(pasport_transport);
        //__________________
        count_docs = Application.getData().getNavigators(order_id);
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
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        ImageLoader.OnRequestPermissionsResult(requestCode, permissions, grantResults);
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
        Helpers.StartClean(this, intent);
        super.onBackPressed();
    }

    private void action_finish(View view) {

        String fio = text_fio.getText().toString();
        String email = text_email.getText().toString();
        String phone = text_phone.getText().toString();
        //String comments = text_comments.getText().toString();
        String name_card = text_name_card.getText().toString();

        //if (false)
        if (true) {
            String msg = fio.length() <= 0 ? "Введите имя" :
                    !email.contains("@") ? "Некорректный адрес @" :
                            phone.length() != 17 ? "Неверный телефон" :
                                    //!pasport_change_owner.hasLoaded() ? "Незагржены документы" :
                                    name_card.length() <= 0 ? "Номер карты невведёт" : "";

            if (msg.length() <= 0) {
                boolean owner = pasport_change_owner.getVisibility() == View.GONE;
                int i = -1;
                for (ItemDocs docs : items_docs) {
                    i++;
                    if (i == 1 && owner) {
                        //если страховаетель есть собственник то пропускаем
                        if (docs.hasLoaded()) {//проверка на ошибку
                            docs.clear();
                        }
                        continue;
                    }
                    if (!docs.hasLoaded()) {
                        int c_i = i + 1;
                        if (owner && i > 0) c_i -= 1;
                        msg = c_i + (
                                c_i == 2 || (c_i >= 6 && c_i <= 8) ? "-ой"
                                        : c_i == 3 ? "-ий"
                                        : "-ый"
                        ) + " документ незагружен.";
                        break;
                    }
                }
            }
            if (msg.length() > 0) {
                Snackbar.make(view, msg, Snackbar.LENGTH_LONG).setAction("Error", null).show();
                return;
            }
        }

        saveData(true);
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

        String[] strs = Application.getData().getDocs(order_id).split("\n");

        text_time.setText(Application.getData().getTimeDocs(order_id));
        boolean showKeyboard = false;

        if (strs.length > 1) {
            text_fio.setText(strs[0]);
            text_fio.setSelection(strs[0].length());

            text_email.setText(strs[1]);
            text_email.setSelection(strs[1].length());

            text_phone.setText(strs[2]);
            //text_phone.setSelection(strs[2].length());

            text_comments.setText(strs[3]);
            text_comments.setSelection(strs[3].length());

            text_name_card.setText(strs[4]);
            text_name_card.setSelection(strs[4].length());

            text_time_card.setText(strs[5]);

            if (strs[0].length() <= 0) {
                text_fio.setFocusable(true);
                showKeyboard = true;
            } else {
                text_fio.clearFocus();
                if (strs[1].length() <= 0) {
                    text_email.requestFocus();
                    showKeyboard = true;
                } else {
                    text_email.clearFocus();
                    if (strs[2].length() <= 4) {

                        text_phone.requestFocus();
                        showKeyboard = true;
                    } else if (strs[4].length() <= 0) {
                        text_phone.clearFocus();
                        text_name_card.requestFocus();
                        //root_scroll.scrollTo(0, root_scroll.getMaxScrollAmount());
                        showKeyboard = true;
                    }
                }
            }
        } else

        {
            text_phone.setText(getText(R.string.phone_start_text));
            showKeyboard = true;
        }
        text_phone.setSelection(text_phone.getText().

                length());

        if (!showKeyboard)

        {
            this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        }

    }

    private void readImage() {
        ImageLoader.ReadImages(Application.getData().getImage(order_id));
    }

    boolean save_not_needed = false;

    private void saveData(final boolean success) {
        if (save_not_needed) return;
        if (success) {
            setVisibleProgress(true);
            //after useed -> call saveData_saving(true); (see in setVisibleProgress)
        } else {
            saveData_saving(success);
        }
    }

    private void saveData_saving(final boolean success) {
        save_not_needed = true;
        final String fio = text_fio.getText().toString();
        String email = text_email.getText().toString();
        String phone = text_phone.getText().toString();
        String comments = text_comments.getText().toString();

        String name_card = text_name_card.getText().toString();
        String time_card = text_time_card.getText().toString();

        final String data = fio + "\n" +
                email + "\n" +
                phone + "\n" +
                comments + "\n" +
                name_card + "\n" +
                time_card;

        final String time_docs = text_time.getText().toString();
        final boolean change_owner = pasport_change_owner.getVisibility() == View.GONE;

        Application.getData().setChangedProcessLoad(new Data.changed_process_load() {
            @Override
            public void save_docs_start() {
            }

            @Override
            public void save_docs_end(String[] request) {
                setVisibleProgress(false);

                Application.getData().deleteImage(order_id, ImageLoader._images);
                //if (true) return;
                Intent intent = new Intent(PersonalDataActivity.this, OrdersActivity.class);
                intent.putExtra(OrdersActivity.OPEN_SUCCESS, true);
                intent.removeExtra(Data.key_oreder_id);

                Helpers.StartClean(PersonalDataActivity.this, intent);
                overridePendingTransition(R.anim.right_in, R.anim.left_out);
            }

            @Override
            public void save_img_start() {
            }

            @Override
            public void save_img_end(String[] urls) {
                Application.getData().saveDocs(order_id,
                        urls,
                        success, fio, time_docs, change_owner,
                        data);
            }
        });

        Application.getData().saveImages(this, order_id, success, ImageLoader._images);
    }


    MaterialDialog progress_dialog;

    private void setVisibleProgress(boolean visible) {
        if (visible) {
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
            progress_dialog = new MaterialDialog.Builder(PersonalDataActivity.this)
                    .title("Загружаются данные")
                    .content("Пожалуйста подождите...")
                    .progress(true, 0)
                    .cancelable(false)
                    //.autoDismiss(true)
                    .showListener(new DialogInterface.OnShowListener() {
                        @Override
                        public void onShow(DialogInterface dialogInterface) {
                            new Handler(getMainLooper()).postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    saveData_saving(true);
                                }
                            }, 500);
                        }
                    })
                    .show();
        } else {
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
            progress_dialog.dismiss();
        }
    }
}
