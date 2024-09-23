package com.farminfinity.farminfinity.Model;

public class EmiModel {
    private String loan_id;
    private int emi_number;
    private double amount;
    private String due_date;
    private boolean status;

    public EmiModel() {
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

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getDue_date() {
        return due_date;
    }

    public void setDue_date(String due_date) {
        this.due_date = due_date;
    }

    public boolean getStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }
}
