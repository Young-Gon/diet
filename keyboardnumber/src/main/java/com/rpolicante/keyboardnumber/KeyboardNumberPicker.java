package com.rpolicante.keyboardnumber;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.res.TypedArray;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.GridLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by Cooper Card on 09/03/2017.
 */

public class KeyboardNumberPicker extends DialogFragment {

    private static final String ARG_TAG = KeyboardNumberPicker.class.getPackage() + ".ARG_TAG";
	private static final String ARG_THEME = KeyboardNumberPicker.class.getPackage() + ".ARG_THEME";
    private static final String ARG_VALUE = KeyboardNumberPicker.class.getPackage() + ".ARG_VALUE";
    private static final String ARG_TITLE= KeyboardNumberPicker.class.getPackage() + ".ARG_TITLE";
    private static final String ARG_ITEM = KeyboardNumberPicker.class.getPackage() + ".ARG_ITEM";

    private TextView display;
    private int theme = R.style.KeyboardNumberTheme;
    private View keyboardNumberView;
    private ImageView backspace;
    private AlertDialog keyboardDialog;
    private String strValue = "";

    private int tag;
	private String itemName ="";
	private String title="";

	private Object item;
	public KeyboardNumberPicker setItem(Object object)
	{
		item=object;
		return this;
	}

	public Object getItem(){
		return item;
	}

    private static KeyboardNumberPicker newInstance(int tag, int theme, Object o, String title, String item, String defaultValue) {
        Bundle args = new Bundle();
        args.putInt(ARG_TAG, tag);
		args.putInt(ARG_THEME, theme);
		args.putString(ARG_TITLE, title);
		args.putString(ARG_ITEM, item);
		args.putString(ARG_VALUE, defaultValue);
        KeyboardNumberPicker fragment = new KeyboardNumberPicker();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        setRetainInstance(true);
        super.onCreate(savedInstanceState);

        if (null != savedInstanceState) {
            loadArguments(savedInstanceState);
        } else if (null != getArguments()) {
            loadArguments(getArguments());
        }

        setStyle(STYLE_NO_TITLE, theme);
    }

    @Override
    public void onDestroyView() {
        if (getDialog() != null && getRetainInstance()) {
            getDialog().setDismissMessage(null);
        }
        super.onDestroyView();
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        createDialog();
        setup(savedInstanceState);
        return keyboardDialog;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(ARG_TAG, tag);
		outState.putInt(ARG_THEME, theme);
		outState.putString(ARG_TITLE, title);
		outState.putString(ARG_ITEM, itemName);
        outState.putString(ARG_VALUE, strValue);
    }

    private void loadArguments(Bundle bundle){
        if (bundle.containsKey(ARG_TAG)){
            tag = bundle.getInt(ARG_TAG);
        }
		theme = bundle.getInt(ARG_THEME,R.style.KeyboardNumberTheme);
		title = bundle.getString(ARG_TITLE,"");
		itemName = bundle.getString(ARG_ITEM,"");
		strValue = bundle.getString(ARG_VALUE,"0");
    }

    private void createDialog() {
        LayoutInflater inflater = getActivity().getLayoutInflater();
        keyboardNumberView = inflater.inflate(R.layout.rpolicante_dialog_picker, null);
		((TextView)keyboardNumberView.findViewById(R.id.txt_dialog_title)).setText(title);
		((TextView)keyboardNumberView.findViewById(R.id.txt_item)).setText(itemName);

        keyboardDialog = new AlertDialog.Builder(getContext(), theme)
                .setView(keyboardNumberView)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        KeyboardNumberPickerHandler handler = getImplementsHandlerListener();
                        if (handler != null){
                            handler.onConfirmAction(KeyboardNumberPicker.this, strValue);
                        }
                        dismiss();
                    }
                })
                .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        KeyboardNumberPickerHandler handler = getImplementsHandlerListener();
                        if (handler != null){
                            handler.onCancelAction(KeyboardNumberPicker.this);
                        }
                        dismiss();
                    }
                })
				.setNeutralButton("삭제", new DialogInterface.OnClickListener()
				{
					@Override
					public void onClick(DialogInterface dialog, int which)
					{
						KeyboardNumberPickerHandler handler = getImplementsHandlerListener();
						if (handler != null)
						{
							handler.onDeleteAction(KeyboardNumberPicker.this);
						}
					}
				})
                .create();
    }

    private void setup(Bundle args) {
        @SuppressLint("Recycle")
        TypedArray attributes = getContext().obtainStyledAttributes(theme, R.styleable.KeyboardNumberPicker);

        int knpDisplayTextColor = attributes.getColor(R.styleable.KeyboardNumberPicker_knpDisplayTextColor, ContextCompat.getColor(getContext(), android.R.color.secondary_text_light));
        int knpDisplayBackgroundColor = attributes.getColor(R.styleable.KeyboardNumberPicker_knpDisplayBackgroundColor, ContextCompat.getColor(getContext(), android.R.color.transparent));
		int colorPrimary = attributes.getColor(R.styleable.KeyboardNumberPicker_colorPrimary, ContextCompat.getColor(getContext(), android.R.color.transparent));
		int titleTextColor = attributes.getColor(R.styleable.KeyboardNumberPicker_titleTextColor, ContextCompat.getColor(getContext(), android.R.color.transparent));

        display = keyboardNumberView.findViewById(R.id.rpolicante_dialog_picker_display);
        display.setBackgroundColor(knpDisplayBackgroundColor);
        display.setTextColor(knpDisplayTextColor);

        if (args != null && args.containsKey(ARG_VALUE)){
            strValue = args.getString(ARG_VALUE, "");
        }
        display.setText(strValue);
		strValue="";

		TextView txtTitle= keyboardNumberView.findViewById(R.id.txt_dialog_title);
		txtTitle.setBackgroundColor(colorPrimary);
		txtTitle.setTextColor(titleTextColor);

        int knpBackspaceTintColor = attributes.getColor(R.styleable.KeyboardNumberPicker_knpBackspaceTintColor, ContextCompat.getColor(getContext(), android.R.color.secondary_text_light));
        int knpBackspaceBackgroundColor = attributes.getColor(R.styleable.KeyboardNumberPicker_knpBackspaceBackgroundColor, ContextCompat.getColor(getContext(), android.R.color.transparent));
        backspace = keyboardNumberView.findViewById(R.id.rpolicante_dialog_picker_backspace);
        backspace.setColorFilter(knpBackspaceTintColor, PorterDuff.Mode.SRC_IN);
        backspace.getRootView().setBackgroundColor(knpBackspaceBackgroundColor);
        backspace.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateText("");
            }
        });
        backspace.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                updateText(null);
                return true;
            }
        });

        int knpBackgroundColor = attributes.getColor(R.styleable.KeyboardNumberPicker_knpBackgroundColor, ContextCompat.getColor(getContext(), android.R.color.background_light));
        keyboardDialog.getWindow().setBackgroundDrawable(new ColorDrawable(knpBackgroundColor));

        View.OnClickListener keyListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (v instanceof TextView) {
                    TextView key = (TextView) v;
                    String value = key.getText().toString();
                    updateText(value);
                }
            }
        };

        int knpKeysTextColor = attributes.getColor(R.styleable.KeyboardNumberPicker_knpKeysTextColor, ContextCompat.getColor(getContext(), android.R.color.secondary_text_light));
        int knpKeysBackgroundColor = attributes.getColor(R.styleable.KeyboardNumberPicker_knpKeysBackgroundColor, ContextCompat.getColor(getContext(), android.R.color.transparent));

        GridLayout grid = keyboardNumberView.findViewById(R.id.rpolicante_dialog_picker_grid);
        int childCount = grid.getChildCount();
        for (int i = 0; i < childCount; i++) {
            if (grid.getChildAt(i) instanceof TextView) {
                TextView key = (TextView) grid.getChildAt(i);
                key.setTextColor(knpKeysTextColor);
                key.setBackgroundColor(knpKeysBackgroundColor);
                key.setOnClickListener(keyListener);
            }
        }

        attributes.recycle();

    }

    @Override
    public void onStart() {
        super.onStart();
        onNumberChanged();
    }

    private void updateText(String value) {
        try{
            String oldValue = strValue;
            if (value != null) {
                if (value.isEmpty()) {
                    if (strValue.length() > 0) {
                        strValue = strValue.substring(0, strValue.length() - 1);
                    }
                } else {
                    strValue += value;
                }
            } else {
                strValue = "";
            }

            KeyboardNumberKeyListener keyListener = getImplementsKeyListener();
            if (keyListener != null) {
                keyListener.beforeKeyPressed(this, oldValue, value);
                keyListener.onTextChanged(this, oldValue, strValue, value);
            }

            KeyboardNumberFormatter formatter = getImplementsFormatterListener();
            if (formatter != null){
                strValue = formatter.formatNumber(this, strValue);
            }

            display.setText(strValue);

            if (keyListener != null) {
                keyListener.afterKeyPressed(this, strValue);
            }

            onNumberChanged();
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    private void onNumberChanged() {
        if (0 == display.getText().length()) {
            keyboardDialog.getButton(DialogInterface.BUTTON_POSITIVE).setEnabled(false);
        } else {
            keyboardDialog.getButton(DialogInterface.BUTTON_POSITIVE).setEnabled(true);
        }
    }

    public void clearKeyboard(){
        updateText(null);
    }

    private KeyboardNumberFormatter getImplementsFormatterListener(){
        final Activity activity = getActivity();
        final Fragment fragment = getParentFragment();
        if (activity instanceof KeyboardNumberFormatter){
            return (KeyboardNumberFormatter) activity;
        }else if (fragment instanceof KeyboardNumberFormatter){
            return (KeyboardNumberFormatter) fragment;
        }else{
            return null;
        }
    }

    private KeyboardNumberPickerHandler getImplementsHandlerListener(){
        final Activity activity = getActivity();
        final Fragment fragment = getParentFragment();
        if (activity instanceof KeyboardNumberPickerHandler){
            return (KeyboardNumberPickerHandler) activity;
        }else if (fragment instanceof KeyboardNumberPickerHandler){
            return (KeyboardNumberPickerHandler) fragment;
        }else{
            return null;
        }
    }

    private KeyboardNumberKeyListener getImplementsKeyListener(){
        final Activity activity = getActivity();
        final Fragment fragment = getParentFragment();
        if (activity instanceof KeyboardNumberKeyListener){
            return (KeyboardNumberKeyListener) activity;
        }else if (fragment instanceof KeyboardNumberKeyListener){
            return (KeyboardNumberKeyListener) fragment;
        }else{
            return null;
        }
    }


    public static class Builder {

        private int tag;
		private String title="";
		private String itemName ="";
		private String defaultValue="0";
		private int theme=R.style.KeyboardNumberTheme;
		private Object item;

		public Builder(int tag){
            this.tag = tag;
        }

        public Builder setTitle(String title)
		{
			this.title = title;
			return this;
		}

		public Builder setItemName(String itemName)
		{
			this.itemName = itemName;
			return this;
		}

		public Builder setDefaultValue(String defaultValue)
		{
			this.defaultValue = defaultValue;
			return this;
		}

		public Builder setTheme(int theme)
		{
			this.theme = theme;
			return this;
		}

		public Builder setItemName(Object item)
		{
			this.item = item;
			return this;
		}

		public KeyboardNumberPicker create() {
            return newInstance(tag, theme, item, title, itemName, defaultValue);
        }
    }

}
