package com.teamyamm.yamm.app;

import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.text.style.ReplacementSpan;

/**
 * Created by parkjiho on 8/22/14.
 */
public class NameSpan extends ReplacementSpan {

    private int squareSize, textSize;
    private float x_padding, y_padding, round;
    private float lineSpacing, height;
    private Resources r;

    public NameSpan(Resources r, float x_padding, float y_padding,
                    float round, float lineSpacing, float height) {
        this.r = r;
        this.x_padding = x_padding;
        this.y_padding = y_padding;
        this.round = round;
        this.lineSpacing = lineSpacing;
        this.height = height;
    }

    @Override
    public int getSize(Paint paint, CharSequence text, int start, int end, Paint.FontMetricsInt fm) {
        textSize = (int)paint.measureText(text, start, end);
        squareSize = (int) (textSize + x_padding*2);
        return squareSize;
    }

    @Override
    public void draw(Canvas canvas, CharSequence text, int start, int end, float x, int top, int y, int bottom, Paint paint) {
        RectF rect = new RectF((int) x, top,
                (int) (x + textSize + 2 * x_padding), bottom - lineSpacing + y_padding*2);

        paint.setColor(r.getColor(R.color.selected_item_color));

        canvas.drawRoundRect(rect, round, round, paint);

        paint.setColor(Color.WHITE);
        canvas.drawText(text, start, end, x + x_padding , y + y_padding , paint);

    }

}