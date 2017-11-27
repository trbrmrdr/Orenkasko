package orenkasko.ru.ui.base;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.support.v4.app.FragmentActivity;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.mvc.imagepicker.ImagePicker;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import orenkasko.ru.PersonalDataActivity;
import orenkasko.ru.R;

/**
 * Created by trbrm on 23.11.2017.
 */

public class ImageLoader extends RelativeLayout {

    public static ArrayList<ImageLoader> _images;
    private static Activity _activity;

    public static void ClearImages() {
        if (null != _images)
            _images.clear();
    }

    String mText = "";
    boolean sLoaded = false;

    public ImageLoader(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    public ImageLoader(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public void init(Context context, AttributeSet attrs) {
        TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.ImageLoader, 0, 0);

        try {
            mText = a.getString(R.styleable.ImageLoader_text_image);
            sLoaded = a.getBoolean(R.styleable.ImageLoader_loaded, false);
        } finally {
            a.recycle();
        }

        inflate(context, R.layout.image_loader, this);
        if (null == _images) {
            _images = new ArrayList<>();
        }
        _images.add(this);
    }


    @Bind(R.id.il_camera_image)
    ImageView camera_image;
    @Bind(R.id.il_close_image)
    ImageView close_image;
    @Bind(R.id.il_back_image)
    ImageView back_image;
    @Bind(R.id.il_text)
    TextView text;

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        //findViewById()d
        ButterKnife.bind(this);

        text.setText(mText);
        setLoaded(sLoaded, null);

        ImagePicker.setMinQuality(600, 600);
    }

    public void setLoaded(boolean loaded, Bitmap bitmap) {
        sLoaded = loaded;
        if (sLoaded) {
            camera_image.setVisibility(View.GONE);
            text.setVisibility(View.GONE);
            close_image.setVisibility(View.VISIBLE);
        } else {
            camera_image.setVisibility(View.VISIBLE);
            text.setVisibility(View.VISIBLE);
            close_image.setVisibility(View.GONE);
        }

        back_image.setImageBitmap(bitmap);
    }


    @OnClick(R.id.il_close_image)
    void click_close(View view) {
        if (!sLoaded) return;
        setLoaded(false, null);
    }

    boolean this_need_image = false;

    @OnClick(R.id.il_camera_image)
    void click_camera(View view) {
        if (sLoaded) return;
        this_need_image = true;
        ImagePicker.pickImage(_activity, getResources().getString(R.string.chooser_image_title));
    }


    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (!this_need_image) return;
        this_need_image = false;

        Bitmap bitmap = ImagePicker.getImageFromResult(this.getContext(), requestCode, resultCode, data);

        setLoaded(null != bitmap, bitmap);
    }

    public boolean hasLoaded() {
        return sLoaded;
    }

    public static void OnActivityResult(int requestCode, int resultCode, Intent data) {
        for (ImageLoader image : _images) {
            image.onActivityResult(requestCode, resultCode, data);
        }
    }

    public static void SetActivity(Activity activity) {
        _activity = activity;
    }

    public static void ReadImages(ArrayList<Bitmap> bitmaps) {
        int i = -1;
        for (Bitmap bitmap : bitmaps) {
            i++;
            if (bitmap == null) continue;
            if (_images.size() < i) break;
            _images.get(i).setLoaded(true, bitmap);
        }
    }

    public ImageView getImageView() {
        return back_image;
    }
}
