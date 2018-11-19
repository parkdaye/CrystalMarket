package com.android.crystalmarket.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.crystalmarket.app.AppConfig;
import com.android.crystalmarket.app.AppController;
import com.android.crystalmarket.helper.SessionManager;
import com.android.crystalmarket.helper.controlMysql;
import com.android.crystalmarket.helper.controlMysql2;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

import info.androidhive.loginandregistration.R;


import static com.android.crystalmarket.activity.CertificateActivity.CROP_FROM_IMAGE;
import static com.android.crystalmarket.activity.CertificateActivity.mContext;
import static com.android.crystalmarket.app.AppController.TAG;

/**
 * Created by user on 2018-05-25.
 */

public class UploadActivity extends Activity implements View.OnClickListener {
    private static final String TAG = com.android.crystalmarket.activity.UploadActivity.class.getSimpleName();
    public static final int PICK_FROM_CAMERA = 10; //사진촬영, 이미지 처리
    public static final int PICK_FROM_ALBUM = 11; //앨범에서 사진을 고르고 처리
    public static final int CROP_FROM_IMAGE = 21;
    private String id;

    private EditText productsName;
    private EditText productsPrice;
    private EditText productsInfo;
    private TextView productscategory;
    private ImageView  productsPic;
    private Button btnUpload;
    private ImageButton btnPic;

    private int id_view;
    private Uri mImageCaptureUri;
    private ProgressDialog pDialog;
    String temp = "";
    SessionManager session;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload);

        productsName = (EditText) findViewById(R.id.products_name);
        productsPrice = (EditText) findViewById(R.id.products_price);
        productsInfo = (EditText) findViewById(R.id.products_info);
        productscategory = (TextView) findViewById(R.id.products_category);
        productsPic = (ImageView) findViewById(R.id.products_pic);
        btnUpload = (Button) findViewById(R.id.products_upload);
        btnPic = (ImageButton) findViewById(R.id.btnpic);
        session = new SessionManager(getApplicationContext());

        session.checkLogin();
        // get user data from session
        HashMap<String, String> user = session.getUserDetails();
        // name
        id = user.get(SessionManager.IS_ID);


        // Progress dialog
        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);

        btnPic.setOnClickListener(this);
        btnUpload.setOnClickListener(this);
        productscategory.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        id_view = v.getId();
        if (v.getId() == R.id.btnpic) {
            DialogInterface.OnClickListener cameraListener = new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    doTakePhotoAction();
                    dialog.dismiss();

                }
            };
            DialogInterface.OnClickListener albumListener = new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int i) {
                    AlbumAction();
                    dialog.dismiss();
                }
            };
            DialogInterface.OnClickListener cancelistener = new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int i) {
                    dialog.dismiss();
                }
            };

            new AlertDialog.Builder(this)
                    .setTitle("업로드 사진 선택")
                    .setPositiveButton("사진촬영", cameraListener)
                    .setNeutralButton("앨범선택", albumListener)
                    .setNegativeButton("취소", cancelistener)
                    .show();

        }

        else if (v.getId() == R.id.products_upload) {
            String products_name = productsName.getText().toString().trim();
            String products_price = productsPrice.getText().toString().trim();
            String products_info = productsInfo.getText().toString().trim();
            String category = productscategory.getText().toString().trim();

            if (!products_name.isEmpty() && !products_price.isEmpty() && !products_info.isEmpty()
                    && !category.isEmpty()) {
                if (temp.length() > 0) {
                    registerProduct(products_name, products_price, products_info, category, temp);
                } else
                    Toast.makeText(getApplicationContext(), "이미지가 없습니다.", Toast.LENGTH_SHORT).show();

            } else {
                Toast.makeText(getApplicationContext(),
                        "모든 물품정보를 입력해주세요.", Toast.LENGTH_SHORT)
                        .show();
            }
        }

        else if (v.getId() == R.id.products_category) {
            Intent intent = new Intent(UploadActivity.this, CategoryActivity.class);
            startActivityForResult(intent, 3000);
        }
    }

    public void doTakePhotoAction()
    {
        Intent i = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        String url = "tmp_" + String.valueOf(System.currentTimeMillis()) + ".jpg";
        mImageCaptureUri = Uri.fromFile(new File(Environment.getExternalStorageDirectory(), url));

        i.putExtra(MediaStore.EXTRA_OUTPUT, mImageCaptureUri);
        startActivityForResult(i, PICK_FROM_CAMERA);
    }


    public void AlbumAction() {
        Intent i = new Intent(Intent.ACTION_PICK);
        i.setType(MediaStore.Images.Media.CONTENT_TYPE);
        startActivityForResult(i, PICK_FROM_ALBUM);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode != RESULT_OK)
            return;

        switch(requestCode)
        {
            case PICK_FROM_ALBUM: {
                mImageCaptureUri = data.getData();
                //Log.d("SmartWheel", mImageCaptureUri.getPath().toString());
            }


            case PICK_FROM_CAMERA: {
                Intent i = new Intent("com.android.camera.action.CROP");
                i.setDataAndType(mImageCaptureUri, "image/*");


                i.putExtra("outputX", 200);
                i.putExtra("outputY", 200);
                i.putExtra("aspectX", 1);
                i.putExtra("aspectY", 1);
                i.putExtra("scale", true);
                i.putExtra("return-data", true);

                startActivityForResult(i, CROP_FROM_IMAGE);

                break;

            }

            case CROP_FROM_IMAGE: {
                if(resultCode != RESULT_OK) {
                    return;
                }

                final Bundle extras = data.getExtras();

                if(extras != null)
                {
                    Bitmap photo = extras.getParcelable("data");
                    productsPic.setImageBitmap(photo);
                    photo = resize(photo);
                    BitMapToString(photo);
                    break;
                }
            }

            case 3000:
                productscategory.setText(data.getStringExtra("result"));
                break;
        }

    }


    /*
    private void showResult() {
        String tag_string_req = "req_upload";

        pDialog.setMessage("상품글 업로드중입니다 ...");
        showDialog();

        StringRequest strReq = new StringRequest(AppConfig.URL_upload, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Upload Response: " + response);
                hideDialog();

                try {
                    JSONObject jObj = new JSONObject(response);
                    boolean error = jObj.getBoolean("error");
                    if (!error) {

                        Toast.makeText(getApplicationContext(), "상품이 업로드되었습니다!", Toast.LENGTH_LONG).show();


                        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                        startActivity(intent);
                        finish();

                    } else {

                        String errorMsg = jObj.getString("error_msg");
                        Toast.makeText(getApplicationContext(), errorMsg, Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {

                    e.printStackTrace();
                    Toast.makeText(getApplicationContext(), "Json error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Uploading Error: " + error.getMessage());
                Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_LONG).show();
                hideDialog();
            }
        });

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }

    private void showDialog() {
        if (!pDialog.isShowing())
            pDialog.show();
    }

    private void hideDialog() {
        if (pDialog.isShowing())
            pDialog.dismiss();
    }
*/

    @SuppressLint("NewApi")
    private Bitmap resize(Bitmap bm){

        Configuration config=getResources().getConfiguration();
		/*if(config.smallestScreenWidthDp>=800)
			bm = Bitmap.createScaledBitmap(bm, 400, 240, true);//이미지 크기로 인해 용량초과
		else */if(config.smallestScreenWidthDp>=600)
            bm = Bitmap.createScaledBitmap(bm, 300, 180, true);
        else if(config.smallestScreenWidthDp>=400)
            bm = Bitmap.createScaledBitmap(bm, 200, 120, true);
        else if(config.smallestScreenWidthDp>=360)
            bm = Bitmap.createScaledBitmap(bm, 180, 108, true);
        else
            bm = Bitmap.createScaledBitmap(bm, 160, 96, true);

        return bm;

    }


    public void BitMapToString(Bitmap bitmap) {

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);    //bitmap compress
        byte[] arr = baos.toByteArray();
        String image = Base64.encodeToString(arr, Base64.DEFAULT);
        try {
            temp = URLEncoder.encode(image, "utf-8");
        } catch (Exception e) {
            Log.e("exception", e.toString());
        }
    }


    void registerProduct(final String products_name, final String products_price, final String products_info,
                         final String category, final String temp) {


        Log.d("insert id", id);

        controlMysql2 db = new controlMysql2(products_name, products_price, products_info, category, id, temp);
        controlMysql2.active = true;
        db.start();


        Toast.makeText(getApplicationContext(), "상품이 업로드되었습니다!", Toast.LENGTH_LONG).show();
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(intent);
        finish();

    }


}




