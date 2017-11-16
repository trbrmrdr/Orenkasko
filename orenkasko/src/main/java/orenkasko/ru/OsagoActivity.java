package orenkasko.ru;

import android.os.Bundle;
import android.support.v7.app.AppCompatDelegate;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;

import java.util.HashMap;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnCheckedChanged;
import butterknife.OnClick;
import butterknife.OnItemSelected;
import orenkasko.ru.ui.base.BaseActivity;

public class OsagoActivity extends BaseActivity {


    @Bind(R.id.spin_way)
    Spinner spin_way;

    @OnClick(R.id.item_spin_way)
    public void on_click_spin_way(View v) {
        spin_way.performClick();
    }

    int possessor = 1;

    @OnItemSelected(R.id.spin_way)
    public void spin_way_selected(int index) {
        possessor = index + 1;
        if (1 == possessor) {
            item_spin_driver_age.setEnabled(true);
        } else if (2 == possessor) {
            item_spin_driver_age.setEnabled(false);
        }
    }

    //___________________________
    @Bind(R.id.spin_sub_type)
    Spinner spin_sub_type;

    @Bind(R.id.item_spin_sub_type)
    RelativeLayout item_spin_sub_type;

    @Bind(R.id.spin_type)
    Spinner spin_type;

    @OnClick(R.id.item_spin_type)
    public void on_click_spin_type(View v) {
        spin_type.performClick();
    }

    @OnClick(R.id.item_spin_sub_type)
    public void on_click_spin_sub_type(View v) {
        spin_type.performClick();
    }

    ArrayAdapter<String> adapter_sub_type_b;
    ArrayAdapter<String> adapter_sub_type_c;
    ArrayAdapter<String> adapter_sub_type_d;

    @OnItemSelected(R.id.spin_type)
    public void spin_type_selected(int index) {
        if (0 == index) {
            spin_sub_type.setAdapter(adapter_sub_type_b);
            spin_power.setEnabled(true);
        } else {
            spin_power.setEnabled(false);
        }

        if (1 == index) {
            spin_sub_type.setAdapter(adapter_sub_type_c);
        } else if (2 == index) {
            spin_sub_type.setAdapter(adapter_sub_type_c);
        }

        if (index <= 2) {
            item_spin_sub_type.setVisibility(View.VISIBLE);
        } else
            item_spin_sub_type.setVisibility(View.GONE);
    }

    @OnItemSelected(R.id.spin_sub_type)
    public void spin_sub_type_selected(int index) {

    }

    //________________ power
    @Bind(R.id.spin_power)
    Spinner spin_power;

    @OnClick(R.id.item_spin_power)
    public void on_click_spin_power(View v) {
        spin_power.performClick();
    }

    @OnItemSelected(R.id.spin_power)
    public void spin_power_selected(int index) {
    }

    //________________ period
    @Bind(R.id.spin_period)
    Spinner spin_period;

    @OnClick(R.id.item_spin_period)
    public void on_click_spin_period(View v) {
        spin_period.performClick();
    }

    @OnItemSelected(R.id.spin_period)
    public void spin_period_selected(int index) {
    }

    //________________ _region

    int[] adapter_region_value;
    HashMap<Integer, ArrayAdapter> adapters_city = new HashMap<>();

    @Bind(R.id.spin_region)
    Spinner spin_region;

    @OnClick(R.id.item_spin_region)
    public void on_click_spin_region(View v) {
        spin_region.performClick();
    }

    @OnItemSelected(R.id.spin_region)
    public void spin_region_selected(int index) {
        int id = adapter_region_value[index];
        if (-1 == id) {
            item_spin_city.setVisibility(View.GONE);
        } else {
            item_spin_city.setVisibility(View.VISIBLE);

            spin_city.setAdapter(adapters_city.get(id));
        }
    }

    //________________ _city
    @Bind(R.id.item_spin_city)
    RelativeLayout item_spin_city;

    @Bind(R.id.spin_city)
    Spinner spin_city;

    @OnClick(R.id.item_spin_city)
    public void on_click_spin_city(View v) {
        spin_city.performClick();
    }

    @OnItemSelected(R.id.spin_city)
    public void spin_city_selected(int index) {
    }

    //________________
    @Bind(R.id.item_navigator_text_count)
    TextView item_navigator_text_count;

    @Bind(R.id.item_navigator_remove)
    ImageButton navigator_remove;

    int navigators = 0;

    int navigation;

    @OnClick(R.id.item_navigator_add)
    void navigator_add(View view) {
        navigators += 1;
        change_navigators();
    }

    private void change_navigators() {
        if (navigators < 1) {
            navigation = 1;
            //item_spin_driver_age.setVisibility(View.GONE);
            item_spin_driver_age.setEnabled(true);
            item_navigator_text_count.setText("Без ограничений");

            navigator_remove.setVisibility(View.GONE);
        } else {
            navigation = 2;
            //item_spin_driver_age.setVisibility(View.VISIBLE);
            item_spin_driver_age.setEnabled(false);
            navigator_remove.setVisibility(View.VISIBLE);
            item_navigator_text_count.setText(String.valueOf(navigators));
        }

    }

    @OnClick(R.id.item_navigator_remove)
    void navigator_remove(View view) {
        navigators -= 1;
        change_navigators();
    }

    //________________ _driver_age
    @Bind(R.id.item_spin_driver_age)
    RelativeLayout item_spin_driver_age;

    @Bind(R.id.spin_driver_age)
    Spinner spin_driver_age;

    @OnClick(R.id.item_spin_driver_age)
    public void on_click_spin_driver_age(View v) {
        spin_driver_age.performClick();
    }

    @OnItemSelected(R.id.spin_driver_age)
    public void spin_driver_age_selected(int index) {
    }

    //________________ _first
    @Bind(R.id.switch_first)
    Switch switch_first;

    @OnCheckedChanged(R.id.switch_first)
    public void switch_first_changed(CompoundButton buttonView, boolean isChecked) {
        Log("");
        if (isChecked) {
            item_spin_discount.setVisibility(View.VISIBLE);

            Kbm = 1.f;
        } else {
            item_spin_discount.setVisibility(View.GONE);
        }

    }

    //________________ _discount

    float[] adapter_discount_val;
    @Bind(R.id.item_spin_discount)
    RelativeLayout item_spin_discount;

    @Bind(R.id.spin_discount)
    Spinner spin_discount;

    @OnClick(R.id.item_spin_discount)
    public void on_click_spin_discount(View v) {
        spin_discount.performClick();
    }

    @OnItemSelected(R.id.spin_discount)
    public void spin_discount_selected(int index) {
        Kbm = adapter_discount_val[index];
    }

    //__________________________
    @Bind(R.id.spin_insurance)
    Spinner spin_insurance;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_osago);
        ButterKnife.bind(this);

        switch_first.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

            }
        });

        adapter_sub_type_b = (ArrayAdapter<String>) createAdapter(R.array.array_spin_type_b);
        adapter_sub_type_c = (ArrayAdapter<String>) createAdapter(R.array.array_spin_type_c);
        adapter_sub_type_d = (ArrayAdapter<String>) createAdapter(R.array.array_spin_type_d);

        //___________________
        adapter_discount_val = getFloatArray(getResources().getStringArray(R.array.array_spin_discount_val));
        //___________________
        adapter_region_value = getResources().getIntArray(R.array.array_spin_region_value);

        int i = 0;
        for (int id : adapter_region_value) {
            if (id == -1) continue;
            try {
                ArrayAdapter adapter = createAdapter(Application.getAppResources().getArrayId("array_spin_city_" + id));
                adapters_city.put(id, adapter);
            } catch (Exception e) {

            }
        }

        //setDropDownAdapter(spin_way, R.layout.oreder_spin_dropdown_item);
        //setDropDownAdapter(spin_type, R.layout.oreder_spin_dropdown_item);
        //___________
        navigator_add(null);
        //____________
        spin_insurance.setEnabled(false);

    }

    private float[] getFloatArray(String[] strings) {
        float[] ret = new float[strings.length];
        for (int i = 0; i < ret.length; i++) {
            ret[i] = Float.parseFloat(strings[i]);
        }
        return ret;
        //return Arrays.stream(strings).map(Float::valueOf).toArray(Float[]::new);
        //return Arrays.stream(strings).mapToDouble(Float::parseFloat).toArray();
    }

    float Tb = 0; // Базовая ставка // тип ТС
    float Kt = 0; // коэф региона
    float Kvs_Ko = 1.7f; // коэф стажа
    float Km = 0; // коэф мощности
    float Kc = 0; // коэф периода использования
    float Kp = 0; // коэф срока страхования
    float Kbm = 1; // коэф за безаварийную езду

    void calculate() {
        /*
        var sum = Math.round(Number(Tb) * Number(Kt) * Number(Kvs_Ko) * Number(Km) * Number(Kc) * Number(Kp) * Number(Kbm) * 100) / 100;

        $(".form__result span").html(sum + ' рублей');
        $('#amount').val(sum);

        $("#variable > div").empty().append(
                "<b>Базовая ставка (Tb):</b> " + Tb + "<br />" +
                        "<b>коэф региона (Kt):</b> " + Kt + "<br />" +
                        "<b>коэф стажа (Kvs_Ko):</b> " + Kvs_Ko + "<br />" +
                        "<b>коэф мощности (Km):</b> " + Km + "<br />" +
                        "<b>коэф периода использования (Kc):</b> " + Kc + "<br />" +
                        "<b>коэф срока страхования (Kp):</b> " + Kp + "<br />" +
                        "<b>коэф за безаварийную езду (Kbm):</b> " + Kbm + "<br />" +
                        "<b>Стоимость (sum = Tb * Kt * Kvs_Ko * Km * Kc * Kp * Kbm):</b> " + sum + "<br />"
        );
    }

   */
    }

    private ArrayAdapter<?> createAdapter(int array_res_id) {
        ArrayAdapter<?> ret = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, getResources().getStringArray(array_res_id));
        ret.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        return ret;
    }

    private void setDropDownAdapter(Spinner spiner, int dropdown_item_red_id) {
        ArrayAdapter<String> adapter = (ArrayAdapter<String>) spiner.getAdapter();
        adapter.setDropDownViewResource(dropdown_item_red_id);
    }


    //________________________________________

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //getMenuInflater().inflate(R.menu.sample_actions, menu);
        return true;
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

    @Override
    protected int getSelfNavDrawerItem() {
        return R.id.nav_osago;
    }

    @Override
    public boolean providesActivityToolbar() {
        return true;
    }


}
