package uk.co.greenwich.findmegre;

import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

import uk.co.greenwich.findmegre.Entity.AccessPoint;
import uk.co.greenwich.findmegre.Entity.Button;
import uk.co.greenwich.findmegre.Entity.Entity;
import uk.co.greenwich.findmegre.Entity.LoadingBar;
import uk.co.greenwich.findmegre.Entity.Text;
import uk.co.greenwich.findmegre.Thread.APAnimator;
import uk.co.greenwich.findmegre.Thread.GUI;
import uk.co.greenwich.findmegre.Thread.LoadingBarAnimator;

import static android.graphics.Color.rgb;

public class DrawingPanel extends View {

    private Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private static List<Entity> shapes = new ArrayList<>();  // A List that stores the shapes that appear on the JPanel
    private static float[] lastTouchDownXY = new float[2]; //save the X,Y of screen touch

    public static float getLastClickLocationX() {
        return lastTouchDownXY[0];
    }

    public static float getLastClickLocationY() {
        return lastTouchDownXY[1];
    }

    public static void setLastClickLocationX(float value) {
        lastTouchDownXY[0] = value;
    }

    public static void setLastClickLocationY(float value) {
        lastTouchDownXY[1] = value;
    }

    public DrawingPanel(Context context) {
        super(context);
        init(null);
        this.setOnTouchListener(touchListener);
    }

    public DrawingPanel(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    public DrawingPanel(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }

    private void init(@Nullable AttributeSet set) {
        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
    }

    OnTouchListener touchListener = new OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            // the purpose of the touch listener is just to store the touch X,Y coordinates
            if (event.getActionMasked() == MotionEvent.ACTION_DOWN) {
                lastTouchDownXY[0] = event.getX();
                lastTouchDownXY[1] = event.getY();
            }
            return false;
        }
    };

    @Override
    public void onDraw(Canvas g) {
        super.onDraw(g);
        for (int i = 0; i < shapes.size(); i++) {
            Entity shape = shapes.get(i);
            if (shape instanceof Text) {
                paint.setColor(rgb(46, 29, 91));
                paint.setStrokeWidth(shape.getThickness() * 10);
                paint.setTextSize(45f);
                g.drawText(((Text) shape).getText(), (float) shape.getXStart(), (float) shape.getYStart(), paint);
            } else if (shape instanceof Button) {
                paint.setColor(rgb(46, 29, 91));
                paint.setStrokeWidth(shape.getThickness());
                // draw the train
                g.drawLine((float) shape.getVertices().get(0)[0], (float) shape.getVertices().get(0)[1], (float) shape.getVertices().get(1)[0], (float) shape.getVertices().get(1)[1], paint);
                g.drawLine((float) shape.getVertices().get(1)[0], (float) shape.getVertices().get(1)[1], (float) shape.getVertices().get(2)[0], (float) shape.getVertices().get(2)[1], paint);
                g.drawLine((float) shape.getVertices().get(2)[0], (float) shape.getVertices().get(2)[1], (float) shape.getVertices().get(3)[0], (float) shape.getVertices().get(3)[1], paint);
                g.drawLine((float) shape.getVertices().get(3)[0], (float) shape.getVertices().get(3)[1], (float) shape.getVertices().get(0)[0], (float) shape.getVertices().get(0)[1], paint);
                paint.setTextSize(100f);
                double xCentreOfButton = shape.getVertices().get(0)[0] + ((shape.getVertices().get(1)[0] - shape.getVertices().get(0)[0]) / 8);
                double yCentreOfButton = shape.getVertices().get(1)[1] + ((shape.getVertices().get(2)[1] - shape.getVertices().get(1)[1]) / 2);
                g.drawText(((Button) shape).getLabel(), (float) xCentreOfButton, (float) yCentreOfButton, paint);
            } else if (shape instanceof AccessPoint) {
              //  paint.setColor(Color.TRANSPARENT);
                AccessPoint oval = (AccessPoint) shape;
                paint.setColor(rgb(0, 162, 232));
                double xs = shape.getXStart();
                double ys = shape.getYStart();
                paint.setStyle(Paint.Style.STROKE);
                paint.setStrokeWidth(oval.getThickness() * 400 / (APAnimator.WavePropergation) / 4);
                g.drawCircle((int) (xs + (int) (oval.getWidth() / 2)), (int) (ys + (int) (oval.getWidth() / 2)), (int) ((oval.getWidth()) + 2 * APAnimator.WavePropergation), paint);
                paint.setStrokeWidth(5);
                paint.setStyle(Paint.Style.FILL);
                paint.setTextSize(30f);
                g.drawText(oval.getSSID(), (int) (oval.getXStart() - 10), (int) (oval.getYStart() - 10), paint);
                //   paint.setStyle(Paint.Style.STROKE);
                paint.setStyle(Paint.Style.FILL);
                paint.setStrokeWidth(oval.getThickness());
                g.drawCircle((int) xs + (int) (oval.getWidth()), (int) ys + (int) (oval.getWidth()), (int) (oval.getWidth()), paint);
            } else if (shape instanceof LoadingBar) {
                //draw arc
                LoadingBar arc = (LoadingBar) shape;
                double multiplier = ((arc.getWidth()) / 713);
                paint.setColor(rgb(46, 29, 91));
                paint.setStrokeCap(Paint.Cap.ROUND);
                paint.setStyle(Paint.Style.STROKE);
                //paint.setStrokeWidth(arc.thickness * multiplier);
                paint.setStrokeWidth(28 * (float) multiplier);
                g.drawArc((float) (LoadingBarAnimator.xCentre - LoadingBarAnimator.Radius), (float) (LoadingBarAnimator.yCentre - LoadingBarAnimator.Radius), (float) ((LoadingBarAnimator.xCentre + LoadingBarAnimator.Radius)), (float) ((LoadingBarAnimator.yCentre + LoadingBarAnimator.Radius)), -1 * arc.getAngle(), arc.getOffset(), false, paint);
                Path path = new Path();
                paint.setStrokeWidth(7 * (float) multiplier);
                path.setFillType(Path.FillType.EVEN_ODD);
                paint.setStyle(Paint.Style.FILL);
                int[] x = {475, 520, 603, 577, 777, 628, 782, 668, 950, 664, 782, 630, 778, 575, 600, 519, 475, 431, 348, 375, 173, 319, 168, 286, 0, 288, 169, 318, 172, 376, 347, 430};
                int[] y = {0, 286, 167, 320, 170, 374, 349, 432, 475, 520, 602, 574, 776, 630, 783, 664, 950, 664, 783, 630, 776, 574, 602, 520, 475, 432, 349, 374, 170, 320, 167, 286};
                for (int j = 0; j < x.length; j++) {
                    x[j] = (int) (x[j] * multiplier) + (int) (LoadingBarAnimator.xCentre - (LoadingBarAnimator.Radius * 1.3));
                    y[j] = (int) (y[j] * multiplier) + (int) (LoadingBarAnimator.yCentre - (LoadingBarAnimator.Radius * 1.3));
                    if (j == 0) {
                        path.moveTo(x[j], y[j]);
                    } else {
                        path.lineTo(x[j], y[j]);
                        //  path.moveTo(x[j], y[j]);
                    }
                    if (j == x.length - 1) {
                        path.lineTo(x[j], y[j]);
                    }
                }
                path.close();
                g.drawPath(path, paint);
                paint.setColor(rgb(MainActivity.red, MainActivity.green, MainActivity.blue));
                path = new Path();
                paint.setStyle(Paint.Style.FILL);
                int[] x2 = {475, 475, 777, 537, 950, 556, 777, 539, 475, 475, 171, 411, 0, 393, 169, 411};
                int[] y2 = {0, 397, 168, 412, 475, 475, 777, 540, 950, 560, 777, 536, 475, 475, 173, 411, 0};
                for (int j = 0; j < x2.length; j++) {
                    x2[j] = (int) (x2[j] * multiplier) + (int) (LoadingBarAnimator.xCentre - LoadingBarAnimator.Radius * 1.3);
                    y2[j] = (int) (y2[j] * multiplier) + (int) (LoadingBarAnimator.yCentre - LoadingBarAnimator.Radius * 1.3);
                    if (j == 0) {
                        path.moveTo(x2[j], y2[j]);
                    } else {
                        path.lineTo(x2[j], y2[j]);
                        // path.moveTo(x2[j], y2[j]);
                    }
                    if (j == x2.length - 1) {
                        path.lineTo(x2[j], y2[j]);
                    }
                }
                path.close();
                g.drawPath(path, paint);

                g.drawCircle((int) (338 * multiplier) + (int) (LoadingBarAnimator.xCentre - LoadingBarAnimator.Radius * 0.93), (int) (342 * multiplier) + (int) (LoadingBarAnimator.yCentre - LoadingBarAnimator.Radius * 0.93), (int) (272 * multiplier) / 2, paint);

                paint.setColor(rgb(46, 29, 91));
                paint.setStrokeWidth((int) (7 * multiplier));
                paint.setStrokeCap(Paint.Cap.SQUARE);
                g.drawLine((int) (474 * multiplier) + (int) (LoadingBarAnimator.xCentre - LoadingBarAnimator.Radius * 1.3), (int) (366 * multiplier) + (int) (LoadingBarAnimator.yCentre - LoadingBarAnimator.Radius * 1.3), (int) (474 * multiplier) + (int) (LoadingBarAnimator.xCentre - LoadingBarAnimator.Radius * 1.3), (int) (586 * multiplier) + (int) (LoadingBarAnimator.yCentre - LoadingBarAnimator.Radius * 1.3), paint);
                g.drawLine((int) (518 * multiplier) + (int) (LoadingBarAnimator.xCentre - LoadingBarAnimator.Radius * 1.3), (int) (373 * multiplier) + (int) (LoadingBarAnimator.yCentre - LoadingBarAnimator.Radius * 1.3), (int) (433 * multiplier) + (int) (LoadingBarAnimator.xCentre - LoadingBarAnimator.Radius * 1.3), (int) (578 * multiplier) + (int) (LoadingBarAnimator.yCentre - LoadingBarAnimator.Radius * 1.3), paint);
                g.drawLine((int) (553 * multiplier) + (int) (LoadingBarAnimator.xCentre - LoadingBarAnimator.Radius * 1.3), (int) (397 * multiplier) + (int) (LoadingBarAnimator.yCentre - LoadingBarAnimator.Radius * 1.3), (int) (397 * multiplier) + (int) (LoadingBarAnimator.xCentre - LoadingBarAnimator.Radius * 1.3), (int) (554 * multiplier) + (int) (LoadingBarAnimator.yCentre - LoadingBarAnimator.Radius * 1.3), paint);
                g.drawLine((int) (577 * multiplier) + (int) (LoadingBarAnimator.xCentre - LoadingBarAnimator.Radius * 1.3), (int) (433 * multiplier) + (int) (LoadingBarAnimator.yCentre - LoadingBarAnimator.Radius * 1.3), (int) (372 * multiplier) + (int) (LoadingBarAnimator.xCentre - LoadingBarAnimator.Radius * 1.3), (int) (517 * multiplier) + (int) (LoadingBarAnimator.yCentre - LoadingBarAnimator.Radius * 1.3), paint);
                g.drawLine((int) (586 * multiplier) + (int) (LoadingBarAnimator.xCentre - LoadingBarAnimator.Radius * 1.3), (int) (475 * multiplier) + (int) (LoadingBarAnimator.yCentre - LoadingBarAnimator.Radius * 1.3), (int) (364 * multiplier) + (int) (LoadingBarAnimator.xCentre - LoadingBarAnimator.Radius * 1.3), (int) (475 * multiplier) + (int) (LoadingBarAnimator.yCentre - LoadingBarAnimator.Radius * 1.3), paint);
                g.drawLine((int) (578 * multiplier) + (int) (LoadingBarAnimator.xCentre - LoadingBarAnimator.Radius * 1.3), (int) (518 * multiplier) + (int) (LoadingBarAnimator.yCentre - LoadingBarAnimator.Radius * 1.3), (int) (373 * multiplier) + (int) (LoadingBarAnimator.xCentre - LoadingBarAnimator.Radius * 1.3), (int) (432 * multiplier) + (int) (LoadingBarAnimator.yCentre - LoadingBarAnimator.Radius * 1.3), paint);
                g.drawLine((int) (554 * multiplier) + (int) (LoadingBarAnimator.xCentre - LoadingBarAnimator.Radius * 1.3), (int) (554 * multiplier) + (int) (LoadingBarAnimator.yCentre - LoadingBarAnimator.Radius * 1.3), (int) (397 * multiplier) + (int) (LoadingBarAnimator.xCentre - LoadingBarAnimator.Radius * 1.3), (int) (397 * multiplier) + (int) (LoadingBarAnimator.yCentre - LoadingBarAnimator.Radius * 1.3), paint);
                g.drawLine((int) (518 * multiplier) + (int) (LoadingBarAnimator.xCentre - LoadingBarAnimator.Radius * 1.3), (int) (578 * multiplier) + (int) (LoadingBarAnimator.yCentre - LoadingBarAnimator.Radius * 1.3), (int) (432 * multiplier) + (int) (LoadingBarAnimator.xCentre - LoadingBarAnimator.Radius * 1.3), (int) (372 * multiplier) + (int) (LoadingBarAnimator.yCentre - LoadingBarAnimator.Radius * 1.3), paint);

                paint.setColor(rgb(MainActivity.red, MainActivity.green, MainActivity.blue));
                g.drawCircle((int) (421 * multiplier) + (int) (LoadingBarAnimator.xCentre - LoadingBarAnimator.Radius * 1.15), (int) (424 * multiplier) + (int) (LoadingBarAnimator.yCentre - LoadingBarAnimator.Radius * 1.15), (int) (105 * multiplier) / 2, paint);

                if (GUI.getLoadingObjective() != null) {
                    paint.setTextSize(30f);
                    paint.setColor(rgb(46, 29, 91));
                    g.drawText(GUI.getLoadingObjective(), Width() * (float) 0.1, Height() * (float) 0.95, paint);
                }
            }
            invalidate();
        }
        // invalidate();
    }

    public static void addShape(Entity shape) {
        shapes.add(shape);
    }

    public static List getShapes() {
        return shapes;
    }

    public int Width() {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        ((Activity) getContext()).getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        return displayMetrics.widthPixels;
    }

    public int Height() {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        ((Activity) getContext()).getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        return displayMetrics.heightPixels;
    }
}
