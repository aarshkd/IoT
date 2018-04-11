package com.example.kapil.firebaseproject;

/**
 * Created by Kapil on 3/17/2018.
 */

public class User {

    private String MotorNumber;
    private String RPM_Value;

    public User() {
    }

    public User(String motorNumber, String RPM_Value) {
        MotorNumber = motorNumber;
        this.RPM_Value = RPM_Value;
    }

    public String getMotorNumber() {
        return MotorNumber;
    }

    public void setMotorNumber(String motorNumber) {
        MotorNumber = motorNumber;
    }

    public String getRPM_Value() {
        return RPM_Value;
    }

    public void setRPM_Value(String RPM_Value) {
        this.RPM_Value = RPM_Value;
    }
}
