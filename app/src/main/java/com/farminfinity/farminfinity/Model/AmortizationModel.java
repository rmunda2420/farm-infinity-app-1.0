package com.farminfinity.farminfinity.Model;

public class AmortizationModel {
    private String loan_id;
    private int emi_number;
    private float emi_amount;
    private String due_date;
    public AmortizationModel() {
    }

    public String getLoan_id() {
        return loan_id;
    }

    public void setLoan_id(String loan_id) {
        this.loan_id = loan_id;
    }

    public int getEmi_number() {
        return emi_number;
    }

    public void setEmi_number(int emi_number) {
        this.emi_number = emi_number;
    }

    public float getEmi_amount() {
        return emi_amount;
    }

    public void setEmi_amount(float emi_amount) {
        this.emi_amount = emi_amount;
    }

    public String getDue_date() {
        return due_date;
    }

    public void setDue_date(String due_date) {
        this.due_date = due_date;
    }
}
