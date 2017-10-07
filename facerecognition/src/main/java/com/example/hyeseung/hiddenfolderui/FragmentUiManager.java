//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.example.hyeseung.hiddenfolderui;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;

import com.google.identitytoolkit.UiManager;


public abstract class FragmentUiManager implements UiManager {
    private final FragmentManager fragmentManager;

    public FragmentUiManager(Activity activity) {
        this.fragmentManager = activity.getFragmentManager();
    }

    protected abstract String getCurrentPageTag();

    protected Page getCurrentPage() {
        return (Page)this.fragmentManager.findFragmentByTag(this.getCurrentPageTag());
    }

    protected void show(Page page) {
        FragmentTransaction transaction = this.fragmentManager.beginTransaction();
        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        Page current = this.getCurrentPage();
        if(current != null) {
            transaction.remove(current);
        }

        transaction.add(page, this.getCurrentPageTag());
        transaction.commitAllowingStateLoss();
    }
}
