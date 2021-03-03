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
    private DcMotor Intake;
    private Servo Flicker;
    private Servo Conveyor;
    private Servo HookOpen;
    private Servo HookTurn;
    private Servo HookBottomTurn;
    private Servo IntakeServo;
    public boolean stopShooter = false;
    private boolean IntakeClose = false;

    public void runOpMode() {

        //initializes all the motors and servos
        backLeftMotor = hardwareMap.get(DcMotor.class, "bl");
        frontLeftMotor = hardwareMap.get(DcMotor.class, "fl");
        frontRightMotor = hardwareMap.get(DcMotor.class, "fr");
        backRightMotor = hardwareMap.get(DcMotor.class, "br");
        shooterLeftMotor = hardwareMap.get(DcMotor.class, "sl");
        shooterRightMotor = hardwareMap.get(DcMotor.class, "sr");
        Intake = hardwareMap.get(DcMotor.class, "Intake");
        Flicker = hardwareMap.get(Servo.class, "Flicker");
        Conveyor = hardwareMap.get(Servo.class, "Conveyor");
        HookOpen = hardwareMap.get(Servo.class, "HookOpen");
        HookTurn = hardwareMap.get(Servo.class, "HookTurn");
        HookBottomTurn = hardwareMap.get(Servo.class, "HookBottomTurn");
        IntakeServo = hardwareMap.get(Servo.class, "IntakeServo");
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
        HookBottomTurn.setPosition(75);

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

            //sets the speed of the drive motors

            backLeftMotor.setPower(p1);
            frontLeftMotor.setPower(p2);
            frontRightMotor.setPower(p3);
            backRightMotor.setPower(p4);

            // Display the current value of the drive motor's speed

            telemetry.addData("backLeftMotor_speed:", "%5.2f", p1);
            telemetry.addData("frontLeftMotor_speed:", "%5.2f", p2);
            telemetry.addData("frontRightMotor_speed:", "%5.2f", p3);
            telemetry.addData("backRightMotor_speed:", "%5.2f", p4);
            telemetry.update();

            //shooting program starts

            // turning the shooting motors on

            if(gamepad1.b) {
                shooterRightMotor.setPower(0.5);
                shooterLeftMotor.setPower(0.5);
                Flicker.setPosition(130);
                sleep(500);
                Flicker.setPosition(0);
            }

            if (gamepad1.x) {
                Intake.setPower(1);
                sleep(200);
            }
            else if(gamepad1.y) {
                Intake.setPower(-1);
                sleep(200);
            }
            if (gampad1.y && !IntakeClose) {
                IntakeServo.setPosition(90);
            }
            else if(gamepad1.y && IntakeClose) {
                IntakeServo.setPosition(45);
            }

            // Shooter program ends

            //Storage movement code starts

            //The conveyor goes down

            if(gamepad1.dpad_down) {
                Conveyor.setPosition(0);
            }

            //The conveyor goes up

            else if(gamepad1.dpad_up) {
                Conveyor.setPosition(120);
            }

            //Storage movement code stops

            //Hook code starts

            //Opens the hook

            if(gamepad1.dpad_left) {
                HookOpen.setPosition(90);
            }

            //Closes the hook

            else if(gamepad1.dpad_right ){
                HookOpen.setPosition(0);
            }

            if(gamepad1.right_bumper) {
                HookTurn.setPosition(20);
            }
            else if(gamepad1.left_bumper) {
                HookTurn.setPosition(0);

            }
            if(gamepad1.dpad_up) {
                HookBottomTurn.setPosition(90);
            }
            //Hook code stops



        }

        //Stoping the drive motors

        backLeftMotor.setPower(0.0);
        frontLeftMotor.setPower(0.0);
        frontRightMotor.setPower(0.0);
        backRightMotor.setPower(0.0);
    }
}