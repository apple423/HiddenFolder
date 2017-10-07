//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.example.hyeseung.hiddenfolderui;

import android.app.Dialog;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.identitytoolkit.R.id;
import com.google.identitytoolkit.UiManager.SignInWithPasswordRequest;
import com.example.hyeseung.facerecognition.R.layout;
import com.example.hyeseung.facerecognition.R.drawable;


public class PasswordSignInPage extends Page {
    @SaveState
    private String email;

    public PasswordSignInPage() {
    }

    public Page init(DefaultUiManager uiManager, String email) {
        this.email = email;
        return this.init(uiManager);
    }

    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.setContentView(layout.identitytoolkit_password_sign_in_page);
        return dialog;
    }

    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Dialog dialog = this.getDialog();
        EditText emailInput = (EditText)dialog.findViewById(id.identitytoolkit_email);
        emailInput.setText(this.email);
        final EditText passwordInput = (EditText)dialog.findViewById(id.identitytoolkit_password);
        final Button nextButton = (Button)dialog.findViewById(id.identitytoolkit_next);
        passwordInput.setTypeface(Typeface.DEFAULT);
        passwordInput.addTextChangedListener(new TextChangedListener() {
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                passwordInput.setBackgroundResource(drawable.identitytoolkit_text_input_bg);
                if(s.length() == 0) {
                    nextButton.setEnabled(false);
                } else {
                    nextButton.setEnabled(true);
                }

            }
        });
        nextButton.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                String password = passwordInput.getText().toString();
                PasswordSignInPage.this.uiManager.getRequestHandler().handle((new SignInWithPasswordRequest()).setEmail(PasswordSignInPage.this.email).setPassword(password));
            }
        });
        TextView forgotLink = (TextView)dialog.findViewById(id.identitytoolkit_trouble_sign_in);
        forgotLink.setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                PasswordSignInPage.this.uiManager.showPasswordRecovery(PasswordSignInPage.this.email);
            }
        });
    }
/*
    public void showPasswordError() {
        EditText passwordInput = (EditText)this.getDialog().findViewById(id.identitytoolkit_password);
        passwordInput.setText("");
        passwordInput.setBackgroundResource(drawable.identitytoolkit_text_input_bg_invalid);
        Activity context = this.getActivity();
        Toast.makeText(context, context.getString(string.identitytoolkit_hint_invalid_password), Toast.LENGTH_LONG).show();
    }*/
}
