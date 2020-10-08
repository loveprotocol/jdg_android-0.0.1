package com.imhc2.plumboard.commons.util;

import android.support.design.widget.TextInputLayout;

import com.mobsandgeeks.saripaar.adapter.ViewDataAdapter;
import com.mobsandgeeks.saripaar.exception.ConversionException;

/**
 * Created by user on 2018-07-18.
 */

public class TextInputLayoutAdapter implements ViewDataAdapter<TextInputLayout,String>{
    @Override
    public String getData(TextInputLayout view) throws ConversionException {
        return getText(view);
    }
    private String getText(TextInputLayout til){
        return til.getEditText().getText().toString();
    }
}
