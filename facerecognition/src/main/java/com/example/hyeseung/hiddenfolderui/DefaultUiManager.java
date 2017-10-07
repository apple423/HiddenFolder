//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.example.hyeseung.hiddenfolderui;

import android.app.Activity;
import android.content.Context;
import android.widget.Toast;

import com.google.identitytoolkit.GitkitUser.UserProfile;
import com.google.identitytoolkit.IdProvider;
import com.google.identitytoolkit.R.string;

import java.util.Collections;

public class DefaultUiManager extends FragmentUiManager {
    public static final String CURRENT_SHOWN_PAGE_TAG = "gitkit_current_shown_page";
    //private final List<IdProvider> preferredIdProviders = null;
    private final String passwordRecoveryUrl;
    private final String tosUrl;
    private RequestHandler requestHandler;
    private final Activity activity;





  /*  public DefaultUiManager(Activity activity, Collection<IdProvider> preferredIdProviders, String passwordRecoveryUrl, String tosUrl) {
        super(activity);
        this.activity = activity;
        this.preferredIdProviders = Collections.unmodifiableList(new ArrayList<>(preferredIdProviders));
        this.passwordRecoveryUrl = passwordRecoveryUrl;
        this.tosUrl = tosUrl;
    }*/

    public DefaultUiManager(Activity activity,  String passwordRecoveryUrl, String tosUrl) {
        super(activity);
        this.activity = activity;

        this.passwordRecoveryUrl = passwordRecoveryUrl;
        this.tosUrl = tosUrl;
    }



    public void setRequestHandler(RequestHandler requestHandler) {
        this.requestHandler = requestHandler;
        Page page = this.getCurrentPage();
        if(page != null) {
            page.init(this);
        }

    }

    public RequestHandler getRequestHandler() {
        return this.requestHandler;
    }

    public void showStartSignIn(UserProfile lastUserProfile) {
        /*if(lastUserProfile != null) {
            this.show((new AccountChipSignInPage()).init(this, lastUserProfile));
        } else {*/
            this.show((new StartSignInPage()).init(this));
        //}

    }

    public void showPasswordSignIn(String email) {
        this.show((new PasswordSignInPage()).init(this, email));
    }

    public void showPasswordSignUp(String email) {
        this.show((new PasswordSignUpPage()).init(this, email));
    }

    /*public void showPasswordSignUp2(){

        this.show((new PasswordSignUpPage()).init(this));

    }*/

    public void showPasswordAccountLinking(String email, IdProvider currentIdProvider) {
        this.show((new PasswordAccountLinkingPage()).init(this, email, currentIdProvider));
    }

    public void showIdpAccountLinking(String email, IdProvider previousIdProvider, IdProvider currentIdProvider) {
        this.show((new IdpAccountLinkingPage()).init(this, email, previousIdProvider, currentIdProvider));
    }

    public void handleError(ErrorCode code, Object... optArgs) {
        //switch(DefaultUiManager.SyntheticClass_1.$SwitchMap$com$google$identitytoolkit$UiManager$ErrorCode[code.ordinal()])
        switch (code){
            case INVALID_PASSWORD:
            this.handleInvalidPassword();
            break;
            case UNKNOWN_CODE:
            this.handleUnknownCode(optArgs);
            break;
        default:
            throw new IllegalArgumentException("unrecognized error code: " + code);
        }

    }

    public void dismiss() {
        Page current = this.getCurrentPage();
       if(current != null) {
            current.dismiss();

        }


    }

    public void showPinDataPage(String email){

        this.show((new PinDataPage()).init(this,email));
    }


    public void showPasswordRecovery(String email) {
        String url = this.passwordRecoveryUrl + email;
        Page page = (new WebViewPage()).init(this, url, Collections.<String,String>emptyMap());
        page.setTitle(string.identitytoolkit_title_forgot_password);
        this.show(page);

    }

    public void showPasswordRecovery() {
        String url = this.passwordRecoveryUrl;
        Page page = (new WebViewPage()).init(this, url, Collections.<String,String>emptyMap());
        page.setTitle(string.identitytoolkit_title_forgot_password);
        this.show(page);

    }

    protected String getCurrentPageTag() {
        return "gitkit_current_shown_page";
    }

   /* List<IdProvider> getPreferredIdProviders() {
        return this.preferredIdProviders;
    }*/

    String getTosUrl() {
        return this.tosUrl;
    }

    private void handleInvalidPassword() {
        Page current = this.getCurrentPage();
        if(current instanceof StartSignInPage) {
            ((StartSignInPage)this.getCurrentPage()).showPasswordError();
        }
    }

    private void handleUnknownCode(Object... optArgs) {
        Context context = this.activity.getApplicationContext();
        String errorMessage = "";
        if(optArgs.length > 0 && optArgs[0] instanceof String) {
            errorMessage = (String)optArgs[0];
        }

        if(errorMessage.length() == 0) {
            errorMessage = context.getString(string.identitytoolkit_sign_up_default_error);
        }

        Toast.makeText(context, errorMessage, Toast.LENGTH_LONG).show();
    }
}
