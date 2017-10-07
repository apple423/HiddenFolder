//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.example.hyeseung.hiddenfolderui;

import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.identitytoolkit.R;
import com.google.identitytoolkit.GitkitUser;
import com.google.identitytoolkit.GitkitUser.UserProfile;
import com.google.identitytoolkit.R.id;
import com.google.identitytoolkit.UiManager.StartSignInRequest;


public class AccountChipSignInPage extends Page {
    @SaveState
    private GitkitUser.UserProfile userProfile;
    //private DefaultUiManager uiManager;

    public AccountChipSignInPage() {
    }

    public Page init(DefaultUiManager uiManager, UserProfile userProfile) {
        this.userProfile = userProfile;
        return this.init(uiManager);
    }

    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.setContentView(R.layout.identitytoolkit_account_chip_sign_in_page);
        return dialog;
    }

    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Dialog dialog = this.getDialog();
        View accountChip = dialog.findViewById(id.identitytoolkit_account_chip);
        this.renderAccountChip(this.userProfile, accountChip);
        accountChip.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                AccountChipSignInPage.this.uiManager.getRequestHandler().handle((new StartSignInRequest()).setEmail(AccountChipSignInPage.this.userProfile.getEmail()).setIdProvider(AccountChipSignInPage.this.userProfile.getIdProvider()));
            }
        });
        TextView switchLink = (TextView)dialog.findViewById(id.identitytoolkit_switch_account);
        switchLink.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                AccountChipSignInPage.this.uiManager.showStartSignIn(null);
            }
        });
    }

    private void renderAccountChip(UserProfile userProfile, View accountChip) {
        TextView emailView = (TextView)accountChip.findViewById(id.identitytoolkit_account_chip_email);
        emailView.setText(userProfile.getEmail());
        if(userProfile.getDisplayName() != null) {
            TextView pictureView = (TextView)accountChip.findViewById(id.identitytoolkit_account_chip_name);
            pictureView.setText(userProfile.getDisplayName());
        }

        if(userProfile.getPhotoUrl() != null) {
            ImageView pictureView1 = (ImageView)accountChip.findViewById(id.identitytoolkit_account_chip_picture);
            this.renderPicture(pictureView1, userProfile.getPhotoUrl());
        }

    }
}
