package com.example.hyeseung.hiddenfolderui;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;

import com.example.hyeseung.facerecognition.R;

/**
 * Created by Woon-hee on 2016-05-27.
 */
public class PinDataPage extends Page {
    private int pincode;
    private PinDataPage.DataChecker dataChecker;
    private EditText pincodeInput;
    private EditText pincodeInput2;
    private Button nextButton;
    private String email;
    private Context  mContext;

    public PinDataPage(){

    }

    public Page init(DefaultUiManager uiManager, String email) {
        this.email = email;
        return this.init(uiManager);
    }

    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        mContext = dialog.getContext();
        // dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.pindatapage);
        // dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);


        return dialog;
    }

    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        final Dialog dialog = this.getDialog();




        this.dataChecker = new PinDataPage.DataChecker();

        this.pincodeInput = (EditText)dialog.findViewById(R.id.pincode_password);
        this.pincodeInput.setTypeface(Typeface.DEFAULT);
        this.pincodeInput.addTextChangedListener(this.dataChecker);
        this.pincodeInput2 = (EditText)dialog.findViewById(R.id.pincode_confirm);
        this.pincodeInput2.setTypeface(Typeface.DEFAULT);
        this.pincodeInput2.addTextChangedListener(this.dataChecker);

        this.nextButton = (Button)dialog.findViewById(R.id.pincode_next);
        this.nextButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                SharedPreferences pref = mContext.getSharedPreferences(email,mContext.MODE_PRIVATE);
                SharedPreferences.Editor editor = pref.edit();
                editor.putString("pin",pincodeInput.getText().toString());
                editor.putInt("exp",1);
                editor.commit();



                Intent intent = new Intent(dialog.getOwnerActivity(), com.example.hyeseung.facerecognition.FdActivity.class);
                intent.setPackage("com.google.identitytoolkit.demo");
                intent.putExtra("userEmail", email);
                startActivity(intent);


            }
        });
    }
    @Override
    public void onDismiss(DialogInterface dialog) {

        super.onDismiss(dialog);
        PinDataPage.this.uiManager.showStartSignIn(null);

    }
    private class DataChecker extends TextChangedListener implements CompoundButton.OnCheckedChangeListener {
        private DataChecker() {
        }

        public void onTextChanged(CharSequence s, int start, int before, int count) {
            this.updatePinInputStatus();
            this.updateButtonStatus();
        }

        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            this.updatePinInputStatus();
            this.updateButtonStatus();
        }

        public boolean checkInputData() {
            return PinDataPage.this.pincodeInput.getText().length() != 0 && this.isPinMatched();
        }

        private void updatePinInputStatus() {
            if(this.isPinMatched()) {
                PinDataPage.this.pincodeInput2.setBackgroundResource(R.drawable.identitytoolkit_text_input_bg);
            } else {
                PinDataPage.this.pincodeInput2.setBackgroundResource(R.drawable.identitytoolkit_text_input_bg_invalid);
            }

        }

        private void updateButtonStatus() {
            PinDataPage.this.nextButton.setEnabled(this.checkInputData());
        }

        private boolean isPinMatched() {
            return PinDataPage.this.pincodeInput.getText().toString().equals(PinDataPage.this.pincodeInput2.getText().toString());
        }

    }

}
