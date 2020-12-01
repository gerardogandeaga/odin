package com.group8.odin.examinee.list_items;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.group8.odin.OdinFirebase;
import com.group8.odin.R;
import com.group8.odin.common.models.ExamSession;
import com.group8.odin.common.models.UserProfile;
import com.mikepenz.fastadapter.FastAdapter;
import com.mikepenz.fastadapter.items.AbstractItem;

import java.util.Comparator;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/*
 * Created by: Gerardo Gandeaga
 * Created on: 2020-10-31
 * Description: Definition for view handling for examinee registered exams
 * Updated by: Raj Patel
 * Updated on: 2020-12-01
 * Description Added sorting of registered exams to loadExamSessions function
 */
public class RegisteredExamItem extends AbstractItem<RegisteredExamItem, RegisteredExamItem.ViewHolder> {
    // Data view item will actually hold
    private ExamSession examSession;

    public RegisteredExamItem setExamSession(ExamSession examSession) {
        this.examSession = examSession;
        return this;
    }

    public ExamSession getExamSession() {
        return examSession;
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
        return R.layout.exam_item;
    }

    // List item view
    protected static class ViewHolder extends FastAdapter.ViewHolder<RegisteredExamItem> {
        @BindView(R.id.tvExamTitle) TextView examTitle;
        @BindView(R.id.tvExamId)    TextView examId;
        @BindView(R.id.tvExamTime)  TextView examTime;
        @BindView(R.id.tvAuthTime)  TextView authTime;

        public ViewHolder(View view) {
            super(view);
            // Bind views to this target (the list item view we created)
            ButterKnife.bind(this, view);
        }

        @Override
        public void bindView(RegisteredExamItem item, List<Object> payloads) {
            // populate the item with content
            examTitle.setText(item.examSession.getTitle());
            if (OdinFirebase.UserProfileContext.getRole() == UserProfile.Role.EXAMINEE) {
                examId.setVisibility(View.GONE);
            }
            else {
                examId.setText("Exam ID: " + item.examSession.getExamId());
            }
            examTime.setText("Exam Begins on " + item.examSession.getExamStartTime().toString());
            authTime.setText("ID check at " + item.examSession.getAuthStartTime().toString());
        }

        @Override
        public void unbindView(RegisteredExamItem item) {
            // remove content from the item view
            examTitle.setText(null);
            examTime.setText(null);
            authTime.setText(null);
        }
    }

    // custom Registered Exam comparator
    public static class Comparison implements Comparator<RegisteredExamItem> {

        @Override
        public int compare(RegisteredExamItem a, RegisteredExamItem b) {
            int comp = a.getExamSession().getExamStartTime().compareTo(b.getExamSession().getExamStartTime());
            // if they have the same status value then compare by title
            if (comp == 0) {
                return a.getExamSession().getTitle().compareTo(b.getExamSession().getTitle());
            }
            // if they are different status values then compare by status
            else {
                return -(comp);
            }
        }
    }
}
