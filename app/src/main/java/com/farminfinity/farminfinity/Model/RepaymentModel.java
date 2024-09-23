package com.farminfinity.farminfinity.Model;

public class RepaymentModel {
    private String loan_id;
    private int emi_number;
    private int amount;
    private String repayment_date;
    private boolean status;

    public RepaymentModel() {
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

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public String getRepayment_date() {
        return repayment_date;
    }

    public void setRepayment_date(String repayment_date) {
        this.repayment_date = repayment_date;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }
}
