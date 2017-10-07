//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.example.hyeseung.hiddenfolderui;

import android.app.Dialog;
import android.content.res.Resources;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.identitytoolkit.IdProvider;
import com.google.identitytoolkit.R.id;
import com.google.identitytoolkit.R.string;
import com.google.identitytoolkit.UiManager.VerifyAccountWithPasswordRequest;
import com.google.identitytoolkit.R.layout;


public class PasswordAccountLinkingPage extends Page {
    @SaveState
    private String email;
    @SaveState
    private IdProvider currentIdProvider;

    public PasswordAccountLinkingPage() {
    }

    public Page init(DefaultUiManager uiManager, String email, IdProvider currentIdProvider) {
        this.email = email;
        this.currentIdProvider = currentIdProvider;
        return this.init(uiManager);
    }

    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.setContentView(layout.identitytoolkit_password_link_account_page);
        return dialog;
    }

    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Dialog dialog = this.getDialog();
        Resources resources = this.getResources();
        TextView text = (TextView)dialog.findViewById(id.identitytoolkit_verify_account_header);
        text.setText(resources.getString(string.identitytoolkit_header_verify_account, new Object[]{this.email}));
        text = (TextView)dialog.findViewById(id.identitytoolkit_current_account_info);
        text.setText(resources.getString(string.identitytoolkit_label_idp_current_account_info, new Object[]{this.currentIdProvider.getDisplayName()}));
        ImageView icon = (ImageView)dialog.findViewById(id.identitytoolkit_current_idp_icon);
        this.renderIdpIcon(icon, this.currentIdProvider);
        final EditText password = (EditText)dialog.findViewById(id.identitytoolkit_password);
        final Button next = (Button)dialog.findViewById(id.identitytoolkit_next);
        password.setTypeface(Typeface.DEFAULT);
        password.addTextChangedListener(new TextChangedListener() {
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(s.length() == 0) {
                    next.setEnabled(false);
                } else {
                    next.setEnabled(true);
                }

            }
        });
        next.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                PasswordAccountLinkingPage.this.uiManager.getRequestHandler().handle((new VerifyAccountWithPasswordRequest()).setEmail(PasswordAccountLinkingPage.this.email).setPassword(password.getText().toString()));
            }
        });
    }
}
