package com.example.hyeseung.filemanager;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.hyeseung.facerecognition.R;

import java.io.File;
import java.util.ArrayList;

public class FileManageActivity extends AppCompatActivity {

    private String currentPath;
    private ArrayList<File> currentList;

    private Activity act = this;
    private GridView gridview;

    private ImageButton btnFolderAdd;
    private ImageButton btnFolderDel;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setTitle("갤러리");
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(0xFF4198E6));
        setContentView(R.layout.activity_file_manage);

        currentPath = Constants.appPath;
        currentList = new ArrayList<>();
        File file = new File(Constants.appPath);
        File[] files = file.listFiles();

        for (int i = 0; i < files.length; i++) {
            File dir = files[i];
            currentList.add(dir);
        }

        gridview = (GridView) findViewById(R.id.gridView);
        gridview.setAdapter(new folderAdapter());

        gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                File file = new File(currentList.get(position).toString());
                currentPath = currentList.get(position).toString();

                Intent intent = new Intent(FileManageActivity.this, FolderActivity.class);
                intent.putExtra("path", currentPath);
                startActivity(intent);
            }
        });

        File appDir = new File(Constants.appPath);
        if (!appDir.exists())
            appDir.mkdirs();

        File imageDir = new File(Constants.appPath + "/내 사진");
        if(!imageDir.exists())
            imageDir.mkdirs();

        File videoDir = new File(Constants.appPath + "/내 동영상");
        if(!videoDir.exists())
            videoDir.mkdirs();

        btnFolderAdd = (ImageButton) findViewById(R.id.btnFolderAdd);
        btnFolderAdd.setOnClickListener(btnClickListener);
        btnFolderAdd.setOnTouchListener(btnTouchListener);
        btnFolderDel = (ImageButton) findViewById(R.id.btnFolderDel);
        btnFolderDel.setOnClickListener(btnClickListener);
        btnFolderDel.setOnTouchListener(btnTouchListener);
    }

    protected void onStart() {
        super.onStart();
    }

    protected void onResume() {
        super.onResume();
        currentPath = Constants.appPath;
        updateUI();
    }

    public void onBackPressed() {
        new AlertDialog.Builder(FileManageActivity.this)
                .setTitle("종료하시겠습니까?")
                .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        ActivityCompat.finishAffinity(FileManageActivity.this);
                    }
                })
                .setNegativeButton("취소", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                    }
                }).show();
    }

    Button.OnClickListener btnClickListener = new View.OnClickListener() {
        public void onClick(View v) {
            final CharSequence[] items;
            final boolean[] checked;
            File file;
            File[] files;

            switch (v.getId()) {
                case R.id.btnFolderAdd:
                    final EditText inputTxt = new EditText(FileManageActivity.this);

                    new AlertDialog.Builder(FileManageActivity.this)
                            .setTitle("이름을 입력하세요.")
                            .setView(inputTxt)
                            .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    String inputPath = inputTxt.getText().toString();
                                    File newDir = new File(currentPath + "/" + inputPath);
                                    newDir.mkdirs();
                                    updateUI();
                                }
                            })
                            .setNegativeButton("취소", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    // TODO Auto-generated method stub
                                }
                            }).show();
                    break;

                case R.id.btnFolderDel:
                    ArrayList<File> folderList = new ArrayList<>();
                    file = new File(Constants.appPath);
                    files = file.listFiles();

                    for (int i = 0; i < files.length; i++) {
                        File dir = files[i];
                        if(dir.isDirectory()) {
                            folderList.add(dir);
                        }
                    }

                    if(folderList.size() == 0)
                        Toast.makeText(FileManageActivity.this, "폴더가 없습니다.", Toast.LENGTH_LONG).show();
                    else {
                        items = new CharSequence[folderList.size()];
                        checked = new boolean[folderList.size()];

                        for (int i = 0; i < folderList.size(); i++) {
                            String[] temp = folderList.get(i).toString().split("/");
                            items[i] = temp[temp.length - 1].toString();
                            checked[i] = false;
                        }

                        new AlertDialog.Builder(FileManageActivity.this)
                                .setTitle("폴더를 선택하세요.")
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
                                                deleteDir(currentPath + "/" + selectedPath);
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
                if(v.getId() == R.id.btnFolderAdd)
                    btnFolderAdd.setImageResource(R.drawable.folder_add_02);
                if(v.getId() == R.id.btnFolderDel)
                    btnFolderDel.setImageResource(R.drawable.folder_del_02);
            }
            if(event.getAction() == MotionEvent.ACTION_UP) {
                if(v.getId() == R.id.btnFolderAdd)
                    btnFolderAdd.setImageResource(R.drawable.folder_add_01);
                if(v.getId() == R.id.btnFolderDel)
                    btnFolderDel.setImageResource(R.drawable.folder_del_01);
            }
            return false;
        }
    };

    private void deleteDir(String path) {
        File file = new File(path);
        File[] childFileList = file.listFiles();
        for (File childFile : childFileList) {
            if (childFile.isDirectory()) {
                deleteDir(childFile.getAbsolutePath());
            } else {
                childFile.delete();
            }
        }
        file.delete();
    }

    public class folderAdapter extends BaseAdapter {
        LayoutInflater inflater;

        public folderAdapter() {
            inflater = (LayoutInflater) act.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        public final int getCount() { return currentList.size(); }
        public final Object getItem(int position) { return currentList.get(position); }
        public final long getItemId(int position) { return position; }

        public View getView(int position, View convertView, ViewGroup parent) {
            if(convertView == null)
                convertView = inflater.inflate(R.layout.grid_item, parent, false);

            ImageView imgView = (ImageView) convertView.findViewById(R.id.folderImage);
            TextView txtView = (TextView) convertView.findViewById(R.id.folderName);

            imgView.setImageResource(R.drawable.icon_folder);
            txtView.setText(currentList.get(position).getName());

            return convertView;
        }
    }

    private void updateUI() {
        currentList = new ArrayList<>();
        File file = new File(Constants.appPath);
        File[] files = file.listFiles();

        for (int i = 0; i < files.length; i++) {
            File dir = files[i];
            currentList.add(dir);
        }

        gridview.setAdapter(new folderAdapter());
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_info:
                Intent intentInfo = new Intent(FileManageActivity.this, InfoActivity.class);
                startActivity(intentInfo);
                break;

            case R.id.action_exit:
                new AlertDialog.Builder(FileManageActivity.this)
                        .setTitle("종료하시겠습니까?")
                        .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                ActivityCompat.finishAffinity(FileManageActivity.this);
                            }
                        })
                        .setNegativeButton("취소", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                            }
                        }).show();
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}