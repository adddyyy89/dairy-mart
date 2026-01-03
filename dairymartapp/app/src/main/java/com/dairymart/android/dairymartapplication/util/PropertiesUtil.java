package com.dairymart.android.dairymartapplication.util;

import android.content.Context;
import android.content.res.Resources;
import android.util.Log;

import com.dairymart.android.dairymartapplication.R;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class PropertiesUtil {

    public static Properties loadConfig(Context context) {
        Properties properties = new Properties();
        try {
            Resources resources = context.getResources();
            InputStream inputStream = resources.openRawResource(R.raw.config); // Assuming your file is named config.properties (Android automatically strips the extension in R.raw)
            properties.load(inputStream);
            inputStream.close();
        } catch (IOException e) {
            Log.e("PropertiesUtil", "Error loading config.properties", e);
        }
        return properties;
    }

    public static String getConfigValue(Context context, String key) {
        Properties properties = loadConfig(context);
        return properties.getProperty(key);
    }
}
