package org.peterkwan.udacity.popularmovies.utils;

import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Locale;

public final class StringUtils {

    private static final String LOG_TAG = StringUtils.class.getSimpleName();

    public static boolean isEmpty(String str) {
        return str == null || str.isEmpty();
    }

    public static String convertDateFormat(String dateString, String fromDateFormat, String toDateFormat) {
        SimpleDateFormat fromFormat = new SimpleDateFormat(fromDateFormat, Locale.getDefault());
        SimpleDateFormat toFormat = new SimpleDateFormat(toDateFormat, Locale.getDefault());

        try {
            return toFormat.format(fromFormat.parse(dateString));
        } catch (ParseException e) {
            Log.e(LOG_TAG, "Error parsing data", e);
            return null;
        }
    }
}
