//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.example.hyeseung.hiddenfolderui;

import com.google.identitytoolkit.IdProvider;
import com.google.identitytoolkit.R.drawable;
import java.util.EnumMap;
import java.util.Map;

public final class IdpIcons {
    private static final Map<IdProvider, IdpIconResources> IDP_ICON_RESOURCES;

    private IdpIcons() {
    }

    public static int getIconFor(IdProvider idProvider) {
        return ((IdpIcons.IdpIconResources)IDP_ICON_RESOURCES.get(idProvider)).icon;
    }

    public static int getBackgroundFor(IdProvider idProvider) {
        return ((IdpIcons.IdpIconResources)IDP_ICON_RESOURCES.get(idProvider)).background;
    }

    static {
        EnumMap map = new EnumMap(IdProvider.class);
        IdProvider[] arr$ = IdProvider.values();
        int len$ = arr$.length;

        for(int i$ = 0; i$ < len$; ++i$) {
            IdProvider idProvider = arr$[i$];
            IdpIcons.IdpIconResources res;
            switch(idProvider.ordinal()) {
            case 1:
                res = new IdpIcons.IdpIconResources(drawable.identitytoolkit_idp_aol_icon, drawable.identitytoolkit_idp_aol_bg);
                map.put(idProvider, res);
                break;
            case 2:
                res = new IdpIcons.IdpIconResources(drawable.identitytoolkit_idp_facebook_icon, drawable.identitytoolkit_idp_facebook_bg);
                map.put(idProvider, res);
                break;
            case 3:
                res = new IdpIcons.IdpIconResources(drawable.identitytoolkit_idp_google_icon, drawable.identitytoolkit_idp_google_bg);
                map.put(idProvider, res);
                break;
            case 4:
                res = new IdpIcons.IdpIconResources(drawable.identitytoolkit_idp_microsoft_icon, drawable.identitytoolkit_idp_microsoft_bg);
                map.put(idProvider, res);
                break;
            case 5:
                res = new IdpIcons.IdpIconResources(drawable.identitytoolkit_idp_paypal_icon, drawable.identitytoolkit_idp_paypal_bg);
                map.put(idProvider, res);
                break;
            case 6:
                res = new IdpIcons.IdpIconResources(drawable.identitytoolkit_idp_yahoo_icon, drawable.identitytoolkit_idp_yahoo_bg);
                map.put(idProvider, res);
            }
        }

        IDP_ICON_RESOURCES = map;
    }

    private static class IdpIconResources {
        private final int icon;
        private final int background;

        private IdpIconResources(int icon, int background) {
            this.icon = icon;
            this.background = background;
        }
    }
}
