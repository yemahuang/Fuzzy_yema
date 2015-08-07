package com.fuzzy.ming.fuzzy.view;

import android.content.Context;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.ImageView;

/**
 * Created by ming on 2015/8/6.
 */
public class TouchImageView extends ImageView{

    private Matrix matrix = new Matrix();
    private Matrix savedMatrix = new Matrix();

    /** 无 */
    private static final int NONE = 0;
    /** 拖动 */
    private static final int DRAG = 1;
    /** 缩放 */
    private static final int ZOOM = 2;
    /** 旋转 */
    private static final int ROTATE = 3;
    /** 缩放或旋转 */
    static final int ZOOM_OR_ROTATE = 4;

    /** 初始化动作标志 */
    private int mode = NONE;

    private PointF pA = new PointF();
    private PointF pB = new PointF();

    /** 记录起始坐标 */
    private PointF start = new PointF();
    /** 记录缩放时两指中间点坐标 */
    private PointF mid = new PointF();
    private float oldDist = 1f;
    private long lastClickTime;
    private PointF lastClickPos = new PointF();
    private double rotation = 0.0;
    private float viewW;
    private float viewH;
    private float imageW;
    private float imageH;
    private float rotatedImageH;
    private float rotatedImageW;
    private static final float MAX_SCALE = 2.0f;

    public TouchImageView(Context context) {
        super(context);
    }

    public TouchImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public TouchImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:
                savedMatrix.set(matrix);
                // 设置开始点位置
                start.set(event.getX(), event.getY());
                pA.set(event.getX(), event.getY());
                pB.set(event.getX(), event.getY());
                mode = DRAG;
                break;
            case MotionEvent.ACTION_POINTER_DOWN:
                if (event.getActionIndex()>1)
                    break;
                oldDist = spacing(event);
                if (oldDist > 10f) {
                    savedMatrix.set(matrix);
                    midPoint(mid, event);
                    pA.set(event.getX(0), event.getY(0));
                    pB.set(event.getX(1), event.getY(1));
                    mode = ZOOM_OR_ROTATE;
                }
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_POINTER_UP:
                if (mode == DRAG) {
                    if (spacing(pA.x, pA.y, pB.x, pB.y) < 50) {
                        long now = System.currentTimeMillis();
                        if (now - lastClickTime < 500
                                && spacing(pA.x, pA.y, lastClickPos.x,
                                lastClickPos.y) < 50) {
//                            doubleClick(pA.x, pA.y);
                            now = 0;
                        }
                        lastClickPos.set(pA);
                        lastClickTime = now;
                    }
                } else if (mode == ROTATE) {
                    int level = (int) Math.floor((rotation + Math.PI / 4)
                            / (Math.PI / 2));
                    if (level == 4)
                        level = 0;
                    matrix.set(savedMatrix);
                    matrix.postRotate(90 * level, mid.x, mid.y);
                    if (level == 1 || level == 3) {
                        float tmp = rotatedImageW;
                        rotatedImageW = rotatedImageH;
                        rotatedImageH = tmp;
//                        fixScale();
                    }
//                    fixTranslation();
                }
                mode = NONE;
                break;
            case MotionEvent.ACTION_MOVE:

                if (mode == ZOOM_OR_ROTATE) {
                    PointF pC = new PointF(event.getX(1) - event.getX(0) + pA.x,
                            event.getY(1) - event.getY(0) + pA.y);
                    double a = spacing(pB.x, pB.y, pC.x, pC.y);
                    double b = spacing(pA.x, pA.y, pC.x, pC.y);
                    double c = spacing(pA.x, pA.y, pB.x, pB.y);
                    if (a >= 10) {
                        double cosB = (a * a + c * c - b * b) / (2 * a * c);
                        double angleB = Math.acos(cosB);
                        double PID4 = Math.PI / 4;
                        if (angleB > PID4 && angleB < 3 * PID4) {
                            mode = ROTATE;
                            rotation = 0;
                        } else {
                            mode = ZOOM;
                        }
                    }
                }

                if (mode == DRAG) {
                    matrix.set(savedMatrix);
                    matrix.postTranslate(event.getX() - start.x, event.getY()
                            - start.y);
                } else if (mode == ZOOM) {
                    float newDist = spacing(event);
                    if (newDist > 10f) {
                        matrix.set(savedMatrix);
                        float scale = newDist / oldDist;
                        matrix.postScale(scale, scale, mid.x, mid.y);
                    }
                } else if (mode == ROTATE) {
                    PointF pC = new PointF(event.getX(1) - event.getX(0) + pA.x,
                            event.getY(1) - event.getY(0) + pA.y);
                    double a = spacing(pB.x, pB.y, pC.x, pC.y);
                    double b = spacing(pA.x, pA.y, pC.x, pC.y);
                    double c = spacing(pA.x, pA.y, pB.x, pB.y);
                    if (b > 10) {
                        double cosA = (b * b + c * c - a * a) / (2 * b * c);
                        double angleA = Math.acos(cosA);
                        double ta = pB.y - pA.y;
                        double tb = pA.x - pB.x;
                        double tc = pB.x * pA.y - pA.x * pB.y;
                        double td = ta * pC.x + tb * pC.y + tc;
                        if (td > 0) {
                            angleA = 2 * Math.PI - angleA;
                        }
                        rotation = angleA;
                        matrix.set(savedMatrix);
                        matrix.postRotate((float) (rotation * 180 / Math.PI),
                                mid.x, mid.y);
                    }

                }
                break;
        }
        setImageMatrix(matrix);
        return true;
    }

    /**
     * @description 多点触控时，计算最先放下的两指距离
     * @param event
     * @return
     */
    private float spacing(MotionEvent event) {
        float x = event.getX(0) - event.getX(1);
        float y = event.getY(0) - event.getY(1);
        return (float) Math.sqrt(x * x + y * y);
    }

    /**
     * @description 多点触控时，计算最先放下的两指中心坐标
     * @param point
     * @param event
     */
    private void midPoint(PointF point, MotionEvent event) {
        float x = event.getX(0) + event.getX(1);
        float y = event.getY(0) + event.getY(1);
        point.set(x / 2, y / 2);
    }

    public void setMatrix(Matrix matrix) {
        this.matrix = matrix;
    }

    private float spacing(float x1, float y1, float x2, float y2) {
        float x = x1 - x2;
        float y = y1 - y2;
        return (float)Math.sqrt(x * x + y * y);
    }

    /*
    private void doubleClick(float x, float y) {
        float p[] = new float[9];
        matrix.getValues(p);
        float curScale = Math.abs(p[0]) + Math.abs(p[1]);

        float minScale = Math.min((float) viewW / (float) rotatedImageW,
                (float) viewH / (float) rotatedImageH);
        if (curScale <= minScale + 0.01) { // 放大
            float toScale = Math.max(minScale, MAX_SCALE) / curScale;
            matrix.postScale(toScale, toScale, x, y);
        } else { // 缩小
            float toScale = minScale / curScale;
            matrix.postScale(toScale, toScale, x, y);
            fixTranslation();
        }
    }

    private float maxPostScale() {
        float p[] = new float[9];
        matrix.getValues(p);
        float curScale = Math.abs(p[0]) + Math.abs(p[1]);

        float minScale = Math.min((float) viewW / (float) rotatedImageW,
                (float) viewH / (float) rotatedImageH);
        float maxScale = Math.max(minScale, MAX_SCALE);
        return maxScale / curScale;
    }

    private void fixTranslation() {
        RectF rect = new RectF(0, 0, imageW, imageH);
        matrix.mapRect(rect);

        float height = rect.height();
        float width = rect.width();

        float deltaX = 0, deltaY = 0;

        if (width < viewW) {
            deltaX = (viewW - width) / 2 - rect.left;
        } else if (rect.left > 0) {
            deltaX = -rect.left;
        } else if (rect.right < viewW) {
            deltaX = viewW - rect.right;
        }

        if (height < viewH) {
            deltaY = (viewH - height) / 2 - rect.top;
        } else if (rect.top > 0) {
            deltaY = -rect.top;
        } else if (rect.bottom < viewH) {
            deltaY = viewH - rect.bottom;
        }
        matrix.postTranslate(deltaX, deltaY);
    }

    private void fixScale() {
        float p[] = new float[9];
        matrix.getValues(p);
        float curScale = Math.abs(p[0]) + Math.abs(p[1]);

        float minScale = Math.min((float) viewW / (float) rotatedImageW,
                (float) viewH / (float) rotatedImageH);
        if (curScale < minScale) {
            if (curScale > 0) {
                double scale = minScale / curScale;
                p[0] = (float) (p[0] * scale);
                p[1] = (float) (p[1] * scale);
                p[3] = (float) (p[3] * scale);
                p[4] = (float) (p[4] * scale);
                matrix.setValues(p);
            } else {
                matrix.setScale(minScale, minScale);
            }
        }
    }
    */
}
