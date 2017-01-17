package com.sega.vimarket.activity;

/**a
 * Created by QynQyn on 11/1/2016.
 */

import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.view.Gravity;
import android.view.View;
import android.widget.Toast;

import com.sega.vimarket.R;
import com.sega.vimarket.color.CActivity;

import java.util.Calendar;

import mehdi.sakout.aboutpage.AboutPage;
import mehdi.sakout.aboutpage.Element;

public class IntroActivity extends CActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Element adsElement = new Element();
        adsElement.setTitle("Advertise with us");

        View aboutPage = new AboutPage(this)
                .isRTL(false)
                .setImage(R.drawable.logo)
                .addItem(new Element().setTitle("Version 1.0"))
                .addItem(adsElement)
                .addGroup("Connect with us")
                .addEmail("elmehdi.sakout@gmail.com")
                .addWebsite("http://medyo.github.io/")
                .addFacebook("the.medy")
                .addTwitter("medyo80")
                .addYoutube("UCdPQtdWIsg7_pi4mrRu46vA")
                .addPlayStore("com.ideashower.readitlater.pro")
                .addInstagram("medyo80")
                .addGitHub("medyo")
                .addItem(getCopyRightsElement())
                .create();

        setContentView(aboutPage);
    }


    Element getCopyRightsElement() {
        Element copyRightsElement = new Element();
        final String copyrights = String.format(getString(R.string.copy_right), Calendar.getInstance().get(Calendar.YEAR));
        copyRightsElement.setTitle(copyrights);
        copyRightsElement.setIcon(R.drawable.about_icon_copy_right);
        copyRightsElement.setColor(ContextCompat.getColor(this, mehdi.sakout.aboutpage.R.color.about_item_icon_color));
        copyRightsElement.setGravity(Gravity.CENTER);
        copyRightsElement.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(IntroActivity.this, copyrights, Toast.LENGTH_SHORT).show();
            }
        });
        return copyRightsElement;
    }
}