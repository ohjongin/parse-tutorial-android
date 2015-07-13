package me.ji5.parsetutorial.utils;

import android.content.Context;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

/**
 * Created by yeojoy on 15. 7. 13..
 */
public class CommonUtils {

    public static void hideSoftKeyboard(Context context, EditText et) {
        InputMethodManager imm = (InputMethodManager)
                context.getSystemService(Context.INPUT_METHOD_SERVICE);
        //txtName is a reference of an EditText Field
        imm.hideSoftInputFromWindow(et.getWindowToken(), 0);
    }
}
