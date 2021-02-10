package org.firstinspires.ftc.robotcontroller.external.samples;

import android.app.Activity;
import android.graphics.Color;
import android.view.View;

import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.hardware.DistanceSensor;
import com.qualcomm.robotcore.hardware.NormalizedColorSensor;
import com.qualcomm.robotcore.hardware.NormalizedRGBA;
import com.qualcomm.robotcore.hardware.SwitchableLight;

import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;


@Autonomous(name = "Sensor: Color", group = "Sensor")
@Disabled
public class NewColorSensorCode extends LinearOpMode {

  /** The colorSensor field will contain a reference to our color sensor hardware object */
  NormalizedColorSensor colorSensor;
  private DcMotor frontLeftMotor;
  private DcMotor frontRightMotor;
  private DcMotor backLeftMotor;
  private DcMotor backRightMotor;
  private Servo servo;
  private static final String TFOD_MODEL_ASSET = "UltimateGoal.tflite";
  private static final String LABEL_FIRST_ELEMENT = "Quad";
  private static final String LABEL_SECOND_ELEMENT = "Single";
  private char targetZone = 'A';

  private static final String VUFORIA_KEY =
          "Ae/YeOf/////AAABmR8KMKVXi0gFg1/JtSBMj5WHZwOHCMtdvkRRmVdKQcjYBCk/JBHyLtxgccLh2ZJezNZ2W/ZU6mi38O6dsGABJtKELx/nxVc78up34+6k21SQSPKu8qgK9RuK5deUYb9K9gk8QG9xuGvGD5xQpH+nxeywwwQQXmExoEeLvlp6+H5Qa90lDZZPs2llKVqvdmuA8TSpGEktHgLcH0L4QtnF1JM1e7GY6woBW3aktTjXtqjK9mtvgbTRuBceBeLUuy7nhrT2+qt7aPzSAWsMgvrdduScWpYl14bQESUVEWX6Dz8xcNHOsDVnPB593nqj2KVVBbcHno8NATIGDvERkE2d4SUa5IRECzJ+nWbI9Fcx3zdZ";
  private VuforiaLocalizer vuforia;
  private TFObjectDetector tfod;
  View relativeLayout;
  //Movement methods
  public void MoveForward(){
    frontLeftMotor.setPower(0.3);
    frontRightMotor.setPower(0.3);
    backLeftMotor.setPower(0.3);
    backRightMotor.setPower(0.3);
  }
  public void MoveBackward(){
    frontLeftMotor.setPower(-0.3);
    frontRightMotor.setPower(-0.3);
    backLeftMotor.setPower(-0.3);
    backRightMotor.setPower(-0.3);
  }
  public void TurnRight(long time){
    frontLeftMotor.setPower(1.0);
    frontRightMotor.setPower(-1.0);
    backLeftMotor.setPower(1.0);
    backRightMotor.setPower(-1.0);
    sleep(time);
  }
  public void TurnLeft(long time){
    frontLeftMotor.setPower(-1.0);
    frontRightMotor.setPower(1.0);
    backLeftMotor.setPower(-1.0);
    backRightMotor.setPower(1.0);
    sleep(time);
  }
  public void Stop(){
    frontLeftMotor.setPower(0);
    frontRightMotor.setPower(0);
    backLeftMotor.setPower(0);
    backRightMotor.setPower(0);
  }

  private void initVuforia() {

    VuforiaLocalizer.Parameters parameters = new VuforiaLocalizer.Parameters();

    parameters.vuforiaLicenseKey = VUFORIA_KEY;
    parameters.cameraName = hardwareMap.get(WebcamName.class, "TensorflowWebcam");

    //  Instantiate the Vuforia engine
    vuforia = ClassFactory.getInstance().createVuforia(parameters);

    // Loading trackables is not necessary for the TensorFlow Object Detection engine.
  }


  private void initTfod() {
    int tfodMonitorViewId = hardwareMap.appContext.getResources().getIdentifier(
            "tfodMonitorViewId", "id", hardwareMap.appContext.getPackageName());
    TFObjectDetector.Parameters tfodParameters = new TFObjectDetector.Parameters(tfodMonitorViewId);
    tfodParameters.minimumConfidence = 0.8;
    tfod = ClassFactory.getInstance().createTFObjectDetector(tfodParameters, vuforia);
    tfod.loadModelFromAsset(TFOD_MODEL_ASSET, LABEL_FIRST_ELEMENT, LABEL_SECOND_ELEMENT);
  }


  //This method will be called whenever robot needs to move forward and stop once it detects blue
  //Will modify this later for all colors
  public void SenseColor(char color){

    //Sense blue color(for Target Zones)

    if(color == 'B'){
      while (true) {
        //Print rgb values on telemetry(for testing purposes only)
        telemetry.addData("Blue:", colorSensor.blue());
        telemetry.addData("Red:", colorSensor.red());
        telemetry.addData("Green:", colorSensor.green());
        telemetry.update();

        MoveForward();

        if (colorSensor.blue() > colorSensor.red() && colorSensor.blue() > colorSensor.green()) {
          Stop();
          break;
        }
        else {
          continue;
        }
      }
    }
    //Sense white color(for Launch Line)

    if(color == 'W'){
      while (true) {
        //Print rgb values on telemetry(for testing purposes only)
        telemetry.addData("Blue:", colorSensor.blue());
        telemetry.addData("Red:", colorSensor.red());
        telemetry.addData("Green:", colorSensor.green());
        telemetry.update();

        MoveForward();
        //According to our tests, colorSensor will detect more of green than other colors when it sees white
        if (colorSensor.green() > 5000) {
          Stop();
          break;
        }
        else {
          continue;
        }
      }
    }

  }

  public void PlaceWobbleGoal(){
    servo.setPosition(90);
    MoveBackward();
    sleep(1000);
    servo.setPosition(0);
  }

  @Override public void runOpMode() {

    // Get a reference to the RelativeLayout so we can later change the background
    // color of the Robot Controller app to match the hue detected by the RGB sensor.
    int relativeLayoutId = hardwareMap.appContext.getResources().getIdentifier("RelativeLayout", "id", hardwareMap.appContext.getPackageName());
    relativeLayout = ((Activity) hardwareMap.appContext).findViewById(relativeLayoutId);

    try {
      runSample(); // actually execute the sample
    } finally {
      // On the way out, *guarantee* that the background is reasonable. It doesn't actually start off
      // as pure white, but it's too much work to dig out what actually was used, and this is good
      // enough to at least make the screen reasonable again.
      // Set the panel back to the default color
      relativeLayout.post(new Runnable() {
        public void run() {
          relativeLayout.setBackgroundColor(Color.WHITE);
        }
      });
    }
  }

  protected void runSample() {
    // You can give the sensor a gain value, will be multiplied by the sensor's raw value before the
    // normalized color values are calculated. Color sensors (especially the REV Color Sensor V3)
    // can give very low values (depending on the lighting conditions), which only use a small part
    // of the 0-1 range that is available for the red, green, and blue values. In brighter conditions,
    // you should use a smaller gain than in dark conditions. If your gain is too high, all of the
    // colors will report at or near 1, and you won't be able to determine what color you are
    // actually looking at. For this reason, it's better to err on the side of a lower gain
    // (but always greater than  or equal to 1).
    float gain = 2;

    // Once per loop, we will update this hsvValues array. The first element (0) will contain the
    // hue, the second element (1) will contain the saturation, and the third element (2) will
    // contain the value. See http://web.archive.org/web/20190311170843/https://infohost.nmt.edu/tcc/help/pubs/colortheory/web/hsv.html
    // for an explanation of HSV color.
    final float[] hsvValues = new float[3];

    // xButtonPreviouslyPressed and xButtonCurrentlyPressed keep track of the previous and current
    // state of the X button on the gamepad
    boolean xButtonPreviouslyPressed = false;
    boolean xButtonCurrentlyPressed = false;

    // Get a reference to our sensor object. It's recommended to use NormalizedColorSensor over
    // ColorSensor, because NormalizedColorSensor consistently gives values between 0 and 1, while
    // the values you get from ColorSensor are dependent on the specific sensor you're using.
    colorSensor = hardwareMap.get(NormalizedColorSensor.class, "CS1");
    backRightMotor = hardwareMap.get(DcMotor.class, "br");
    backLeftMotor = hardwareMap.get(DcMotor.class, "bl");
    frontRightMotor = hardwareMap.get(DcMotor.class, "fr");
    frontLeftMotor = hardwareMap.get(DcMotor.class, "fl");
    servo = hardwareMap.get(Servo.class, "Flicker");
    servo.setPosition(0)

    // If possible, turn the light on in the beginning (it might already be on anyway,
    // we just make sure it is if we can).
    if (colorSensor instanceof SwitchableLight) {
      ((SwitchableLight)colorSensor).enableLight(true);
    }

    // Wait for the start button to be pressed.
    waitForStart();

    // Loop until we are asked to stop
    while (opModeIsActive()) {
      // Explain basic gain information via telemetry
      telemetry.addLine("Hold the A button on gamepad 1 to increase gain, or B to decrease it.\n");
      telemetry.addLine("Higher gain values mean that the sensor will report larger numbers for Red, Green, and Blue, and Value\n");

      // Update the gain value if either of the A or B gamepad buttons is being held
      if (gamepad1.a) {
        // Only increase the gain by a small amount, since this loop will occur multiple times per second.
        gain += 0.005;
      } else if (gamepad1.b && gain > 1) { // A gain of less than 1 will make the values smaller, which is not helpful.
        gain -= 0.005;
      }

      // Show the gain value via telemetry
      telemetry.addData("Gain", gain);

      // Tell the sensor our desired gain value (normally you would do this during initialization,
      // not during the loop)
      colorSensor.setGain(gain);

      // Check the status of the X button on the gamepad
      xButtonCurrentlyPressed = gamepad1.x;

      // If the button state is different than what it was, then act
      if (xButtonCurrentlyPressed != xButtonPreviouslyPressed) {
        // If the button is (now) down, then toggle the light
        if (xButtonCurrentlyPressed) {
          if (colorSensor instanceof SwitchableLight) {
            SwitchableLight light = (SwitchableLight)colorSensor;
            light.enableLight(!light.isLightOn());
          }
        }
      }
      xButtonPreviouslyPressed = xButtonCurrentlyPressed;

      // Get the normalized colors from the sensor
      NormalizedRGBA colors = colorSensor.getNormalizedColors();

      /* Use telemetry to display feedback on the driver station. We show the red, green, and blue
       * normalized values from the sensor (in the range of 0 to 1), as well as the equivalent
       * HSV (hue, saturation and value) values. See http://web.archive.org/web/20190311170843/https://infohost.nmt.edu/tcc/help/pubs/colortheory/web/hsv.html
       * for an explanation of HSV color. */

      // Update the hsvValues array by passing it to Color.colorToHSV()
      Color.colorToHSV(colors.toColor(), hsvValues);

      telemetry.addLine()
              .addData("Red", "%.3f", colors.red)
              .addData("Green", "%.3f", colors.green)
              .addData("Blue", "%.3f", colors.blue);
      telemetry.addLine()
              .addData("Hue", "%.3f", hsvValues[0])
              .addData("Saturation", "%.3f", hsvValues[1])
              .addData("Value", "%.3f", hsvValues[2]);
      telemetry.addData("Alpha", "%.3f", colors.alpha);

      /* If this color sensor also has a distance sensor, display the measured distance.
       * Note that the reported distance is only useful at very close range, and is impacted by
       * ambient light and surface reflectivity. */
      if (colorSensor instanceof DistanceSensor) {
        telemetry.addData("Distance (cm)", "%.3f", ((DistanceSensor) colorSensor).getDistance(DistanceUnit.CM));
      }

      telemetry.update();

      // Change the Robot Controller's background color to match the color detected by the color sensor.
      relativeLayout.post(new Runnable() {
        public void run() {
          relativeLayout.setBackgroundColor(Color.HSVToColor(hsvValues));
        }
      });
      backLeftMotor.setDirection(DcMotor.Direction.REVERSE);
      frontLeftMotor.setDirection(DcMotor.Direction.REVERSE);
      initVuforia();
      initTfod();
      if (tfod != null) {
        tfod.activate();
        tfod.setZoom(2.5, 16.0/9.0);
      }
      telemetry.addData(">", "Ready to Start!");
      telemetry.update();
      waitForStart();



      if (tfod != null) {
        List<Recognition> updatedRecognitions = tfod.getUpdatedRecognitions();
        if (updatedRecognitions != null) {
          telemetry.addData("# Object Detected", updatedRecognitions.size());
          int i = 0;
          for (Recognition recognition : updatedRecognitions) {
            telemetry.addData(String.format("label (%d)", i), recognition.getLabel());
            telemetry.addData(String.format("  left,top (%d)", i), "%.03f , %.03f",
                    recognition.getLeft(), recognition.getTop());
            telemetry.addData(String.format("  right,bottom (%d)", i), "%.03f , %.03f",
                    recognition.getRight(), recognition.getBottom());
            if(recognition.getLabel().equals("Quad")){
              targetZone = 'C';
            }
            if(recognition.getLabel().equals("Single")){
              targetZone = 'B';
            }
          }
          if(updatedRecognitions.size() == 0){
            telemetry.addData(">", "Target Zone A");
            telemetry.update();
          }

        }
      }


      if (tfod != null) {
        tfod.shutdown();
      }








      SenseColor('W');


      //Target Zone A
      if(targetZone == 'A') {
        //Move to target zone and place wobble goal
        TurnLeft(500);
        SenseColor('B');
        PlaceWobbleGoal();

        //Move back to Launch Line
        MoveBackward();
        sleep(1000);
        TurnRight(500);
        MoveBackward();
        sleep(500);



      }
      //Target Zone B
      if(targetZone == 'B'){
        //Move to target zone and place wobble goal
        SenseColor('B');
        PlaceWobbleGoal();
        //Move back to launch line
        MoveBackward();
        sleep(1000);


      }
      //Target Zone C
      if(targetZone == 'C'){
        //Move through Target Zone B
        for(int i = 1; i <= 2; i++){
          SenseColor('B');
          MoveForward();
          sleep(1000);

        }
        //Move to Target Zone C
        TurnLeft(500);
        SenseColor('B');
        //Place wobble goal
        PlaceWobbleGoal();

        //Go back to Launch Line
        MoveBackward();
        sleep(500);
        TurnLeft(500);
        for(int j = 1; j <= 2; j++){
          SenseColor('B');
          MoveForward();
          sleep(1000);
          //buoy
        }
      }
    }
  }
}
