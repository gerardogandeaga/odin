package com.group8.odin.examinee.list_items;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.net.Uri;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.google.firebase.storage.StorageReference;
import com.group8.odin.GlideApp;
import com.group8.odin.MyAppGlideModule;
import com.group8.odin.R;
import com.group8.odin.common.models.ExamSession;
import com.mikepenz.fastadapter.FastAdapter;
import com.mikepenz.fastadapter.items.AbstractItem;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/*
 * Created by: Gerardo Gandeaga
 * Created on: 2020-10-31
 * Description: Definition for view handling for examinee registered exams
 */
public class AuthPhotoItem extends AbstractItem<AuthPhotoItem, AuthPhotoItem.ViewHolder> {
    // Data view item will actually hold
    private StorageReference mReference;

    public AuthPhotoItem setPhotoReference(StorageReference ref) {
        mReference = ref;
        return this;
    }

    public StorageReference getPhotoReference() {
        return mReference;
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
        return R.layout.auth_photo_item;
    }

    // List item view
    protected static class ViewHolder extends FastAdapter.ViewHolder<AuthPhotoItem> {
        Context context;
        @BindView(R.id.imgAuthPhoto) ImageView authPhoto;

        public ViewHolder(View view) {
            super(view);
            // Bind views to this target (the list item view we created)
            ButterKnife.bind(this, view);
            context = view.getContext();
        }

        @Override
        public void bindView(AuthPhotoItem item, List<Object> payloads) {
            // populate the item with content
            GlideApp.with(context)
                    .load(item.getPhotoReference())
                    .into(authPhoto);

        }

        @Override
        public void unbindView(AuthPhotoItem item) {
            // remove content from the item view
        }
    }
}
