package co.infinum.showsapp.CustomViews;

import android.content.Context;
import android.support.design.widget.CollapsingToolbarLayout;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TextView;

import co.infinum.showsapp.R;

/**
 * Created by Ivan Lovrencic on 1.8.2018..
 */

public class ExpandableTextView extends android.support.v7.widget.AppCompatTextView implements View.OnClickListener {

    private static final int MAX_LINES = 3;
    private int currentMaxLines = Integer.MAX_VALUE;

    public ExpandableTextView(Context context)
    {
        super(context);
        setOnClickListener(this);
    }
    public ExpandableTextView(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);
        setOnClickListener(this);
    }

    public ExpandableTextView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        setOnClickListener(this);
    }

    @Override
    protected void onTextChanged(CharSequence text, int start, int lengthBefore, int lengthAfter)
    {
        post(new Runnable()
        {
            public void run()
            {
                if (getLineCount()>MAX_LINES)
                    setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, R.drawable.ic_more_horiz_black_24dp);
                else
                    setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);

                setMaxLines(MAX_LINES);
            }
        });
    }

    @Override
    public void setMaxLines(int maxLines)
    {
        currentMaxLines = maxLines;
        super.setMaxLines(maxLines);
    }

    public int getMyMaxLines()
    {
        return currentMaxLines;
    }

    @Override
    public void onClick(View v)
    {
        if (getMyMaxLines() == Integer.MAX_VALUE)
            setMaxLines(MAX_LINES);
        else
            setMaxLines(Integer.MAX_VALUE);
    }

}
