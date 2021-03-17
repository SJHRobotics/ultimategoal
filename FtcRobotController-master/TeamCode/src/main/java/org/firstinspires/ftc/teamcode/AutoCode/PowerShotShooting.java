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
    public void Shoot() {
        shooterLeftMotor.setPower(0.75);
        shooterRightMotor.setPower(0.75);
        sleep(1500);
        Flicker.setPosition(170);
        sleep(500);
        Flicker.setPosition(0);
    }
    public void ShootAndMove() {
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
            TurnLeft(1250);
            StrafeRight(1000);
            MoveBackward(0.6);
            sleep(50);
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

} //end class
