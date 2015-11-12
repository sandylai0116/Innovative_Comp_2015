package com.project.piano;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

import android.graphics.RectF;

import android.util.AttributeSet;
import android.view.View;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by LAI on 2015/11/6.
 */
public class PianoSheet extends View {
    // defines paint and canvas
    private Paint paint;
    private List<Integer> noteList = new ArrayList<>();
    private int currentPosition = -1;

    public List<Integer> getNoteList() {
        return noteList;
    }

    public void setNoteList(List<Integer> noteList) {
        this.noteList = noteList;
    }

    public void setCurrentPosition(int currentPosition) {
        this.currentPosition = currentPosition;
    }

    public PianoSheet(Context context, AttributeSet attrs) {
        super(context, attrs);
        setFocusable(true);
        setFocusableInTouchMode(true);
        setupPaint();
    }

    // Setup paint with color and stroke styles
    private void setupPaint() {
        paint = new Paint();
        paint.setColor(Color.BLACK);
        paint.setAntiAlias(true);
        paint.setStrokeWidth(5);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeJoin(Paint.Join.ROUND);
        paint.setStrokeCap(Paint.Cap.ROUND);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width = 20000;
        int height = 800;
        setMeasuredDimension(width, height);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        generateNewPianoSheet(canvas, paint);
        Iterator<Integer> it = noteList.iterator();
        int i =0;
        while (it.hasNext()) {
            if(i==currentPosition) paint.setColor(Color.RED);
            drawNatural(canvas, it.next());
            paint.setColor(Color.BLACK);
            i++;
        }
    }

    public void generateNewPianoSheet(Canvas canvas, Paint paint){
        canvas.drawColor(Color.WHITE);
        canvas.drawColor(Color.argb(17, 0, 0, 0));
        int y = canvas.getHeight()/4;
        for (int line = 1; line <= 5; line++) {
            paint.setStrokeWidth(5.0f);
            canvas.drawLine(0, (float) y, 20000, (float) y, paint);
            y += 50;
        }
        canvas.translate(50, 0);
    }

    public void drawNatural(Canvas canvas, int ynote) {
        float offset = (float) (419 - ynote * 25.7);
        paint.setStrokeWidth(3.0f);
        paint.setStyle(Paint.Style.FILL);
        canvas.translate(0, offset);
        RectF oval1 = new RectF(0, 10, 60,60);
        canvas.drawOval(oval1, paint);
        paint.setStrokeWidth(7.0f);
        if(ynote<6)canvas.drawLine(55,20,55,-135,paint);
        else canvas.drawLine(7,25,7,190,paint);
        if(ynote==0) canvas.drawLine(-10,30,73,30,paint);
        canvas.translate(0, -offset);
        canvas.translate(150, 0);
    }
}
