package com.example.kapil.motor_iot;

/**
 * Created by Kapil on 5/2/2018.
 */

public class CurrentMotorReading {

    private Long Current;
    private float Power;
    private Long encoderRPM;
    private Long userRPM;
    private Long PID;
    private Long Switch;

    public CurrentMotorReading(Long Current,float Power,Long encoderRPM,Long userRPM, Long PID, Long Switch)
    {
        this.Current = Current;
        this.Power = Power;
        this.encoderRPM = encoderRPM;
        this.userRPM = userRPM;
        this.PID = PID;
        this.Switch = Switch;
    }

    public CurrentMotorReading()
    {

    }


    public Long getCurrent() {
        return Current;
    }

    public void setCurrent(Long current) {
        Current = current;
    }

    public float getPower() {
        return Power;
    }

    public void setPower(float power) {
        Power = power;
    }

    public Long getEncoderRPM() {
        return encoderRPM;
    }

    public void setEncoderRPM(Long encoderRPM) {
        this.encoderRPM = encoderRPM;
    }

    public Long getUserRPM() {
        return userRPM;
    }

    public void setUserRPM(Long userRPM) {
        this.userRPM = userRPM;
    }

    public Long getPID() {
        return PID;
    }

    public void setPID(Long PID) {
        this.PID = PID;
    }

    public Long getSwitch() {
        return Switch;
    }

    public void setSwitch(Long Switch) {
        this.Switch = Switch;
    }
}
