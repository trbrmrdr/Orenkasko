package orenkasko.ru;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.widget.CompoundButton;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnCheckedChanged;
import butterknife.OnClick;
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

    @OnCheckedChanged(R.id.change_owner)
    void change_owner(CompoundButton button, boolean isChecked) {
        if (isChecked) {
            pasport_owner.setVisibility(View.GONE);
        } else {
            pasport_owner.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal_data);
        ButterKnife.bind(this);
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
        Snackbar.make(view, "Hello Snackbar!", Snackbar.LENGTH_LONG).setAction("Action", null).show();
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
