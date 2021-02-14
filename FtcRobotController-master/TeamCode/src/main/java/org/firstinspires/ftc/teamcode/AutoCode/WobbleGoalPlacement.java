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

@Autonomous(name = "WobbleGoalPlacement", group = "WobbleGoal")

public class WobbleGoalPlacement extends LinearOpMode{
    //Hardware objects
    private ColorSensor CSensor;
    private DcMotor frontLeftMotor;
    private DcMotor frontRightMotor;
    private DcMotor backLeftMotor;
    private DcMotor backRightMotor;
    private Servo servo;

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
    public void MoveForward(){
        //Set frontLeft motor power level slightly higher than others
        //This is to offset drift in our robot's movement
        frontLeftMotor.setPower(0.32);
        frontRightMotor.setPower(0.3);
        backLeftMotor.setPower(0.3);
        backRightMotor.setPower(0.3);
    }
    public void MoveBackward(){
        frontLeftMotor.setPower(-0.32);
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


    //This method programs the robot to drive forward or backward until CSensor has detected white or blue
    //color parameter is for the color robot needs to stop at, direction parameter is the direction robot should drive
    // FOR COLOR PARAMETER: 'W' = white, 'B' = blue
    // FOR DIRECTION PARAMETER: 'F' = forward, 'B' = backward
    public void SenseColor(char color, char direction){

        //Sense blue color(for Target Zones)

        if(color == 'B'){
            while (true) {
                //Print rgb values on telemetry(for testing purposes only)
                telemetry.addData("Blue:", CSensor.blue());
                telemetry.addData("Red:", CSensor.red());
                telemetry.addData("Green:", CSensor.green());
                telemetry.update();
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
                //Print rgb values on telemetry(for testing purposes only)
                telemetry.addData("Blue:", CSensor.blue());
                telemetry.addData("Red:", CSensor.red());
                telemetry.addData("Green:", CSensor.green());
                telemetry.update();
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








    @Override
    public void runOpMode(){
        CSensor = hardwareMap.get(ColorSensor.class, "CS1");
        backRightMotor = hardwareMap.get(DcMotor.class, "br");
        backLeftMotor = hardwareMap.get(DcMotor.class, "bl");
        frontRightMotor = hardwareMap.get(DcMotor.class, "fr");
        frontLeftMotor = hardwareMap.get(DcMotor.class, "fl");
        servo = hardwareMap.get(Servo.class, "Hook");
        servo.setPosition(0);

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


        // Invoke TF API
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

        // Drive Till White Line
        SenseColor('W', 'F');


        //Target Zone A
        if(targetZone == 'A') {
            //Move to target zone and place wobble goal
            TurnRight(500);
            MoveBackward();
            sleep(350);
            servo.setPosition(90);

            //Clear area
            MoveForward();
            sleep(350);

            //Move back to Start Line for second Wobble Goal
            TurnLeft(500);
            SenseColor('B', 'B');

            //Pick up second Wobble Goal
            TurnLeft(500);
            servo.setPosition(0);

            //Move back to White Line
            TurnRight(500);
            SenseColor('W', 'F');

            //Move to target zone and place wobble goal
            TurnRight(500);
            MoveBackward();
            sleep(350);
            servo.setPosition(90);

            //Clear area
            MoveForward();
            sleep(350);
        }

        //Target Zone B
        if(targetZone == 'B'){
            //Move to target zone B
            TurnRight(500);
            MoveForward();
            sleep(500);
            TurnLeft(500);
            SenseColor('B', 'F');
            TurnRight(1000);

            // Release wobble Goal
            servo.setPosition(90);
            sleep(500);
            //Turn Right to ready to go to white line
            // Move to white line
            SenseColor('W', 'F');
        }
        //Target Zone C
        if(targetZone == 'C'){
            //Skip 2 Blue Lines
            for(int i = 1; i <= 2; i++){
                SenseColor('B', 'F');
                MoveForward();
                sleep(500);

            }
            //Move to Target Zone C
            TurnRight(500);

            //Move backward 1/2 sec
            MoveBackward();
            sleep(350);
            //Place wobble goal
            servo.setPosition(90);
            sleep(1000);

            // Clear Area
            MoveForward();
            sleep(350);

            //Go back to Launch Line
            TurnRight(500);
            SenseColor('W', 'F');
        }

    } // end runOpMode()

} //end class