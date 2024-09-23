package com.farminfinity.farminfinity.Helper;

public class EndPoints {
    //private static final String ROOT_URL = "http://192.168.43.230:5000/api/v1/";
    //private static final String ROOT_URL = "http://192.168.2.222:5000/api/v1/"; // test
    //private static final String ROOT_URL = "http://www.test.api.farmeasytechnologies.com/api/v1/"; // test
    private static final String ROOT_URL = "http://www.api.farmeasytechnologies.com/api/v1/"; // Prod
    //private static final String ROOT_URL = "http://192.168.50.222:5000/api/v1/"; // local
    //private static final String ROOT_URL = "http://192.168.98.137:5000/api/v1/"; // local
    //private static final String ROOT_URL = "http://192.168.124.137:5000/api/v1/";

    public static final String URL_GET_FPO = ROOT_URL + "fpo/get/list";
    public static final String URL_FARMER_GET_SHORT_INFO = ROOT_URL + "farmer/get/s/info/";
    public static final String URL_GET_AGENT_MAPPED_FPO = ROOT_URL + "agent/mapped/fpo/list/";
    public static final String URL_FPO_GET_FPO = ROOT_URL + "fpo/get/fpo/";

    public static final String URL_FARMER_SIGNUP = ROOT_URL + "farmer/signup";
    public static final String URL_FARMER_SIGNUP_SEND_OTP = ROOT_URL + "farmer/signup/send/otp";
    public static final String URL_FARMER_SIGNUP_VERIFY_OTP = ROOT_URL + "farmer/signup/verify/otp";
    public static final String URL_FARMER_LOGIN_SEND_OTP = ROOT_URL + "farmer/login/send/otp";
    public static final String URL_FARMER_LOGIN_VERIFY_OTP = ROOT_URL + "farmer/login/verify/otp";

    public static final String URL_FARMER_ADD_FORM_ONE = ROOT_URL + "farmer/add/form/one";
    public static final String URL_FARMER_ADD_FORM_TWO = ROOT_URL + "farmer/add/form/two";
    public static final String URL_FARMER_ADD_FORM_THREE = ROOT_URL + "farmer/add/form/three";
    public static final String URL_FARMER_ADD_FORM_FARM_AUTH = ROOT_URL + "farmer/add/form/farmAuth";
    public static final String URL_FARMER_ADD_FORM_END = ROOT_URL + "farmer/add/form/end";
    public static final String URL_FARMER_ADD_SUBSCRIPTION = ROOT_URL + "farmer/addScorecardSubscription";
    public static final String URL_FARMER_GET_REPORT = ROOT_URL + "farmer/getScorecard/";

    public static final String URL_FARMER_UPDATE_FORM_ONE = ROOT_URL + "farmer/update/form/one";
    public static final String URL_FARMER_UPDATE_FORM_TWO = ROOT_URL + "farmer/update/form/two";
    public static final String URL_FARMER_UPDATE_FORM_THREE = ROOT_URL + "farmer/update/form/three";
    public static final String URL_FARMER_UPDATE_FORM_FARM_AUTH = ROOT_URL + "farmer/update/form/farmAuth";
    public static final String URL_FARMER_UPDATE_FORM_END = ROOT_URL + "farmer/update/form/end";

    public static final String URL_FARMER_GET_LEVEL_ONE = ROOT_URL + "farmer/get/form/one";
    public static final String URL_FARMER_GET_LEVEL_TWO = ROOT_URL + "farmer/get/form/two";
    public static final String URL_FARMER_GET_LEVEL_THREE = ROOT_URL + "farmer/get/form/three";
    public static final String URL_FARMER_GET_FARM_AUTH = ROOT_URL + "farmer/get/form/farmAuth";
    public static final String URL_FARMER_GET_LEVEL_END = ROOT_URL + "farmer/get/form/end";
    public static final String URL_FARMER_GET_LOANS = ROOT_URL + "farmer/get/loans";
    public static final String URL_FARMER_GET_EMIS = ROOT_URL + "farmer/repayment/schedule/";

    public static final String URL_FARMER_PAY_EMI = ROOT_URL + "farmer/repay/borrower/loan";
    // FI
    public static final String URL_EMP_LOGIN = ROOT_URL + "emp/login";
    public static final String URL_EMP_FORGOT_PASSWORD_SEND_OTP = ROOT_URL + "emp/send/otp";
    public static final String URL_EMP_FORGOT_PASSWORD_VERIFY_OTP = ROOT_URL + "emp/forgot/password/verify/otp";
    public static final String URL_EMP_FORGOT_PASSWORD = ROOT_URL + "emp/forgot/password";
    public static final String URL_EMP_CHANGE_PASSWORD = ROOT_URL + "emp/change/password";
    public static final String URL_EMP_GET_ALL_ONBOARDED_FARMER = ROOT_URL + "emp/get/all/farmers/onboarded/";


    public static final String URL_EMP_SIGNUP_FARMER = ROOT_URL + "emp/signup/farmer";
    public static final String URL_EMP_ADD_FORM_ONE = ROOT_URL + "emp/add/farmer/form/one";
    public static final String URL_EMP_ADD_FORM_TWO = ROOT_URL + "emp/add/farmer/form/two";
    public static final String URL_EMP_ADD_FORM_THREE = ROOT_URL + "emp/add/farmer/form/three";
    public static final String URL_EMP_ADD_FORM_FARM_AUTH = ROOT_URL + "emp/add/farmer/form/farmAuth";
    public static final String URL_EMP_ADD_FORM_END = ROOT_URL + "emp/add/farmer/form/end";

    public static final String URL_EMP_UPDATE_FORM_ONE = ROOT_URL + "emp/update/farmer/form/one/";
    public static final String URL_EMP_UPDATE_FORM_TWO = ROOT_URL + "emp/update/farmer/form/two/";
    public static final String URL_EMP_UPDATE_FORM_THREE = ROOT_URL + "emp/update/farmer/form/three/";
    public static final String URL_EMP_UPDATE_FORM_FARM_AUTH = ROOT_URL + "emp/update/farmer/form/farmAuth/";
    public static final String URL_EMP_UPDATE_FORM_END = ROOT_URL + "emp/update/farmer/form/end/";

    public static final String URL_EMP_GET_LEVEL_ONE = ROOT_URL + "emp/get/farmer/form/one/";
    public static final String URL_EMP_GET_LEVEL_TWO = ROOT_URL + "emp/get/farmer/form/two/";
    public static final String URL_EMP_GET_LEVEL_THREE = ROOT_URL + "emp/get/farmer/form/three/";
    public static final String URL_EMP_GET_FARM_AUTH = ROOT_URL + "emp/get/farmer/form/farmAuth/";
    public static final String URL_EMP_GET_LEVEL_END = ROOT_URL + "emp/get/farmer/form/end/";
    public static final String URL_EMP_GET_BORROWER_LOAN = ROOT_URL + "emp/get/loans/";
    public static final String URL_EMP_GET_BORROWER_EMI = ROOT_URL + "emp/repayment/schedule/";

    // Agent
    public static final String URL_AGENT_LOGIN = ROOT_URL + "agent/login";
    public static final String URL_AGENT_FORGOT_PASSWORD_SEND_OTP = ROOT_URL + "agent/send/otp";
    public static final String URL_AGENT_FORGOT_PASSWORD_VERIFY_OTP = ROOT_URL + "agent/forgot/password/verify/otp";
    public static final String URL_AGENT_FORGOT_PASSWORD = ROOT_URL + "agent/forgot/password";
    public static final String URL_AGENT_CHANGE_PASSWORD = ROOT_URL + "agent/change/password";
    public static final String URL_AGENT_GET_ALL_ONBOARDED_FARMER = ROOT_URL + "agent/get/all/farmers/onboarded/";

    public static final String URL_AGENT_SIGNUP_FARMER = ROOT_URL + "agent/signup/farmer";
    public static final String URL_AGENT_ADD_FORM_ONE = ROOT_URL + "agent/add/farmer/form/one";
    public static final String URL_AGENT_ADD_FORM_TWO = ROOT_URL + "agent/add/farmer/form/two";
    public static final String URL_AGENT_ADD_FORM_THREE = ROOT_URL + "agent/add/farmer/form/three";
    public static final String URL_AGENT_ADD_FORM_FARM_AUTH = ROOT_URL + "agent/add/farmer/form/farmAuth";
    public static final String URL_AGENT_ADD_FORM_END = ROOT_URL + "agent/add/farmer/form/end";

    public static final String URL_AGENT_UPDATE_FORM_ONE = ROOT_URL + "agent/update/farmer/form/one/";
    public static final String URL_AGENT_UPDATE_FORM_TWO = ROOT_URL + "agent/update/farmer/form/two/";
    public static final String URL_AGENT_UPDATE_FORM_THREE = ROOT_URL + "agent/update/farmer/form/three/";
    public static final String URL_AGENT_UPDATE_FORM_FARM_AUTH = ROOT_URL + "agent/update/farmer/form/farmAuth/";
    public static final String URL_AGENT_UPDATE_FORM_END = ROOT_URL + "agent/update/farmer/form/end/";

    public static final String URL_AGENT_GET_LEVEL_ONE = ROOT_URL + "agent/get/farmer/form/one/";
    public static final String URL_AGENT_GET_LEVEL_TWO = ROOT_URL + "agent/get/farmer/form/two/";
    public static final String URL_AGENT_GET_LEVEL_THREE = ROOT_URL + "agent/get/farmer/form/three/";
    public static final String URL_AGENT_GET_FARM_AUTH = ROOT_URL + "agent/get/farmer/form/farmAuth/";
    public static final String URL_AGENT_GET_LEVEL_END = ROOT_URL + "agent/get/farmer/form/end/";
    public static final String URL_AGENT_GET_BORROWER_LOAN = ROOT_URL + "agent/get/borrower/loan/";
    public static final String URL_AGENT_GET_BORROWER_EMI = ROOT_URL + "agent/repayment/schedule/";
    // Fpo
    public static final String URL_FPO_LOGIN = ROOT_URL + "fpo/login";
    public static final String URL_FPO_FORGOT_PASSWORD_SEND_OTP = ROOT_URL + "fpo/send/otp";
    public static final String URL_FPO_FORGOT_PASSWORD_VERIFY_OTP = ROOT_URL + "fpo/forgot/password/verify/otp";
    public static final String URL_FPO_FORGOT_PASSWORD = ROOT_URL + "fpo/forgot/password";
    public static final String URL_FPO_CHANGE_PASSWORD = ROOT_URL + "fpo/change/password";

    public static final String URL_FPO_SIGNUP_FARMER = ROOT_URL + "fpo/signup/farmer";
    public static final String URL_FPO_ADD_FORM_ONE = ROOT_URL + "fpo/add/farmer/form/one";
    public static final String URL_FPO_ADD_FORM_TWO = ROOT_URL + "fpo/add/farmer/form/two";
    public static final String URL_FPO_ADD_FORM_THREE = ROOT_URL + "fpo/add/farmer/form/three";
    public static final String URL_FPO_ADD_FORM_FARM_AUTH = ROOT_URL + "fpo/add/farmer/form/farmAuth";
    public static final String URL_FPO_ADD_FORM_END = ROOT_URL + "fpo/add/farmer/form/end";

    public static final String URL_FPO_UPDATE_FORM_ONE = ROOT_URL + "fpo/update/farmer/form/one/";
    public static final String URL_FPO_UPDATE_FORM_TWO = ROOT_URL + "fpo/update/farmer/form/two/";
    public static final String URL_FPO_UPDATE_FORM_THREE = ROOT_URL + "fpo/update/farmer/form/three/";
    public static final String URL_FPO_UPDATE_FORM_FARM_AUTH = ROOT_URL + "fpo/update/farmer/form/farmAuth/";
    public static final String URL_FPO_UPDATE_FORM_END = ROOT_URL + "fpo/update/farmer/form/end/";

    public static final String URL_FPO_GET_LEVEL_ONE = ROOT_URL + "fpo/get/form/one/";
    public static final String URL_FPO_GET_LEVEL_TWO = ROOT_URL + "fpo/get/form/two/";
    public static final String URL_FPO_GET_LEVEL_THREE = ROOT_URL + "fpo/get/form/three/";
    public static final String URL_FPO_GET_FARM_AUTH = ROOT_URL + "farmer/get/form/farmAuth";
    public static final String URL_FPO_GET_LEVEL_END = ROOT_URL + "fpo/get/form/end/";
    // Bank representative
    public static final String URL_BANK_REP_LOGIN = ROOT_URL + "br/login";
    public static final String URL_BANK_REP_FORGOT_PASSWORD_SEND_OTP = ROOT_URL + "br/send/otp";
    public static final String URL_BANK_REP_FORGOT_PASSWORD_VERIFY_OTP = ROOT_URL + "br/forgot/password/verify/otp";
    public static final String URL_BANK_REP_FORGOT_PASSWORD = ROOT_URL + "br/forgot/password";
    public static final String URL_BANK_REP_CHANGE_PASSWORD = ROOT_URL + "br/change/password";
    public static final String URL_GET_ALL_ONBOARDED_FARMER_ASSIGNED_TO_BANK_REP = ROOT_URL + "br/get/all/farmers/assigned/";

    public static final String URL_BANK_REP_SIGNUP_FARMER = ROOT_URL + "br/signup/farmer";
    public static final String URL_BANK_REP_ADD_FORM_ONE = ROOT_URL + "br/add/farmer/form/one";
    public static final String URL_BANK_REP_ADD_FORM_TWO = ROOT_URL + "br/add/farmer/form/two";
    public static final String URL_BANK_REP_ADD_FORM_THREE = ROOT_URL + "br/add/farmer/form/three";
    public static final String URL_BANK_REP_ADD_FORM_FARM_AUTH = ROOT_URL + "br/add/farmer/form/farmAuth";
    public static final String URL_BANK_REP_GET_FARM_AUTH = ROOT_URL + "farmer/get/form/farmAuth";
    public static final String URL_BANK_REP_ADD_FORM_END = ROOT_URL + "br/add/farmer/form/end";

    public static final String URL_BANK_REP_UPDATE_FORM_ONE = ROOT_URL + "br/update/farmer/form/one/";
    public static final String URL_BANK_REP_UPDATE_FORM_TWO = ROOT_URL + "br/update/farmer/form/two/";
    public static final String URL_BANK_REP_UPDATE_FORM_THREE = ROOT_URL + "br/update/farmer/form/three/";
    public static final String URL_BANK_REP_UPDATE_FORM_FARM_AUTH = ROOT_URL + "br/update/farmer/form/farmAuth/";
    public static final String URL_BANK_REP_UPDATE_FORM_END = ROOT_URL + "br/update/farmer/form/end/";

    public static final String URL_BANK_REP_GET_LEVEL_ONE = ROOT_URL + "br/get/form/one/";
    public static final String URL_BANK_REP_GET_LEVEL_TWO = ROOT_URL + "br/get/form/two/";
    public static final String URL_BANK_REP_GET_LEVEL_THREE = ROOT_URL + "br/get/form/three/";
    public static final String URL_BANK_REP_GET_LEVEL_END = ROOT_URL + "br/get/form/end/";


    public static final String URL_SEARCH_PHONE = ROOT_URL + "farmer/search-by-phone/";
    public static final String URL_SEARCH_NAME = ROOT_URL + "farmer/search-by-name/";

    public static final String URL_PG_ORDER = ROOT_URL + "pg/order/";
    public static final String URL_PG_LINK = ROOT_URL + "pg/link/";

    public static final String URL_PG_EMI_ORDER = ROOT_URL + "pg/order/emi/";

    public static final String URL_PG_PP_ORDER = ROOT_URL + "pg/order/pp/";

    public static final String URL_PG_EMI_LINK = ROOT_URL + "pg/link/emi/";

    public static final String URL_OCR_FARM_PHOTO = ROOT_URL + "ocr/farmPhoto";
    public static final String URL_FACE_RECOGNITION = ROOT_URL + "recognition/face";

}
