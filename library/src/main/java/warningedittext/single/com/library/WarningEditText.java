package warningedittext.single.com.library;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.text.InputType;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;

/**
 * Created by xiangcheng on 16/11/11.
 */
public class WarningEditText extends FrameLayout {

    private static final String TAG = WarningEditText.class.getSimpleName();
    private String text;
    private int duration;

    private int color = Color.parseColor("#9999CC");
    private EditText et;
    private WarningText tv;
    private float startSize = Utils.sp2px(getContext(), 15);
    private float endSize = Utils.sp2px(getContext(), 10);
    private TextAlign align;
    private boolean hasLayout;

    public boolean isHasWarning() {
        if (tv != null) {
            return tv.getStatus() == WarningText.Status.WarningStart;
        }
        return false;
    }

    public enum TextAlign {
        LeftTop, RightTop, LeftButtom, RightButtom
    }

    public WarningEditText(Context context) {
        super(context);
    }

    public WarningEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.WarningEditText);
        text = array.getString(R.styleable.WarningEditText_warning_text);
        int intAlign = array.getInt(R.styleable.WarningEditText_warning_align, 1);
        align = getAlign(intAlign);
        color = array.getColor(R.styleable.WarningEditText_waring_color, color);
        duration = array.getInt(R.styleable.WarningEditText_warning_duration, duration);
        startSize = array.getDimension(R.styleable.WarningEditText_warning_start_size, startSize);
        endSize = array.getDimension(R.styleable.WarningEditText_warning_end_size, endSize);
        initViews();
    }

    private TextAlign getAlign(int intAlign) {
        switch (intAlign) {
            case 1:
                return TextAlign.LeftTop;
            case 2:
                return TextAlign.RightTop;
            case 3:
                return TextAlign.LeftButtom;
            case 4:
                return TextAlign.RightButtom;
        }
        return null;
    }

    private void initViews() {
        setBackgroundResource(R.drawable.warning_back);
        et = new EditText(getContext());
        et.setBackground(null);
        et.setGravity(Gravity.LEFT);
        et.setInputType(InputType.TYPE_CLASS_NUMBER);
        tv = new WarningText(getContext(), text, align);
        FrameLayout.LayoutParams tvParams = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        FrameLayout.LayoutParams etParams = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        addView(et, etParams);
        addView(tv, tvParams);
        et.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (isHasWarning() && tv.isHasWarning()) {
                    tv.setHasWarning(false);
                    tv.startWaring();
                }
                return false;
            }
        });
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        if (!hasLayout) {
            tv.setParent(this);
            if (align == TextAlign.LeftTop || align == TextAlign.RightTop) {
                et.setPadding(0, (int) (tv.getTextHeight(endSize) + 0.5), 0, 0);
            } else {
                et.setPadding(0, 0, 0, (int) (tv.getTextHeight(endSize) + 0.5));
            }
            hasLayout = true;
        }
    }

    public void startWarning() {
        if (tv != null) {
            tv.startWaring();
        }
    }

    public void setWarning(boolean warning) {
        if (tv != null) {
            tv.setHasWarning(warning);
        }
    }

    public boolean getWarning() {
        return tv.isHasWarning();
    }

    public String getText() {
        return et.getText().toString();
    }

}
