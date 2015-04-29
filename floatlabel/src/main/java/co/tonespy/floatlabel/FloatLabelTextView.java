package co.tonespy.floatlabel;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.text.method.KeyListener;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.nineoldandroids.animation.Animator;
import com.nineoldandroids.animation.Animator.AnimatorListener;
import com.nineoldandroids.animation.AnimatorSet;
import com.nineoldandroids.animation.ArgbEvaluator;
import com.nineoldandroids.animation.ObjectAnimator;
import com.nineoldandroids.animation.ValueAnimator;


public class FloatLabelTextView extends LinearLayout {
    private static final String TAG = "FloatLabelTextView";

    private static long ANIMATION_DURATION = 400;
    private static int HINT_DEFAULT_COLOR = 0xFF808080;
    private static int EDIT_DEFAULT_COLOR = 0xFF808080;
    private static final int HINT_DEFAULT_COLOR_DISABLED = 0xFF2F6CB3;
    private static int HINT_DEFAULT_SIZE = 13;
    private static int EDIT_TEXT_INPUT_TYPE = InputType.TYPE_CLASS_TEXT|InputType.TYPE_TEXT_FLAG_CAP_SENTENCES;

    private TextView mHintView;
    private EditText mEditText;

    private int mHintColor;
    private int mEditColor;

    private AnimatorSet mEntranceAnimation = null;
    private AnimatorSet mExitAnimation = null;
    private ValueAnimator mAddColorAnimation = null;
    private ValueAnimator mRemoveColorAnimation = null;

    public FloatLabelTextView(Context context) {
        this(context, null);
    }

    public FloatLabelTextView(Context context, AttributeSet attrs) {
        super(context, attrs);

        setOrientation(LinearLayout.VERTICAL);
        setGravity(Gravity.LEFT);

        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.float_label_textview, this, true);

        TypedArray array = context.obtainStyledAttributes(attrs,
                R.styleable.FloatLabelTextView, 0, 0);
        String hint = array
                .getString(R.styleable.FloatLabelTextView_editText_hint);
        mEditColor = array.getColor(
                R.styleable.FloatLabelTextView_editText_color,
                EDIT_DEFAULT_COLOR);
        mHintColor = array.getColor(
                R.styleable.FloatLabelTextView_hint_color,
                HINT_DEFAULT_COLOR);
        String typeface = array.getString(R.styleable.FloatLabelTextView_typeface);

        int inputType = array.getInt(R.styleable.FloatLabelTextView_android_inputType, EDIT_TEXT_INPUT_TYPE);

        boolean singleLine = array.getBoolean(R.styleable.FloatLabelTextView_single_line, true);

        float size = array
                .getDimension(R.styleable.FloatLabelTextView_editText_size,
                        HINT_DEFAULT_SIZE);
        array.recycle();

        mHintView = (TextView) findViewById(R.id.textview_float);
        mEditText = (EditText) findViewById(R.id.textview_main);

        mHintView.setTextColor(HINT_DEFAULT_COLOR_DISABLED);
        mEditText.setTextColor(mEditColor);
        mEditText.setHintTextColor(mHintColor);

        mEditText.setTypeface(Typeface.create(typeface, Typeface.NORMAL));
        mHintView.setTypeface(Typeface.create(typeface, Typeface.NORMAL));

        //setGravity(Gravity.LEFT);
//        mEditText.setGravity(Gravity.CENTER);

        mHintView.setText(hint);
        mEditText.setHint(hint);
        mEditText.setInputType(inputType);
        mEditText.setSingleLine(singleLine);

        mHintView.setTextSize(size);

        mEditText.setOnFocusChangeListener(new OnFocusChangeListener() {

            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    getRemoveColorAnimation().start();
                } else {
                    getAddColorAnimation().start();
                }
            }
        });

        mEditText.addTextChangedListener(new TextWatcher() {

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() > 0) {
                    if (mHintView.getVisibility() == View.INVISIBLE) {
                        getEntranceAnimation().start();
                    }
                } else {
                    getExitAnimation().start();
                }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before,
                                      int count) {
            }
        });
    }

    public void addTextChangedListener(TextWatcher watcher) {
        mEditText.addTextChangedListener(watcher);
    }

    protected ValueAnimator getAddColorAnimation() {
        if (mAddColorAnimation == null) {
            mAddColorAnimation = ObjectAnimator.ofInt(mHintView, "textColor",
                    HINT_DEFAULT_COLOR_DISABLED, mHintColor);
            mAddColorAnimation.setEvaluator(new ArgbEvaluator());
            mAddColorAnimation.setDuration(ANIMATION_DURATION);
        }
        return mAddColorAnimation;
    }

    protected ValueAnimator getRemoveColorAnimation() {
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
                public void onAnimationRepeat(Animator animation) {
                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    mHintView.setVisibility(View.INVISIBLE);
                }

                @Override
                public void onAnimationCancel(Animator animation) {
                }
            });
        }

        return mExitAnimation;
    }

    @Override
    public void setOnClickListener(OnClickListener l) {
        mEditText.setOnClickListener(l);
        super.setOnClickListener(l);
    }

    @Override
    public void setTag(Object tag) {
        mEditText.setTag(tag);
        super.setTag(tag);
    }

    @Override
    public void setTag(int key, Object tag) {
        mEditText.setTag(key, tag);
        super.setTag(key, tag);
    }

    protected AnimatorSet getEntranceAnimation() {
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
                public void onAnimationRepeat(Animator animation) {
                }

                @Override
                public void onAnimationEnd(Animator animation) {
                }

                @Override
                public void onAnimationCancel(Animator animation) {
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
        mEditText.setHint(text);
    }

    public void setKeyListener(KeyListener input) {
        mEditText.setKeyListener(input);
    }

    public void setText(CharSequence text) {
        mEditText.setText(text);
    }

    public void setText(String text) {
        mEditText.setText(text);
    }

    public void setTextColor(ColorStateList colors) {
        mHintView.setTextColor(colors);
    }

    public void setTextColor(int color) {
        mHintView.setTextColor(color);
    }

    public Editable getText() {
        return mEditText.getText();
    }

    public void setTextSize(float size) {
        mHintView.setTextSize(size);
    }

    public void setTextSize(int unit, float size) {
        mHintView.setTextSize(unit, size);
    }

    public void setTypeface(Typeface tf, int style) {
        mHintView.setTypeface(tf, style);
        mEditText.setTypeface(tf, style);
    }

    public void setTypeface(Typeface tf) {
        mHintView.setTypeface(tf);
        mEditText.setTypeface(tf);
    }

    public void setError(String error) {
        mEditText.setError(error);
    }

    public void setInputType(int inputType) {
        mEditText.setInputType(inputType);
    }

    public void setTextGravity(int textGravity) {
        mEditText.setGravity(textGravity);
    }

}
