//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.example.hyeseung.hiddenfolderui;

import android.app.Dialog;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.identitytoolkit.IdProvider;
import com.google.identitytoolkit.R.id;
import com.google.identitytoolkit.R.string;
import com.google.identitytoolkit.UiManager.VerifyAccountWithIdpRequest;

public class IdpAccountLinkingPage extends Page {
    @SaveState
    private String email;
    @SaveState
    private IdProvider previousIdProvider;
    @SaveState
    private IdProvider currentIdProvider;

    public IdpAccountLinkingPage() {
    }

    public Page init(DefaultUiManager uiManager, String email, IdProvider previousIdProvider, IdProvider currentIdProvider) {
        this.email = email;
        this.previousIdProvider = previousIdProvider;
        this.currentIdProvider = currentIdProvider;
        return this.init(uiManager);
    }

    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.setContentView(com.google.identitytoolkit.R.layout.identitytoolkit_idp_link_account_page);
        return dialog;
    }

    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Dialog dialog = this.getDialog();
        Resources resources = this.getResources();
        TextView text = (TextView)dialog.findViewById(id.identitytoolkit_verify_account_header);
        text.setText(resources.getString(string.identitytoolkit_header_verify_account, new Object[]{this.email}));
        text = (TextView)dialog.findViewById(id.identitytoolkit_verified_account_info);
        text.setText(resources.getString(string.identitytoolkit_label_idp_verified_account_info, new Object[]{this.previousIdProvider.getDisplayName()}));
        ImageView icon = (ImageView)dialog.findViewById(id.identitytoolkit_verified_idp_icon);
        this.renderIdpIcon(icon, this.previousIdProvider);
        text = (TextView)dialog.findViewById(id.identitytoolkit_current_account_info);
        text.setText(resources.getString(string.identitytoolkit_label_idp_current_account_info, new Object[]{this.currentIdProvider.getDisplayName()}));
        icon = (ImageView)dialog.findViewById(id.identitytoolkit_current_idp_icon);
        this.renderIdpIcon(icon, this.currentIdProvider);
        text = (TextView)dialog.findViewById(id.identitytoolkit_verify_account_instruction);
        text.setText(resources.getString(string.identitytoolkit_label_idp_verify_account_instruction, new Object[]{this.previousIdProvider.getDisplayName()}));
        Button next = (Button)dialog.findViewById(id.identitytoolkit_next);
        next.setText(resources.getString(string.identitytoolkit_label_idp_verify_account_continue, new Object[]{this.previousIdProvider.getDisplayName()}));
        next.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                IdpAccountLinkingPage.this.uiManager.getRequestHandler().handle((new VerifyAccountWithIdpRequest()).setEmail(IdpAccountLinkingPage.this.email).setPreviousIdProvider(IdpAccountLinkingPage.this.previousIdProvider));
            }
        });
    }
}
