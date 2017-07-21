package groupdeadline.com.moviereview.flash_main;

import android.content.Intent;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import groupdeadline.com.moviereview.R;

public class MainActivity extends AppCompatActivity implements Animation.AnimationListener {

    private ConstraintLayout layout;
    private ImageView imgLogo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        addControls();
    }

    private void addControls() {
        layout = (ConstraintLayout) findViewById(R.id.layoutFlash);
        imgLogo = (ImageView) findViewById(R.id.imgFlash);
        Animation layout_anim = AnimationUtils.loadAnimation(this, R.anim.flash_anim);
        Animation logo_anim = AnimationUtils.loadAnimation(this, R.anim.logo_anim);
        layout.setAnimation(layout_anim);
        imgLogo.setAnimation(logo_anim);
        logo_anim.setAnimationListener(this);
    }

    @Override
    public void onAnimationStart(Animation animation) {

    }

    @Override
    public void onAnimationEnd(Animation animation) {
        startActivity(new Intent(MainActivity.this, RequestLoginActivity.class));
    }

    @Override
    public void onAnimationRepeat(Animation animation) {

    }
}
