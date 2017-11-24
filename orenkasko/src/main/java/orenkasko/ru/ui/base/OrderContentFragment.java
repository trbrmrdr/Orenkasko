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

import android.content.Context;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import orenkasko.ru.Data;
import orenkasko.ru.R;

/**
 * Provides UI for the view with Cards.
 */
public class OrderContentFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        RecyclerView recyclerView = (RecyclerView) inflater.inflate(R.layout.recycler_view, container, false);
        ContentAdapter adapter = new ContentAdapter(recyclerView.getContext());
        recyclerView.setAdapter(adapter);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        return recyclerView;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

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
            Snackbar.make(v, "erace this content", Snackbar.LENGTH_LONG).show();
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
    public static class ContentAdapter extends RecyclerView.Adapter<ViewHolder> {
        // Set numbers of Card in RecyclerView.
        private static final int LENGTH = 18;

        private final String[] m_ids;
        private final String[] m_names;
        private final String[] m_avto_types;
        private final String[] m_dates;


        public ContentAdapter(Context context) {

            if(Data.da)
            Resources resources = context.getResources();
            m_ids = resources.getStringArray(R.array.ids);
            m_names = resources.getStringArray(R.array.names);
            m_avto_types = resources.getStringArray(R.array.avto_types);
            m_dates = resources.getStringArray(R.array.dates);
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new ViewHolder(LayoutInflater.from(parent.getContext()), parent);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            int this_pos = position % m_ids.length;

            holder.title.setText("ОСАГО # " + m_ids[this_pos]);
            holder.name_val.setText(m_names[this_pos]);
            holder.avto_type_val.setText(m_avto_types[this_pos]);
            holder.data_val.setText(m_dates[this_pos]);
        }

        @Override
        public int getItemCount() {
            return LENGTH;
        }
    }
}
