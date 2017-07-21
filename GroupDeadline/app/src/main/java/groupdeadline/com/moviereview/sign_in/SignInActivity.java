package groupdeadline.com.moviereview.sign_in;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import groupdeadline.com.moviereview.R;
import groupdeadline.com.moviereview.oop.BaseActivity;
import groupdeadline.com.moviereview.reset_password.ResetPasswordActivity;

public class SignInActivity extends BaseActivity implements View.OnFocusChangeListener, View.OnClickListener{

    private EditText edtEmail, edtPassword;
    private CheckBox chkRemember;
    private TextView txtForgotPassword, txtLabelEmail, txtLabelPassword;
    private Button btnSignIn;
    private ImageView imgBackSignIn;
    private FirebaseAuth mAuth;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);
        addControls();
        addEvents();
    }

    private void addEvents() {
        btnSignIn.setOnClickListener(this);
        txtForgotPassword.setOnClickListener(this);
        imgBackSignIn.setOnClickListener(this);

        edtEmail.setOnFocusChangeListener(this);
        edtPassword.setOnFocusChangeListener(this);
    }


    private void signIn() {
        progressDialog.show();
        String email = edtEmail.getText().toString().trim();
        String password = edtPassword.getText().toString().trim();
        boolean isVail = true;
        if (TextUtils.isEmpty(email)) {
            isVail = false;
            edtEmail.setError(getResources().getString(R.string.empty_text));
        }
        if (TextUtils.isEmpty(password)) {
            isVail = false;
            edtPassword.setError(getResources().getString(R.string.empty_text));
        }
        if (isVail) {
            mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()){
                        Log.d("SUCCESSFUL", "Login Successful");
                        progressDialog.hide();
                    }
                    else {
                        progressDialog.hide();
                        Toast.makeText(SignInActivity.this, getResources().getString(R.string.password_not_correct), Toast.LENGTH_SHORT).show();
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    progressDialog.hide();
                    Log.d("ERROR", e.getMessage().toString());
                    Toast.makeText(SignInActivity.this, e.getMessage().toString(), Toast.LENGTH_SHORT).show();

                }
            });
        }
        else {
            progressDialog.hide();
        }
    }

    private void addControls() {
        edtEmail = (EditText) findViewById(R.id.edtEmail_SignIn);
        edtPassword = (EditText) findViewById(R.id.edtPassword_SignIn);
        chkRemember = (CheckBox) findViewById(R.id.chkRememberme);
        txtForgotPassword = (TextView) findViewById(R.id.txtForgotPassword);
        btnSignIn = (Button) findViewById(R.id.btnSignIn);
        imgBackSignIn = (ImageView) findViewById(R.id.imgBackSignIn);

        txtLabelEmail = (TextView) findViewById(R.id.txtLabelEmail);
        txtLabelPassword = (TextView) findViewById(R.id.txtLabelPassword);

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading...");

        //Authencation
        mAuth = FirebaseAuth.getInstance();
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id){
            case R.id.btnSignIn :
                signIn();
                break;
            case R.id.txtForgotPassword :
                progressForgotPassword ();
                break;
            case R.id.imgBackSignIn :
                closeActivity ();
                break;
            default:
                break;

        }
    }

    private void closeActivity() {
        String email = edtEmail.getText().toString().trim();
        String password = edtPassword.getText().toString().trim();
        if (!TextUtils.isEmpty(email) |!TextUtils.isEmpty(password)){
            AlertDialog.Builder aler = new AlertDialog.Builder(this);
            aler.setCancelable(false);
            aler.setMessage(getResources().getString(R.string.remind_close));
            aler.setNegativeButton(getResources().getString(R.string.no), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            aler.setPositiveButton(getResources().getString(R.string.yes), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    finish();
                }
            });
            aler.show();
        }
        else {
            finish();
        }
    }

    private void progressForgotPassword() {
        startActivity(new Intent(this, ResetPasswordActivity.class));
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {

        String email = edtEmail.getText().toString().trim();
        String password = edtPassword.getText().toString().trim();

        //edtEmail Focus
        if (v.getId() == R.id.edtEmail_SignIn && hasFocus){
            if (TextUtils.isEmpty(email)){
                txtLabelEmail.setText(getResources().getString(R.string.email));
                txtLabelEmail.setTextColor(getResources().getColor(R.color.yellow));
                edtEmail.setHint("");
            }
            else {
                txtLabelEmail.setText(getResources().getString(R.string.email));
                txtLabelEmail.setTextColor(getResources().getColor(R.color.grey));
            }
        }
        else if (v.getId() == R.id.edtEmail_SignIn && !hasFocus){
            if (TextUtils.isEmpty(email)){
                txtLabelEmail.setText("");
                edtEmail.setHint(getResources().getString(R.string.email));
            }
            else {
                txtLabelEmail.setText(getResources().getString(R.string.email));
                txtLabelEmail.setTextColor(getResources().getColor(R.color.grey));
            }
        }
        //edtPassword Focus

        if (v.getId() == R.id.edtPassword_SignIn && hasFocus){
            if (TextUtils.isEmpty(password)){
                txtLabelPassword.setText(getResources().getString(R.string.password));
                txtLabelPassword.setTextColor(getResources().getColor(R.color.yellow));
                edtPassword.setHint("");
            }
            else {
                txtLabelPassword.setText(getResources().getString(R.string.password));
                txtLabelPassword.setTextColor(getResources().getColor(R.color.grey));
            }
        }
        else if (v.getId() == R.id.edtPassword_SignIn && !hasFocus){
            if (TextUtils.isEmpty(password)){
                txtLabelPassword.setText("");
                edtPassword.setHint(getResources().getString(R.string.password));
            }
            else {
                txtLabelPassword.setText(getResources().getString(R.string.password));
                txtLabelPassword.setTextColor(getResources().getColor(R.color.grey));
            }
        }

    }
}
