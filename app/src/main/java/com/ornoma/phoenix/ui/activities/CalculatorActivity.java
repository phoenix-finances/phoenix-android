package com.ornoma.phoenix.ui.activities;

import android.content.Intent;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.ornoma.phoenix.R;

public class CalculatorActivity extends AppCompatActivity {
    public static final String KEY_MODE = "_key_mode";
    public static final String MODE_SELECTION = "_mode_selection";
    public static final String KEY_RESULT = "_key_result";

    private static final int ACTION_EQUALS = 0;
    private static final int ACTION_ADD = 1;
    private static final int ACTION_MINUS = 2;
    private static final int ACTION_STAR = 3;
    private static final int ACTION_SLASH = 4;

    private TextView textViewResult, textViewPreviousResult;
    private boolean isInSelectionMode = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calculator);

        bindActivity();
        if (getIntent().hasExtra(KEY_MODE))
            isInSelectionMode = getIntent().getStringExtra(KEY_MODE).equals(MODE_SELECTION);
    }

    private void bindActivity(){
        textViewResult = (TextView)findViewById(R.id.t_result);
        textViewPreviousResult = (TextView)findViewById(R.id.t_previous_result);
    }

    @Override
    protected void onStart() {
        super.onStart();
        currentExpression = "";
        previousExpression = "";
    }

    private String currentExpression, previousExpression;
    private int lastAction = 1, actionBeforeLast = 0;

    private void addCharacter(String character){
        currentExpression += character;
        textViewResult.setText(currentExpression);
    }

    private void onEquals(){
        handleAction(0, "=");
        if (isInSelectionMode){
            Intent intent = new Intent();
            intent.putExtra(KEY_RESULT, previousExpression);
            setResult(RESULT_OK, intent);
            finish();
        }
    }

    private void handleAction(int action, String sign){
        double value = 0;
        double previousValue = 0;
        double result = 0;
        try{value = Double.parseDouble(currentExpression);}
        catch (Exception e){}
        try{previousValue = Double.parseDouble(previousExpression);}
        catch (Exception e){}
        switch (lastAction){
            case ACTION_ADD:
                result = value + previousValue;
                break;
            case ACTION_MINUS:
                result = value - previousValue;
                break;
            case ACTION_STAR:
                result = value * previousValue;
                break;
            case ACTION_SLASH:
                if (previousValue != 0)
                    result = value / previousValue;
                break;
        }
        previousExpression = String.valueOf(result);
        currentExpression = "";
        textViewResult.setText("0");
        actionBeforeLast = lastAction;
        lastAction = action;
        String previousText = previousExpression + sign;
        textViewPreviousResult.setText(previousText);
    }

    public void onAction(View view){
        int id = view.getId();
        switch (id){
            case R.id.t_equals:
                onEquals();
                break;
            case R.id.t_plus:
                handleAction(ACTION_ADD, ((TextView)view).getText().toString());
                break;
            case R.id.t_minus:
                handleAction(ACTION_MINUS, ((TextView)view).getText().toString());
                break;
            case R.id.t_star:
                handleAction(ACTION_STAR, ((TextView)view).getText().toString());
                break;
            case R.id.t_slash:
                handleAction(ACTION_SLASH, ((TextView)view).getText().toString());
                break;
            default:
                TextView textView = (TextView)view;
                addCharacter(textView.getText().toString());
                break;
        }
    }
}
