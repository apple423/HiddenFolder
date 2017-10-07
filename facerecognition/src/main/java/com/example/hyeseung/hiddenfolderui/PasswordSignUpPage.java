//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.example.hyeseung.hiddenfolderui;

import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;

import com.example.hyeseung.facerecognition.R;
import com.example.hyeseung.facerecognition.R.drawable;
import com.google.identitytoolkit.UiManager.SignUpWithPasswordRequest;

public class PasswordSignUpPage extends Page {
    @SaveState
    private String email;
    private PasswordSignUpPage.DataChecker dataChecker;
    private EditText emailText;
    private EditText nameInput;
    private EditText passwordInput;
    private EditText passwordInput2;
    private CheckBox tosCheckBox;
    private Button nextButton;

    public PasswordSignUpPage() {
    }

    public Page init(DefaultUiManager uiManager, String email) {
        this.email = email;
        return this.init(uiManager);
    }


/* public Page init(DefaultUiManager uiManager){

        return this.init(uiManager);
    }*/

    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.setContentView(R.layout.signuppage);
        return dialog;
    }

    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Dialog dialog = this.getDialog();

        this.dataChecker = new PasswordSignUpPage.DataChecker();
        this.emailText = (EditText)dialog.findViewById(R.id.sign_up_email);
        this.emailText.setTypeface(Typeface.DEFAULT);
        this.emailText.addTextChangedListener(this.dataChecker);
        this.nameInput = (EditText)dialog.findViewById(R.id.sign_up_display_name);
        this.nameInput.addTextChangedListener(this.dataChecker);
        this.passwordInput = (EditText)dialog.findViewById(R.id.sign_up_password);
        this.passwordInput.setTypeface(Typeface.DEFAULT);
        this.passwordInput.addTextChangedListener(this.dataChecker);
        this.passwordInput2 = (EditText)dialog.findViewById(R.id.sign_up_password_confirm);
        this.passwordInput2.setTypeface(Typeface.DEFAULT);
        this.passwordInput2.addTextChangedListener(this.dataChecker);
        this.tosCheckBox = (CheckBox)dialog.findViewById(R.id.sign_up_check_tos);
        if(this.uiManager.getTosUrl() == null) {
            this.tosCheckBox.setVisibility(View.GONE);
        } else {
           // String tosText = String.format(this.getResources().getString(string.identitytoolkit_label_tos), new Object[]{this.uiManager.getTosUrl()});
            this.tosCheckBox.setText("이용약관에 동의하십니까?");
            //this.tosCheckBox.setMovementMethod(LinkMovementMethod.getInstance());
            this.tosCheckBox.setOnCheckedChangeListener(this.dataChecker);
        }

        this.nextButton = (Button)dialog.findViewById(R.id.sign_up_next);
        this.nextButton.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                PasswordSignUpPage.this.dataChecker.checkInputData();

                PasswordSignUpPage.this.uiManager.getRequestHandler().handle((new SignUpWithPasswordRequest()).setEmail(PasswordSignUpPage.this.emailText.getText().toString()).setDisplayName(PasswordSignUpPage.this.nameInput.getText().toString()).setPassword(PasswordSignUpPage.this.passwordInput.getText().toString()));
            }
        });
    }
    @Override
    public void onDismiss(DialogInterface dialog) {

        super.onDismiss(dialog);
        PasswordSignUpPage.this.uiManager.showStartSignIn(null);

    }
    private class DataChecker extends TextChangedListener implements OnCheckedChangeListener {
        private DataChecker() {
        }

        public void onTextChanged(CharSequence s, int start, int before, int count) {
            this.updatePasswordInputStatus();
            this.updateButtonStatus();
        }

        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            this.updatePasswordInputStatus();
            this.updateButtonStatus();
        }

        public boolean checkInputData() {
            return PasswordSignUpPage.this.emailText.getText().length() != 0 && PasswordSignUpPage.this.nameInput.getText().length() != 0 && PasswordSignUpPage.this.passwordInput.getText().length() != 0 && this.isPasswordMatched() && this.isTosAccepted();
        }

        private void updatePasswordInputStatus() {
            if(this.isPasswordMatched()) {
                PasswordSignUpPage.this.passwordInput2.setBackgroundResource(drawable.identitytoolkit_text_input_bg);
            } else {
                PasswordSignUpPage.this.passwordInput2.setBackgroundResource(drawable.identitytoolkit_text_input_bg_invalid);
            }

        }

        private void updateButtonStatus() {
            PasswordSignUpPage.this.nextButton.setEnabled(this.checkInputData());
        }

        private boolean isPasswordMatched() {
            return PasswordSignUpPage.this.passwordInput.getText().toString().equals(PasswordSignUpPage.this.passwordInput2.getText().toString());
        }

        private boolean isTosAccepted() {
            return PasswordSignUpPage.this.tosCheckBox.getVisibility() != View.VISIBLE || PasswordSignUpPage.this.tosCheckBox.isChecked();
        }
    }
}
