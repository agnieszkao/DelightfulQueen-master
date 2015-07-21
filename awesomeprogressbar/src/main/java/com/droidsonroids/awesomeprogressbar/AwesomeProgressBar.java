package com.droidsonroids.awesomeprogressbar;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.view.animation.OvershootInterpolator;

public class AwesomeProgressBar extends View {
    private static final int DEFAULT_ANIMATION_DURATION = 800;
    private static final float DEFAULT_RADIUS = 100.0f;
    private static final float DEFAULT_STROKE = 4.0f;

    private Paint mBackgroundPaint;
    private Paint mProgressBarPaint;
    private State mState;
    private RectF mRectF;
    private AnimatorSet mAnimatorSet;
    private AnimatorSet mAnimatorSetCross;
    private ValueAnimator mProgressAnimation;
    private ValueAnimator mPlusAnimation;
    private ValueAnimator mCrossAnimation;

    private float mProgressValue;
    private float mLineLenght;
    private int mAnimationDuration;
    private int mBackgroundColor;
    private int mProgressBarColor;
    private float mCenterX;
    private float mCenterY;
    private float mRadius;
    private float mStroke;

    private boolean isAnimationInitialized = false;
    private boolean isAnimationSuccess = false;

    private enum State {RUNNING_STATE, IDLE_STATE, STATE_SUCCESS, STATE_FAILURE}

    public AwesomeProgressBar(Context context) {
        this(context, null);
    }

    public AwesomeProgressBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        initialize(context, attrs);
    }

    private void initialize(Context context, AttributeSet attrs) {
        if (attrs != null) {
            TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.AwesomeProgressBar);
            try {
                mRadius = array.getDimension(R.styleable.AwesomeProgressBar_radius, DEFAULT_RADIUS);
                mStroke = array.getDimension(R.styleable.AwesomeProgressBar_stroke, DEFAULT_STROKE);
                mAnimationDuration = array.getInteger(
                        R.styleable.AwesomeProgressBar_animationDuration, DEFAULT_ANIMATION_DURATION);
                mBackgroundColor = array.getColor(R.styleable.AwesomeProgressBar_backgroundColor,
                        getDefaultBackgroundColor());
                mProgressBarColor = array.getColor(R.styleable.AwesomeProgressBar_progressBarColor, getDefaultProgressBarColor());
            } finally {
                array.recycle();
            }
        }
        mBackgroundPaint = new Paint();
        mBackgroundPaint.setColor(mBackgroundColor);
        mBackgroundPaint.setStrokeWidth(mStroke);
        mBackgroundPaint.setStyle(Paint.Style.STROKE);

        mProgressBarPaint = new Paint();
        mProgressBarPaint.setColor(mProgressBarColor);
        mProgressBarPaint.setStrokeWidth(mStroke);
        mProgressBarPaint.setStyle(Paint.Style.STROKE);
        mState = State.IDLE_STATE;
    }

    private int getDefaultBackgroundColor() {
        return getResources().getColor(R.color.default_background_color);
    }

    private int getDefaultProgressBarColor() {
        return getResources().getColor(R.color.default_progress_bar_color);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mCenterX = w / 2f;
        mCenterY = h / 2f;
        mRectF = new RectF(mCenterX - mRadius, mCenterY - mRadius,
                mCenterX + mRadius, mCenterY + mRadius);
        if (!isAnimationInitialized) {
            setupAnimations();
            isAnimationInitialized = true;
        }
    }

    private void setupAnimations() {
        mProgressAnimation = ValueAnimator.ofFloat(0, 360.0f);
        mProgressAnimation.setDuration(mAnimationDuration);
        mProgressAnimation.setInterpolator(new LinearInterpolator());
        mProgressAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                mProgressValue = (float) animation.getAnimatedValue();
                invalidate();
            }
        });
        mProgressAnimation.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                mState = State.RUNNING_STATE;
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                setClickable(true);
                if (isAnimationSuccess) {
                    mAnimatorSet.start();
                } else {
                    mAnimatorSetCross.start();
                }
            }

            @Override
            public void onAnimationCancel(Animator animation) {
            }

            @Override
            public void onAnimationRepeat(Animator animation) {
            }
        });

        ObjectAnimator rotateAnimation = ObjectAnimator.ofFloat(this, "rotation", 0f, 360f);
        rotateAnimation.setInterpolator(new LinearInterpolator());

        mPlusAnimation = ValueAnimator.ofFloat(0, mRadius / 2f);
        mPlusAnimation.setDuration(mAnimationDuration);
        mPlusAnimation.setInterpolator(new OvershootInterpolator());
        mPlusAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                mLineLenght = (float) animation.getAnimatedValue();
                invalidate();
            }
        });

        mPlusAnimation.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                mState = State.STATE_SUCCESS;
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                setClickable(true);
            }

            @Override
            public void onAnimationCancel(Animator animation) {
            }

            @Override
            public void onAnimationRepeat(Animator animation) {
            }
        });

        mCrossAnimation = ValueAnimator.ofFloat(0, mRadius / 2f);
        mCrossAnimation.setDuration(mAnimationDuration);
        mCrossAnimation.setInterpolator(new OvershootInterpolator());
        mCrossAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                mLineLenght = (float) animation.getAnimatedValue();
                invalidate();
            }
        });
        mCrossAnimation.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                mState = State.STATE_FAILURE;
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                setClickable(true);
            }

            @Override
            public void onAnimationCancel(Animator animation) {
            }

            @Override
            public void onAnimationRepeat(Animator animation) {
            }
        });

        mAnimatorSet = new AnimatorSet();
        mAnimatorSet.playTogether(mPlusAnimation, rotateAnimation);
        
        mAnimatorSetCross = new AnimatorSet();
        mAnimatorSetCross.playTogether(mCrossAnimation, rotateAnimation);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawCircle(mCenterX, mCenterY, mRadius, mBackgroundPaint);
        if (mState == State.RUNNING_STATE) {
            canvas.drawArc(mRectF, 270.0f, mProgressValue, false, mProgressBarPaint);
        } else if (mState == State.STATE_SUCCESS) {
            canvas.drawLine(mCenterX, mCenterY, mCenterX - mLineLenght, mCenterY, mProgressBarPaint);
            canvas.drawLine(mCenterX, mCenterY, mCenterX + mLineLenght, mCenterY, mProgressBarPaint);
            canvas.drawLine(mCenterX, mCenterY, mCenterX, mCenterY - mLineLenght, mProgressBarPaint);
            canvas.drawLine(mCenterX, mCenterY, mCenterX, mCenterY + mLineLenght, mProgressBarPaint);
        } else if (mState == State.STATE_FAILURE) {
            canvas.drawLine(mCenterX, mCenterY, mCenterX - mLineLenght, mCenterY + mLineLenght, mProgressBarPaint);
            canvas.drawLine(mCenterX, mCenterY, mCenterX + mLineLenght, mCenterY - mLineLenght, mProgressBarPaint);
            canvas.drawLine(mCenterX, mCenterY, mCenterX - mLineLenght, mCenterY - mLineLenght, mProgressBarPaint);
            canvas.drawLine(mCenterX, mCenterY, mCenterX + mLineLenght, mCenterY + mLineLenght, mProgressBarPaint);
        }
    }

    public void play(boolean isAnimationSuccess) {
        setClickable(false);
        if (mProgressAnimation.isRunning()) {
            mState = State.IDLE_STATE;
        }
        this.isAnimationSuccess = isAnimationSuccess;
        mProgressAnimation.start();
    }
}
