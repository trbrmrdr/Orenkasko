package orenkasko.ru;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;

import java.util.ArrayList;

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
    @Bind(R.id.text_comments)
    EditText text_comments;


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


    ArrayList<ItemDocs> items_docs = new ArrayList<>();

    int oreder_id = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal_data);
        ButterKnife.bind(this);

        oreder_id = savedInstanceState.getInt(Data.key_oreder_id, -1);


        //todo read save in this activity fio etc
        EditText text_phone = this.text_phone;

        text_phone.setText(getText(R.string.phone_start_text));
        text_phone.addTextChangedListener(new Helpers.TextWatcher_Phone(this, text_phone));

        int count_docs = Data.getNavigators(this, oreder_id);
        for (int i = 0; i < count_docs; ++i) {
            ItemDocs docs = new ItemDocs(this);
            docs.setLayoutParams(new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT));

            docs.setText("какието документы #" + (i + 1));

            items_docs.add(docs);
            layout_from_docs.addView(docs);
        }
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        for (ImageLoader image : ImageLoader._images) {
            image.setActivity(this);
        }
    }

    @OnClick(R.id.tab)
    public void onTabClicked(View view) {
        action_finish(fab);
    }

    @OnClick(R.id.fab)
    public void onFabClicked(View view) {
        action_finish(view);
    }

    private void action_finish(View view) {

        String fio = text_fio.getText().toString();
        String email = text_email.getText().toString();
        String phone = text_phone.getText().toString();
        String comments = text_comments.getText().toString();


        String msg = fio.length() <= 0 ? "Введите имя" :
                !email.contains("@") ? "Некорректный адрес @" :
                        phone.length() != 17 ? "Неверный телефон" :
                                !pasport_owner.hasLoaded() ? "Незагржены документы" : "";

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

        String data = fio + "\n" +
                email + "\n" +
                phone + "\n" +
                comments;
        Data.saveDocs(this, oreder_id, data);

        Helpers.StartClean(this, OrdersActivity.class);
    }


    @Override
    protected int getSelfNavDrawerItem() {
        return R.id.nav_osago;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        for (ImageLoader image : ImageLoader._images) {
            image.onActivityResult(requestCode, resultCode, data);
        }
    }
}
