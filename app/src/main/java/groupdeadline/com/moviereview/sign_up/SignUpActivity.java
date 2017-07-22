package groupdeadline.com.moviereview.sign_up;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import groupdeadline.com.moviereview.R;
import groupdeadline.com.moviereview.oop.BaseActivity;
import groupdeadline.com.moviereview.sign_in.SignInActivity;
import groupdeadline.com.moviereview.user.User;
import groupdeadline.com.moviereview.utils.Utils;

public class SignUpActivity extends BaseActivity implements View.OnClickListener, View.OnFocusChangeListener {

    private EditText edtFullName, edtEmail, edtPassword, edtConfirmPassword;
    private Button btnSignUp;
    private ImageView imgHidePassword, imgHideConfirmPassword, imgBackSignUp;
    private boolean isHidePassword = true, isHideConfirmPassword = true;
    private FirebaseAuth mAuth;
    private DatabaseReference mData;
    private TextView txtLabelFullName, txtLabelEmail, txtLabelPassword, txtLabelConfirmPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        addControl();
        addEvetns();
    }

    private void addEvetns() {
        btnSignUp.setOnClickListener(this);
        imgHidePassword.setOnClickListener(this);
        imgHideConfirmPassword.setOnClickListener(this);
        imgBackSignUp.setOnClickListener(this);

        edtFullName.setOnFocusChangeListener(this);
        edtEmail.setOnFocusChangeListener(this);
        edtPassword.setOnFocusChangeListener(this);
        edtConfirmPassword.setOnFocusChangeListener(this);
    }

    private void addControl() {
        edtEmail = (EditText) findViewById(R.id.edtEmail);
        edtFullName = (EditText) findViewById(R.id.edtFullName);
        edtPassword = (EditText) findViewById(R.id.edtPassword);
        edtConfirmPassword = (EditText) findViewById(R.id.edtConfimPassword);
        btnSignUp = (Button) findViewById(R.id.btnSignUp);
        imgHidePassword = (ImageView) findViewById(R.id.imgHidePassword);
        imgHideConfirmPassword = (ImageView) findViewById(R.id.imgHideConfirmPassword);
        imgBackSignUp = (ImageView) findViewById(R.id.imgBackSignUp);

        txtLabelFullName = (TextView) findViewById(R.id.txtLabelFullName);
        txtLabelEmail = (TextView) findViewById(R.id.txtLabelEmail_SignUp);
        txtLabelPassword = (TextView) findViewById(R.id.txtLabelPassword_SignUp);
        txtLabelConfirmPassword = (TextView) findViewById(R.id.txtLabelConfimPassword);

        //Firebase
        mAuth = FirebaseAuth.getInstance();
        mData = FirebaseDatabase.getInstance().getReference();

    }

    private void hideConfirmPassword() {
        if (isHideConfirmPassword) {
            isHideConfirmPassword = false;
            imgHideConfirmPassword.setImageResource(R.drawable.icon_show);
            edtConfirmPassword.setTransformationMethod(new HideReturnsTransformationMethod());
        } else {
            isHideConfirmPassword = true;
            imgHideConfirmPassword.setImageResource(R.drawable.icon_hide);
            edtConfirmPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());

        }
    }

    private void hidePassword() {
        if (isHidePassword) {
            isHidePassword = false;
            imgHidePassword.setImageResource(R.drawable.icon_show);
            edtPassword.setTransformationMethod(new HideReturnsTransformationMethod());
        } else {
            isHidePassword = true;
            imgHidePassword.setImageResource(R.drawable.icon_hide);
            edtPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());

        }

    }

    private void signUp() {
        showProgress(getResources().getString(R.string.loading));
        boolean isVail = true;
        String email = edtEmail.getText().toString().trim();
        String fullName = edtFullName.getText().toString().trim();
        String password = edtPassword.getText().toString();
        String confirmPassword = edtConfirmPassword.getText().toString();


        //Check Empty
        if (TextUtils.isEmpty(email)) {
            isVail = false;
            edtEmail.setError(getResources().getString(R.string.empty_text));
        }
        if (TextUtils.isEmpty(fullName)) {
            isVail = false;
            edtFullName.setError(getResources().getString(R.string.empty_text));
        }
        if (TextUtils.isEmpty(password)) {
            isVail = false;
            Toast.makeText(this, getResources().getString(R.string.password_not_vail), Toast.LENGTH_SHORT).show();
        }
        if (TextUtils.isEmpty(confirmPassword)) {
            isVail = false;
            Toast.makeText(this, getResources().getString(R.string.password_not_match), Toast.LENGTH_SHORT).show();
        }
        //Check Email Vaild
        if (!isValidEmailAddress(email)) {
            isVail = false;
            edtEmail.setError(getResources().getString(R.string.email_invaild));
        }
        //Check Password
        if (6 > password.length() && password.length() > 0) {
            isVail = false;
            Toast.makeText(this, getResources().getString(R.string.password_not_vail), Toast.LENGTH_SHORT).show();
        }
        //Check ConfirmPassword
        if (!password.equals(confirmPassword)) {
            isVail = false;
            Toast.makeText(this, getResources().getString(R.string.password_not_match), Toast.LENGTH_SHORT).show();
        }
        if (isVail) {
            registerUser(email, password, fullName);
        } else {
            hideProgress();
        }
    }

    private void registerUser(final String email, String password, final String fullName) {
        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    Log.d("SUCCESSFUL", "Register Successful");
                    User user = new User(fullName, task.getResult().getUser().getUid(), "", email);
                    createUserOnFireBase(user);
                } else {
                    hideProgress();
                    Log.d("UNSUCCESSFUL", "Register Unsuccessful");

                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(SignUpActivity.this, e.getMessage().toString(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void createUserOnFireBase(final User user) {
        final DatabaseReference userNode = mData.child(Utils.USER).child(user.getIdUser());
        userNode.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() == null) {
                    userNode.setValue(user);
                    hideProgress();
                    Toast.makeText(SignUpActivity.this, getResources().getString(R.string.register_successful), Toast.LENGTH_SHORT).show();
                    finish();
                    startActivity(new Intent(SignUpActivity.this, SignInActivity.class));
                    Log.d("ADDSUCCESSFUL", "Add User Successful");
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        String fullName = edtFullName.getText().toString().trim();
        String email = edtEmail.getText().toString().trim();
        String password = edtPassword.getText().toString().trim();
        String confirmPassword = edtConfirmPassword.getText().toString().trim();

        //edtFullName Focus
        if (v.getId() == R.id.edtFullName && hasFocus) {
            if (TextUtils.isEmpty(fullName)) {
                txtLabelFullName.setText(getResources().getString(R.string.full_name));
                txtLabelFullName.setTextColor(getResources().getColor(R.color.yellow));
                edtFullName.setHint("");
            } else {
                txtLabelFullName.setText(getResources().getString(R.string.full_name));
                txtLabelFullName.setTextColor(getResources().getColor(R.color.grey));
            }
        } else if (v.getId() == R.id.edtFullName && !hasFocus) {
            if (TextUtils.isEmpty(fullName)) {
                txtLabelFullName.setText("");
                edtFullName.setHint(getResources().getString(R.string.full_name));
            } else {
                txtLabelFullName.setText(getResources().getString(R.string.full_name));
                txtLabelFullName.setTextColor(getResources().getColor(R.color.grey));
            }
        }
        //edtEmail Focus
        if (v.getId() == R.id.edtEmail && hasFocus) {
            if (TextUtils.isEmpty(email)) {
                txtLabelEmail.setText(getResources().getString(R.string.email));
                txtLabelEmail.setTextColor(getResources().getColor(R.color.yellow));
                edtEmail.setHint("");
            } else {
                txtLabelEmail.setText(getResources().getString(R.string.email));
                txtLabelEmail.setTextColor(getResources().getColor(R.color.grey));
            }
        } else if (v.getId() == R.id.edtEmail && !hasFocus) {
            if (TextUtils.isEmpty(email)) {
                txtLabelEmail.setText("");
                edtEmail.setHint(getResources().getString(R.string.email));
            } else {
                txtLabelEmail.setText(getResources().getString(R.string.email));
                txtLabelEmail.setTextColor(getResources().getColor(R.color.grey));
            }
        }
        //edtPassword Focus
        if (v.getId() == R.id.edtPassword && hasFocus) {
            if (TextUtils.isEmpty(password)) {
                txtLabelPassword.setText(getResources().getString(R.string.password));
                txtLabelPassword.setTextColor(getResources().getColor(R.color.yellow));
                edtPassword.setHint("");
            } else {
                txtLabelPassword.setText(getResources().getString(R.string.password));
                txtLabelPassword.setTextColor(getResources().getColor(R.color.grey));
            }
        } else if (v.getId() == R.id.edtPassword && !hasFocus) {
            if (TextUtils.isEmpty(password)) {
                txtLabelPassword.setText("");
                edtPassword.setHint(getResources().getString(R.string.password));
            } else {
                txtLabelPassword.setText(getResources().getString(R.string.password));
                txtLabelPassword.setTextColor(getResources().getColor(R.color.grey));
            }
        }
        //edtConfirmPasword Focus
        if (v.getId() == R.id.edtConfimPassword && hasFocus) {
            if (TextUtils.isEmpty(confirmPassword)) {
                txtLabelConfirmPassword.setText(getResources().getString(R.string.confirm_password));
                txtLabelConfirmPassword.setTextColor(getResources().getColor(R.color.yellow));
                edtConfirmPassword.setHint("");
            } else {
                txtLabelConfirmPassword.setText(getResources().getString(R.string.confirm_password));
                txtLabelConfirmPassword.setTextColor(getResources().getColor(R.color.grey));
            }
        } else if (v.getId() == R.id.edtConfimPassword && !hasFocus) {
            if (TextUtils.isEmpty(confirmPassword)) {
                txtLabelConfirmPassword.setText("");
                edtConfirmPassword.setHint(getResources().getString(R.string.confirm_password));
            } else {
                txtLabelConfirmPassword.setText(getResources().getString(R.string.confirm_password));
                txtLabelConfirmPassword.setTextColor(getResources().getColor(R.color.grey));
            }
        }

    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.btnSignUp:
                signUp();
                break;
            case R.id.imgHidePassword:
                hidePassword();
                break;
            case R.id.imgHideConfirmPassword:
                hideConfirmPassword();
                break;
            case R.id.imgBackSignUp :
                closeActivity ();
                break;
            default:
                break;
        }
    }

    private void closeActivity() {
        String fullName = edtFullName.getText().toString().trim();
        String email = edtEmail.getText().toString().trim();
        String password = edtPassword.getText().toString().trim();
        String confirmPassword = edtConfirmPassword.getText().toString().trim();
        if (!TextUtils.isEmpty(email) |!TextUtils.isEmpty(password) | !TextUtils.isEmpty(fullName) |!TextUtils.isEmpty(confirmPassword)){
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
}
