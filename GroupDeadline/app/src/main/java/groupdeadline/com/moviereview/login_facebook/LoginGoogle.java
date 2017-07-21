package groupdeadline.com.moviereview.login_facebook;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import groupdeadline.com.moviereview.R;
import groupdeadline.com.moviereview.user.User;
import groupdeadline.com.moviereview.utils.Utils;

/**
 * Created by KunPark on 7/19/2017.
 */

public class LoginGoogle {

    private Activity mActivity;
    private GoogleApiClient mGoogleApiClient;
    private int RC_SIGN_IN= 002;
    private FirebaseAuth mAuth;
    private DatabaseReference mData;
    private ProgressDialog prgressDialog;
    private String email = "", userName = "", linkImageUser = "", idUser;

    public LoginGoogle(Activity mActivity, GoogleApiClient mGoogleApiClient) {
        this.mActivity = mActivity;
        this.mGoogleApiClient = mGoogleApiClient;
        mAuth = FirebaseAuth.getInstance();
        mData = FirebaseDatabase.getInstance().getReference();
        prgressDialog = new ProgressDialog(mActivity);
    }



    public void signIn (){
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        mActivity.startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    public void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        showProgress(mActivity.getResources().getString(R.string.loading));
        Log.d("ID", "firebaseAuthWithGoogle:" + acct.getId());
        // [START_EXCLUDE silent]
        // [END_EXCLUDE]

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()){
                    hideProgress();
                    email = task.getResult().getUser().getEmail();
                    userName = task.getResult().getUser().getDisplayName();
                    linkImageUser = task.getResult().getUser().getPhotoUrl().toString();
                    idUser = task.getResult().getUser().getUid();
                    User user = new User(userName, idUser, linkImageUser, email);
                    createUserOnFireBase(user);
                    Toast.makeText(mActivity, "Login Google Successful", Toast.LENGTH_SHORT).show();
                }
                else {
                    hideProgress();
                    Toast.makeText(mActivity, "Login Google Unsuccessful", Toast.LENGTH_SHORT).show();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                hideProgress();
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
