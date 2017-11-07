package net.hongqianfly.myview;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by HongQian.Wang on 2017/11/7.
 */

public class PlaceHolderView extends LinearLayout implements TriggerView {
    private Context mContext;
    private ImageView im_empty;
    private ProgressBar loading;
    private TextView txt_empty;

    private int[] mTextIds = new int[]{0, 0, 0};
    private int[] mDrawableIds =new int[]{0, 0};

    private View[] mBindViews;

    public PlaceHolderView(Context context) {
        this(context, null);
    }

    public PlaceHolderView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.mContext = context;
        init(attrs);
    }

    private void init(AttributeSet attrs) {
        inflate(mContext, R.layout.view_placeholder, this);
        im_empty = findViewById(R.id.im_empty);
        loading = findViewById(R.id.loading);
        txt_empty = findViewById(R.id.txt_empty);
        TypedArray typedArray = mContext.obtainStyledAttributes(attrs, R.styleable.PlaceHolderView);
        mTextIds[0] = typedArray.getInt(R.styleable.PlaceHolderView_emptyText, R.string.prompt_empty);
        mTextIds[1] = typedArray.getInt(R.styleable.PlaceHolderView_errorText, R.string.prompt_error);
        mDrawableIds[0] = typedArray.getInt(R.styleable.PlaceHolderView_emptyDrawable, R.drawable.status_empty);
        mDrawableIds[1] = typedArray.getInt(R.styleable.PlaceHolderView_errowDrawable, R.drawable.status_empty);
        mTextIds[2] = typedArray.getInt(R.styleable.PlaceHolderView_loadingText, R.string.prompt_loading);
        typedArray.recycle();
    }


    public void bind(View...views){
        this.mBindViews=views;
    }


    private void changeBindViewVisibility(int visible){
        if(mBindViews==null||mBindViews.length==0) {
            return;
        }
        for (View view:mBindViews){
            view.setVisibility(visible);
        }
    }



    @Override
    public void triggerEmpty() {
           loading.setVisibility(GONE);
           im_empty.setImageResource(mDrawableIds[0]);
           txt_empty.setText(mTextIds[0]);
           im_empty.setVisibility(VISIBLE);
           setVisibility(VISIBLE);
           changeBindViewVisibility(GONE);
    }

    @Override
    public void triggerNetError() {
        loading.setVisibility(GONE);
        im_empty.setImageResource(mDrawableIds[1]);
        im_empty.setVisibility(VISIBLE);
        txt_empty.setText(mTextIds[1]);
        setVisibility(VISIBLE);
        changeBindViewVisibility(GONE);
    }

    @Override
    public void triggerError(int strRes) {
        Toast.makeText(getContext(), strRes, Toast.LENGTH_SHORT).show();
            setVisibility(VISIBLE);
            changeBindViewVisibility(GONE);
    }

    @Override
    public void triggerLoading() {
                  im_empty.setVisibility(GONE);
                  txt_empty.setText(mTextIds[2]);
                  loading.setVisibility(VISIBLE);
                  setVisibility(VISIBLE);
                  changeBindViewVisibility(GONE);
    }

    @Override
    public void triggerOk() {
        setVisibility(GONE);
        changeBindViewVisibility(VISIBLE);

    }

    @Override
    public void triggerOkOrEmpty(boolean isOk) {
             if(isOk)
                 triggerOk();
             else
                 triggerEmpty();

    }
}
