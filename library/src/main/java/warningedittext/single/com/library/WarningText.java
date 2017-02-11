package warningedittext.single.com.library;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.Log;
import android.util.Property;
import android.view.View;
import android.view.animation.DecelerateInterpolator;

/**
 * Created by xiangcheng on 16/11/11.
 */
public class WarningText extends View {

    private static final String TAG = WarningText.class.getSimpleName();
    private String text;
    private float textSize;
    private int color = Color.parseColor("#9999CC");
    private Paint paint;
    private Rect rect;
    private float textX;
    private float textY;
    private AnimatorSet animatorSet;
    private boolean hasWarning;
    private View parent;
    private float startX;
    private float startY;
    private float endX;
    private float endY;
    private float startSize = Utils.sp2px(getContext(), 15);
    private float endSize = Utils.sp2px(getContext(), 10);
    private int duration = 800;
    private WarningEditText.TextAlign align = WarningEditText.TextAlign.LeftTop;
    private int startAlpha;
    private int endAlpha;
    private Paint measurePaint;
    private Status status = Status.WarningStart;

    enum Status {
        WarningStart, Warning
    }

    public WarningText(Context context) {
        super(context);
    }

    public WarningText(Context context, String text, WarningEditText.TextAlign align) {
        super(context);
        this.text = text;
        this.align = align;
    }

    public WarningText(Context context, String text, int color, int duration) {
        super(context);
        this.text = text;
        this.color = color;
        this.duration = duration;
    }

    public WarningText(Context context, String text, int color, int duration, float startSize, float endSize,
                       WarningEditText.TextAlign align) {
        super(context);
        this.text = text;
        this.color = color;
        this.duration = duration;
        this.startSize = startSize;
        this.endSize = endSize;
        this.align = align;
    }

    private void initArgus() {
        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setTextSize(startSize);
        paint.setColor(color);
        paint.setStrokeWidth(Utils.dp2px(getContext(), 0.05f));
        paint.setStyle(Paint.Style.STROKE);

        measurePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        measurePaint.setTextSize(textSize);
        measurePaint.setColor(color);
        measurePaint.setStrokeWidth(Utils.dp2px(getContext(), 0.05f));
        measurePaint.setStyle(Paint.Style.STROKE);

        rect = new Rect();
        animatorSet = new AnimatorSet();
        calculate(true);
        Log.d(TAG, "textY:" + textY);
    }

    public void setParent(View parent) {
        this.parent = parent;
        initArgus();
    }

    public void startWaring() {
        calculate(false);
        if (status == Status.WarningStart) {
            status = Status.Warning;
            final ValueAnimator textSizeAnimator = ValueAnimator.ofFloat(startSize, endSize);
            textSizeAnimator.setDuration(duration);
            textSizeAnimator.addListener(new SimpleAnimatorListener() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    textSizeAnimator.cancel();
                }
            });
            textSizeAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    Log.d(TAG, "textSize:" + animation.getAnimatedValue());
                    textSize = (float) animation.getAnimatedValue();
                    paint.setTextSize(textSize);
                    invalidate();
                }
            });
            //文字透明度的变化动画对象
            final ValueAnimator textAlphaAnimator = ValueAnimator.ofInt(startAlpha, endAlpha);
            textAlphaAnimator.setDuration(duration);
            textAlphaAnimator.setRepeatCount(2);
            textAlphaAnimator.setRepeatMode(ValueAnimator.REVERSE);
            textAlphaAnimator.addListener(new SimpleAnimatorListener() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    textAlphaAnimator.cancel();
                }
            });
            textAlphaAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    //在此处需要给画笔至上透明度
                    int alpha = (int) animation.getAnimatedValue();
                    paint.setColor(setAlphaComponent(color, alpha));
                }
            });

            final ObjectAnimator textXAnimator = ObjectAnimator.ofFloat(this, textXProperty, startX, endX);
            textXAnimator.setDuration(duration);
            textXAnimator.setInterpolator(new DecelerateInterpolator(2));
            textXAnimator.addListener(new SimpleAnimatorListener() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    textXAnimator.cancel();
                }
            });
            final ObjectAnimator textYAnimator = ObjectAnimator.ofFloat(this, textYProperty, startY, endY);
            textYAnimator.setDuration(duration);
            textYAnimator.setInterpolator(new DecelerateInterpolator(2));
            textYAnimator.addListener(new SimpleAnimatorListener() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    textYAnimator.cancel();
                    status = Status.WarningStart;
                }

            });
            animatorSet.playTogether(textSizeAnimator, textAlphaAnimator, textXAnimator, textYAnimator);
            animatorSet.start();

        }
    }

    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);
        canvas.drawText(text, textX, textY, paint);
    }

    public float getTextHeight(float targetSize) {
        measurePaint.setTextSize(targetSize);
        measurePaint.getTextBounds(text, 0, text.length(), rect);
        return rect.height();
    }

    public float getTextWidth(float targetSize) {
        measurePaint.setTextSize(targetSize);
        return measurePaint.measureText(text);
    }

    private void calculate(boolean isStart) {
        //如果是没有移动到提示相应的位置的话，计算达到提示位置的坐标
        float tempTextSize;
        if (!hasWarning) {
            startSize = Utils.sp2px(getContext(), 15);
            endSize = Utils.sp2px(getContext(), 10);
            if (isStart) {
                //初始的位置坐标
                textX = parent.getPaddingLeft();
                textY = (float) (parent.getMeasuredHeight() * 1.0 / 2 + getTextHeight(startSize) * 1.0 / 2);
                hasWarning = true;
            } else {
                switch (align) {
                    case LeftTop:
                        startX = textX;
                        startY = textY;
                        endX = startX;
                        endY = parent.getPaddingTop() + getTextHeight(endSize);
                        break;
                    case RightTop:
                        startX = textX;
                        startY = textY;
                        endX = (float) (parent.getMeasuredWidth() - parent.getPaddingRight() - 1.2 * getTextWidth(endSize));
                        endY = parent.getPaddingTop() + getTextHeight(endSize);
                        break;
                    case LeftButtom:
                        startX = textX;
                        startY = textY;
                        endX = startX;
                        endY = (float) (parent.getMeasuredHeight() - parent.getPaddingBottom() - getTextHeight(endSize) * 1.0 / 2);
                        break;
                    case RightButtom:
                        startX = textX;
                        startY = textY;
                        endX = (float) (parent.getMeasuredWidth() - parent.getPaddingRight() - 1.2 * getTextWidth(endSize));
                        endY = (float) (parent.getMeasuredHeight() - parent.getPaddingBottom() - getTextHeight(endSize) * 1.0 / 2);
                        break;
                }
                startAlpha = 255;
                endAlpha = 100;
            }

        } else {
            startAlpha = 100;
            endAlpha = 255;
            startX = endX;
            startY = endY;
            endX = parent.getPaddingLeft();
            endY = (float) (parent.getMeasuredHeight() * 1.0 / 2 + getTextHeight(startSize) * 1.0 / 2);
            tempTextSize = startSize;
            startSize = endSize;
            endSize = tempTextSize;
        }
    }

    public float getTextX() {
        return textX;
    }

    public void setTextX(float textX) {
        this.textX = textX;
    }

    public float getTextY() {
        return textY;
    }

    public void setTextY(float textY) {
        this.textY = textY;
    }

    Property<WarningText, Float> textXProperty = new Property<WarningText, Float>(Float.class, "textX") {
        @Override
        public Float get(WarningText object) {
            return object.getTextX();
        }

        @Override
        public void set(WarningText object, Float value) {
            object.setTextX(value);
        }
    };

    Property<WarningText, Float> textYProperty = new Property<WarningText, Float>(Float.class, "textY") {
        @Override
        public Float get(WarningText object) {
            return object.getTextY();
        }

        @Override
        public void set(WarningText object, Float value) {
            object.setTextY(value);
        }
    };

    private class SimpleAnimatorListener implements Animator.AnimatorListener {

        @Override
        public void onAnimationStart(Animator animation) {

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
    }

    public int setAlphaComponent(int color, int alpha) {
        if (alpha < 0 || alpha > 255) {
            throw new IllegalArgumentException("alpha must be between 0 and 255.");
        }
        return (color & 0x00ffffff) | (alpha << 24);
    }

    public void setHasWarning(boolean hasWarning) {
        this.hasWarning = hasWarning;
    }

    public boolean isHasWarning() {
        return hasWarning;
    }

    public Status getStatus() {
        return status;
    }
}
