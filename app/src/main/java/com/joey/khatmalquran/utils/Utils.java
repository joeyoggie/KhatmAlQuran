package com.joey.khatmalquran.utils;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Build;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.Toast;

import androidx.fragment.app.FragmentTransaction;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;
import com.joey.khatmalquran.R;

import java.io.IOException;
import java.net.InetAddress;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class Utils {
    private static final String TAG = Utils.class.getSimpleName();
    public static final int ANIMATION_TYPE_TRANSLATION = 0;
    public static final int ANIMATION_TYPE_FADE = 1;

    private static CustomProgressDialog customProgressDialog;

    public static FragmentTransaction setAnimations(FragmentTransaction originalFragmentTransaction, int animationType){
        switch (animationType){
            case ANIMATION_TYPE_TRANSLATION:
                originalFragmentTransaction.setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left, R.anim.enter_from_left, R.anim.exit_to_right);
                return originalFragmentTransaction;
            case ANIMATION_TYPE_FADE:
                originalFragmentTransaction.setCustomAnimations(R.anim.fade_in, R.anim.fade_out, R.anim.fade_in, R.anim.fade_out);
                return originalFragmentTransaction;

            default:
                return originalFragmentTransaction;
        }
    }

    public static void setActivityEnterAnimations(Activity activity){
        if(MySettings.getActiveLanguage().equals("en")){
            activity.overridePendingTransition(R.anim.enter_from_right, R.anim.exit_to_left);
        }else if(MySettings.getActiveLanguage().equals("ar")){
            activity.overridePendingTransition(R.anim.enter_from_left, R.anim.exit_to_right);
        }
    }

    public static void setActivityExitAnimations(Activity activity){
        if(MySettings.getActiveLanguage().equals("en")){
            activity.overridePendingTransition(R.anim.enter_from_left, R.anim.exit_to_right);
        }else if(MySettings.getActiveLanguage().equals("ar")){
            activity.overridePendingTransition(R.anim.enter_from_right, R.anim.exit_to_left);
        }
    }

    public static void setGridViewHeightBasedOnChildren(GridView gridView, int columns) {
        ListAdapter listAdapter = gridView.getAdapter();
        if (listAdapter == null) {
            // pre-condition
            return;
        }

        int totalHeight = 0;
        int items = listAdapter.getCount();
        int rows = 0;

        View listItem = listAdapter.getView(0, null, gridView);
        listItem.measure(0, 0);
        totalHeight = listItem.getMeasuredHeight();

        float x = 1;
        if( items > columns ){
            x = items/columns;
            rows = (int) (x + 1);
            totalHeight *= rows;
        }

        ViewGroup.LayoutParams params = gridView.getLayoutParams();
        params.height = totalHeight;
        gridView.setLayoutParams(params);
    }

    public static void justifyListViewHeightBasedOnChildren(ListView listView) {
        ListAdapter adapter = listView.getAdapter();
        if (adapter == null) {
            return;
        }

        ViewGroup vg = listView;
        int totalHeight = 0;
        for (int i = 0; i < adapter.getCount(); i++) {
            View listItem = adapter.getView(i, null, vg);
            listItem.measure(0, 0);
            totalHeight += listItem.getMeasuredHeight();
        }

        ViewGroup.LayoutParams par = listView.getLayoutParams();
        par.height = (int)(totalHeight + (listView.getDividerHeight() * (adapter.getCount() - 1)));
        listView.setLayoutParams(par);
        listView.requestLayout();
    }

    public static void justifyListViewHeightBasedOnChildren2(ListView listView) {
        ListAdapter adapter = listView.getAdapter();
        if (adapter == null) {
            return;
        }

        ViewGroup vg = listView;
        int totalHeight = 0;
        for (int i = 0; i < adapter.getCount(); i++) {
            View listItem = adapter.getView(i, null, vg);
            listItem.measure(0, 0);
            totalHeight += listItem.getMeasuredHeight();
        }

        ViewGroup.LayoutParams par = listView.getLayoutParams();
        par.height = (int)(1.5*totalHeight + (listView.getDividerHeight() * (adapter.getCount() - 1)));
        listView.setLayoutParams(par);
        listView.requestLayout();
    }

    public static void setGridViewWidthBasedOnChildren(GridView gridView) {
        ListAdapter gridViewAdapter = gridView.getAdapter();
        if (gridViewAdapter == null) {
            return;
        }

        ViewGroup vg = gridView;
        int totalWidth = 0;
        for (int i = 0; i < gridViewAdapter.getCount(); i++) {
            View gridItem = gridViewAdapter.getView(i, null, vg);
            gridItem.measure(0, 0);
            totalWidth += gridItem.getMeasuredHeight(); //Hack: use getMeasuredHeight instead of getMeasuredWidth as each grid item is square anyway, becauase of ScrollingTextviews variable width
        }

        ViewGroup.LayoutParams params = gridView.getLayoutParams();
        params.width = totalWidth;
        gridView.setLayoutParams(params);
    }

    public static String getDateString(long timestamp){
        String dateString = "";
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(timestamp);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        int month = calendar.get(Calendar.MONTH) + 1;
        int year = calendar.get(Calendar.YEAR);
        dateString = day + "/" + month + "/" + year;

        return dateString;
    }

    private static SimpleDateFormat simpleDateFormatDateHoursMinute;
    public static String getTimeStringDateHoursMinutes(long timestamp){
        String timeString = "";
        /*Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(timestamp);
        int hour = calendar.get(Calendar.HOUR);
        int minute = calendar.get(Calendar.MINUTE);
        int second = calendar.get(Calendar.SECOND);
        String amPM = "";
        if(calendar.get(Calendar.AM_PM) == Calendar.PM){
            amPM = "PM";
        }else{
            amPM = "AM";
        }
        timeString = hour + ":" + minute + ":" + second + " " + amPM;*/
        if(simpleDateFormatDateHoursMinute == null) {
            simpleDateFormatDateHoursMinute = new SimpleDateFormat("dd/MM/yy  h:mm a");
        }
        timeString = simpleDateFormatDateHoursMinute.format(timestamp);
        return timeString;
    }

    private static SimpleDateFormat simpleDateFormatHoursMinute;
    public static String getTimeStringHoursMinutes(long timestamp){
        String timeString = "";
        /*Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(timestamp);
        int hour = calendar.get(Calendar.HOUR);
        int minute = calendar.get(Calendar.MINUTE);
        int second = calendar.get(Calendar.SECOND);
        String amPM = "";
        if(calendar.get(Calendar.AM_PM) == Calendar.PM){
            amPM = "PM";
        }else{
            amPM = "AM";
        }
        timeString = hour + ":" + minute + ":" + second + " " + amPM;*/
        if(simpleDateFormatHoursMinute == null) {
            simpleDateFormatHoursMinute = new SimpleDateFormat("h:mm a");
        }
        timeString = simpleDateFormatHoursMinute.format(timestamp);
        return timeString;
    }

    private static SimpleDateFormat simpleDateFormatServer; //2019-07-14T10:29:23Z
    public static long getTimestamp(String serverTimestamp){
        long timestamp = 0;
        if(simpleDateFormatServer == null) {
            simpleDateFormatServer = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH);
        }
        try{
            Date date = simpleDateFormatServer.parse(serverTimestamp);
            timestamp = date.getTime();
        }catch (ParseException e){
            Log.d(TAG, "Exception: " + e.getMessage());
        }

        return timestamp;
    }

    public static boolean validateInputs(EditText... editTexts){
        boolean inputsValid = true;

        for (EditText editText:editTexts) {
            if(editText == null || editText.getText().toString() == null || editText.getText().toString().length() < 1){
                inputsValid = false;
                YoYo.with(Techniques.Shake)
                        .duration(700)
                        .repeat(1)
                        .playOn(editText);
            }
        }

        return inputsValid;
    }

    public static boolean validateInputsWithoutYoyo(EditText... editTexts){
        boolean inputsValid = true;

        for (EditText editText:editTexts) {
            if(editText == null || editText.getText().toString() == null || editText.getText().toString().length() < 1){
                inputsValid = false;
            }
        }

        return inputsValid;
    }

    public static class InternetChecker extends AsyncTask<Void, Void, Boolean> {

        private OnConnectionCallback onConnectionCallback;
        private Context context;

        public InternetChecker(Context context, OnConnectionCallback onConnectionCallback) {
            super();
            this.onConnectionCallback = onConnectionCallback;
            this.context = context;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            if (context == null)
                return false;

            try {
                InetAddress ipAddr = InetAddress.getByName("google.com");
                //You can replace it with your name
                return !ipAddr.equals("");

            } catch (Exception e) {
                Utils.log(TAG, "Exception: " + e.getMessage());
                return false;
            }
        }

        @Override
        protected void onPostExecute(Boolean b) {
            super.onPostExecute(b);

            if (b) {
                onConnectionCallback.onConnectionSuccess();
            } else {
                String msg = "No Internet Connection";
                if (context == null)
                    msg = "Context is null";
                onConnectionCallback.onConnectionFail(msg);
            }

        }

        public interface OnConnectionCallback {
            void onConnectionSuccess();
            void onConnectionFail(String errorMsg);
        }
    }

    public static void showLoading(Context context){
        customProgressDialog = CustomProgressDialog.show(context, "", "");
    }

    public static void dismissLoading(){
        if(customProgressDialog != null && customProgressDialog.isShowing()){
            customProgressDialog.dismiss();
        }
    }

    public static void showToast(Context context, String text, boolean longDuration){
        if(context != null){
            if(longDuration){
                Toast.makeText(context, text, Toast.LENGTH_LONG).show();
            }else{
                Toast.makeText(context, text, Toast.LENGTH_SHORT).show();
            }
        }
    }

    public static String getString(Context context, int resID){
        if(context != null){
            return context.getResources().getString(resID);
        }else {
            return "";
        }
    }

    public static String getStringExtraInt(Context context, int resID, Integer... params){
        if(context != null){
            if(params != null){
                int length = params.length;
                switch (length){
                    case 1:
                        return context.getResources().getString(resID, params[0]);
                    case 2:
                        return context.getResources().getString(resID, params[0], params[1]);
                    case 3:
                        return context.getResources().getString(resID, params[0], params[1], params[2]);
                    case 4:
                        return context.getResources().getString(resID, params[0], params[1], params[2], params[3]);
                    default:
                        return context.getResources().getString(resID);
                }
            }else{
                return context.getResources().getString(resID);
            }
        }else {
            return "";
        }
    }

    public static String getStringExtraText(Context context, int resID, String... params){
        if(context != null){
            if(params != null){
                int length = params.length;
                switch (length){
                    case 1:
                        return context.getResources().getString(resID, params[0]);
                    case 2:
                        return context.getResources().getString(resID, params[0], params[1]);
                    case 3:
                        return context.getResources().getString(resID, params[0], params[1], params[2]);
                    case 4:
                        return context.getResources().getString(resID, params[0], params[1], params[2], params[3]);
                    default:
                        return context.getResources().getString(resID);
                }
            }else{
                return context.getResources().getString(resID);
            }
        }else {
            return "";
        }
    }

    public static void log(String tag, String message){
        Log.d(tag, message);
    }

    public static void hideKeyboard(View view, Context context){
        if (view != null) {
            //view.clearFocus();
            InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            //imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
        }
    }

    public static String getDeviceInfo(Context context){
        String deviceInfo = "";
        if(context != null){
            /*String versionName = Build.VERSION_CODES.class.getFields()[Build.VERSION.SDK_INT + 1].getName();
            String[] versionNames = new String[]{
                    "ANDROID BASE", "ANDROID BASE 1.1", "CUPCAKE", "DONUT",
                    "ECLAIR", "ECLAIR_0_1", "ECLAIR_MR1", "FROYO", "GINGERBREAD",
                    "GINGERBREAD_MR1", "HONEYCOMB", "HONEYCOMB_MR1", "HONEYCOMB_MR2",
                    "ICE_CREAM_SANDWICH", "ICE_CREAM_SANDWICH_MR1",
                    "JELLY_BEAN", "JELLY_BEAN_MR1", "JELLY_BEAN_MR2", "KITKAT", "KITKAT_WATCH",
                    "LOLLIPOP", "LOLLIPOP_MR1", "MARSHMALLOW", "NOUGAT", "OREO", "OREO_MR1"
            };
            int nameIndex = Build.VERSION.SDK_INT - 1;
            if (nameIndex < versionNames.length) {
                versionName = versionNames[nameIndex];
            }*/

            deviceInfo = deviceInfo.concat("Manufacturer: " + Build.MANUFACTURER + "\n");
            deviceInfo = deviceInfo.concat("Brand: " + Build.BRAND + "\n");
            deviceInfo = deviceInfo.concat("Model: " + android.os.Build.MODEL + "\n");
            //deviceInfo = deviceInfo.concat("Android version: " + versionName  + " (" + Build.VERSION.RELEASE + ")" + "\n");
            deviceInfo = deviceInfo.concat("SDK v" + Build.VERSION.SDK_INT);
        }

        return deviceInfo;
    }

    public static Gson getGson(){
        return new GsonBuilder()
                .registerTypeAdapter(Boolean.class, booleanAsIntAdapter)
                .registerTypeAdapter(boolean.class, booleanAsIntAdapter)
                .create();
    }
    private static final TypeAdapter<Boolean> booleanAsIntAdapter = new TypeAdapter<Boolean>() {
        @Override public void write(JsonWriter out, Boolean value) throws IOException {
            if (value == null) {
                out.nullValue();
            } else {
                out.value(value);
            }
        }
        @Override public Boolean read(JsonReader in) throws IOException {
            JsonToken peek = in.peek();
            switch (peek) {
                case BOOLEAN:
                    return in.nextBoolean();
                case NULL:
                    in.nextNull();
                    return null;
                case NUMBER:
                    return in.nextInt() != 0;
                case STRING:
                    return Boolean.parseBoolean(in.nextString());
                default:
                    throw new IllegalStateException("Expected BOOLEAN or NUMBER but was " + peek);
            }
        }
    };
}
