package org.firstinspires.ftc.teamcode;

//import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import java.util.List;
import org.firstinspires.ftc.robotcore.external.ClassFactory;
import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaLocalizer;
import org.firstinspires.ftc.robotcore.external.tfod.TFObjectDetector;
import org.firstinspires.ftc.robotcore.external.tfod.Recognition;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.ColorSensor;
@Autonomous(name = "WobbleGoalPlacement", group = "WobbleGoal")

public class WobbleGoalPlacement extends LinearOpMode{
    private ColorSensor CSensor;
    private DcMotor frontLeftMotor;
    private DcMotor frontRightMotor;
    private DcMotor backLeftMotor;
    private DcMotor backRightMotor;
    private static final String TFOD_MODEL_ASSET = "UltimateGoal.tflite";
    private static final String LABEL_FIRST_ELEMENT = "Quad";
    private static final String LABEL_SECOND_ELEMENT = "Single";
    private static final String VUFORIA_KEY =
            "Ae/YeOf/////AAABmR8KMKVXi0gFg1/JtSBMj5WHZwOHCMtdvkRRmVdKQcjYBCk/JBHyLtxgccLh2ZJezNZ2W/ZU6mi38O6dsGABJtKELx/nxVc78up34+6k21SQSPKu8qgK9RuK5deUYb9K9gk8QG9xuGvGD5xQpH+nxeywwwQQXmExoEeLvlp6+H5Qa90lDZZPs2llKVqvdmuA8TSpGEktHgLcH0L4QtnF1JM1e7GY6woBW3aktTjXtqjK9mtvgbTRuBceBeLUuy7nhrT2+qt7aPzSAWsMgvrdduScWpYl14bQESUVEWX6Dz8xcNHOsDVnPB593nqj2KVVBbcHno8NATIGDvERkE2d4SUa5IRECzJ+nWbI9Fcx3zdZ";
    private VuforiaLocalizer vuforia;
    private TFObjectDetector tfod;
    private char targetZone = 'A';
    //Movement methods
    public void MoveForward(){
        frontLeftMotor.setPower(0.2);
        frontRightMotor.setPower(0.2);
        backLeftMotor.setPower(0.2);
        backRightMotor.setPower(0.2);
    }
    public void MoveBackward(){
        frontLeftMotor.setPower(-0.2);
        frontRightMotor.setPower(-0.2);
        backLeftMotor.setPower(-0.2);
        backRightMotor.setPower(-0.2);
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
    //Initiate Vuforia
    private void initVuforia() {
        VuforiaLocalizer.Parameters parameters = new VuforiaLocalizer.Parameters();

        parameters.vuforiaLicenseKey = VUFORIA_KEY;
        parameters.cameraName = hardwareMap.get(WebcamName.class, "TensorflowWebcam");

        vuforia = ClassFactory.getInstance().createVuforia(parameters);

    }
    //Initiate TFOD
    private void initTfod() {
        int tfodMonitorViewId = hardwareMap.appContext.getResources().getIdentifier(
                "tfodMonitorViewId", "id", hardwareMap.appContext.getPackageName());
        TFObjectDetector.Parameters tfodParameters = new TFObjectDetector.Parameters(tfodMonitorViewId);
        tfodParameters.minimumConfidence = 0.8;
        tfod = ClassFactory.getInstance().createTFObjectDetector(tfodParameters, vuforia);
        tfod.loadModelFromAsset(TFOD_MODEL_ASSET, LABEL_FIRST_ELEMENT, LABEL_SECOND_ELEMENT);
    }
    //This method will use TFOD and Vuforia to return how many rings there are in stack
    public void DetectRings(){
        initVuforia();
        initTfod();
        if (tfod != null) {
            List<Recognition> updatedRecognitions = tfod.getUpdatedRecognitions();
            if (updatedRecognitions != null) {
                // This will stay here for testing purposes, but may be removed later
                telemetry.addData("# Object Detected", updatedRecognitions.size());
                //No rings
                if(updatedRecognitions.size() == 0){
                    telemetry.addData(">", "Target Zone A");
                    targetZone = 'A';
                }
                //There are rings
                else{
                    int i = 0;
                    for (Recognition recognition : updatedRecognitions) {
                        telemetry.addData(String.format("label (%d)", i), recognition.getLabel());
                        telemetry.addData(String.format("  left,top (%d)", i), "%.03f , %.03f",
                                recognition.getLeft(), recognition.getTop());
                        telemetry.addData(String.format("  right,bottom (%d)", i), "%.03f , %.03f",
                                recognition.getRight(), recognition.getBottom());
                        //1 ring
                        if(recognition.getLabel().equals("Single")){
                            telemetry.addData(">", "Target Zone B");
                            targetZone = 'B';
                        }
                        if(recognition.getLabel().equals("Quad")){
                            telemetry.addData(">", "Target Zone C");
                            targetZone = 'C';
                        }
                        telemetry.update();
                    }
                    


                }

            }

        }
    }

    //This method will be called whenever robot needs to move forward and stop once it detects blue
    //Will modify this later for all colors
    public void SenseColor(char color){
        //Sense blue color(for Target Zones)
        if(color == 'B'){
            while (true) {
                //Print rgb values on telemetry(for testing purposes only)
                telemetry.addData("Blue:", CSensor.blue());
                telemetry.addData("Red:", CSensor.red());
                telemetry.addData("Green:", CSensor.green());
                telemetry.update();

                MoveForward();

                if (CSensor.blue() > CSensor.red() && CSensor.blue() > CSensor.green()) {
                    Stop();
                    telemetry.addData(">", "I am at Target Zone!");
                    telemetry.update();
                    sleep(3000);
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

                MoveForward();
                //According to our tests, CSensor will detect more of green than other colors when it sees white
                if (CSensor.green() > CSensor.blue() && CSensor.green() > CSensor.red()) {
                    Stop();
                    telemetry.addData(">", "I am at Target Zone!");
                    telemetry.update();
                    sleep(3000);
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
        telemetry.addData(">", "Ready to start!");
        telemetry.update();
        waitForStart();

        /*
        NOTE: Before running this program, robot must be positioned at Launch Line
        In the future, I will modify it so the robot drives up to the line automatically.
         */


        DetectRings();

/*
        //Target Zone A
        if(targetZone == 'A') {
            //Turn left to align with target zone - adjust as needed
            TurnLeft(500);
            SenseBlueColor();
            MoveBackward();
            sleep(1000);
            TurnRight(500);
            MoveBackward();
            sleep(500);
            //Arm Servo code goes here
        }
        //Target Zone B
        if(targetZone == 'B'){
            SenseBlueColor();
            MoveBackward();
            sleep(1000);
        }
        //Target Zone C
        if(targetZone == 'C'){
            for(int i = 1; i <= 2; i++){
                SenseBlueColor();
                MoveForward();
                sleep(1000);
            }
            TurnLeft(500);
            SenseBlueColor();
            //Go back to Launch Line
            MoveBackward();
            sleep(500);
            TurnLeft(500);
            for(int j = 1; j <= 2; j++){
                SenseBlueColor();
                MoveForward();
                sleep(1000);
            }
        }
*/
    } // end runOpMode()


} //end class
