package vn.binhnc.banhanga.widget;

import android.content.Context;
import android.text.TextPaint;
import android.text.TextUtils;
import android.util.AttributeSet;

import androidx.annotation.NonNull;

import com.google.android.material.appbar.CollapsingToolbarLayout;

public class CustomCollapsingToolbarLayout extends CollapsingToolbarLayout {

    private static final int MAX_TITLE_LENGTH = 10;

    private CharSequence customTitle;

    public CustomCollapsingToolbarLayout(@NonNull Context context) {
        super(context);
    }

    public CustomCollapsingToolbarLayout(@NonNull Context context, @NonNull AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    public CustomCollapsingToolbarLayout(@NonNull Context context, @NonNull AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }

    private void init(AttributeSet attrs) {
        customTitle = attrs.getAttributeValue("http://schemas.android.com/apk/res-auto", "title");
        if (!TextUtils.isEmpty(customTitle)) {
            setTitle(customTitle);
        }
    }

    @Override
    public void setTitle(CharSequence title) {
        customTitle = title;
        super.setTitle(getEllipsizedText(title));
    }

    public CharSequence getEllipsizedText(CharSequence text) {
        if (!TextUtils.isEmpty(text) && text.length() > MAX_TITLE_LENGTH) {
            return text.subSequence(0, MAX_TITLE_LENGTH) + "...";
        }
        return text;
    }
}