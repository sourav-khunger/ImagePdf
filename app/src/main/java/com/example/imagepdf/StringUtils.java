package com.example.imagepdf;

import android.app.Activity;
import android.os.Environment;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import java.util.Objects;

import static com.example.imagepdf.Constants.pdfDirectory;

/**
 * Created by anandparmar on 18/06/18.
 */

public class StringUtils {

    public static boolean isEmpty(CharSequence s) {
        return s == null || s.toString().trim().equals("");
    }

    public static boolean isNotEmpty(CharSequence s) {
        return s != null && !s.toString().trim().equals("");
    }


    public static void hideKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        //Find the currently focused view, so we can grab the correct window token from it.
        View view = activity.getCurrentFocus();
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = new View(activity);
        }
        if (imm != null)
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    public static String getDefaultStorageLocation() {
        return Environment.getExternalStorageDirectory().getAbsolutePath() +
                pdfDirectory;
    }
    public static StringBuilder trimExternal(String path) {
        StringBuilder trimmedPath = new StringBuilder();
        int tempPath = path.indexOf('/');
        trimmedPath.append(path.substring(tempPath + 1));
        return trimmedPath;
    }
}
