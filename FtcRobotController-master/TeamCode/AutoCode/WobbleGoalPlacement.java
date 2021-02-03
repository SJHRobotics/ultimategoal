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


    public void MoveForward(long time){
        frontLeftMotor.setPower(0.5);
        frontRightMotor.setPower(0.5);
        backLeftMotor.setPower(0.5);
        backRightMotor.setPower(0.5);
        sleep(time);
    }
    public void TurnRight(long time){
        frontLeftMotor.setPower(1.0);
        frontRightMotor.setPower(-1.0);
        backLeftMotor.setPower(1.0);
        backRightMotor.setPower(-1.0);
        sleep(time);
    }
    public void Stop(){
        frontLeftMotor.setPower(0);
        frontRightMotor.setPower(0);
        backLeftMotor.setPower(0);
        backRightMotor.setPower(0);
    }
/*
    WobbleGoalPlacement obj = new WobbleGoalPlacement();
    Thread thread = new Thread(obj);
    thread.start();

    public void run(){

    }
    (*/
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

        boolean condition = true;
        //TurnRight(850);
        while (condition) {
            telemetry.addData("Blue:", CSensor.blue());
            telemetry.addData("Red:", CSensor.red());
            telemetry.addData("Green:", CSensor.green());
            telemetry.update();

            MoveForward(1000);
            Stop();
            if(CSensor.red() > CSensor.blue() && CSensor.red() > CSensor.green()){
                Stop();
                System.out.println("I am at Target Zone!");
                break;
            }
            else{
                continue;
            }

        }


    }


}

