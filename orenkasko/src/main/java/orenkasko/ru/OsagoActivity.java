package orenkasko.ru;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Pattern;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnCheckedChanged;
import butterknife.OnClick;
import orenkasko.ru.Utils.Helpers;
import orenkasko.ru.ui.base.BaseActivity;

public class OsagoActivity extends BaseActivity {

    //int spinner_item = android.R.layout.simple_spinner_item;
    int spinner_item = R.layout.spinner_item;
    int spinner_dropdown_item = android.R.layout.simple_spinner_dropdown_item;


    //on_registration_way
    //period
    //is_registered_abroad
    Item item_possessor;

    int possessor = 1;

    CallbackSelected select_possessor = new CallbackSelected() {
        @Override
        public void selected(int index) {
            possessor = index + 1;
            if (1 == possessor) {
                if (!without_limitation) {
                    item_driverAgeStage.setEnabled(true);
                }
            } else if (2 == possessor) {
                item_driverAgeStage.setEnabled(false);
            }
            cost();
        }
    };


    //______________________________________________________________________________________________

    Item item_type;
    Item item_sub_type;

    Types sub_type_def;
    Types sub_type_b;
    Types sub_type_c;
    Types sub_type_d;

    Types sub_type_selected;

    boolean type_traktor;

    CallbackSelected select_type = new CallbackSelected() {
        @Override
        public void selected(int index) {
            type_traktor = index == 6;
            if (0 == index) {
                item_power.setEnabled(true);
                sub_type_selected = sub_type_b;
            } else {
                item_power.setEnabled(false);
                if (1 == index) {
                    sub_type_selected = sub_type_c;
                } else if (2 == index) {
                    sub_type_selected = sub_type_d;
                } else {
                    sub_type_selected = null;
                }
            }

            if (index <= 2) {
                item_sub_type.setAdapter(sub_type_selected.types);
            } else {
                item_sub_type.setVisibility(false);

                Tb = sub_type_def.value[index];
                cost();
            }

            //cost();
        }
    };

    CallbackSelected select_sub_type = new CallbackSelected() {
        @Override
        public void selected(int index) {
            if (-1 == index || null == sub_type_selected) return;
            Tb = sub_type_selected.value[index];
            cost();
        }
    };

    //______________power___________________________________________________________________________
    Item item_power;
    float[] item_power_title;

    CallbackSelected select_power = new CallbackSelected() {
        @Override
        public void selected(int index) {
            if (index == -1) return;
            Km = item_power_title[index];
            cost();
        }
    };

    //______________period_ispolzovania_____________________________________________________________
    Item item_period_ispolzovania;
    float[] item_period_ispolzovania_title;

    CallbackSelected select_period_ispolzovania = new CallbackSelected() {
        @Override
        public void selected(int index) {
            Kc = item_period_ispolzovania_title[index];
            cost();
        }
    };

    //_____________region___________________________________________________________________________

    Item item_region;
    int[] item_region_title;

    CallbackSelected select_region = new CallbackSelected() {
        @Override
        public void selected(int index) {
            int id = item_region_title[index];
            region_selected = regions.get(id);
            if (null == region_selected || region_selected.isEmpty()) {
                item_city.setVisibility(false);
                cost();
            } else {
                item_city.setAdapter(region_selected.arrayAdapter);
            }
            //not see from spin_city_selected
            //cost();
        }
    };

    HashMap<Integer, Region> regions;
    Region region_selected;


    //___________________city_______________________________________________________________________
    Item item_city;
    @Nullable
    float[] spin_city_title;

    CallbackSelected select_city = new CallbackSelected() {
        @Override
        public void selected(int index) {
            if (null == region_selected) {
                spin_city_title = null;
            } else if (-1 == index) {
                spin_city_title = region_selected.titles.get(0);
            } else {
                spin_city_title = region_selected.titles.get(index);
            }

            cost();
        }
    };

    void swRegion() {
        if (null == spin_city_title) {
            Kt = -1.0f;
            return;
        }
        Kt = type_traktor ? spin_city_title[1] : spin_city_title[0];
    }

    //______________________________________________________________________________________________
    @Bind(R.id.item_navigation_text_count)
    TextView item_navigator_text_count;

    @Bind(R.id.item_navigation_remove)
    ImageButton navigator_remove;

    int navigators = 1;
    boolean without_limitation = false;

    @OnClick(R.id.item_navigation_add)
    void navigator_add(View view) {
        navigators += 1;
        change_navigators();
    }

    private void change_navigators() {
        without_limitation = navigators < 1;
        if (without_limitation) {
            item_driverAgeStage.setEnabled(false);

            navigator_remove.setVisibility(View.GONE);
            item_navigator_text_count.setText("Без ограничений");

        } else {
            item_driverAgeStage.setEnabled(true && possessor == 1);

            navigator_remove.setVisibility(View.VISIBLE);
            item_navigator_text_count.setText(String.valueOf(navigators));
        }
    }

    @OnClick(R.id.item_navigation_remove)
    void navigator_remove(View view) {
        navigators -= 1;
        change_navigators();
    }

    //________________ _driver_age
    Item item_driverAgeStage;
    float[] item_driverAgeStage_title;

    CallbackSelected select_driverAgeStage = new CallbackSelected() {
        @Override
        public void selected(int index) {
            if (-1 == index) {
                if (item_driverAgeStage.isEnabled()) {
                    index = item_driverAgeStage.getSelectedItem();
                } else {
                    index = item_driverAgeStage.getLastItem();
                }
            }
            Kvs_Ko = item_driverAgeStage_title[index];
            cost();
        }
    };
    //________________ _first
    @Bind(R.id.switch_firstInsurance)
    Switch switch_first;

    @OnCheckedChanged(R.id.switch_firstInsurance)
    public void switch_first_changed(CompoundButton buttonView, boolean isChecked) {
        if (isChecked) {
            item_discount.setVisibility(false);
        } else {
            item_discount.setVisibility(true);
        }
    }

    //________________ _discount
    Item item_discount;
    float[] item_discount_title;

    CallbackSelected select_discount = new CallbackSelected() {
        @Override
        public void selected(int index) {
            if (index < 0 && !item_discount.isVisibility()) {
                Kbm = 1.f;
            } else {
                Kbm = item_discount_title[index];
            }
            cost();
        }
    };

    //__________________________
    @Bind(R.id.spin_insurance)
    Spinner spin_insurance;

    @Bind(R.id.item_amount_value)
    TextView item_amount_value;

    int order_loaded_id = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_osago);
        ButterKnife.bind(this);

        item_possessor = new Item(R.id.item_spin_possessor, R.id.spin_possessor, select_possessor,
                R.array.array_spin_possessor);
        //#
        sub_type_b = new Types(R.array.array_spin_type_b, R.array.array_spin_type_b_title);
        sub_type_c = new Types(R.array.array_spin_type_c, R.array.array_spin_type_c_title);
        sub_type_d = new Types(R.array.array_spin_type_d, R.array.array_spin_type_d_title);
        sub_type_def = new Types(R.array.array_spin_type_def);

        item_type = new Item(R.id.item_spin_type, R.id.spin_type, select_type,
                R.array.array_spin_type);
        item_sub_type = new Item(R.id.item_spin_sub_type, R.id.spin_sub_type, select_sub_type,
                sub_type_b);
        //#
        item_power_title = Helpers.getFloatArray(this, R.array.array_spin_power_title);
        item_power = new Item(R.id.item_spin_power, R.id.spin_power, select_power,
                R.array.array_spin_power);
        //#
        item_period_ispolzovania_title = Helpers.getFloatArray(this, R.array.array_spin_period_ispolzovania_title);
        item_period_ispolzovania = new Item(R.id.item_spin_period_ispolzovania, R.id.spin_period_ispolzovania, select_period_ispolzovania,
                R.array.array_spin_period_ispolzovania
        );
        //#
        item_region_title = getResources().getIntArray(R.array.array_spin_region_value);
        read_regions();
        item_region = new Item(R.id.item_spin_region, R.id.spin_region, select_region,
                R.array.array_spin_region);
        //_
        item_city = new Item(R.id.item_spin_city, R.id.spin_city, select_city);
        //#
        item_driverAgeStage_title = Helpers.getFloatArray(this, R.array.array_spin_driverAgeStage_title);
        item_driverAgeStage = new Item(R.id.item_spin_driverAgeStage, R.id.spin_driverAgeStage, select_driverAgeStage,
                R.array.array_spin_driverAgeStage);
        change_navigators();
        //#
        item_discount_title = Helpers.getFloatArray(this, R.array.array_spin_discount_val);
        item_discount = new Item(R.id.item_spin_discount, R.id.spin_discount, select_discount,
                R.array.array_spin_discount);
        //-
        spin_insurance.setEnabled(false);
        switch_first_changed(null, switch_first.isChecked());


        order_loaded_id = getIntent().getIntExtra(Data.key_oreder_id, -1);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        readData();
    }

    boolean not_needed_save = false;

    @Override
    protected void onStop() {
        super.onStop();
        if (not_needed_save) return;
        saveData();
    }


    //warning save data
    private void readData() {
        String str = Application.getData().getOsagoDat(order_loaded_id);

        if (str.length() <= 0) return;

        String[] str_i = str.split("\n");

        switch_first.setChecked(Boolean.parseBoolean(str_i[0]));
        //switch_first_changed(null, Boolean.parseBoolean(str_i[0]));

        navigators = Integer.parseInt(str_i[1]);
        change_navigators();

        item_possessor.selected(Integer.parseInt(str_i[2]));
        item_type.selected(Integer.parseInt(str_i[3]));
        item_sub_type.selected(Integer.parseInt(str_i[4]));
        item_power.selected(Integer.parseInt(str_i[5]));
        item_period_ispolzovania.selected(Integer.parseInt(str_i[6]));
        item_region.selected(Integer.parseInt(str_i[7]));
        item_city.selected(Integer.parseInt(str_i[8]));
        item_driverAgeStage.selected(Integer.parseInt(str_i[9]));
        item_discount.selected(Integer.parseInt(str_i[10]));
    }

    private void saveData() {
        String save_dat = "" + switch_first.isChecked() + "\n" +
                //switch_first_changed(null, switch_first.isChecked());
                "" + navigators + "\n" +
                //change_navigators();
                "" + item_possessor.getPos() + "\n" +
                "" + item_type.getPos() + "\n" +
                "" + item_sub_type.getPos() + "\n" +
                "" + item_power.getPos() + "\n" +
                "" + item_period_ispolzovania.getPos() + "\n" +
                "" + item_region.getPos() + "\n" +
                "" + item_city.getPos() + "\n" +
                "" + item_driverAgeStage.getPos() + "\n" +
                "" + item_discount.getPos();


        String type_avto = item_type.mSpin.getSelectedItem().toString() + " " + item_sub_type.mSpin.getSelectedItem().toString();
        order_loaded_id = Application.getData().saveOsagoDat(order_loaded_id, navigators, type_avto, save_dat);
    }

    float Tb = 0; // Базовая ставка // тип ТС
    float Kt = 0; // коэф региона
    float Kvs_Ko = 1.7f; // коэф стажа
    float Km = 0; // коэф мощности
    float Kc = 0; // коэф периода использования
    float Kp = 0; // коэф срока страхования
    float Kbm = 1; // коэф за безаварийную езду

    void stag() {
        if (2 == possessor || without_limitation) {
            Kvs_Ko = 1.8f;
        } else {
            //in selected callback
            //Kvs_Ko = $("select[name='driverAgeStage'] option:selected").attr('title');
        }
    }

    boolean one_step = true;

    void cost() {
        if (!one_step) return;
        one_step = false;
        swRegion();
        stag();

        Kp = 1;
        if (2 == possessor)
            Kvs_Ko = 1.7f;
        if (2 == possessor)
            Kc = 1;
        if (sub_type_selected != sub_type_b)
            Km = 1;

        float sum = (Tb * Kt * Kvs_Ko * Km * Kc * Kp * Kbm * 100.f) / 100.f;

        /*
        Log(
                "Базовая ставка (Tb):=" + Tb + "\n" +
                        "коэф региона (Kt):=" + Kt + "\n" +
                        "коэф стажа (Kvs_Ko):=" + Kvs_Ko + "\n" +
                        "коэф мощности (Km):=" + Km + "\n" +
                        "коэф периода использования (Kc):=" + Kc + "\n" +
                        "коэф срока страхования (Kp):=" + Kp + "\n" +
                        "коэф за безаварийную езду (Kbm):=" + Kbm + "\n" +
                        "Стоимость :=" + sum + "\n" +
                        "######################################################\n"
        );
        */
        one_step = true;
        item_amount_value.setText(" " + sum + " Р");
    }


    @OnClick(R.id.action_next)
    void action_next(View view) {
        not_needed_save = true;
        saveData();

        Intent intent = new Intent(this, PersonalDataActivity.class);
        intent.putExtra(Data.key_oreder_id, order_loaded_id);
        startActivity(intent);
        overridePendingTransition(R.anim.right_in, R.anim.left_out);
    }
    //________________________________________


    @Override
    protected int getSelfNavDrawerItem() {
        return R.id.nav_osago;
    }

//##############################################################################################

    private class Region {
        public ArrayAdapter<?> arrayAdapter;

        public int region_id;

        public Region(int id) {
            region_id = id;
        }


        public ArrayList<float[]> titles = new ArrayList<>();
        ArrayList<String> items = new ArrayList<>();

        public void add_item(String line) {

            String[] new_item = line.split(Pattern.quote("|"));

            titles.add(getTitle(new_item[0]));
            if (new_item.length > 1) {
                items.add(new_item[1]);
            }
        }

        private float[] getTitle(String str) {
            String[] tmp = str.split("_");
            float[] title = new float[tmp.length];
            int i = 0;
            for (String it : tmp) {
                title[i] = Float.parseFloat(it);
                i++;
            }
            return title;
        }

        public boolean isEmpty() {
            return null == arrayAdapter;
        }

        public void end() {
            if (items.size() == 0) return;
            //arrayAdapter = createAdapter(items);
            arrayAdapter = createAdapter(items);
        }
    }

    private class Types {
        public ArrayAdapter<String> types;
        public float[] value;

        public Types(int array_type_id, int array_value_id) {
            this.types = (ArrayAdapter<String>) createAdapter(array_type_id);
            this.value = Helpers.getFloatArray(OsagoActivity.this, array_value_id);
        }

        public Types(int array_value_id) {
            this.value = Helpers.getFloatArray(OsagoActivity.this, array_value_id);
        }
    }

    //##############################################################################################

    private void read_regions() {
        ArrayList<Region> temp = readFile();
        regions = new HashMap<>();
        for (int id : item_region_title) {
            if (regions.containsKey(id)) continue;
            Region cr = null;
            for (Region region : temp) {
                if (region.region_id == id) {
                    cr = region;
                    break;
                }
            }
            if (null == cr) {
                new Exception();
            }

            regions.put(id, cr);
        }
    }

    private ArrayList<Region> readFile() {
        ArrayList<Region> ret = new ArrayList<>();
        String file = Helpers.ReadFromfile(this, "array_spin_city");

        String[] lines = file.split("\n");

        Region region = null;
        for (String line : lines) {
            if (line.contains("array_spin_city_")) {
                if (null != region) {
                    region.end();
                    ret.add(region);
                }

                int id = Integer.parseInt(line.replace("array_spin_city_", ""));
                region = new Region(id);
                continue;
            }
            region.add_item(line);
        }
        return ret;
    }

    private ArrayAdapter<?> createAdapter(int array_res_id) {
        return createAdapter(Arrays.asList(getResources().getStringArray(array_res_id)));
    }

    private ArrayAdapter<?> createAdapter(List<?> array) {
        ArrayAdapter<?> ret = new ArrayAdapter<>(this, spinner_item, array);
        ret.setDropDownViewResource(spinner_dropdown_item);
        return ret;
    }

    private ArrayAdapter<?> createAdapter() {
        ArrayAdapter<?> ret = new ArrayAdapter<>(this, spinner_item);
        ret.setDropDownViewResource(spinner_dropdown_item);
        return ret;
    }

    interface CallbackSelected {
        void selected(int index);
    }

    public class Item implements View.OnTouchListener {


        public ViewGroup mItem;
        public Spinner mSpin;
        CallbackSelected mCallback;


        public Item(int layout_id_res, int spin_id_res, final CallbackSelected callback) {
            init(layout_id_res, spin_id_res, callback);

            mSpin.setAdapter(createAdapter());
        }

        public Item(int layout_id_res, int spin_id_res, final CallbackSelected callback
                , int array_res_id) {
            init(layout_id_res, spin_id_res, callback);

            mSpin.setAdapter(createAdapter(array_res_id));
        }

        public Item(int layout_id_res, int spin_id_res, final CallbackSelected callback
                , Types types) {
            init(layout_id_res, spin_id_res, callback);

            mSpin.setAdapter(types.types);
        }

        private void init(int layout_id_res, int spin_id_res, final CallbackSelected callback) {
            mItem = (ViewGroup) findViewById(layout_id_res);
            mItem.setOnTouchListener(this);

            mCallback = callback;
            mSpin = (Spinner) findViewById(spin_id_res);

            mSpin.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    mCallback.selected((int) id);
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });
        }

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            //mSpin.performClick();
            return false;
        }

        public void setAdapter(ArrayAdapter<?> types) {
            mSpin.setAdapter(types);
            mItem.setVisibility(View.VISIBLE);
        }

        public void setVisibility(boolean visible) {
            if (visible) {
                mItem.setVisibility(View.VISIBLE);
                mCallback.selected(mSpin.getSelectedItemPosition());
            } else {
                mItem.setVisibility(View.GONE);
                mCallback.selected(-1);
            }
        }

        public boolean isVisibility() {
            return mItem.getVisibility() == View.VISIBLE;
        }


        public void setEnabled(boolean enabled) {
            mSpin.setEnabled(enabled);
            mCallback.selected(-1);

            //if (enabled) {
            //    mSpin.setSelection(mSpin.getSelectedItemPosition());
            //}
        }

        public boolean isEnabled() {
            return mSpin.isEnabled();
        }

        public int getSelectedItem() {
            return mSpin.getSelectedItemPosition();
        }

        public int getLastItem() {
            return mSpin.getCount() - 1;
        }

        public int getPos() {
            return mSpin.getSelectedItemPosition();
        }

        public void selected(int index) {
            mSpin.setSelection(index);
        }
    }
}
