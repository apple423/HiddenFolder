package com.example.hyeseung.filemanager;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.drawable.ColorDrawable;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.hyeseung.facerecognition.R;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.util.ArrayList;

public class FolderActivity extends AppCompatActivity {

    private String currentPath;
    private ArrayList<File> currentList;
    private File targetFile = null;
    private boolean isDelete = false;

    private Bitmap[] imgBit;
    private int displayWidth;
    private boolean isEmpty = false;
    private int[] fileType;

    private Activity act = this;
    private GridView gridview;

    private ImageButton btnFileAdd;
    private ImageButton btnFileDel;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_folder);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(0xFF4198E6));

        DisplayMetrics dm = getApplicationContext().getResources().getDisplayMetrics();
        displayWidth = dm.widthPixels;

        Intent intent = getIntent();
        currentPath = intent.getExtras().getString("path");

        String[] temp = currentPath.split("/");
        String folderName = temp[temp.length - 1];
        getSupportActionBar().setTitle(folderName);

        currentList = new ArrayList<>();
        File file = new File(currentPath);
        File[] files = file.listFiles();

        imgBit = new Bitmap[files.length];
        fileType = new int[files.length];

        gridview = (GridView) findViewById(R.id.imgView);
        if(files.length == 0)
            Toast.makeText(FolderActivity.this, "파일이 없습니다.", Toast.LENGTH_SHORT).show();
        else {
            for (int i = 0; i < files.length; i++) {
                File dir = files[i];
                currentList.add(dir);
                imgBit[i] = createThumbnail(files[i].toString());
                fileType[i] = getType(files[i].toString());
            }
            gridview.setAdapter(new fileAdapter());

            gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                    Toast.makeText(FolderActivity.this, "" + currentList.get(position).getName(), Toast.LENGTH_SHORT).show();

                    Intent intent = new Intent(Intent.ACTION_VIEW);

                    switch (getType(currentList.get(position).toString())) {
                        case Constants.EXTENSION_TYPE_IMAGE:
                            intent.setDataAndType(Uri.fromFile(currentList.get(position)), "image/*");
                            break;

                        case Constants.EXTENSION_TYPE_VIDEO:
                            intent.setDataAndType(Uri.fromFile(currentList.get(position)), "video/*");
                            break;

                        case Constants.EXTENSION_TYPE_AUDIO:
                            intent.setDataAndType(Uri.fromFile(currentList.get(position)), "audio/*");
                            break;

                        case Constants.EXTENSION_TYPE_TEXT:
                            intent.setDataAndType(Uri.fromFile(currentList.get(position)), "text/*");
                            break;

                        case Constants.EXTENSION_TYPE_EXE:
                            break;

                        case Constants.EXTENSION_TYPE_UNKNOWN:
                            break;
                    }
                    startActivity(intent);
                }
            });
        }

        btnFileAdd = (ImageButton) findViewById(R.id.btnFileAdd);
        btnFileAdd.setOnClickListener(btnClickListener);
        btnFileAdd.setOnTouchListener(btnTouchListener);
        btnFileDel = (ImageButton) findViewById(R.id.btnFileDel);
        btnFileDel.setOnClickListener(btnClickListener);
        btnFileDel.setOnTouchListener(btnTouchListener);
    }

    protected void onStart() {
        super.onStart();
    }

    protected void onResume() {
        super.onResume();
        updateUI();
    }

    public void onBackPressed() {
        finish();
    }

    Button.OnClickListener btnClickListener = new View.OnClickListener() {
        public void onClick(View v) {
            final CharSequence[] items;
            final boolean[] checked;
            File file;
            File[] files;

            switch (v.getId()) {
                case R.id.btnFileAdd:
                    final CharSequence[] category = {"사진", "동영상", "오디오", "기타"};

                    new AlertDialog.Builder(FolderActivity.this)
                            .setTitle("파일 추가")
                            .setItems(category, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    switch (which) {
                                        case Constants.REQ_CODE_SELECT_IMAGE:
                                            Intent intentImage = new Intent(Intent.ACTION_GET_CONTENT);
                                            intentImage.setType("image/*");
                                            startActivityForResult(intentImage, Constants.REQ_CODE_SELECT_IMAGE);
                                            break;

                                        case Constants.REQ_CODE_SELECT_VIDEO:
                                            Intent intentVideo = new Intent(Intent.ACTION_GET_CONTENT);
                                            intentVideo.setType("video/*");
                                            startActivityForResult(intentVideo, Constants.REQ_CODE_SELECT_VIDEO);
                                            break;

                                        case Constants.REQ_CODE_SELECT_AUDIO:
                                            Intent intentAudio = new Intent(Intent.ACTION_GET_CONTENT);
                                            intentAudio.setType("audio/*");
                                            startActivityForResult(intentAudio, Constants.REQ_CODE_SELECT_AUDIO);
                                            break;

                                        case Constants.REQ_CODE_SELECT_ETC:
                                            Intent intentETC = new Intent(FolderActivity.this, FileListActivity.class);
                                            startActivityForResult(intentETC, Constants.REQ_CODE_SELECT_ETC);
                                            break;
                                    }
                                }
                            })
                            .setNegativeButton("취소", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    // TODO Auto-generated method stub
                                }
                            }).show();
                    break;

                case R.id.btnFileDel:
                    ArrayList<File> fileList = new ArrayList<>();
                    file = new File(currentPath);
                    files = file.listFiles();

                    for (int i = 0; i < files.length; i++) {
                        File f = files[i];
                        if(f.isFile()) {
                            fileList.add(f);
                        }
                    }

                    if(fileList.size() == 0)
                        Toast.makeText(FolderActivity.this, "파일이 없습니다.", Toast.LENGTH_LONG).show();
                    else {
                        items = new CharSequence[fileList.size()];
                        checked = new boolean[fileList.size()];

                        for (int i = 0; i < fileList.size(); i++) {
                            String[] temp = fileList.get(i).toString().split("/");
                            items[i] = temp[temp.length - 1].toString();
                            checked[i] = false;
                        }

                        new AlertDialog.Builder(FolderActivity.this)
                                .setTitle("파일을 선택하세요.")
                                .setMultiChoiceItems(items, checked, new DialogInterface.OnMultiChoiceClickListener() {
                                    public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                                        checked[which] = isChecked;
                                    }
                                })
                                .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        for (int i = 0; i < checked.length; i++) {
                                            if (checked[i]) {
                                                String selectedPath = items[i].toString();
                                                targetFile = new File(currentPath + "/" + selectedPath);
                                                targetFile.delete();
                                            }
                                        }
                                        updateUI();
                                    }
                                })
                                .setNegativeButton("취소", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        // TODO Auto-generated method stub
                                    }
                                }).show();
                    }
                    break;
            }
        }
    };

    Button.OnTouchListener btnTouchListener = new View.OnTouchListener() {
        public boolean onTouch(View v, MotionEvent event) {
            if(event.getAction() == MotionEvent.ACTION_DOWN) {
                if(v.getId() == R.id.btnFileAdd)
                    btnFileAdd.setImageResource(R.drawable.file_add_02);
                if(v.getId() == R.id.btnFileDel)
                    btnFileDel.setImageResource(R.drawable.file_del_02);
            }
            if(event.getAction() == MotionEvent.ACTION_UP) {
                if(v.getId() == R.id.btnFileAdd)
                    btnFileAdd.setImageResource(R.drawable.file_add_01);
                if(v.getId() == R.id.btnFileDel)
                    btnFileDel.setImageResource(R.drawable.file_del_01);
            }
            return false;
        }
    };

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case Constants.REQ_CODE_SELECT_IMAGE:
                case Constants.REQ_CODE_SELECT_VIDEO:
                case Constants.REQ_CODE_SELECT_AUDIO:
                    try {
                        Uri uri = data.getData();
                        File file = new File(getImagePath(uri));
                        targetFile = file;
                        moveFile(targetFile.getAbsolutePath(), currentPath + "/" + targetFile.getName(), isDelete);
                    } catch (Exception e) {
                        e.printStackTrace();
                        Toast.makeText(FolderActivity.this, "파일 저장에 실패했습니다.", Toast.LENGTH_SHORT).show();
                    }
                    break;

                case Constants.REQ_CODE_SELECT_ETC:
                    String targetPath = data.getStringExtra("path");
                    if (!targetPath.equals(null)) {
                        File file = new File(targetPath);
                        targetFile = file;
                        moveFile(targetFile.getAbsolutePath(), currentPath + "/" + targetFile.getName(), isDelete);
                    }
                    break;
            }
        }
    }

    private void moveFile(String source, String dest, boolean del) {
        FileInputStream fis = null;
        FileOutputStream fos = null;

        try {
            fis = new FileInputStream(source);
            fos = new FileOutputStream(dest);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        FileChannel fcin = fis.getChannel();
        FileChannel fcout = fos.getChannel();

        long size = 0;
        try {
            size = fcin.size();
            fcin.transferTo(0, size, fcout);

            fcout.close();
            fcin.close();
            fos.close();
            fis.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        if(del) {
            File delFile = new File(source);
            delFile.delete();
        }
    }

    private String getImagePath(Uri uri) {
        String[] proj = {MediaStore.Images.Media.DATA};
        Cursor cursor = managedQuery(uri, proj, null, null, null);
        int index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);

        cursor.moveToFirst();
        String path = cursor.getString(index);

        return path;
    }

    private Bitmap createThumbnail(String path) {
        Bitmap bitmap;
        int imgSize = displayWidth/3;
        int type = getType(path);

        switch (type) {
            case Constants.EXTENSION_TYPE_IMAGE:
                bitmap = BitmapFactory.decodeFile(path);
                int w = bitmap.getWidth();
                int h = bitmap.getHeight();

                int scale;
                if (w > h) scale = h;
                else scale = w;

                bitmap = Bitmap.createBitmap(bitmap, w / 2 - scale / 2, h / 2 - scale / 2, scale, scale);
                bitmap = Bitmap.createScaledBitmap(bitmap, imgSize, imgSize, false);
                return bitmap;

            case Constants.EXTENSION_TYPE_VIDEO:
                bitmap = ThumbnailUtils.createVideoThumbnail(path, MediaStore.Images.Thumbnails.MICRO_KIND);
                bitmap = overlayMark(bitmap, BitmapFactory.decodeResource(this.getResources(), R.drawable.icon_play_mark));
                return bitmap;

            default:
                return null;
        }
    }

    private Bitmap overlayMark(Bitmap bm1, Bitmap bm2) {
        Bitmap bmOverlay = Bitmap.createBitmap(bm1.getWidth(), bm1.getHeight(), bm1.getConfig());
        Bitmap bmMark = Bitmap.createScaledBitmap(bm2, bm1.getWidth(), bm1.getHeight(), true);
        Canvas canvas = new Canvas(bmOverlay);
        canvas.drawBitmap(bm1, 0, 0, null);
        canvas.drawBitmap(bmMark, 0, 0, null);

        bm1.recycle();
        bm2.recycle();
        bmMark.recycle();

        return bmOverlay;
    }

    private String getExtension(String path) {
        return path.substring(path.lastIndexOf(".")+1, path.length());
    }

    private int getType(String path) {
        String extension = getExtension(path);
        switch (extension) {
            case "PNG":case "png":
            case "JPG":case "jpg":
            case "JPEG":case "jpeg":
            case "GIF":case "gif":
            case "BMP":case "bmp":
                return Constants.EXTENSION_TYPE_IMAGE;

            case "AVI":case "avi":
            case "WMV":case "wmv":
            case "MP4":case "mp4":
            case "FLV":case "flv":
            case "MPEG":case "mpeg":
                return Constants.EXTENSION_TYPE_VIDEO;

            case "MP3":case "mp3":
            case "M4A":case "m4a":
            case "WAV":case "wav":
            case "OGG":case "ogg":
            case "AMR":case "amr":
                return Constants.EXTENSION_TYPE_AUDIO;

            case "TXT":case "txt":
            case "HWP":case "hwp":
            case "DOC":case "doc":
            case "DOCX":case "docx":
            case "XML":case "xml":
            case "PDF":case "pdf":
            case "PPTX":case "pptx":
                return Constants.EXTENSION_TYPE_TEXT;

            case "EXE":case "exe":
                return Constants.EXTENSION_TYPE_EXE;

            default:
                return Constants.EXTENSION_TYPE_UNKNOWN;
        }
    }

    public class fileAdapter extends BaseAdapter {
        LayoutInflater inflater;

        public fileAdapter() {
            inflater = (LayoutInflater) act.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        public final int getCount() { return currentList.size(); }
        public final Object getItem(int position) { return currentList.get(position); }
        public final long getItemId(int position) { return position; }

        public View getView(int position, View convertView, ViewGroup parent) {
            if(convertView == null)
                convertView = inflater.inflate(R.layout.image_item, parent, false);

            ImageView imgView = (ImageView) convertView.findViewById(R.id.fileImage);
            if(isEmpty)
                imgView.setImageResource(R.drawable.empty);
            else {
                switch (fileType[position]) {
                    case Constants.EXTENSION_TYPE_IMAGE:
                        if(imgBit[position] == null)
                            imgView.setImageResource(R.drawable.icon_image);
                        else
                            imgView.setImageBitmap(imgBit[position]);
                        break;

                    case Constants.EXTENSION_TYPE_VIDEO:
                        if(imgBit[position] == null)
                            imgView.setImageResource(R.drawable.icon_video);
                        else
                            imgView.setImageBitmap(imgBit[position]);
                        break;

                    case Constants.EXTENSION_TYPE_AUDIO:
                        imgView.setImageResource(R.drawable.icon_audio);
                        break;

                    case Constants.EXTENSION_TYPE_TEXT:
                        imgView.setImageResource(R.drawable.icon_text);
                        break;

                    case Constants.EXTENSION_TYPE_EXE:
                        imgView.setImageResource(R.drawable.icon_exe);
                        break;

                    default:
                        imgView.setImageResource(R.drawable.icon_etc);
                }
            }

            return convertView;
        }
    }

    private void updateUI() {
        currentList = new ArrayList<>();
        File file = new File(currentPath);
        File[] files = file.listFiles();

        imgBit = new Bitmap[files.length];
        fileType = new int[files.length];

        gridview = (GridView) findViewById(R.id.imgView);
        if(files.length == 0) {
            Toast.makeText(FolderActivity.this, "파일이 없습니다.", Toast.LENGTH_SHORT).show();
            isEmpty = true;
        }
        else {
            isEmpty = false;
            for (int i = 0; i < files.length; i++) {
                File dir = files[i];
                currentList.add(dir);
                imgBit[i] = createThumbnail(files[i].toString());
                fileType[i] = getType(files[i].toString());
            }
        }
        gridview.setAdapter(new fileAdapter());
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_info:
                Intent intentInfo = new Intent(FolderActivity.this, InfoActivity.class);
                startActivity(intentInfo);
                break;

            case R.id.action_exit:
                new AlertDialog.Builder(FolderActivity.this)
                        .setTitle("종료하시겠습니까?")
                        .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                ActivityCompat.finishAffinity(FolderActivity.this);
                            }
                        })
                        .setNegativeButton("취소", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                            }
                        }).show();
                break;

            case android.R.id.home:
                // NavUtils.navigateUpFromSameTask(this);
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}