package com.andruby.live.ui.customviews;

import android.content.Context;
import android.content.res.TypedArray;
import android.text.InputFilter;
import android.text.Spanned;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.andruby.live.R;
import com.andruby.live.utils.Constants;
import com.andruby.live.utils.OtherUtils;

/**
 * @description: 文本修改控件，对控件EditText的简单封装，可以用来修改文本，并显示相关信息
 * 目前只有昵称设置用到
 * @author: Andruby
 * @time: 2016/12/17 10:23
 */
public class LineEditTextView extends LinearLayout {
    private String name;
    private boolean isBottom;
    private String content;
    private EditText evText;

    public LineEditTextView(Context context, AttributeSet attrs) {
        super(context, attrs);

        LayoutInflater.from(getContext()).inflate(R.layout.view_line_edit_text, this);
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.LineView, 0, 0);
        name = ta.getString(R.styleable.LineView_name);
        content = ta.getString(R.styleable.LineView_content);
        isBottom = ta.getBoolean(R.styleable.LineView_isBottom, false);
        initView();
    }

    private void initView() {
        TextView tvName = (TextView) findViewById(R.id.ett_name);
        tvName.setText(name);
        evText = (EditText) findViewById(R.id.ett_content);
        evText.setText(content);
        View bottomLine = findViewById(R.id.ett_bottomLine);
        bottomLine.setVisibility(isBottom ? VISIBLE : GONE);
    }

    public void addTextChangedListener(TextWatcher watcher) {
        evText.addTextChangedListener(watcher);
    }


    /**
     * 设置EditText内容
     */
    public void setContent(String content) {
        evText.setText(content);
        evText.setSelection(evText.getText().length());
    }

    /**
     * 获取EditText内容
     */
    public String getContent() {
        return evText.getText().toString();
    }


    /**
     * contentEditView可输入最大长度限制检测
     *
     * @param max_length 可输入最大长度
     * @param err_msg    达到可输入最大长度时的提示语
     */
    private void filterLength(final int max_length, final String err_msg) {
        InputFilter[] filters = new InputFilter[1];
        filters[0] = new InputFilter.LengthFilter(max_length) {
            @Override
            public CharSequence filter(CharSequence source, int start, int end,
                                       Spanned dest, int dstart, int dend) {
                int destLen = OtherUtils.getCharacterNum(dest.toString()); //获取字符个数(一个中文算2个字符)
                int sourceLen = OtherUtils.getCharacterNum(source.toString());
                if (destLen + sourceLen > max_length) {
                    evText.setError(err_msg);
                    return "";
                }
                return source;
            }
        };
        evText.setFilters(filters);
    }

}
