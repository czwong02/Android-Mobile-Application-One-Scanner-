package com.example.onescanner;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.huawei.hmf.tasks.Task;
import com.huawei.hms.common.ApiException;
import com.huawei.hms.support.account.AccountAuthManager;
import com.huawei.hms.support.account.request.AccountAuthParams;
import com.huawei.hms.support.account.request.AccountAuthParamsHelper;
import com.huawei.hms.support.account.result.AuthAccount;
import com.huawei.hms.support.account.service.AccountAuthService;
import com.huawei.hms.support.api.entity.common.CommonConstant;

public class profilePage extends AppCompatActivity {

    TextView signOutButton;
    View share;
    FloatingActionButton home;
    ImageView userProfilePicture;
    TextView email;
    TextView userName;
    String userName1;
    String email1;
    String profilePictureUrl;
    AuthAccount authAccount;
    TextView view;
    boolean signStatus = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_page);

        userProfilePicture = findViewById(R.id.userProfilePicture);
        email = findViewById(R.id.email);
        userName = findViewById(R.id.userName);
        view = findViewById(R.id.view);

        findViewById(R.id.HuaweiIdAuthButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                silentSignInByHwId();
                signStatus = true;
            }
        });

        signOutButton = findViewById(R.id.HuaweiIdSignOutButton);
        signOutButton.setOnClickListener(v -> {
            if(signStatus) {
                signOut();
                userProfilePicture.setImageResource(R.drawable.guestprofile);
                userName.setText("Name: -");
                email.setText("Email: -");
                Toast.makeText(profilePage.this, "Sign Out Successfully", Toast.LENGTH_SHORT).show();
                signStatus = false;
            } else {
                Toast.makeText(profilePage.this, "Please sign in first. If you have signed in, please confirm your name and email before sign out by clicking show details button", Toast.LENGTH_LONG).show();
            }
        });

        share = findViewById(R.id.miShare);
        home = findViewById(R.id.home);

        share.setOnClickListener(view -> {
            Intent sharingIntent = new Intent(Intent.ACTION_SEND);

            // type of the content to be shared
            sharingIntent.setType("text/plain");

            // Body of the content
            String shareBody = "Hey! I've just downloaded the One Scanner Mobile App! Download the App at App Gallery now! https://appgallery.huawei.com/app/C107085323";

            // subject of the content. you can share anything
            String shareSubject = "One Scanner";

            // passing body of the content
            sharingIntent.putExtra(Intent.EXTRA_TEXT, shareBody);

            // passing subject of the content
            sharingIntent.putExtra(Intent.EXTRA_SUBJECT, shareSubject);
            startActivity(Intent.createChooser(sharingIntent, "Share using"));
        });

        home.setOnClickListener(view -> {
            Intent intent = new Intent(profilePage.this, MainActivity.class);
            startActivity(intent);
        });


        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                silentSignInByHwId();
                signStatus = true;
            }
        });

    }


    // AccountAuthService provides a set of APIs, including silentSignIn, getSignInIntent, and signOut.
    private AccountAuthService mAuthService;

    // Set HUAWEI ID sign-in authorization parameters.
    private AccountAuthParams mAuthParam;

    // Define the request code for signInIntent.
    private static final int REQUEST_CODE_SIGN_IN = 1000;

    // Define the log tag.
    private static final String TAG = "Account";

    /**
     * Silent sign-in: If a user has authorized your app and signed in, no authorization or sign-in screen will appear during subsequent sign-ins, and the user will directly sign in.
     * After a successful silent sign-in, the HUAWEI ID information will be returned in the success event listener.
     * If the user has not authorized your app or signed in, the silent sign-in will fail. In this case, your app will show the authorization or sign-in screen to the user.
     */
    private void silentSignInByHwId() {
        // 1. Use AccountAuthParams to specify the user information to be obtained after user authorization, including the user ID (OpenID and UnionID), email address, and profile (nickname and picture).
        // 2. By default, DEFAULT_AUTH_REQUEST_PARAM specifies two items to be obtained, that is, the user ID and profile.
        // 3. If your app needs to obtain the user's email address, call setEmail().
        //  4. To support ID token-based HUAWEI ID sign-in, use setIdToken(). User information can be parsed from the ID token.
        mAuthParam = new AccountAuthParamsHelper(AccountAuthParams.DEFAULT_AUTH_REQUEST_PARAM)
                .setEmail()
                .setIdToken()
                .createParams();
        // Use AccountAuthParams to build AccountAuthService.
        mAuthService = AccountAuthManager.getService(this, mAuthParam);
        // Sign in with a HUAWEI ID silently.
        Task<AuthAccount> task = mAuthService.silentSignIn();
        // The silent sign-in is successful. Process the returned AuthAccount object to obtain the HUAWEI ID information.
        task.addOnSuccessListener(this::dealWithResultOfSignIn);
        task.addOnFailureListener(e -> {
            // The silent sign-in fails. Your app will call getSignInIntent() to show the authorization or sign-in screen.
            if (e instanceof ApiException) {
                ApiException apiException = (ApiException) e;
                Intent signInIntent = mAuthService.getSignInIntent();
                // If your app appears in full screen mode when a user tries to sign in, that is, with no status bar at the top of the device screen, add the following parameter in the intent:
                // intent.putExtra(CommonConstant.RequestParams.IS_FULL_SCREEN, true);
                // Check the details in this FAQ.
                signInIntent.putExtra(CommonConstant.RequestParams.IS_FULL_SCREEN, true);
                startActivityForResult(signInIntent, REQUEST_CODE_SIGN_IN);
            }
        });
    }

    /**
     * Process the returned AuthAccount object to obtain the HUAWEI ID information.
     *
     * @param authAccount AuthAccount object, which contains the HUAWEI ID information.
     */
    private void dealWithResultOfSignIn(AuthAccount authAccount) {
        Log.i(TAG, "idToken:" + authAccount.getIdToken());
        // TODO: After obtaining the ID token, your app will send it to your app server if there is one. If you have no app server, your app will verify and parse the ID token locally.
        profilePictureUrl = authAccount.getAvatarUriString().toString();
        userName1 = "Name: " + authAccount.getDisplayName().toString();
        email1 = "Email: " + authAccount.getEmail().toString();
        userName.setText(userName1);
        email.setText(email1);
        if(!TextUtils.isEmpty(profilePictureUrl)) {
            userProfilePicture.setImageURI(Uri.parse(profilePictureUrl));
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_SIGN_IN) {
            Log.i(TAG, "onActivityResult of sigInInIntent, request code: " + REQUEST_CODE_SIGN_IN);
            Task<AuthAccount> authAccountTask = AccountAuthManager.parseAuthResultFromIntent(data);
            if (authAccountTask.isSuccessful()) {
                // The sign-in is successful, and the authAccount object that contains the HUAWEI ID information is obtained.
                AuthAccount authAccount = authAccountTask.getResult();
                dealWithResultOfSignIn(authAccount);
                Log.i(TAG, "onActivityResult of sigInInIntent, request code: " + REQUEST_CODE_SIGN_IN);

            } else {
                // The sign-in failed. Find the failure cause from the status code. For more information, please refer to Error Codes.
                Log.e(TAG, "sign in failed : " +((ApiException)authAccountTask.getException()).getStatusCode());
            }
        }
    }


    private void signOut() {
        Task<Void> signOutTask = mAuthService.signOut();
        signOutTask.addOnSuccessListener(aVoid -> Log.i(TAG, "signOut Success")).addOnFailureListener(e -> Log.i(TAG, "signOut fail"));
        Task<Void> task = mAuthService.cancelAuthorization();
        task.addOnSuccessListener(aVoid -> Log.i(TAG, "cancelAuthorization success"));
        task.addOnFailureListener(e -> Log.i(TAG, "cancelAuthorization failure:" + e.getClass().getSimpleName()));
    }
}