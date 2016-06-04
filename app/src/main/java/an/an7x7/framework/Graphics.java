package an.an7x7.framework;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;

import static android.graphics.Bitmap.Config.ARGB_8888;

/**
 * Created by jvilar on 29/03/16.
 */
public class Graphics {
    Bitmap frameBuffer;
    Canvas canvas;
    Paint paint;

    public Graphics(int width, int height) {
        this.frameBuffer = Bitmap.createBitmap(width, height, ARGB_8888);
        canvas = new Canvas(frameBuffer);
        paint = new Paint();
        paint.setTextAlign(Paint.Align.LEFT);
        paint.setTextSize(20);
        paint.setTypeface(Typeface.DEFAULT_BOLD);
    }

    public Bitmap getFrameBuffer() {
        return frameBuffer;
    }

    public void clear(int color) {
        canvas.drawRGB((color & 0xff0000) >> 16, (color & 0xff00) >> 8, color & 0xff);
    }
    public void drawRectStroke( float x, float y, float width, float height, int color) {
        paint.setStyle(Paint.Style.STROKE);
        paint.setColor(color);
        paint.setStrokeWidth(3);
        canvas.drawRect(x, y, x + width - 1, y + height - 1, paint);
    }
    public void drawRect(float x, float y, float width, float height, int color) {
        paint.setColor(color);
        paint.setStyle(Paint.Style.FILL);
        canvas.drawRect(x, y, x + width - 1, y + height - 1, paint);
    }

    public void drawLine(float initialX, float initialY, float finalX, float finalY, int color){
        paint.setColor(color);
        paint.setStrokeWidth(2);
        canvas.drawLine(initialX, initialY, finalX, finalY, paint);
    }

    public int getWidth() {
        return frameBuffer.getWidth();
    }

    public int getHeight() {
        return frameBuffer.getHeight();
    }


    public void drawText (String text, float x, float y) {
        paint.setColor(Color.BLACK);
        canvas.drawText(text, x, y, paint);
    }
}
