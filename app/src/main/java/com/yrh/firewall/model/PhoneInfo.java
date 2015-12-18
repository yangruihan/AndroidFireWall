package com.yrh.firewall.model;

/**
 * Created by Yrh on 2015/12/13.
 */
public class PhoneInfo {

    private String phoneNum;
    private int inIncommingBlack;
    private int inSMSBlack;

    public  PhoneInfo(String phoneNum, int inIncommingBlack, int inSMSBlack) {
        this.phoneNum = phoneNum;
        this.inIncommingBlack = inIncommingBlack;
        this.inSMSBlack = inSMSBlack;
    }

    public void setPhoneNum(String phoneNum) {
        this.phoneNum = phoneNum;
    }

    public void setInIncommingBlack(int inIncommingBlack) {
        this.inIncommingBlack = inIncommingBlack;
    }

    public void setInSMSBlack(int inSMSBlack) {
        this.inSMSBlack = inSMSBlack;
    }

    public String getPhoneNum() {
        return phoneNum;
    }

    public int getInIncommingBlack() {
        return inIncommingBlack;
    }

    public int getInSMSBlack() {
        return inSMSBlack;
    }
}
