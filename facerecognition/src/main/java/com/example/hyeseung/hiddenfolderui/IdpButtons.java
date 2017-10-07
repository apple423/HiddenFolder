//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.example.hyeseung.hiddenfolderui;

import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.MarginLayoutParams;
import android.widget.Button;

import com.google.identitytoolkit.IdProvider;
import com.google.identitytoolkit.R.dimen;
import com.google.identitytoolkit.R.integer;
import com.google.identitytoolkit.R.string;

import java.util.List;

public final class IdpButtons {
    private IdpButtons() {
    }

    public static void show(LayoutInflater inflater, Resources resources, ViewGroup container, List<IdProvider> idProviders, final IdpButtons.OnIdpButtonClickListener listener) {
        int maxNumberOfIdpButtons = Math.min(idProviders.size(), resources.getInteger(integer.identitytoolkit_max_number_of_idp_buttons));

        for(int i = 0; i < maxNumberOfIdpButtons; ++i) {
            final IdProvider idProvider = (IdProvider)idProviders.get(i);
            Button button = renderButton(inflater, resources, container, idProvider);
            if(i != 0) {
                MarginLayoutParams params = (MarginLayoutParams)button.getLayoutParams();
                int marginTop = resources.getDimensionPixelSize(dimen.identitytoolkit_space_small);
                params.setMargins(0, marginTop, 0, 0);
                button.setLayoutParams(params);
            }

            button.setOnClickListener(new OnClickListener() {
                public void onClick(View v) {
                    listener.onIdpButtonClick(idProvider);
                }
            });
        }

    }

    private static Button renderButton(LayoutInflater inflater, Resources resources, ViewGroup container, IdProvider idProvider) {
        Button button = (Button)inflater.inflate(com.google.identitytoolkit.R.layout.identitytoolkit_idp_button, container, false);
        container.addView(button);
        String text = resources.getString(string.identitytoolkit_label_sign_in_with, new Object[]{idProvider.getDisplayName()});
        button.setText(text);
        button.setBackgroundResource(IdpIcons.getBackgroundFor(idProvider));
        Drawable icon = resources.getDrawable(IdpIcons.getIconFor(idProvider));
        if(icon != null) {
            int height = resources.getDimensionPixelSize(dimen.identitytoolkit_idp_button_height);
            icon.setBounds(0, 0, height, height);
            button.setCompoundDrawables(icon, (Drawable)null, (Drawable)null, (Drawable)null);
        }

        return button;
    }

    public interface OnIdpButtonClickListener {
        void onIdpButtonClick(IdProvider var1);
    }
}
