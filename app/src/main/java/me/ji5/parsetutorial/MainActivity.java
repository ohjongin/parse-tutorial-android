package me.ji5.parsetutorial;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.LogInCallback;
import com.parse.ParseACL;
import com.parse.ParseException;
import com.parse.ParseFile;
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
    protected static final String COL_CREATEDBY = "createdBy";
    protected static final String COL_IMAGE = "image";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.btn_signup).setOnClickListener(this);
        findViewById(R.id.btn_login).setOnClickListener(this);
        findViewById(R.id.btn_upload_object).setOnClickListener(this);
        findViewById(R.id.btn_query).setOnClickListener(this);
        findViewById(R.id.btn_upload_file).setOnClickListener(this);
        findViewById(R.id.btn_upload_acl).setOnClickListener(this);
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
            case R.id.btn_upload_object:
                onUploadObject();
                break;
            case R.id.btn_query:
                onQuery();
                break;
            case R.id.btn_upload_file:
                onUploadFile();
                break;
            case R.id.btn_upload_acl:
                onUploadAcl();
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

    /**
     * User정보와 Text만을 간단하게 등록
     */
    protected void onUploadObject() {
        ParseObject obj = new ParseObject("Data");

        final String content = getContentText();

        obj.put(COL_CONTENT, content);
        obj.put(COL_USERNAME, ParseUser.getCurrentUser().getUsername());
        obj.put(COL_CREATEDBY, ParseUser.getCurrentUser());

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

    /**
     * 등록된 Data 중에서 로그인한 사용자가 올린 Data만 읽어오도록 Query
     */
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
                    Log.e(TAG, "[" + obj.get(COL_USERNAME) + "]" + obj.get(COL_CONTENT));
                }
            }
        });
    }

    /**
     * Object를 등록할때 asset 폴더에 있는 이미지를 포함해서 등록하도록 기능 추가
     */
    protected void onUploadFile() {
        byte[] photo_array = getBitmapArray(this);
        if (photo_array != null) {
            final ParseFile photoFile = new ParseFile(System.currentTimeMillis() + ".png", photo_array);
            photoFile.saveInBackground(new SaveCallback() {
                @Override
                public void done(ParseException e) {
                    if (e != null) {
                        if (BuildConfig.DEBUG) e.printStackTrace();
                        Toast.makeText(MainActivity.this, e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
                        return;

                    }

                    ParseObject obj = new ParseObject("Data");

                    final String content = getContentText();

                    obj.put(COL_CONTENT, content);
                    obj.put(COL_USERNAME, ParseUser.getCurrentUser().getUsername());
                    obj.put(COL_CREATEDBY, ParseUser.getCurrentUser());
                    obj.put(COL_IMAGE, photoFile);

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
            });
        } else {
            Toast.makeText(MainActivity.this, "No Image Resource!!!", Toast.LENGTH_LONG).show();
        }
    }

    /**
     * ACL(Access Control List) 기능을 사용해서 현재 로그인 사용자만 접근이 가능하도록 Object 등록
     */
    protected void onUploadAcl() {
        ParseACL acl = new ParseACL();
        acl.setPublicReadAccess(false);
        acl.setPublicWriteAccess(false);
        acl.setReadAccess(ParseUser.getCurrentUser(), true);
        acl.setWriteAccess(ParseUser.getCurrentUser(), true);

        ParseObject obj = new ParseObject("Data");

        final String content = getContentText();

        obj.put(COL_CONTENT, content);
        obj.put(COL_USERNAME, ParseUser.getCurrentUser().getUsername());
        obj.put(COL_CREATEDBY, ParseUser.getCurrentUser());
        obj.setACL(acl);

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

    /**
     * 테스트에 사용할 sample text를 random하게 읽어서 반환
     *
     * @return string resource에 있는 random text
     */
    protected String getContentText() {
        Random rand = new Random();
        String[] proverbs = getResources().getStringArray(R.array.proverbs);
        int pos = rand.nextInt(proverbs.length);

        return proverbs[pos];
    }

    /**
     * Asset으로부터 이미지를 읽어서 byte array로 반환
     * @param context {@see Context}
     * @return 이미지 byte array
     */
    protected byte[] getBitmapArray(Context context) {
        String [] res_name_array = {"heros.png", "hero_designer.png", "hero_developer.png", "hero_maker.png"};
        byte[] byte_array = null;

        Random rand = new Random();
        int pos = rand.nextInt(res_name_array.length);

        try {
            InputStream is = context.getAssets().open(res_name_array[pos]);
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
