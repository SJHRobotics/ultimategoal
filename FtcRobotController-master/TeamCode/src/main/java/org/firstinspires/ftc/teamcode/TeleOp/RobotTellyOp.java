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
    public boolean startIntakeProcess = false;

    public void startIntakeProcess(){
        Conveyor.setPosition(0);
        Intake.setPower(-0.3);
        sleep(500);
        IntakeServo.setPosition(90);
        sleep(250);
        IntakeServo.setPosition(0);
        sleep(250);
        Intake.setPower(0.3);
        sleep(500);
        IntakeServo.setPosition(90);
        sleep(250);
        IntakeServo.setPosition(0);
        sleep(250);
        Conveyor.setPosition(120);
    }
    public void empty(){
        Intake.setPower(0);
    }
    @Override
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
            if(gamepad1.b) {
                // turning the shooting motors on
                shooterRightMotor.setPower(0.5);
                shooterLeftMotor.setPower(0.5);
                Flicker.setPosition(130);
                sleep(150);
                Flicker.setPosition(0);
            }
            else{
                shooterRightMotor.setPower(0.0);
                shooterLeftMotor.setPower(0.0);
            }
            if(gamepad1.x) {
                startIntakeProcess();
            }
            else{
                empty();
            }

            /*if (gamepad1.dpad_up) {
                Intake.setPower(-0.3);
                sleep(2000);
            }
            else if(gamepad1.dpad_down) {
                Intake.setPower(0.2);
                sleep(2000);
            }
            if (gamepad1.y) {
                IntakeServo.setPosition(90);
            }
            else if(gamepad1.a) {
                IntakeServo.setPosition(0);
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
            }*/

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

            //Hook code stops



        }

        //Stoping the drive motors

        backLeftMotor.setPower(0.0);
        frontLeftMotor.setPower(0.0);
        frontRightMotor.setPower(0.0);
        backRightMotor.setPower(0.0);
    }
}