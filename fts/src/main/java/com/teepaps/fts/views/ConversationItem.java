package com.teepaps.fts.views;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;

/**
 * Created by ted on 4/11/14.
 */
public class ConversationItem extends LinearLayout {

    /**
     * Context
     */
    private Context context;

    public ConversationItem(Context context) {
        super(context);
    }

    public ConversationItem(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
    }


}
