package com.example.streetreport;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NavUtils;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.amazonaws.auth.CognitoCachingCredentialsProvider;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferObserver;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class ShareActivity extends AppCompatActivity implements View.OnClickListener {
    //카메라 작업 처리위한 변수
    static final int PICK_FROM_CAMERA = 0;
    static final int PICK_FROM_ALBUM = 1;
    static final int CROP_FROM_IMAGE = 2;

    Uri imageUri;
    ImageView user_img;
    EditText report_content;
    String absolutePath;
    int id_view;
    File copyFile;
    Button type1, type2, type3, type4, type5;
    TextView addLocation;
    double lat;
    double lon;
    GpsInfo gps;
    String image;
    String type;
    String nickname;
    String content;
    String latitude;
    String longitude;
    CognitoCachingCredentialsProvider credentialsProvider;
    AmazonS3 s3;
    TransferUtility transferUtility;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share);

        //actionBar 객체를 가져옴.
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);


        Button btn_UploadPhoto = (Button) this.findViewById(R.id.uploadPhoto);
        user_img = (ImageView) this.findViewById(R.id.user_img);
        report_content = (EditText) this.findViewById(R.id.report_content);

        btn_UploadPhoto.setOnClickListener(this);

        // Initialize the Amazon Cognito credentials provider
        credentialsProvider = new CognitoCachingCredentialsProvider(
                getApplicationContext(),
                "ap-northeast-2:589cd763-a0df-4f5b-9731-389b7ccd998a", // Identity Pool ID
                Regions.AP_NORTHEAST_2 // Region
        );

        s3 = new AmazonS3Client(credentialsProvider);
        transferUtility = new TransferUtility(s3, getApplicationContext());

        // S3 버킷의 위치와 목적 설정
        s3.setRegion(Region.getRegion(Regions.AP_NORTHEAST_2));
        s3.setEndpoint("s3.ap-northeast-2.amazonaws.com");

        type1 = (Button) findViewById(R.id.type1);
        type1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                type1.setSelected(!type1.isSelected());
            }
        });

        type2 = (Button) findViewById(R.id.type2);
        type2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                type2.setSelected(!type2.isSelected());
            }
        });

        type3 = (Button) findViewById(R.id.type3);
        type3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                type3.setSelected(!type3.isSelected());
            }
        });

        type4 = (Button) findViewById(R.id.type4);
        type4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                type4.setSelected(!type4.isSelected());
            }
        });

        type5 = (Button) findViewById(R.id.type5);
        type5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                type5.setSelected(!type5.isSelected());
            }
        });

        addLocation = (TextView) findViewById(R.id.add_location);
        addLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gps = new GpsInfo(ShareActivity.this);
                if (gps.isGetLocation()) {
                    lat = gps.getLatitude();
                    lon = gps.getLongitude();
                }
                Log.d("lat", "" + lat);
                Log.d("lon", "" + lon);
            }
        });

    }

    //카메라에서 사진 촬영
    public void doTakePhotoAction() { //카메라 촬영 후 이미지 가져오기
        int permissionCheck = ContextCompat.checkSelfPermission(ShareActivity.this, Manifest.permission.CAMERA);
        if (permissionCheck == PackageManager.PERMISSION_DENIED) {
            //권한없음
            ActivityCompat.requestPermissions(ShareActivity.this, new String[]{Manifest.permission.CAMERA}, 0);
        } else {
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

            //임시로 사용할 파일 경로를 생성
            String url = "temp_" + String.valueOf(System.currentTimeMillis()) + ".jpg";
            imageUri = Uri.fromFile(new File(Environment.getExternalStorageDirectory(), url));

            intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
            startActivityForResult(intent, PICK_FROM_CAMERA);
        }
    }

    //앨범에게 이미지 가져오기
    public void doTakeAlbumAction() { //앨범에서 이미지 가져오기
        //앨범호출
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType(MediaStore.Images.Media.CONTENT_TYPE);
        startActivityForResult(intent, PICK_FROM_ALBUM);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode != RESULT_OK)
            return;

        switch (requestCode) {
            case PICK_FROM_ALBUM: {
                imageUri = data.getData();
                Log.d("앨범", imageUri.getPath().toString());
//                break;
            }

            case PICK_FROM_CAMERA: {
                //이미지를 가져온 이후의 리사이즈할 이미지 크기 결정
                // 이후에 이미지 크롭 앱 호출
                Intent intent = new Intent("com.android.camera.action.CROP");
                intent.setDataAndType(imageUri, "image/*");

                //CROP할 이미지를 100*100 크기로 저장
                intent.putExtra("outputX", 100);
                intent.putExtra("outputY", 100);
                intent.putExtra("aspectX", 1);
                intent.putExtra("aspectY", 1);
                intent.putExtra("scale", true);
                intent.putExtra("return-data", true);
                startActivityForResult(intent, CROP_FROM_IMAGE); //CROP_FROM_CAMERA case문 이동
                break;
            }
            case CROP_FROM_IMAGE: {
                //크롭된 이후의 이미지 넘겨받음
                //이미지뷰에 이미지를 보여주는 작업 이후 임시파일 삭제
                if (resultCode != RESULT_OK) {
                    return;
                }

                final Bundle extras = data.getExtras();

                //CROP된 이미지를 저장하기 위한 FILE경로
                String filePath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/StreetReport/" + System.currentTimeMillis() + ".jpg";

                if (extras != null) {
                    Bitmap photo = extras.getParcelable("data");//크롭된 bitmap
                    user_img.setImageBitmap(photo); //레이아웃의 이미지칸에 CROP된 BITMAP을 보여줌
                    storeCropImage(photo, filePath); //CROP된 이미지를 외부저장소, 앨범에 저장
                    absolutePath = filePath;
                    break;
                }

                //임시파일삭제
                File f = new File(imageUri.getPath());
                if (f.exists()) {
                    f.delete();
                }
            }
        }
    }

    @Override
    public void onClick(View v) {
        id_view = v.getId();

        if (v.getId() == R.id.uploadPhoto) {
            DialogInterface.OnClickListener cameraListener = new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    doTakePhotoAction();
                }
            };
            DialogInterface.OnClickListener albumListener = new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    doTakeAlbumAction();
                }
            };
            DialogInterface.OnClickListener cancelLister = new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            };

            //dialog생성
            new AlertDialog.Builder(this)
                    .setTitle("업로드할 이미지 선택")
                    .setPositiveButton("사진촬영", cameraListener)
                    .setNeutralButton("앨범에서 선택", albumListener)
                    .setNegativeButton("취소", cancelLister)
                    .show();
        }
    }

    //Bitmap을 저장하는 부분
    private void storeCropImage(Bitmap bitmap, String filePath) {
        //StreetReport 폴더를 생성해서 이미지를 저장하는 방식
        String dirPath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/StreetReport";
        File directory_StreetReport = new File(dirPath);

        if (!directory_StreetReport.exists()) //StreetReport 디렉터리에 폴더가 없으면(새로 이미지저장할 경우)
            directory_StreetReport.mkdir();

        copyFile = new File(filePath);
        BufferedOutputStream out = null;

        try {
            copyFile.createNewFile();
            out = new BufferedOutputStream(new FileOutputStream(copyFile));
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);

            sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(copyFile)));
            out.flush();
            out.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.share_menu, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                finish();
                Toast.makeText(this, "홈아이콘 클릭", Toast.LENGTH_SHORT).show();
                return true;
            case R.id.share_complete:
                Log.d("copyFile", copyFile.getName());

                Toast.makeText(this, "공유하기 클릭", Toast.LENGTH_SHORT).show();

                image = copyFile.getName();
                type = type1.getTag().toString();
                nickname = KakaoSignupActivity.userprofile.getNickname();
                content = report_content.getText().toString();
                latitude = "" + lat;
                longitude = "" + lon;

                //파일업로드
                TransferObserver observer = transferUtility.upload(
                        "streetreport",     /* 업로드 할 버킷 이름 */
                        copyFile.getName(),    /* 버킷에 저장할 파일의 이름 */
                        copyFile        /* 버킷에 저장할 파일  */
                );
                Log.d("copyFile", copyFile.getName());

                String req_url = "";
                req_url += "type=" + type;
                req_url += "&nickname=" + nickname;
                req_url += "&content=" + content;
                req_url += "&latitude=" + latitude;
                req_url += "&longitude=" + longitude;
                req_url += "&image=" + image;

                CallHttp callHttp = new CallHttp(HttpHandler.BASE_URL, req_url);
                callHttp.start();
                return true;

        }
        return super.onOptionsItemSelected(item);
    }

    public class CallHttp extends Thread {
        private String data;

        public CallHttp(String url, String data) {
            this.data = url + data;
        }

        @Override
        public void run() {
            try {
                String url = this.data;
                String response = HttpHandler.getInstance().GET(url);
                Log.d("HTTP", response);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}