package groupdeadline.com.moviereview.login_facebook;

import android.app.Activity;
import android.app.ProgressDialog;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Arrays;

import groupdeadline.com.moviereview.R;
import groupdeadline.com.moviereview.flash_main.RequestLoginActivity;
import groupdeadline.com.moviereview.oop.BaseActivity;
import groupdeadline.com.moviereview.user.User;
import groupdeadline.com.moviereview.utils.Utils;

/**
 * Created by KunPark on 7/18/2017.
 */

public class LoginFacebook  {
    private LoginManager loginManager;
    private CallbackManager callbackManager;
    private Activity mActivity;
    private FirebaseAuth mAuth;
    private DatabaseReference mData;
    private ProgressDialog prgressDialog;
    private String email = "", userName = "", linkImageUser = "", idUser;

    public LoginFacebook(LoginManager loginManager, CallbackManager callbackManager, Activity mActivity) {
        this.loginManager = loginManager;
        this.callbackManager = callbackManager;
        this.mActivity = mActivity;
        prgressDialog = new ProgressDialog(mActivity);
        mAuth = FirebaseAuth.getInstance();
        mData = FirebaseDatabase.getInstance().getReference();
    }

    public void loginFacebook (){
        String [] permisstion = {"public_profile", "email", "user_friends"};
        loginManager.logInWithReadPermissions(mActivity, Arrays.asList(permisstion));
        loginManager.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                handleFacebookAccessToken(loginResult.getAccessToken());
                //Toast.makeText(mActivity, "Login Successful", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancel() {
                Toast.makeText(mActivity, "Login Canceled", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(FacebookException error) {
                Toast.makeText(mActivity, "Login Error", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void handleFacebookAccessToken(AccessToken token) {
        showProgress(mActivity.getResources().getString(R.string.loading));
        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        mAuth.signInWithCredential(credential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()){
                    hideProgress();
                    Log.d("SUCCESSFUL", "SignInSuccessful");
                    email = task.getResult().getUser().getEmail();
                    userName = task.getResult().getUser().getDisplayName();
                    linkImageUser = task.getResult().getUser().getPhotoUrl().toString();
                    idUser = task.getResult().getUser().getUid();
                    User user = new User(userName, idUser, linkImageUser, email);
                    createUserOnFireBase(user);
                }
                else {
                    hideProgress();
                    Log.d("UNSUCCESSFUL", "SignInUnSuccessful");
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                hideProgress();
                Log.d("UNSUCCESSFUL", "SignInError");
                Toast.makeText(mActivity, e.getMessage().toString(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void createUserOnFireBase (final User user){
        final DatabaseReference userNode = mData.child(Utils.USER).child(user.getIdUser());
        userNode.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() == null){
                    userNode.setValue(user);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void showProgress (String s){
        prgressDialog.setCancelable(false);
        prgressDialog.setMessage(mActivity.getResources().getString(R.string.loading));
        prgressDialog.show();
    }
    private void hideProgress (){
        prgressDialog.hide();
    }

}
