package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

/**
 * This is the final robot
 */
@TeleOp(name ="RobotTellyOp", group = "Telly-Op")
public class  RobotTellyOp extends LinearOpMode {
    /*static final double INCREMENT   = .01;      amount to slew servo each CYCLE_MS cycle
    static final int    CYCLE_MS    =   50;      period of each cycle
    static final double MAX_POS     =  1.0;      Maximum rotational position
    static final double MIN_POS     =  0.0;      Minimum rotational position

    // Define class members

    double  position = (MAX_POS - MIN_POS)/2; // Start at halfway position
    boolean rampUp = true;*/
    private DcMotor frontLeftMotor;
    private DcMotor frontRightMotor;
    private DcMotor backLeftMotor;
    private DcMotor backRightMotor;
    private DcMotor shooterLeftMotor;
    private DcMotor shooterRightMotor;
    private Servo Flicker;
    public boolean moveBack = false;

    public void runOpMode() {


        backLeftMotor = hardwareMap.get(DcMotor.class, "bl");
        frontLeftMotor = hardwareMap.get(DcMotor.class, "fl");
        frontRightMotor = hardwareMap.get(DcMotor.class, "fr");
        backRightMotor = hardwareMap.get(DcMotor.class, "br");
        shooterLeftMotor = hardwareMap.get(DcMotor.class, "sl");
        shooterRightMotor = hardwareMap.get(DcMotor.class, "sr");
        Flicker = hardwareMap.get(Servo.class, "Flicker");
        backLeftMotor.setDirection(DcMotor.Direction.REVERSE);
        frontLeftMotor.setDirection(DcMotor.Direction.REVERSE);
        shooterLeftMotor.setDirection(DcMotor.Direction.REVERSE);
        backLeftMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        frontLeftMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        frontRightMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        backRightMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        backLeftMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        frontLeftMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        frontRightMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        backRightMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

        waitForStart();
        while (opModeIsActive()) {

            double px = gamepad1.left_stick_x;
            double py = -gamepad1.left_stick_y;
            double pa = (gamepad1.left_trigger - gamepad1.right_trigger);
            if (Math.abs(pa) < 0.05) pa = 0;
            double p1 = -px + py - pa;
            double p2 = px + py + -pa;
            double p3 = -px + py + pa;
            double p4 = px + py + pa;
            double max = Math.max(1.0, Math.abs(p1));
            max = Math.max(max, Math.abs(p2));
            max = Math.max(max, Math.abs(p3));
            max = Math.max(max, Math.abs(p4));
            p1 /= max;
            p2 /= max;
            p3 /= max;
            p4 /= max;
            // Display the current value of the motor speedz
            telemetry.addData("backLeftMotor_speed:", "%5.2f", p1);
            telemetry.addData("frontLeftMotor_speed:", "%5.2f", p2);
            telemetry.addData("frontRightMotor_speed:", "%5.2f", p3);
            telemetry.addData("backRightMotor_speed:", "%5.2f", p4);
            telemetry.update();
            double p11 = p1/2;
            double p22 = p2/2;
            double p33 = p3/2;
            double p44 = p4/2;
            backLeftMotor.setPower(p1);
            frontLeftMotor.setPower(p2);
            frontRightMotor.setPower(p3);
            backRightMotor.setPower(p4);
            if(gamepad1.b) {
                shooterRightMotor.setPower(1.0);
                shooterLeftMotor.setPower(1.0);
            }// turning the shooting motors on
            else{
                shooterRightMotor.setPower(0.0);
                shooterLeftMotor.setPower(0.0);


                if(gamepad1.y && !moveBack) {
                    Flicker.setPosition(90);
                    moveBack = true;
                } else if(gamepad1.y && moveBack) {
                    Flicker.setPosition(0);
                    moveBack = false;
                }

            }//turning the shooting motors off
            /* slew the servo, according to the rampUp (direction) variable.
            if (rampUp && gamepad1.dpad_up) {
                 Keep stepping up until we hit the max value.
                position += INCREMENT ;
                if (position >= MAX_POS ) {
                    position = MAX_POS;
                    rampUp = !rampUp;    Switch ramp direction
                }
            }
            else if(!rampUp && gamepad1.dpad_down) {
                 Keep stepping down until we hit the min value.
                position -= INCREMENT ;
                if (position <= MIN_POS ) {
                    position = MIN_POS;
                    rampUp = !rampUp;   Switch ramp direction
                }
            }


             Set the servo to the new position and pause
          s1.setPosition(position);
            sleep(CYCLE_MS);
            idle();*/
        }
        backLeftMotor.setPower(0.0);//Stoping the motors: Start.
        frontLeftMotor.setPower(0.0);
        frontRightMotor.setPower(0.0);
        backRightMotor.setPower(0.0);//Stopping the motors: End.
        /*telemetry.addData(">", "Done");//Showing the task done on DS.
        telemetry.update();// Updates the console.*/
    }
}

