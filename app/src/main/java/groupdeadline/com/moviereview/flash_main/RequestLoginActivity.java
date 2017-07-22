package groupdeadline.com.moviereview.flash_main;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Arrays;

import groupdeadline.com.moviereview.R;
import groupdeadline.com.moviereview.login_facebook.LoginFacebook;
import groupdeadline.com.moviereview.login_facebook.LoginGoogle;
import groupdeadline.com.moviereview.oop.BaseActivity;
import groupdeadline.com.moviereview.sign_in.SignInActivity;
import groupdeadline.com.moviereview.sign_up.SignUpActivity;

public class RequestLoginActivity extends BaseActivity implements View.OnClickListener, GoogleApiClient.OnConnectionFailedListener {

    private Button btnSignIn;
    private TextView txtSignUp;
    private ImageView imgLoginFb, imgLoginGoogle;
    private LoginManager loginManager;
    private CallbackManager callbackManager;
    private FirebaseAuth mAụth;
    private LoginFacebook loginFacebook;
    private LoginGoogle loginGoogle;
    private LinearLayout layoutMain;
    private GoogleApiClient mGoogleApiClient;
    private int RC_SIGN_IN= 002;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        setContentView(R.layout.activity_request_login);
        addControl ();
        addEvents ();
    }

    private void addEvents() {
        imgLoginFb.setOnClickListener(this);
        imgLoginGoogle.setOnClickListener(this);
        txtSignUp.setOnClickListener(this);
        btnSignIn.setOnClickListener(this);
    }

    private void addControl() {
        imgLoginFb = (ImageView) findViewById(R.id.imgLoginFb);
        imgLoginGoogle = (ImageView) findViewById(R.id.imgLoginGoogle);
        btnSignIn = (Button) findViewById(R.id.btnSignIn_Request);
        txtSignUp = (TextView) findViewById(R.id.txtSignUp);
        layoutMain = (LinearLayout) findViewById(R.id.layoutMain);

        //Animation
        Animation alpha = AnimationUtils.loadAnimation(this, R.anim.alpha_anim);
        layoutMain.setAnimation(alpha);

        //Login Facebook
        loginManager = LoginManager.getInstance();
        callbackManager = CallbackManager.Factory.create();
        mAụth = FirebaseAuth.getInstance();
        loginFacebook = new LoginFacebook(loginManager, callbackManager, this);

        //Login Google
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this /* FragmentActivity */, this/* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();
        loginGoogle = new LoginGoogle(this, mGoogleApiClient);

    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id){
            case R.id.imgLoginFb :
                loginFacebook ();
                break;
            case R.id.imgLoginGoogle :
                loginGoogle ();
                break;
            case R.id.txtSignUp :
                signUp ();
                break;
            case R.id.btnSignIn_Request :
                signIn ();
                break;
            default:
                break;
        }
    }

    private void signIn() {
        startActivity(new Intent(this, SignInActivity.class));
    }

    private void signUp() {
        startActivity(new Intent(this, SignUpActivity.class));
    }

    private void loginGoogle() {
        loginGoogle.signIn();
    }

    private void loginFacebook() {
        loginFacebook.loginFacebook();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        callbackManager.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            if (result.isSuccess()) {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = result.getSignInAccount();
                loginGoogle.firebaseAuthWithGoogle(account);
            }
        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.d("SIGNIN_FAIL", connectionResult + "");
    }
}
