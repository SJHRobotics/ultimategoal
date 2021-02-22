package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.ColorSensor;

@Autonomous(name = "WobbleGoalPlacement", group = "WobbleGoal")

public class WobbleGoalPlacement extends LinearOpMode{
    //Hardware objects
    private ColorSensor CSensor;
    private DcMotor frontLeftMotor;
    private DcMotor frontRightMotor;
    private DcMotor backLeftMotor;
    private DcMotor backRightMotor;



    //Movement methods
    /*
    NOTE: We didn't add the time parameter for MoveForward() and MoveBackward() like we did for TurnLeft() and TurnRight()
    because we will need these methods to drive the robot forward/backward continously until Color Sensor detects the blue line.
    If we need to ever move forward for a few seconds, we can just add a sleep statement underneath the call.
     */
    public void MoveForward(){
        frontLeftMotor.setPower(0.3);
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


        //Target Zone A
        if(targetZone == 'A') {
            //Move to target zone
            TurnLeft(500);

            while (true) {
                //Print rgb values on telemetry(for testing purposes only)
                telemetry.addData("Blue:", CSensor.blue());
                telemetry.addData("Red:", CSensor.red());
                telemetry.addData("Green:", CSensor.green());
                telemetry.update();

                MoveForward();

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

        //Target Zone B
        if(targetZone == 'B'){
            while (true) {
                //Print rgb values on telemetry(for testing purposes only)
                telemetry.addData("Blue:", CSensor.blue());
                telemetry.addData("Red:", CSensor.red());
                telemetry.addData("Green:", CSensor.green());
                telemetry.update();

                MoveForward();

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
        //Target Zone C
        if(targetZone == 'C'){
            //Skip 2 Blue Lines
            for(int i = 1; i <= 2; i++){
                while (true) {
                    //Print rgb values on telemetry(for testing purposes only)
                    telemetry.addData("Blue:", CSensor.blue());
                    telemetry.addData("Red:", CSensor.red());
                    telemetry.addData("Green:", CSensor.green());
                    telemetry.update();

                    MoveForward();

                    //If CSensor detects blue line(more blue than other colors)
                    if (CSensor.blue() > CSensor.red() && CSensor.blue() > CSensor.green()) {
                        Stop();
                        break;
                    }
                    else {
                        continue;
                    }
                }
                MoveForward();
                sleep(500);

            }

            TurnLeft(500);

        }

    } // end runOpMode()

} //end class
