package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

/**
 * This is if we use all the motors
 */
@TeleOp(name ="RobotTellyOp", group = "Telly-Op")
public class  RobotTellyOp extends LinearOpMode {
    static final double INCREMENT   = .01;     // amount to slew servo each CYCLE_MS cycle
    static final int    CYCLE_MS    =   50;     // period of each cycle
    static final double MAX_POS     =  1.0;     // Maximum rotational position
    static final double MIN_POS     =  0.0;     // Minimum rotational position

    // Define class members

    double  position = (MAX_POS - MIN_POS)/5; // Start at halfway position
    boolean rampUp = true;


    public void runOpMode() {
        DcMotor m1 = hardwareMap.dcMotor.get("bl");
        DcMotor m2 = hardwareMap.dcMotor.get("fl");
        DcMotor m3 = hardwareMap.dcMotor.get("fr");
        DcMotor m4 = hardwareMap.dcMotor.get("br");
        DcMotor m5 = hardwareMap.dcMotor.get("sl");
        DcMotor m6 = hardwareMap.dcMotor.get("sr");
        Servo s1 = hardwareMap.servo.get("LinearServo");
        m1.setDirection(DcMotor.Direction.REVERSE);
        m2.setDirection(DcMotor.Direction.REVERSE);
        m5.setDirection(DcMotor.Direction.REVERSE);
        m1.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        m2.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        m3.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        m4.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        m1.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        m2.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        m3.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        m4.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
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
            m1.setPower(p1);
            m2.setPower(p2);
            m3.setPower(p3);
            m4.setPower(p4);
            if(gamepad1.b) {
                m5.setPower(1.0);
                m6.setPower(1.0);
            }
            else{
                m5.setPower(0.0);
                m6.setPower(0.0);
            }
            if (rampUp && gamepad1.dpad_up) {
                // Keep stepping up until we hit the max value.
                position += INCREMENT;
                if (position >= MAX_POS ) {
                    position = MAX_POS;
                    rampUp = !rampUp;   // Switch ramp direction
                }
            } else if(!rampUp && gamepad1.dpad_down) {
                // Keep stepping down until we hit the min value.
                position -= INCREMENT;
                if (position <= MIN_POS) {

                    position = MIN_POS;
                    rampUp = !rampUp;  // Switch ramp direction
                }
            }

            // Display the current value
            telemetry.addData("Servo Position", "%5.2f", position);
            telemetry.addData(">", "Press Stop to end test.");
            telemetry.update();

            // Set the servo to the new position and pause
            s1.setPosition(position);
            sleep(CYCLE_MS);
            idle();
        }
        m1.setPower(0.0);//Stopping the motors: Start.
        m2.setPower(0.0);
        m3.setPower(0.0);
        m4.setPower(0.0);//Stopping the motors: End.
        telemetry.addData(">", "Done");//Showing the task done on DS.
        telemetry.update();// Updates the console.
    }
}

