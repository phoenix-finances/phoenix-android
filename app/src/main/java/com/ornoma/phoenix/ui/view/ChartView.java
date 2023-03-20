package com.ornoma.phoenix.ui.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.os.Build;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.content.ContextCompat;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;

import com.ornoma.phoenix.R;

/**
 * Created by de76 on 5/27/17.
 */

public class ChartView extends View {
    private static final String TAG = "ChartView";
    private CharPair[] xyMap = null;
    private String title = "";
    private String rangeText = "";
    private String max = "";

    private int COLOR_GRID_BASE = 0;
    private int COLOR_GRID_X = 0;
    private int COLOR_GRID_NX = 0;
    private int COLOR_GRID_BOTH = 0;
    private int COLOR_GRID_TEXT = 0;

    private boolean initialized = false;
    private DisplayMetrics displayMetrics;

    public ChartView(Context context) {
        super(context);
        if (!initialized) initialize(context);
    }

    public ChartView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        if (!initialized) initialize(context);
    }

    public ChartView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        if (!initialized) initialize(context);
    }

    private void initialize(Context context){
        COLOR_GRID_BASE = ContextCompat.getColor(context, R.color.graph_grid_base);
        COLOR_GRID_X = ContextCompat.getColor(context, R.color.graph_grid_x);
        COLOR_GRID_NX = ContextCompat.getColor(context, R.color.graph_grid_nx);
        COLOR_GRID_BOTH = ContextCompat.getColor(context, R.color.graph_grid_both);
        COLOR_GRID_TEXT = ContextCompat.getColor(context, R.color.graph_grid_text);
        displayMetrics = context.getResources().getDisplayMetrics();
        initialized = true;
    }

    public void setData(CharPair[] xyMap){this.xyMap = xyMap; this.invalidate();}
    public void setTitle(String title){this.title = title;}
    public void setRangeText(String rangeText){this.rangeText = rangeText;}

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public ChartView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        if (!initialized) initialize(context);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (isInEditMode())
            return;
        drawGrid(canvas);
        if (xyMap != null)
            drawGraph(canvas);
        drawText(canvas);
    }

    private int getPX(int dp){
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                dp, getResources().getDisplayMetrics());
    }

    private void drawText(Canvas canvas){
        Rect rect = new Rect();
        int textSize = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                10, getResources().getDisplayMetrics());

        TextPaint paint = new TextPaint(TextPaint.ANTI_ALIAS_FLAG);
        paint.setColor(COLOR_GRID_TEXT);
        paint.setStrokeWidth(5.0f);
        paint.setTextSize(textSize);
        paint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));

        Paint paintWhite = new Paint();
        paintWhite.setStyle(Paint.Style.FILL);
        paintWhite.setStrokeWidth(0);
        paintWhite.setColor(COLOR_GRID_BOTH);

        paint.getTextBounds(title, 0, title.length(), rect);
        canvas.drawRect(0, 0, 40 + rect.width(), 40 + rect.height(), paintWhite);
        canvas.drawText(title, 20, rect.height() + 20, paint);

        canvas.drawText(rangeText, 20, canvas.getHeight() - 20, paint);

        int padding = 10;
        paint.getTextBounds(max, 0, max.length(), rect);
        canvas.drawText(max, canvas.getWidth() - rect.width() - padding, rect.height() + padding, paint);
    }

    private void drawBox(Canvas canvas, int left, int top, int right, int bottom){
        Paint mPaint = new Paint();
        mPaint.setColor(Color.RED);
        Path mPath = new Path();
        RectF mRectF = new RectF(20, 20, 240, 240);
        mPath.addRect(mRectF, Path.Direction.CCW);
        mPaint.setStrokeWidth(20);
        mPaint.setStyle(Paint.Style.STROKE);
        canvas.drawPath(mPath, mPaint);
    }

    private void drawGrid(Canvas canvas){
        canvas.drawColor(Color.WHITE);
        Paint paint = new Paint();
        paint.setStyle(Paint.Style.STROKE);
        paint.setColor(COLOR_GRID_BASE);
        paint.setStrokeWidth(2.0f);

        int height = canvas.getHeight();
        int width = canvas.getWidth();

        Path path = new Path();
        path.moveTo(0, height/2);
        path.lineTo(width, height/2);

        path.moveTo(0,0);
        path.lineTo(width, 0);
        path.lineTo(width, height);
        path.lineTo(0, height);

        path.close();
        canvas.drawPath(path, paint);

        int minBoxWidth = 20;
        int ratio = width / 30;
        Log.d(TAG, "GridX Ratio" + ratio);

        Paint gridPaint = new Paint();
        gridPaint.setStyle(Paint.Style.STROKE);
        gridPaint.setColor(COLOR_GRID_BOTH);
        gridPaint.setStrokeWidth(2.0f);

        Path gridPath = new Path();
        for (int x = -2; x<=width; x+=ratio){
            gridPath.moveTo(x, 0);
            gridPath.lineTo(x, canvas.getHeight());
            //Log.d(TAG, "GridX " + x);
        }
        gridPath.close();
        canvas.drawPath(gridPath, gridPaint);

    }

    private void drawGraph(Canvas canvas){
        int height = canvas.getHeight();
        int width = canvas.getWidth();

        double maxValue = 0;
        for (CharPair temp: xyMap)
            if (maxValue < temp.getY2())
                maxValue = temp.getY2();

        for (CharPair temp: xyMap)
            if (maxValue < temp.getY1())
                maxValue = temp.getY1();
        max = "Max : " + (int)maxValue;

        int ratio = width / xyMap.length;
        int barWidth = (int)((float)ratio * 0.5f);

        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(barWidth);
        paint.setColor(COLOR_GRID_X);  //Change to what you want

        Path path = new Path();

        double y;
        int counter = 0;
        int xDif = ratio/2;
        for (int x = 0; x < width; x+=ratio){
            if (counter == xyMap.length) break;

            y = xyMap[counter].getY1();
            if (maxValue > 0)
                y = (y/maxValue) * (height/2 - 20);

            path.moveTo(x + xDif, height/2);
            path.lineTo(x + xDif, (height/2) - (int)y);

            y = xyMap[counter].getY2();
            if (maxValue > 0)
                y = (y/maxValue) * (height/2 - 20);

            path.moveTo(x + xDif, height/2);
            path.lineTo(x + xDif, (height/2) + (int)y);
            counter++;
        }

        canvas.drawPath(path, paint);
    }
}
