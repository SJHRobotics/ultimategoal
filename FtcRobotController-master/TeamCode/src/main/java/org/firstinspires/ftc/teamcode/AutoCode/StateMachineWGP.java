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

@Autonomous(name = "State Machine Version WGP", group = "WobbleGoal")

public class StateMachineWGP extends LinearOpMode{
    //Hardware objects
    private ColorSensor CSensor;
    private DcMotor frontLeftMotor;
    private DcMotor frontRightMotor;
    private DcMotor backLeftMotor;
    private DcMotor backRightMotor;

    //TFOD Variables
    private static final String TFOD_MODEL_ASSET = "UltimateGoal.tflite";
    private static final String LABEL_FIRST_ELEMENT = "Quad";
    private static final String LABEL_SECOND_ELEMENT = "Single";
    private char targetZone;
    private VuforiaLocalizer vuforia;
    private TFObjectDetector tfod;
    private static final String VUFORIA_KEY =
            "Ae/YeOf/////AAABmR8KMKVXi0gFg1/JtSBMj5WHZwOHCMtdvkRRmVdKQcjYBCk/JBHyLtxgccLh2ZJezNZ2W/ZU6mi38O6dsGABJtKELx/nxVc78up34+6k21SQSPKu8qgK9RuK5deUYb9K9gk8QG9xuGvGD5xQpH+nxeywwwQQXmExoEeLvlp6+H5Qa90lDZZPs2llKVqvdmuA8TSpGEktHgLcH0L4QtnF1JM1e7GY6woBW3aktTjXtqjK9mtvgbTRuBceBeLUuy7nhrT2+qt7aPzSAWsMgvrdduScWpYl14bQESUVEWX6Dz8xcNHOsDVnPB593nqj2KVVBbcHno8NATIGDvERkE2d4SUa5IRECzJ+nWbI9Fcx3zdZ";
    //Variable for holding current state
    private String currentState = TFODPROCESS;


    enum States{
        TFODPROCESS,
        MOVETOLAUNCHLINE,
        MOVETOZONEA,
        MOVETOZONEB,
        MOVETOZONEC,
        PICKUP2NDWG,
        END
    }

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
    @Override
    public void runOpMode(){
        CSensor = hardwareMap.get(ColorSensor.class, "CS1");
        backRightMotor = hardwareMap.get(DcMotor.class, "br");
        backLeftMotor = hardwareMap.get(DcMotor.class, "bl");
        frontRightMotor = hardwareMap.get(DcMotor.class, "fr");
        frontLeftMotor = hardwareMap.get(DcMotor.class, "fl");


        backLeftMotor.setDirection(DcMotor.Direction.REVERSE);
        frontLeftMotor.setDirection(DcMotor.Direction.REVERSE);
        initVuforia();
        initTfod();
        waitForStart();

        while(currentState != States.END){
            switch(currentState) {
                case TFODPROCESS:
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
                case MOVETOLAUNCHLINE:
                    SenseColor('W', 'F');
                    switch(targetZone){
                        case 'A':
                            currentState = States.MOVETOZONEA;
                        case 'B':
                            currentState = States.MOVETOZONEB;
                        case 'C':
                            currentState = States.MOVETOZONEC;
                    }
                case MOVETOZONEA:
                    TurnRight(500);
                    SenseColor('B', 'B');
                    currentState = States.PICKUP2NDWG;
                case MOVETOZONEB:
                    TurnRight(1600);
                    MoveBackward();
                    sleep(1000);
                    TurnLeft(500);
                    SenseColor('B', 'B');
                    currentState = States.PICKUP2NDWG;
                case MOVETOZONEC:
                    //Skip 2 Blue Lines
                    for(int i = 1; i <= 2; i++){
                        SenseColor('B', 'F');
                        MoveForward();
                        sleep(500);

                    }
                    //Move to Target Zone C
                    TurnRight(500);
                    SenseColor('B', 'B');
                    currentState = States.PICKUP2NDWG;

                case PICKUP2NDWG:
                    switch(targetZone){
                        case 'A':
                            //Move back to Start Line for second Wobble Goal
                            TurnLeft(450);
                            MoveBackward();
                            sleep(5500);

                            //Pick up second Wobble Goal
                            TurnLeft(500);
                            MoveBackward();
                            sleep(700);
                            TurnRight(500);
                            currentState = States.MOVETOZONEA;
                        case 'B':
                            // Move to white line
                            SenseColor('W', 'F');

                            //Move to Start Line for 2nd Wobble Goal
                            MoveForward();
                            sleep(1000);
                            TurnRight(500);
                            SenseColor('B', 'F');
                            TurnLeft(500);
                            MoveForward();
                            sleep(4000);
                            TurnRight(500);
                            MoveBackward();
                            sleep(700);

                            //Move to Target Zone B
                            SenseColor('B', 'F');
                            TurnRight(650);
                            SenseColor('W', 'F');
                            currentStates = States.MOVETOZONEB;
                        case 'C':
                            //Clear area
                            MoveForward();
                            sleep(350);

                            //Go back to Launch Line
                            TurnLeft(500);
                            SenseColor('W', 'F');

                            //Move to Start Line
                            MoveBackward();
                            sleep(1000);
                            TurnRight(500);
                            SenseColor('B', 'B');
                            TurnLeft(500);
                            MoveBackward();
                            sleep(4000);
                            TurnLeft(500);
                            MoveBackward();
                            sleep(700);

                            //Go back to Launch Line
                            SenseColor('B', 'F');
                            TurnRight(500);
                            SenseColor('W', 'F');

                            currentState = States.MOVETOZONEC;
                    }


            }

        }
    }
} // end class