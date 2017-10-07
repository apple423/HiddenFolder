package com.example.hyeseung.filemanager;

import android.os.Environment;

public class Constants {
    public static final String rootPath = Environment.getExternalStorageDirectory().getAbsolutePath();
    public static final String appPath = rootPath + "/.HiddenFolder";

    public static final int REQ_CODE_SELECT_IMAGE = 0;
    public static final int REQ_CODE_SELECT_VIDEO = 1;
    public static final int REQ_CODE_SELECT_AUDIO = 2;
    public static final int REQ_CODE_SELECT_ETC = 3;

    public static final int EXTENSION_TYPE_IMAGE = 0;
    public static final int EXTENSION_TYPE_VIDEO = 1;
    public static final int EXTENSION_TYPE_AUDIO = 2;
    public static final int EXTENSION_TYPE_TEXT = 3;
    public static final int EXTENSION_TYPE_EXE = 4;
    public static final int EXTENSION_TYPE_UNKNOWN = 5;
}