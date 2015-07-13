package me.ji5.parsetutorial.login;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseUser;

import me.ji5.parsetutorial.BuildConfig;
import me.ji5.parsetutorial.MainActivity;
import me.ji5.parsetutorial.R;
import me.ji5.parsetutorial.common.Consts;
import me.ji5.parsetutorial.utils.CommonUtils;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener, Consts {
    protected static final String TAG = LoginActivity.class.getSimpleName();

    private Context mContext;

    private EditText mEtUsername, mEtPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mContext = this;

        findViewById(R.id.btn_login).setOnClickListener(this);
        findViewById(R.id.btn_signup).setOnClickListener(this);

        mEtUsername = (EditText) findViewById(R.id.et_username);
        mEtPassword = (EditText) findViewById(R.id.et_password);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btn_login)
            onLogin();
        else if (v.getId() == R.id.btn_signup)
            goToSignupActivity();
    }

    /**
     * Parse Login을 수행
     * 입력한 id / pwd가 없을 경우 Consts에 정의한 USERNAME과 PASSWORD를 사용해서
     * 로그인한다.
     */
    protected void onLogin() {

        CommonUtils.hideSoftKeyboard(mContext, mEtUsername);

        final ProgressDialog progressDialog = new ProgressDialog(mContext);
        progressDialog.setCancelable(true);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();

        String username = mEtUsername.getText().toString();

        if (username == null || TextUtils.isEmpty(username))
            username = USERNAME;

        String password = mEtPassword.getText().toString();

        if (password == null || TextUtils.isEmpty(password))
            password = PASSWORD;

        ParseUser.logInInBackground(username, password, new LogInCallback() {
            @Override
            public void done(ParseUser parseUser, ParseException e) {
                if (progressDialog.isShowing())
                    progressDialog.dismiss();

                if (e != null) {
                    if (BuildConfig.DEBUG) e.printStackTrace();
                    Toast.makeText(LoginActivity.this, e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
                    return;
                }

                if (parseUser == null) {
                    Toast.makeText(LoginActivity.this, "The user info is null!!!", Toast.LENGTH_LONG).show();
                    return;
                }

                Toast.makeText(LoginActivity.this, "Login success - " + ParseUser.getCurrentUser().getUsername(), Toast.LENGTH_LONG).show();

                // 성공 후 MainActivity로 이동
                Intent intent = new Intent(mContext, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    private void goToSignupActivity() {
        Intent intent = new Intent(mContext, SignupActivity.class);
        startActivity(intent);
    }
}
