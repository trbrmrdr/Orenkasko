package orenkasko.ru;

import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import butterknife.ButterKnife;
import butterknife.OnClick;
import orenkasko.ru.ui.base.BaseActivity;

public class BalanceActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_balance);
        ButterKnife.bind(this);
    }

    @Override
    protected int getSelfNavDrawerItem() {
        return -1;
        //return R.id.nav_balance;
    }

}
