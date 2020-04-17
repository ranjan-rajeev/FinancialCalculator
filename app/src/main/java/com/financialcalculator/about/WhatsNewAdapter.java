package com.financialcalculator.about;

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.financialcalculator.R;

import java.util.List;


/**
 * Created by Rajeev Ranjan on 11/01/2018.
 */

public class WhatsNewAdapter extends RecyclerView.Adapter<WhatsNewAdapter.WhatsNewItem> implements View.OnClickListener {
    Context context;
    List<AboutUsEntity> whatsNewEntities;


    public WhatsNewAdapter(Context context, List<AboutUsEntity> list) {
        this.context = context;
        whatsNewEntities = list;
    }

    @Override

    public WhatsNewItem onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.layout_whats_new_item, parent, false);
        return new WhatsNewAdapter.WhatsNewItem(itemView);


    }

    @Override
    public void onBindViewHolder(WhatsNewItem holder, int position) {

        if (holder instanceof WhatsNewItem) {
            final AboutUsEntity entity = whatsNewEntities.get(position);
            holder.tvTitle.setText("" + entity.getTitle());
            holder.tvDesc1.setText("" + entity.getDesc1());
            holder.tvDesc2.setText("" + entity.getDesc2());
            holder.tvDesc3.setText("" + entity.getDesc3());
        }
    }


    @Override
    public int getItemCount() {
        return whatsNewEntities.size();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
        }
    }

    public class WhatsNewItem extends RecyclerView.ViewHolder {

        TextView tvTitle, tvDesc1, tvDesc2, tvDesc3;

        public WhatsNewItem(View itemView) {
            super(itemView);
            tvTitle = (TextView) itemView.findViewById(R.id.tvTitle);
            tvDesc1 = (TextView) itemView.findViewById(R.id.tvDesc1);
            tvDesc2 = (TextView) itemView.findViewById(R.id.tvDesc2);
            tvDesc3 = (TextView) itemView.findViewById(R.id.tvDesc3);
        }
    }

}