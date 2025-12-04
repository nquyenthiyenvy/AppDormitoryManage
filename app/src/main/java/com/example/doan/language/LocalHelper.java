package com.example.doan.language;

import android.content.Context;
import android.content.res.Configuration;

import java.util.Locale;

public class LocalHelper {
    public static void setLocale(Context context, String lang) {
        Locale locale = new Locale(lang);
        Locale.setDefault(locale);

        Configuration config = new Configuration();
        config.setLocale(locale);

        context.getResources().updateConfiguration(
                config,
                context.getResources().getDisplayMetrics()
        );
    }
}
