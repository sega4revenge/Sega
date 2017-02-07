package com.sega.vimarket.color;

import android.app.ActivityManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.WindowManager;

import com.sega.vimarket.model.Utils;

import org.jetbrains.annotations.Nullable;

public abstract class CActivity extends AppCompatActivity {
    private String themeString;



    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        themeString=Colorful.getThemeString();
        setTheme(Colorful.getThemeDelegate().getStyle());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            if (Colorful.getThemeDelegate().isTranslucent()) {
                getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            }

            ActivityManager.TaskDescription tDesc = new ActivityManager.TaskDescription(null, null, getResources().getColor(Colorful.getThemeDelegate().getPrimaryColor().getColorRes()));
            setTaskDescription(tDesc);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!Colorful.getThemeString().equals(themeString)) {
            Log.d(Utils.LOG_TAG, "Theme change detected, restarting activity");
            recreate();
        }
    }


}
