package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.ColorSensor;
import org.firstinspires.ftc.robotcore.external.Telemetry;
@Autonomous(name = "WobbleGoalPlacement", group = "WobbleGoal")

public class WobbleGoalPlacement extends LinearOpMode{
    private ColorSensor CSensor;
    private DcMotor frontLeftMotor;
    private DcMotor frontRightMotor;
    private DcMotor backLeftMotor;
    private DcMotor backRightMotor;


    public void MoveForward(){
        frontLeftMotor.setPower(0.2);
        frontRightMotor.setPower(0.2);
        backLeftMotor.setPower(0.2);
        backRightMotor.setPower(0.2);
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

    //This method will be called whenever robot needs to move forward and stop once it detects blue
    //Will modify this later for all colors
    public void SenseBlueColor(){
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
    
    
    
    @Override
    public void runOpMode(){
        CSensor = hardwareMap.get(ColorSensor.class, "CS1");
        backRightMotor = hardwareMap.get(DcMotor.class, "br");
        backLeftMotor = hardwareMap.get(DcMotor.class, "bl");
        frontRightMotor = hardwareMap.get(DcMotor.class, "fr");
        frontLeftMotor = hardwareMap.get(DcMotor.class, "fl");
        backLeftMotor.setDirection(DcMotor.Direction.REVERSE);
        frontLeftMotor.setDirection(DcMotor.Direction.REVERSE);

        waitForStart();

        /*
        NOTE: Before running this program, robot must be positioned at Launch Line
        In the future, I will modify it so the robot drives up to the line automatically.
         */

        char targetZone = 'A';



        //Target Zone A
        if(targetZone == 'A') {
            //Turn left to align with target zone - adjust as needed
            TurnLeft(500);
            SenseBlueColor();


            //Arm Servo code goes here

        }

        //Target Zone B
        if(targetZone == 'B'){
            SenseBlueColor();
        }

        //Target Zone C
        if(targetZone == 'C'){

        }


    } // end runOpMode()


} //end class

