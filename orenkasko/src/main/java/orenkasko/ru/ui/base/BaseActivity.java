package orenkasko.ru.ui.base;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;

import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;
import orenkasko.ru.Application;
import orenkasko.ru.BalanceActivity;
import orenkasko.ru.Data;
import orenkasko.ru.LoginActivity;
import orenkasko.ru.OrdersActivity;
import orenkasko.ru.OsagoActivity;
import orenkasko.ru.ProfileActivity;
import orenkasko.ru.R;
import orenkasko.ru.Utils.Helpers;

/**
 * The base class for all Activity classes.
 * This class creates and provides the navigation drawer and toolbar.
 * The navigation logic is handled in {@link BaseActivity#goToNavDrawerItem(int)}
 */
public abstract class BaseActivity extends AppCompatActivity {

    static {
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
    }

    private static final String TAG = "BaseActivity";

    public final static void Log(final String msg) {
        Log.e(TAG, msg);
    }

    protected static final int NAV_DRAWER_ITEM_INVALID = -1;

    private DrawerLayout drawerLayout;
    private Toolbar actionBarToolbar;

    public CircleImageView circleImageView;

    public void setupToolbar() {
        final ActionBar ab = getActionBarToolbar();
        ab.setHomeAsUpIndicator(R.drawable.ic_menu);
        ab.setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                openDrawer();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /*
    @OnTouch(R.id.linearLayout_nav_header)
    public boolean on_toucn_profile(View v, MotionEvent event) {
        startActivity(new Intent(this, ProfileActivity.class));
        return true;
    }
    */

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        setupNavDrawer();
        setupToolbar();
        //ButterKnife.bind(this);


        /*
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        View header_view = navigationView.getHeaderView(0);
        if (null != header_view) {
            header_view.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    startActivity(new Intent(BaseActivity.this, ProfileActivity.class));
                    return true;
                }
            });
            circleImageView = (CircleImageView) header_view.findViewById(R.id.main_profile_image);
            setProfileImage(Application.getData().getProfileImage());
        }
        */
    }

    public void setProfileImage(Bitmap bitmap) {
        if (null != circleImageView) {
            circleImageView.setImageBitmap(bitmap);
        }
    }

    /**
     * Sets up the navigation drawer.
     */
    private void setupNavDrawer() {
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawerLayout == null) {
            // current activity does not have a drawer.
            return;
        }

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        if (navigationView != null) {
            setupDrawerSelectListener(navigationView);
            setSelectedItem(navigationView);
        }

        Log("navigation drawer setup finished");
    }

    /**
     * Updated the checked item in the navigation drawer
     *
     * @param navigationView the navigation view
     */
    private void setSelectedItem(NavigationView navigationView) {
        // Which navigation item should be selected?
        int selectedItem = getSelfNavDrawerItem(); // subclass has to override this method
        navigationView.setCheckedItem(selectedItem);
    }

    /**
     * Creates the item click listener.
     *
     * @param navigationView the navigation view
     */
    private void setupDrawerSelectListener(NavigationView navigationView) {
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {
                        drawerLayout.closeDrawers();
                        onNavigationItemClicked(menuItem.getItemId());
                        return true;
                    }
                });
    }

    /**
     * Handles the navigation item click.
     *
     * @param itemId the clicked item
     */
    private void onNavigationItemClicked(final int itemId) {
        if (itemId == getSelfNavDrawerItem()) {
            // Already selected
            closeDrawer();
            return;
        }

        goToNavDrawerItem(itemId);
    }

    /**
     * Handles the navigation item click and starts the corresponding activity.
     *
     * @param item the selected navigation item
     */
    private void goToNavDrawerItem(int item) {
        switch (item) {
            /*
            case R.id.nav_balance:
                startActivity(new Intent(this, BalanceActivity.class));
                finish();
                break;


            case R.id.nav_profile:
                startActivity(new Intent(BaseActivity.this, ProfileActivity.class));
                finish();

                break;
                */
            case R.id.nav_orders:
                startActivity(new Intent(this, OrdersActivity.class));
                break;
            case R.id.nav_osago:
                Intent intent = new Intent(this, OsagoActivity.class);
                intent.putExtra(Data.key_oreder_id, -1);
                startActivity(intent);
                finish();
                break;
            case R.id.nav_logout:
                Application.getData().clear();

                Helpers.StartClean(this, LoginActivity.class);
                finish();
                break;
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        Class<?> clazz = getClass();
        if (clazz == OsagoActivity.class) return;
        if (clazz == OrdersActivity.class) return;

        overridePendingTransition(R.anim.left_in, R.anim.right_out);
    }

    /**
     * Provides the action bar instance.
     *
     * @return the action bar.
     */
    protected ActionBar getActionBarToolbar() {
        if (actionBarToolbar == null) {
            actionBarToolbar = (Toolbar) findViewById(R.id.toolbar);
            if (actionBarToolbar != null) {
                setSupportActionBar(actionBarToolbar);
            }
        }
        return getSupportActionBar();
    }


    /**
     * Returns the navigation drawer item that corresponds to this Activity. Subclasses
     * have to override this method.
     */
    protected int getSelfNavDrawerItem() {
        return NAV_DRAWER_ITEM_INVALID;
    }

    protected void openDrawer() {
        if (drawerLayout == null)
            return;

        drawerLayout.openDrawer(GravityCompat.START);
    }

    protected void closeDrawer() {
        if (drawerLayout == null)
            return;

        drawerLayout.closeDrawer(GravityCompat.START);
    }

    //public abstract boolean providesActivityToolbar();

    public boolean providesActivityToolbar() {
        return true;
    }

    public void setToolbar(Toolbar toolbar) {
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }
}
