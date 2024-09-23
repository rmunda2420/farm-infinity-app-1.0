package com.farminfinity.farminfinity.Model;

public class FarmerModel {
    private String id;
    private String app_id;
    private String f_name;
    private String m_name;
    private String l_name;
    private String ph_no;
    private String village;
    private String city_tow;
    private String loan_amt_sought;
    private int status;
    private String created_at;
    private String modified_at;

    public FarmerModel() {
    }

    public FarmerModel(String id, String app_id, String f_name, String m_name, String l_name, String ph_no, String village, String city_tow, String loan_amt_sought, int status, String created_at, String modified_at) {
        this.id = id;
        this.app_id = app_id;
        this.f_name = f_name;
        this.m_name = m_name;
        this.l_name = l_name;
        this.ph_no = ph_no;
        this.village = village;
        this.city_tow = city_tow;
        this.loan_amt_sought = loan_amt_sought;
        this.status = status;
        this.created_at = created_at;
        this.modified_at = modified_at;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getApp_id() {
        return app_id;
    }

    public void setApp_id(String app_id) {
        this.app_id = app_id;
    }

    public String getF_name() {
        return f_name;
    }

    public void setF_name(String f_name) {
        this.f_name = f_name;
    }

    public String getM_name() {
        return m_name;
    }

    public void setM_name(String m_name) {
        this.m_name = m_name;
    }

    public String getL_name() {
        return l_name;
    }

    public void setL_name(String l_name) {
        this.l_name = l_name;
    }

    public String getPh_no() {
        return ph_no;
    }

    public void setPh_no(String ph_no) {
        this.ph_no = ph_no;
    }

    public String getVillage() {
        return village;
    }

    public void setVillage(String village) {
        this.village = village;
    }

    public String getCity_tow() {
        return city_tow;
    }

    public void setCity_tow(String city_tow) {
        this.city_tow = city_tow;
    }

    public String getLoan_amt_sought() {
        return loan_amt_sought;
    }

    public void setLoan_amt_sought(String loan_amt_sought) {
        this.loan_amt_sought = loan_amt_sought;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getCreated_at() {
        return created_at;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }

    public String getModified_at() {
        return modified_at;
    }

    public void setModified_at(String modified_at) {
        this.modified_at = modified_at;
    }
}