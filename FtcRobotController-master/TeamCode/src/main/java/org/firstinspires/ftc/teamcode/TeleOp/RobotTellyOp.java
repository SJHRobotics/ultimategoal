package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.hardware.DcMotor;
import java.util.Set;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

/** This is the final Telly-Op Code
 * Buttons in use
 * Left Bumper
 * Right Bumper
 * D-Pad up
 * D-Pad down
 * D-Pad right
 * D-Pad left
 * b
 * y
 * a
 * x
 * left stick
 * right stick
 * right trigger
 */
@TeleOp(name ="Test", group = "Telly-Op")
public class  LinearServo extends LinearOpMode {
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
    private boolean startIntakeProcess = false;
    private boolean CanOpenHook = true;
    private boolean CanIntakeOpen = true;
    private int ServoPosition = 0;
    private int SetPosition = 0;
    public void startIntakeProcess(){
        Conveyor.setPosition(250);
        sleep(500);
        Intake.setPower(-0.5);
        sleep(500);
        IntakeServo.setPosition(215);
        sleep(1000);
        IntakeServo.setPosition(0);
        sleep(500);
        Intake.setPower(0.3);
        sleep(1000);
        IntakeServo.setPosition(215);
        sleep(500);
        IntakeServo.setPosition(0);
        sleep(500);
        Conveyor.setPosition(50);
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
        waitForStart();





        while (opModeIsActive()) {

            double px = gamepad1.left_stick_x;
            double py = -gamepad1.left_stick_y;
            double pa = (gamepad1.right_stick_x - gamepad1.right_stick_y);
            if (Math.abs(pa) < 0.05) pa = 0;
            double p1 = -px + py + pa;
            double p2 = px + py + pa;
            double p3 = -px + py - pa;
            double p4 = px + py - pa;
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
                Conveyor.setPosition(80);

            }else if(gamepad1.a) {
                Conveyor.setPosition(180);

            }


        }

        //Stoping the drive motors

        backLeftMotor.setPower(0.0);
        frontLeftMotor.setPower(0.0);
        frontRightMotor.setPower(0.0);
        backRightMotor.setPower(0.0);
    }
}