package orenkasko.ru.Utils;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import orenkasko.ru.R;
import orenkasko.ru.ui.base.ImageLoader;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

/**
 * Created by trbrm on 13.11.2017.
 */

public class Helpers {
    private final static String TAG = "Helpers";

    private final static void Log(final String msg) {
        Log.e(TAG, msg);
    }


    public static void Toast(final Context context, final String msg) {
        Toast.makeText(context, msg, Toast.LENGTH_LONG).show();
    }

    public static void SetAutoInputText(final Context context, final EditText editText, long delay) {
        SetAutoInputText(context, editText, delay, null, null);
    }

    public static void SetAutoInputText(final Context context, final EditText editText, long delay, CharSequence text, final Runnable callback) {

        editText.setFocusable(false);
        editText.setText("");

        if (null == text || text.length() == 0) {
            new Handler(context.getMainLooper()).postDelayed(new Runnable() {
                @Override
                public void run() {
                    editText.setFocusable(true);
                    editText.setFocusableInTouchMode(true);

                }
            }, delay);
            return;
        }

        final int lenght = text.length();
        long delay_one_sumbol = lenght * 100;
        long delay_start = delay - delay_one_sumbol;
        if (delay_start <= 0) {
            delay_start = delay;
            delay_one_sumbol = 0;
        } else {
            delay_one_sumbol = 50;
        }
        for (int i = 0; i < lenght; ++i) {

            final CharSequence new_text = text.subSequence(0, i + 1);
            final boolean hasLast = i == lenght - 1;
            new Handler(context.getMainLooper()).postDelayed(new Runnable() {
                @Override
                public void run() {
                    editText.setText(new_text);

                    if (hasLast) {

                        editText.setSelection(lenght);
                        editText.setFocusable(true);
                        editText.setFocusableInTouchMode(true);
                        if (null != callback)
                            callback.run();

                    }
                }
            }, delay_start + delay_one_sumbol * i);
        }
    }

    public static void StartClean(Context context, Class<?> clazz) {
        Intent intent = new Intent(context, clazz);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                | Intent.FLAG_ACTIVITY_CLEAR_TASK
                | Intent.FLAG_ACTIVITY_NO_HISTORY);
        context.startActivity(intent);
    }

    public static void StartClean(Context context, Intent intent) {
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                | Intent.FLAG_ACTIVITY_CLEAR_TASK
                | Intent.FLAG_ACTIVITY_NO_HISTORY);
        context.startActivity(intent);
    }

    public static class TextWatcher_Phone implements TextWatcher {

        private Context mContext;
        private EditText editText;

        public TextWatcher_Phone(final Context context, EditText phoneText) {
            mContext = context;
            editText = phoneText;
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        String mLastText = "";
        int mSelectPos = -1;
        boolean mHasAddToLast = false;
        boolean mTextErace = false;

        String erace(String str, int pos) {
            return str.substring(0, pos) + str.substring(pos + 1, str.length());
        }

        String insert(String str, int pos, char ch) {
            return str.substring(0, pos) + ch + str.substring(pos, str.length());
        }

        String getNumber(String str) {
            char _ch[] = {'(', ')', '-', '+'};
            String ret = str;
            for (char ch : _ch) {
                ret = ret.replace(ch + "", "");
            }
            return ret;
        }

        String setFromFormat(String phone) {
            String newText = phone.toString();
            int lenght = newText.length();

            if (lenght > 0 && newText.charAt(0) != '+') {
                newText = "+" + newText;
                lenght = newText.length();
            }

            if (lenght > 2 && newText.charAt(2) != '(') {
                newText = insert(newText, 2, '(');
                lenght = newText.length();
            }
            if (lenght > 6 && newText.charAt(6) != ')') {
                newText = insert(newText, 6, ')');
                lenght = newText.length();
            }
            //7 11 14
            int t_pos[] = {7, 11, 14};
            for (int _pos : t_pos) {
                if (lenght > _pos && newText.charAt(_pos) != '-') {
                    newText = insert(newText, _pos, '-');
                    lenght = newText.length();
                }
            }

            //__________

            mHasAddToLast = false;
            if (lenght == 0) {
                newText = "+";
                mHasAddToLast = true;
            } else if (lenght == 2) {
                newText += "(";
                mHasAddToLast = true;
            } else if (lenght == 6) {
                newText += ")";
                lenght += 1;
                mHasAddToLast = true;
            }
            if (lenght == 7 || lenght == 11 || lenght == 14) {
                newText += "-";
                mHasAddToLast = true;
            }

            return newText;
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            //if (s.length() <= 3 && mLastText.length() < s.length()) return;
            String newText = s.toString();
            if (newText.length() < 3) {
                newText = mContext.getString(R.string.phone_start_text);
            }

            if (mLastText.length() == 0 || !newText.equals(mLastText))
                mSelectPos = editText.getSelectionStart();

            String phoneString = getNumber(newText);
            newText = setFromFormat(phoneString);


            if (s.length() < mLastText.length()) {
                mTextErace = true;
                if (newText.equals(mLastText)) {
                    Log(mSelectPos + "");

                    if (mSelectPos != 0 || mSelectPos != 2) {
                        int posErace = phoneString.length() - 1;
                        if (mSelectPos == 6 || mSelectPos == 7) {
                            posErace = 3;
                            mSelectPos = 5;
                        } else if (mSelectPos == 11) {
                            posErace = 6;
                            mSelectPos = 10;
                        } else if (mSelectPos == 14) {
                            posErace = 8;
                            mSelectPos = 13;
                        }
                        if (phoneString.length() > 1) {
                            phoneString = erace(phoneString, posErace);
                        } else {
                            phoneString = "";
                        }

                        newText = setFromFormat(phoneString);
                    } else {
                        mTextErace = false;
                    }
                }
            }


            //+7(981)-699-18-77
            //Log(mLastText + " " + newText);
            if (null == mLastText || !newText.equals(mLastText)) {
                mLastText = newText;
                editText.setText(newText);
                if (!mHasAddToLast) {
                    if (mSelectPos == -1 || mSelectPos > newText.length())
                        mSelectPos = newText.length();
                    editText.setSelection(mSelectPos);
                }
            }
            if (mHasAddToLast) {
                if (mTextErace && newText.length() > mSelectPos) {
                    mSelectPos = mSelectPos;
                    mTextErace = false;
                } else if (newText.length() >= 3)
                    mSelectPos = newText.length();
                editText.setSelection(mSelectPos);
                mLastText = newText;
            }
            return;
        }

        @Override
        public void afterTextChanged(Editable s) {

        }

    }

    public static String ReadFromfile(Context context, String fileName) {
        StringBuilder returnString = new StringBuilder();
        InputStream fIn = null;
        InputStreamReader isr = null;
        BufferedReader input = null;
        try {
            fIn = context.getResources().getAssets().open(fileName, Context.MODE_WORLD_READABLE);
            isr = new InputStreamReader(fIn);
            input = new BufferedReader(isr);
            String line = "";
            while ((line = input.readLine()) != null) {
                returnString.append(line + "\n");
            }
        } catch (Exception e) {
            e.getMessage();
        } finally {
            try {
                if (isr != null)
                    isr.close();
                if (fIn != null)
                    fIn.close();
                if (input != null)
                    input.close();
            } catch (Exception e2) {
                e2.getMessage();
            }
        }
        return returnString.toString();
    }

    //##############################################################################################

    public static Integer[] getIntArray(Context context, int array_strings_id) {
        return getIntArray(context.getResources().getStringArray(array_strings_id));
    }

    public static float[] getFloatArray(Context context, int array_strings_id) {
        return getFloatArray(context.getResources().getStringArray(array_strings_id));
    }

    public static float[] getFloatArray(String[] strings) {
        float[] ret = new float[strings.length];
        for (int i = 0; i < ret.length; i++) {
            ret[i] = Float.parseFloat(strings[i]);
        }
        return ret;
        //return Arrays.stream(strings).map(Float::valueOf).toArray(Float[]::new);
        //return Arrays.stream(strings).mapToDouble(Float::parseFloat).toArray();
    }

    public static Integer[] getIntArray(String[] strings) {
        Integer[] ret = new Integer[strings.length];
        for (int i = 0; i < ret.length; i++) {
            ret[i] = Integer.parseInt(strings[i]);
        }
        return ret;
    }

    public static Bitmap GetImage(ImageView imageView) {
        BitmapDrawable bitmapDrawable = ((BitmapDrawable) imageView.getDrawable());
        Bitmap bitmap;
        if (bitmapDrawable == null) {
            imageView.buildDrawingCache();
            bitmap = imageView.getDrawingCache();
            imageView.buildDrawingCache(false);
        } else {
            bitmap = bitmapDrawable.getBitmap();
        }
        return bitmap;
    }

    //#################################################################################################

    private static final String _URL_DOCS = "https://orenkasko.herokuapp.com/applications";
    private static final String _URL_IMG = "https://orenkasko.herokuapp.com/";
    //private static final String _URL_IMG = "0.0.0.0:80";

    interface Service {
        @Multipart
        @POST("upload_image")
        Call<ResponseBody> postImage(@Part MultipartBody.Part image);
    }

    public interface RequestCallback {
        void callback(String[] result);

    }

    static class PostRequest extends AsyncTask<String[], Object, String> {

        RequestCallback mCallback;

        PostRequest(RequestCallback callback) {
            mCallback = callback;
        }

        @Override
        protected String doInBackground(String[]... params) {
            try {
                URLConnection connection = new URL(_URL_DOCS).openConnection();
                connection.setDoOutput(true);
                connection.setDoInput(true);

                String content = "{\"data\":{ ";//++ "}}";

                String[] vals = params[0];
                int lenght = vals.length;
                for (int i = 0; i < lenght; ) {
                    if (i > 1) {
                        content += ",";
                    }
                    String val = vals[i + 1];
                    if (val.length() > 1 && val.charAt(0) == '[')
                        content += "\"" + vals[i] + "\":" + val + "";
                    else
                        content += "\"" + vals[i] + "\":\"" + val + "\"";
                    i += 2;
                }
                content += "}}";

                connection.setRequestProperty("Content-Type", "application/json");
                connection.setRequestProperty("Content-Length", String.valueOf(content.getBytes().length));

                OutputStream output = connection.getOutputStream();
                output.write(content.getBytes());
                output.close();


                BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                String inputLine;
                String ret = "";
                while ((inputLine = in.readLine()) != null) {
                    if (ret.length() > 0)
                        ret += "\n";
                    ret += inputLine;
                }
                in.close();
                return ret;

            } catch (Exception e) {
                e.printStackTrace();
            }

            return "";
        }

        @Override
        protected void onPostExecute(String result) {
            mCallback.callback(new String[]{result});
        }

    }

    public static void SendPost(RequestCallback callback, String[] params) {
        new PostRequest(callback).execute(params);
    }

    static class ImgRequest extends AsyncTask<Bitmap, Object, String[]> {
        RequestCallback mCallback;
        int mId;
        Context mContext;

        ImgRequest(Context context, RequestCallback callback, int id) {
            mCallback = callback;
            mContext = context;
            mId = id;
        }

        @Override
        protected String[] doInBackground(Bitmap... bitmaps) {
            Bitmap bm = bitmaps[0];
            final String[] ret = new String[2];
            ret[0] = String.valueOf(mId);
            ret[1] = "";
            if (null == bm) return ret;

            try {
                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                bm.compress(Bitmap.CompressFormat.JPEG, 75, bos);
                byte[] data = bos.toByteArray();
                String fileName = "image" + System.currentTimeMillis();

                if (false) {

                    //#####################################################
                    //URLConnection connection = new URL(_URL_IMG).openConnection();
                    URL url = new URL(_URL_IMG + "upload_image");
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    connection.setUseCaches(false);
                    connection.setDoOutput(true);
                    connection.setDoInput(true);
                    connection.setRequestMethod("POST");


                    String lineEnd = "\r\n";
                    String twoHyphens = "--";
                    String boundary = "*****";
                    boundary = "===" + System.currentTimeMillis() + "===";


                    //_____________________________________________
                    connection.setRequestProperty("Connection", "Keep-Alive");
                    //connection.setRequestProperty("ENCTYPE", "multipart/form-data");

                    connection.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary);
                    //connection.setRequestProperty("Content-Type", "multipart/form-data; name=\"filename\"; filename=" + fileName + "\"" + lineEnd);
                    //connection.setRequestProperty("Content-Type", "multipart/form-data");

                    Log(data.length + "");
                    DataOutputStream dos = new DataOutputStream(connection.getOutputStream());
                    //dos.writeBytes(twoHyphens + boundary + lineEnd);
                    //dos.writeBytes("Content-Disposition: form-data; name=\"filename\"; filename=\"" + fileName + "\"" + lineEnd);
                    //dos.writeBytes("Content-Type: multipart/form-data; boundary=" + boundary);
                    dos.writeBytes("Content-Type: image/*" + lineEnd);
                    //dos.writeBytes("Content-Transfer-Encoding: binary" + lineEnd);
                    //connection.setRequestProperty("Content-Length", String.valueOf(content.getBytes().length));


                    //dos.writeBytes("Content-Transfer-Encoding: binary" + lineEnd);

                    //--
                    dos.write(data);
                    //dos.writeBytes(lineEnd);
                    //dos.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);

                    dos.flush();
                    dos.close();
                    //__
                    //___________________________________
                    String test_str = connection.getResponseMessage();
                    int tmp2 = connection.getResponseCode();

                    ret[1] = "0";
                } else {

                    OkHttpClient client = new OkHttpClient.Builder().build();

                    Service service = new Retrofit.Builder().baseUrl(_URL_IMG).client(client).build().create(Service.class);

                    RequestBody reqFile = RequestBody.create(MediaType.parse("image/*"), data);
                    MultipartBody.Part body = MultipartBody.Part.createFormData("filename", fileName, reqFile);
                    //RequestBody name = RequestBody.create(MediaType.parse("text/plain"), "upload_test");
                    //retrofit2.Call<okhttp3.ResponseBody> req = service.postImage(body, name);
                    retrofit2.Call<okhttp3.ResponseBody> req = service.postImage(body);

                    req.enqueue(new Callback<ResponseBody>() {
                        @Override
                        public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                            String ret_path = "0";
                            try {
                                String ret = response.body().string();
                                ret = ret.substring(14, ret.lastIndexOf('"'));
                                //{"temp_path":"/tmp/RackMultipart20171206-4-1g7nq6b"}

                                ret_path = ret;
                            } catch (IOException e) {
                                e.printStackTrace();
                            }

                            synchronized (ret) {
                                ret[1] = ret_path;
                            }
                        }

                        @Override
                        public void onFailure(Call<ResponseBody> call, Throwable t) {
                            t.printStackTrace();
                            synchronized (ret) {
                                ret[1] = "0";
                            }
                        }
                    });


                    do {
                        try {
                            Thread.sleep(500);
                        } catch (Exception e) {
                        }
                        synchronized (ret) {
                            if (0 < ret[1].length())
                                break;
                        }
                    } while (true);
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
            return ret;
        }

        @Override
        protected void onPostExecute(String[] result) {
            mCallback.callback(result);
        }
    }

    static class ImagesRequest extends AsyncTask<Object, Object, String[]> {
        RequestCallback mCallback;
        int img_size;

        ImagesRequest(Context context, RequestCallback callback, ArrayList<ImageLoader> images) {
            mCallback = callback;

            img_size = images.size();
            int i = 0;
            for (ImageLoader image : images) {
                Bitmap bmp = Helpers.GetImage(image.getImageView());
                new ImgRequest(context, img_callback, i++).execute(bmp);
            }
        }

        HashMap<Integer, String> uploaded_img = new HashMap<>();

        RequestCallback img_callback = new RequestCallback() {
            @Override
            public void callback(String[] result) {
                if (null == result) return;
                synchronized (uploaded_img) {
                    Log("img " + result[0] + " = " + result[1]);
                    uploaded_img.put(Integer.parseInt(result[0]), result[1]);
                }
            }
        };

        @Override
        protected String[] doInBackground(Object... params) {

            do {
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                }
                synchronized (uploaded_img) {
                    if (uploaded_img.size() == img_size)
                        break;
                }
            } while (true);

            String[] ret = new String[img_size];
            for (int i = 0; i < img_size; ++i) {
                ret[i] = uploaded_img.get(i);
            }
            return ret;
        }

        @Override
        protected void onPostExecute(String[] result) {
            mCallback.callback(result);
        }

    }

    public static void SendImage(Context context, RequestCallback callback, ArrayList<ImageLoader> images) {
        new ImagesRequest(context, callback, images).execute();
    }


    int serverResponseCode = 0;

    public int uploadFile(Context context, String sourceFileUri) {

        String fileName = sourceFileUri;

        HttpURLConnection conn = null;
        DataOutputStream dos = null;
        String lineEnd = "\r\n";
        String twoHyphens = "--";
        String boundary = "*****";
        int bytesRead, bytesAvailable, bufferSize;
        byte[] buffer;
        int maxBufferSize = 1 * 1024 * 1024;
        File sourceFile = new File(sourceFileUri);

        if (!sourceFile.isFile()) {
            return 0;
        } else {
            try {
                FileInputStream fileInputStream = new FileInputStream(sourceFile);
                URL url = new URL("url");

                // Open a HTTP  connection to  the URL
                conn = (HttpURLConnection) url.openConnection();
                conn.setDoInput(true); // Allow Inputs
                conn.setDoOutput(true); // Allow Outputs
                conn.setUseCaches(false); // Don't use a Cached Copy
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Connection", "Keep-Alive");
                conn.setRequestProperty("ENCTYPE", "multipart/form-data");
                if (false) {
                    conn.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);
                    conn.setRequestProperty("uploaded_file", fileName);

                    dos = new DataOutputStream(conn.getOutputStream());
                    dos.writeBytes(twoHyphens + boundary + lineEnd);
                    dos.writeBytes("Content-Disposition: form-data; name=\"uploaded_file\";filename=\""
                            + fileName + "\"" + lineEnd);
                    dos.writeBytes(lineEnd);
                }
                // create a buffer of  maximum size
                bytesAvailable = fileInputStream.available();

                bufferSize = Math.min(bytesAvailable, maxBufferSize);
                buffer = new byte[bufferSize];

                // read file and write it into form...
                bytesRead = fileInputStream.read(buffer, 0, bufferSize);

                while (bytesRead > 0) {

                    dos.write(buffer, 0, bufferSize);
                    bytesAvailable = fileInputStream.available();
                    bufferSize = Math.min(bytesAvailable, maxBufferSize);
                    bytesRead = fileInputStream.read(buffer, 0, bufferSize);

                }

                // Responses from the server (code and message)
                serverResponseCode = conn.getResponseCode();
                String serverResponseMessage = conn.getResponseMessage();

                Log.i("uploadFile", "HTTP Response is : " + serverResponseMessage + ": " + serverResponseCode);

                if (serverResponseCode == 200) {
                    //complete
                }

                //close the streams //
                fileInputStream.close();
                dos.flush();
                dos.close();

            } catch (MalformedURLException ex) {
                ex.printStackTrace();
                Log("Upload file to server error: " + ex.getMessage());
            } catch (Exception e) {
                Log("Upload file to server Exception Exception : " + e.getMessage());
            }
            return serverResponseCode;

        } // End else block
    }
}