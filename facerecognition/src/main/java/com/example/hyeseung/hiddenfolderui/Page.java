//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.example.hyeseung.hiddenfolderui;

import android.app.Dialog;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.Window;
import android.widget.ImageView;

import com.google.identitytoolkit.IdProvider;
import com.google.identitytoolkit.R.bool;
import com.example.hyeseung.facerecognition.R;
import com.google.identitytoolkit.util.HttpUtils;

import java.io.IOException;
import java.io.Serializable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public abstract class Page extends FragmentPage {
    private final List<Field> saveStateFields = this.getSavedStateFields();
    protected DefaultUiManager uiManager;
    protected int titleResourceId;

    public Page() {
    }

    public Page init(DefaultUiManager uiManager) {
        this.uiManager = uiManager;
        return this;
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(savedInstanceState != null) {
            this.restoreStates(savedInstanceState);
        }

    }

    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        this.saveStates(outState);
    }

    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog;
        if(this.isFloating()) {
            dialog = new Dialog(this.getActivity());
           // dialog.requestWindowFeature(1);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setCanceledOnTouchOutside(false);
        } else {
            dialog = new Dialog(this.getActivity(), R.style.identitytoolkit_non_floating);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
           // dialog = new Dialog(this.getActivity(), R.style.full_screen_dialog);
            if(this.titleResourceId != 0) {
                dialog.setTitle(this.getResources().getString(this.titleResourceId));
            }
        }

        this.forcePortraitIfNecessary();
        return dialog;
    }

    public Page setTitle(int titleResourceId) {
        this.titleResourceId = titleResourceId;
        return this;
    }

    protected boolean isFloating() {
        return true;
    }

    protected void renderPicture(final ImageView view, String pictureUrl) {
        (new AsyncTask<String,Bitmap,Bitmap>() {

            protected Bitmap doInBackground(String... arg) {
                try {
                    byte[] e = HttpUtils.get(arg[0], Collections.<String,String>emptyMap());
                    return BitmapFactory.decodeByteArray(e, 0, e.length);
                } catch (IOException var3) {
                    return null;
                }
            }

            protected void onPostExecute(Bitmap bitmap) {
                if(bitmap != null) {
                    view.setImageBitmap(bitmap);
                }

            }
        }).execute(new String[]{pictureUrl});
    }

    protected void renderIdpIcon(ImageView view, IdProvider idProvider) {
        view.setImageResource(IdpIcons.getIconFor(idProvider));
        view.setBackgroundResource(IdpIcons.getBackgroundFor(idProvider));
    }

    private void forcePortraitIfNecessary() {
        if(this.getResources().getBoolean(bool.identitytoolkit_force_portrait)) {
            this.getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }

    }

    private List<Field> getSavedStateFields() {
        ArrayList fields = new ArrayList();
        Field[] arr$ = this.getClass().getDeclaredFields();
        int len$ = arr$.length;

        for(int i$ = 0; i$ < len$; ++i$) {
            Field field = arr$[i$];
            if(field.getAnnotation(Page.SaveState.class) != null) {
                field.setAccessible(true);
                fields.add(field);
            }
        }

        return fields;
    }

    private void saveStates(Bundle outState) {
        Iterator i$ = this.saveStateFields.iterator();

        while(i$.hasNext()) {
            Field field = (Field)i$.next();

            try {
                this.saveState(outState, field);
            } catch (IllegalAccessException var5) {
                ;
            }
        }

    }

    private void restoreStates(Bundle inState) {
        Iterator i$ = this.saveStateFields.iterator();

        while(i$.hasNext()) {
            Field field = (Field)i$.next();

            try {
                this.restoreState(inState, field);
            } catch (IllegalAccessException var5) {
                ;
            }
        }

    }

    private void saveState(Bundle outState, Field field) throws IllegalAccessException {
        Class type = field.getType();
        String name = field.getName();
        Object value = field.get(this);
        if(Serializable.class.isAssignableFrom(type)) {
            outState.putSerializable(name, (Serializable)value);
        } else {
            if(!Parcelable.class.isAssignableFrom(type)) {
                throw new IllegalArgumentException("Field " + name + " should be either Serializable or Parcelable.");
            }

            outState.putParcelable(name, (Parcelable)value);
        }

    }

    private void restoreState(Bundle inState, Field field) throws IllegalAccessException {
        Class type = field.getType();
        String name = field.getName();
        if(Serializable.class.isAssignableFrom(type)) {
            field.set(this, inState.getSerializable(name));
        } else {
            if(!Parcelable.class.isAssignableFrom(type)) {
                throw new IllegalArgumentException("Field " + name + " should be either Serializable or Parcelable.");
            }

            field.set(this, inState.getParcelable(name));
        }

    }

    @Retention(RetentionPolicy.RUNTIME)
    protected @interface SaveState {
    }
}
