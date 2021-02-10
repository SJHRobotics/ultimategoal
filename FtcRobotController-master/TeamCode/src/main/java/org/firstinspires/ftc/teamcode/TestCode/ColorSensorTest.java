// Opmode for testing color sensor
package org.firstinspires.ftc.teamcode;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.ColorSensor;
@Autonomous(name = "ColorSensorTest", group = "ColorSensorTest")
public class ColorSensorTestCode extends LinearOpMode {
    private ColorSensor CSensor;
    
    private DcMotor frontLeftMotor;
    private DcMotor frontRightMotor;
    private DcMotor backLeftMotor;
    private DcMotor backRightMotor;
    public void MoveForward(){
       frontLeftMotor.setPower(-0.2);
       frontRightMotor.setPower(-0.2);
       backLeftMotor.setPower(-0.2);
       backRightMotor.setPower(-0.2);
       //sleep(time);
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
        frontLeftMotor.setDirection(DcMotor.Direction.REVERSE);
        backLeftMotor.setDirection(DcMotor.Direction.REVERSE);
        
        
        waitForStart();
        
        
        //MoveForward(2000);
        while(opModeIsActive()){
            telemetry.addData("Blue:", CSensor.blue());
            telemetry.addData("Red:", CSensor.red());
            telemetry.addData("Green:", CSensor.green());
            telemetry.update();
            
        
        }
        
        
        
        
        
    }
}
