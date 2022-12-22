//package com.google.mlkit.vision.demo.java.posedetector;
//
//
////import static com.google.mlkit.vision.demo.java.musicplayer.song.audioPlayer;
//
//
//
//
//
//import static android.util.Pair.create;
//import static com.google.common.reflect.Reflection.getPackageName;
//import static com.google.mlkit.vision.demo.R.*;
//import static com.google.mlkit.vision.demo.R.raw.music;
//import static com.google.mlkit.vision.demo.R.raw.song;
//import static com.google.mlkit.vision.demo.R.raw.song11;
//
//import android.content.Context;
//        import android.content.res.Resources;
//import android.graphics.Canvas;
//import android.graphics.Color;
//import android.graphics.Paint;
//import android.graphics.PointF;
//
//import android.media.AudioManager;
//import android.media.MediaPlayer;
//import android.net.Uri;
//import android.util.Log;
//import android.view.View;
//import android.widget.AdapterView;
//
//import androidx.annotation.NonNull;
//
//
//import com.google.mlkit.vision.demo.EntryChoiceActivity;
//import com.google.mlkit.vision.demo.GraphicOverlay;
//
//import com.google.mlkit.vision.demo.R;
//import com.google.mlkit.vision.demo.java.LivePreviewActivity;
//import com.google.mlkit.vision.pose.Pose;
//import com.google.mlkit.vision.pose.PoseLandmark;
//
//
//import java.util.List;
//
//
//
//public class PoseGraphic1 extends Graphic {
//
//    private static final float DOT_RADIUS = 15;
//    private static final float IN_FRAME_LIKELIHOOD_TEXT_SIZE = 30.0f;
//    private static float threshold = 0f;
//    private static boolean IsAudioRunning = false;
//    private final Pose pose;
//    private boolean showInFrameLikelihood;
//    private float dynamicPaintThreshold = 6.5f;
//    private float angleError;
//    private final Paint linePaint;
//    private final Paint linePaintGreen;
//    private final Paint dynamicLinePaint;
//    private final Paint dotPaint;
//    private final Paint textPaint;
//    private final Paint clinePaint;
//    private final Paint rclinePaint;
//    private Context context;
////    private  MediaPlayer player = MediaPlayer.create(this, song11);
//
//    PoseGraphic1(GraphicOverlay overlay, Pose pose, boolean showInFrameLikelihood) {
//        super(overlay);
//        MediaPlayer mediaPlayer;
//
//        final float DOT_RADIUS = 60.0f;
//
//        this.pose = pose;
//        this.showInFrameLikelihood = true;
//        this.angleError = 0;
//
//        dotPaint = new Paint();
//        dotPaint.setColor(Color.WHITE);
//        dotPaint.setTextSize(IN_FRAME_LIKELIHOOD_TEXT_SIZE);
//
//        linePaint = new Paint();
//        linePaint.setStrokeWidth(5);
//        linePaint.setColor(Color.CYAN);
//
//        clinePaint = new Paint();
//        clinePaint.setStrokeWidth(10);
//        clinePaint.setColor(Color.GREEN);
//
//        rclinePaint = new Paint();
//        rclinePaint.setStrokeWidth(10);
//        rclinePaint.setColor(Color.RED);
//
//
//        linePaintGreen = new Paint();
//        linePaintGreen.setStrokeWidth(17);
//        linePaintGreen.setColor(Color.YELLOW);
//
//        dynamicLinePaint = new Paint();
//        dynamicLinePaint.setStrokeWidth(17.5f);
//
//        textPaint = new Paint();
//        textPaint.setColor(Color.BLUE);
//        textPaint.setTextSize(50);
//
//    }
//
//    public static int getScreenWidth() {
//        return Resources.getSystem().getDisplayMetrics().widthPixels;
//    }
//
//    public static int getScreenHeight() {
//        return Resources.getSystem().getDisplayMetrics().heightPixels;
//    }
//
//    private int getRed(Float value) {
//        return (int) ((1 - value) * 255);
//
//
//    }
//
//    private int getGreen(Float value) {
//        return (int) ((value) * 255);
//
//    }
//
////
//
//
//    @Override
//    public void draw(Canvas canvas) {
//        /* super.getClass(canvas); */
//        List<PoseLandmark> landmarks = pose.getAllPoseLandmarks();
//        if (landmarks.isEmpty()) {
//            return;
//        }
//
//
//        // get cordinate of pose
//        PoseLandmark leftShoulder = pose.getPoseLandmark(PoseLandmark.LEFT_SHOULDER);
//        PoseLandmark rightShoulder = pose.getPoseLandmark(PoseLandmark.RIGHT_SHOULDER);
//        PoseLandmark leftElbow = pose.getPoseLandmark(PoseLandmark.LEFT_ELBOW);
//        PoseLandmark rightElbow = pose.getPoseLandmark(PoseLandmark.RIGHT_ELBOW);
//        PoseLandmark leftWrist = pose.getPoseLandmark(PoseLandmark.LEFT_WRIST);
//        PoseLandmark rightWrist = pose.getPoseLandmark(PoseLandmark.RIGHT_WRIST);
//        PoseLandmark leftHip = pose.getPoseLandmark(PoseLandmark.LEFT_HIP);
//        PoseLandmark rightHip = pose.getPoseLandmark(PoseLandmark.RIGHT_HIP);
//        // for Lunges pose
//
//        PoseLandmark leftKnee = pose.getPoseLandmark(PoseLandmark.LEFT_KNEE);
//        PoseLandmark rightKnee = pose.getPoseLandmark(PoseLandmark.RIGHT_KNEE);
//        PoseLandmark leftAnkle = pose.getPoseLandmark(PoseLandmark.LEFT_ANKLE);
//        PoseLandmark rightAnkle = pose.getPoseLandmark(PoseLandmark.RIGHT_ANKLE);
//
//
//        // Frame Visibility Check
//
//        showInFrameLikelihood = true;
//
//
//        if (leftShoulder.getInFrameLikelihood() < threshold) {
//
//            showInFrameLikelihood = false;
//
//        }
//
//        if (rightShoulder.getInFrameLikelihood() < threshold) {
//
//            showInFrameLikelihood = false;
//
//        }
//
//        if (leftElbow.getInFrameLikelihood() < threshold) {
//
//            showInFrameLikelihood = false;
//
//        }
//
//        if (rightElbow.getInFrameLikelihood() < threshold) {
//
//            showInFrameLikelihood = false;
//
//        }
//
//        if (leftWrist.getInFrameLikelihood() < threshold) {
//
//            showInFrameLikelihood = false;
//
//        }
//
//        if (rightWrist.getInFrameLikelihood() < threshold) {
//
//            showInFrameLikelihood = false;
//
//        }
//        if (leftHip.getInFrameLikelihood() < threshold) {
//            showInFrameLikelihood = false;
//        }
//        if (rightHip.getInFrameLikelihood() < threshold) {
//            showInFrameLikelihood = false;
//        }
//        if (leftKnee.getInFrameLikelihood() < threshold) {
//            showInFrameLikelihood = false;
//        }
//        if (rightKnee.getInFrameLikelihood() < threshold) {
//            showInFrameLikelihood = false;
//        }
//        if (leftAnkle.getInFrameLikelihood() < threshold) {
//            showInFrameLikelihood = false;
//        }
//        if (rightAnkle.getInFrameLikelihood() < threshold) {
//            showInFrameLikelihood = false;
//        }
//
//
//        if (showInFrameLikelihood == true) {
//
//
//            //get Angle
//            SmoothAngles smoothAngles = new SmoothAngles();
//            // int leftShoulder_angle = (int) smoothAngles.getAngle(leftElbow, leftShoulder, leftHip);
//            // int rightShoulder_angle = (int) smoothAngles.getAngle(rightElbow, rightShoulder, rightHip);
//            int leftElbow_angle = (int) smoothAngles.getAngle(leftShoulder, leftElbow, leftWrist);
//            int rightElbow_angle = (int) smoothAngles.getAngle(rightShoulder, rightElbow, rightWrist);
//            int leftKnee_angle = (int) smoothAngles.getAngle(leftAnkle, leftKnee, leftHip);
//            int rightKnee_angle = (int) smoothAngles.getAngle(rightAnkle, rightKnee, rightHip);
//
//
////            drawPoint(canvas, leftShoulder, dotPaint);
////            drawPoint(canvas, rightShoulder, dotPaint);
////            drawPoint(canvas, leftElbow, dotPaint);
////            drawPoint(canvas, rightElbow, dotPaint);
////            drawPoint(canvas, leftWrist, dotPaint);
////            drawPoint(canvas, rightWrist, dotPaint);
//
//
//            int refrence = 175;
//            if ((refrence - 5 <= leftElbow_angle & leftElbow_angle <= refrence + 5) & (refrence - 5 <= rightElbow_angle & rightElbow_angle <= refrence + 5)) {
//                // line
//                Line(canvas, leftShoulder, rightShoulder, clinePaint);
//                Line(canvas, leftShoulder, leftElbow, clinePaint);
//                Line(canvas, leftElbow, leftWrist, clinePaint);
//                Line(canvas, rightShoulder, rightElbow, clinePaint);
//                Line(canvas, rightElbow, rightWrist, clinePaint);
//                Line(canvas, leftElbow, leftWrist, clinePaint);
//                // for angle
//                drawAngle(canvas, leftElbow_angle, rightElbow, textPaint);
//
//                drawAngle(canvas, rightElbow_angle, leftElbow, textPaint);
//                drawPoint(canvas, leftShoulder, dotPaint);
//                drawPoint(canvas, rightShoulder, dotPaint);
//                drawPoint(canvas, leftElbow, dotPaint);
//                drawPoint(canvas, rightElbow, dotPaint);
//                drawPoint(canvas, leftWrist, dotPaint);
//                drawPoint(canvas, rightWrist, dotPaint);
//
//
//            } else {
//
//                Log.e("XAPP", "onPrepared " + Long.toString(Thread.currentThread().getId()));
//                boolean IsAudioRunning = false;
//                if (IsAudioRunning == false)
//                    //audio();
//
//                    // audioPlayer(String.valueOf(Uri.parse("android.resource://com.google.mlkit.vision.demo/" + R.raw.song1)));
//                    // audioPlayer("android.resource://com.google.mlkit.vision.demo/raw/song1.mp3");
//                    audioPlayer("https://www.soundhelix.com/examples/mp3/SoundHelix-Song-1.mp3");
//                //audioPlayer1();
//
//
////                      MediaPl ayer music = MediaPlayer.create(this,R.raw.song);
////                        music.start();
//
//                // audioPlayer("R.raw.song");
//
//                cLine(canvas, leftShoulder, rightShoulder, rclinePaint);
//                cLine(canvas, leftShoulder, leftElbow, rclinePaint);
//                cLine(canvas, leftElbow, leftWrist, rclinePaint);
//                cLine(canvas, rightShoulder, rightElbow, rclinePaint);
//                cLine(canvas, rightElbow, rightWrist, rclinePaint);
//                cLine(canvas, leftElbow, leftWrist, rclinePaint);
//                // for angle
//                drawAngle(canvas, leftElbow_angle, rightElbow, textPaint);
//
//                drawAngle(canvas, rightElbow_angle, leftElbow, textPaint);
//                drawPoint(canvas, leftShoulder, dotPaint);
//                drawPoint(canvas, rightShoulder, dotPaint);
//                drawPoint(canvas, leftElbow, dotPaint);
//                drawPoint(canvas, rightElbow, dotPaint);
//                drawPoint(canvas, leftWrist, dotPaint);
//                drawPoint(canvas, rightWrist, dotPaint);
//
//            }
//            int lunges = 80;
//            if ((lunges - 10 <= leftKnee_angle) && (leftKnee_angle <= lunges + 10)) {
//                Line(canvas, leftHip, leftKnee, clinePaint);
//                Line(canvas, leftKnee, leftAnkle, clinePaint);
//
//                // for angle
//                //drawAngleLunges(canvas, leftKnee_angle, textPaint);
//
//
//                drawPoint(canvas, leftHip, dotPaint);
//                drawPoint(canvas, leftKnee, dotPaint);
//                drawPoint(canvas, leftAnkle, dotPaint);
//
//            } else if ((lunges - 10 <= rightKnee_angle) && (rightKnee_angle <= lunges + 10)) {
//                Line(canvas, rightHip, rightKnee, clinePaint);
//                Line(canvas, rightKnee, rightAnkle, clinePaint);
//
//                // for angle
//                // drawAngleLunges(canvas, rightKnee_angle, textPaint);
//
//
//                drawPoint(canvas, rightHip, dotPaint);
//                drawPoint(canvas, rightKnee, dotPaint);
//                drawPoint(canvas, rightAnkle, dotPaint);
//            }
//
//        }
//
//    }
//
//
////    private void drawAngleLunges(Canvas canvas, int point, Paint textPaint) {
////        char[] angle;
////        canvas.drawText(String.valueOf(angle), translateX(point), translateY(point), textPaint);
////    }
//
//
////    public void audioPlayer(String path) {
////        // set up MediaPlayer
////        MediaPlayer mp = new MediaPlayer();
////        mp.setAudioStreamType(AudioManager.STREAM_MUSIC);
////        try {
////             mp.setDataSource(path);
////             mp.prepare();
////
////           // mp.prepareAsync();
////            mp.start();
////        } catch (Exception e) {
////            e.printStackTrace();
////        }
////    }
//
//
//    public void audioPlayer(String path) {
//        if (IsAudioRunning == false) {
//            IsAudioRunning = true;
//            Thread thread = new Thread() {
//
//
//                @Override
//                public void run() {
//                    MediaPlayer mp = new MediaPlayer();
//                    mp.setAudioStreamType(AudioManager.STREAM_MUSIC);
//
//
//                    try {
////                       player=MediaPlayer.create(this,R.raw.song11);
////                       player.start();;
//                        mp.setDataSource(path);
//                        mp.prepare();
//                        mp.start();
////                       Thread.sleep(30000);
//                        IsAudioRunning = false;
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                    }
//                    //Thread.sleep(1000);
//                }
//            };
//
//
//            thread.start();
//
//        }
//    }
//
//
//
//
//
//
////   //public  void  audioPlayer1() {
////     MediaPlayer.create(this, R.raw.sample).start();
////   }
//
//
//
////
////   private void audio()
////   {
////       MediaPlayer music = MediaPlayer.create(context; this,R.raw.music);
////               music.start();
////   }
//
//
//
////    private void playAudio() {
////
////
////        String audioUrl = "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-1.mp3";
////        MediaPlayer mediaPlayer = new MediaPlayer();
////        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
////        try {
////            mediaPlayer.setDataSource(audioUrl);
////            mediaPlayer.prepare();
////            mediaPlayer.start();
////        } catch (IOException e) {
////            e.printStackTrace();
////
////        }
//
//
//
//
//        private void cLine (Canvas canvas, @NonNull PoseLandmark rstart, PoseLandmark rend, Paint rclinePaint){
//                canvas.drawLine(
//                        translateX(rstart.getPosition().x), translateY(rstart.getPosition().y),
//                        translateX(rend.getPosition().x), translateY(rend.getPosition().y), rclinePaint);
//            }
//
//
//        void drawAngle (Canvas canvas,int angle, PoseLandmark point, Paint textPaint){
//
////          DecimalFormat df = new DecimalFormat("#0");
////                 angle = float.valueOf(df.format(angle));
//
//            canvas.drawText(String.valueOf(angle), translateX(point.getPosition().x), translateY(point.getPosition().y), textPaint);
//
//        }
//
//        void drawPoint (Canvas canvas, PoseLandmark landmark, Paint paint){
//            canvas.drawCircle(translateX(landmark.getPosition().x), translateY(landmark.getPosition().y), DOT_RADIUS, paint);
//        }
//
//        void drawLine (Canvas canvas, PoseLandmark startLandmark, PoseLandmark endLandmark, Paint
//        paint){
//            canvas.drawLine(
//                    translateX(startLandmark.getPosition().x), translateY(startLandmark.getPosition().y),
//                    translateX(endLandmark.getPosition().x), translateY(endLandmark.getPosition().y), paint);
//        }
//        void Line (Canvas canvas, PoseLandmark start, PoseLandmark end, Paint clinePaint ){
//            canvas.drawLine(
//                    translateX(start.getPosition().x), translateY(start.getPosition().y),
//                    translateX(end.getPosition().x), translateY(end.getPosition().y), clinePaint);
//        }
//
//
//
//}
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
