package orenkasko.ru;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import orenkasko.ru.ui.base.BaseActivity;
import orenkasko.ru.ui.base.OrderContentFragment;


public class OrdersActivity extends BaseActivity {

    public static String FIRST_OPEN = "first_open";
    public static String OPEN_SUCCESS = "open_success";
    @Bind(R.id.viewpager)
    ViewPager viewPager;

    @Bind(R.id.tabs)
    TabLayout tabs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order);
        ButterKnife.bind(this);
        Application.getData().preparedata();
    }

    @Override
    public void setupToolbar() {
        super.setupToolbar();
        setupViewPager(viewPager);
        tabs.setupWithViewPager(viewPager);

        Data data = Application.getData();

        if (getIntent().hasExtra(OPEN_SUCCESS) ||
                data.mOrder_Success.data_order_count > 0)
            viewPager.setCurrentItem(1);


        if (getIntent().hasExtra(FIRST_OPEN) ||
                (data.mOrder.data_order_count <= 0 &&
                        data.mOrder_Success.data_order_count <= 0)
                )
            openDrawer();
    }

    // Add Fragments to Tabs
    private void setupViewPager(ViewPager viewPager) {
        Adapter adapter = new Adapter(getSupportFragmentManager());
        adapter.addFragment(new OrderContentFragment(0), "Ченовики");
        adapter.addFragment(new OrderContentFragment(1), "Оформление");
        adapter.addFragment(new OrderContentFragment(2), "Выполненные");
        viewPager.setAdapter(adapter);
    }

    static class Adapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public Adapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }


    @Override
    protected int getSelfNavDrawerItem() {
        return R.id.nav_orders;
    }

}
