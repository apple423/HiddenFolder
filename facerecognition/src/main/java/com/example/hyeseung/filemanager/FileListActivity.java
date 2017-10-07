package com.example.hyeseung.filemanager;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.example.hyeseung.facerecognition.R;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class FileListActivity extends ListActivity{

    private String targetPath, currentPath;
    private List<String> item = null;
    private List<String> path = null;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.file_list);

        currentPath = Constants.rootPath;
        targetPath = null;
        getDir(Constants.rootPath);
    }

    public void onBackPressed() {
        if(currentPath.equals(Constants.rootPath)) {
            setResult(RESULT_CANCELED);
            finish();
        } else {
            String[] temp = currentPath.split("/");
            currentPath = currentPath.substring(0, currentPath.length() - (temp[temp.length - 1].length() + 1));
            getDir(currentPath);
        }
    }

    private void getDir(String dirPath) {
        TextView textPath = (TextView) findViewById(R.id.path);
        textPath.setText("Location: " + dirPath);

        currentPath = dirPath;
        item = new ArrayList<String>();
        path = new ArrayList<String>();

        File f = new File(dirPath);
        File[] files = f.listFiles();

        if (!dirPath.equals(Constants.rootPath)) {
            item.add(Constants.rootPath);
            path.add(Constants.rootPath);
            item.add("../");
            path.add(f.getParent());
        }

        for (int i = 0; i < files.length; i++) {
            File file = files[i];
            path.add(file.getPath());
            if (file.isDirectory())
                item.add(file.getName() + "/");
            else
                item.add(file.getName());
        }
        ArrayAdapter<String> fileList = new ArrayAdapter<String>(this, R.layout.list_row, item);
        setListAdapter(fileList);
    }

    public String getCurrentPath() { return currentPath; }
    public String getFilePath() { return targetPath; }

    protected void onListItemClick(ListView l, View v, int position, long id) {
        File file = new File(path.get(position));

        if (file.isDirectory()) {
            if (file.canRead()) {
                getDir(path.get(position));
            }
            else {
                new AlertDialog.Builder(this)
                        .setTitle("[" + file.getName() + "] 이 폴더는 읽을 수 없습니다.")
                        .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // TODO Auto-generated method stub
                            }
                        }).show();
            }
        } else {
            targetPath = file.getPath();
            new AlertDialog.Builder(this)
                    .setTitle("[" + file.getName() + "]")
                    .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            Intent intent = new Intent();
                            intent.putExtra("path", targetPath);
                            setResult(RESULT_OK, intent);
                            finish();
                        }
                    })
                    .setNegativeButton("취소", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            // TODO Auto-generated method stub
                        }
                    }).show();
        }
    }
}