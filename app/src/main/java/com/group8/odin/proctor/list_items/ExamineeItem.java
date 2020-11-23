package com.group8.odin.proctor.list_items;

import android.content.Context;
import android.util.Pair;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

import com.group8.odin.OdinFirebase;
import com.group8.odin.R;
import com.group8.odin.Utils;
import com.group8.odin.common.models.ActivityLog;
import com.group8.odin.common.models.UserProfile;
import com.mikepenz.fastadapter.FastAdapter;
import com.mikepenz.fastadapter.items.AbstractItem;

import java.util.List;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;

/*
 * Created by: Gerardo Gandeaga
 * Created on: 2020-10-31
 * Description: Definition for view handling for examinee registered exams
 */
public class ExamineeItem extends AbstractItem<ExamineeItem, ExamineeItem.ViewHolder> {
    // Data view item will actually hold
    private Pair<UserProfile, ActivityLog> examinee;
    private String header;

    public ExamineeItem setExaminee(Pair<UserProfile, ActivityLog> examinee) {
        this.examinee = examinee;
        this.header = examinee.second.getStatus() ? "Active" : "Inactive";
        return this;
    }

    public Pair<UserProfile, ActivityLog> getExaminee() {
        return examinee;
    }

    @NonNull
    @Override
    public ViewHolder getViewHolder(View v) {
        return new ViewHolder(v);
    }

    @Override
    public int getType() {
        return R.id.fastadapter_item;
    }

    @Override
    public int getLayoutRes() {
        return R.layout.examinee_item;
    }

    // header properties
    public ExamineeItem setHeader(String header) {
        this.header = header;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof ExamineeItem)) return false;
        return ((ExamineeItem) o).examinee.first.getUserId().equals(examinee.first.getUserId());
    }

    // List item view
    protected static class ViewHolder extends FastAdapter.ViewHolder<ExamineeItem> {
        private Context context;
        @BindView(R.id.tvName)    TextView name;
        @BindView(R.id.imgStatus) ImageView status;

        public ViewHolder(View view) {
            super(view);
            // Bind views to this target (the list item view we created)
            ButterKnife.bind(this, view);
            context = view.getContext();
        }

        @Override
        public void bindView(ExamineeItem item, List<Object> payloads) {
            // populate the item with content
            name.setText(item.examinee.first.getName());
            // set colour filter
            status.setColorFilter(Utils.getExamineeStatusColour(context, item.examinee.second.getStatus()));
        }

        @Override
        public void unbindView(ExamineeItem item) {
            // remove content from the item view
            name.setText(null);
            status.setColorFilter(ContextCompat.getColor(context, R.color.black));
        }
    }
}