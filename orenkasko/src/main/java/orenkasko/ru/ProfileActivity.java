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
import orenkasko.ru.ui.base.ImageLoader;


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
                .input(null, Application.getData().getName(), new MaterialDialog.InputCallback() {
                    @Override
                    public void onInput(MaterialDialog dialog, CharSequence input) {
                        Application.getData().saveName(input.toString());
                        text_name.setText(input);
                    }
                }).show();
    }

    @OnClick(R.id.change_emai_layout)
    void change_email(View view) {
        new MaterialDialog.Builder(this)
                .title("Введите email")
                .inputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS)
                .inputRangeRes(4, 30, R.color.text_err_red)
                .input(null, Application.getData().getEmail(), new MaterialDialog.InputCallback() {
                    @Override
                    public void onInput(MaterialDialog dialog, CharSequence input) {
                        Application.getData().saveEmail(input.toString());
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

    static final String empty_str = "__empty__";

    @Override
    protected void onDestroy() {
        super.onDestroy();

        String tmp = text_name.getText().toString();
        Application.getData().saveName(0 == tmp.compareTo(empty_str) ? "" : tmp);

        tmp = text_email.getText().toString();
        Application.getData().saveEmail(0 == tmp.compareTo(empty_str) ? "" : tmp);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        ButterKnife.bind(this);

        phone_text.setText(Application.getData().getPhone());
        balance_text.setText("1000 р.");

        String tmp = Application.getData().getName();
        text_name.setText(tmp.length() <= 0 ? empty_str : tmp);
        tmp = Application.getData().getEmail();
        text_email.setText(tmp.length() <= 0 ? empty_str : tmp);

        setLoaded(Application.getData().getProfileImage());
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        ImageLoader.OnRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        ImageLoader.SetActivity(this);
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
        Application.getData().SaveProfileImage(bitmap);
    }


}
