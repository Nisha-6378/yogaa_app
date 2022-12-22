///*
// * Copyright 2020 Google LLC. All rights reserved.
// *
// * Licensed under the Apache License, Version 2.0 (the "License");
// * you may not use this file except in compliance with the License.
// * You may obtain a copy of the License at
// *
// *     http://www.apache.org/licenses/LICENSE-2.0
// *
// * Unless required by applicable law or agreed to in writing, software
// * distributed under the License is distributed on an "AS IS" BASIS,
// * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// * See the License for the specific language governing permissions and
// * limitations under the License.
// */
//
//package com.google.mlkit.vision.demo.java;
//
//import static com.google.mlkit.vision.demo.GraphicOverlay.Graphic.getApplicationContext;
//
//import android.app.ActivityManager;
//import android.app.ActivityManager.MemoryInfo;
//import android.content.Context;
//import android.graphics.Bitmap;
//import android.media.MediaPlayer;
//import android.os.Build.VERSION_CODES;
//import android.os.SystemClock;
//import android.util.Log;
//import android.widget.Toast;
//import androidx.annotation.GuardedBy;
//import androidx.annotation.NonNull;
//import androidx.annotation.Nullable;
//import androidx.annotation.RequiresApi;
//import androidx.camera.core.ExperimentalGetImage;
//import androidx.camera.core.ImageProxy;
//import com.google.android.gms.tasks.Task;
//import com.google.android.gms.tasks.TaskExecutors;
//import com.google.android.gms.tasks.Tasks;
//import com.google.android.odml.image.BitmapMlImageBuilder;
//import com.google.android.odml.image.ByteBufferMlImageBuilder;
//import com.google.android.odml.image.MediaMlImageBuilder;
//import com.google.android.odml.image.MlImage;
//import com.google.mlkit.common.MlKitException;
//import com.google.mlkit.vision.common.InputImage;
//import com.google.mlkit.vision.demo.BitmapUtils;
//import com.google.mlkit.vision.demo.CameraImageGraphic;
//import com.google.mlkit.vision.demo.FrameMetadata;
//import com.google.mlkit.vision.demo.GraphicOverlay;
//import com.google.mlkit.vision.demo.InferenceInfoGraphic;
//import com.google.mlkit.vision.demo.R;
//import com.google.mlkit.vision.demo.ScopedExecutor;
//import com.google.mlkit.vision.demo.VisionImageProcessor;
//import com.google.mlkit.vision.demo.java.posedetector.PoseDetectorProcessor;
//import com.google.mlkit.vision.demo.preference.PreferenceUtils;
//import com.google.mlkit.vision.pose.Pose;
//
//import java.nio.ByteBuffer;
//import java.util.Timer;
//import java.util.TimerTask;
//
///**
// * Abstract base class for vision frame processors. Subclasses need to implement {@link
// * #onSuccess(Object, GraphicOverlay)} to define what they want to with the detection results and
// * {@link #detectInImage(InputImage)} to specify the detector object.
// *
// * @param <T> The type of the detected feature.
// */
//public abstract class VisionProcessorBase<T> implements VisionImageProcessor {
//
//  protected static final String MANUAL_TESTING_LOG = "LogTagForTest";
//  private static final String TAG = "VisionProcessorBase";
//  private static Boolean IsAudioRunning = false;
//  private final ActivityManager activityManager;
//  private final Timer fpsTimer = new Timer();
//  private final ScopedExecutor executor;
//  private static MediaPlayer mediaPlayer = null;
//
//  // Whether this processor is already shut down
//  private boolean isShutdown;
//
//  // Used to calculate latency, running in the same thread, no sync needed.
//  private int numRuns = 0;
//  private long totalRunMs = 0;
//  private long maxRunMs = 0;
//  private long minRunMs = Long.MAX_VALUE;
//
//  // Frame count that have been processed so far in an one second interval to calculate FPS.
//  private int frameProcessedInOneSecondInterval = 0;
//  private int framesPerSecond = 0;
//  private static Integer frame=0;
//  // To keep the latest images and its metadata.
//  @GuardedBy("this")
//  private ByteBuffer latestImage;
//
//  @GuardedBy("this")
//  private FrameMetadata latestImageMetaData;
//  // To keep the images and metadata in process.
//  @GuardedBy("this")
//  private ByteBuffer processingImage;
//
//  @GuardedBy("this")
//  private FrameMetadata processingMetaData;
//
//  protected VisionProcessorBase(Context context) {
//    activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
//    executor = new ScopedExecutor(TaskExecutors.MAIN_THREAD);
//    fpsTimer.scheduleAtFixedRate(
//            new TimerTask() {
//              @Override
//              public void run() {
//                framesPerSecond = frameProcessedInOneSecondInterval;
//                frameProcessedInOneSecondInterval = 0;
//              }
//            },
//            /* delay= */ 0,
//            /* period= */ 1000);
//  }
//
//  public  static void AudioStop()
//  {
//    try {
//      if (mediaPlayer.isPlaying() == true)
//        mediaPlayer.stop();
//    } catch (Exception e) {
//      e.printStackTrace();
//    }
//  }
//
//  public static void AudioforError( Integer type) {
//    Thread thread = new Thread() {
//      @Override
//      public void run() {
//        try {
//          Integer AudioFileName = 0;
//          if (type == 1)
//            AudioFileName = R.raw.plank_error;
//          else if (type == 2)
//            AudioFileName = R.raw.hip_error;
//          else if (type == 3)
//            AudioFileName = R.raw.staff_pose_nose;
//          else if(type == 4)
//            AudioFileName = R.raw.wrong;
//          else if(type==5)
//            AudioFileName=R.raw.squats_back_error;
//          else if(type==6)
//            AudioFileName = R.raw.squats_knee_error;
//
//          if (mediaPlayer == null || mediaPlayer.isPlaying() == false) {
//            if (frame == 0 || frame >= 100 ) {
//              frame = 1;
//              mediaPlayer = MediaPlayer.create(getApplicationContext(), AudioFileName);
//              mediaPlayer.start();
//            }
//            else
//            {
//              frame += 1;
//            }
//          }
//        } catch (Exception e) {
//          e.printStackTrace();
//          IsAudioRunning = false;
//        }
//      }
//    };
//    thread.start();
//  }
//
//  // -----------------Code for processing single still image----------------------------------------
////  @Override
////  public void processBitmap(Bitmap bitmap, final GraphicOverlay graphicOverlay) {
////    requestDetectInImage(
////            InputImage.fromBitmap(bitmap, 0),
////            graphicOverlay,
////            /* originalCameraImage= */ null,
////            /* shouldShowFps= */ false);
////      frame +=1;
////      Log.e("ppp", String.valueOf(frame));
//
// // }
//
//
//
//  @Override
//  public void processBitmap(Bitmap bitmap, final GraphicOverlay graphicOverlay) {
//    long frameStartMs = SystemClock.elapsedRealtime();
//
//    if (isMlImageEnabled(graphicOverlay.getContext())) {
//      MlImage mlImage = new BitmapMlImageBuilder(bitmap).build();
//      requestDetectInImage(
//              mlImage,
//              graphicOverlay,
//              /* originalCameraImage= */ null,
//              /* shouldShowFps= */ false,
//              frameStartMs);
//      mlImage.close();
//
//      return;
//    }
//
//    requestDetectInImage(
//            InputImage.fromBitmap(bitmap, 0),
//            graphicOverlay,
//            /* originalCameraImage= */ null,
//            /* shouldShowFps= */ false,
//            frameStartMs);
//  }
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
//  // -----------------Code for processing live preview frame from Camera1 API-----------------------
//  @Override
//  public synchronized void processByteBuffer(
//          ByteBuffer data, final FrameMetadata frameMetadata, final GraphicOverlay graphicOverlay) {
//    latestImage = data;
//    latestImageMetaData = frameMetadata;
//    if (processingImage == null && processingMetaData == null) {
//      processLatestImage(graphicOverlay);
//    }
//  }
//
//  private synchronized void processLatestImage(final GraphicOverlay graphicOverlay) {
//    processingImage = latestImage;
//    processingMetaData = latestImageMetaData;
//    latestImage = null;
//    latestImageMetaData = null;
//    if (processingImage != null && processingMetaData != null && !isShutdown) {
//      processImage(processingImage, processingMetaData, graphicOverlay);
//    }
//  }
//
//  private void processImage(
//          ByteBuffer data, final FrameMetadata frameMetadata, final GraphicOverlay graphicOverlay) {
//
//          //
//    long frameStartMs = SystemClock.elapsedRealtime();
//    // If live viewport is on (that is the underneath surface view takes care of the camera preview
//    // drawing), skip the unnecessary bitmap creation that used for the manual preview drawing.
//    Bitmap bitmap =
//            PreferenceUtils.isCameraLiveViewportEnabled(graphicOverlay.getContext())
//                    ? null
//                    : BitmapUtils.getBitmap(data, frameMetadata);
//    if (isMlImageEnabled(graphicOverlay.getContext())) {
//      MlImage mlImage =
//              new ByteBufferMlImageBuilder(
//                      data,
//                      frameMetadata.getWidth(),
//                      frameMetadata.getHeight(),
//                      MlImage.IMAGE_FORMAT_NV21)
//                      .setRotation(frameMetadata.getRotation())
//                      .build();
//      requestDetectInImage(mlImage, graphicOverlay, bitmap, /* shouldShowFps= */ true, frameStartMs)
//              .addOnSuccessListener(executor, results -> processLatestImage(graphicOverlay));
//
//      // This is optional. Java Garbage collection can also close it eventually.
//      mlImage.close();
//      return;
//    }
//
//    requestDetectInImage(
//            InputImage.fromByteBuffer(
//                    data,
//                    frameMetadata.getWidth(),
//                    frameMetadata.getHeight(),
//                    frameMetadata.getRotation(),
//                    InputImage.IMAGE_FORMAT_NV21),
//            graphicOverlay,
//            bitmap,
//            /* shouldShowFps= */ true, frameStartMs)
//            .addOnSuccessListener(executor, results -> processLatestImage(graphicOverlay));
//
//  }
//
//  // -----------------Code for processing live preview frame from CameraX API-----------------------
//  @Override
//  @RequiresApi(VERSION_CODES.KITKAT)
//  @ExperimentalGetImage
//  public void processImageProxy(ImageProxy image, GraphicOverlay graphicOverlay) {
//    if (isShutdown) {
//      image.close();
//      return;
//    }
//
//    Bitmap bitmap = null;
//    if (!PreferenceUtils.isCameraLiveViewportEnabled(graphicOverlay.getContext())) {
//      bitmap = BitmapUtils.getBitmap(image);
//
//    }
//    long frameStartMs = 0;
//    if (isMlImageEnabled(graphicOverlay.getContext())) {
//      MlImage mlImage =
//              new MediaMlImageBuilder(image.getImage())
//                      .setRotation(image.getImageInfo().getRotationDegrees())
//                      .build();
//      frameStartMs = 0;
//      requestDetectInImage(
//              mlImage,
//              graphicOverlay,
//              /* originalCameraImage= */ bitmap,
//              /* shouldShowFps= */ true,
//              frameStartMs)
//              // When the image is from CameraX analysis use case, must call image.close() on received
//              // images when finished using them. Otherwise, new images may not be received or the
//              // camera may stall.
//              // Currently MlImage doesn't support ImageProxy directly, so we still need to call
//              // ImageProxy.close() here.
//              .addOnCompleteListener(results -> image.close());
//      return;
//    }
//
//
//    requestDetectInImage(
//            InputImage.fromMediaImage(image.getImage(), image.getImageInfo().getRotationDegrees()),
//            graphicOverlay,
//            /* originalCameraImage= */ bitmap,
//            /* shouldShowFps= */ true, frameStartMs)
//            // When the image is from CameraX analysis use case, must call image.close() on received
//            // images when finished using them. Otherwise, new images may not be received or the camera
//            // may stall.
//            .addOnCompleteListener(results -> image.close());
//  }
//
//
//
//  // -----------------Common processing logic-------------------------------------------------------
//  private Task<T> requestDetectInImage(
//          final InputImage image,
//          final GraphicOverlay graphicOverlay,
//          @Nullable final Bitmap originalCameraImage,
//          boolean shouldShowFps, long frameStartMs) {
//    return setUpListener(
//            (Task<T>) detectInImage(image), graphicOverlay, originalCameraImage, shouldShowFps, frameStartMs);
//  }
//
//  private Task<T> requestDetectInImage(
//          final MlImage image,
//          final GraphicOverlay graphicOverlay,
//          @Nullable final Bitmap originalCameraImage,
//          boolean shouldShowFps,
//          long frameStartMs) {
//    return setUpListener(
//            (Task<T>) detectInImage(image), graphicOverlay, originalCameraImage, shouldShowFps, frameStartMs);
//  }
//  private Task<T> setUpListener(
//          Task<T> task,
//          final GraphicOverlay graphicOverlay,
//          @Nullable final Bitmap originalCameraImage,
//          boolean shouldShowFps,
//          long frameStartMs) {
//    final long detectorStartMs = SystemClock.elapsedRealtime();
//
//
//    final long startMs = SystemClock.elapsedRealtime();
// //   return detectInImage(image)
//    return task.addOnSuccessListener(
//                    executor,
//                    results -> {
//                      long currentLatencyMs = SystemClock.elapsedRealtime() - startMs;
//                      numRuns++;
//                      //frame +=1;
//                      //Log.d(TAG, "count number of frame:" + frame);
//                      frameProcessedInOneSecondInterval++;
//                      totalRunMs += currentLatencyMs;
//                      maxRunMs = Math.max(currentLatencyMs, maxRunMs);
//                      minRunMs = Math.min(currentLatencyMs, minRunMs);
//                      // audio(frame);
//                      // Only log inference info once per second. When frameProcessedInOneSecondInterval is
//                      // equal to 1, it means this is the first frame processed during the current second.
//                      if (frameProcessedInOneSecondInterval == 1) {
//                        //Log.d(TAG, "Max latency is: " + maxRunMs);
//                        //Log.d(TAG, "Min latency is: " + minRunMs);
////                Log.d(TAG, "count number of frame:" + frame);
//
//                        Log.d(TAG, "Num of Runs: " + numRuns + ", Avg latency is: " + totalRunMs / numRuns);
//                        MemoryInfo mi = new MemoryInfo();
//                        activityManager.getMemoryInfo(mi);
//                        long availableMegs = mi.availMem / 0x100000L;
//                        Log.d(TAG, "Memory available in system: " + availableMegs + " MB");
//                      }
//
//
//                      // Only log inference info once per second. When frameProcessedInOneSecondInterval is
//                      // equal to 1, it means this is the first frame processed during the current second.
////                      if (frameProcessedInOneSecondInterval == 1) {
////                        Log.d(TAG, "Num of Runs: " + numRuns);
////                        Log.d(
////                                TAG,
////                                "Frame latency: max="
////                                        + maxFrameMs
////                                        + ", min="
////                                        + minFrameMs
////                                        + ", avg="
////                                        + totalFrameMs / numRuns);
////                        Log.d(
////                                TAG,
////                                "Detector latency: max="
////                                        + maxDetectorMs
////                                        + ", min="
////                                        + minDetectorMs
////                                        + ", avg="
////                                        + totalDetectorMs / numRuns);
////                        MemoryInfo mi = new MemoryInfo();
////                        activityManager.getMemoryInfo(mi);
////                        long availableMegs = mi.availMem / 0x100000L;
////                        Log.d(TAG, "Memory available in system: " + availableMegs + " MB");
////                        temperatureMonitor.logTemperature();
////                      }
//
//
//
//
//                      graphicOverlay.clear();
//                      if (originalCameraImage != null) {
//                        graphicOverlay.add(new CameraImageGraphic(graphicOverlay, originalCameraImage));
//                      }
//                      graphicOverlay.add(
//                              new InferenceInfoGraphic(
//                                      graphicOverlay, currentLatencyMs, shouldShowFps ? framesPerSecond : null));
//                      VisionProcessorBase.this.onSuccess(results, graphicOverlay);
//                      graphicOverlay.postInvalidate();
//                    })
//            .addOnFailureListener(
//                    executor,
//                    e -> {
//                      graphicOverlay.clear();
//                      graphicOverlay.postInvalidate();
//                      String error = "Failed to process. Error: " + e.getLocalizedMessage();
//                      Toast.makeText(
//                                      graphicOverlay.getContext(),
//                                      error + "\nCause: " + e.getCause(),
//                                      Toast.LENGTH_SHORT)
//                              .show();
//                      Log.d(TAG, error);
//                      e.printStackTrace();
//                      VisionProcessorBase.this.onFailure(e);
//                    });
//
//
//  }
//
//  @Override
//  public void stop() {
//    executor.shutdown();
//    isShutdown = true;
//    numRuns = 0;
//
//    totalRunMs = 0;
//    fpsTimer.cancel();
//  }
//
//  protected abstract void onSuccess(
//          @NonNull PoseDetectorProcessor.PoseWithClassification poseWithClassification,
//          @NonNull GraphicOverlay graphicOverlay);
//
//  protected abstract Task<Pose> detectInImage(InputImage image);
//
//  protected Task<PoseDetectorProcessor.PoseWithClassification> detectInImage(MlImage image) {
//    return Tasks.forException(
//            new MlKitException(
//                    "MlImage is currently not demonstrated for this feature",
//                    MlKitException.INVALID_ARGUMENT));
//  }
//
//
//  protected abstract void onSuccess(@NonNull T results, @NonNull GraphicOverlay graphicOverlay);
//
//  protected abstract void onFailure(@NonNull Exception e);
//  protected boolean isMlImageEnabled(Context context) {
//    return false;
//
//}



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

package com.google.mlkit.vision.demo.java;

import static com.google.mlkit.vision.demo.GraphicOverlay.Graphic.getApplicationContext;

import android.app.ActivityManager;
import android.app.ActivityManager.MemoryInfo;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.MediaPlayer;
import android.os.Build.VERSION_CODES;
import android.os.SystemClock;
import android.util.Log;
import android.widget.Toast;
import androidx.annotation.GuardedBy;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.camera.core.ExperimentalGetImage;
import androidx.camera.core.ImageProxy;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskExecutors;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.demo.BitmapUtils;
import com.google.mlkit.vision.demo.CameraImageGraphic;
import com.google.mlkit.vision.demo.FrameMetadata;
import com.google.mlkit.vision.demo.GraphicOverlay;
import com.google.mlkit.vision.demo.InferenceInfoGraphic;
import com.google.mlkit.vision.demo.R;
import com.google.mlkit.vision.demo.ScopedExecutor;
import com.google.mlkit.vision.demo.VisionImageProcessor;
import com.google.mlkit.vision.demo.preference.PreferenceUtils;
import com.google.mlkit.vision.pose.Pose;

import java.nio.ByteBuffer;
import java.util.Timer;
import java.util.TimerTask;

import kotlin.jvm.Synchronized;

/**
 * Abstract base class for vision frame processors. Subclasses need to implement {@link
 * #onSuccess(Object, GraphicOverlay)} to define what they want to with the detection results and
 * {@link #detectInImage(InputImage)} to specify the detector object.
 *
 * @param <T> The type of the detected feature.
 */
public abstract class VisionProcessorBase<T> implements VisionImageProcessor {

  protected static final String MANUAL_TESTING_LOG = "LogTagForTest";
  private static final String TAG = "VisionProcessorBase";
  private static Boolean IsAudioRunning = false;
  private final ActivityManager activityManager;
  private final Timer fpsTimer = new Timer();
  private final ScopedExecutor executor;
  private static MediaPlayer mediaPlayer = null;

  // Whether this processor is already shut down
  private boolean isShutdown;

  // Used to calculate latency, running in the same thread, no sync needed.
  private int numRuns = 0;
  private long totalRunMs = 0;
  private long maxRunMs = 0;
  private long minRunMs = Long.MAX_VALUE;

  // Frame count that have been processed so far in an one second interval to calculate FPS.
  private int frameProcessedInOneSecondInterval = 0;
  private int framesPerSecond = 0;
  public static Integer frame=0;
  // To keep the latest images and its metadata.
  @GuardedBy("this")
  private ByteBuffer latestImage;

  @GuardedBy("this")
  private FrameMetadata latestImageMetaData;
  // To keep the images and metadata in process.
  @GuardedBy("this")
  private ByteBuffer processingImage;

  @GuardedBy("this")
  private FrameMetadata processingMetaData;

  protected VisionProcessorBase(Context context) {
    activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
    executor = new ScopedExecutor(TaskExecutors.MAIN_THREAD);
    fpsTimer.scheduleAtFixedRate(
            new TimerTask() {
              @Override
              public void run() {
                framesPerSecond = frameProcessedInOneSecondInterval;
                frameProcessedInOneSecondInterval = 0;
              }
            },
            /* delay= */ 0,
            /* period= */ 1000);
  }

  public  static void AudioStop()
  {
    try {
      if (mediaPlayer.isPlaying() == true)
        mediaPlayer.stop();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  public static void AudioforError( Integer type) {
    Thread thread = new Thread() {
      @Override
      public void run() {
        try {
          Integer AudioFileName = 0;
          if (type == 1)
            AudioFileName = R.raw.plank_error;
          else if (type == 2)
            AudioFileName = R.raw.hip_error;
          else if (type == 3)
            AudioFileName = R.raw.staff_pose_nose;
          else if(type == 4)
            AudioFileName = R.raw.wrong;
          else if(type==5)
            AudioFileName=R.raw.squats_back_error;
          else if(type==6)
            AudioFileName = R.raw.squats_knee_error;
          else if(type==7)
             AudioFileName= R.raw.start;
          else  if(type==8)
            AudioFileName = R.raw.quit;
          else if(type == 9)
            AudioFileName = R.raw.repeat_mistake;
          else if(type==10)
            AudioFileName= R.raw.leftknee;
          else if(type==11)
            AudioFileName= R.raw.rightknee;


          if (mediaPlayer == null || mediaPlayer.isPlaying() == false) {
            if (frame == 0 || frame >= 100 ) {
              frame = 1;
              mediaPlayer = MediaPlayer.create(getApplicationContext(), AudioFileName);
              mediaPlayer.start();
            }
            else
            {
              frame += 1;
            }
          }
        } catch (Exception e) {
          e.printStackTrace();
          IsAudioRunning = false;
        }
      }
    };
    thread.start();
  }

  // -----------------Code for processing single still image----------------------------------------
  @Override
  public void processBitmap(Bitmap bitmap, final GraphicOverlay graphicOverlay) {
    requestDetectInImage(
            InputImage.fromBitmap(bitmap, 0),
            graphicOverlay,
            /* originalCameraImage= */ null,
            /* shouldShowFps= */ false);
//      frame +=1;
//      Log.e("ppp", String.valueOf(frame));

  }

  // -----------------Code for processing live preview frame from Camera1 API-----------------------
  @Override
  public synchronized void processByteBuffer(
          ByteBuffer data, final FrameMetadata frameMetadata, final GraphicOverlay graphicOverlay) {
    latestImage = data;
    latestImageMetaData = frameMetadata;
    if (processingImage == null && processingMetaData == null) {
      processLatestImage(graphicOverlay);
    }
  }

  private synchronized void processLatestImage(final GraphicOverlay graphicOverlay) {
    processingImage = latestImage;
    processingMetaData = latestImageMetaData;
    latestImage = null;
    latestImageMetaData = null;
    if (processingImage != null && processingMetaData != null && !isShutdown) {
      processImage(processingImage, processingMetaData, graphicOverlay);
    }
  }

  private void processImage(
          ByteBuffer data, final FrameMetadata frameMetadata, final GraphicOverlay graphicOverlay) {
    // If live viewport is on (that is the underneath surface view takes care of the camera preview
    // drawing), skip the unnecessary bitmap creation that used for the manual preview drawing.
    Bitmap bitmap =
            PreferenceUtils.isCameraLiveViewportEnabled(graphicOverlay.getContext())
                    ? null
                    : BitmapUtils.getBitmap(data, frameMetadata);

    requestDetectInImage(
            InputImage.fromByteBuffer(
                    data,
                    frameMetadata.getWidth(),
                    frameMetadata.getHeight(),
                    frameMetadata.getRotation(),
                    InputImage.IMAGE_FORMAT_NV21),
            graphicOverlay,
            bitmap,
            /* shouldShowFps= */ true)
            .addOnSuccessListener(executor, results -> processLatestImage(graphicOverlay));

  }

  // -----------------Code for processing live preview frame from CameraX API-----------------------
  @Override
  @RequiresApi(VERSION_CODES.KITKAT)
  @ExperimentalGetImage
  public void processImageProxy(ImageProxy image, GraphicOverlay graphicOverlay) {
    if (isShutdown) {
      image.close();
      return;
    }

    Bitmap bitmap = null;
    if (!PreferenceUtils.isCameraLiveViewportEnabled(graphicOverlay.getContext())) {
      bitmap = BitmapUtils.getBitmap(image);

    }

    requestDetectInImage(
            InputImage.fromMediaImage(image.getImage(), image.getImageInfo().getRotationDegrees()),
            graphicOverlay,
            /* originalCameraImage= */ bitmap,
            /* shouldShowFps= */ true)
            // When the image is from CameraX analysis use case, must call image.close() on received
            // images when finished using them. Otherwise, new images may not be received or the camera
            // may stall.
            .addOnCompleteListener(results -> image.close());
  }



  // -----------------Common processing logic-------------------------------------------------------
  public Task<T> requestDetectInImage(
          final InputImage image,
          final GraphicOverlay graphicOverlay,
          @Nullable final Bitmap originalCameraImage,
          boolean shouldShowFps) {
    final long startMs = SystemClock.elapsedRealtime();
    return detectInImage(image)
            .addOnSuccessListener(
                    executor,
                    results -> {
                      long currentLatencyMs = SystemClock.elapsedRealtime() - startMs;
                      numRuns++;
                      frame +=1;
                      //Log.d(TAG, "count number of frame:" + frame);
                      frameProcessedInOneSecondInterval++;
                      totalRunMs += currentLatencyMs;
                      maxRunMs = Math.max(currentLatencyMs, maxRunMs);
                      minRunMs = Math.min(currentLatencyMs, minRunMs);
                      // audio(frame);
                      // Only log inference info once per second. When frameProcessedInOneSecondInterval is
                      // equal to 1, it means this is the first frame processed during the current second.
                      if (frameProcessedInOneSecondInterval == 1) {
                        //Log.d(TAG, "Max latency is: " + maxRunMs);
                        //Log.d(TAG, "Min latency is: " + minRunMs);
                        // Log.d(TAG, "count number of frame:" + frame);

                        Log.d(TAG, "Num of Runs: " + numRuns + ", Avg latency is: " + totalRunMs / numRuns);
                        MemoryInfo mi = new MemoryInfo();
                        activityManager.getMemoryInfo(mi);
                        long availableMegs = mi.availMem / 0x100000L;
                       // Log.d(TAG, "Memory available in system: " + availableMegs + " MB");
                      }

                      graphicOverlay.clear();
                      if (originalCameraImage != null) {
                        graphicOverlay.add(new CameraImageGraphic(graphicOverlay, originalCameraImage));
                      }
                      graphicOverlay.add(
                              new InferenceInfoGraphic(
                                      graphicOverlay, currentLatencyMs, shouldShowFps ? framesPerSecond : null));
                      VisionProcessorBase.this.onSuccess(results, graphicOverlay);
                      graphicOverlay.postInvalidate();
                    })
            .addOnFailureListener(
                    executor,
                    e -> {
                      graphicOverlay.clear();
                      graphicOverlay.postInvalidate();
                      String error = "Failed to process. Error: " + e.getLocalizedMessage();
                      Toast.makeText(
                                      graphicOverlay.getContext(),
                                      error + "\nCause: " + e.getCause(),
                                      Toast.LENGTH_SHORT)
                              .show();
                      Log.d(TAG, error);
                      e.printStackTrace();
                      VisionProcessorBase.this.onFailure(e);
                    });


  }

  @Override
  public void stop() {
    executor.shutdown();
    isShutdown = true;
    numRuns = 0;

    totalRunMs = 0;
    fpsTimer.cancel();
  }

  protected abstract Task<T> detectInImage(InputImage image);

  protected abstract void onSuccess(@NonNull T results, @NonNull GraphicOverlay graphicOverlay);

  protected abstract void  onSuccess1(@NonNull Pose pose, @NonNull GraphicOverlay graphicOverlay);

  protected abstract void onFailure(@NonNull Exception e);

}