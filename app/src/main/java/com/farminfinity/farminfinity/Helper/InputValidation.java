package com.farminfinity.farminfinity.Helper;

import android.app.Activity;
import android.content.Context;
import android.util.Patterns;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AutoCompleteTextView;
import android.widget.MultiAutoCompleteTextView;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.regex.Pattern;

public class InputValidation {
    private Context context;

    public InputValidation(Context context) {
        this.context = context;
    }

    public boolean isInputEditTextFilled(TextInputEditText textInputEditText, TextInputLayout textInputLayout, String message){
        String value = textInputEditText.getText().toString().trim();
        if(value.isEmpty()){
            textInputLayout.setError(message);
            hideKeyboardFrom(textInputEditText);
            return false;
        }
        else {
            textInputLayout.setErrorEnabled(false);
        }
        return true;
    }

    public boolean isInputEditTextValidAccountNo(TextInputEditText textInputEditText, TextInputLayout textInputLayout, String message){
        String value = textInputEditText.getText().toString().trim();
        if(!(value.length() >=6)){
            textInputLayout.setError(message);
            hideKeyboardFrom(textInputEditText);
            return false;
        }
        else {
            textInputLayout.setErrorEnabled(false);
        }
        return true;
    }

    public boolean isInputEditTextValidPan(TextInputEditText textInputEditText, TextInputLayout textInputLayout, String message){
        String value = textInputEditText.getText().toString().trim();
        if(value.length() !=10){
            textInputLayout.setError(message);
            hideKeyboardFrom(textInputEditText);
            return false;
        }
        else {
            textInputLayout.setErrorEnabled(false);
        }
        return true;
    }

    public boolean isInputEditTextValidAadhar(TextInputEditText textInputEditText, TextInputLayout textInputLayout, String message){
        String value = textInputEditText.getText().toString().trim();
        if(value.length() !=14){
            textInputLayout.setError(message);
            hideKeyboardFrom(textInputEditText);
            return false;
        }
        else {
            textInputLayout.setErrorEnabled(false);
        }
        return true;
    }

    public boolean isInputEditTextValidDl(TextInputEditText textInputEditText, TextInputLayout textInputLayout, String message){
        String value = textInputEditText.getText().toString().trim();
        if(value.length() !=15){
            textInputLayout.setError(message);
            hideKeyboardFrom(textInputEditText);
            return false;
        }
        else {
            textInputLayout.setErrorEnabled(false);
        }
        return true;
    }

    public boolean isInputEditTextValidMnrega(TextInputEditText textInputEditText, TextInputLayout textInputLayout, String message){
        String value = textInputEditText.getText().toString().trim();
        if(value.length() !=17){
            textInputLayout.setError(message);
            hideKeyboardFrom(textInputEditText);
            return false;
        }
        else {
            textInputLayout.setErrorEnabled(false);
        }
        return true;
    }

    public boolean isInputEditTextValidPassport(TextInputEditText textInputEditText, TextInputLayout textInputLayout, String message){
        String value = textInputEditText.getText().toString().trim();
        if(value.length() !=15){
            textInputLayout.setError(message);
            hideKeyboardFrom(textInputEditText);
            return false;
        }
        else {
            textInputLayout.setErrorEnabled(false);
        }
        return true;
    }

    public boolean isInputEditTextValidVoterCard(TextInputEditText textInputEditText, TextInputLayout textInputLayout, String message){
        String value = textInputEditText.getText().toString().trim();
        if(value.length() !=10){
            textInputLayout.setError(message);
            hideKeyboardFrom(textInputEditText);
            return false;
        }
        else {
            textInputLayout.setErrorEnabled(false);
        }
        return true;
    }

    public boolean isMultiAutoCompleteTextView(MultiAutoCompleteTextView multiAutoCompleteTextView, TextInputLayout textInputLayout, String message){
        String value = multiAutoCompleteTextView.getText().toString().trim();
        if(value.isEmpty()){
            textInputLayout.setError(message);
            hideKeyboardFrom(multiAutoCompleteTextView);
            return false;
        }
        else {
            textInputLayout.setErrorEnabled(false);
        }
        return true;
    }

    public boolean isAutoCompleteTextViewFilled(AutoCompleteTextView autoCompleteTextView, TextInputLayout textInputLayout, String message){
        String value = autoCompleteTextView.getText().toString().trim();
        if(value.isEmpty()){
            textInputLayout.setError(message);
            hideKeyboardFrom(autoCompleteTextView);
            return false;
        }
        else {
            textInputLayout.setErrorEnabled(false);
        }
        return true;
    }

    public boolean isInputEditTextPositiveInteger(TextInputEditText textInputEditText, TextInputLayout textInputLayout, String message){
        String value = textInputEditText.getText().toString().trim();
        if(!Pattern.matches("^[1-9]\\d*$", value)){
            textInputLayout.setError(message);
            hideKeyboardFrom(textInputEditText);
            return false;
        }
        else {
            textInputLayout.setErrorEnabled(false);
        }
        return true;
    }

    public boolean isInputEditTextPasswordMatch(TextInputEditText textInputEditTextNewPassword, TextInputEditText textInputEditTextConfirmPassword, TextInputLayout textInputLayout, String message){
        String value = textInputEditTextNewPassword.getText().toString().trim();
        String value1 = textInputEditTextConfirmPassword.getText().toString().trim();
        if (!value.equals(value1)){
            textInputLayout.setError(message);
            hideKeyboardFrom(textInputEditTextConfirmPassword);
            return false;
        }
        else {
            textInputLayout.setErrorEnabled(false);
        }
        return true;
    }

    public boolean isInputEditTextEmail(TextInputEditText textInputEditText, TextInputLayout textInputLayout, String message){
        String value = textInputEditText.getText().toString().trim();
        if(!value.isEmpty() && !Patterns.EMAIL_ADDRESS.matcher(value).matches()){
            textInputLayout.setError(message);
            hideKeyboardFrom(textInputEditText);
            return false;
        }
        else {
            textInputLayout.setErrorEnabled(false);
        }
        return true;
    }

    public boolean isInputEditTextPhone(TextInputEditText textInputEditText, TextInputLayout textInputLayout, String message){
        String value = textInputEditText.getText().toString().trim();
        if(value.isEmpty()|| !Patterns.PHONE.matcher(value).matches()|| value.length()!=10){
            textInputLayout.setError(message);
            hideKeyboardFrom(textInputEditText);
            return false;
        }
        else {
            textInputLayout.setErrorEnabled(false);
        }
        return true;
    }

    private void hideKeyboardFrom(View view) {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Activity.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
    }
}
