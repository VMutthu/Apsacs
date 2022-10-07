package com.entrolabs.apsacs.Common;

import android.text.method.PasswordTransformationMethod;
import android.view.View;

/**
 * Created by KK on 13-09-2017.
 */

public class ChangeTransformationMethod extends PasswordTransformationMethod {
    @Override
    public CharSequence getTransformation(CharSequence source, View view) {
        return new PasswordCharSequence(source);
    }

    private class PasswordCharSequence implements CharSequence {
        private CharSequence mSource;
        public PasswordCharSequence(CharSequence source) {
            mSource = source;
        }
        public char charAt(int index) {
            if(index <=7)
                return '*';
            else
                return mSource.charAt(index);
        }
        public int length() {
            return mSource.length();
        }
        public CharSequence subSequence(int start, int end) {
            return mSource.subSequence(start, end);
        }
    }
}