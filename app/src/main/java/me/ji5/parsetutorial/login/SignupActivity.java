package me.ji5.parsetutorial.login;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

import me.ji5.parsetutorial.BuildConfig;
import me.ji5.parsetutorial.R;
import me.ji5.parsetutorial.common.Consts;
import me.ji5.parsetutorial.utils.CommonUtils;

public class SignupActivity extends AppCompatActivity implements View.OnClickListener, Consts {
    protected static final String TAG = SignupActivity.class.getSimpleName();

    private EditText mEtUsername, mEtPassword, mEtEmail, mEtDisplayName;

    private Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        mContext = this;

        findViewById(R.id.btn_signup).setOnClickListener(this);

        mEtUsername = (EditText) findViewById(R.id.et_username);
        mEtPassword = (EditText) findViewById(R.id.et_password);
        mEtEmail = (EditText) findViewById(R.id.et_email);
        mEtDisplayName = (EditText) findViewById(R.id.et_display_name);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_signup:
                onSignUp();
                break;
        }
    }

    protected void onSignUp() {

        CommonUtils.hideSoftKeyboard(mContext, mEtUsername);

        final ProgressDialog progressDialog = new ProgressDialog(mContext);
        progressDialog.setCancelable(true);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();

        final ParseUser user = new ParseUser();

        // validation check login도 추가하면 좋음

        String username = mEtUsername.getText().toString();
        if (username == null || TextUtils.isEmpty(username))
            username = USERNAME;

        String password = mEtPassword.getText().toString();
        if (password == null || TextUtils.isEmpty(password))
            password = PASSWORD;

        String email = mEtEmail.getText().toString();
        if (email == null || TextUtils.isEmpty(email))
            email = EMAIL;

        String displayName = mEtEmail.getText().toString();
        if (displayName == null || TextUtils.isEmpty(displayName))
            displayName = NAME;

        user.setUsername(username);
        user.setPassword(password);
        user.setEmail(email);

        user.put(COL_DISPLAYNAME, displayName);
        user.signUpInBackground(new SignUpCallback() {
            @Override
            public void done(ParseException e) {

                if (progressDialog.isShowing())
                    progressDialog.dismiss();

                if (e != null) {
                    if (BuildConfig.DEBUG) e.printStackTrace();
                    Toast.makeText(SignupActivity.this, e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
                    return;
                }

                Toast.makeText(SignupActivity.this, "Registered! - " + user.getUsername(), Toast.LENGTH_LONG).show();

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        finish();
                    }
                }, 1500);
            }
        });
    }

}
