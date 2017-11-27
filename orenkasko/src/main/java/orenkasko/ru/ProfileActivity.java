package orenkasko.ru;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.text.InputType;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.mvc.imagepicker.ImagePicker;

import org.w3c.dom.Text;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;
import orenkasko.ru.Utils.Helpers;
import orenkasko.ru.ui.base.BaseActivity;


//dealogs
//https://github.com/afollestad/material-dialogs

public class ProfileActivity extends BaseActivity {

    @Bind(R.id.balance_text)
    TextView balance_text;

    @Bind(R.id.phone_text)
    TextView phone_text;

    @Bind(R.id.text_email)
    TextView text_email;
    @Bind(R.id.text_name)
    TextView text_name;

    @Bind(R.id.text_profile_photo)
    TextView text_profile_photo;

    @Bind(R.id.erace_photo)
    ImageButton erace_photo;

    @Bind(R.id.back_photo)
    CircleImageView back_photo;

    @Bind(R.id.change_photo)
    ImageButton change_photo;

    @OnClick(R.id.change_name_layout)
    void change_name(View view) {
        new MaterialDialog.Builder(this)
                .title("Введите имя")
                .inputRangeRes(2, 20, R.color.text_err_red)
                .input(null, null, new MaterialDialog.InputCallback() {
                    @Override
                    public void onInput(MaterialDialog dialog, CharSequence input) {
                        Data.saveName(input.toString());
                        text_name.setText(input);
                    }
                }).show();
    }


    void change_email(View view) {
        new MaterialDialog.Builder(this)
                .title("Введите email")
                .inputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS)
                //.inputRangeRes(2, 20, R.color.text_err_red)
                .input(null, null, new MaterialDialog.InputCallback() {
                    @Override
                    public void onInput(MaterialDialog dialog, CharSequence input) {
                        Data.saveEmail(input.toString());
                        text_email.setText(input);
                    }
                }).show();
    }

    @OnClick(R.id.change_photo)
    void change_photo_click(View view) {
        ImagePicker.pickImage(this, getResources().getString(R.string.chooser_image_title));
    }

    @OnClick(R.id.erace_photo)
    void erace_photo_click(View view) {
        setLoaded(null);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Data.saveName(text_name.getText().toString());
        Data.saveEmail(text_email.getText().toString());

        Data.SaveImage(Helpers.GetImage(back_photo));
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        ButterKnife.bind(this);

        phone_text.setText(Data.getPhone());
        balance_text.setText("1000 р.");

        text_name.setText(Data.getName());
        text_email.setText(Data.getEmail());

        setLoaded(Data.getProfileImage());
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        setLoaded(ImagePicker.getImageFromResult(this, requestCode, resultCode, data));
    }

    public void setLoaded(Bitmap bitmap) {
        boolean loaded = bitmap != null;
        if (loaded) {
            change_photo.setVisibility(View.GONE);
            text_profile_photo.setVisibility(View.GONE);
            erace_photo.setVisibility(View.VISIBLE);
        } else {
            change_photo.setVisibility(View.VISIBLE);
            text_profile_photo.setVisibility(View.VISIBLE);
            erace_photo.setVisibility(View.GONE);
        }

        back_photo.setImageBitmap(bitmap);
        setProfileImage(bitmap);
    }


}
