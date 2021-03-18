package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import org.firstinspires.ftc.robotcore.external.ClassFactory;
import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import org.firstinspires.ftc.robotcore.external.matrices.OpenGLMatrix;
import org.firstinspires.ftc.robotcore.external.matrices.VectorF;
import org.firstinspires.ftc.robotcore.external.navigation.Orientation;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaLocalizer;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaTrackable;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaTrackableDefaultListener;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaTrackables;
import org.firstinspires.ftc.robotcore.external.navigation.Position

import java.util.ArrayList;
import java.util.List;

import static org.firstinspires.ftc.robotcore.external.navigation.AngleUnit.DEGREES;
import static org.firstinspires.ftc.robotcore.external.navigation.AxesOrder.XYZ;
import static org.firstinspires.ftc.robotcore.external.navigation.AxesOrder.YZX;
import static org.firstinspires.ftc.robotcore.external.navigation.AxesReference.EXTRINSIC;
import static org.firstinspires.ftc.robotcore.external.navigation.VuforiaLocalizer.CameraDirection.BACK;
@Autonomous(name = "WGP with shooting", group = "WobbleGoal")

public class PowerShotShooting extends LinearOpMode{
    //Hardware objects
    private ColorSensor CSensor;
    private DcMotor frontLeftMotor;
    private DcMotor frontRightMotor;
    private DcMotor backLeftMotor;
    private DcMotor backRightMotor;
    private DcMotor shooterLeftMotor;
    private DcMotor shooterRightMotor;
    private DcMotor Arm;
    private Servo Flicker;
    private Servo ArmTurn;
    private Servo ArmOpen;

    //private Servo servo;

    //TFOD Variables
    private static final String TFOD_MODEL_ASSET = "UltimateGoal.tflite";
    private static final String LABEL_FIRST_ELEMENT = "Quad";
    private static final String LABEL_SECOND_ELEMENT = "Single";
    private char targetZone;
    private VuforiaLocalizer vuforia;
    private TFObjectDetector tfod;


    //VUFORIA KEY
    private static final String VUFORIA_KEY =
            "Ae/YeOf/////AAABmR8KMKVXi0gFg1/JtSBMj5WHZwOHCMtdvkRRmVdKQcjYBCk/JBHyLtxgccLh2ZJezNZ2W/ZU6mi38O6dsGABJtKELx/nxVc78up34+6k21SQSPKu8qgK9RuK5deUYb9K9gk8QG9xuGvGD5xQpH+nxeywwwQQXmExoEeLvlp6+H5Qa90lDZZPs2llKVqvdmuA8TSpGEktHgLcH0L4QtnF1JM1e7GY6woBW3aktTjXtqjK9mtvgbTRuBceBeLUuy7nhrT2+qt7aPzSAWsMgvrdduScWpYl14bQESUVEWX6Dz8xcNHOsDVnPB593nqj2KVVBbcHno8NATIGDvERkE2d4SUa5IRECzJ+nWbI9Fcx3zdZ";

    private static final float mmPerInch        = 25.4f;
    private static final float mmTargetHeight   = (6) * mmPerInch;          // the height of the center of the target image above the floor

    // Constants for perimeter targets
    private static final float halfField = 72 * mmPerInch;
    private static final float quadField  = 36 * mmPerInch;

    // Class Members
    private OpenGLMatrix lastLocation = null;
    private VuforiaLocalizer vuforia = null;

    /**
     * This is the webcam we are to use. As with other hardware devices such as motors and
     * servos, this device is identified using the robot configuration tool in the FTC application.
     */
    WebcamName webcamName = null;

    private boolean targetVisible = false;
    private float phoneXRotate    = 0;
    private float phoneYRotate    = 0;
    private float phoneZRotate    = 0;


    //Movement methods
    public void MoveForward(double speed){
        //Set frontLeft motor power level slightly higher than others
        //This is to offset drift in our robot's movement
        frontLeftMotor.setPower(speed + .03);
        frontRightMotor.setPower(speed);
        backLeftMotor.setPower(speed);
        backRightMotor.setPower(speed);
    }
    public void MoveBackward(double speed){
        //Set frontLeft motor power level slightly higher than others
        //This is to offset drift in our robot's movement
        frontLeftMotor.setPower(-speed - .03);
        frontRightMotor.setPower(-speed);
        backLeftMotor.setPower(-speed);
        backRightMotor.setPower(-speed);
    }
    public void TurnRight(long time){
        frontLeftMotor.setPower(0.5);
        frontRightMotor.setPower(-0.5);
        backLeftMotor.setPower(0.5);
        backRightMotor.setPower(-0.5);
        sleep(time);
    }
    public void TurnLeft(long time){
        frontLeftMotor.setPower(-0.5);
        frontRightMotor.setPower(0.5);
        backLeftMotor.setPower(-0.5);
        backRightMotor.setPower(0.5);
        sleep(time);
    }
    public void StrafeLeft(long time) {
        frontLeftMotor.setPower(-0.5);
        frontRightMotor.setPower(0.5);
        backLeftMotor.setPower(0.5);
        backRightMotor.setPower(-0.5);
        sleep(time);
    }
    public void StrafeRight(long time) {
        frontLeftMotor.setPower(0.5);
        frontRightMotor.setPower(-0.5);
        backLeftMotor.setPower(-0.5);
        backRightMotor.setPower(0.5);
        sleep(time);
    }
    public void STrafeRight() {
        frontLeftMotor.setPower(0.5);
        frontRightMotor.setPower(-0.5);
        backLeftMotor.setPower(-0.5);
        backRightMotor.setPower(0.5);

    }
    public void Shoot() {
        shooterLeftMotor.setPower(0.75);
        shooterRightMotor.setPower(0.75);
        sleep(1500);
        Flicker.setPosition(170);
        sleep(500);
        Flicker.setPosition(0);
    }
    public void ShootAndMove() {
        if(blueAllianceTarget.get(1) < 1785){
            STrafeRight();
        }
        MoveBackward(0.6);
        sleep(50);
        Shoot();
        StrafeRight(100);
        Shoot();
        StrafeRight(100);
        Shoot();
        sleep(100);
        MoveForward(100);
    }
    public void Stop(){
        frontLeftMotor.setPower(0);
        frontRightMotor.setPower(0);
        backLeftMotor.setPower(0);
        backRightMotor.setPower(0);
    }

    //For initializing Vuforia
    private void initVuforia() {

        VuforiaLocalizer.Parameters parameters = new VuforiaLocalizer.Parameters();

        parameters.vuforiaLicenseKey = VUFORIA_KEY;
        parameters.cameraName = hardwareMap.get(WebcamName.class, "TensorflowWebcam");
        parameters.cameraName = webcamName;

        // Make sure extended tracking is disabled for this example.
        parameters.useExtendedTracking = false;

        //  Instantiate the Vuforia engine
        vuforia = ClassFactory.getInstance().createVuforia(parameters);

        // Loading trackables is not necessary for the TensorFlow Object Detection engine.
    }

    //For initalizing TFOD
    private void initTfod() {
        int tfodMonitorViewId = hardwareMap.appContext.getResources().getIdentifier(
                "tfodMonitorViewId", "id", hardwareMap.appContext.getPackageName());
        TFObjectDetector.Parameters tfodParameters = new TFObjectDetector.Parameters(tfodMonitorViewId);
        tfodParameters.minimumConfidence = 0.8;
        tfod = ClassFactory.getInstance().createTFObjectDetector(tfodParameters, vuforia);
        tfod.loadModelFromAsset(TFOD_MODEL_ASSET, LABEL_FIRST_ELEMENT, LABEL_SECOND_ELEMENT);
    }


    public void SenseColor(char color, char direction){

        //Sense blue color(for Target Zones)
        if(color == 'B'){
            while (true) {

                if(direction == 'F'){
                    MoveForward(0.3);
                }
                if(direction == 'B'){
                    MoveBackward(0.3);
                }
                //If CSensor detects blue line(more blue than other colors)
                if (CSensor.blue() > CSensor.red() && CSensor.blue() > CSensor.green()) {
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

                if(direction == 'F'){
                    MoveForward(0.3);
                }
                if(direction == 'B') {
                    MoveBackward(0.3);
                }

                //If CSensor detects white(green value is always higher than 5000 when it detects white)
                if (CSensor.green() > 5000) {
                    Stop();
                    break;
                }
                else {
                    continue;
                }
            }
        }

    }

    @Override
    public void runOpMode(){
        //Initalize Hardware
        CSensor = hardwareMap.get(ColorSensor.class, "CS1");
        backRightMotor = hardwareMap.get(DcMotor.class, "br");
        backLeftMotor = hardwareMap.get(DcMotor.class, "bl");
        frontRightMotor = hardwareMap.get(DcMotor.class, "fr");
        frontLeftMotor = hardwareMap.get(DcMotor.class, "fl");
        shooterLeftMotor = hardwareMap.get(DcMotor.class, "sl");
        shooterRightMotor = hardwareMap.get(DcMotor.class, "sr");
        //ArmTurn= hardwareMap.get(DcMotor.class, "HookTurn");
        //ArmOpen= hardwareMap.get(DcMotor.class, "HookOpen");
        Arm = hardwareMap.get(DcMotor.class, "Intake");
        Flicker = hardwareMap.get(Servo.class, "Flicker");
        backLeftMotor.setDirection(DcMotor.Direction.REVERSE);
        frontLeftMotor.setDirection(DcMotor.Direction.REVERSE);
        shooterLeftMotor.setDirection(DcMotor.Direction.REVERSE);
        //Initialize Vuforia and TFOD
        initVuforia();
        initTfod();
        if (tfod != null) {
            tfod.activate();
            tfod.setZoom(2.5, 16.0/9.0);
        }


        // Load the data sets for the trackable objects. These particular data
        // sets are stored in the 'assets' part of our application.
            VuforiaTrackables targetsUltimateGoal = this.vuforia.loadTrackablesFromAsset("UltimateGoal");
        VuforiaTrackable blueTowerGoalTarget = targetsUltimateGoal.get(0);
        blueTowerGoalTarget.setName("Blue Tower Goal Target");
        VuforiaTrackable blueAllianceTarget = targetsUltimateGoal.get(3);
        blueAllianceTarget.setName("Blue Alliance Target");
        VuforiaTrackable frontWallTarget = targetsUltimateGoal.get(4);
        frontWallTarget.setName("Front Wall Target");


        blueAllianceTarget.setLocation(OpenGLMatrix
                .translation(0, halfField, mmTargetHeight)
                .multiplied(Orientation.getRotationMatrix(EXTRINSIC, XYZ, DEGREES, 90, 0, 0)));
        frontWallTarget.setLocation(OpenGLMatrix
                .translation(-halfField, 0, mmTargetHeight)
                .multiplied(Orientation.getRotationMatrix(EXTRINSIC, XYZ, DEGREES, 90, 0 , 90)));

        // The tower goal targets are located a quarter field length from the ends of the back perimeter wall.
        blueTowerGoalTarget.setLocation(OpenGLMatrix
                .translation(halfField, quadField, mmTargetHeight)
                .multiplied(Orientation.getRotationMatrix(EXTRINSIC, XYZ, DEGREES, 90, 0 , -90)));

        //
        // Create a transformation matrix describing where the phone is on the robot.
        //
        // NOTE !!!!  It's very important that you turn OFF your phone's Auto-Screen-Rotation option.
        // Lock it into Portrait for these numbers to work.
        //
        // Info:  The coordinate frame for the robot looks the same as the field.
        // The robot's "forward" direction is facing out along X axis, with the LEFT side facing out along the Y axis.
        // Z is UP on the robot.  This equates to a bearing angle of Zero degrees.
        //
        // The phone starts out lying flat, with the screen facing Up and with the physical top of the phone
        // pointing to the LEFT side of the Robot.
        // The two examples below assume that the camera is facing forward out the front of the robot.

        // We need to rotate the camera around it's long axis to bring the correct camera forward.

        // Next, translate the camera lens to where it is on the robot.
        // In this example, it is centered (left to right), but forward of the middle of the robot, and above ground level.
        final float CAMERA_FORWARD_DISPLACEMENT  = 4.0f * mmPerInch;   // eg: Camera is 4 Inches in front of robot-center
        final float CAMERA_VERTICAL_DISPLACEMENT = 8.0f * mmPerInch;   // eg: Camera is 8 Inches above ground
        final float CAMERA_LEFT_DISPLACEMENT     = 0;     // eg: Camera is ON the robot's center line

        OpenGLMatrix robotFromCamera = OpenGLMatrix
                .translation(CAMERA_FORWARD_DISPLACEMENT, CAMERA_LEFT_DISPLACEMENT, CAMERA_VERTICAL_DISPLACEMENT)
                .multiplied(Orientation.getRotationMatrix(EXTRINSIC, YZX, DEGREES, phoneYRotate, phoneZRotate, phoneXRotate));

        /**  Let all the trackable listeners know where the phone is.  */
        for (VuforiaTrackable trackable : allTrackables) {
            ((VuforiaTrackableDefaultListener) trackable.getListener()).setPhoneInformation(robotFromCamera, parameters.cameraDirection);
        }



        targetsUltimateGoal.activate();


        // check all the trackable targets to see which one (if any) is visible.
        targetVisible = false;
        for (VuforiaTrackable trackable : allTrackables) {
            if (((VuforiaTrackableDefaultListener)trackable.getListener()).isVisible()) {
                telemetry.addData("Visible Target", trackable.getName());
                targetVisible = true;

                // getUpdatedRobotLocation() will return null if no new information is available since
                // the last time that call was made, or if the trackable is not currently visible.
                OpenGLMatrix robotLocationTransform = ((VuforiaTrackableDefaultListener)trackable.getListener()).getUpdatedRobotLocation();
                if (robotLocationTransform != null) {
                    lastLocation = robotLocationTransform;
                }
                break;
            }
        }

        // Provide feedback as to where the robot is located (if we know).
        if (targetVisible) {
            // express position (translation) of robot in inches.
            VectorF translation = lastLocation.getTranslation();
            telemetry.addData("Pos (in)", "{X, Y, Z} = %.1f, %.1f, %.1f",
                    translation.get(0) / mmPerInch, translation.get(1) / mmPerInch, translation.get(2) / mmPerInch);

            // express the rotation of the robot in degrees.
            Orientation rotation = Orientation.getOrientation(lastLocation, EXTRINSIC, XYZ, DEGREES);
            telemetry.addData("Rot (deg)", "{Roll, Pitch, Heading} = %.0f, %.0f, %.0f", rotation.firstAngle, rotation.secondAngle, rotation.thirdAngle);
        }

        else {
            telemetry.addData("Visible Target", "none");
        }
        telemetry.update();


        telemetry.addData(">", "Ready to Start!");
        telemetry.update();

        waitForStart();


        // Detect rings using TFOD
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
                    targetZone = 'A';
                }

            }
        }

        // Close TF API
        if (tfod != null) {
            tfod.shutdown();
        }

        // Drive Till Launch Line
        Arm.setPower(-0.3);
        MoveForward(0.6);
        sleep(600);
        SenseColor('W', 'F');


        //Target Zone A
        if(targetZone == 'A') {
            telemetry.addData(">", "Zone A");
            telemetry.update();
            sleep(100);
            TurnRight(1250);

            Stop();
            sleep(200);

            Arm.setPower(-0.5);
            sleep(750);
            Arm.setPower(0.5);
            TurnLeft(2500);

            ShootAndMove();
        }

        //Target Zone B
        if(targetZone == 'B'){
            telemetry.addData(">", "Zone B");
            telemetry.update();
            sleep(100);

            //Move to target zone B
            /*
            MoveForward();
            sleep(350);
            */
            SenseColor('B', 'F');
            /*
            TurnRight(1000);
            MoveForward();
            sleep(500);
            */
            TurnRight(2500);

            //Pause for 1 second
            Stop();
            sleep(1000);

            //Place Wobble Goal
            Arm.setPower(0.5);
            sleep(1000);

            Arm.setPower(-0.5);
            sleep(1000);

            // Move to Launch line
            SenseColor('W', 'F');
            TurnLeft(1000);
            StrafeRight(500);
            MoveBackward(0.6);
            sleep(200);
            ShootAndMove();

        }

        //Target Zone C
        if(targetZone == 'C'){
            telemetry.addData(">", "Zone C");
            telemetry.update();
            sleep(100);

            //Skip Target Zone B
            for(int i = 0; i <= 2; i++){
                SenseColor('B', 'F');
                MoveForward(.3);
                sleep(150);
            }

            MoveForward(.5);
            sleep(100);

            //Move to Target Zone C
            TurnRight(1250);
            // Move Backward
            SenseColor('B', 'B');
            Stop();
            sleep(1000);

            //Place Wobble Goal
            Arm.setPower(0.5);
            sleep(1000);

            Arm.setPower(-0.5);
            sleep(1000);


            //Face to Launch Line
            StrafeRight(500);
            TurnRight(1000);

            // Move to Launch line
            SenseColor('W', 'F');
            TurnLeft(1000);
            StrafeRight(500);
            MoveBackward(0.6);
            sleep(200);
            ShootAndMove();
        }

    } // end runOpMode()

// Disable Tracking when we are done;
        targetsUltimateGoal.deactivate();
} //end class
