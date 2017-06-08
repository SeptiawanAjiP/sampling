/*
 * Decompiled with CFR 0_118.
 * 
 * Could not load the following classes:
 *  android.content.Context
 *  android.content.res.Resources
 *  android.util.AttributeSet
 *  android.util.DisplayMetrics
 *  android.util.TypedValue
 *  android.view.View
 *  android.view.ViewGroup
 *  android.view.ViewGroup$LayoutParams
 *  android.widget.LinearLayout
 *  android.widget.LinearLayout$LayoutParams
 *  android.widget.ProgressBar
 *  android.widget.RelativeLayout
 *  android.widget.RelativeLayout$LayoutParams
 *  android.widget.TextView
 */
package org.odk.collect.android.augmentedreality.arkit;

import android.content.Context;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class PARProgressBar
extends ProgressBar {
    private TextView textView;
    private RelativeLayout mainLayout;

    public void setVisibility(int v) {
        super.setVisibility(v);
        if (this.mainLayout != null) {
            this.mainLayout.setVisibility(v);
        }
    }

    public PARProgressBar(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.setIndeterminate(true);
        this.setVisibility(VISIBLE);
        this.textView = new TextView(context);
        this.textView.setText((CharSequence)"");
        this.textView.setTextAppearance(context, 16974257);
        this.textView.setTextColor(-1);
        LinearLayout linearLayout = new LinearLayout(context);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(-2, -2);
        linearLayout.setOrientation(1);
        int pad = (int)TypedValue.applyDimension((int)1, (float)10.0f, (DisplayMetrics)this.getResources().getDisplayMetrics());
        linearLayout.setPadding(pad, pad, pad, pad);
        linearLayout.setBackgroundColor(-2013265920);
        linearLayout.setLayoutParams((ViewGroup.LayoutParams)lp);
        lp.gravity = 17;
        this.setLayoutParams((ViewGroup.LayoutParams)lp);
        linearLayout.addView((View)this.textView, (ViewGroup.LayoutParams)lp);
        linearLayout.addView((View)this, (ViewGroup.LayoutParams)lp);
        this.mainLayout = new RelativeLayout(context);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(-2, -2);
        params.addRule(13);
        this.mainLayout.setLayoutParams((ViewGroup.LayoutParams)params);
        this.mainLayout.addView((View)linearLayout);
    }

    public void setText(String text) {
        this.getTextView().setText((CharSequence)text);
    }

    public void showWithText(String text) {
        this.setText(text);
        this.mainLayout.setVisibility(0);
    }

    public void hide() {
        this.mainLayout.setVisibility(8);
    }

    public RelativeLayout getMainLayout() {
        return this.mainLayout;
    }

    public TextView getTextView() {
        return this.textView;
    }
}

