package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.ColorSensor;
import java.util.List;
import org.firstinspires.ftc.robotcore.external.ClassFactory;
import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaLocalizer;
import org.firstinspires.ftc.robotcore.external.tfod.TFObjectDetector;
import org.firstinspires.ftc.robotcore.external.tfod.Recognition;
import org.firstinspires.ftc.robotcore.external.Telemetry;

@Autonomous(name = "WobbleGoalPlacement2", group = "WobbleGoal")

public class WobbleGoalPlacement extends LinearOpMode{
    //Common hardware objects
    private ColorSensor CSensor;
    private DcMotor frontLeftMotor;
    private DcMotor frontRightMotor;
    private DcMotor backLeftMotor;
    private DcMotor backRightMotor;

    //WG 1 Hardware Objects
    private DcMotor Arm; // Intake Arm

    //WG 2 Hardware Objects
    private Servo Clamps;
    private Servo Elbow;

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

    //Vuforia Navigation Variables
    private static final VuforiaLocalizer.CameraDirection CAMERA_CHOICE = BACK;
    private static final boolean PHONE_IS_PORTRAIT = false  ;

    private static final float mmPerInch        = 25.4f;
    private static final float mmTargetHeight   = (6) * mmPerInch; // the height of the center of the target image above the floor

    private static final float halfField = 72 * mmPerInch;
    private static final float quadField  = 36 * mmPerInch;

    private OpenGLMatrix lastLocation = null;
    private VuforiaLocalizer vuforia = null;
    WebcamName webcamName = null;

    private boolean targetVisible = false;
    private float phoneXRotate    = 0;
    private float phoneYRotate    = 0;
    private float phoneZRotate    = 0;

    private float xInchPos;
    private float yInchPos;
    private float zInchPos;

    //Motor Speed Variables
    private static final double slowSpeed = 0.3;
    private static final double regularSpeed = 0.5;
    private static final double driftOffset = 0.03;
    
    private static final int pauseTimer = 1000;

    //Movement methods
    public void MoveForward(){
        //Set frontLeft motor power level slightly higher than others
        //This is to offset drift in our robot's movement
        frontLeftMotor.setPower(slowSpeed + driftOffset);
        frontRightMotor.setPower(slowSpeed);
        backLeftMotor.setPower(slowSpeed);
        backRightMotor.setPower(slowSpeed);
    }
    public void MoveBackward(){
        frontLeftMotor.setPower(-(slowSpeed + driftOffset));
        frontRightMotor.setPower(-slowSpeed);
        backLeftMotor.setPower(-slowSpeed);
        backRightMotor.setPower(-slowSpeed);
    }
    public void TurnRight(long time){
        frontLeftMotor.setPower(regularSpeed);
        frontRightMotor.setPower(-regularSpeed);
        backLeftMotor.setPower(regularSpeed);
        backRightMotor.setPower(-regularSpeed);
        sleep(time);
    }
    public void TurnLeft(long time){
        frontLeftMotor.setPower(-regularSpeed);
        frontRightMotor.setPower(regularSpeed);
        backLeftMotor.setPower(-regularSpeed);
        backRightMotor.setPower(regularSpeed);
        sleep(time);
    }
    public void Stop(){
        frontLeftMotor.setPower(0);
        frontRightMotor.setPower(0);
        backLeftMotor.setPower(0);
        backRightMotor.setPower(0);
    }

    //For initializing Vuforia
    private void initVuforia() {
        webcamName = hardwareMap.get(WebcamName.class, "TensorflowWebcam");
        VuforiaLocalizer.Parameters parameters = new VuforiaLocalizer.Parameters();

        parameters.vuforiaLicenseKey = VUFORIA_KEY;
        parameters.cameraName = webcamName;
        parameters.useExtendedTracking = false;

        //  Instantiate the Vuforia engine
        vuforia = ClassFactory.getInstance().createVuforia(parameters);
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


    //This method programs the robot to drive forward or backward until the Color Sensor(referred to as CSensor) has detected white or blue
    //color parameter is for the color robot needs to stop at, direction parameter is the direction robot should drive
    // FOR COLOR PARAMETER: 'W' = white, 'B' = blue
    // FOR DIRECTION PARAMETER: 'F' = forward, 'B' = backward
    public void SenseColor(char color, char direction){

        //Sense blue color(for Target Zones)
        if(color == 'B'){
            while (true) {

                if(direction == 'F'){
                    MoveForward();
                }
                if(direction == 'B'){
                    MoveBackward();
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
                    MoveForward();
                }
                if(direction == 'B') {
                    MoveBackward();
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
    //Methods for moving to Zones A, B, C
    public void MoveToZoneA(){
        //Print Zone on DS
        telemetry.addData(">", "Zone A");
        telemetry.update();
        sleep(pauseTimer);

        //Turn Right about 90 degrees
        TurnRight(pauseTimer);

        //Pause for 1 sec
        Stop();
        sleep(pauseTimer);
    }

    public void MoveToZoneB(){
        //Print Zone on DS
        telemetry.addData(">", "Zone B");
        telemetry.update();
        sleep(pauseTimer);

        //Move to 1st line of target zone B
        SenseColor('B', 'F');

        //Turn Right about 180 degrees
        TurnRight(2500);

        //Pause for 1 second
        Stop();
        sleep(pauseTimer);
    }

    public void MoveToZoneC(){
        //Print Zone on DS
        telemetry.addData(">", "Zone C");
        telemetry.update();
        sleep(pauseTimer);

        //Skip 2 blue lines of Target Zone B
        for(int i = 0; i <= 2; i++){
            SenseColor('B', 'F');
            MoveForward();
            sleep(150);
        }

        //Move forward a bit more
        MoveForward();
        sleep(300);

        //Turn Right about 90 degrees
        TurnRight(1250);

        // Move Backward to Target Zone C
        SenseColor('B', 'B');

        //Pause for 1 sec
        Stop();
        sleep(pauseTimer);
    }

    //Method for placing WG 1
    public void PlaceWG1(){
        Arm.setPower(0.5);
        sleep(pauseTimer);

        Arm.setPower(-0.5);
        sleep(pauseTimer);
    }
    //This method will use Vuforia to program the robot to navigate through the field
    //One Parameter: navImage
    // F = Front Wall Target, B = Blue Alliance Target
    public void VuforiaNavigate(char navImage){
        while (true) {
            // check all the trackable targets to see which one (if any) is visible.
            targetVisible = false;
            for (VuforiaTrackable trackable : allTrackables) {
                if (((VuforiaTrackableDefaultListener)trackable.getListener()).isVisible()) {
                    //telemetry.addData("Visible Target", trackable.getName());
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
                xInchPos = translation.get(0) / mmPerInch;
                yInchPos = translation.get(1) / mmPerInch;
                zInchPos = translation.get(2) / mmPerInch;
                telemetry.addData("Pos (in)", "{X, Y, Z} = %.1f, %.1f, %.1f",
                        xInchPos, yInchPos, zInchPos);

                /*
                // express the rotation of the robot in degrees.
                Orientation rotation = Orientation.getOrientation(lastLocation, EXTRINSIC, XYZ, DEGREES);

                //telemetry.addData("Rot (deg)", "{Roll, Pitch, Heading} = %.0f, %.0f, %.0f", rotation.firstAngle, rotation.secondAngle, rotation.thirdAngle);
            */
            }

            else {
                //telemetry.addData("Visible Target", "none");
                break;
            }
            //telemetry.update();

            //Front Wall Target
            if(navImage == 'F'){
                MoveForward();
                if (xInchPos <= -30){
                    Stop();
                    break;
                }
                else{
                    continue;
                }
            }
            //Blue Alliance Target
            else if(navImage == 'B'){
                MoveBackward();
                if (yInchPos <= 10){
                    Stop();
                    break;
                }
                else{
                    continue;
                }
            }
            //Invalid Parameter Input
            else{
                telemetry.addData(">", "ERROR: Invalid Parameter Input for VuforiaNavigate()");
                telemetry.update();
                sleep(6000);
                break;
            }






        } // end tracking loop
    }


    @Override
    public void runOpMode(){
        //Initalize common hardware
        CSensor = hardwareMap.get(ColorSensor.class, "CS1");
        backRightMotor = hardwareMap.get(DcMotor.class, "br");
        backLeftMotor = hardwareMap.get(DcMotor.class, "bl");
        frontRightMotor = hardwareMap.get(DcMotor.class, "fr");
        frontLeftMotor = hardwareMap.get(DcMotor.class, "fl");

        //Initalize WG 1 hardware
        Arm = hardwareMap.get(DcMotor.class, "Intake");

        //Initalize WG 2 hardware
        Clamps = hardwareMap.get(Servo.class, "HookOpen");
        Elbow = hardwareMap.get(Servo.class, "HookTurn");

        //Initalize Drivetrain Motors
        backLeftMotor.setDirection(DcMotor.Direction.REVERSE);
        frontLeftMotor.setDirection(DcMotor.Direction.REVERSE);
        backRightMotor.setDirection(DcMotor.Direction.FORWARD);
        frontRightMotor.setDirection(DcMotor.Direction.FORWARD);

        //Initialize Vuforia and TFOD
        initVuforia();
        initTfod();
        if (tfod != null) {
            tfod.activate();
            tfod.setZoom(2.5, 16.0/9.0);
        }

        //Load trackables for Vuforia Navigation
        VuforiaTrackables targetsUltimateGoal = this.vuforia.loadTrackablesFromAsset("UltimateGoal");
        VuforiaTrackable blueTowerGoalTarget = targetsUltimateGoal.get(0);
        blueTowerGoalTarget.setName("Blue Tower Goal Target");
        VuforiaTrackable blueAllianceTarget = targetsUltimateGoal.get(3);
        blueAllianceTarget.setName("Blue Alliance Target");
        VuforiaTrackable frontWallTarget = targetsUltimateGoal.get(4);
        frontWallTarget.setName("Front Wall Target");

        List<VuforiaTrackable> allTrackables = new ArrayList<VuforiaTrackable>();
        allTrackables.addAll(targetsUltimateGoal);

        //Set location of trackables
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

        if (CAMERA_CHOICE == BACK) {
            phoneYRotate = -90;
        } else {
            phoneYRotate = 90;
        }

        if (PHONE_IS_PORTRAIT) {
            phoneXRotate = 90 ;
        }


        final float CAMERA_FORWARD_DISPLACEMENT  = 4.0f * mmPerInch;   // eg: Camera is 4 Inches in front of robot-center
        final float CAMERA_VERTICAL_DISPLACEMENT = 8.0f * mmPerInch;   // eg: Camera is 8 Inches above ground
        final float CAMERA_LEFT_DISPLACEMENT     = 0;     // eg: Camera is ON the robot's center line

        OpenGLMatrix robotFromCamera = OpenGLMatrix
                .translation(CAMERA_FORWARD_DISPLACEMENT, CAMERA_LEFT_DISPLACEMENT, CAMERA_VERTICAL_DISPLACEMENT)
                .multiplied(Orientation.getRotationMatrix(EXTRINSIC, YZX, DEGREES, phoneYRotate, phoneZRotate, phoneXRotate));

        for (VuforiaTrackable trackable : allTrackables) {
            ((VuforiaTrackableDefaultListener) trackable.getListener()).setPhoneInformation(robotFromCamera, parameters.cameraDirection);
        }

        targetsUltimateGoal.activate();

        //Print Start Message on DS
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
        SenseColor('W', 'F');


        //Target Zone A
        if(targetZone == 'A') {
            //Move to Zone A
            MoveToZoneA();

            //Place WG 1
            PlaceWG1();

            //Turn Right about 180 degrees
            TurnRight(2500);

            //Using Blue Alliance Target, align with Front Wall Target
            VuforiaNavigate('B');

            //Turn Left about 90 degrees to see Front Wall Target
            TurnLeft(pauseTimer);

            //Using Front Wall Target, align with WG 2
            VuforiaNavigate('F');

            //Turn Left about 90 degrees
            TurnLeft(pauseTimer);

            //Move Backward to align Arm with WG 2
            SenseColor('B', 'B');
            MoveBackward();
            sleep(350);

            //Pick up WG 2 (TODO)

            //Drive to Start Line
            SenseColor('B', 'B');

            //Turn Left about 90 degrees
            TurnLeft(pauseTimer);

            //Drive to Launch Line
            SenseColor('W', 'F');
            
            //Move back to Zone A
            MoveToZoneA();

            //Place WG 2 (TODO)

        }

        //Target Zone B
        if(targetZone == 'B'){
            //Move to Zone B
            MoveToZoneB();
            
            //Place WG 1
            PlaceWG1();
            
            // Move to Launch line
            SenseColor('W', 'F');

            //Turn right about 90 degrees to align with Blue Alliance Target
            TurnRight(pauseTimer);

            //Using Blue Alliance Target, align with Front Wall Target
            VuforiaNavigate('B');

            //Turn left about 90 degrees to align with Front Wall Target
            TurnLeft(pauseTimer);

            //Using Front Wall Target, align with WG 2
            VuforiaNavigate('F');
            //Turn Left about 90 degrees
            TurnLeft(pauseTimer);

            //Align Arm with WG 2
            SenseColor('B', 'B');
            MoveBackward();
            sleep(350);

            //Pick up WG 2 (TODO)

            //Drive to Start Line
            SenseColor('B', 'B');

            //Turn Left about 90 degrees
            TurnLeft(pauseTimer);

            //Drive to Launch Line
            SenseColor('W', 'F');

            //Move back to Zone B
            MoveToZoneB();

            //Place WG 2 (TODO)

            // Move to Launch line
            SenseColor('W', 'F');
        }

        //Target Zone C
        if(targetZone == 'C'){
            //Move to Zone C
            MoveToZoneC();

            //Place WG 1
            PlaceWG1();

            //Turn right to Launch Line
            TurnRight(1250);
            
            //Move to Launch line
            SenseColor('W', 'F');

            //Turn right about 90 degrees to align with Blue Alliance Target
            TurnRight(pauseTimer);

            //Using Blue Alliance Target, align with Front Wall Target
            VuforiaNavigate('B');

            //Turn Left about 90 degrees to align with Front Wall Target
            TurnLeft(pauseTimer);

            //Using Front Wall Target, align with WG 2
            VuforiaNavigate('F');

            //Turn Left about 90 degrees
            TurnLeft(pauseTimer);

            //Align Arm with WG 2
            SenseColor('B', 'B');
            MoveBackward();
            sleep(350);

            //Pick up WG 2 (TODO)

            //Drive to Start Line
            SenseColor('B', 'B');

            //Turn Left about 90 degrees
            TurnLeft(pauseTimer);

            //Drive to Launch Line
            SenseColor('W', 'F');

            //Move to Zone C
            MoveToZoneC();

            //Place WG 2 (TODO)

            //Turn Right to Launch line
            TurnRight(1250);

            //Drive to Launch Line
            SenseColor('W', 'F');



        }

    } // end runOpMode()

} //end class
