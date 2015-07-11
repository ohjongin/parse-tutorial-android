package me.ji5.parsetutorial;

import android.app.Application;

import com.parse.Parse;
import com.parse.ParseACL;

/**
 * Describe about this class here...
 *
 * @author ohjongin
 * @since 1.0
 * 15. 7. 10
 */
public class ParseTutorialApplication extends Application {
    // valueup2015@gmail.com / valueup2015
    private static final String APPLICATION_ID = "XJSqb6GHk3kCpUilyhPcx9GixdY1NDsupEimnctx";
    private static final String CLIENT_KEY = "ooZMBNK9Qm1yXeX2VwLXzXtjFX6HEZDlqx58owIr";

    @Override
    public void onCreate() {
        super.onCreate();

        // Parse.enableLocalDatastore(this);
        Parse.initialize(this, APPLICATION_ID, CLIENT_KEY);
        ParseACL defaultACL = new ParseACL();
        defaultACL.setPublicReadAccess(true);
        defaultACL.setPublicWriteAccess(false);
        ParseACL.setDefaultACL(defaultACL, true);
    }
}