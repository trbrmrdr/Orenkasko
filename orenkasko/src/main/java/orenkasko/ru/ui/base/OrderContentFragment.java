/*
 * Copyright (C) 2015 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package orenkasko.ru.ui.base;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import orenkasko.ru.Data;
import orenkasko.ru.PersonalDataActivity;
import orenkasko.ru.R;

/**
 * Provides UI for the view with Cards.
 */
@SuppressLint("ValidFragment")
public class OrderContentFragment extends Fragment {

    //0 черновик
    //1 в процессе
    //2 выполненные
    int mType = -1;
    ContentAdapter mAdapter;
    private Data.Order mOrder;

    private void buildData() {
        Data data = Data.getInstance();
        if (mType == 0 && data.mOrder.data_order_count > 0) {
            mOrder = data.mOrder;
            return;
        }
        if (mType == 1 && data.mOrder_Success.data_order_count > 0) {
            mOrder = data.mOrder_Success;
            return;
        }

        //if (mType == 2)
        if (true) {
            if (null == mOrder)
                mOrder = new Data.Order();
            else
                mOrder.clear();
            return;
        }

            /*
            Resources resources = context.getResources();
            m_ids = Helpers.getIntArray(context, R.array.tids);
            LENGTH = m_ids.length;
            m_tids = resources.getStringArray(R.array.tids);
            m_names = resources.getStringArray(R.array.names);
            m_avto_types = resources.getStringArray(R.array.avto_types);
            m_dates = resources.getStringArray(R.array.dates);
            */
    }

    @SuppressLint("ValidFragment")
    public OrderContentFragment(int type) {
        super();
        mType = type;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        RecyclerView recyclerView = (RecyclerView) inflater.inflate(R.layout.recycler_view, container, false);
        mAdapter = new ContentAdapter();
        recyclerView.setAdapter(mAdapter);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        return recyclerView;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {


        public int order_id = -1;
        public int position = -1;

        @Bind(R.id.card_title)
        public TextView title;
        @Bind(R.id.card_name_val)
        public TextView name_val;
        @Bind(R.id.card_avto_type_val)
        public TextView avto_type_val;
        @Bind(R.id.card_date_val)
        public TextView data_val;


        @Bind(R.id.card_name)
        public TextView name;
        @Bind(R.id.card_avto_type)
        public TextView avto_type;
        @Bind(R.id.card_date)
        public TextView data;

        @OnClick(R.id.erace_button)
        public void erace_click(View v) {
            //Snackbar.make(v, "erace this content " + order_id, Snackbar.LENGTH_LONG).show();

            new MaterialDialog.Builder(this.itemView.getContext())
                    //.title(R.string.title)
                    .content("Удалить запись")
                    .positiveText("Да")
                    .negativeText("нет")
                    .callback(new MaterialDialog.ButtonCallback() {
                        @Override
                        public void onPositive(MaterialDialog dialog) {
                            Data.rmOrder(order_id);
                            Data.getInstance().preparedata();
                            buildData();
                            mAdapter.notifyItemRemoved(position);
                        }
                    })
                    .show();

        }

        @OnClick(R.id.card_view)
        public void edit_click(View v) {
            //Snackbar.make(v, "edit this content " + order_id, Snackbar.LENGTH_LONG).show();


            final Context context = this.itemView.getContext();
            new MaterialDialog.Builder(context)
                    //.title("MaterialDialog")
                    .content("Редактировать запись")
                    .positiveText("OK")
                    .negativeText("CANCEL")
                    .callback(new MaterialDialog.ButtonCallback() {
                        @Override
                        public void onPositive(MaterialDialog dialog) {
                            Intent intent = new Intent(context, PersonalDataActivity.class);
                            intent.putExtra(Data.key_oreder_id, order_id);
                            startActivity(intent);
                        }
                    }).show();
        }


        public ViewHolder(LayoutInflater inflater, ViewGroup parent) {
            super(inflater.inflate(R.layout.item_order, parent, false));
            ButterKnife.bind(this, itemView);
            //avator = (ImageView) itemView.findViewById(R.id.list_avatar);

            name.setText("Клиент");
            avto_type.setText("Транспорт");
            data.setText("Дата");
        }
    }

    /**
     * Adapter to display recycler view.
     */
    public class ContentAdapter extends RecyclerView.Adapter<ViewHolder> {


        public ContentAdapter() {
            buildData();
        }

        @Override
        public void onViewRecycled(ViewHolder holder) {
            super.onViewRecycled(holder);
            //buildData();
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new ViewHolder(LayoutInflater.from(parent.getContext()), parent);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            holder.position = position;
            holder.order_id = mOrder.ids[position];
            holder.title.setText("ОСАГО # " + mOrder.tids[position]);
            holder.name_val.setText(mOrder.names[position]);
            holder.avto_type_val.setText(mOrder.avto_types[position]);
            holder.data_val.setText(mOrder.avto_time[position]);
        }

        @Override
        public int getItemCount() {
            return mOrder.data_order_count;
        }
    }
}
