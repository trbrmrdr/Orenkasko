package orenkasko.ru.ui.base;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import orenkasko.ru.R;

/**
 * Created by trbrm on 23.11.2017.
 */

public class ItemDocs extends LinearLayout {

    public static ArrayList<ItemDocs> _images;

    public ItemDocs(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    public ItemDocs(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public ItemDocs(Context context) {
        super(context);
        inflate(context, R.layout.item_docs, this);
    }

    String mText;
    boolean hasSeparator = false;

    public void init(Context context, AttributeSet attrs) {
        TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.ItemDocs, 0, 0);

        try {
            mText = a.getString(R.styleable.ItemDocs_text_item);
            hasSeparator = a.getBoolean(R.styleable.ItemDocs_separator, false);
        } finally {
            a.recycle();
        }


        inflate(context, R.layout.item_docs, this);
    }


    @Bind(R.id.first_image)
    ImageLoader first_image;
    @Bind(R.id.separator)
    View separator;

    @Bind(R.id.second_image)
    ImageLoader second_image;

    @Bind(R.id.item_docs_text)
    TextView text;

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        //findViewById()d
        ButterKnife.bind(this);

        text.setText(mText);
        if (hasSeparator)
            separator.setVisibility(View.VISIBLE);
        else
            separator.setVisibility(View.GONE);
    }

    public void setText(String str) {
        mText = str;
        text.setText(mText);
    }

    public boolean hasLoaded() {
        return first_image.hasLoaded() && second_image.hasLoaded();
    }
}
