package com.android.crystalmarket.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.crystalmarket.helper.SessionManager;
import com.android.crystalmarket.helper.controlMysql;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.net.URLEncoder;
import java.util.HashMap;


import info.androidhive.loginandregistration.R;


public class CertificateActivity extends Activity implements View.OnClickListener {
    public static final int PICK_FROM_CAMERA = 0; //사진촬영, 이미지 처리
    public static final int PICK_FROM_ALBUM = 1; //앨범에서 사진을 고르고 처리
    public static final int CROP_FROM_IMAGE = 2;

    String temp = "";
    static ProgressDialog pd;
    private Uri mImageCaptureUri;
    private ImageView IV;
    private int id_view;
    static Context mContext;
    SessionManager session;



    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_certificate);

        IV = (ImageView) this.findViewById(R.id.imageView4);
        ImageButton btn_upload = (ImageButton) this.findViewById(R.id.upload);
        Button btn_ctf = (Button) this.findViewById(R.id.btnctf);

        session = new SessionManager(getApplicationContext());
        btn_ctf.setOnClickListener(this);
        btn_upload.setOnClickListener(this);
    }

    public void doTakePhotoAction()
    {
        Intent i = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        String url = "tmp_" + String.valueOf(System.currentTimeMillis()) + ".jpg";
        mImageCaptureUri = Uri.fromFile(new File(Environment.getExternalStorageDirectory(), url));

        i.putExtra(MediaStore.EXTRA_OUTPUT, mImageCaptureUri);
        startActivityForResult(i, PICK_FROM_CAMERA);
    }

    public void doTakeAlbumAction()
    {
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
                    IV.setBackgroundResource(R.drawable.photo_frame);
                    IV.setImageBitmap(photo);
                    photo = resize(photo);
                    BitMapToString(photo);
                    break;
                }
            }
        }

    }

    @Override
    public void onClick(View v) {
        id_view = v.getId();
        if(v.getId() == R.id.btnctf) {
            if (temp.length() > 0) {
                insert_blob();
                pd.dismiss();
                Intent mainIntent = new Intent(CertificateActivity.this, WaitActivity.class);
                startActivity(mainIntent);
                finish();
            }
            else
                Toast.makeText(this, "이미지가 없습니다.", Toast.LENGTH_SHORT).show();

        }else if(v.getId() == R.id.upload) {
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
                    doTakeAlbumAction();
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
    }

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


    void insert_blob() {
        pd = new ProgressDialog(this);
        pd.setMessage("이미지를 DB에 저장중입니다. 잠시만 기다리세요.");
        pd.show();
        Log.e("insert image", temp);

        session.checkLogin();
        HashMap<String, String> user = session.getUserDetails();
        String id = user.get(SessionManager.IS_ID);
        Log.d("insert id", id);

        controlMysql adddb = new controlMysql(temp, id);
        controlMysql.active = true;
        adddb.start();
    }

    static public void add_image(String result) {   //이미지 추가 결과
        if (result != null)
            Log.e("result", result);
        controlMysql.active = false;
        if (result.contains("true")) {
            Toast.makeText(mContext, "이미지가 DB에 추가되었습니다..", Toast.LENGTH_SHORT).show();
        } else {

            Toast.makeText(mContext, result + " 이미지가 DB에 추가되지 못했습니다.", Toast.LENGTH_SHORT).show();
        }
        pd.cancel();

    }

}