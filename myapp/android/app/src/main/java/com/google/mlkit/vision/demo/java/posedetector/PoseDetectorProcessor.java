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

import android.content.Context;
import androidx.annotation.NonNull;
import android.util.Log;
import com.google.android.gms.tasks.Task;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.demo.GraphicOverlay;
import com.google.mlkit.vision.demo.java.VisionProcessorBase;
//import com.google.mlkit.vision.demo.java.posedetector.classification.PoseClassifierProcessor;
import com.google.mlkit.vision.pose.Pose;
import com.google.mlkit.vision.pose.PoseDetection;
import com.google.mlkit.vision.pose.PoseDetector;
import com.google.mlkit.vision.pose.PoseDetectorOptionsBase;

import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/** A processor to run pose detector. */
public abstract class  PoseDetectorProcessor extends VisionProcessorBase<Pose> implements PushUpsCountInterface {

  private static final String TAG = "PoseDetectorProcessor";

  private final PoseDetector detector;

  private final boolean showInFrameLikelihood;
  int pushpusCount=0;
//  private final boolean visualizeZ;
//  private final boolean rescaleZForVisualization;
//  private final boolean runClassification;
//  private final boolean isStreamMode;
  private final Context context;
  private final Executor classificationExecutor;
  //private PoseClassifierProcessor poseClassifierProcessor;

  /** Internal class to hold Pose and classification results. */
  public static class PoseWithClassification {
    private final Pose pose;
    private final List<String> classificationResult;

    public PoseWithClassification(Pose pose, List<String> classificationResult) {
      this.pose = pose;
      this.classificationResult = classificationResult;
    }

    public Pose getPose() {
      return pose;
    }

    public List<String> getClassificationResult() {
      return classificationResult;
    }



  }
  public PoseDetectorProcessor(
          Context context, PoseDetectorOptionsBase options, boolean showInFrameLikelihood, boolean shouldShowInFrameLikelihood, boolean b) {
    super(context);
    this.showInFrameLikelihood = showInFrameLikelihood;
    detector = PoseDetection.getClient(options);
    this.context = context;
    classificationExecutor = Executors.newSingleThreadExecutor();
  }

  @Override
  public void stop() {
    super.stop();
    detector.close();
  }


  @Override
  protected Task<Pose> detectInImage(InputImage image) {
    return detector.process(image);
  }



  @Override
  protected void onSuccess(@NonNull Pose pose, @NonNull GraphicOverlay graphicOverlay) {
//    graphicOverlay.add(new PoseGraphic(graphicOverlay,pose, showInFrameLikelihood));

    graphicOverlay.add(new PoseGraphicForPushUp(graphicOverlay,pose, showInFrameLikelihood));
    Log.d(TAG, "onSuccess: ");

  }





























































































































































































































































































































































































































































  @Override
  protected void onFailure(@NonNull Exception e) {
    Log.e(TAG, "Pose detection failed!", e);
  }





  @Override
  public void pushUpsCountInterface(int count) {

    pushpusCount +=0.5;
    Log.d(TAG, "pushUpsCount: "+pushpusCount);
  }
}
