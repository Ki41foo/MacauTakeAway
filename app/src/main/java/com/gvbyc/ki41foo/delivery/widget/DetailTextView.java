package com.gvbyc.ki41foo.delivery.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TextView;

import com.gvbyc.ki41foo.delivery.AppManager;
import com.gvbyc.ki41foo.delivery.R;
import com.gvbyc.ki41foo.delivery.TextDialog;
import com.gvbyc.ki41foo.delivery.utils.IntentUtils;


/**
 * Created by goodview on 20/11/15.
 */
public class DetailTextView extends TextView {
    public DetailTextView(Context context) {
        super(context);
    }

    public DetailTextView(Context context, AttributeSet attrs) {
        super(context, attrs);

    }

    public DetailTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }


    @Override
    public void setText(final CharSequence text, BufferType type) {
        super.setText(text, type);


        this.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                new TextDialog(AppManager.getAppManager().currentActivity(), (String) text);
            }
        });
    }
}
