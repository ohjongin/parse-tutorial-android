package me.ji5.parsetutorial;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.parse.SignUpCallback;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Random;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    protected static final String TAG = "MainActivity";

    protected static final String USERNAME = "ohjongin";
    protected static final String PASSWORD = "1234567890";
    protected static final String EMAIL = "ohjongin@gmail.com";

    protected static final String COL_CONTENT = "content";
    protected static final String COL_USERNAME = "username";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.btn_signup).setOnClickListener(this);
        findViewById(R.id.btn_login).setOnClickListener(this);
        findViewById(R.id.btn_upload).setOnClickListener(this);
        findViewById(R.id.btn_query).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_signup:
                onSignUp();
                break;
            case R.id.btn_login:
                onLogin();
                break;
            case R.id.btn_upload:
                onUpload();
                break;
            case R.id.btn_query:
                onQuery();
                break;
        }
    }

    protected void onSignUp() {
        final ParseUser user = new ParseUser();

        user.setUsername(USERNAME);
        user.setPassword(PASSWORD);
        user.setEmail(EMAIL);
        user.signUpInBackground(new SignUpCallback() {
            @Override
            public void done(ParseException e) {
                if (e != null) {
                    if (BuildConfig.DEBUG) e.printStackTrace();
                    Toast.makeText(MainActivity.this, e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
                    return;
                }

                Toast.makeText(MainActivity.this, "Registered! - " + user.getUsername(), Toast.LENGTH_LONG).show();
            }
        });
    }

    protected void onLogin() {
        ParseUser.logInInBackground(USERNAME, PASSWORD, new LogInCallback() {
            @Override
            public void done(ParseUser parseUser, ParseException e) {
                if (e != null) {
                    if (BuildConfig.DEBUG) e.printStackTrace();
                    Toast.makeText(MainActivity.this, e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
                    return;
                }

                if (parseUser == null) {
                    Toast.makeText(MainActivity.this, "The user info is null!!!", Toast.LENGTH_LONG).show();
                    return;
                }

                Toast.makeText(MainActivity.this, "Login success - " + ParseUser.getCurrentUser().getUsername(), Toast.LENGTH_LONG).show();
            }
        });
    }

    protected void onUpload() {
        ParseObject obj = new ParseObject("Data");

        Random rand = new Random();
        String[] proverbs = getResources().getStringArray(R.array.proverbs);
        int pos = rand.nextInt(proverbs.length);

        final String content = proverbs[pos];

        obj.put(COL_CONTENT, content);
        obj.put(COL_USERNAME, ParseUser.getCurrentUser().getUsername());

        obj.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e != null) {
                    if (BuildConfig.DEBUG) e.printStackTrace();
                    Toast.makeText(MainActivity.this, e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
                    return;
                }

                Toast.makeText(MainActivity.this, "Upload success - " + content, Toast.LENGTH_LONG).show();
            }
        });
    }

    protected void onQuery() {
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Data");
        query.whereEqualTo(COL_USERNAME, USERNAME);
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> list, ParseException e) {
                if (e != null) {
                    if (BuildConfig.DEBUG) e.printStackTrace();
                    Toast.makeText(MainActivity.this, e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
                    return;
                }

                if (list == null || list.size() < 1) {
                    Toast.makeText(MainActivity.this, "No data", Toast.LENGTH_LONG).show();
                    return;
                }

                for (ParseObject obj : list) {
                    Log.e(TAG, "[" + obj.get(USERNAME) + "]" + obj.get(COL_CONTENT));
                }
            }
        });
    }

    /**
     * Asset으로부터 이미지를 읽어서 byte array로 반환
     * @param context {@see Context}
     * @return 이미지 byte array
     */
    protected byte[] getBitmapArray(Context context) {
        byte[] byte_array = null;

        try {
            InputStream is = context.getAssets().open("hero_developer.pgn");
            byte_array = readByteArray(is);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return byte_array;
    }

    /**
     * InputStream에서 data를 byte array 전체로 읽어서 반환
     * @param input {@see InputStream}
     * @return InputStream에서 읽은 전체 data의 byte array
     * @throws IOException
     */
    public byte[] readByteArray(InputStream input) throws IOException {
        byte[] buffer = new byte[8192];
        int bytesRead;
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        while ((bytesRead = input.read(buffer)) != -1) {
            output.write(buffer, 0, bytesRead);
        }
        return output.toByteArray();
    }
}