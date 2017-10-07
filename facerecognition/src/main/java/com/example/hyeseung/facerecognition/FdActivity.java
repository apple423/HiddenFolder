package com.example.hyeseung.facerecognition;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase.CvCameraViewFrame;
import org.opencv.android.CameraBridgeViewBase.CvCameraViewListener2;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfRect;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.objdetect.CascadeClassifier;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class FdActivity extends Activity implements CvCameraViewListener2 {

    private static final String TAG = "OCVSample::Activity";
    private static final Scalar FACE_RECT_COLOR = new Scalar(0, 255, 0, 255);
    public static final int JAVA_DETECTOR = 0;
    public static final int NATIVE_DETECTOR = 1;

    public static final int TRAINING = 0;
    public static final int SEARCHING = 1;
    public static final int IDLE = 2;

    public static final long MAXIMG = 10;

    private int faceState = IDLE;

    private Mat mRgba;
    private Mat mGray;
    private File mCascadeFile;
    private CascadeClassifier mJavaDetector;

    private int mDetectorType = JAVA_DETECTOR;
    private String[] mDetectorName;

    private float mRelativeFaceSize = 0.2f;
    private int mAbsoluteFaceSize = 0;
    private int mLikely = 999;

    String mPath = "";

    private Tutorial3View mOpenCvCameraView;
    PersonRecognizer fr;
    labels labelsFile;

    Bitmap mBitmap;
    Handler mHandler;

    private ToggleButton btnRecognition, btnRegistration, btnStart;
    private Button btnPin;
    private ImageView ivGreen, ivYellow, ivRed, faceImg;
    private TextView tvGuide;

    int countImages = 0;

    private String email;
    private int likelyCount = 0;
    private int pinCount = 0;
    private String pincode;
    private int pinValid=0;

    private BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(this) {
        @Override
        public void onManagerConnected(int status) {
            switch (status) {
                case LoaderCallbackInterface.SUCCESS: {
                    Log.i(TAG, "OpenCV loaded successfully");

                    fr = new PersonRecognizer(mPath);
                    fr.load();

                    try {
                        // load cascade file from application resources
                        InputStream is = getResources().openRawResource(R.raw.lbpcascade_frontalface);
                        File cascadeDir = getDir("cascade", Context.MODE_PRIVATE);
                        mCascadeFile = new File(cascadeDir, "lbpcascade.xml");
                        FileOutputStream os = new FileOutputStream(mCascadeFile);

                        byte[] buffer = new byte[4096];
                        int bytesRead;
                        while ((bytesRead = is.read(buffer)) != -1) {
                            os.write(buffer, 0, bytesRead);
                        }
                        is.close();
                        os.close();

                        mJavaDetector = new CascadeClassifier(mCascadeFile.getAbsolutePath());
                        if (mJavaDetector.empty()) {
                            Log.e(TAG, "Failed to load cascade classifier");
                            mJavaDetector = null;
                        } else
                            Log.i(TAG, "Loaded cascade classifier from " + mCascadeFile.getAbsolutePath());

                        cascadeDir.delete();

                    } catch (IOException e) {
                        e.printStackTrace();
                        Log.e(TAG, "Failed to load cascade. Exception thrown: " + e);
                    }

                    mOpenCvCameraView.enableView();
                }
                break;
                default: {
                    super.onManagerConnected(status);
                }
                break;
            }
        }
    };

    public FdActivity() {
        mDetectorName = new String[2];
        mDetectorName[JAVA_DETECTOR] = "Java";
        mDetectorName[NATIVE_DETECTOR] = "Native (tracking)";

        Log.i(TAG, "Instantiated new " + this.getClass());
    }

    public void onCreate(Bundle savedInstanceState) {
        Log.i(TAG, "called onCreate");
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.face_detect_surface_view);

        mOpenCvCameraView = (Tutorial3View) findViewById(R.id.cameraView);
        mOpenCvCameraView.setCvCameraViewListener(this);

        Bundle bundle = getIntent().getExtras();
        email = bundle.getString("userEmail");

        mPath = getFilesDir() + "/facerecogOCV/";
        labelsFile = new labels(mPath);

        btnRecognition = (ToggleButton) findViewById(R.id.btnRecognition);
        btnRegistration = (ToggleButton) findViewById(R.id.btnRegistration);
        btnStart = (ToggleButton) findViewById(R.id.btnStart);
        btnPin = (Button) findViewById(R.id.btnPin);

        ivGreen = (ImageView) findViewById(R.id.imgGreen);
        ivYellow = (ImageView) findViewById(R.id.imgYellow);
        ivRed = (ImageView) findViewById(R.id.imgRed);
        faceImg = (ImageView) findViewById(R.id.faceView);
        tvGuide = (TextView) findViewById(R.id.txtGuide);

        setInvisibleLight();
        btnStart.setVisibility(View.INVISIBLE);
        btnPin.setVisibility(View.INVISIBLE);
        faceImg.setVisibility(View.INVISIBLE);

        mHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                if (msg.obj == "IMG") {
                    Canvas canvas = new Canvas();
                    canvas.setBitmap(mBitmap);
                    faceImg.setImageBitmap(mBitmap);

                    if (countImages >= MAXIMG - 1) {
                        btnStart.setChecked(false);
                        grabarOnclick();
                    }
                } else {
                    setInvisibleLight();

                    if (mLikely < 0) ;
                    else if (mLikely < 50) {
                        ivGreen.setVisibility(View.VISIBLE);
                        if (msg.obj.toString().equals(email) || msg.obj.toString().equals(email + "_copy"))
                            likelyCount += 10;
                    } else if (mLikely < 90) {
                        ivYellow.setVisibility(View.VISIBLE);
                        if (msg.obj.toString().equals(email) || msg.obj.toString().equals(email + "_copy"))
                            likelyCount++;
                    } else {
                        ivRed.setVisibility(View.VISIBLE);
                        pinCount++;
                    }
                }

                if (likelyCount > 10) {
                    Toast.makeText(FdActivity.this, "얼굴 인식에 성공했습니다.", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(FdActivity.this, com.example.hyeseung.filemanager.FileManageActivity.class);
                    intent.setPackage("com.example.hyeseung.facerecognition");
                    startActivity(intent);
                }

                if (pinCount > 10 && btnRecognition.isChecked()) {
                    btnPin.setVisibility(View.VISIBLE);
                }
            }
        };

        btnRecognition.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (btnRecognition.isChecked()) {
                    if (!fr.canPredict()) {
                        btnRecognition.setChecked(false);
                        Toast.makeText(getApplicationContext(), "얼굴을 등록해 주세요.", Toast.LENGTH_LONG).show();
                        return;
                    }
                    faceState = SEARCHING;
                    tvGuide.setText("얼굴 인식 중...");
                    tvGuide.setTextColor(Color.GRAY);
                    btnRegistration.setVisibility(View.INVISIBLE);
                    btnStart.setVisibility(View.INVISIBLE);
                } else {
                    faceState = IDLE;
                    tvGuide.setText("");
                    setInvisibleLight();
                    btnRegistration.setVisibility(View.VISIBLE);
                    btnPin.setVisibility(View.INVISIBLE);
                }
            }
        });

        btnRegistration.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (btnRegistration.isChecked()) {
                    tvGuide.setText("시작 버튼을 눌러 얼굴을 등록해 주세요.");
                    setInvisibleLight();
                    btnRecognition.setVisibility(View.INVISIBLE);
                    btnStart.setVisibility(View.VISIBLE);
                    faceImg.setVisibility(View.VISIBLE);
                } else {
                    tvGuide.setText("얼굴 등록 중...");
                    btnRecognition.setVisibility(View.VISIBLE);
                    btnStart.setVisibility(View.INVISIBLE);
                    faceImg.setVisibility(View.INVISIBLE);

                    fr.train();
                    tvGuide.setText("");
                }
            }
        });

        btnStart.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (btnStart.isChecked())
                    tvGuide.setText("얼굴이 제대로 등록되었으면 완료 버튼을 눌러주세요.");
                grabarOnclick();
            }
        });

        btnPin.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                mOpenCvCameraView.disableView();
                final EditText inputTxt = new EditText(FdActivity.this);
                inputTxt.setInputType(InputType.TYPE_CLASS_NUMBER);
                //inputTxt.setShowSoftInputOnFocus(true);
                    new AlertDialog.Builder(FdActivity.this)
                            .setTitle("핀코드를 입력하세요.")
                            .setView(inputTxt)

                            .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    SharedPreferences pref = getSharedPreferences(email, MODE_PRIVATE);
                                    pincode = pref.getString("pin", "");
                                    if (pinValid < 2) {
                                        if (pincode.equals(inputTxt.getText().toString())) {
                                            pinValid = 0;
                                            Toast.makeText(FdActivity.this, "핀코드로 접속하셨습니다.", Toast.LENGTH_SHORT).show();
                                            Intent intent = new Intent(FdActivity.this, com.example.hyeseung.filemanager.FileManageActivity.class);
                                            intent.setPackage("com.example.hyeseung.facerecognition");
                                            startActivity(intent);


                                        } else {
                                            pinValid++;
                                            mOpenCvCameraView.enableView();
                                            Toast.makeText(FdActivity.this, "핀코드를 확인해주세요.", Toast.LENGTH_SHORT).show();
                                        }
                                    } else {
                                        mOpenCvCameraView.enableView();
                                        mOpenCvCameraView.takePicture(email + "침입자");
                                       // mOpenCvCameraView.disableView();
                                        new AlertDialog.Builder(FdActivity.this)
                                                .setTitle("침입자 경고")
                                                .setMessage("Pincode 3회 입력 실패하셨습니다.")
                                                .setNegativeButton("닫기", new DialogInterface.OnClickListener() {
                                                    public void onClick(DialogInterface dlg, int sumthin) {

                                                        onDestroy();
                                                        ActivityCompat.finishAffinity(FdActivity.this);

                                                    }


                                                }).show();


                                    }
                                    // TODO Auto-generated method stub
                                }
                            })
                            .setNegativeButton("취소", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    // TODO Auto-generated method stub
                                    mOpenCvCameraView.enableView();

                                }
                            }).show();
                }

        });


        boolean success = (new File(mPath)).mkdirs();
        if (!success) {
            Log.e("Error", "Error creating directory");
        }
    }

    void grabarOnclick() {
        if (btnStart.isChecked())
            faceState = TRAINING;
        else {
            if (faceState == TRAINING) ;
            // train();
            //fr.train();
            countImages = 0;
            faceState = IDLE;
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mOpenCvCameraView != null)
            mOpenCvCameraView.disableView();
    }

    @Override
    public void onResume() {
        super.onResume();
        OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_2_4_10, this, mLoaderCallback);
    }

    public void onDestroy() {
        super.onDestroy();
        mOpenCvCameraView.disableView();
    }

    public void onBackPressed() {
        new AlertDialog.Builder(FdActivity.this)
                .setTitle("종료하시겠습니까?")
                .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        ActivityCompat.finishAffinity(FdActivity.this);
                    }
                })
                .setNegativeButton("취소", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                    }
                }).show();
    }

    public void onCameraViewStarted(int width, int height) {
        mGray = new Mat();
        mRgba = new Mat();
    }

    public void onCameraViewStopped() {
        mGray.release();
        mRgba.release();
    }

    public Mat onCameraFrame(CvCameraViewFrame inputFrame) {

        mRgba = inputFrame.rgba();
        mGray = inputFrame.gray();

        if (mAbsoluteFaceSize == 0) {
            int height = mGray.rows();
            if (Math.round(height * mRelativeFaceSize) > 0) {
                mAbsoluteFaceSize = Math.round(height * mRelativeFaceSize);
            }
        }

        MatOfRect faces = new MatOfRect();

        if (mDetectorType == JAVA_DETECTOR) {
            if (mJavaDetector != null)
                mJavaDetector.detectMultiScale(mGray, faces, 1.1, 2, 2, // TODO: objdetect.CV_HAAR_SCALE_IMAGE
                        new Size(mAbsoluteFaceSize, mAbsoluteFaceSize), new Size());
        } else {
            Log.e(TAG, "Detection method is not selected!");
        }

        Rect[] facesArray = faces.toArray();

        if ((facesArray.length == 1) && (faceState == TRAINING) && (countImages < MAXIMG)) {

            Mat m = new Mat();
            Rect r = facesArray[0];

            m = mRgba.submat(r);
            mBitmap = Bitmap.createBitmap(m.width(), m.height(), Bitmap.Config.ARGB_8888);

            Utils.matToBitmap(m, mBitmap);

            Message msg = new Message();
            String textTochange = "IMG";
            msg.obj = textTochange;
            mHandler.sendMessage(msg);
            if (countImages < MAXIMG) {
                fr.add(m, email);
                fr.add(m, email + "_copy");
                countImages++;
            }

        } else if ((facesArray.length > 0) && (faceState == SEARCHING)) {
            Mat m = new Mat();
            m = mGray.submat(facesArray[0]);
            mBitmap = Bitmap.createBitmap(m.width(), m.height(), Bitmap.Config.ARGB_8888);

            Utils.matToBitmap(m, mBitmap);
            Message msg = new Message();
            String textTochange = "IMG";
            msg.obj = textTochange;
            mHandler.sendMessage(msg);

            textTochange = fr.predict(m);
            mLikely = fr.getProb();
            msg = new Message();
            msg.obj = textTochange;
            mHandler.sendMessage(msg);
        }

        for (int i = 0; i < facesArray.length; i++)
            Core.rectangle(mRgba, facesArray[i].tl(), facesArray[i].br(), FACE_RECT_COLOR, 3);

        return mRgba;
    }

    private void setMinFaceSize(float faceSize) {
        mRelativeFaceSize = faceSize;
        mAbsoluteFaceSize = 0;
    }

    private void setInvisibleLight() {
        ivGreen.setVisibility(View.INVISIBLE);
        ivYellow.setVisibility(View.INVISIBLE);
        ivRed.setVisibility(View.INVISIBLE);
    }
}