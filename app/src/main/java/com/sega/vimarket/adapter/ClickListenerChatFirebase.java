package com.sega.vimarket.adapter;

import android.view.View;

/**a
 * Created by Sega on 04/01/2017.
 */

public interface ClickListenerChatFirebase {


    void clickImageChat(View view, int position, String nameUser, String urlPhotoUser, String urlPhotoClick);


    void clickImageMapChat(View view, int position,String latitude,String longitude);

}
