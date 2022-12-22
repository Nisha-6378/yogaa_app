/*
 * Copyright 2020 Google LLC. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.mlkit.vision.demo.java.posedetector;

import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.media.MediaPlayer;
import android.util.Log;

import androidx.annotation.NonNull;

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
public class PoseGraphic extends Graphic {
    private   MediaPlayer Player2;
    private static final float DOT_RADIUS = 15;
    private static final float IN_FRAME_LIKELIHOOD_TEXT_SIZE = 30.0f;
    private static float threshold = 0f;

    private final Pose pose;
    private boolean showInFrameLikelihood;
    private float dynamicPaintThreshold = 6.5f;
    private float angleError;
    private final Paint linePaint;
    private final Paint linePaintGreen;
    private final Paint dynamicLinePaint;
    private final Paint dotPaint;
    private final Paint textPaint;
    private final Paint clinePaint;
    private final Paint rclinePaint;
    boolean IsAudioRunning = false;

    PoseGraphic(GraphicOverlay overlay, Pose pose, boolean showInFrameLikelihood) {
        super(overlay);


        this.pose = pose;
        this.showInFrameLikelihood = true;
        this.angleError = 0;

        dotPaint = new Paint();
        dotPaint.setColor(Color.WHITE);
        dotPaint.setTextSize(IN_FRAME_LIKELIHOOD_TEXT_SIZE);

        linePaint = new Paint();
        linePaint.setStrokeWidth(17);
        linePaint.setColor(Color.CYAN);

        clinePaint = new Paint();
        clinePaint.setStrokeWidth(10);
        clinePaint.setColor(Color.GREEN);

        rclinePaint = new Paint();
        rclinePaint.setStrokeWidth(10);
        rclinePaint.setColor(Color.RED);

        linePaintGreen = new Paint();
        linePaintGreen.setStrokeWidth(17);
        linePaintGreen.setColor(Color.GREEN);

        dynamicLinePaint = new Paint();
        dynamicLinePaint.setStrokeWidth(17.5f);

        textPaint = new Paint();
        textPaint.setColor(Color.GREEN);
        textPaint.setTextSize(50);

    }

    public static int getScreenWidth() {
        return Resources.getSystem().getDisplayMetrics().widthPixels;
    }

    public static int getScreenHeight() {
        return Resources.getSystem().getDisplayMetrics().heightPixels;
    }



//    public static void audio(final int x) {
//        Thread thread = new Thread() {
//
//            @Override
//            public void run() {
//
//                try {
//                    if ((x>= 95) && ( x <= 100)) {
//
//                        MediaPlayer mediaPlayer = MediaPlayer.create(getApplicationContext(), R.raw.tpose);
//                        mediaPlayer.start();
//                        x=0;
//
//                    }
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//            }
//
//
//        };
//        thread.start();
//
//    }


    private int getRed(Float value){
        return (int)((1-value)*255);
//        return 0;
    }

    private int getGreen(Float value){
        return (int)((value)*255);
//        return 0;
    }

    @Override
    public void draw(Canvas canvas) {
        List<PoseLandmark> landmarks = pose.getAllPoseLandmarks();
        if (landmarks.isEmpty()) {
            return;
        }

        // Getting all the landmarks

        PoseLandmark leftShoulder = pose.getPoseLandmark(PoseLandmark.LEFT_SHOULDER);
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
        showInFrameLikelihood = true;

        if(leftShoulder.getInFrameLikelihood()<threshold){
            showInFrameLikelihood = false;
        }
        if(rightShoulder.getInFrameLikelihood()<threshold){
            showInFrameLikelihood = false;
        }
        if(leftElbow.getInFrameLikelihood()<threshold){
            showInFrameLikelihood = false;
        }
        if(rightElbow.getInFrameLikelihood()<threshold){
            showInFrameLikelihood = false;
        }
        if(leftWrist.getInFrameLikelihood()<threshold){
            showInFrameLikelihood = false;
        }
        if(rightWrist.getInFrameLikelihood()<threshold){
            showInFrameLikelihood = false;
        }
        if(leftHip.getInFrameLikelihood()<threshold){
            showInFrameLikelihood = false;
        }
        if(rightHip.getInFrameLikelihood()<threshold){
            showInFrameLikelihood = false;
        }
        if(leftKnee.getInFrameLikelihood()<threshold){
            showInFrameLikelihood = false;
        }
        if(rightKnee.getInFrameLikelihood()<threshold){
            showInFrameLikelihood = false;
        }
        if(leftAnkle.getInFrameLikelihood()<threshold){
            showInFrameLikelihood = false;
        }
        if(rightAnkle.getInFrameLikelihood()<threshold){
            showInFrameLikelihood = false;
        }

        if (showInFrameLikelihood==true) {


            //Angles
            SmoothAngles smoothAngles = new SmoothAngles();
            int leftElbow_angle = (int) smoothAngles.getAngle(leftShoulder, leftElbow, leftWrist);
            int rightElbow_angle = (int) smoothAngles.getAngle(rightShoulder, rightElbow, rightWrist);


            int refrence = 175;
            if ((refrence - 5 <= leftElbow_angle & leftElbow_angle <= refrence + 5) & (refrence - 5 <= rightElbow_angle & rightElbow_angle <= refrence + 5)) {
                // line
                Line(canvas, leftShoulder, rightShoulder, clinePaint);
                Line(canvas, leftShoulder, leftElbow, clinePaint);
                Line(canvas, leftElbow, leftWrist, clinePaint);
                Line(canvas, rightShoulder, rightElbow, clinePaint);
                Line(canvas, rightElbow, rightWrist, clinePaint);
                Line(canvas, leftElbow, leftWrist, clinePaint);
                // for angle
                drawAngle(canvas, leftElbow_angle, rightElbow, textPaint);

                drawAngle(canvas, rightElbow_angle, leftElbow, textPaint);
                drawPoint(canvas, leftShoulder, dotPaint);
                drawPoint(canvas, rightShoulder, dotPaint);
                drawPoint(canvas, leftElbow, dotPaint);
                drawPoint(canvas, rightElbow, dotPaint);
                drawPoint(canvas, leftWrist, dotPaint);
                drawPoint(canvas, rightWrist, dotPaint);


            } else {

                Log.e("XAPP", "onPrepared " + Long.toString(Thread.currentThread().getId()));
                cLine(canvas, leftShoulder, rightShoulder, rclinePaint);
                cLine(canvas, leftShoulder, leftElbow, rclinePaint);
                cLine(canvas, leftElbow, leftWrist, rclinePaint);
                cLine(canvas, rightShoulder, rightElbow, rclinePaint);
                cLine(canvas, rightElbow, rightWrist, rclinePaint);
                cLine(canvas, leftElbow, leftWrist, rclinePaint);
                // for angle
                drawAngle(canvas, leftElbow_angle, rightElbow, textPaint);

                drawAngle(canvas, rightElbow_angle, leftElbow, textPaint);
                drawPoint(canvas, leftShoulder, dotPaint);
                drawPoint(canvas, rightShoulder, dotPaint);
                drawPoint(canvas, leftElbow, dotPaint);
                drawPoint(canvas, rightElbow, dotPaint);
                drawPoint(canvas, leftWrist, dotPaint);
                drawPoint(canvas, rightWrist, dotPaint);

                //VisionProcessorBase.audio();


                    // some code
                // VisionProcessorBase.call_method();// 'Method' is Name of the any one method in SecondActivity



                // VisionProcessorBase.audio(frame);

//                if (IsAudioRunning == false) {
//                    IsAudioRunning=true;
//                    Thread thread = new Thread() {
//
//                        @Override
//                        public void run() {
//                            try {
//                                    MediaPlayer mediaPlayer = MediaPlayer.create(getApplicationContext(), R.raw.song11);
//                                    mediaPlayer.start();
//                                    Thread.sleep(10000);
//
//
//                            } catch (Exception e) {
//                                e.printStackTrace();
//                            }
//
//                        }
//                    };
//
//                 thread.start();

//
//                }
            }

             }

        }

    private void cLine (Canvas canvas, @NonNull PoseLandmark rstart, PoseLandmark rend, Paint rclinePaint){
                canvas.drawLine(
                        translateX(rstart.getPosition().x), translateY(rstart.getPosition().y),
                        translateX(rend.getPosition().x), translateY(rend.getPosition().y), rclinePaint);
    }


        void drawAngle (Canvas canvas,int angle, PoseLandmark point, Paint textPaint){
            canvas.drawText(String.valueOf(angle), translateX(point.getPosition().x), translateY(point.getPosition().y), textPaint);
        }

        void drawPoint (Canvas canvas, PoseLandmark landmark, Paint paint){
            canvas.drawCircle(translateX(landmark.getPosition().x), translateY(landmark.getPosition().y), DOT_RADIUS, paint);
        }

        void drawLine (Canvas canvas, PoseLandmark startLandmark, PoseLandmark endLandmark, Paint
        paint){
            canvas.drawLine(
                    translateX(startLandmark.getPosition().x), translateY(startLandmark.getPosition().y),
                    translateX(endLandmark.getPosition().x), translateY(endLandmark.getPosition().y), paint);
        }
        void Line (Canvas canvas, PoseLandmark start, PoseLandmark end, Paint clinePaint ){
            canvas.drawLine(
                    translateX(start.getPosition().x), translateY(start.getPosition().y),
                    translateX(end.getPosition().x), translateY(end.getPosition().y), clinePaint);
        }

}
