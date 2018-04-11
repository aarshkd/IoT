package com.example.kapil.firebaseproject;

/**
 * Created by Kapil on 4/10/2018.
 */

public class CurrentMotorReading {
    private Long Current;
    private Long Power;
    private Long encoderRPM;
    private Long userRPM;

    public CurrentMotorReading(Long Current,Long Power,Long encoderRPM,Long userRPM)
    {
        this.Current = Current;
        this.Power = Power;
        this.encoderRPM = encoderRPM;
        this.userRPM = userRPM;
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

    public Long getPower() {
        return Power;
    }

    public void setPower(Long power) {
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
}
