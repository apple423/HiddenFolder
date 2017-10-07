//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.example.hyeseung.hiddenfolderui;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.util.Patterns;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;

import com.example.hyeseung.facerecognition.R;
import com.google.identitytoolkit.IdProvider;
import com.google.identitytoolkit.UiManager;
import com.google.identitytoolkit.UiManager.StartSignInRequest;


public class StartSignInPage extends Page {
    private boolean chkSignUp = false;

    public StartSignInPage() {
    }

    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
       // dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.signinpage);
       // dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);


        return dialog;
    }

    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Dialog dialog = this.getDialog();
        this.initEmailInputBox(dialog);
        chooseIdprovider(dialog);
        signup(dialog);
        //forgetPassword(dialog);
        // this.initIdpButtons(dialog);
    }

    private void initEmailInputBox(Dialog dialog) {
        final EditText emailInput = (EditText) dialog.findViewById(R.id.sign_in_email);
        emailInput.setTypeface(Typeface.DEFAULT);
        emailInput.requestFocus();
        final EditText passwordInput = (EditText) dialog.findViewById(R.id.sign_in_passward);
        passwordInput.setTypeface(Typeface.DEFAULT);
       // passwordInput.requestFocus();
        final Button nextButton = (Button) dialog.findViewById(R.id.sign_in_login);

        emailInput.addTextChangedListener(new TextChangedListener() {
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                if (Patterns.EMAIL_ADDRESS.matcher(s).matches()) {
                    passwordInput.addTextChangedListener(new TextChangedListener() {
                        public void onTextChanged(CharSequence s, int start, int before, int count) {
                            passwordInput.setBackgroundResource(com.google.identitytoolkit.R.drawable.identitytoolkit_text_input_bg);
                            if (s.length() == 0) {
                                nextButton.setEnabled(false);
                            } else {
                                nextButton.setEnabled(true);
                            }

                        }
                    });
                    //nextButton.setEnabled(true);
                } else {
                    nextButton.setEnabled(false);
                }

            }
        });
        emailInput.setOnEditorActionListener(new OnEditorActionListener() {
            public boolean onEditorAction(TextView v, int actionId, KeyEvent keyEvent) {
                String email = emailInput.getText().toString();
                String password = passwordInput.getText().toString();
                if (actionId == 6 && Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    //StartSignInPage.this.uiManager.getRequestHandler().handle((new StartSignInRequest()).setEmail(email));
                    StartSignInPage.this.uiManager.getRequestHandler().handle((new UiManager.SignInWithPasswordRequest()).setEmail(email).setPassword(password));
                }


                return false;
            }
        });
        nextButton.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                String email = emailInput.getText().toString();
                String password = passwordInput.getText().toString();
                // StartSignInPage.this.uiManager.getRequestHandler().handle((new StartSignInRequest()).setEmail(email));
                StartSignInPage.this.uiManager.getRequestHandler().handle((new UiManager.SignInWithPasswordRequest()).setEmail(email).setPassword(password));
            }
        });
    }
/*
    private void initIdpButtons(Dialog dialog) {
        View separator = dialog.findViewById(id.identitytoolkit_or_separator);
        ViewGroup idpButtons = (ViewGroup)dialog.findViewById(id.identitytoolkit_idp_buttons);
        if(this.uiManager.getPreferredIdProviders().isEmpty()) {
            separator.setVisibility(View.GONE);
            idpButtons.setVisibility(View.GONE);
        } else {
            IdpButtons.show(this.getActivity().getLayoutInflater(), this.getResources(), idpButtons, this.uiManager.getPreferredIdProviders(), new IdpButtons.OnIdpButtonClickListener() {
                public void onIdpButtonClick(IdProvider idProvider) {
                    StartSignInPage.this.uiManager.getRequestHandler().handle((new StartSignInRequest()).setIdProvider(idProvider));
                }
            });
        }

    }*/


    private void chooseIdprovider(Dialog dialog) {
        Button google = (Button) dialog.findViewById(R.id.sign_in_google);
        Button facebook = (Button) dialog.findViewById(R.id.sign_in_facebook);
        google.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                StartSignInPage.this.uiManager.getRequestHandler().handle((new StartSignInRequest()).setIdProvider(IdProvider.GOOGLE));
            }
        });
        facebook.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                StartSignInPage.this.uiManager.getRequestHandler().handle((new StartSignInRequest()).setIdProvider(IdProvider.FACEBOOK));
            }
        });
    }

    private void signup(Dialog dialog) {

        TextView signup = (TextView) dialog.findViewById(R.id.sign_in_reg);

        signup.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                chkSignUp = true;
                StartSignInPage.this.uiManager.showPasswordSignUp("apple423aa@naver.com");
            }
        });


    }

    public void showPasswordError() {
        EditText passwordInput = (EditText)this.getDialog().findViewById(R.id.sign_in_passward);
        passwordInput.setText("");
        passwordInput.setBackgroundResource(com.google.identitytoolkit.R.drawable.identitytoolkit_text_input_bg_invalid);
        Activity context = this.getActivity();
        Toast.makeText(context, "비밀번호를 확인해 주세요", Toast.LENGTH_LONG).show();
    }

    public void forgetPassword(Dialog dialog) {
        TextView forgotLink = (TextView) dialog.findViewById(R.id.sign_in_forgot_password);
        forgotLink.setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                StartSignInPage.this.uiManager.showPasswordRecovery();
            }
        });
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        //super.onDismiss(dialog);

        if(!chkSignUp) {
            new AlertDialog.Builder(StartSignInPage.this.getActivity())
                    .setTitle("종료하시겠습니까?")
                    .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            ActivityCompat.finishAffinity(StartSignInPage.this.getActivity());
                        }
                    })
                    .setNegativeButton("취소", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {

                            StartSignInPage.this.uiManager.showStartSignIn(null);

                        }
                    }).show();
        }

    }

}

