package orenkasko.ru;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.view.View;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import orenkasko.ru.ui.base.BaseActivity;

public class PersonalDataActivity extends BaseActivity {

    @Bind(R.id.fab)
    FloatingActionButton fab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal_data);
        ButterKnife.bind(this);

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

}
