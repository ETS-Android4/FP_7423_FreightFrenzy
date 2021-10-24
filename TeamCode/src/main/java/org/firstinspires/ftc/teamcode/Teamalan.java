package org.firstinspires.ftc.teamcode;

import static org.firstinspires.ftc.robotcore.external.BlocksOpModeCompanion.telemetry;

import android.util.Log;

import com.qualcomm.hardware.bosch.BNO055IMU;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;

import org.firstinspires.ftc.robotcore.external.navigation.Orientation;
import org.firstinspires.ftc.teamcode.ImageNavigation;

@Autonomous(name="Get Duckies", group = "none")

public class Teamalan extends LinearOpMode {

    public DcMotor fl;
    public DcMotor fr;
    public DcMotor bl;
    public DcMotor br;

    public float PPR = 1120;

    public float diameter = 4;

    public MyIMU imu;

    public ImageNavigation imageNavigation;

    public void initialize (){
        fl = hardwareMap.dcMotor.get("frontleft");
        fr = hardwareMap.dcMotor.get("frontright");
        bl = hardwareMap.dcMotor.get("backleft");
        br = hardwareMap.dcMotor.get("backright");

        fl.setDirection(DcMotorSimple.Direction.REVERSE);
        bl.setDirection(DcMotorSimple.Direction.REVERSE);

        imu = new MyIMU(hardwareMap);
        BNO055IMU.Parameters p = new BNO055IMU.Parameters();
        imu.initialize(p);

        imageNavigation = new ImageNavigation(hardwareMap, this);
        imageNavigation.init();
    }

    public void StopAll() {
        fl.setPower(0);
        fr.setPower(0);
        bl.setPower(0);
        br.setPower(0);
    }

    public void Drive (float power, float distance) {
        float x = (PPR * distance)/(diameter * (float)Math.PI);

        int targetEncoderValue = Math.round(x);

        bl.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        bl.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        int currentPosition = 0;

        while (currentPosition < targetEncoderValue && opModeIsActive()) {
            currentPosition = Math.abs(bl.getCurrentPosition());
            fl.setPower(power);
            fr.setPower(power);
            bl.setPower(power);
            br.setPower(power);
        }

        StopAll();

    }

    public void Turn (float power, int angle, Direction turnDirection, MyIMU imu) {


        Orientation startOrientation = imu.getAngularOrientation();

        float targetangle;
        float currentangle;

        imu.reset(turnDirection);
        if (turnDirection == Direction.COUNTERCLOCKWISE) {

            targetangle = startOrientation.firstAngle + angle;
            currentangle = startOrientation.firstAngle;

            while (currentangle < targetangle && opModeIsActive()) {

                currentangle = imu.getAdjustedAngle();

                Log.i("[pheonix:angleInfo]", String.format("startingAngle = %f, targetAngl = %f, currentAngle = %f", startOrientation.firstAngle, targetangle, currentangle));

                fl.setPower(-power);
                bl.setPower(-power);
                fr.setPower(power);
                br.setPower(power);
            }
        }

        else {

            targetangle = startOrientation.firstAngle - angle;
            currentangle = startOrientation.firstAngle;

            while (currentangle > targetangle && opModeIsActive()) {

                currentangle = imu.getAdjustedAngle();

                this.telemetry.addData("CurrentAngle =: %f", currentangle);
                this.telemetry.update();

                fl.setPower(power);
                bl.setPower(power);
                fr.setPower(-power);
                br.setPower(-power);


            }
        }

        StopAll();

    }

    public void Strafe(float power, float distance, Direction d) {
        float x = (PPR * (2 * distance))/(diameter * (float)Math.PI);

        int targetEncoderValue = Math.round(x);

        bl.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        bl.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        int currentPosition = 0;

        if (d == Direction.LEFT) {
            while (currentPosition < targetEncoderValue && opModeIsActive()) {
                currentPosition = Math.abs(bl.getCurrentPosition());
                fl.setPower(power);
                fr.setPower(-power);
                bl.setPower(-power);
                br.setPower(power);
            }
        } else {
            while (currentPosition < targetEncoderValue && opModeIsActive()) {
                currentPosition = Math.abs(bl.getCurrentPosition());
                fl.setPower(-power);
                fr.setPower(power);
                bl.setPower(power);
                br.setPower(-power);
            }
        }

        StopAll();
    }

    @Override
    public void runOpMode() throws InterruptedException {
        initialize();
        waitForStart();
        while (opModeIsActive()) {
            if(imageNavigation != null){
//                int duckPos = imageNavigation.getDuckies();
//                telemetry.addData("ducky: %d", duckPos);
//                telemetry.update();
//                sleep(500);
                imageNavigation.getPosition();
            }
        }
    }
}
