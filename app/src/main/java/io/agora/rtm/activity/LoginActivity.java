package io.agora.rtm.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import io.agora.rtm.AGApplication;
import io.agora.rtm.ErrorInfo;
import io.agora.rtm.R;
import io.agora.rtm.ResultCallback;
import io.agora.rtm.RtmClient;
import io.agora.rtm.rtmtutorial.ChatManager;
import io.agora.rtm.utils.MessageUtil;


public class LoginActivity extends Activity {
    private final String TAG = LoginActivity.class.getSimpleName();

    private TextView mLoginBtn;
    private EditText mUserIdEditText;
    private String mUserId;

    private RtmClient mRtmClient;
    private boolean mIsInChat = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mUserIdEditText = findViewById(R.id.user_id);
        mLoginBtn = findViewById(R.id.button_login);

        ChatManager mChatManager = AGApplication.the().getChatManager();
        mRtmClient = mChatManager.getRtmClient();
    }

    public void onClickLogin(View v) {
        mUserId = mUserIdEditText.getText().toString();
        if (mUserId.equals("")) {
            showToast(getString(R.string.account_empty));
        } else if (mUserId.length() > MessageUtil.MAX_INPUT_NAME_LENGTH) {
            showToast(getString(R.string.account_too_long));
        } else if (mUserId.startsWith(" ")) {
            showToast(getString(R.string.account_starts_with_space));
        } else if (mUserId.equals("null")) {
            showToast(getString(R.string.account_literal_null));
        } else {
            mLoginBtn.setEnabled(false);
            doLogin();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        mLoginBtn.setEnabled(true);
        if (mIsInChat) {
            doLogout();
        }
    }

    /**
     * API CALL: login RTM server
     */
    private void doLogin() {
        mIsInChat = true;

        String token = getBaseContext().getString(R.string.agora_rtm_app_token);
        mRtmClient.login(token, mUserId, new ResultCallback<Void>() {
            @Override
            public void onSuccess(Void responseInfo) {
                Log.i(TAG, "login success");
                runOnUiThread(() -> {
                    Intent intent = new Intent(LoginActivity.this, SelectionActivity.class);
                    intent.putExtra(MessageUtil.INTENT_EXTRA_USER_ID, mUserId);
                    startActivity(intent);
                });
            }

            @Override
            public void onFailure(ErrorInfo errorInfo) {
                Log.i(TAG, "login failed: " + errorInfo.getErrorCode() + "-" + errorInfo.getErrorDescription());
                runOnUiThread(() -> {
                    mLoginBtn.setEnabled(true);
                    mIsInChat = false;
                    showToast(getString(R.string.login_failed));
                });
            }
        });
    }

    /**
     * API CALL: logout from RTM server
     */
    private void doLogout() {
        mRtmClient.logout(null);
        MessageUtil.cleanMessageListBeanList();
    }

    private void showToast(String text) {
        Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
    }
}
