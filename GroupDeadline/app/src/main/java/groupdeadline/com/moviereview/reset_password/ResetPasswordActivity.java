package groupdeadline.com.moviereview.reset_password;

import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

import groupdeadline.com.moviereview.R;
import groupdeadline.com.moviereview.oop.BaseActivity;

public class ResetPasswordActivity extends BaseActivity implements View.OnClickListener{

    private EditText edtEmailReset;
    private Button btnReset;
    private FirebaseAuth mAuth;
    private ImageView imgBack;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);
        addControls ();
    }

    private void addControls() {
        edtEmailReset = (EditText) findViewById(R.id.edtEmailResetPassword);
        btnReset = (Button) findViewById(R.id.btnResetPassword);
        imgBack = (ImageView) findViewById(R.id.imgBackResetPassword);
        mAuth = FirebaseAuth.getInstance();

        btnReset.setOnClickListener(this);
        imgBack.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id){
            case R.id.btnResetPassword :
                resetPassword ();
                break;
            case R.id.imgBackResetPassword :
                closeActivity ();
            default:
                break;
        }
    }

    private void closeActivity() {
        String email = edtEmailReset.getText().toString().trim();
        if (!TextUtils.isEmpty(email) ){
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

    private void resetPassword() {
        boolean isValid = true;
        final String email = edtEmailReset.getText().toString().trim();
        if (TextUtils.isEmpty(email)){
            isValid = false;
            edtEmailReset.setError(getResources().getString(R.string.empty_text));
        }
        if (!isValidEmailAddress(email)){
            isValid = false;
            edtEmailReset.setError(getResources().getString(R.string.email_invaild));
        }
        if (isValid){
            showProgress(getResources().getString(R.string.loading));
            AlertDialog.Builder aler = new AlertDialog.Builder(this);
            aler.setCancelable(false);
            aler.setMessage(getResources().getString(R.string.confirm_reset_password));
            aler.setPositiveButton(getResources().getString(R.string.yes), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    mAuth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()){
                                hideProgress();
                                edtEmailReset.setText("");
                                Toast.makeText(ResetPasswordActivity.this, getResources().getString(R.string.notification_sent_email), Toast.LENGTH_SHORT).show();
                            }
                            else {
                                hideProgress();
                                Log.d("FAIL", "Send reset password failed!");
                            }
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            hideProgress();
                            Toast.makeText(ResetPasswordActivity.this, e.getMessage().toString(), Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            });
            aler.setNegativeButton(getResources().getString(R.string.no), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            aler.show();
        }
        else {
        }
    }
}
