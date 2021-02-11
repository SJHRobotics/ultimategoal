package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

/**
 * This is the final Telly-Op Code
 */
@TeleOp(name ="RobotTellyOp", group = "Telly-Op")
public class  RobotTellyOp extends LinearOpMode {
    private DcMotor frontLeftMotor;
    private DcMotor frontRightMotor;
    private DcMotor backLeftMotor;
    private DcMotor backRightMotor;
    private DcMotor shooterLeftMotor;
    private DcMotor shooterRightMotor;
    private Servo Flicker;
    private Servo Conveyor;
    private Servo Hook;
    public boolean moveBack = false;
    public boolean conveyorCanMoveBack = true;
    public boolean hookToNienty = true;
    public boolean stopShooter = false;

    public void runOpMode() {
        backLeftMotor = hardwareMap.get(DcMotor.class, "bl");
        frontLeftMotor = hardwareMap.get(DcMotor.class, "fl");
        frontRightMotor = hardwareMap.get(DcMotor.class, "fr");
        backRightMotor = hardwareMap.get(DcMotor.class, "br");
        shooterLeftMotor = hardwareMap.get(DcMotor.class, "sl");
        shooterRightMotor = hardwareMap.get(DcMotor.class, "sr");
        Flicker = hardwareMap.get(Servo.class, "Flicker");
        Conveyor = hardwareMap.get(Servo.class, "Conveyor");
        Hook = hardwareMap.get(Servo.class, "Hook");
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

            //sets the speed of the motors

            backLeftMotor.setPower(p1);
            frontLeftMotor.setPower(p2);
            frontRightMotor.setPower(p3);
            backRightMotor.setPower(p4);

            // Display the current value of the motor speed

            telemetry.addData("backLeftMotor_speed:", "%5.2f", p1);
            telemetry.addData("frontLeftMotor_speed:", "%5.2f", p2);
            telemetry.addData("frontRightMotor_speed:", "%5.2f", p3);
            telemetry.addData("backRightMotor_speed:", "%5.2f", p4);
            telemetry.update();

            //shooting program starts

            // turning the shooting motors on

            if(gamepad1.b && !stopShooter) {
                shooterRightMotor.setPower(1.0);
                shooterLeftMotor.setPower(1.0);
                stopShooter = true;
            }

            //shooting program stops

            else if(gamepad1.b && stopShooter){
                shooterRightMotor.setPower(0.0);
                shooterLeftMotor.setPower(0.0);
                stopShooter = false;
            }


            // Flicker Servo code starts

            //FLickes the servo to shoot
            if(gamepad1.dpad_up && !moveBack) {
                Flicker.setPosition(130);
                moveBack = true;
            }
            //Servo goes back
            else if(gamepad1.dpad_down && moveBack) {
                Flicker.setPosition(0);
                moveBack = false;
            }
            // Flicker Servo code stops

            //Storage movement code starts

            //The conveyor goes down
            if(gamepad1.left_bumper && conveyorCanMoveBack) {
                Conveyor.setPosition(0);
                conveyorCanMoveBack = false;
            }
            //The conveyor goes up
            else if(gamepad1.right_bumper && !conveyorCanMoveBack) {
                Conveyor.setPosition(120);
                conveyorCanMoveBack = true;
            }
            //Storage movement code stops

            //Hook code starts

            //Opens the hook
            if(gamepad1.dpad_left && hookToNienty) {
                Hook.setPosition(90);
                hookToNienty = false;
            }
            //Closes the hook
            else if(gamepad1.dpad_right && !hookToNienty){
                Hook.setPosition(0);
                hookToNienty = true;
            }

            //Hook code stops
        }
        backLeftMotor.setPower(0.0);//Stoping the motors: Start.
        frontLeftMotor.setPower(0.0);
        frontRightMotor.setPower(0.0);
        backRightMotor.setPower(0.0);//Stopping the motors: End.

    }
}