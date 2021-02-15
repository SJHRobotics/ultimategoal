// Opmode for testing movement
package org.firstinspires.ftc.teamcode;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.hardware.DcMotor;
@Autonomous(name = "ColorSensorTest", group = "ColorSensorTest")
public class ColorSensorTestCode extends LinearOpMode {
    private DcMotor frontLeftMotor;
    private DcMotor frontRightMotor;
    private DcMotor backLeftMotor;
    private DcMotor backRightMotor;
    public void MoveForward(){
        frontLeftMotor.setPower(1.0);
        frontRightMotor.setPower(1.0);
        backLeftMotor.setPower(1.0);
        backRightMotor.setPower(1.0);
    }
    public void MoveBackward(){
        frontLeftMotor.setPower(-1.0);
        frontRightMotor.setPower(-1.0);
        backLeftMotor.setPower(-1.0);
        backRightMotor.setPower(-1.0);
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

        backRightMotor = hardwareMap.get(DcMotor.class, "br");
        backLeftMotor = hardwareMap.get(DcMotor.class, "bl");
        frontRightMotor = hardwareMap.get(DcMotor.class, "fr");
        frontLeftMotor = hardwareMap.get(DcMotor.class, "fl");
        frontLeftMotor.setDirection(DcMotor.Direction.REVERSE);
        backLeftMotor.setDirection(DcMotor.Direction.REVERSE);


        waitForStart();

        MoveForward();
        sleep(1000);

        MoveBackward();
        sleep(1000);

        //500 = 90 degrees, 1000 = 180 degrees
        TurnLeft(500);
        TurnRight(500);

    }
}
