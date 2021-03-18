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

        VuforiaLocalizer.Parameters parameters = new VuforiaLocalizer.Parameters();

        parameters.vuforiaLicenseKey = VUFORIA_KEY;
        parameters.cameraName = hardwareMap.get(WebcamName.class, "TensorflowWebcam");

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

    public void PlaceWG1(){
        Arm.setPower(0.5);
        sleep(pauseTimer);

        Arm.setPower(-0.5);
        sleep(pauseTimer);
    }

    @TODO
    public void PlaceWG2(){

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
            //Print Zone on DS
            telemetry.addData(">", "Zone A");
            telemetry.update();
            sleep(pauseTimer);

            //Turn Right about 90 degrees
            TurnRight(pauseTimer);

            //Pause for 1 sec
            Stop();
            sleep(pauseTimer);

            //Place WG 1
            PlaceWG1();

            //Prepare robot for WG 2 Placement

        }

        //Target Zone B
        if(targetZone == 'B'){
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
            
            //Place WG 1
            PlaceWG1();
            
            // Move to Launch line
            SenseColor('W', 'F');

            //Prepare robot for WG 2 Placement
        }

        //Target Zone C
        if(targetZone == 'C'){
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

            //Place Wobble Goal
            PlaceWG1();

            //Turn right to Launch Line
            TurnRight(1250);
            
            //Move to Launch line
            SenseColor('W', 'F');

            //Prepare robot for WG 2 Placement

        }

    } // end runOpMode()

} //end class
