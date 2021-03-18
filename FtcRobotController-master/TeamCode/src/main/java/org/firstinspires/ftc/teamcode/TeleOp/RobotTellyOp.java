package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.util.Range;
import java.util.Set;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

/** This is the final Telly-Op Code
 * Buttons in use:
 * Left Bumper(Moves the Flicker Servo)
 * Right Bumper(Turn the shooter the on)
 * D-Pad up(Brings the intake up)
 * D-Pad down(Brings the intake down)
 * D-Pad right(Moves the WG arm (elbow) up)
 * D-Pad left (Moves the WG arm (elbow) down)
 * b (Holding the (b) will open the intake claws and when released closes the claws)
 * y (Holding the (y) will open the arm claws and when released closes the claws)
 * x (Brings the conveyor down then the intake goes down then the claws open and close, afterwards the intake goes back
 * up... )
 * left stick(It moves and strafes the robot)
 * right stick(It will turn the robot)
 * right trigger (It brings the conveyor down)
 * left trigger (It brings the conyeor up)
 */
@TeleOp(name ="Robot Tele-Op", group = "Tele-Op")
public class  RobotTellyOp extends LinearOpMode {
    // Defines all the hardware.
    //Defines drivetrain motors
    private DcMotor frontLeftMotor;
    private DcMotor frontRightMotor;
    private DcMotor backLeftMotor;
    private DcMotor backRightMotor;
    // Defines shooter motors
    private DcMotor shooterLeftMotor;
    private DcMotor shooterRightMotor;
    // Defines intake motor
    private DcMotor Intake;
    // Defines servos
    private Servo Flicker;
    private Servo Conveyor;
    private Servo HookOpen;
    private Servo HookTurn;
    private Servo IntakeServo;
    // Boolean variables
    private boolean startIntakeProcess = false;
    private boolean CanOpenHook = true;
    private boolean CanIntakeOpen = true;
    // Intake Servo position variables
    private double IntakeClaw_HOME_POSITION = 0.0;
    private double IntakeClaw_MIN_RANGE = 0.0;
    private double IntakeClaw_MAX_RANGE = 0.25;
    private double IntakeClaw_SPEED = 0.05;
    private double IntakeClaw_POSITION = null;
    //Claw Servo position variables
    private double HookOpen_HOME_POSITION = 0.0;
    private double HookOpen_SPEED = 0.1;
    private double HookOpen_POSITION = null;
    //Arm Servo position variables
    private double HookTurn_HOME_POSITION = 0.0;
    private double HookTurn_SPEED = 0.1;
    private double HookTurn_POSITION = null;
    //Conveyor Servo position variables
    private double Conveyor_HOME_POSITION = 180;
    private double Conveyor_SPEED = -0.1;
    private double Conveyor_POSITION = null;

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
        //Drivetrain Motors
        backLeftMotor = hardwareMap.get(DcMotor.class, "bl");
        frontLeftMotor = hardwareMap.get(DcMotor.class, "fl");
        frontRightMotor = hardwareMap.get(DcMotor.class, "fr");
        backRightMotor = hardwareMap.get(DcMotor.class, "br");
        //Shooter Motors
        shooterLeftMotor = hardwareMap.get(DcMotor.class, "sl");
        shooterRightMotor = hardwareMap.get(DcMotor.class, "sr");
        // Intake Motor
        Intake = hardwareMap.get(DcMotor.class, "Intake");
        // Servos
        Flicker = hardwareMap.get(Servo.class, "Flicker");
        Conveyor = hardwareMap.get(Servo.class, "Conveyor");
        HookOpen = hardwareMap.get(Servo.class, "HookOpen");
        HookTurn = hardwareMap.get(Servo.class, "HookTurn");
        IntakeServo = hardwareMap.get(Servo.class, "IntakeServo");
        // Sets the direction of the motors
        backLeftMotor.setDirection(DcMotor.Direction.REVERSE);
        frontLeftMotor.setDirection(DcMotor.Direction.REVERSE);
        backRightMotor.setDirection(DcMotor.Direction.FORWARD);
        frontRightMotor.setDirection(DcMotor.Direction.FORWARD);
        shooterLeftMotor.setDirection(DcMotor.Direction.REVERSE);
        // Reset Servo Position
        IntakeServo.setPosition(IntakeClaw_HOME_POSITION);
        HookOpen.setPosition(HookOpen_HOME_POSITION);
        HookTurn.setPosition(HookTurn_HOME_POSITION);
        Conveyor.setPosition(Conveyor_HOME_POSITION);
        IntakeClaw_POSITION = IntakeClaw_HOME_POSITION;
        HookOpen_POSITION = HookOpen_HOME_POSITION;
        HookTurn_POSITION = HookTurn_HOME_POSITION;
        Conveyor_POSITION = Conveyor_HOME_POSITION;
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
            if(gamepad1.right_bumper) {
                // turning the shooting motors on
                shooterRightMotor.setPower(0.7);
                shooterLeftMotor.setPower(0.7);
            }
            else{
                shooterRightMotor.setPower(0.0);
                shooterLeftMotor.setPower(0.0);
            }
            if(gamepad1.left_bumper) {
                Flicker.setPosition(170);
                sleep(500);
                Flicker.setPosition(0);
            }
            if(gamepad1.x) {
                startIntakeProcess();
            }
            else{
                empty();
            }

            if (gamepad1.dpad_up) {
                Intake.setPower(-0.5);
                sleep(2000);
            }
            else if(gamepad1.dpad_down) {
                Intake.setPower(0.5);
                sleep(2000);
            }

            if(gamepad1.b){ // Holding 'B' button will open the intake claw
                IntakeClaw_POSITION += IntakeClaw_SPEED;
            }else if(gamepad1.x){// Holding 'X' button will close the intake claw
                IntakeClaw_POSITION -= IntakeClaw_SPEED;
            }
            // Ensures that intake claw position stays within the range.
            IntakeClaw_POSITION = scaleRange(0.0, 0.25);
            IntakeServo.setPosition(IntakeClaw_POSITION);
            telemetry.addData( "Intake claw position:", IntakeClaw_POSITION);
            telemetry.update();


            if(gamepad1.y) {// Holding 'Y' button will open the hook claw
                HookOpen_POSITION += HookOpen_SPEED;
            }else{ // Relasing 'Y' button will close the hook claw
                HookOpen_POSITION -= HookOpen_SPEED;
            }
            //Ensures that hook claw position stays within the range.
            HookOpen_POSITION = scaleRange(0.0, 0.111);
            HookOpen.setPosition(HookOpen_POSITION);
            telemetry.addData( "Hook claw position:", HookOpen_POSITION);
            telemetry.update();
            //Closes the hook


            if(gamepad1.dpad_left) {// Holding 'dpad_left' button will roll the arm down
                HookTurn_POSITION += HookTurn_SPEED;
            }
            else if(gamepad1.dpad_right) {// Holding 'dpad_right' button will roll the arm arm.
                HookTurn_POSITION -= HookTurn_SPEED;
            }
            //Ensures that hook arm position stays within the range.
            HookTurn_POSITION = scaleRange(0.0, 0.5);
            HookTurn.setPosition(HookTurn_POSITION);
            telemetry.addData( "Arm position:", HookTurn_POSITION);
            telemetry.update();

            //Hook code stops
            if(gamepad1.right_trigger > .01) {// Holding 'right_trigger' button will put the conveyor up
                Conveyor_POSITION += Conveyor_SPEED;
            }
            else if(gamepad1.left_trigger > .01){//Holding 'left_trigger' button will put the conveyor down
               Conveyor_POSITION -= Conveyor_SPEED;
            }
            //Ensures that conveyor position stays within the range.
            Conveyor_POSITION = scaleRange(0.25, 1);
            Conveyor.setPosition(Conveyor_POSITION);
            telemetry.addData( "Conveyor position:", Conveyor_POSITION);
            telemetry.update();

        }

        //Stoping the drive motors

        backLeftMotor.setPower(0.0);
        frontLeftMotor.setPower(0.0);
        frontRightMotor.setPower(0.0);
        backRightMotor.setPower(0.0);
    }
}