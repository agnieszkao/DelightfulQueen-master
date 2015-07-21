package com.droidsonroids.delightfulqueen;

import android.app.Activity;
import android.os.Bundle;

import com.droidsonroids.awesomeprogressbar.AwesomeProgressBar;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends Activity {

    @Bind(R.id.super_awesome_progress_bar)
    AwesomeProgressBar mSuperAwesomeProgressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_custom_view);
        ButterKnife.bind(this);
    }

    @SuppressWarnings("unused")
    @OnClick(R.id.plus_button)
    public void onPlusButtonClick() {
        mSuperAwesomeProgressBar.play(true);
    }

    @SuppressWarnings("unused")
    @OnClick(R.id.cross_button)
    public void onCrossButtonClick() {
        mSuperAwesomeProgressBar.play(false);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ButterKnife.unbind(this);
    }
}
