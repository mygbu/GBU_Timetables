package com.varun.gbu_timetables;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.support.v4.content.ContextCompat;
import android.util.DisplayMetrics;
import android.util.Log;

import com.varun.gbu_timetables.data.database.TimetableContract;

/**
 * com.varun.gbu_timetables (Timetables_sql)
 * Created by Varun garg <varun.10@live.com> on 1/9/2016 11:24 AM.
 */
public class Utility {

    public static String getSchool(String src) {
        if (src.equalsIgnoreCase("SOICT"))
            return "Information and Communication  Technology";
        else if (src.equalsIgnoreCase("SOVSAS"))
            return "Vocational Studies And Applied Sciences";
        else if (src.equalsIgnoreCase("SOBT"))
            return "Biotechnology";
        else if (src.equalsIgnoreCase("SOE"))
            return "Engineering";
        else if (src.equalsIgnoreCase("SOM"))
            return "Management";
        else if (src.equalsIgnoreCase("SOLJ"))
            return "Law, Justice and Governance";
        else if (src.equalsIgnoreCase("SOBSC"))
            return "Buddhist Studies And Civilization";
        else if (src.equalsIgnoreCase("SOHSS"))
            return "Humanities and Social Sciences";
        else
            return src;
    }

    public static int getPeriodTitleNo(int Period_no) {
        //for period 1, we have 8 (8:30)
        Period_no += 7;
        if (Period_no > 12) //12 hr clock
            Period_no -= 12;
        return Period_no;
    }

    public static String getFullSectionName(String SectionCode, Context context) {
        String splitted[] = SectionCode.split("-");
        String Year = null;
        if (splitted.length >= 2)
            Year = " (" + splitted[1] + ")";

        Uri uri = TimetableContract.BuildFullSectionName(splitted[0]);
        Cursor cursor = context.getContentResolver().query(uri, null, null, null, null);

        if (!(cursor.moveToFirst()) || cursor.getCount() == 0) {
            cursor.close();
            return SectionCode;
        }

        try {

            cursor.moveToFirst();
            String SectionFullName = cursor.getString(cursor.getColumnIndex("Name"));
            cursor.close();
            return SectionFullName + Year;
        } catch (Exception e) {
            Log.d(Utility.class.getSimpleName(), e.toString());
            Log.d(Utility.class.getSimpleName(), "Dump :" + DatabaseUtils.dumpCursorToString(cursor));
            cursor.close();
            return SectionCode;
        }
    }

    /**
     * This method converts dp unit to equivalent pixels, depending on device density.
     *
     * @param dp      A value in dp (density independent pixels) unit. Which we need to convert into pixels
     * @param context Context to get resources and device specific display metrics
     * @return A float value to represent px equivalent to dp depending on device density
     */
    public static int convertDpToPixel(int dp, Context context) {
        Resources resources = context.getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        int px = dp * (metrics.densityDpi / DisplayMetrics.DENSITY_DEFAULT);
        return px;
    }

    /**
     * This method converts device specific pixels to density independent pixels.
     *
     * @param px      A value in px (pixels) unit. Which we need to convert into db
     * @param context Context to get resources and device specific display metrics
     * @return A float value t1o represent dp equivalent to px value
     */
    public static float convertPixelsToDp(float px, Context context) {
        Resources resources = context.getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        float dp = px / ((float) metrics.densityDpi / DisplayMetrics.DENSITY_DEFAULT);
        return dp;
    }

    public static void setFirebaseInstanceId(Context context, String InstanceId) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor;
        editor = sharedPreferences.edit();
        editor.putString(context.getString(R.string.pref_firebase_instance_id_key),InstanceId);
        editor.apply();
    }

    public static String getFirebaseInstanceId(Context context) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        String key = context.getString(R.string.pref_firebase_instance_id_key);
        String default_value = context.getString(R.string.pref_firebase_instance_id_default_key);
        return sharedPreferences.getString(key, default_value);
    }

    // copy text to clipboard
    public static void setClipboard(Context context,String text) {
        if(android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.HONEYCOMB) {
            android.text.ClipboardManager clipboard = (android.text.ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
            clipboard.setText(text);
        } else {
            android.content.ClipboardManager clipboard = (android.content.ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
            android.content.ClipData clip = android.content.ClipData.newPlainText("Copied Text", text);
            clipboard.setPrimaryClip(clip);
        }
    }

    public static class ThemeTools {

        public static int getThemeId(Context context) {
            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);

            String theme = sharedPreferences.getString(context.getString(R.string.pref_theme_key), "0");
            if (theme.equals("0"))
                return R.style.LightTheme;
            else
                return R.style.DarkTheme;
        }

        public static int getMarginDrawable(Context context) {
            int theme_id = getThemeId(context);
            if (theme_id == R.style.LightTheme)
                return R.drawable.margin_light;
            else
                return R.drawable.margin_dark;
        }

        public static int getDialogThemeId(Context context) {
            int theme_id = getThemeId(context);
            if (theme_id == R.style.LightTheme) {
                if (android.os.Build.VERSION.SDK_INT >= 21) {
                    return android.R.style.Theme_Material_Light_Dialog;
                } else {
                    return ProgressDialog.THEME_HOLO_LIGHT;
                }
            } else if (android.os.Build.VERSION.SDK_INT >= 21) {
                return android.R.style.Theme_Material_Dialog;
            } else {
                return ProgressDialog.THEME_HOLO_DARK;
            }
        }

        public static Drawable getListGroupIconInverseDrawable(Context context) {
            int theme_id = getThemeId(context);
            if (theme_id == R.style.LightTheme)
                return ContextCompat.getDrawable(context, R.drawable.ic_school_black_24dp);
            else
                return ContextCompat.getDrawable(context, R.drawable.ic_school_white_24dp);
        }

        public static class FavouriteIcon {
            public static Drawable getFavYes(Context context) {
                int theme_id = getThemeId(context);
                if (theme_id == R.style.LightTheme)
                    return ContextCompat.getDrawable(context, R.drawable.ic_favorite_white_24dp);
                else
                    return ContextCompat.getDrawable(context, R.drawable.ic_favorite_black_24dp);
            }

            public static Drawable getFavYesInverse(Context context) {
                int theme_id = getThemeId(context);
                if (theme_id == R.style.LightTheme)
                    return ContextCompat.getDrawable(context, R.drawable.ic_favorite_black_24dp);
                else
                    return ContextCompat.getDrawable(context, R.drawable.ic_favorite_white_24dp);
            }

            public static Drawable getFavNo(Context context) {
                int theme_id = getThemeId(context);
                if (theme_id == R.style.LightTheme)
                    return ContextCompat.getDrawable(context, R.drawable.ic_favorite_border_white_24dp);
                else
                    return ContextCompat.getDrawable(context, R.drawable.ic_favorite_border_black_24dp);
            }
        }

        public static class BackgroundIcons {

            public static int getBgBoxDefaultDrawable(Context context) {
                int theme_id = getThemeId(context);
                if (theme_id == R.style.LightTheme)
                    return R.drawable.bg_box_default_light;
                else
                    return R.drawable.bg_box_default_dark;
            }

            public static int getBgBoxPinkDrawable(Context context) {

                int theme_id = getThemeId(context);
                if (theme_id == R.style.LightTheme)
                    return R.drawable.bg_box_pink_light;
                else
                    return R.drawable.bg_box_pink_dark;
            }

            public static int getBgBoxGreenDrawable(Context context) {

                int theme_id = getThemeId(context);
                if (theme_id == R.style.LightTheme)
                    return R.drawable.bg_box_green_light;
                else
                    return R.drawable.bg_box_green_dark;
            }
        }
    }


}
