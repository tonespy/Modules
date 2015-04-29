package co.tonespy.floatlabel;

/**
 * Created by mac on 4/29/15.
 */
import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.nineoldandroids.animation.Animator;
import com.nineoldandroids.animation.Animator.AnimatorListener;
import com.nineoldandroids.animation.AnimatorSet;
import com.nineoldandroids.animation.ArgbEvaluator;
import com.nineoldandroids.animation.ObjectAnimator;
import com.nineoldandroids.animation.ValueAnimator;

public class FloatLabelSpinnerView extends LinearLayout {

    private static final String TAG = "FloatSpinnerView";

    private static long ANIMATION_DURATION = 400;
    private static int HINT_DEFAULT_COLOR = 0xFF006363;
    private static final int HINT_DEFAULT_COLOR_DISABLED = 0xFF2F6CB3;
    private static int HINT_DEFAULT_SIZE = 13;

    private TextView mHintView;
    private Spinner mSpinner;

    private int mHintColor;

    private AnimatorSet mEntranceAnimation = null;
    private AnimatorSet mExitAnimation = null;
    private ValueAnimator mAddColorAnimation = null;
    private ValueAnimator mRemoveColorAnimation = null;

    public FloatLabelSpinnerView(Context context) {
        super(context);
    }

    public FloatLabelSpinnerView(Context context, AttributeSet attrs) {
        super(context, attrs);

        setOrientation(LinearLayout.VERTICAL);
        setGravity(Gravity.LEFT);

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.float_label_spinnerview, this, true);

        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.FloatLabelTextView, 0, 0);
        String hint = array.getString(R.styleable.FloatLabelTextView_editText_hint);
        mHintColor = array.getColor(R.styleable.FloatLabelTextView_editText_color, HINT_DEFAULT_COLOR);

        float size = array.getDimension(R.styleable.FloatLabelTextView_editText_size, HINT_DEFAULT_SIZE);

        array.recycle();

        mHintView = (TextView) findViewById(R.id.spinnerTextview_float);
        mSpinner = (Spinner) findViewById(R.id.spinnerview_main);

        mHintView.setTextColor(HINT_DEFAULT_COLOR_DISABLED);

        mHintView.setText(hint);

        mHintView.setTextSize(size);

        mSpinner.setOnFocusChangeListener(new OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    getRemoveColorAnimation().start();
                } else {
                    getAddColorAnimation().start();
                }
            }
        });

        mSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position > 0) {
                    if (mHintView.getVisibility() == View.INVISIBLE) {
                        getEntranceAnimation().start();
                    }
                } else {
                    getExitAnimation().start();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                getExitAnimation().start();
            }
        });
    }

    protected ValueAnimator getAddColorAnimation() {
        if (mAddColorAnimation == null){
            mAddColorAnimation = ObjectAnimator.ofInt(mHintView, "textColor", HINT_DEFAULT_COLOR_DISABLED, mHintColor);
            mAddColorAnimation.setEvaluator(new ArgbEvaluator());
            mAddColorAnimation.setDuration(ANIMATION_DURATION);
        }
        return mAddColorAnimation;
    }

    protected ValueAnimator getRemoveColorAnimation(){
        if (mRemoveColorAnimation == null) {
            mRemoveColorAnimation = ObjectAnimator.ofInt(mHintView,
                    "textColor", mHintColor, HINT_DEFAULT_COLOR_DISABLED);
            mRemoveColorAnimation.setEvaluator(new ArgbEvaluator());
            mRemoveColorAnimation.setDuration(ANIMATION_DURATION);
        }
        return mRemoveColorAnimation;
    }

    protected AnimatorSet getExitAnimation() {
        if (mExitAnimation == null) {
            mExitAnimation = new AnimatorSet();
            mExitAnimation.playTogether(
                    ObjectAnimator.ofFloat(mHintView, "translationY", 0, 30),
                    ObjectAnimator.ofFloat(mHintView, "alpha", 1, 0));
            mExitAnimation.setDuration(ANIMATION_DURATION);
            mExitAnimation.addListener(new AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animation) {

                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    mHintView.setVisibility(View.INVISIBLE);
                }

                @Override
                public void onAnimationCancel(Animator animation) {

                }

                @Override
                public void onAnimationRepeat(Animator animation) {

                }
            });
        }
        return mExitAnimation;
    }

    @Override
    public void setOnTouchListener(OnTouchListener l) {
        mSpinner.setOnTouchListener(l);
        super.setOnTouchListener(l);
    }

    @Override
    public void setTag(Object tag) {
        mSpinner.setTag(tag);
        super.setTag(tag);
    }

    @Override
    public void setTag(int key, Object tag) {
        mSpinner.setTag(key, tag);
        super.setTag(key, tag);
    }

    protected AnimatorSet getEntranceAnimation(){
        if (mEntranceAnimation == null) {
            mEntranceAnimation = new AnimatorSet();
            mEntranceAnimation.playTogether(
                    ObjectAnimator.ofFloat(mHintView, "translationY", 30, 0),
                    ObjectAnimator.ofFloat(mHintView, "alpha", 0, 1));
            mEntranceAnimation.setDuration(ANIMATION_DURATION);
            mEntranceAnimation.addListener(new AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animation) {
                    mHintView.setVisibility(View.VISIBLE);
                }

                @Override
                public void onAnimationEnd(Animator animation) {

                }

                @Override
                public void onAnimationCancel(Animator animation) {

                }

                @Override
                public void onAnimationRepeat(Animator animation) {

                }
            });
        }
        return mEntranceAnimation;
    }

    public void setAnimationDuration(long duration) {
        ANIMATION_DURATION = duration;
    }

    public void setHint(String text) {
        mHintView.setText(text);
    }

    public void setTextSize(float size) {
        mHintView.setTextSize(size);
    }

    public void setTextSize(int unit, float size) {
        mHintView.setTextSize(unit, size);
    }

    public void setTypeface(Typeface tf, int style) {
        mHintView.setTypeface(tf, style);
    }

    public void setTypeface(Typeface tf) {
        mHintView.setTypeface(tf);
    }

    public void setTextColor(ColorStateList colors) {
        mHintView.setTextColor(colors);
    }

    public void setTextColor(int color) {
        mHintView.setTextColor(color);
    }

    public void setAdatper(ArrayAdapter<String> adapter){
        mSpinner.setAdapter(adapter);
    }

    public Object getSelectedItem() {
        return mSpinner.getSelectedItem();
    }
}