package org.odk.collect.android.augmentedreality.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.GridView;

/**
 * Created by Septiawan Aji Pradan on 4/14/2017.
 */

public class ExpandGridView extends GridView {
    boolean expanded = false;

    public ExpandGridView(Context context){
        super(context);
    }

    public ExpandGridView(Context context, AttributeSet attrs){
        super(context,attrs);
    }

    public ExpandGridView(Context context, AttributeSet attrs, int defStyle){
        super(context,attrs,defStyle);
    }

    public boolean isExpanded(){
        return expanded;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if(isExpanded()){
            int expandSpec = MeasureSpec.makeMeasureSpec(
                    Integer.MAX_VALUE >> 2, MeasureSpec.AT_MOST);
            super.onMeasure(widthMeasureSpec, expandSpec);
            ViewGroup.LayoutParams params = getLayoutParams();
            params.height = getMeasuredHeight();
        }else{
            super.onMeasure(widthMeasureSpec,heightMeasureSpec);
        }
    }

    public void setExpanded(boolean expanded) {
        this.expanded = expanded;
    }
}
