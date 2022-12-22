package com.google.mlkit.vision.demo.java.posedetector;
import static com.google.mlkit.vision.demo.InferenceInfoGraphic.TEXT_SIZE;
import static com.google.mlkit.vision.demo.java.VisionProcessorBase.frame;
import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.View;
import android.widget.Button;

import com.google.mlkit.vision.demo.GraphicOverlay;
import com.google.mlkit.vision.demo.GraphicOverlay.Graphic;
import com.google.mlkit.vision.demo.R;
import com.google.mlkit.vision.demo.java.VisionProcessorBase;
import com.google.mlkit.vision.pose.Pose;
import com.google.mlkit.vision.pose.PoseLandmark;
import java.util.List;

/**
 * Draw the detected pose in preview.
 */
public class PoseGraphicForPushUp extends Graphic {

    private static final float DOT_RADIUS = 15;
    private static final float IN_FRAME_LIKELIHOOD_TEXT_SIZE = 30.0f;
    private static float threshold = 0f;

    private final Pose pose;
    private boolean showInFrameLikelihood;
    private float dynamicPaintThreshold = 6.5f;
    private float angleError;
    private final Paint linePaint;
    private final Paint linePaintGreen;
    private final Paint rlinePaintGreen;
    private final Paint dynamicLinePaint;
    private final Paint dotPaint;
    private final Paint textPaint;
    private  final Paint textPaint1;
    private final Paint TextCount;

    private static boolean isUp=true;
    private static   boolean isDown=false;
    public static Integer  upcount=0;
    private  static Integer downcount=0;
    private  static boolean isCount =false ;// iscounting
    private  static  String lineOneText = " ";
    private  static String lineTwoText=" ";
    private  static float line;
    private  boolean Elbow_falg =false;
    private  static Integer prevcount=0;
    private  boolean standing=false;


    public PoseGraphicForPushUp(GraphicOverlay overlay, Pose pose, boolean showInFrameLikelihood) {
        super(overlay);
        this.pose = pose;
        this.showInFrameLikelihood = true;
        this.angleError = 0;

        dotPaint = new Paint();
        dotPaint.setColor(Color.WHITE);
        dotPaint.setTextSize(IN_FRAME_LIKELIHOOD_TEXT_SIZE);

        linePaint = new Paint();
        linePaint.setStrokeWidth(8);
        linePaint.setColor(Color.CYAN);


        linePaintGreen = new Paint();
        linePaintGreen.setStrokeWidth(5);
        linePaintGreen.setColor(Color.GREEN);

        rlinePaintGreen = new Paint();
        rlinePaintGreen.setStrokeWidth(6);
        rlinePaintGreen.setColor(Color.RED);

        dynamicLinePaint = new Paint();
        dynamicLinePaint.setStrokeWidth(17.5f);

        textPaint = new Paint();
        textPaint.setColor(Color.BLUE);
        textPaint.setTextSize(50);

        textPaint1 = new Paint();
        textPaint1.setColor(Color.WHITE);
        textPaint1.setTextSize(40);

        TextCount = new Paint();
        TextCount.setColor(Color.CYAN);
        TextCount.setTextSize(100);


    }
    public static int getScreenWidth() {
        return Resources.getSystem().getDisplayMetrics().widthPixels;
    }

    public static int getScreenHeight() {
        return Resources.getSystem().getDisplayMetrics().heightPixels;
    }

    private int getRed(Float value) {
        return (int) ((1 - value) * 255);
//        return 0;
    }

    private int getGreen(Float value) {
        return (int) ((value) * 255);
//        return 0;
    }



    @SuppressLint("SuspiciousIndentation")
    @Override
    public synchronized void draw(Canvas canvas) {
        float x = TEXT_SIZE * 0.5f;
        float y = TEXT_SIZE * 1.5f;
        List<PoseLandmark> landmarks = pose.getAllPoseLandmarks();
        if (landmarks.isEmpty()) {
            return;
        }

        // Getting all the landmarks

        PoseLandmark leftShoulder =pose.getPoseLandmark(PoseLandmark.LEFT_SHOULDER);
        PoseLandmark rightShoulder = pose.getPoseLandmark(PoseLandmark.RIGHT_SHOULDER);
        PoseLandmark leftElbow = pose.getPoseLandmark(PoseLandmark.LEFT_ELBOW);
        PoseLandmark rightElbow = pose.getPoseLandmark(PoseLandmark.RIGHT_ELBOW);
        PoseLandmark leftWrist = pose.getPoseLandmark(PoseLandmark.LEFT_WRIST);
        PoseLandmark rightWrist = pose.getPoseLandmark(PoseLandmark.RIGHT_WRIST);
        PoseLandmark leftHip = pose.getPoseLandmark(PoseLandmark.LEFT_HIP);
        PoseLandmark rightHip = pose.getPoseLandmark(PoseLandmark.RIGHT_HIP);
        PoseLandmark leftKnee = pose.getPoseLandmark(PoseLandmark.LEFT_KNEE);
        PoseLandmark rightKnee = pose.getPoseLandmark(PoseLandmark.RIGHT_KNEE);
        PoseLandmark leftAnkle = pose.getPoseLandmark(PoseLandmark.LEFT_ANKLE);
        PoseLandmark rightAnkle = pose.getPoseLandmark(PoseLandmark.RIGHT_ANKLE);


        // Frame Visibility Check


        if (leftShoulder.getInFrameLikelihood() < threshold) {
            showInFrameLikelihood = false;
        }
        if (rightShoulder.getInFrameLikelihood() < threshold) {
            showInFrameLikelihood = false;
        }
        if (leftElbow.getInFrameLikelihood() < threshold) {
            showInFrameLikelihood = false;
        }
        if (rightElbow.getInFrameLikelihood() < threshold) {
            showInFrameLikelihood = false;
        }
        if (leftWrist.getInFrameLikelihood() < threshold) {
            showInFrameLikelihood = false;
        }
        if (rightWrist.getInFrameLikelihood() < threshold) {
            showInFrameLikelihood = false;
        }
        if (leftHip.getInFrameLikelihood() < threshold) {
            showInFrameLikelihood = false;
        }
        if (rightHip.getInFrameLikelihood() < threshold) {
            showInFrameLikelihood = false;
        }
        if (leftKnee.getInFrameLikelihood() < threshold) {
            showInFrameLikelihood = false;
        }
        if (rightKnee.getInFrameLikelihood() < threshold) {
            showInFrameLikelihood = false;
        }
        if (leftAnkle.getInFrameLikelihood() < threshold) {
            showInFrameLikelihood = false;
        }
        if (rightAnkle.getInFrameLikelihood() < threshold) {
            showInFrameLikelihood = false;
        }


        if (showInFrameLikelihood == true) {


            //Angles
            SmoothAngles smoothAngles = new SmoothAngles();
            int leftElbow_angle = (int) smoothAngles.getAngle(leftWrist,leftElbow, leftShoulder);
            int rightElbow_angle = (int) smoothAngles.getAngle( rightWrist, rightElbow,rightShoulder);

            int leftHip_angle = (int) smoothAngles.getAngle( leftKnee,leftHip, leftShoulder);
            int rightHip_angle = (int) smoothAngles.getAngle(rightKnee, rightHip,rightShoulder);
            int leftKnee_angle =(int) smoothAngles.getAngle(leftAnkle,leftKnee,leftHip);
            int rightKnee_angle = (int) smoothAngles.getAngle(rightAnkle, rightKnee, rightHip);


//            int wrong_leftElbow = leftElbow_angle-
            drawLine(canvas, leftShoulder, rightShoulder,linePaintGreen);
            drawLine(canvas, leftShoulder, leftElbow, linePaintGreen);
            drawLine(canvas, leftElbow, leftWrist, linePaintGreen);
            drawLine(canvas, rightShoulder, rightElbow, linePaintGreen);
            drawLine(canvas, rightElbow, rightWrist,linePaintGreen);
            drawLine(canvas, leftShoulder, leftHip, linePaintGreen);
            drawLine(canvas, leftHip, leftKnee, linePaintGreen);
            drawLine(canvas, rightShoulder, rightHip,linePaintGreen);
            drawLine(canvas, rightHip, rightKnee, linePaintGreen);
            drawLine(canvas, leftKnee, leftAnkle, linePaintGreen);
            drawLine(canvas, rightKnee, rightAnkle,linePaintGreen);
            // for angle
            drawAngle(canvas, leftElbow_angle, rightElbow, textPaint);
            drawAngle(canvas, rightElbow_angle, leftElbow, textPaint);
            drawAngle(canvas, leftHip_angle, leftHip, textPaint);
            drawAngle(canvas, rightHip_angle, rightHip, textPaint);
            drawAngle(canvas, leftKnee_angle,leftKnee, textPaint);
            drawAngle(canvas,rightKnee_angle, rightKnee, textPaint);
            // for cordidanates
            drawPoint(canvas, leftShoulder, dotPaint);
            drawPoint(canvas, rightShoulder, dotPaint);
            drawPoint(canvas, leftElbow, dotPaint);
            drawPoint(canvas, rightElbow, dotPaint);
            drawPoint(canvas, leftWrist, dotPaint);
            drawPoint(canvas, rightWrist, dotPaint);
            drawPoint(canvas, leftShoulder, dotPaint);
            drawPoint(canvas, rightHip, dotPaint);
            drawPoint(canvas, leftHip, dotPaint);
            drawPoint(canvas, leftKnee, dotPaint);
            drawPoint(canvas, rightKnee, dotPaint);
            drawPoint(canvas, leftAnkle, dotPaint);
            drawPoint(canvas, rightAnkle, dotPaint);

//            Line(canvas, leftShoulder, rightShoulder, linePaint);
//            Line(canvas, leftShoulder, leftElbow, linePaint);
//            Line(canvas, leftElbow, leftWrist, linePaint);
//            Line(canvas, rightShoulder, rightElbow, linePaint);
//            Line(canvas, rightElbow, rightWrist, linePaint);
//            Line(canvas, leftShoulder, leftHip, linePaint);
//            Line(canvas, leftHip, leftKnee, linePaint);
//            Line(canvas, rightShoulder, rightHip, linePaint);
//            Line(canvas, rightHip, rightKnee, linePaint);
//            Line(canvas, leftKnee, leftAnkle, linePaint);
//            Line(canvas, rightKnee, rightAnkle, linePaint);
//            // for angle
//            drawAngle(canvas, leftElbow_angle, rightElbow, textPaint);
//            drawAngle(canvas, rightElbow_angle, leftElbow, textPaint);
//            drawAngle(canvas, leftHip_angle, leftHip, textPaint);
//            drawAngle(canvas, rightHip_angle, rightHip, textPaint);
//            // for cordidanates
//            drawPoint(canvas, leftShoulder, dotPaint);
//            drawPoint(canvas, rightShoulder, dotPaint);
//            drawPoint(canvas, leftElbow, dotPaint);
//            drawPoint(canvas, rightElbow, dotPaint);
//            drawPoint(canvas, leftWrist, dotPaint);
//            drawPoint(canvas, rightWrist, dotPaint);
//            drawPoint(canvas, leftShoulder, dotPaint);
//            drawPoint(canvas, rightHip, dotPaint);
//            drawPoint(canvas, leftHip, dotPaint);
//            drawPoint(canvas, leftKnee, dotPaint);
//            drawPoint(canvas, rightKnee, dotPaint);
//            drawPoint(canvas, leftAnkle, dotPaint);
  //          drawPoint(canvas, rightAnkle, dotPaint);

            // plank pose
            int LeftElbow = 170, RightElow = 170, LeftHip = 170, RightHip = 170;
            // staff pose
            int Leftelbow = 90;
            int rightelbow = 90;

            // plank Pose
            boolean plank_leftE= LeftElbow - 25<= leftElbow_angle & leftElbow_angle <= LeftElbow + 10;
            boolean plank_right=RightElow - 25 <= rightElbow_angle & rightElbow_angle <= RightElow + 10;
            boolean left_hip= LeftHip - 30 <= leftHip_angle & leftHip_angle <= LeftHip + 10;
            boolean right_hip= RightHip - 30 <= rightHip_angle & rightHip_angle <= RightHip + 10;

          // staff pose
            boolean staf_leftE =  Leftelbow-10 <= leftElbow_angle && leftElbow_angle <=  Leftelbow + 10;
            boolean staf_rightR =  rightelbow-10<= rightElbow_angle && rightElbow_angle <= rightelbow+10;


           float  leftshoulder_x= ( (float) pose.getPoseLandmark(PoseLandmark.LEFT_SHOULDER).getLandmarkType());
           float leftshoulder_y = ( (float) pose.getPoseLandmark(PoseLandmark.LEFT_SHOULDER).getLandmarkType());
           float rightshoulder_x= ( (float) pose.getPoseLandmark(PoseLandmark.RIGHT_SHOULDER).getLandmarkType());
           float rightshoulder_y = ( (float) pose.getPoseLandmark(PoseLandmark.RIGHT_SHOULDER).getLandmarkType());
           float leftankle_x= ( (float) pose.getPoseLandmark(PoseLandmark.LEFT_ANKLE).getLandmarkType());
           float leftankle_y = ( (float) pose.getPoseLandmark(PoseLandmark.LEFT_ANKLE).getLandmarkType());
           float rightankle_x= ( (float) pose.getPoseLandmark(PoseLandmark.RIGHT_ANKLE).getLandmarkType());
           float rightankle_y = ( (float) pose.getPoseLandmark(PoseLandmark.RIGHT_ANKLE).getLandmarkType());

        // calculating the tan theta of the left and right shoulders and ankles
           boolean left_tan = Math.abs(leftshoulder_y - leftankle_y) > Math.abs(leftshoulder_x - leftankle_x);
           boolean right_tan = Math.abs(rightshoulder_y - rightankle_y)> Math.abs(rightshoulder_x - rightankle_x);
        // cheking both left and right difference  for robustness
        // if tan  theta > 1 then standing otherwise not standing and ready to do Pushups

            //if(standing ==  left_tan && right_tan)
                //Log.d(TAG ,"stand",standing);
//
//            if(!left_hip && !right_hip )
//             {
//
//                    VisionProcessorBase.AudioforError(2);
//                    drawLine(canvas, leftShoulder, rightShoulder, rlinePaintGreen);
//                    drawLine(canvas, leftShoulder, leftElbow, rlinePaintGreen);
//                    drawLine(canvas, leftElbow, leftWrist, rlinePaintGreen);
//                    drawLine(canvas, rightShoulder, rightElbow, rlinePaintGreen);
//                    drawLine(canvas, rightElbow, rightWrist, rlinePaintGreen);
//                    drawLine(canvas, leftShoulder, leftHip, rlinePaintGreen);
//                    drawLine(canvas, leftHip, leftKnee, rlinePaintGreen);
//                    drawLine(canvas, rightShoulder, rightHip, rlinePaintGreen);
//                    drawLine(canvas, rightHip, rightKnee, rlinePaintGreen);
//                    drawLine(canvas, leftKnee, leftAnkle, rlinePaintGreen);
//                    drawLine(canvas, rightKnee, rightAnkle, rlinePaintGreen);
//                    // for angle
//                    drawAngle(canvas, leftElbow_angle, rightElbow, textPaint);
//                    drawAngle(canvas, rightElbow_angle, leftElbow, textPaint);
//                    drawAngle(canvas, leftHip_angle, leftHip, textPaint);
//                    drawAngle(canvas, rightHip_angle, rightHip, textPaint);
//                    drawAngle(canvas, leftKnee_angle,leftKnee, textPaint);
//                    drawAngle(canvas,rightKnee_angle, rightKnee, textPaint);
//                    // for cordidanates
//                    drawPoint(canvas, leftShoulder, dotPaint);
//                    drawPoint(canvas, rightShoulder, dotPaint);
//                    drawPoint(canvas, leftElbow, dotPaint);
//                    drawPoint(canvas, rightElbow, dotPaint);
//                    drawPoint(canvas, leftWrist, dotPaint);
//                    drawPoint(canvas, rightWrist, dotPaint);
//                    drawPoint(canvas, leftShoulder, dotPaint);
//                    drawPoint(canvas, rightHip, dotPaint);
//                    drawPoint(canvas, leftHip, dotPaint);
//                    drawPoint(canvas, leftKnee, dotPaint);
//                    drawPoint(canvas, rightKnee, dotPaint);
//                    drawPoint(canvas, leftAnkle, dotPaint);
//                    drawPoint(canvas, rightAnkle, dotPaint);
//
//            }
            // for left and right knee error
             int LeftKnee= 170, Rightknee=170;
             if(LeftKnee - 20 <= leftKnee_angle && leftKnee_angle <= LeftKnee + 10)
             {
                 VisionProcessorBase.AudioforError(10);
             }
             else if(Rightknee -20 <= rightKnee_angle && rightKnee_angle <= Rightknee + 10)
             {
                 VisionProcessorBase.AudioforError(11);
             }

            else  if(!staf_leftE && !staf_rightR && !plank_leftE && !plank_right && !left_hip && !right_hip)
            {
                int repitation=0;
                if(!standing && frame > 0 && frame % 101 ==0 && repitation <= 2)
                {
                    VisionProcessorBase.AudioforError(7);
                     repitation +=1;
                }
                else if(!standing && repitation >2 && frame >0  && frame % 101 == 0)
                {
                    VisionProcessorBase.AudioforError(8);
                     frame=0;
                }
                else {
                    //  for staff pose
                    if(!staf_leftE || !staf_rightR && upcount > 1)
                    {
                        if(frame >0 && frame % 151 ==0 && upcount== prevcount)
                            VisionProcessorBase.AudioforError(3);
                        Elbow_falg=true;
                    }
                    else  if((!staf_leftE || (!staf_rightR) && Elbow_falg))
                    {
                        if(frame > 0 && frame % 251 == 0 && upcount== prevcount)
                        {
                            VisionProcessorBase.AudioforError(9);
                            frame=0;
                        }
                    }
                    // for Plank pose
                    else if(!plank_leftE && !plank_right)
                    {
                        if(frame > 0 && frame % 151==0)
                        {
                            VisionProcessorBase.AudioforError(1);
                            Elbow_falg=true;
                        }
                    }
                    else if(!plank_leftE && !plank_right && Elbow_falg )
                    {
                        if(frame >0 && frame== 251)
                        {
                            VisionProcessorBase.AudioforError(9);
                        }
                    }
                    else{
                        if(frame == 151 )
                        {
                            VisionProcessorBase.AudioforError(2);
                        }
                    }
                }

            }
            else{
                VisionProcessorBase.AudioforError(8);
            }

           if(!standing && left_tan && right_tan ) {
               if (!isCount) {
                   isCount = true;
                   lineOneText = "Gesture ready";
               }

               if (!isDown) {

                   if (plank_leftE && plank_right && left_hip && right_hip) {
                       VisionProcessorBase.AudioStop();
                       drawLine(canvas, leftShoulder, rightShoulder, linePaintGreen);
                       drawLine(canvas, leftShoulder, leftElbow, linePaintGreen);
                       drawLine(canvas, leftElbow, leftWrist, linePaintGreen);
                       drawLine(canvas, rightShoulder, rightElbow, linePaintGreen);
                       drawLine(canvas, rightElbow, rightWrist, linePaintGreen);
                       drawLine(canvas, leftShoulder, leftHip, linePaintGreen);
                       drawLine(canvas, leftHip, leftKnee, linePaintGreen);
                       drawLine(canvas, rightShoulder, rightHip, linePaintGreen);
                       drawLine(canvas, rightHip, rightKnee, linePaintGreen);
                       drawLine(canvas, leftKnee, leftAnkle, linePaintGreen);
                       drawLine(canvas, rightKnee, rightAnkle, linePaintGreen);
                       // for angle
                       drawAngle(canvas, leftElbow_angle, rightElbow, textPaint);
                       drawAngle(canvas, rightElbow_angle, leftElbow, textPaint);
                       drawAngle(canvas, leftHip_angle, leftHip, textPaint);
                       drawAngle(canvas, rightHip_angle, rightHip, textPaint);
                       drawAngle(canvas, leftKnee_angle, leftKnee, textPaint);
                       drawAngle(canvas, rightKnee_angle, rightKnee, textPaint);
                       // for cordidanates
                       drawPoint(canvas, leftShoulder, dotPaint);
                       drawPoint(canvas, rightShoulder, dotPaint);
                       drawPoint(canvas, leftElbow, dotPaint);
                       drawPoint(canvas, rightElbow, dotPaint);
                       drawPoint(canvas, leftWrist, dotPaint);
                       drawPoint(canvas, rightWrist, dotPaint);
                       drawPoint(canvas, leftShoulder, dotPaint);
                       drawPoint(canvas, rightHip, dotPaint);
                       drawPoint(canvas, leftHip, dotPaint);
                       drawPoint(canvas, leftKnee, dotPaint);
                       drawPoint(canvas, rightKnee, dotPaint);
                       drawPoint(canvas, leftAnkle, dotPaint);
                       drawPoint(canvas, rightAnkle, dotPaint);

                       if (isUp == true) {
                           upcount++;
                           isDown = true;
                           isUp = false;

                           lineTwoText = "start down";
                       }
                   }


               }
               if (!isUp) {

                   lineTwoText = "start up";
                   if (left_hip && right_hip && staf_leftE && staf_rightR) {
                       VisionProcessorBase.AudioStop();
                       drawLine(canvas, leftShoulder, rightShoulder, linePaintGreen);
                       drawLine(canvas, leftShoulder, leftElbow, linePaintGreen);
                       drawLine(canvas, leftElbow, leftWrist, linePaintGreen);
                       drawLine(canvas, rightShoulder, rightElbow, linePaintGreen);
                       drawLine(canvas, rightElbow, rightWrist, linePaintGreen);
                       drawLine(canvas, leftShoulder, leftHip, linePaintGreen);
                       drawLine(canvas, leftHip, leftKnee, linePaintGreen);
                       drawLine(canvas, rightShoulder, rightHip, linePaintGreen);
                       drawLine(canvas, rightHip, rightKnee, linePaintGreen);
                       drawLine(canvas, leftKnee, leftAnkle, linePaintGreen);
                       drawLine(canvas, rightKnee, rightAnkle, linePaintGreen);
                       // for angle
                       drawAngle(canvas, leftElbow_angle, rightElbow, textPaint);
                       drawAngle(canvas, rightElbow_angle, leftElbow, textPaint);
                       drawAngle(canvas, leftHip_angle, leftHip, textPaint);
                       drawAngle(canvas, rightHip_angle, rightHip, textPaint);
                       drawAngle(canvas, leftKnee_angle, leftKnee, textPaint);
                       drawAngle(canvas, rightKnee_angle, rightKnee, textPaint);
                       // for cordidanates
                       drawPoint(canvas, leftShoulder, dotPaint);
                       drawPoint(canvas, rightShoulder, dotPaint);
                       drawPoint(canvas, leftElbow, dotPaint);
                       drawPoint(canvas, rightElbow, dotPaint);
                       drawPoint(canvas, leftWrist, dotPaint);
                       drawPoint(canvas, rightWrist, dotPaint);
                       drawPoint(canvas, leftShoulder, dotPaint);
                       drawPoint(canvas, rightHip, dotPaint);
                       drawPoint(canvas, leftHip, dotPaint);
                       drawPoint(canvas, leftKnee, dotPaint);
                       drawPoint(canvas, rightKnee, dotPaint);
                       drawPoint(canvas, leftAnkle, dotPaint);
                       drawPoint(canvas, rightAnkle, dotPaint);
                       isUp = true;
                       isDown = false;
                       lineTwoText = "start up";
                   }
               }
           }
        }
        drawText(canvas, lineOneText,1);
        drawText(canvas, lineTwoText,2);


    }

    private void  drawText(Canvas canvas, String text, Integer line) {


       canvas.drawText(text, TEXT_SIZE*0.5f, TEXT_SIZE*3 + TEXT_SIZE*line, textPaint1);
    }


    private void drawLine(Canvas canvas, PoseLandmark start, PoseLandmark end, Paint linePaintGreen) {

        canvas.drawLine(
                translateX(start.getPosition().x), translateY(start.getPosition().y),
                translateX(end.getPosition().x), translateY(end.getPosition().y),linePaintGreen);
    }

    private void Line(Canvas canvas, PoseLandmark start, PoseLandmark end, Paint linePaint) {
        canvas.drawLine(
                translateX(start.getPosition().x), translateY(start.getPosition().y),
                translateX(end.getPosition().x), translateY(end.getPosition().y),linePaint);
    }
    void drawAngle (Canvas canvas,int angle, PoseLandmark point, Paint textPaint){
        canvas.drawText(String.valueOf(angle), translateX(point.getPosition().x), translateY(point.getPosition().y), textPaint);
    }

    void drawPoint (Canvas canvas, PoseLandmark landmark, Paint paint){
        canvas.drawCircle(translateX(landmark.getPosition().x), translateY(landmark.getPosition().y), DOT_RADIUS, paint);
    }
}

