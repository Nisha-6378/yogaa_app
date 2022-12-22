package com.google.mlkit.vision.demo;
import static com.google.mlkit.vision.demo.java.posedetector.PoseGraphicForPushUp.upcount;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import androidx.annotation.Nullable;
/** Graphic instance for rendering inference info (latency, FPS, resolution) in an overlay view. */
public class InferenceInfoGraphic extends GraphicOverlay.Graphic {

  private static final int TEXT_COLOR = Color.WHITE;
  public static final float TEXT_SIZE = 60.0f;

  private final Paint textPaint;
  private  final Paint textPaint1;
  private final GraphicOverlay overlay;
  private final double latency;
  int  frame=0;

  // Only valid when a stream of input images is being processed. Null for single image mode.
  @Nullable private final Integer framesPerSecond;

  public InferenceInfoGraphic(
      GraphicOverlay overlay, double latency, @Nullable Integer framesPerSecond ) {
    super(overlay);
    this.overlay = overlay;
    this.latency = latency;
    this.framesPerSecond = framesPerSecond;

    textPaint = new Paint();
    textPaint.setColor(TEXT_COLOR);
    textPaint.setTextSize(TEXT_SIZE);

    textPaint1 = new Paint();
    textPaint1.setColor(Color.WHITE);
    textPaint1.setTextSize(40);
    postInvalidate();
    for(int i=0;i<=framesPerSecond;i++)
    {
      frame+=1;
    }
  }

  @Override
  public synchronized void draw(Canvas canvas) {
    float x = TEXT_SIZE * 0.5f;
    float y = TEXT_SIZE * 1.5f;



    canvas.drawText(
        "InputImage size: " + overlay.getImageWidth() + "x" + overlay.getImageHeight(),
        x,
        y,
        textPaint);


    // Draw FPS (if valid) and inference latency
    if (framesPerSecond != null) {
      canvas.drawText(
          "FPS: " + framesPerSecond + ", latency: " + latency + " ms", x, y + TEXT_SIZE, textPaint);
//      canvas.drawText("Numerof Frame" +frame,"ms",x,y,+TEXT_SIZE,textPaint);

    } else {
      canvas.drawText("Latency: " + latency + " ms", x, y + TEXT_SIZE, textPaint);
    }



    drawText(canvas, "count："+upcount.toString(),3);


  }
  private void  drawText(Canvas canvas, String text, Integer line) {


       canvas.drawText(text, TEXT_SIZE*0.5f, TEXT_SIZE*3 + TEXT_SIZE*line, textPaint1);
    }

}
