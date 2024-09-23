package com.farminfinity.farminfinity.Fragments;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.nfc.Tag;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContract;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.AppCompatAutoCompleteTextView;
import androidx.cardview.widget.CardView;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.provider.MediaStore;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkError;
import com.android.volley.NetworkResponse;
import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.auth0.android.jwt.Claim;
import com.auth0.android.jwt.JWT;
import com.baoyachi.stepview.HorizontalStepView;
import com.baoyachi.stepview.bean.StepBean;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.target.Target;
import com.bumptech.glide.request.transition.Transition;
import com.ceylonlabs.imageviewpopup.ImagePopup;
import com.farminfinity.farminfinity.Activities.ImagePickerActivity;
import com.farminfinity.farminfinity.Activities.LoginActivity;
import com.farminfinity.farminfinity.App.AppController;
import com.farminfinity.farminfinity.Helper.AlertDialogManager;
import com.farminfinity.farminfinity.Helper.EndPoints;
import com.farminfinity.farminfinity.Helper.InputValidation;
import com.farminfinity.farminfinity.Helper.SessionManager;
import com.farminfinity.farminfinity.Helper.VolleyMultipartRequest;
import com.farminfinity.farminfinity.Model.DataPart;
import com.farminfinity.farminfinity.R;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static android.app.Activity.RESULT_OK;

public class EditLevelTwoFragment extends BaseFragment implements View.OnClickListener {
    private static final String TAG = "EditLevelTwoFragment";

    private String userType = null;
    private String token = null;
    private int intUserType;
    private String loginId = null;
    private boolean isAgentMappedToFpo;

    private String fId = null;
    private boolean isLevelDone = false;

    private String f_name = null;
    private String m_name = null;
    private String l_name = null;
    private String dob = null;

    private SimpleDateFormat sdf2 = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());

    private static final int CROP_PASSPORT_PHOTO_IMAGE_REQUEST = 108;
    private static final int CROP_PASSBOOK_IMAGE_REQUEST = 109;
    private static final int CROP_ID_PROOF_IMAGE_REQUEST = 111;
    private static final int CROP_ADDRESS_PROOF_IMAGE_REQUEST = 112;
    private static final int CROP_ADDRESS_PROOF_IMAGE_REQUEST_BACK = 114;

    private String urlImagePassportSizePhoto = "";
    private String urlImagePassbook = "";
    private String urlImageIdProofFront = "";
    private String urlImageIdProofBack = "";
    private String urlImageAddressProofFront = "";
    private String urlImageAddressProofBack = "";
    // Added so that first time url is chosen instead of filepath in popup image(20-07-2022)
    private boolean isFirstTimePhotoAttach = false;

    private Uri filePath1, filePath2, filePath3, filePath4, filePath5, filePath6;

    private CoordinatorLayout coordinatorLayout;

    private ProgressBar pbAccNoValidation;
    private ProgressBar pbPoiValidation;
    private ProgressBar pbPoaValidation;

    private ImageView ivAccNoValid;
    private ImageView ivPoiValid;
    private ImageView ivPoaValid;

    private TextInputLayout tilFpoCode;
    private TextInputLayout tilFPOName;
    private TextInputLayout tilNoFamilyMember;
    private TextInputLayout tilDbtAccNo;
    private TextInputLayout tilDbtBankName;
    private TextInputLayout tilDbtIfsc;
    private TextInputLayout tilKLoanAccNo;
    private TextInputLayout tilKLoanBankName;
    private TextInputLayout tilKIfsc;
    private TextInputLayout tilKLoanAmount;
    private TextInputLayout tilKLoanObligation;
    private TextInputLayout tilOtherLoanAccNo;
    private TextInputLayout tilOtherLoanBankName;
    private TextInputLayout tilOtherIfsc;
    private TextInputLayout tilOtherLoanAmount;
    private TextInputLayout tilOtherLoanObligation;
    private TextInputLayout tilIdProofNo;
    private TextInputLayout tilAddressProofNo;

    private TextInputEditText tietFpoCode;
    private AppCompatAutoCompleteTextView actvFPOName;
    private TextInputEditText tietNoFamilyMember;
    private TextInputEditText tietDbtAccNo;
    private AppCompatAutoCompleteTextView actvDbtBankName;
    private TextInputEditText tietDbtIfsc;
    private TextInputEditText tietKLoanAccNo;
    private AppCompatAutoCompleteTextView actvKLoanBankName;
    private TextInputEditText tietKIfsc;
    private TextInputEditText tietKLoanAmount;
    private TextInputEditText tietKLoanObligation;
    private TextInputEditText tietOtherLoanAccNo;
    private AppCompatAutoCompleteTextView actvOtherLoanBankName;
    private TextInputEditText tietOtherIfsc;
    private TextInputEditText tietOtherLoanAmount;
    private TextInputEditText tietOtherLoanObligation;
    private TextInputEditText tietIdProofNo;
    private TextInputEditText tietAddressProofNo;

    private CardView cvBtnAttachPassportPhoto, cvBtnAttachPassbook, cvBtnAttachIdProof, cvBtnAttachAddressProof, cvBtnAttachAddressProofBack;

    private ImageView ivViewPassportPhoto, ivViewPassbook, ivViewPan, ivViewIdProof, ivViewAddressProof, ivViewAddressProofBack;

    private Spinner spinnerKccLoanType, spinnerKccLoanTenure, spinnerOtherLoanType, spinnerOtherLoanTenure, spinnerIdProof, spinnerAddressProof;

    private LinearLayout llGbQKLoan, llGbOtherLoan, llFPO, llPan;

    private CheckBox cbPanCard, cbFpoNil;

    private TextView tvLblUploadPassportPhoto, tvLblUploadPassbook, tvLblUploadIdProof, tvLblUploadAddressProof, tvLblUploadAddressProofBack;
    ;

    private RadioGroup radGroupQKloan, radGroupQClInsurance, radGroupQOtherLoan, radioGroupQFPO, radioGroupQContact;

    private CardView cvBtnSaveAndContinue;

    private HorizontalStepView horizontalStepView;

    private SessionManager mSessionManager;
    private InputValidation mInputValidation;
    private AlertDialogManager mAlert;

    private boolean qFPO = false;
    private boolean qKLoan = false;
    private boolean qClInsuranceLoan = false;
    private boolean qOtherLoan = false;
    private boolean qPanAvailable = false;
    private boolean qContact = false;

    // Karza validation
    private boolean isAccNoValid = false;//Change to true in debug
    private boolean isPanValid = false;
    private boolean isPoiValid = false;
    private boolean isPoaValid = false;

    private String accVerifyId = "";
    private String panVerifyId = "";
    private String poiVerifyId = "";
    private String poaVerifyId = "";

    private boolean isAttachedPassportSizePhoto, isAttachedPassbook, isAttachedPan, isAttachedIdProof, isAttachedAddressProof, isAttachedAddressProofBack = false;

    private Bitmap bitmapPassportSizePhoto, bitmapPassbook, bitmapPan, bitmapIdProof, bitmapAddressProof, bitmapAddressProofBack = null;

    private ImagePopup imagePopupPassportPhoto, imagePopupPassbook, imagePopupPan, imagePopupIdFront, imagePopupAddressProofFront, imagePopupAddressProofBack;

    private List<String> lstBankName;
    private List<String> lstBankNameB;
    private List<String> lstFpoName = new ArrayList<>();
    private List<String> lstFPOCode = new ArrayList<>();

    private Map<String, String> mapFpo = new HashMap();

    public EditLevelTwoFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_edit_level_two, container, false);

        // Get farmer id from previous fragment
        fId = getArguments().getString("FID");
        f_name = getArguments().getString("F_NAME");
        m_name = getArguments().getString("M_NAME");
        l_name = getArguments().getString("L_NAME");
        dob = getArguments().getString("DOB");

        // Initialize objects
        mAlert = new AlertDialogManager();
        mSessionManager = new SessionManager(getActivity());
        mInputValidation = new InputValidation(getActivity());

        // Get shared preference value
        if (mSessionManager.isLoggedIn()) {
            HashMap<String, String> userInfo = mSessionManager.getUserDetails();
            userType = userInfo.get("UserType");
            token = userInfo.get("Token");
        }

        // Get claims
        try {
            if (userType.equals("officer")) {
                JWT jwt = new JWT(token);
                Map<String, Claim> allClaims = jwt.getClaims();
                loginId = allClaims.get("login_id").asString();
                intUserType = allClaims.get("int_user_type").asInt();
                if (intUserType == 2)
                    isAgentMappedToFpo = allClaims.get("is_mapped").asBoolean();
            }
        } catch (Exception e) {
            Toast.makeText(getActivity(), e.toString(), Toast.LENGTH_SHORT).show();
        }

        coordinatorLayout = (CoordinatorLayout) view.findViewById(R.id.cl_fragment_edit_level_two);

        ivAccNoValid = (ImageView) view.findViewById(R.id.iv_viewAccNo_valid_fragment_edit_level_two);
        pbAccNoValidation = (ProgressBar) view.findViewById(R.id.pBar_acc_no_fragment_edit_level_two);
        pbPoiValidation = (ProgressBar) view.findViewById(R.id.pBar_poi_fragment_edit_level_two);
        pbPoaValidation = (ProgressBar) view.findViewById(R.id.pBar_poa_fragment_edit_level_two);

        ivAccNoValid = (ImageView) view.findViewById(R.id.iv_viewAccNo_valid_fragment_edit_level_two);
        ivPoiValid = (ImageView) view.findViewById(R.id.iv_viewPoi_valid_fragment_edit_level_two);
        ivPoaValid = (ImageView) view.findViewById(R.id.iv_viewPoa_valid_fragment_edit_level_two);

        tilFpoCode = (TextInputLayout) view.findViewById(R.id.til_fpoCode_fragment_edit_level_two);
        tilFPOName = (TextInputLayout) view.findViewById(R.id.til_FPOName_fragment_edit_level_two);
        tilNoFamilyMember = (TextInputLayout) view.findViewById(R.id.til_familyNo_fragment_edit_level_two);
        tilDbtAccNo = (TextInputLayout) view.findViewById(R.id.til_accNo_fragment_edit_level_two);
        tilDbtBankName = (TextInputLayout) view.findViewById(R.id.til_bank_fragment_edit_level_two);
        tilDbtIfsc = (TextInputLayout) view.findViewById(R.id.til_ifsc_fragment_edit_level_two);
        tilKLoanAccNo = (TextInputLayout) view.findViewById(R.id.til_kLoanAccNo_fragment_edit_level_two);
        tilKIfsc = (TextInputLayout) view.findViewById(R.id.til_kLoanIfsc_fragment_edit_level_two);
        tilKLoanBankName = (TextInputLayout) view.findViewById(R.id.til_kLoanBank_fragment_edit_level_two);
        tilKLoanAmount = (TextInputLayout) view.findViewById(R.id.til_kLoanAmount_fragment_edit_level_two);
        tilKLoanObligation = (TextInputLayout) view.findViewById(R.id.til_kLoanObligation_fragment_edit_level_two);
        tilOtherLoanAccNo = (TextInputLayout) view.findViewById(R.id.til_otherLoanAccNo_fragment_edit_level_two);
        tilOtherIfsc = (TextInputLayout) view.findViewById(R.id.til_otherLoanIfsc_fragment_edit_level_two);
        tilOtherLoanBankName = (TextInputLayout) view.findViewById(R.id.til_otherLoanBank_fragment_edit_level_two);
        tilOtherLoanAmount = (TextInputLayout) view.findViewById(R.id.til_otherLoanAmount_fragment_edit_level_two);
        tilOtherLoanObligation = (TextInputLayout) view.findViewById(R.id.til_otherLoanObligation_fragment_edit_level_two);

        //tilPanNo = (TextInputLayout) view.findViewById(R.id.til_panNo_fragment_level_two);
        tilIdProofNo = (TextInputLayout) view.findViewById(R.id.til_idProofNo_fragment_edit_level_two);
        tilAddressProofNo = (TextInputLayout) view.findViewById(R.id.til_addressProofNo_fragment_edit_level_two);


        actvFPOName = (AppCompatAutoCompleteTextView) view.findViewById(R.id.actv_FPOName_fragment_edit_level_two);
        tietFpoCode = (TextInputEditText) view.findViewById(R.id.tiet_fpoCode_fragment_edit_level_two);
        tietNoFamilyMember = (TextInputEditText) view.findViewById(R.id.tiet_familyNo_fragment_edit_level_two);
        tietDbtAccNo = (TextInputEditText) view.findViewById(R.id.tiet_accNo_fragment_edit_level_two);
        actvDbtBankName = (AppCompatAutoCompleteTextView) view.findViewById(R.id.actv_bank_fragment_edit_level_two);
        tietDbtIfsc = (TextInputEditText) view.findViewById(R.id.tiet_ifsc_fragment_edit_level_two);
        tietKLoanAccNo = (TextInputEditText) view.findViewById(R.id.tiet_kLoanAccNo_fragment_edit_level_two);
        tietKIfsc = (TextInputEditText) view.findViewById(R.id.tiet_kLoanIfsc_fragment_edit_level_two);
        actvKLoanBankName = (AppCompatAutoCompleteTextView) view.findViewById(R.id.actv_kLoanBank_fragment_edit_level_two);
        tietKLoanAmount = (TextInputEditText) view.findViewById(R.id.tiet_kLoanAmount_fragment_edit_level_two);
        tietKLoanObligation = (TextInputEditText) view.findViewById(R.id.tiet_kLoanObligation_fragment_edit_level_two);
        tietOtherLoanAccNo = (TextInputEditText) view.findViewById(R.id.tiet_otherLoanAccNo_fragment_edit_level_two);
        tietOtherIfsc = (TextInputEditText) view.findViewById(R.id.tiet_otherLoanIfsc_fragment_edit_level_two);
        actvOtherLoanBankName = (AppCompatAutoCompleteTextView) view.findViewById(R.id.actv_otherLoanBank_fragment_edit_level_two);
        tietOtherLoanAmount = (TextInputEditText) view.findViewById(R.id.tiet_otherLoanAmount_fragment_edit_level_two);
        tietOtherLoanObligation = (TextInputEditText) view.findViewById(R.id.tiet_otherLoanObligation_fragment_edit_level_two);
        //tietPanNo = (TextInputEditText) view.findViewById(R.id.tiet_panNo_fragment_level_two);
        tietIdProofNo = (TextInputEditText) view.findViewById(R.id.tiet_idProofNo_fragment_edit_level_two);
        tietAddressProofNo = (TextInputEditText) view.findViewById(R.id.tiet_addressProofNo_fragment_edit_level_two);

        cvBtnAttachPassportPhoto = (CardView) view.findViewById(R.id.cv_btn_upload_passport_image_fragment_edit_level_two);
        cvBtnAttachPassbook = (CardView) view.findViewById(R.id.cv_btn_upload_passbook_fragment_edit_level_two);
        cvBtnAttachIdProof = (CardView) view.findViewById(R.id.cv_btn_upload_poi_fragment_edit_level_two);
        cvBtnAttachAddressProof = (CardView) view.findViewById(R.id.cv_btn_upload_poa_fragment_edit_level_two);
        cvBtnAttachAddressProofBack = (CardView) view.findViewById(R.id.cv_btn_upload_poa_back_fragment_edit_level_two);

        tvLblUploadPassportPhoto = (TextView) view.findViewById(R.id.tv_cv_btn_upload_passport_image_fragment_edit_level_two);
        tvLblUploadPassbook = (TextView) view.findViewById(R.id.tv_cv_btn_upload_passbook_fragment_edit_level_two);
        tvLblUploadIdProof = (TextView) view.findViewById(R.id.tv_cv_btn_upload_poi_fragment_edit_level_two);
        tvLblUploadAddressProof = (TextView) view.findViewById(R.id.tv_cv_btn_upload_poa_fragment_edit_level_two);
        tvLblUploadAddressProofBack = (TextView) view.findViewById(R.id.tv_cv_btn_upload_poa_back_fragment_edit_level_two);

        //ivAttachPassportPhoto = (ImageView) view.findViewById(R.id.iv_attachPassport_fragment_level_two);
        //ivAttachIdProof = (ImageView) view.findViewById(R.id.iv_attachIdProofDoc_fragment_level_two);
        //ivAttachAddressProof = (ImageView) view.findViewById(R.id.iv_attachAddressProofDoc_fragment_level_two);
        //ivAttachAddressProofBack = (ImageView) view.findViewById(R.id.iv_attachAddressProofDocBack_fragment_level_two);

        ivViewPassportPhoto = (ImageView) view.findViewById(R.id.iv_viewPassport_fragment_edit_level_two);
        ivViewPassbook = (ImageView) view.findViewById(R.id.iv_viewPassbook_fragment_edit_level_two);
        ivViewIdProof = (ImageView) view.findViewById(R.id.iv_viewIdProofDoc_fragment_edit_level_two);
        ivViewAddressProof = (ImageView) view.findViewById(R.id.iv_viewAddressProofDoc_fragment_edit_level_two);
        ivViewAddressProofBack = (ImageView) view.findViewById(R.id.iv_viewAddressProofDocBack_fragment_edit_level_two);

        spinnerKccLoanType = (Spinner) view.findViewById(R.id.spinner_kLoanType_fragment_edit_level_two);
        spinnerKccLoanTenure = (Spinner) view.findViewById(R.id.spinner_kLoanTenure_fragment_edit_level_two);
        spinnerOtherLoanType = (Spinner) view.findViewById(R.id.spinner_otherType_fragment_edit_level_two);
        spinnerOtherLoanTenure = (Spinner) view.findViewById(R.id.spinner_otherLoanTenure_fragment_edit_level_two);
        spinnerIdProof = (Spinner) view.findViewById(R.id.spinner_idProof_fragment_edit_level_two);
        spinnerAddressProof = (Spinner) view.findViewById(R.id.spinner_addressProof_fragment_edit_level_two);

        llFPO = (LinearLayout) view.findViewById(R.id.ll_fpo_fragment_edit_level_two);
        llGbQKLoan = (LinearLayout) view.findViewById(R.id.ll_groupBox_kLoan_fragment_edit_level_two);
        llGbOtherLoan = (LinearLayout) view.findViewById(R.id.ll_groupBox_otherLoan_fragment_edit_level_two);

        radioGroupQFPO = (RadioGroup) view.findViewById(R.id.radioGroup_qFPO_fragment_edit_level_two);
        radGroupQKloan = (RadioGroup) view.findViewById(R.id.radioGroup_qKLoan_fragment_edit_level_two);
        radGroupQClInsurance = (RadioGroup) view.findViewById(R.id.radioGroup_qKClInsurance_fragment_edit_level_two);
        radGroupQOtherLoan = (RadioGroup) view.findViewById(R.id.radioGroup_qOtherLoan_fragment_edit_level_two);
        radioGroupQContact = (RadioGroup) view.findViewById(R.id.radioGroup_qContactUs_fragment_edit_level_two);

        cbPanCard = (CheckBox) view.findViewById(R.id.cb_panCard_fragment_edit_level_two);
        cbFpoNil = (CheckBox) view.findViewById(R.id.cb_fpo_nil_fragment_edit_level_two);

        cvBtnSaveAndContinue = (CardView) view.findViewById(R.id.cv_btn_save_fragment_edit_level_two);

        horizontalStepView = view.findViewById(R.id.step_view_fragment_edit_level_two);

        /** Set popup height, width & background color as you need or just leave default settings **/

        imagePopupPassportPhoto = new ImagePopup(getActivity());
        imagePopupPassportPhoto.setWindowHeight(800); // Optional
        imagePopupPassportPhoto.setWindowWidth(800); // Optional
        imagePopupPassportPhoto.setBackgroundColor(Color.BLACK);  // Optional
        imagePopupPassportPhoto.setFullScreen(true); // Optional
        imagePopupPassportPhoto.setHideCloseIcon(true);  // Optional
        imagePopupPassportPhoto.setImageOnClickClose(true);  // Optional

        imagePopupPassbook = new ImagePopup(getActivity());
        imagePopupPassbook.setWindowHeight(800); // Optional
        imagePopupPassbook.setWindowWidth(800); // Optional
        imagePopupPassbook.setBackgroundColor(Color.BLACK);  // Optional
        imagePopupPassbook.setFullScreen(true); // Optional
        imagePopupPassbook.setHideCloseIcon(true);  // Optional
        imagePopupPassbook.setImageOnClickClose(true);  // Optional

        /*imagePopupPan = new ImagePopup(getActivity());
        imagePopupPan.setWindowHeight(800); // Optional
        imagePopupPan.setWindowWidth(800); // Optional
        imagePopupPan.setBackgroundColor(Color.BLACK);  // Optional
        imagePopupPan.setFullScreen(true); // Optional
        imagePopupPan.setHideCloseIcon(true);  // Optional
        imagePopupPan.setImageOnClickClose(true);  // Optional*/

        imagePopupIdFront = new ImagePopup(getActivity());
        imagePopupIdFront.setWindowHeight(800); // Optional
        imagePopupIdFront.setWindowWidth(800); // Optional
        imagePopupIdFront.setBackgroundColor(Color.BLACK);  // Optional
        imagePopupIdFront.setFullScreen(true); // Optional
        imagePopupIdFront.setHideCloseIcon(true);  // Optional
        imagePopupIdFront.setImageOnClickClose(true);  // Optional

        imagePopupAddressProofFront = new ImagePopup(getActivity());
        imagePopupAddressProofFront.setWindowHeight(800); // Optional
        imagePopupAddressProofFront.setWindowWidth(800); // Optional
        imagePopupAddressProofFront.setBackgroundColor(Color.BLACK);  // Optional
        imagePopupAddressProofFront.setFullScreen(true); // Optional
        imagePopupAddressProofFront.setHideCloseIcon(true);  // Optional
        imagePopupAddressProofFront.setImageOnClickClose(true);  // Optional

        imagePopupAddressProofBack = new ImagePopup(getActivity());
        imagePopupAddressProofBack.setWindowHeight(800); // Optional
        imagePopupAddressProofBack.setWindowWidth(800); // Optional
        imagePopupAddressProofBack.setBackgroundColor(Color.BLACK);  // Optional
        imagePopupAddressProofBack.setFullScreen(true); // Optional
        imagePopupAddressProofBack.setHideCloseIcon(true);  // Optional
        imagePopupAddressProofBack.setImageOnClickClose(true);  // Optional

        // Init Listeners
        initListener();

        List<StepBean> stepsBeanList = new ArrayList<>();
        StepBean stepBean0 = new StepBean("1", 1);
        StepBean stepBean1 = new StepBean("2", 0);
        StepBean stepBean2 = new StepBean("3", -1);
        StepBean stepBean3 = new StepBean("4", -1);
        stepsBeanList.add(stepBean0);
        stepsBeanList.add(stepBean1);
        stepsBeanList.add(stepBean2);
        stepsBeanList.add(stepBean3);

        horizontalStepView.setStepViewTexts(stepsBeanList)
                .setTextSize(12)
                .setStepsViewIndicatorCompletedLineColor(ContextCompat.getColor(getActivity(), android.R.color.darker_gray))
                .setStepsViewIndicatorUnCompletedLineColor(ContextCompat.getColor(getActivity(), android.R.color.darker_gray))
                .setStepViewComplectedTextColor(ContextCompat.getColor(getActivity(), android.R.color.darker_gray))
                .setStepViewUnComplectedTextColor(ContextCompat.getColor(getActivity(), android.R.color.darker_gray))
                .setStepsViewIndicatorCompleteIcon(ContextCompat.getDrawable(getActivity(), R.drawable.ic_success))
                .setStepsViewIndicatorDefaultIcon(ContextCompat.getDrawable(getActivity(), R.drawable.default_icon))
                .setStepsViewIndicatorAttentionIcon(ContextCompat.getDrawable(getActivity(), R.drawable.attention));

        lstBankName = Arrays.asList(getResources().getStringArray(R.array.bank_name));
        lstBankNameB = Arrays.asList(getResources().getStringArray(R.array.bank_name_second));

        ArrayAdapter<String> adapterBankName = new ArrayAdapter<>(getActivity(),
                android.R.layout.simple_list_item_1, lstBankName);

        ArrayAdapter<String> adapterBankNameB = new ArrayAdapter<>(getActivity(),
                android.R.layout.simple_list_item_1, lstBankNameB);

        actvDbtBankName.setAdapter(adapterBankName);
        actvKLoanBankName.setAdapter(adapterBankNameB);
        actvOtherLoanBankName.setAdapter(adapterBankNameB);

        // Data for kcc Loan Type Spinner
        List<String> lstKccLoanType = new ArrayList<>();
        lstKccLoanType.add("--Select--");
        lstKccLoanType.add("KCC");
        // Data for Loan Type Spinner
        List<String> lstOtherLoanType = new ArrayList<>();
        lstOtherLoanType.add("--Select--");
        lstOtherLoanType.add("Personal Loan");
        lstOtherLoanType.add("Home Loan");
        lstOtherLoanType.add("Car Loan");
        lstOtherLoanType.add("Two Wheeler Loan");
        lstOtherLoanType.add("Equipment Loan");
        lstOtherLoanType.add("Business Overdraft");
        lstOtherLoanType.add("Other");
        // Data for tenure spinner
        List<String> lstKccLoanTenure = new ArrayList<>();
        lstKccLoanTenure.add("--Select--");
        for (int i = 12; i <= 60; i++) {
            lstKccLoanTenure.add(String.valueOf(i));
        }
        // Data for tenure spinner
        List<String> lstOtherLoanTenure = new ArrayList<>();
        lstOtherLoanTenure.add("--Select--");
        for (int i = 12; i <= 60; i++) {
            lstOtherLoanTenure.add(String.valueOf(i));
        }
        // Data for Id Proof Spinner
        List<String> lstIdProof = new ArrayList<>();
        lstIdProof.add("--Select--");
        lstIdProof.add("PAN CARD");
        lstIdProof.add("AADHAAR CARD");
        lstIdProof.add("DRIVING LICENCE");
        lstIdProof.add("VOTERS CARD");
        lstIdProof.add("PASSPORT");
        //lstIdProof.add("MNREGA CARD");
        // Data for Address Proof Spinner
        List<String> lstAddressProof = new ArrayList<>();
        lstAddressProof.add("--Select--");
        lstAddressProof.add("AADHAAR CARD");
        lstAddressProof.add("VOTERS CARD");
        //lstAddressProof.add("MNREGA CARD");
        lstAddressProof.add("PASSPORT");
        lstAddressProof.add("DRIVING LICENCE");
        // Creating adapter for spinner

        ArrayAdapter<String> dataAdapterKccLoanType = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, lstKccLoanType);
        ArrayAdapter<String> dataAdapterOtherLoanType = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, lstOtherLoanType);
        ArrayAdapter<String> dataAdapterKccLoanTenure = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, lstKccLoanTenure);
        ArrayAdapter<String> dataAdapterOtherLoanTenure = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, lstOtherLoanTenure);
        ArrayAdapter<String> dataAdapterIdProof = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, lstIdProof);
        ArrayAdapter<String> dataAdapterAddressProof = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, lstAddressProof);

        // Drop down layout style - list view spinner
        dataAdapterKccLoanType.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        dataAdapterOtherLoanType.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        dataAdapterKccLoanTenure.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        dataAdapterOtherLoanTenure.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        dataAdapterIdProof.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        dataAdapterAddressProof.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Attaching data adapter to spinner
        spinnerKccLoanType.setAdapter(dataAdapterKccLoanType);
        spinnerOtherLoanType.setAdapter(dataAdapterOtherLoanType);
        spinnerKccLoanTenure.setAdapter(dataAdapterKccLoanTenure);
        spinnerOtherLoanTenure.setAdapter(dataAdapterOtherLoanTenure);
        spinnerIdProof.setAdapter(dataAdapterIdProof);
        spinnerIdProof.setSelection(dataAdapterIdProof.NO_SELECTION, false);
        spinnerAddressProof.setAdapter(dataAdapterAddressProof);
        spinnerAddressProof.setSelection(dataAdapterAddressProof.NO_SELECTION, false);

        radioGroupQFPO.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.radBtn_qFPONo_fragment_edit_level_two) {
                    llFPO.setVisibility(View.GONE);
                    qFPO = false;
                    actvFPOName.setText(null);
                    tietFpoCode.setText(null);
                    cbFpoNil.setChecked(false);

                } else if (checkedId == R.id.radBtn_qFPOYes_fragment_edit_level_two) {
                    llFPO.setVisibility(View.VISIBLE);
                    qFPO = true;
                }
            }
        });

        radGroupQKloan.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.radBtn_qKLoanNo_fragment_edit_level_two) {
                    llGbQKLoan.setVisibility(View.GONE);
                    qKLoan = false;

                } else if (checkedId == R.id.radBtn_qKLoanYes_fragment_edit_level_two) {
                    llGbQKLoan.setVisibility(View.VISIBLE);
                    qKLoan = true;
                }
            }
        });

        radGroupQClInsurance.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.radBtn_qKClInsuranceNo_fragment_edit_level_two) {
                    qClInsuranceLoan = false;
                } else if (checkedId == R.id.radBtn_qKClInsuranceYes_fragment_edit_level_two) {
                    qClInsuranceLoan = true;
                }
            }
        });

        radGroupQOtherLoan.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.radBtn_qOtherLoanNo_fragment_edit_level_two) {
                    llGbOtherLoan.setVisibility(View.GONE);
                    qOtherLoan = false;

                } else if (checkedId == R.id.radBtn_qOtherLoanYes_fragment_edit_level_two) {
                    llGbOtherLoan.setVisibility(View.VISIBLE);
                    qOtherLoan = true;
                }
            }
        });

        radioGroupQContact.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.radBtn_qContactUsYes_fragment_edit_level_two)
                    qContact = true;
                else if (checkedId == R.id.radBtn_qContactUsNo_fragment_edit_level_two)
                    qContact = false;
            }
        });

        cbFpoNil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                actvFPOName.setText("Not In List");
                tietFpoCode.setText("FPO0");
            }
        });

        spinnerIdProof.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (adapterView.getSelectedItem().toString().equals("AADHAAR CARD") && !isPoaValid) {
                    // set max length to 14
                    int maxLength = 14;
                    InputFilter[] FilterArray = new InputFilter[1];
                    FilterArray[0] = new InputFilter.LengthFilter(maxLength);
                    tietIdProofNo.setFilters(FilterArray);

                    cbPanCard.setChecked(false);
                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    builder.setMessage(getResources().getString(R.string.msg_aadhaar_prompt));
                    builder.setCancelable(true);
                    builder.setPositiveButton(
                            "OK",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.cancel();
                                    // Hit karza api for SDK token
                                    GetSdkTokenPoi();
                                }
                            });

                    builder.setNegativeButton(
                            "Cancel",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.cancel();
                                }
                            });

                    AlertDialog alert = builder.create();
                    alert.show();
                } else if (adapterView.getSelectedItem().toString().equals("PAN CARD")) {
                    // set max length to 10
                    int maxLength = 10;
                    InputFilter[] FilterArray = new InputFilter[1];
                    FilterArray[0] = new InputFilter.LengthFilter(maxLength);
                    tietIdProofNo.setFilters(FilterArray);

                    cbPanCard.setChecked(true);
                } else if (adapterView.getSelectedItem().toString().equals("DRIVING LICENCE")) {
                    // set max length to 15
                    int maxLength = 15;
                    InputFilter[] FilterArray = new InputFilter[1];
                    FilterArray[0] = new InputFilter.LengthFilter(maxLength);
                    tietIdProofNo.setFilters(FilterArray);
                } else if (adapterView.getSelectedItem().toString().equals("VOTERS CARD")) {
                    // set max length to 10
                    int maxLength = 10;
                    InputFilter[] FilterArray = new InputFilter[1];
                    FilterArray[0] = new InputFilter.LengthFilter(maxLength);
                    tietIdProofNo.setFilters(FilterArray);
                } else if (adapterView.getSelectedItem().toString().equals("PASSPORT")) {
                    // set max length to 15
                    int maxLength = 15;
                    InputFilter[] FilterArray = new InputFilter[1];
                    FilterArray[0] = new InputFilter.LengthFilter(maxLength);
                    tietIdProofNo.setFilters(FilterArray);
                } else {
                    cbPanCard.setChecked(false);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        spinnerAddressProof.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                // If user is giving same doc for address proof as id proof
                if (adapterView.getSelectedItem().toString().equals(spinnerIdProof.getSelectedItem().toString())) {
                    tietAddressProofNo.setText(tietIdProofNo.getText().toString().trim());
                    if (isPoiValid) {
                        isPoaValid = true;
                        ivPoaValid.setImageResource(R.drawable.ic_check_circle_green);
                        spinnerAddressProof.setEnabled(false);
                        tietAddressProofNo.setEnabled(false);
                    }
                    if (bitmapIdProof != null) {
                        if (adapterView.getSelectedItem().toString().equals("AADHAAR CARD")) {
                            urlImageAddressProofFront = urlImageIdProofFront;
                            bitmapAddressProof = bitmapIdProof;
                        }
                        isAttachedAddressProof = true;
                        cvBtnAttachAddressProof.setEnabled(false);
                        cvBtnAttachAddressProof.setBackgroundColor(Color.parseColor("#FD7D00"));
                        ivViewAddressProof.setImageDrawable(ContextCompat.getDrawable(getActivity(), R.drawable.ic_view_doc_black));
                    }
                } else {
                    cvBtnAttachAddressProof.setEnabled(true);
                }

                if (adapterView.getSelectedItem().toString().equals("AADHAAR CARD") && !isPoaValid) {
                    // set max length to 14
                    int maxLength = 14;
                    InputFilter[] FilterArray = new InputFilter[1];
                    FilterArray[0] = new InputFilter.LengthFilter(maxLength);
                    tietAddressProofNo.setFilters(FilterArray);

                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    builder.setMessage(getResources().getString(R.string.msg_aadhaar_prompt));
                    builder.setCancelable(true);
                    builder.setPositiveButton(
                            "OK",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.cancel();
                                    // Hit karza api for SDK token
                                    GetSdkTokenPoa();
                                }
                            });

                    builder.setNegativeButton(
                            "Cancel",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.cancel();
                                }
                            });

                    AlertDialog alert = builder.create();
                    alert.show();
                } else if (adapterView.getSelectedItem().toString().equals("DRIVING LICENCE")) {
                    // set max length to 15
                    int maxLength = 15;
                    InputFilter[] FilterArray = new InputFilter[1];
                    FilterArray[0] = new InputFilter.LengthFilter(maxLength);
                    tietAddressProofNo.setFilters(FilterArray);
                } else if (adapterView.getSelectedItem().toString().equals("VOTERS CARD")) {
                    // set max length to 10
                    int maxLength = 10;
                    InputFilter[] FilterArray = new InputFilter[1];
                    FilterArray[0] = new InputFilter.LengthFilter(maxLength);
                    tietAddressProofNo.setFilters(FilterArray);
                } else if (adapterView.getSelectedItem().toString().equals("PASSPORT")) {
                    // set max length to 15
                    int maxLength = 15;
                    InputFilter[] FilterArray = new InputFilter[1];
                    FilterArray[0] = new InputFilter.LengthFilter(maxLength);
                    tietAddressProofNo.setFilters(FilterArray);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        actvFPOName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                tietFpoCode.setText("");
            }
        });
        // Not in use
        /*tietPanNo.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });*/

        tietDbtAccNo.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if (!hasFocus) {
                    if (!isAccNoValid) {
                        if (!mInputValidation.isInputEditTextValidAccountNo(tietDbtAccNo, tilDbtAccNo, getResources().getString(R.string.err_msg_acc_no)))
                            return;
                        // Comment in debug
                        KarzaCheckAccountNo(tietDbtAccNo.getText().toString());
                    }
                }
            }
        });

        tietDbtAccNo.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable et) {
                String s = et.toString();
                if (!s.equals(s.toUpperCase())) {
                    s = s.toUpperCase();
                    tietDbtAccNo.setText(s);
                    tietDbtAccNo.setSelection(tietDbtAccNo.length()); //Set cursor to end
                }
            }
        });

        tietDbtIfsc.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable et) {
                String s = et.toString();
                if (!s.equals(s.toUpperCase())) {
                    s = s.toUpperCase();
                    tietDbtIfsc.setText(s);
                    tietDbtIfsc.setSelection(tietDbtIfsc.length()); //Set cursor to end
                }
            }
        });

        tietKLoanAccNo.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable et) {
                String s = et.toString();
                if (!s.equals(s.toUpperCase())) {
                    s = s.toUpperCase();
                    tietKLoanAccNo.setText(s);
                    tietKLoanAccNo.setSelection(tietKLoanAccNo.length()); //Set cursor to end
                }
            }
        });

        tietKIfsc.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable et) {
                String s = et.toString();
                if (!s.equals(s.toUpperCase())) {
                    s = s.toUpperCase();
                    tietKIfsc.setText(s);
                    tietKIfsc.setSelection(tietKIfsc.length()); //Set cursor to end
                }
            }
        });

        tietOtherLoanAccNo.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable et) {
                String s = et.toString();
                if (!s.equals(s.toUpperCase())) {
                    s = s.toUpperCase();
                    tietOtherLoanAccNo.setText(s);
                    tietOtherLoanAccNo.setSelection(tietOtherLoanAccNo.length()); //Set cursor to end
                }

            }
        });

        tietOtherIfsc.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable et) {
                String s = et.toString();
                if (!s.equals(s.toUpperCase())) {
                    s = s.toUpperCase();
                    tietOtherIfsc.setText(s);
                    tietOtherIfsc.setSelection(tietOtherIfsc.length()); //Set cursor to end
                }

            }
        });

        tietIdProofNo.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (!isPoiValid) {
                    switch (spinnerIdProof.getSelectedItem().toString()) {
                        case "PAN CARD":
                            if (!mInputValidation.isInputEditTextValidPan(tietIdProofNo, tilIdProofNo, getResources().getString(R.string.err_msg_invalid_pan)))
                                return;

                            if (cbPanCard.isChecked()) {
                                // Connemt in debug
                                KarzaCheckPanPoi(charSequence);
                            }
                            break;

                        case "AADHAAR CARD":
                            if (!mInputValidation.isInputEditTextValidAadhar(tietIdProofNo, tilIdProofNo, getResources().getString(R.string.err_msg_invalid_aadhaar)))
                                return;
                            break;

                        case "DRIVING LICENCE":
                            if (!mInputValidation.isInputEditTextValidDl(tietIdProofNo, tilIdProofNo, getResources().getString(R.string.err_msg_invalid_passport)))
                                return;
                            KarzaCheckDrivingLicencePoi(charSequence);
                            break;

                        case "PASSPORT":
                            if (!mInputValidation.isInputEditTextValidDl(tietIdProofNo, tilIdProofNo, getResources().getString(R.string.err_msg_invalid_passport)))
                                return;
                            KarzaCheckPassportPoi(charSequence);
                            break;

                        case "MNREGA CARD":
                            if (!mInputValidation.isInputEditTextValidMnrega(tietIdProofNo, tilIdProofNo, "Invalid mngera card number"))
                                return;
                            break;

                        case "VOTERS CARD":
                            if (!mInputValidation.isInputEditTextValidVoterCard(tietIdProofNo, tilIdProofNo, getResources().getString(R.string.err_msg_invalid_voter_id)))
                                return;
                            KarzaCheckVoterIdPoi(charSequence);
                            break;
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable et) {
                String s = et.toString();
                if (!s.equals(s.toUpperCase())) {
                    s = s.toUpperCase();
                    tietIdProofNo.setText(s);
                    tietIdProofNo.setSelection(tietIdProofNo.length()); //Set cursor to end
                }
            }
        });

        tietAddressProofNo.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (!isPoaValid) {
                    switch (spinnerAddressProof.getSelectedItem().toString()) {
                        case "AADHAAR CARD":
                            if (!mInputValidation.isInputEditTextValidAadhar(tietAddressProofNo, tilAddressProofNo, getResources().getString(R.string.err_msg_invalid_aadhaar)))
                                return;
                            break;

                        case "DRIVING LICENCE":
                            if (!mInputValidation.isInputEditTextValidDl(tietAddressProofNo, tilAddressProofNo, getResources().getString(R.string.err_msg_invalid_dl)))
                                return;
                            KarzaCheckDrivingLicencePoa(charSequence);
                            break;

                        case "PASSPORT":
                            if (!mInputValidation.isInputEditTextValidPassport(tietAddressProofNo, tilAddressProofNo, getResources().getString(R.string.err_msg_invalid_passport)))
                                return;
                            KarzaCheckPassportPoa(charSequence);
                            break;

                        case "MNREGA CARD":
                            if (!mInputValidation.isInputEditTextValidMnrega(tietAddressProofNo, tilAddressProofNo, "Invalid mnrega card number"))
                                return;
                            break;

                        case "VOTERS CARD":
                            if (!mInputValidation.isInputEditTextValidVoterCard(tietAddressProofNo, tilAddressProofNo, getResources().getString(R.string.err_msg_invalid_voter_id)))
                                return;
                            // Comment in debug
                            KarzaCheckVoterIdPoa(charSequence);
                            break;
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable et) {
                String s = et.toString();
                if (!s.equals(s.toUpperCase())) {
                    s = s.toUpperCase();
                    tietAddressProofNo.setText(s);
                    tietAddressProofNo.setSelection(tietAddressProofNo.length()); //Set cursor to end
                }
            }
        });

        // Not in use
        /*tietIdProofNo.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if(!hasFocus){
                    String txt = tietIdProofNo.getText().toString().trim();
                    switch (spinnerIdProof.getSelectedItem().toString()){
                        case "PAN CARD":
                            if (!isPoiValid && cbPanCard.isChecked()) {
                                KarzaCheckPanPoi(txt);
                            }
                            break;

                        case "AADHAAR CARD":
                            break;

                        case "DRIVING LICENCE":
                            if (!isPoiValid) {
                                KarzaCheckDrivingLicencePoi(txt);
                            }
                            break;

                        case "PASSPORT":
                            if (!isPoiValid) {
                                KarzaCheckPassportPoi(txt);
                            }
                            break;

                        case "MNREGA CARD":
                            break;

                        case "VOTERS CARD":
                            if (!isPoiValid) {
                                KarzaCheckVoterIdPoi(txt);
                            }
                            break;
                    }
                }
            }
        });*/
        // Not in use
        /*tietAddressProofNo.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if(!hasFocus){
                    String txt = tietAddressProofNo.getText().toString().trim();
                    Toast.makeText(getActivity(), txt, Toast.LENGTH_SHORT).show();
                    switch (spinnerAddressProof.getSelectedItem().toString()){
                        case "PAN CARD":
                            if (!isPoaValid && cbPanCard.isChecked()) {
                                KarzaCheckPanPoa(txt);
                            }
                            break;

                        case "AADHAAR CARD":
                            break;

                        case "DRIVING LICENCE":
                            if (!isPoaValid) {
                                KarzaCheckDrivingLicencePoa(txt);
                            }
                            break;

                        case "PASSPORT":
                            if (!isPoaValid) {
                                KarzaCheckPassportPoa(txt);
                            }
                            break;

                        case "MNREGA CARD":
                            break;

                        case "VOTERS CARD":
                            if (!isPoaValid) {
                                KarzaCheckVoterIdPoa(txt);
                            }
                            break;
                    }
                }
            }
        });*/

        cbPanCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (cbPanCard.isChecked()) {
                    qPanAvailable = true;
                    spinnerIdProof.setSelection(getIndex(spinnerIdProof, "PAN CARD"));
                    //tietIdProofNo.setText(null);
                    //ivAttachIdProof.setEnabled(false);
                    //ivViewIdProof.setEnabled(false);
                    //spinnerIdProof.setEnabled(false);
                    //tietIdProofNo.setEnabled(false);
                    //llPan.setVisibility(View.VISIBLE);
                } else {
                    qPanAvailable = false;
                    //tietPanNo.setText(null);
                    //spinnerIdProof.setEnabled(true);
                    //tietIdProofNo.setEnabled(true);
                    //ivAttachIdProof.setEnabled(true);
                    //ivViewIdProof.setEnabled(true);
                    //llPan.setVisibility(View.GONE);
                    spinnerIdProof.setSelection(0);
                }
            }
        });

        actvFPOName.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String fpoCode = getSingleKeyFromValue(mapFpo, adapterView.getAdapter().getItem(i).toString());
                tietFpoCode.setText(fpoCode);
            }
        });

        cvBtnSaveAndContinue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Validate();
                // Uncomment while debug
                /*if (isLevelDone)
                    EditDataWithFile();
                else
                    AddDataWithFile();*/
            }
        });

        initFpo();
        InitData();
        //if (!isLevelDone) // Changed
        //InitPreData();

        return view;
    }

    private void initListener() {
        cvBtnAttachPassportPhoto.setOnClickListener(this);
        cvBtnAttachPassbook.setOnClickListener(this);
        cvBtnAttachIdProof.setOnClickListener(this);
        cvBtnAttachAddressProof.setOnClickListener(this);
        cvBtnAttachAddressProofBack.setOnClickListener(this);

        ivViewPassportPhoto.setOnClickListener(this);
        ivViewPassbook.setOnClickListener(this);
        ivViewIdProof.setOnClickListener(this);
        ivViewAddressProof.setOnClickListener(this);
        ivViewAddressProofBack.setOnClickListener(this);
    }

    private void Validate() {
        if (!mInputValidation.isInputEditTextFilled(tietNoFamilyMember, tilNoFamilyMember, getString(R.string.error_message_blank_field))) {
            return;
        }
        if (!mInputValidation.isInputEditTextFilled(tietDbtAccNo, tilDbtAccNo, getString(R.string.error_message_blank_field))) {
            return;
        }
        if (!mInputValidation.isAutoCompleteTextViewFilled(actvDbtBankName, tilDbtBankName, getString(R.string.error_message_blank_field))) {
            return;
        }
        if (!mInputValidation.isInputEditTextFilled(tietDbtIfsc, tilDbtIfsc, getString(R.string.error_message_blank_field))) {
            return;
        }
        if (tietDbtIfsc.getText().length() != 11) {
            mAlert.showAlertDialog(getActivity(), "Validation Error", getResources().getString(R.string.err_msg_ifsc_code), false);
            return;
        }
        if (!isCorrectBankName(actvDbtBankName.getText().toString().trim())) {
            tilDbtBankName.setError("Invalid Input");
            return;
        }
        if (!mInputValidation.isInputEditTextPositiveInteger(tietNoFamilyMember, tilNoFamilyMember, getString(R.string.error_message_invalid_input))) {
            return;
        }
        /*if (!mInputValidation.isInputEditTextPositiveInteger(tietDbtAccNo, tilDbtAccNo, getString(R.string.error_message_invalid_input))) {
            return;
        }*/
        if (!(Integer.valueOf(tietNoFamilyMember.getText().toString()) < 16)) {
            mAlert.showAlertDialog(getActivity(), "Validation Error", getResources().getString(R.string.err_msg_family_member), false);
            return;
        }
        /*if (cbPanCard.isChecked()) {
            if (!mInputValidation.isInputEditTextFilled(tietPanNo, tilPanNo, getString(R.string.error_message_blank_field))) {
                return;
            }
            if (tietPanNo.getText().length() != 10) {
                mAlert.showAlertDialog(getActivity(), "Validation Error", "Pan number should be of 10 characters", false);
                return;
            }
        } else {
            if (!mInputValidation.isInputEditTextFilled(tietIdProofNo, tilIdProofNo, getString(R.string.error_message_blank_field))) {
                return;
            }
            // Check doc in spinner and validate digits accordingly
            switch (spinnerIdProof.getSelectedItem().toString()) {
                case "PAN CARD":
                    if (tietIdProofNo.getText().length() != 10) {
                        mAlert.showAlertDialog(getActivity(), "Validation Error", "Pan card number should be of 10 characters", false);
                        return;
                    }
                    break;
                case "AADHAAR CARD":
                    if (tietIdProofNo.getText().length() != 12) {
                        mAlert.showAlertDialog(getActivity(), "Validation Error", "Aadhaar Letter/Card number  should be of 12 characters", false);
                        return;
                    }
                    if (!mInputValidation.isInputEditTextPositiveInteger(tietIdProofNo, tilIdProofNo, getString(R.string.error_message_invalid_input))) {
                        return;
                    }
                    break;
                case "DRIVING LICENCE":
                    if (tietIdProofNo.getText().length() != 15) {
                        mAlert.showAlertDialog(getActivity(), "Validation Error", "Driving Licence number should be of 15 characters", false);
                        return;
                    }
                    break;
                case "MNREGA CARD":
                    if (tietIdProofNo.getText().length() != 17) {
                        mAlert.showAlertDialog(getActivity(), "Validation Error", "MNREGA Card number should be of 15 characters", false);
                        return;
                    }
                    break;
                case "PASSPORT":
                    if (tietIdProofNo.getText().length() != 8) {
                        mAlert.showAlertDialog(getActivity(), "Validation Error", "Passport number should be of 8 characters", false);
                        return;
                    }
                    break;
                case "VOTERS CARD":
                    if (tietIdProofNo.getText().length() != 10) {
                        mAlert.showAlertDialog(getActivity(), "Validation Error", "Voter's Identity Card number should be of 8 characters", false);
                        return;
                    }
                    break;
            }
        }*/
        if (cbPanCard.isChecked()) {
            if (!spinnerIdProof.getSelectedItem().toString().equals("PAN CARD")) {
                mAlert.showAlertDialog(getActivity(), "Validation Error", getResources().getString(R.string.err_msg_pan_checked), false);
                return;
            }
        }
        if (!mInputValidation.isInputEditTextFilled(tietAddressProofNo, tilAddressProofNo, getString(R.string.error_message_blank_field))) {
            return;
        }
        if (isAttachedPassbook) {
            if (actvDbtBankName.getText().toString().length() < 1) {
                mAlert.showAlertDialog(getActivity(), "Validation Error", getResources().getString(R.string.err_msg_dbt_empty), false);
                return;
            }
            if (!mInputValidation.isInputEditTextFilled(tietDbtAccNo, tilDbtAccNo, getString(R.string.error_message_blank_field))) {
                return;
            }
        }

        /*if (isAttachedPan) {
            if (!mInputValidation.isInputEditTextFilled(tietPanNo, tilPanNo, getString(R.string.error_message_blank_field))) {
                return;
            }
            if (etPanPath.getText().toString().trim().length() == 0) {
                mAlert.showAlertDialog(getActivity(), "Validation Error", "Path is empty", false);
                return;
            }
        }*/

        if (isAttachedIdProof) {
            if (spinnerIdProof.getSelectedItem().toString().equals("--Select--")) {
                mAlert.showAlertDialog(getActivity(), "Validation Error", getResources().getString(R.string.err_msg_select_doc_name), false);
                return;
            }
            if (!mInputValidation.isInputEditTextFilled(tietIdProofNo, tilIdProofNo, getString(R.string.error_message_blank_field))) {
                return;
            }
        }

        if (isAttachedAddressProof) {
            if (spinnerAddressProof.getSelectedItem().toString().equals("--Select--")) {
                mAlert.showAlertDialog(getActivity(), "Validation Error", getResources().getString(R.string.err_msg_select_doc_name), false);
                return;
            }
            if (!mInputValidation.isInputEditTextFilled(tietAddressProofNo, tilAddressProofNo, getString(R.string.error_message_blank_field))) {
                return;
            }
        }

        switch (radioGroupQFPO.getCheckedRadioButtonId()) {
            case R.id.radBtn_qFPOYes_fragment_edit_level_two:
                if (!mInputValidation.isAutoCompleteTextViewFilled(actvFPOName, tilFPOName, getString(R.string.error_message_blank_field))) {
                    return;
                }
                if (!mInputValidation.isInputEditTextFilled(tietFpoCode, tilFPOName, getString(R.string.error_message_blank_field))) {
                    return;
                }
                if (!isCorrectFpoName(actvFPOName.getText().toString().trim())) {
                    tilFPOName.setError("Invalid Input");
                    return;
                }
                break;
            default:
                break;
        }

        switch (radGroupQKloan.getCheckedRadioButtonId()) {
            case R.id.radBtn_qKLoanYes_fragment_edit_level_two:
                if (spinnerKccLoanType.getSelectedItem().toString().equals("--Select--")) {
                    mAlert.showAlertDialog(getActivity(), "Validation Error", getResources().getString(R.string.err_msg_select_loan_type), false);
                    return;
                }
                if (!mInputValidation.isInputEditTextFilled(tietKLoanAccNo, tilKLoanAccNo, getString(R.string.error_message_blank_field))) {
                    return;
                }
                if (!mInputValidation.isAutoCompleteTextViewFilled(actvKLoanBankName, tilKLoanBankName, getString(R.string.error_message_blank_field))) {
                    return;
                }
                /*if (!mInputValidation.isInputEditTextFilled(tietKIfsc, tilKIfsc, getString(R.string.error_message_blank_field))) {
                    return;
                }*/
                /*if (tietKIfsc.getText().length() != 11) {
                    mAlert.showAlertDialog(getActivity(), "Validation Error", "Ifsc code should be of 11 characters", false);
                    return;
                }*/
                if (spinnerKccLoanTenure.getSelectedItem().toString().equals("--Select--")) {
                    mAlert.showAlertDialog(getActivity(), "Validation Error", getResources().getString(R.string.err_msg_select_tenure), false);
                    return;
                }
                if (!mInputValidation.isInputEditTextFilled(tietKLoanAmount, tilKLoanAmount, getString(R.string.error_message_blank_field))) {
                    return;
                }
                if (!isCorrectBankName(actvKLoanBankName.getText().toString().trim())) {
                    tilKLoanBankName.setError("Invalid Input");
                    return;
                }
                break;
            case R.id.radBtn_qKLoanNo_fragment_edit_level_two:
                break;
        }
        switch (radGroupQOtherLoan.getCheckedRadioButtonId()) {
            case R.id.radBtn_qOtherLoanYes_fragment_edit_level_two:
                if (spinnerOtherLoanType.getSelectedItem().toString().equals("--Select--")) {
                    mAlert.showAlertDialog(getActivity(), "Validation Error", getResources().getString(R.string.err_msg_select_loan_type), false);
                    return;
                }
                if (!mInputValidation.isInputEditTextFilled(tietOtherLoanAccNo, tilOtherLoanAccNo, getString(R.string.error_message_blank_field))) {
                    return;
                }
                if (!mInputValidation.isAutoCompleteTextViewFilled(actvOtherLoanBankName, tilOtherLoanBankName, getString(R.string.error_message_blank_field))) {
                    return;
                }
                /*if (!mInputValidation.isInputEditTextFilled(tietOtherIfsc, tilOtherIfsc, getString(R.string.error_message_blank_field))) {
                    return;
                }*/
                /*if (tietOtherIfsc.getText().length() != 11) {
                    mAlert.showAlertDialog(getActivity(), "Validation Error", "Ifsc code should be of 11 characters", false);
                    return;
                }*/
                if (spinnerOtherLoanTenure.getSelectedItem().toString().equals("--Select--")) {
                    mAlert.showAlertDialog(getActivity(), "Validation Error", getResources().getString(R.string.err_msg_select_tenure), false);
                    return;
                }
                if (!mInputValidation.isInputEditTextFilled(tietOtherLoanAmount, tilOtherLoanAmount, getString(R.string.error_message_blank_field))) {
                    return;
                }
                if (!isCorrectBankName(actvOtherLoanBankName.getText().toString().trim())) {
                    tilOtherLoanBankName.setError("Invalid Input");
                    return;
                }
                break;
            case R.id.radBtn_qOtherLoanNo_fragment_edit_level_two:
                break;
        }

        if (mInputValidation.isInputEditTextFilled(tietIdProofNo, tilIdProofNo, "")) {
            if (spinnerIdProof.getSelectedItem().toString().equals("--Select--")) {
                mAlert.showAlertDialog(getActivity(), "Validation Error", getResources().getString(R.string.err_msg_id_proof_select), false);
                return;
            }
        }
        if (mInputValidation.isInputEditTextFilled(tietAddressProofNo, tilAddressProofNo, "")) {
            if (spinnerAddressProof.getSelectedItem().toString().equals("--Select--")) {
                mAlert.showAlertDialog(getActivity(), "Validation Error", getResources().getString(R.string.err_msg_add_proof_select), false);
                return;
            }
        }

        if (!spinnerIdProof.getSelectedItem().toString().equals("--Select--")) {
            if (!mInputValidation.isInputEditTextFilled(tietIdProofNo, tilIdProofNo, getString(R.string.error_message_blank_field))) {
                return;
            }
        }
        if (!spinnerAddressProof.getSelectedItem().toString().equals("--Select--")) {
            if (!mInputValidation.isInputEditTextFilled(tietAddressProofNo, tilAddressProofNo, getString(R.string.error_message_blank_field))) {
                return;
            }
        }

        /*switch (spinnerIdProof.getSelectedItem().toString()) {
            case "PAN CARD":
                if (!mInputValidation.isInputEditTextValidPan(tietIdProofNo, tilIdProofNo, getResources().getString(R.string.err_msg_invalid_pan)))
                    return;

            case "AADHAAR CARD":
                if (!mInputValidation.isInputEditTextValidAadhar(tietIdProofNo, tilIdProofNo, getResources().getString(R.string.err_msg_invalid_aadhaar)))
                    return;
                break;

            case "DRIVING LICENCE":
                if (!mInputValidation.isInputEditTextValidDl(tietIdProofNo, tilIdProofNo, getResources().getString(R.string.err_msg_invalid_dl)))
                    return;

            case "PASSPORT":
                if (!mInputValidation.isInputEditTextValidDl(tietIdProofNo, tilIdProofNo, getResources().getString(R.string.err_msg_invalid_passport)))
                    return;

            case "MNREGA CARD":
                if (!mInputValidation.isInputEditTextValidMnrega(tietIdProofNo, tilIdProofNo, "Invalid mngera card number"))
                    return;
                break;

            case "VOTERS CARD":
                if (!mInputValidation.isInputEditTextValidVoterCard(tietIdProofNo, tilIdProofNo, getResources().getString(R.string.err_msg_invalid_voter_id)))
                    return;
        }

        switch (spinnerAddressProof.getSelectedItem().toString()) {
            case "PAN CARD":
                if (!mInputValidation.isInputEditTextValidPan(tietAddressProofNo, tilAddressProofNo, getResources().getString(R.string.err_msg_invalid_pan)))
                    return;

            case "AADHAAR CARD":
                if (!mInputValidation.isInputEditTextValidAadhar(tietAddressProofNo, tilAddressProofNo, getResources().getString(R.string.err_msg_invalid_aadhaar)))
                    return;
                break;

            case "DRIVING LICENCE":
                if (!mInputValidation.isInputEditTextValidDl(tietAddressProofNo, tilAddressProofNo, getResources().getString(R.string.err_msg_invalid_dl)))
                    return;

            case "PASSPORT":
                if (!mInputValidation.isInputEditTextValidPassport(tietAddressProofNo, tilAddressProofNo, getResources().getString(R.string.err_msg_invalid_passport)))
                    return;

            case "MNREGA CARD":
                if (!mInputValidation.isInputEditTextValidMnrega(tietAddressProofNo, tilAddressProofNo, "Invalid mnrega card number"))
                    return;
                break;

            case "VOTERS CARD":
                if (!mInputValidation.isInputEditTextValidVoterCard(tietAddressProofNo, tilAddressProofNo, getResources().getString(R.string.err_msg_invalid_voter_id)))
                    return;
        }

        if (!isPoiValid) {
            mAlert.showAlertDialog(getActivity(), "Validation error", getResources().getString(R.string.err_msg_valid_poi), false);
            return;
        }

        if (!isPoaValid) {
            mAlert.showAlertDialog(getActivity(), "Validation error", getResources().getString(R.string.err_msg_valid_poa), false);
            return;
        }*/
        if (isLevelDone) {
            EditDataWithFile();
        } else {
            // Check image attachments
            if (!isAttachedPassbook || !isAttachedPassportSizePhoto || !isAttachedIdProof || !isAttachedAddressProof) {
                mAlert.showAlertDialog(getActivity(), "Validation Error", "Attach relevant document image", false);
                return;
            }
            AddDataWithFile();
        }
    }

    private void InitPreData() {
        String url = EndPoints.URL_FARMER_GET_SHORT_INFO + fId;

        // Tag used to cancel the request
        String tag_json_arr = "json_arr_req";

        final Snackbar snackbar = Snackbar
                .make(coordinatorLayout, "Please wait..", Snackbar.LENGTH_INDEFINITE);

        snackbar.show();

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            snackbar.dismiss();
                            JSONObject obj = response.getJSONObject("data");
                            //f_name = obj.getString("first_name");
                            //m_name = obj.getString("middle_name");
                            //l_name = obj.getString("last_name");
                            //dob = obj.getString("dob");
                            urlImagePassportSizePhoto = obj.getString("profile_pic");
                            if (!urlImagePassportSizePhoto.isEmpty()) {
                                isFirstTimePhotoAttach = true;//newly added
                                isAttachedPassportSizePhoto = true;
                                tvLblUploadPassportPhoto.setText("Uploaded");
                                cvBtnAttachPassportPhoto.setBackgroundColor(Color.parseColor("#FD7D00"));
                                ivViewPassportPhoto.setImageDrawable(ContextCompat.getDrawable(getActivity(), R.drawable.ic_view_doc_black));
                                // get bitmap
                                Glide.with(getActivity())
                                        .asBitmap()
                                        .load(urlImagePassportSizePhoto)
                                        .into(new CustomTarget<Bitmap>() {
                                            @Override
                                            public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                                                bitmapPassportSizePhoto = resource;
                                            }

                                            @Override
                                            public void onLoadCleared(@Nullable Drawable placeholder) {
                                            }
                                        });
                            }
                        } catch (JSONException e) {
                            snackbar.dismiss();
                            e.printStackTrace();
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                snackbar.dismiss();
                NetworkResponse networkResponse = error.networkResponse;
                String errorMessage = "Unknown error";
                if (networkResponse == null) {
                    if (error.getClass().equals(TimeoutError.class)) {
                        errorMessage = "Request timeout";
                    } else if (error.getClass().equals(NoConnectionError.class)) {
                        errorMessage = "Failed to connect server";
                    } else if (error.getClass().equals(NetworkError.class)) {
                        errorMessage = "Check your internet connection";
                    }
                } else {
                    String result = new String(networkResponse.data);
                    try {
                        JSONObject response = new JSONObject(result);
                        String message = response.getString("msg");

                        Log.e("Error Message", message);

                        if (networkResponse.statusCode == 404) {
                            errorMessage = "Resource not found";
                        } else if (networkResponse.statusCode == 401) {
                            errorMessage = message + "";
                        } else if (networkResponse.statusCode == 400) {
                            errorMessage = message + "";
                        } else if (networkResponse.statusCode == 500) {
                            errorMessage = message + " Something is getting wrong";
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                Log.i("Error", errorMessage);
                error.printStackTrace();
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                final String token1 = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiJGSUFQUDEwMSIsImlhdCI6MTUxNjIzOTAyMn0._jKrqO1xcUoKoguQgzhhbB3yML0GfsNTo7Mm14Bw0Bo";//Prod
                //final String token1 = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiJGSUFQUDEwMSIsImlhdCI6MTUxNjIzOTAyMn0.mbwX1OXEXaY3BQoa5QPHRlBYKNgFQUpaf4wxqciEMuU";
                headers.put("app-access-token", token1);
                headers.put("Content-Type", "application/json");
                return headers;
            }
        };

        jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(20 * 1000, 0,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(jsonObjectRequest, tag_json_arr);
    }

    private void InitData() {
        String url = "";
        if (userType.equals("farmer"))
            url = EndPoints.URL_FARMER_GET_LEVEL_TWO;
        else if (userType.equals("officer")) {
            switch (intUserType) {
                case 1:// FIE
                    url = EndPoints.URL_EMP_GET_LEVEL_TWO + fId;
                    break;
                case 2:
                    url = EndPoints.URL_AGENT_GET_LEVEL_TWO + fId;
                    break;
                case 3:
                    url = EndPoints.URL_FPO_GET_LEVEL_TWO + fId;
                    break;
                case 4:
                    url = EndPoints.URL_BANK_REP_GET_LEVEL_TWO + fId;
                    break;
                default:
                    Toast.makeText(getActivity(), "Invalid userid!", Toast.LENGTH_SHORT).show();
                    break;
            }
        }
        if (url.isEmpty())
            return;

        // Tag used to cancel the request
        String tag_json_obj = "json_obj_req";

        final Snackbar snackbar = Snackbar
                .make(coordinatorLayout, "Please wait..", Snackbar.LENGTH_INDEFINITE);

        snackbar.show();
        cvBtnSaveAndContinue.setEnabled(false);

        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            snackbar.dismiss();
                            cvBtnSaveAndContinue.setEnabled(true);
                            // Parsing json object response
                            JSONObject data = response.getJSONObject("data");
                            isLevelDone = data.getBoolean("done_level");
                            if (isLevelDone) {
                                if (data.getBoolean("farmer_acc_no_valid")) {
                                    isAccNoValid = true;
                                    accVerifyId = data.getString("farmer_acc_no_verification_id");
                                    tilDbtAccNo.setErrorEnabled(false);
                                    ivAccNoValid.setImageResource(R.drawable.ic_check_circle_green);
                                    tietDbtIfsc.setEnabled(false);
                                    tietDbtAccNo.setEnabled(false);
                                }

                                if (data.getBoolean("farmer_pan_valid")) {
                                    panVerifyId = data.getString("farmer_pan_verification_id");
                                    isPanValid = true;
                                }

                                if (data.getBoolean("farmer_poi_valid")) {
                                    isPoiValid = true;
                                    poiVerifyId = data.getString("farmer_poi_verification_id");
                                    ivPoiValid.setImageResource(R.drawable.ic_check_circle_green);
                                    spinnerIdProof.setEnabled(false);
                                    tietIdProofNo.setEnabled(false);
                                    cbPanCard.setEnabled(false);
                                    if (data.getString("farmer_poi_doc_name").equals("AADHAAR CARD")) {
                                        cvBtnAttachAddressProof.setEnabled(false);
                                    }
                                }
                                if (data.getBoolean("farmer_poa_valid")) {
                                    isPoaValid = true;
                                    poaVerifyId = data.getString("farmer_poa_verification_id");
                                    ivPoaValid.setImageResource(R.drawable.ic_check_circle_green);
                                    spinnerAddressProof.setEnabled(false);
                                    tietAddressProofNo.setEnabled(false);
                                    if (data.getString("farmer_poa_doc_name").equals("AADHAAR CARD")) {
                                        cvBtnAttachAddressProof.setEnabled(false);
                                    }
                                }

                                String fpo_available = data.getString("fpo_available_yn");
                                String pan_available = data.getString("farmer_pan_available_yn");
                                if (fpo_available.equals("true")) {
                                    RadioButton child = (RadioButton) radioGroupQFPO.getChildAt(0);
                                    child.setChecked(true);
                                    qFPO = true;
                                    llFPO.setVisibility(View.VISIBLE);
                                    //Set fpo code spinner
                                    actvFPOName.setText(data.getString("fpo_name"));
                                    tietFpoCode.setText(data.getString("fpo_code"));
                                }
                                tietNoFamilyMember.setText(data.getString("farmer_family_member_count"));
                                String bankDetails = data.getString("farmer_bank_details");
                                String[] componentsBD = bankDetails.split(",");
                                tietDbtAccNo.setText(componentsBD[0].replace("-", ""));
                                actvDbtBankName.setText(componentsBD[1].replace("-", ""));
                                tietDbtIfsc.setText(componentsBD[2].replace("-", ""));
                                if (data.getString("farmer_kcc_holder_yn").equals("true")) {
                                    RadioButton child = (RadioButton) radGroupQKloan.getChildAt(0);
                                    child.setChecked(true);
                                    qKLoan = true;
                                    llGbQKLoan.setVisibility(View.VISIBLE);

                                    String kccLoanDetails = data.getString("farmer_kcc_details");
                                    String[] componentsKLD = kccLoanDetails.split(",");
                                    spinnerKccLoanType.setSelection(getIndex(spinnerKccLoanType, componentsKLD[0]));
                                    tietKLoanAccNo.setText(componentsKLD[1].replace("-", ""));
                                    actvKLoanBankName.setText(componentsKLD[2].replace("-", ""));
                                    tietKIfsc.setText(componentsKLD[3].replace("-", ""));
                                    tietKLoanAmount.setText(componentsKLD[4].replace("-", ""));
                                    spinnerKccLoanTenure.setSelection(getIndex(spinnerKccLoanTenure, componentsKLD[5]));
                                    tietKLoanObligation.setText(componentsKLD[6].replace("-", ""));
                                }

                                if (data.getString("farmer_crp_ln_ins_availed_yn").equals("true")) {
                                    RadioButton child = (RadioButton) radGroupQClInsurance.getChildAt(0);
                                    child.setChecked(true);
                                    qClInsuranceLoan = true;
                                }

                                if (data.getString("farmer_other_loan_availed_yn").equals("true")) {
                                    RadioButton child = (RadioButton) radGroupQOtherLoan.getChildAt(0);
                                    child.setChecked(true);
                                    qOtherLoan = true;
                                    llGbOtherLoan.setVisibility(View.VISIBLE);

                                    String otherLoanDetails = data.getString("farmer_other_loan_details");
                                    String[] componentsOLD = otherLoanDetails.split(",");
                                    spinnerOtherLoanType.setSelection(getIndex(spinnerOtherLoanType, componentsOLD[0]));
                                    tietOtherLoanAccNo.setText(componentsOLD[1].replace("-", ""));
                                    actvOtherLoanBankName.setText(componentsOLD[2].replace("-", ""));
                                    tietOtherIfsc.setText(componentsOLD[3].replace("-", ""));
                                    tietOtherLoanAmount.setText(componentsOLD[4].replace("-", ""));
                                    spinnerOtherLoanTenure.setSelection(getIndex(spinnerOtherLoanTenure, componentsOLD[5]));
                                    tietOtherLoanObligation.setText(componentsOLD[6].replace("-", ""));
                                }
                                if (pan_available.equals("true")) {
                                    cbPanCard.setChecked(true);
                                    qPanAvailable = true;
                                }
                                tietIdProofNo.setText(data.getString("farmer_poi_doc_number"));
                                spinnerIdProof.setSelection(getIndex(spinnerIdProof, data.getString("farmer_poi_doc_name")));

                                spinnerAddressProof.setSelection(getIndex(spinnerAddressProof, data.getString("farmer_poa_doc_name")));
                                tietAddressProofNo.setText(data.getString("farmer_poa_doc_number"));

                                urlImagePassportSizePhoto = data.getString("farmer_passport_size_photo_img");
                                if (!urlImagePassportSizePhoto.isEmpty()) {
                                    isFirstTimePhotoAttach = false;//newly added
                                    tvLblUploadPassportPhoto.setText("Uploaded");
                                    cvBtnAttachPassportPhoto.setBackgroundColor(Color.parseColor("#FD7D00"));
                                    ivViewPassportPhoto.setImageDrawable(ContextCompat.getDrawable(getActivity(), R.drawable.ic_view_doc_black));
                                    // get bitmap
                                    Glide.with(getActivity())
                                            .asBitmap()
                                            .load(urlImagePassportSizePhoto)
                                            .into(new CustomTarget<Bitmap>() {
                                                @Override
                                                public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                                                    bitmapPassportSizePhoto = resource;
                                                }

                                                @Override
                                                public void onLoadCleared(@Nullable Drawable placeholder) {
                                                }
                                            });
                                }

                                urlImagePassbook = data.getString("farmer_passbook_img");
                                if (!urlImagePassbook.isEmpty()) {
                                    tvLblUploadPassbook.setText("Uploaded");
                                    cvBtnAttachPassbook.setBackgroundColor(Color.parseColor("#FD7D00"));
                                    ivViewPassbook.setImageDrawable(ContextCompat.getDrawable(getActivity(), R.drawable.ic_view_doc_black));
                                }

                                urlImageIdProofFront = data.getString("farmer_poi_img_front");
                                if (!urlImageIdProofFront.isEmpty()) {
                                    tvLblUploadIdProof.setText("Uploaded");
                                    cvBtnAttachIdProof.setBackgroundColor(Color.parseColor("#FD7D00"));
                                    ivViewIdProof.setImageDrawable(ContextCompat.getDrawable(getActivity(), R.drawable.ic_view_doc_black));
                                }

                                urlImageIdProofBack = data.getString("farmer_poi_img_back");
                                if (!urlImageIdProofBack.isEmpty()) {
                                    tvLblUploadIdProof.setText("Uploaded");
                                    cvBtnAttachIdProof.setBackgroundColor(Color.parseColor("#FD7D00"));
                                    ivViewIdProof.setImageDrawable(ContextCompat.getDrawable(getActivity(), R.drawable.ic_view_doc_black));
                                }

                                urlImageAddressProofFront = data.getString("farmer_poa_img_front");
                                if (!urlImageAddressProofFront.isEmpty()) {
                                    tvLblUploadAddressProof.setText("Uploaded");
                                    cvBtnAttachAddressProof.setBackgroundColor(Color.parseColor("#FD7D00"));
                                    ivViewAddressProof.setImageDrawable(ContextCompat.getDrawable(getActivity(), R.drawable.ic_view_doc_black));
                                }

                                urlImageAddressProofBack = data.getString("farmer_poa_img_back");
                                if (!urlImageAddressProofBack.isEmpty()) {
                                    tvLblUploadAddressProofBack.setText("Uploaded");
                                    cvBtnAttachAddressProofBack.setBackgroundColor(Color.parseColor("#FD7D00"));
                                    ivViewAddressProofBack.setImageDrawable(ContextCompat.getDrawable(getActivity(), R.drawable.ic_view_doc_black));
                                }
                            } else {
                                InitPreData();
                            }
                        } catch (JSONException e) {
                            snackbar.dismiss();
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                snackbar.dismiss();
                cvBtnSaveAndContinue.setEnabled(true);
                NetworkResponse networkResponse = error.networkResponse;
                String errorMessage = "Unknown error";
                if (networkResponse == null) {
                    if (error.getClass().equals(TimeoutError.class)) {
                        errorMessage = "Request timeout";
                    } else if (error.getClass().equals(NoConnectionError.class)) {
                        errorMessage = "Failed to connect server";
                    } else if (error.getClass().equals(NetworkError.class)) {
                        errorMessage = "Check your internet connection";
                    }
                } else {
                    String result = new String(networkResponse.data);
                    try {
                        JSONObject response = new JSONObject(result);
                        String message = response.getString("msg");
                        Log.e("Error Message", message);
                        if (networkResponse.statusCode == 404) {
                            errorMessage = "Resource not found";
                        } else if (networkResponse.statusCode == 401) {
                            errorMessage = message + "";
                        } else if (networkResponse.statusCode == 400) {
                            errorMessage = message + "";
                        } else if (networkResponse.statusCode == 500) {
                            errorMessage = message + " Something is getting wrong";
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                Log.i("Error", errorMessage);
                error.printStackTrace();
                if (!errorMessage.equals("C404"))
                    Toast.makeText(getActivity(), errorMessage, Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                headers.put("x-access-token", token);
                headers.put("Content-Type", "application/json");
                return headers;
            }
        };

        jsonObjReq.setRetryPolicy(new DefaultRetryPolicy(20 * 1000, 0,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(jsonObjReq, tag_json_obj);
    }

    private void initFpo() {
        String url = EndPoints.URL_GET_FPO;
        if (userType.equals("officer")) {
            if (intUserType == 2 && isAgentMappedToFpo) {
                url = EndPoints.URL_GET_AGENT_MAPPED_FPO + loginId;
            } else if (intUserType == 3) {
                url = EndPoints.URL_FPO_GET_FPO + loginId;
            }
        }
        // Tag used to cancel the request
        String tag_json_arr = "json_arr_req";

        final Snackbar snackbar = Snackbar
                .make(coordinatorLayout, "Please wait..", Snackbar.LENGTH_INDEFINITE);

        snackbar.show();
        cvBtnSaveAndContinue.setEnabled(false);

        JsonArrayRequest jsonArrReq = new JsonArrayRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        try {
                            snackbar.dismiss();
                            cvBtnSaveAndContinue.setEnabled(true);
                            // Parsing json array response
                            // loop through each json object
                            lstFPOCode.clear();
                            lstFpoName.clear();

                            for (int i = 0; i < response.length(); i++) {
                                JSONObject objFpo = (JSONObject) response.getJSONObject(i);
                                String fpoCode = objFpo.getString("fpo_code");
                                String fpoName = objFpo.getString("fpo_name");
                                lstFPOCode.add(fpoCode);
                                lstFpoName.add(fpoName);
                                mapFpo.put(fpoCode, fpoName);
                            }
                            lstFPOCode.add("FPO");
                            lstFpoName.add("Not In List");
                            mapFpo.put("FPO0", "Not In List");
                            ArrayAdapter<String> adapterFpoName = new ArrayAdapter<>(getActivity(),
                                    android.R.layout.simple_list_item_1, lstFpoName);

                            ArrayAdapter<String> dataAdapterFPOCode = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, lstFPOCode);

                            dataAdapterFPOCode.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                            actvFPOName.setThreshold(1);
                            actvFPOName.setAdapter(adapterFpoName);

                            // Set first item from list in case of agent and fpo
                            if (intUserType == 2 && isAgentMappedToFpo || intUserType == 3) {
                                RadioButton child = (RadioButton) radioGroupQFPO.getChildAt(0);
                                child.setChecked(true);
                                qFPO = true;
                                llFPO.setVisibility(View.VISIBLE);
                                actvFPOName.setText(lstFpoName.get(0));
                                String fpoCode = getSingleKeyFromValue(mapFpo, lstFpoName.get(0));
                                tietFpoCode.setText(fpoCode);
                            }
                        } catch (JSONException e) {
                            snackbar.dismiss();
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                snackbar.dismiss();
                cvBtnSaveAndContinue.setEnabled(true);
                NetworkResponse networkResponse = error.networkResponse;
                String errorMessage = "Unknown error";
                if (networkResponse == null) {
                    if (error.getClass().equals(TimeoutError.class)) {
                        errorMessage = "Request timeout";
                    } else if (error.getClass().equals(NoConnectionError.class)) {
                        errorMessage = "Failed to connect server";
                    } else if (error.getClass().equals(NetworkError.class)) {
                        errorMessage = "Check your internet connection";
                    }
                } else {
                    String result = new String(networkResponse.data);
                    try {
                        JSONObject response = new JSONObject(result);
                        String message = response.getString("msg");

                        Log.e("Error Message", message);

                        if (networkResponse.statusCode == 404) {
                            errorMessage = "Resource not found";
                        } else if (networkResponse.statusCode == 401) {
                            errorMessage = message + "";
                        } else if (networkResponse.statusCode == 400) {
                            errorMessage = message + "";
                        } else if (networkResponse.statusCode == 500) {
                            errorMessage = message + " Something is getting wrong";
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                Log.i("Error", errorMessage);
                error.printStackTrace();
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                final String token1 = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiJGSUFQUDEwMSIsImlhdCI6MTUxNjIzOTAyMn0._jKrqO1xcUoKoguQgzhhbB3yML0GfsNTo7Mm14Bw0Bo";//Prod
                //final String token1 = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiJGSUFQUDEwMSIsImlhdCI6MTUxNjIzOTAyMn0.mbwX1OXEXaY3BQoa5QPHRlBYKNgFQUpaf4wxqciEMuU";
                headers.put("app-access-token", token1);
                headers.put("Content-Type", "application/json");
                return headers;
            }
        };

        jsonArrReq.setRetryPolicy(new DefaultRetryPolicy(20 * 1000, 0,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(jsonArrReq, tag_json_arr);
    }

    private void AddDataWithFile() {
        String url = "";
        if (userType.equals("farmer"))
            url = EndPoints.URL_FARMER_ADD_FORM_TWO;
        else if (userType.equals("officer")) {
            switch (intUserType) {
                case 1:// FIE
                    url = EndPoints.URL_EMP_ADD_FORM_TWO;
                    break;
                case 2:
                    url = EndPoints.URL_AGENT_ADD_FORM_TWO;
                    break;
                case 3:
                    url = EndPoints.URL_FPO_ADD_FORM_TWO;
                    break;
                case 4:
                    url = EndPoints.URL_BANK_REP_ADD_FORM_TWO;
                    break;
                default:
                    Toast.makeText(getActivity(), "Invalid userid!", Toast.LENGTH_SHORT).show();
                    break;
            }
        }
        if (url.isEmpty())
            return;

        final String tag_custom_multi_part_obj = "custom_multi_part_obj_req";

        final Snackbar snackbar = Snackbar
                .make(coordinatorLayout, "Saving..", Snackbar.LENGTH_INDEFINITE);

        snackbar.show();

        // Custom Volley Request
        VolleyMultipartRequest volleyMultipartRequest = new VolleyMultipartRequest(Request.Method.POST, url,
                new Response.Listener<NetworkResponse>() {
                    @Override
                    public void onResponse(NetworkResponse response) {
                        try {
                            JSONObject obj = new JSONObject(new String(response.data));
                            boolean isSuccess = obj.getBoolean("success");
                            String message = obj.getString("msg");
                            if (isSuccess) {
                                snackbar.dismiss();
                                //FarmAuthentication nextFrag = new FarmAuthentication();
                                EditLevelThreeFragment nextFrag = new EditLevelThreeFragment();
                                // Pass mode
                                Bundle args = new Bundle();
                                //args.putString("MODE", "add");
                                args.putString("FID", fId);
                                nextFrag.setArguments(args);
                                FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                                transaction.replace(R.id.fragment_container, nextFrag, "AddEditFragment");
                                transaction.addToBackStack(null);
                                transaction.commit();
                                Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            snackbar.dismiss();
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        snackbar.dismiss();

                        NetworkResponse networkResponse = error.networkResponse;
                        String errorMessage = "Unknown error";
                        if (networkResponse == null) {
                            if (error.getClass().equals(TimeoutError.class)) {
                                errorMessage = "Request timeout";
                            } else if (error.getClass().equals(NoConnectionError.class)) {
                                errorMessage = "Failed to connect server";
                            }
                        } else {
                            String result = new String(networkResponse.data);
                            try {
                                JSONObject response = new JSONObject(result);
                                String message = response.getString("msg");

                                Log.e("Error Message", message);

                                if (networkResponse.statusCode == 404) {
                                    errorMessage = "Resource not found";
                                } else if (networkResponse.statusCode == 401) {
                                    errorMessage = message + "";
                                } else if (networkResponse.statusCode == 400) {
                                    errorMessage = message + "";
                                } else if (networkResponse.statusCode == 500) {
                                    errorMessage = message + " Something is getting wrong";
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                        Log.i("Error", errorMessage);
                        error.printStackTrace();
                        Toast.makeText(getActivity(), errorMessage, Toast.LENGTH_SHORT).show();
                    }
                }) {

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                headers.put("x-access-token", token);
                return headers;
            }

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                String fpoAvailable = String.valueOf(qFPO);
                String fpoCode = null;
                String fpoName = null;
                if (qFPO) {
                    fpoCode = tietFpoCode.getText().toString();
                    fpoName = actvFPOName.getText().toString().trim();
                }

                String noOfFamilyMember = tietNoFamilyMember.getText().toString().trim();

                String bankAccNo = tietDbtAccNo.getText().toString();
                String bankName = actvDbtBankName.getText().toString().trim();
                String bankIfsc = tietDbtIfsc.getText().toString().trim();
                if (bankAccNo.isEmpty())
                    bankAccNo = bankAccNo + ("-");
                if (bankName.isEmpty())
                    bankName = bankName + ("-");
                if (bankIfsc.isEmpty())
                    bankIfsc = bankIfsc + ("-");

                String kLoanType = spinnerKccLoanType.getSelectedItem().toString();
                String kAccNo = tietKLoanAccNo.getText().toString().trim();
                String kBankName = actvKLoanBankName.getText().toString().trim();
                String kIfsc = tietKIfsc.getText().toString().trim();
                String kLoanTenure = spinnerKccLoanTenure.getSelectedItem().toString();
                String kLoanAmt = tietKLoanAmount.getText().toString().trim();
                String kLoanObligation = tietKLoanObligation.getText().toString().trim();
                if (kAccNo.isEmpty())
                    kAccNo = kAccNo + ("-");
                if (kBankName.isEmpty())
                    kBankName = kBankName + ("-");
                if (kIfsc.isEmpty())
                    kIfsc = kIfsc + ("-");
                if (kLoanAmt.isEmpty())
                    kLoanAmt = kLoanAmt + ("-");
                if (kLoanObligation.isEmpty())
                    kLoanObligation = kLoanObligation + ("-");

                String otherLoanType = spinnerOtherLoanType.getSelectedItem().toString();
                String otherLoanAccNo = tietOtherLoanAccNo.getText().toString().trim();
                String otherBankName = actvOtherLoanBankName.getText().toString().trim();
                String otherIfsc = tietOtherIfsc.getText().toString().trim();
                String otherLoanTenure = spinnerOtherLoanTenure.getSelectedItem().toString();
                String otherLoanAmt = tietOtherLoanAmount.getText().toString().trim();
                String otherLoanObligation = tietOtherLoanObligation.getText().toString().trim();
                if (otherLoanAccNo.isEmpty())
                    otherLoanAccNo = otherLoanAccNo + ("-");
                if (otherBankName.isEmpty())
                    otherBankName = otherBankName + ("-");
                if (otherIfsc.isEmpty())
                    otherIfsc = otherIfsc + ("-");
                if (otherLoanAmt.isEmpty())
                    otherLoanAmt = otherLoanAmt + ("-");
                if (otherLoanObligation.isEmpty())
                    otherLoanObligation = otherLoanObligation + ("-");

                String panNumber = null;
                String idProofNumber;
                String idProofDocName;
                if (cbPanCard.isChecked()) {
                    qPanAvailable = true;
                    panNumber = tietIdProofNo.getText().toString().trim();
                    idProofNumber = panNumber;
                    idProofDocName = "PAN CARD";
                } else {
                    idProofNumber = tietIdProofNo.getText().toString().trim();
                    idProofDocName = spinnerIdProof.getSelectedItem().toString();
                    if (idProofDocName.equals("PAN CARD")) {
                        qPanAvailable = true;
                        panNumber = idProofNumber;
                    } else {
                        qPanAvailable = false;
                    }
                }

                String addressProofDocName = spinnerAddressProof.getSelectedItem().toString();
                String addressProofNumber = tietAddressProofNo.getText().toString().trim();
                boolean done_level = true;

                // Add Parameters
                params.put("user_id", fId);
                params.put("fpo_available_yn", fpoAvailable);
                params.put("fpo_code", fpoCode);
                params.put("fpo_name", fpoName);
                params.put("farmer_family_member_count", noOfFamilyMember);
                params.put("farmer_bank_details", bankAccNo + "," + bankName + "," + bankIfsc);
                params.put("farmer_acc_no_valid", String.valueOf(isAccNoValid));
                params.put("farmer_acc_no_verification_id", accVerifyId);
                params.put("farmer_kcc_holder_yn", String.valueOf(qKLoan));
                params.put("farmer_kcc_details", kLoanType + "," + kAccNo + "," + kBankName + "," + kIfsc + "," + kLoanAmt + "," + kLoanTenure + "," + kLoanObligation);
                params.put("farmer_crp_ln_ins_availed_yn", String.valueOf(qClInsuranceLoan));
                params.put("farmer_other_loan_availed_yn", String.valueOf(qOtherLoan));
                params.put("farmer_other_loan_details", otherLoanType + "," + otherLoanAccNo + "," + otherBankName + "," + otherIfsc + "," + otherLoanAmt + "," + otherLoanTenure + "," + otherLoanObligation);
                params.put("farmer_pan_available_yn", String.valueOf(qPanAvailable));
                params.put("farmer_pan_number", panNumber);
                params.put("farmer_pan_valid", String.valueOf(isPanValid));
                params.put("farmer_pan_verification_id", panVerifyId);
                params.put("farmer_poi_doc_name", idProofDocName);
                params.put("farmer_poi_doc_number", idProofNumber);
                params.put("farmer_poi_valid", String.valueOf(isPoiValid));
                params.put("farmer_poi_verification_id", poiVerifyId);
                params.put("farmer_poa_doc_name", addressProofDocName);
                params.put("farmer_poa_doc_number", addressProofNumber);
                params.put("farmer_poa_valid", String.valueOf(isPoaValid));
                params.put("farmer_poa_verification_id", poaVerifyId);
                params.put("farmer_contact_me_yn", String.valueOf(qContact));
                params.put("done_level", String.valueOf(done_level));

                return params;
            }

            /*
             * Here we are passing image by renaming it with a unique name
             * */
            @Override
            protected Map<String, DataPart> getByteData() {
                Map<String, DataPart> params = new HashMap<>();
                long imgNamePassportSizePhoto = System.currentTimeMillis() + 1;
                long imgNamePassbook = System.currentTimeMillis() + 2;
                long imgNamePan = System.currentTimeMillis() + 3;
                long imgNameIdProof = System.currentTimeMillis() + 4;
                long imgNameAddressProof = System.currentTimeMillis() + 5;
                long imgNameAddressProofBack = System.currentTimeMillis() + 6;
                if (isAttachedPassportSizePhoto) {
                    params.put("img_passport_size_photo", new DataPart(imgNamePassportSizePhoto + ".jpg", getFileDataFromDrawable(bitmapPassportSizePhoto)));
                }
                if (isAttachedPassbook) {
                    params.put("img_passbook", new DataPart(imgNamePassbook + ".jpg", getFileDataFromDrawable(bitmapPassbook)));
                }
                if (isAttachedPan) {
                    params.put("img_pan_card", new DataPart(imgNamePan + ".jpg", getFileDataFromDrawable(bitmapPan)));
                }
                if (isAttachedIdProof) {
                    params.put("img_id_proof_front", new DataPart(imgNameIdProof + ".jpg", getFileDataFromDrawable(bitmapIdProof)));
                }
                if (isAttachedAddressProof) {
                    params.put("img_address_proof_front", new DataPart(imgNameAddressProof + ".jpg", getFileDataFromDrawable(bitmapAddressProof)));
                }
                if (isAttachedAddressProofBack) {
                    params.put("img_address_proof_back", new DataPart(imgNameAddressProofBack + ".jpg", getFileDataFromDrawable(bitmapAddressProofBack)));
                }

                return params;
            }
        };

        volleyMultipartRequest.setRetryPolicy(new DefaultRetryPolicy(20 * 1000, 0,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(volleyMultipartRequest, tag_custom_multi_part_obj);
    }

    private void EditDataWithFile() {
        String url = "";
        if (userType.equals("farmer"))
            url = EndPoints.URL_FARMER_UPDATE_FORM_TWO;
        else if (userType.equals("officer")) {
            switch (intUserType) {
                case 1:// FIE
                    url = EndPoints.URL_EMP_UPDATE_FORM_TWO + fId;
                    break;
                case 2:
                    url = EndPoints.URL_AGENT_UPDATE_FORM_TWO + fId;
                    break;
                case 3:
                    url = EndPoints.URL_FPO_UPDATE_FORM_TWO + fId;
                    break;
                case 4:
                    url = EndPoints.URL_BANK_REP_UPDATE_FORM_TWO + fId;
                    break;
                default:
                    Toast.makeText(getActivity(), "Invalid userid!", Toast.LENGTH_SHORT).show();
                    break;
            }
        }
        if (url.isEmpty())
            return;

        final String tag_custom_multi_part_obj = "custom_multi_part_obj_req";

        final Snackbar snackbar = Snackbar
                .make(coordinatorLayout, "Saving..", Snackbar.LENGTH_INDEFINITE);

        snackbar.show();

        // Custom Volley Request
        VolleyMultipartRequest volleyMultipartRequest = new VolleyMultipartRequest(Request.Method.PUT, url,
                new Response.Listener<NetworkResponse>() {
                    @Override
                    public void onResponse(NetworkResponse response) {
                        try {
                            JSONObject obj = new JSONObject(new String(response.data));
                            boolean isSuccess = obj.getBoolean("success");
                            String message = obj.getString("msg");
                            if (isSuccess) {
                                snackbar.dismiss();
                                Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
                                //FarmAuthentication nextFrag = new FarmAuthentication();
                                EditLevelThreeFragment nextFrag = new EditLevelThreeFragment();
                                // Pass mode
                                Bundle args = new Bundle();
                                //args.putString("MODE", "add");
                                args.putString("FID", fId);
                                nextFrag.setArguments(args);
                                FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                                transaction.replace(R.id.fragment_container, nextFrag, "AddEditFragment");
                                transaction.addToBackStack(null);
                                transaction.commit();
                                //onBackPressed();
                            }
                        } catch (JSONException e) {
                            snackbar.dismiss();
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        snackbar.dismiss();

                        NetworkResponse networkResponse = error.networkResponse;
                        String errorMessage = "Unknown error";
                        if (networkResponse == null) {
                            if (error.getClass().equals(TimeoutError.class)) {
                                errorMessage = "Request timeout";
                            } else if (error.getClass().equals(NoConnectionError.class)) {
                                errorMessage = "Failed to connect server";
                            }
                        } else {
                            String result = new String(networkResponse.data);
                            try {
                                JSONObject response = new JSONObject(result);
                                String message = response.getString("msg");

                                Log.e("Error Message", message);

                                if (networkResponse.statusCode == 404) {
                                    errorMessage = "Resource not found";
                                } else if (networkResponse.statusCode == 401) {
                                    errorMessage = message + "";
                                } else if (networkResponse.statusCode == 400) {
                                    errorMessage = message + "";
                                } else if (networkResponse.statusCode == 500) {
                                    errorMessage = message + " Something is getting wrong";
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                        Log.i("Error", errorMessage);
                        error.printStackTrace();
                        Toast.makeText(getActivity(), errorMessage, Toast.LENGTH_SHORT).show();
                    }
                }) {

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                headers.put("x-access-token", token);
                return headers;
            }

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                String fpoAvailable = String.valueOf(qFPO);
                String fpoCode = null;
                String fpoName = null;
                if (qFPO) {
                    fpoCode = tietFpoCode.getText().toString();
                    fpoName = actvFPOName.getText().toString().trim();
                }

                String noOfFamilyMember = tietNoFamilyMember.getText().toString().trim();

                String bankAccNo = tietDbtAccNo.getText().toString();
                String bankName = actvDbtBankName.getText().toString().trim();
                String bankIfsc = tietDbtIfsc.getText().toString().trim();
                if (bankAccNo.isEmpty())
                    bankAccNo = bankAccNo + ("-");
                if (bankName.isEmpty())
                    bankName = bankName + ("-");
                if (bankIfsc.isEmpty())
                    bankIfsc = bankIfsc + ("-");

                String kLoanType = spinnerKccLoanType.getSelectedItem().toString();
                String kAccNo = tietKLoanAccNo.getText().toString().trim();
                String kBankName = actvKLoanBankName.getText().toString().trim();
                String kIfsc = tietKIfsc.getText().toString().trim();
                String kLoanTenure = spinnerKccLoanTenure.getSelectedItem().toString();
                String kLoanAmt = tietKLoanAmount.getText().toString().trim();
                String kLoanObligation = tietKLoanObligation.getText().toString().trim();
                if (kAccNo.isEmpty())
                    kAccNo = kAccNo + ("-");
                if (kBankName.isEmpty())
                    kBankName = kBankName + ("-");
                if (kIfsc.isEmpty())
                    kIfsc = kIfsc + ("-");
                if (kLoanAmt.isEmpty())
                    kLoanAmt = kLoanAmt + ("-");
                if (kLoanObligation.isEmpty())
                    kLoanObligation = kLoanObligation + ("-");

                String otherLoanType = spinnerOtherLoanType.getSelectedItem().toString();
                String otherLoanAccNo = tietOtherLoanAccNo.getText().toString().trim();
                String otherBankName = actvOtherLoanBankName.getText().toString().trim();
                String otherIfsc = tietOtherIfsc.getText().toString().trim();
                String otherLoanTenure = spinnerOtherLoanTenure.getSelectedItem().toString();
                String otherLoanAmt = tietOtherLoanAmount.getText().toString().trim();
                String otherLoanObligation = tietOtherLoanObligation.getText().toString().trim();
                if (otherLoanAccNo.isEmpty())
                    otherLoanAccNo = otherLoanAccNo + ("-");
                if (otherBankName.isEmpty())
                    otherBankName = otherBankName + ("-");
                if (otherIfsc.isEmpty())
                    otherIfsc = otherIfsc + ("-");
                if (otherLoanAmt.isEmpty())
                    otherLoanAmt = otherLoanAmt + ("-");
                if (otherLoanObligation.isEmpty())
                    otherLoanObligation = otherLoanObligation + ("-");

                String panNumber = null;
                String idProofNumber;
                String idProofDocName;
                if (cbPanCard.isChecked()) {
                    qPanAvailable = true;
                    panNumber = tietIdProofNo.getText().toString().trim();
                    idProofNumber = panNumber;
                    idProofDocName = "PAN CARD";
                } else {
                    idProofNumber = tietIdProofNo.getText().toString().trim();
                    idProofDocName = spinnerIdProof.getSelectedItem().toString();
                    if (idProofDocName.equals("PAN CARD")) {
                        qPanAvailable = true;
                        panNumber = idProofNumber;
                    } else {
                        qPanAvailable = false;
                    }
                }

                String addressProofDocName = spinnerAddressProof.getSelectedItem().toString();
                String addressProofNumber = tietAddressProofNo.getText().toString().trim();

                // Add Parameters
                params.put("fpo_available_yn", fpoAvailable);
                params.put("fpo_code", fpoCode);
                params.put("fpo_name", fpoName);
                params.put("farmer_family_member_count", noOfFamilyMember);
                params.put("farmer_bank_details", bankAccNo + "," + bankName + "," + bankIfsc);
                params.put("farmer_acc_no_valid", String.valueOf(isAccNoValid));
                params.put("farmer_acc_no_verification_id", accVerifyId);
                params.put("farmer_kcc_holder_yn", String.valueOf(qKLoan));
                params.put("farmer_kcc_details", kLoanType + "," + kAccNo + "," + kBankName + "," + kIfsc + "," + kLoanAmt + "," + kLoanTenure + "," + kLoanObligation);
                params.put("farmer_crp_ln_ins_availed_yn", String.valueOf(qClInsuranceLoan));
                params.put("farmer_other_loan_availed_yn", String.valueOf(qOtherLoan));
                params.put("farmer_other_loan_details", otherLoanType + "," + otherLoanAccNo + "," + otherBankName + "," + otherIfsc + "," + otherLoanAmt + "," + otherLoanTenure + "," + otherLoanObligation);
                params.put("farmer_pan_available_yn", String.valueOf(qPanAvailable));
                params.put("farmer_pan_number", panNumber);
                params.put("farmer_pan_valid", String.valueOf(isPanValid));
                params.put("farmer_pan_verification_id", panVerifyId);
                params.put("farmer_poi_doc_name", idProofDocName);
                params.put("farmer_poi_doc_number", idProofNumber);
                params.put("farmer_poi_valid", String.valueOf(isPoiValid));
                params.put("farmer_poi_verification_id", poiVerifyId);
                params.put("farmer_poa_doc_name", addressProofDocName);
                params.put("farmer_poa_doc_number", addressProofNumber);
                params.put("farmer_poa_valid", String.valueOf(isPoaValid));
                params.put("farmer_poa_verification_id", poaVerifyId);
                params.put("farmer_contact_me_yn", String.valueOf(qContact));

                return params;
            }

            /*
             * Here we are passing image by renaming it with a unique name
             * */
            @Override
            protected Map<String, DataPart> getByteData() {
                Map<String, DataPart> params = new HashMap<>();
                long imgNamePassportSizePhoto = System.currentTimeMillis() + 1;
                long imgNamePassbook = System.currentTimeMillis() + 2;
                long imgNamePan = System.currentTimeMillis() + 3;
                long imgNameIdProof = System.currentTimeMillis() + 4;
                long imgNameAddressProof = System.currentTimeMillis() + 5;
                long imgNameAddressProofBack = System.currentTimeMillis() + 6;
                ;
                if (isAttachedPassportSizePhoto) {
                    params.put("img_passport_size_photo", new DataPart(imgNamePassportSizePhoto + ".jpg", getFileDataFromDrawable(bitmapPassportSizePhoto)));
                }
                if (isAttachedPassbook) {
                    params.put("img_passbook", new DataPart(imgNamePassbook + ".jpg", getFileDataFromDrawable(bitmapPassbook)));
                }
                if (isAttachedPan) {
                    params.put("img_pan_card", new DataPart(imgNamePan + ".jpg", getFileDataFromDrawable(bitmapPan)));
                }
                if (isAttachedIdProof) {
                    params.put("img_id_proof_front", new DataPart(imgNameIdProof + ".jpg", getFileDataFromDrawable(bitmapIdProof)));
                }
                if (isAttachedAddressProof) {
                    params.put("img_address_proof_front", new DataPart(imgNameAddressProof + ".jpg", getFileDataFromDrawable(bitmapAddressProof)));
                }
                if (isAttachedAddressProofBack) {
                    params.put("img_address_proof_back", new DataPart(imgNameAddressProofBack + ".jpg", getFileDataFromDrawable(bitmapAddressProofBack)));
                }
                return params;
            }
        };

        volleyMultipartRequest.setRetryPolicy(new DefaultRetryPolicy(20 * 1000, 0,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(volleyMultipartRequest, tag_custom_multi_part_obj);
    }

    //Set spinner text
    private int getIndex(Spinner spinner, String myString) {
        for (int i = 0; i < spinner.getCount(); i++) {
            if (spinner.getItemAtPosition(i).toString().equalsIgnoreCase(myString)) {
                return i;
            }
        }
        return 0;
    }

    private void showImagePickerOptions(View view) {
        ImagePickerActivity.showImagePickerOptions(getActivity(), new ImagePickerActivity.PickerOptionListener() {
            @Override
            public void onTakeCameraSelected() {
                launchCameraIntent(view);
            }

            @Override
            public void onChooseGallerySelected() {
                launchGalleryIntent(view);
            }
        });
    }

    private void launchCameraIntent(View view) {
        Intent intent = new Intent(getActivity(), ImagePickerActivity.class);
        intent.putExtra(ImagePickerActivity.INTENT_IMAGE_PICKER_OPTION, ImagePickerActivity.REQUEST_IMAGE_CAPTURE);

        // setting aspect ratio
        //intent.putExtra(ImagePickerActivity.INTENT_LOCK_ASPECT_RATIO, true);
        //intent.putExtra(ImagePickerActivity.INTENT_ASPECT_RATIO_X, 1); // 16x9, 1x1, 3:4, 3:2
        //intent.putExtra(ImagePickerActivity.INTENT_ASPECT_RATIO_Y, 1);

        // setting maximum bitmap width and height
        intent.putExtra(ImagePickerActivity.INTENT_SET_BITMAP_MAX_WIDTH_HEIGHT, true);
        intent.putExtra(ImagePickerActivity.INTENT_BITMAP_MAX_WIDTH, 600);
        intent.putExtra(ImagePickerActivity.INTENT_BITMAP_MAX_HEIGHT, 600);

        switch (view.getId()) {
            case R.id.cv_btn_upload_passport_image_fragment_edit_level_two:
                startActivityForResult(intent, CROP_PASSPORT_PHOTO_IMAGE_REQUEST);
                break;
            case R.id.cv_btn_upload_passbook_fragment_edit_level_two:
                startActivityForResult(intent, CROP_PASSBOOK_IMAGE_REQUEST);
                break;
            case R.id.cv_btn_upload_poi_fragment_edit_level_two:
                startActivityForResult(intent, CROP_ID_PROOF_IMAGE_REQUEST);
                break;
            case R.id.cv_btn_upload_poa_fragment_edit_level_two:
                startActivityForResult(intent, CROP_ADDRESS_PROOF_IMAGE_REQUEST);
                break;
            case R.id.cv_btn_upload_poa_back_fragment_edit_level_two:
                startActivityForResult(intent, CROP_ADDRESS_PROOF_IMAGE_REQUEST_BACK);
                break;
            default:
                break;
        }
    }

    private void launchGalleryIntent(View view) {
        Intent intent = new Intent(getActivity(), ImagePickerActivity.class);
        intent.putExtra(ImagePickerActivity.INTENT_IMAGE_PICKER_OPTION, ImagePickerActivity.REQUEST_GALLERY_IMAGE);

        // setting aspect ratio
        //intent.putExtra(ImagePickerActivity.INTENT_LOCK_ASPECT_RATIO, true);
        //intent.putExtra(ImagePickerActivity.INTENT_ASPECT_RATIO_X, 1); // 16x9, 1x1, 3:4, 3:2
        //intent.putExtra(ImagePickerActivity.INTENT_ASPECT_RATIO_Y, 1);

        // setting maximum bitmap width and height
        intent.putExtra(ImagePickerActivity.INTENT_SET_BITMAP_MAX_WIDTH_HEIGHT, true);
        intent.putExtra(ImagePickerActivity.INTENT_BITMAP_MAX_WIDTH, 600);
        intent.putExtra(ImagePickerActivity.INTENT_BITMAP_MAX_HEIGHT, 600);

        switch (view.getId()) {
            case R.id.cv_btn_upload_passport_image_fragment_edit_level_two:
                startActivityForResult(intent, CROP_PASSPORT_PHOTO_IMAGE_REQUEST);
                break;
            case R.id.cv_btn_upload_passbook_fragment_edit_level_two:
                startActivityForResult(intent, CROP_PASSBOOK_IMAGE_REQUEST);
                break;
            case R.id.cv_btn_upload_poi_fragment_edit_level_two:
                startActivityForResult(intent, CROP_ID_PROOF_IMAGE_REQUEST);
                break;
            case R.id.cv_btn_upload_poa_fragment_edit_level_two:
                startActivityForResult(intent, CROP_ADDRESS_PROOF_IMAGE_REQUEST);
                break;
            case R.id.cv_btn_upload_poa_back_fragment_edit_level_two:
                startActivityForResult(intent, CROP_ADDRESS_PROOF_IMAGE_REQUEST_BACK);
                break;
            default:
                break;
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.cv_btn_upload_passport_image_fragment_edit_level_two:
                try {
                    /*Intent intent = CropImage.activity()
                            .setAspectRatio(1, 1)
                            .getIntent(getActivity());
                    startActivityForResult(intent, CROP_PASSPORT_PHOTO_IMAGE_REQUEST);*/
                    showImagePickerOptions(v);

                    Toast.makeText(getActivity(), "Crop and upload a passport size photo", Toast.LENGTH_SHORT).show();
                } catch (ActivityNotFoundException anfe) {
                    // display an error message
                    String errorMessage = "Whoops - your device doesn't support the crop action!";
                    Toast toast = Toast.makeText(getActivity(), errorMessage, Toast.LENGTH_SHORT);
                    toast.show();
                }
                break;
            case R.id.cv_btn_upload_passbook_fragment_edit_level_two:
                try {
                    /*Intent intent = CropImage.activity()
                            .setAspectRatio(16, 9)
                            .getIntent(getActivity());
                    startActivityForResult(intent, CROP_PASSBOOK_IMAGE_REQUEST);*/
                    //getPassbookImageActivityResult.launch(intent);
                    showImagePickerOptions(v);
                    Toast.makeText(getActivity(), "Crop and upload front page of passbook", Toast.LENGTH_SHORT).show();
                } catch (ActivityNotFoundException anfe) {
                    // display an error message
                    String errorMessage = "Whoops - your device doesn't support the crop action!";
                    Toast toast = Toast.makeText(getActivity(), errorMessage, Toast.LENGTH_SHORT);
                    toast.show();
                }
                break;
            case R.id.cv_btn_upload_poi_fragment_edit_level_two:
                try {
                    /*Intent intent = CropImage.activity()
                            .getIntent(getActivity());
                    startActivityForResult(intent, CROP_ID_PROOF_IMAGE_REQUEST);*/
                    showImagePickerOptions(v);
                    Toast.makeText(getActivity(), "Crop and upload id proof document", Toast.LENGTH_SHORT).show();
                } catch (ActivityNotFoundException anfe) {
                    // display an error message
                    String errorMessage = "Whoops - your device doesn't support the crop action!";
                    Toast toast = Toast.makeText(getActivity(), errorMessage, Toast.LENGTH_SHORT);
                    toast.show();
                }
                break;
            case R.id.cv_btn_upload_poa_fragment_edit_level_two:
                try {
                    /*Intent intent = CropImage.activity()
                            .getIntent(getActivity());
                    startActivityForResult(intent, CROP_ADDRESS_PROOF_IMAGE_REQUEST);*/
                    showImagePickerOptions(v);
                    Toast.makeText(getActivity(), "Crop and upload address proof document", Toast.LENGTH_SHORT).show();
                } catch (ActivityNotFoundException anfe) {
                    // display an error message
                    String errorMessage = "Whoops - your device doesn't support the crop action!";
                    Toast toast = Toast.makeText(getActivity(), errorMessage, Toast.LENGTH_SHORT);
                    toast.show();
                }
                break;
            case R.id.cv_btn_upload_poa_back_fragment_edit_level_two:
                try {
                    /*Intent intent = CropImage.activity()
                            .getIntent(getActivity());
                    startActivityForResult(intent, CROP_ADDRESS_PROOF_IMAGE_REQUEST_BACK);*/
                    showImagePickerOptions(v);
                    Toast.makeText(getActivity(), "Crop and upload back side of address proof document", Toast.LENGTH_SHORT).show();
                } catch (ActivityNotFoundException anfe) {
                    // display an error message
                    String errorMessage = "Whoops - your device doesn't support the crop action!";
                    Toast toast = Toast.makeText(getActivity(), errorMessage, Toast.LENGTH_SHORT);
                    toast.show();
                }
                break;
            case R.id.iv_viewPassport_fragment_edit_level_two:
                // Instantiating image popup
                if (isAttachedPassportSizePhoto && !isFirstTimePhotoAttach) {//newly added
                    imagePopupPassportPhoto.initiatePopupWithPicasso(filePath1);
                    imagePopupPassportPhoto.viewPopup();
                } else {
                    imagePopupPassportPhoto.initiatePopupWithPicasso(urlImagePassportSizePhoto);
                    imagePopupPassportPhoto.viewPopup();
                }
                break;
            case R.id.iv_viewPassbook_fragment_edit_level_two:
                // Instantiating image popup
                if (isAttachedPassbook) {
                    imagePopupPassbook.initiatePopupWithPicasso(filePath2);
                    imagePopupPassbook.viewPopup();
                } else {
                    imagePopupPassbook.initiatePopupWithPicasso(urlImagePassbook);
                    imagePopupPassbook.viewPopup();
                }
                break;
            case R.id.iv_viewIdProofDoc_fragment_edit_level_two:
                if (isAttachedIdProof && !spinnerIdProof.getSelectedItem().toString().equals("AADHAAR CARD")) {
                    imagePopupIdFront.initiatePopupWithPicasso(filePath3);
                    imagePopupIdFront.viewPopup();
                } else {
                    imagePopupIdFront.initiatePopupWithPicasso(urlImageIdProofFront);
                    imagePopupIdFront.viewPopup();
                }
                break;
            case R.id.iv_viewAddressProofDoc_fragment_edit_level_two:
                if (isAttachedAddressProof && !spinnerIdProof.getSelectedItem().toString().equals("AADHAAR CARD")) {
                    imagePopupAddressProofFront.initiatePopupWithPicasso(filePath4);
                    imagePopupAddressProofFront.viewPopup();
                } else {
                    imagePopupAddressProofFront.initiatePopupWithPicasso(urlImageAddressProofFront);
                    imagePopupAddressProofFront.viewPopup();
                }
                break;
            case R.id.iv_viewAddressProofDocBack_fragment_edit_level_two:
                if (isAttachedAddressProofBack && !spinnerAddressProof.getSelectedItem().toString().equals("AADHAAR CARD")) {
                    imagePopupAddressProofBack.initiatePopupWithPicasso(filePath5);
                    imagePopupAddressProofBack.viewPopup();
                } else {
                    imagePopupAddressProofBack.initiatePopupWithPicasso(urlImageAddressProofBack);
                    imagePopupAddressProofBack.viewPopup();
                }
                break;
            default:
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CROP_PASSPORT_PHOTO_IMAGE_REQUEST) {
            if (resultCode == Activity.RESULT_OK) {
                Uri uri1 = data.getParcelableExtra("path");
                filePath1 = uri1;
                try {
                    //Getting image from gallery
                    bitmapPassportSizePhoto = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), uri1);
                    isAttachedPassportSizePhoto = true;
                    ivViewPassportPhoto.setImageDrawable(ContextCompat.getDrawable(getActivity(), R.drawable.ic_view_doc_black));
                    cvBtnAttachPassportPhoto.setBackgroundColor(Color.parseColor("#FD7D00"));
                } catch (Exception e) {
                    isAttachedPassportSizePhoto = false;
                    e.printStackTrace();
                }
            }

        } else if (requestCode == CROP_PASSBOOK_IMAGE_REQUEST) {
            //CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                Uri uri2 = data.getParcelableExtra("path");
                filePath2 = uri2;
                try {
                    //Getting image from gallery
                    bitmapPassbook = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), uri2);
                    isAttachedPassbook = true;
                    ivViewPassbook.setImageDrawable(ContextCompat.getDrawable(getActivity(), R.drawable.ic_view_doc_black));
                    cvBtnAttachPassbook.setBackgroundColor(Color.parseColor("#FD7D00"));
                } catch (Exception e) {
                    isAttachedPassbook = false;
                    e.printStackTrace();
                }
            }
        } else if (requestCode == CROP_ID_PROOF_IMAGE_REQUEST) {
            //CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                Uri uri3 = data.getParcelableExtra("path");
                filePath3 = uri3;
                try {
                    //Getting image from gallery
                    bitmapIdProof = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), filePath3);
                    // compare face
                    //CompareFaceIdProof(bitmapPassportSizePhoto, bitmapIdProof);
                    isAttachedIdProof = true;
                    ivViewIdProof.setImageDrawable(ContextCompat.getDrawable(getActivity(), R.drawable.ic_view_doc_black));
                    cvBtnAttachIdProof.setBackgroundColor(Color.parseColor("#FD7D00"));
                    // Call karza ocr api
                    if (spinnerIdProof.getSelectedItem().toString().equals("AADHAAR CARD")) {
                        //isAttachedAddressProof = false;
                        GetMaskedAadhaarPoi();
                    }
                } catch (Exception e) {
                    isAttachedIdProof = false;
                    e.printStackTrace();
                }
            }
        } else if (requestCode == CROP_ADDRESS_PROOF_IMAGE_REQUEST) {
            //CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                Uri uri4 = data.getParcelableExtra("path");
                filePath4 = uri4;
                try {
                    //getting image from gallery
                    bitmapAddressProof = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), filePath4);
                    //CompareFaceAddressProof(bitmapPassportSizePhoto, bitmapAddressProof);
                    isAttachedAddressProof = true;
                    ivViewAddressProof.setImageDrawable(ContextCompat.getDrawable(getActivity(), R.drawable.ic_view_doc_black));
                    cvBtnAttachAddressProof.setBackgroundColor(Color.parseColor("#FD7D00"));
                    // Call karza ocr api
                    if (spinnerAddressProof.getSelectedItem().toString().equals("AADHAAR CARD")) {
                        //isAttachedAddressProof = false;
                        GetMaskedAadhaarPoa();
                    }
                } catch (Exception e) {
                    isAttachedAddressProof = false;
                    e.printStackTrace();
                }
            }
        } else if (requestCode == CROP_ADDRESS_PROOF_IMAGE_REQUEST_BACK) {
            //CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                Uri uri5 = data.getParcelableExtra("path");
                filePath5 = uri5;
                try {
                    //getting image from gallery
                    bitmapAddressProofBack = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), filePath5);
                    isAttachedAddressProofBack = true;
                    ivViewAddressProofBack.setImageDrawable(ContextCompat.getDrawable(getActivity(), R.drawable.ic_view_doc_black));
                    cvBtnAttachAddressProofBack.setBackgroundColor(Color.parseColor("#FD7D00"));
                    // Call karza ocr api
                    if (spinnerAddressProof.getSelectedItem().toString().equals("AADHAAR CARD")) {
                        //isAttachedAddressProof = false;
                        GetMaskedAadhaarPoaBack();
                    }
                } catch (Exception e) {
                    isAttachedAddressProofBack = false;
                    e.printStackTrace();
                }
            }
        } else if (requestCode == 100) {// For sdk activity (poi)
            if (resultCode == RESULT_OK) {
                Bundle bundle = data.getExtras();
                if (bundle != null) {
                    Boolean error = Boolean.parseBoolean(bundle.getString("isError"));
                    Log.d("OKyc", String.valueOf(error));
                    if (!error) {
                        tietIdProofNo.setText(bundle.getString("maskedAadhaarNumber"));
                        ivPoiValid.setImageResource(R.drawable.ic_check_circle_green);
                        isPoiValid = true;
                        spinnerIdProof.setEnabled(false);
                        tietIdProofNo.setEnabled(false);
                    } else {
                        Toast.makeText(getActivity(), getResources().getString(R.string.err_msg_doc_varifation), Toast.LENGTH_SHORT).show();
                    }
                }
            }
        } else if (requestCode == 101) { // For sdk activity
            if (resultCode == RESULT_OK) {
                Bundle bundle = data.getExtras();
                if (bundle != null) {
                    Boolean error = Boolean.parseBoolean(bundle.getString("isError"));
                    Log.d("OKyc", String.valueOf(error));
                    if (!error) {
                        tietAddressProofNo.setText(bundle.getString("maskedAadhaarNumber"));
                        ivPoaValid.setImageResource(R.drawable.ic_check_circle_green);
                        isPoaValid = true;
                        spinnerAddressProof.setEnabled(false);
                        tietAddressProofNo.setEnabled(false);
                    } else {
                        Toast.makeText(getActivity(), getResources().getString(R.string.err_msg_doc_varifation), Toast.LENGTH_SHORT).show();
                    }
                }
            }
        }
    }

    private void CompareFaceIdProof(Bitmap image_s, Bitmap image_t) {
        String url = EndPoints.URL_FACE_RECOGNITION;
        // Use this to cancel request
        final String tag_custom_multi_part_obj = "custom_multi_part_obj_req";

        final Snackbar snackbar = Snackbar
                .make(coordinatorLayout, "Please wait..", Snackbar.LENGTH_INDEFINITE);

        snackbar.show();

        // Custom Volley Request
        VolleyMultipartRequest volleyMultipartRequest = new VolleyMultipartRequest(Request.Method.POST, url,
                new Response.Listener<NetworkResponse>() {
                    @Override
                    public void onResponse(NetworkResponse response) {
                        snackbar.dismiss();
                        try {
                            JSONObject obj_A = new JSONObject(new String(response.data));
                            JSONArray arr = obj_A.getJSONArray("FaceMatches");
                            if (arr.length() > 0) {
                                JSONObject obj_B = arr.getJSONObject(0);
                                double matchPercentage = obj_B.getDouble("Similarity");
                                if (matchPercentage < 60) {
                                    Toast.makeText(getActivity(), "Face identification failed", Toast.LENGTH_SHORT).show();
                                    isAttachedIdProof = true;
                                } else {
                                    isAttachedIdProof = true;
                                }
                            } else {
                                Toast.makeText(getActivity(), "Face identification failed", Toast.LENGTH_SHORT).show();
                                isAttachedIdProof = true;
                            }
                        } catch (JSONException e) {
                            snackbar.dismiss();
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        snackbar.dismiss();

                        NetworkResponse networkResponse = error.networkResponse;
                        String errorMessage = "Unknown error";
                        if (networkResponse == null) {
                            if (error.getClass().equals(TimeoutError.class)) {
                                errorMessage = "Request timeout";
                            } else if (error.getClass().equals(NoConnectionError.class)) {
                                errorMessage = "Failed to connect server";
                            }
                        } else {
                            String result = new String(networkResponse.data);
                            try {
                                JSONObject response = new JSONObject(result);
                                String message = response.getString("msg");

                                Log.e("Error Message", message);

                                if (networkResponse.statusCode == 404) {
                                    errorMessage = "Resource not found";
                                } else if (networkResponse.statusCode == 401) {
                                    errorMessage = message + "";
                                } else if (networkResponse.statusCode == 400) {
                                    errorMessage = message + "";
                                } else if (networkResponse.statusCode == 500) {
                                    errorMessage = message + " Something is getting wrong";
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                        Log.i("Error", errorMessage);
                        error.printStackTrace();
                        Toast.makeText(getActivity(), errorMessage, Toast.LENGTH_SHORT).show();
                    }
                }) {

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                final String token = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiJGSUFQUDEwMSIsImlhdCI6MTUxNjIzOTAyMn0._jKrqO1xcUoKoguQgzhhbB3yML0GfsNTo7Mm14Bw0Bo";//Prod
                headers.put("app-access-token", token);
                return headers;
            }

            /*
             * Here we are passing image by renaming it with a unique name
             * */
            @Override
            protected Map<String, DataPart> getByteData() {
                Map<String, DataPart> params = new HashMap<>();
                long imgNameS = System.currentTimeMillis() + 1;
                long imgNameT = System.currentTimeMillis() + 2;
                params.put("source_img", new DataPart(imgNameS + ".jpg", getFileDataFromDrawable(image_s)));
                params.put("target_img", new DataPart(imgNameT + ".jpg", getFileDataFromDrawable(image_t)));
                return params;
            }
        };

        volleyMultipartRequest.setRetryPolicy(new DefaultRetryPolicy(20 * 1000, 0,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(volleyMultipartRequest, tag_custom_multi_part_obj);
    }

    private void CompareFaceAddressProof(Bitmap image_s, Bitmap image_t) {
        String url = EndPoints.URL_FACE_RECOGNITION;
        // Use this to cancel request
        final String tag_custom_multi_part_obj = "custom_multi_part_obj_req";

        final Snackbar snackbar = Snackbar
                .make(coordinatorLayout, "Please wait..", Snackbar.LENGTH_INDEFINITE);

        snackbar.show();

        // Custom Volley Request
        VolleyMultipartRequest volleyMultipartRequest = new VolleyMultipartRequest(Request.Method.POST, url,
                new Response.Listener<NetworkResponse>() {
                    @Override
                    public void onResponse(NetworkResponse response) {
                        snackbar.dismiss();
                        try {
                            JSONObject obj_A = new JSONObject(new String(response.data));
                            JSONArray arr = obj_A.getJSONArray("FaceMatches");
                            if (arr.length() > 0) {
                                JSONObject obj_B = arr.getJSONObject(0);
                                double matchPercentage = obj_B.getDouble("Similarity");
                                if (matchPercentage < 60) {
                                    Toast.makeText(getActivity(), "Face identification failed", Toast.LENGTH_SHORT).show();
                                    isAttachedAddressProof = true;
                                } else {
                                    isAttachedAddressProof = true;
                                }
                            } else {
                                Toast.makeText(getActivity(), "Face identification failed", Toast.LENGTH_SHORT).show();
                                isAttachedAddressProof = true;
                            }
                        } catch (JSONException e) {
                            snackbar.dismiss();
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        snackbar.dismiss();

                        NetworkResponse networkResponse = error.networkResponse;
                        String errorMessage = "Unknown error";
                        if (networkResponse == null) {
                            if (error.getClass().equals(TimeoutError.class)) {
                                errorMessage = "Request timeout";
                            } else if (error.getClass().equals(NoConnectionError.class)) {
                                errorMessage = "Failed to connect server";
                            }
                        } else {
                            String result = new String(networkResponse.data);
                            try {
                                JSONObject response = new JSONObject(result);
                                String message = response.getString("msg");

                                Log.e("Error Message", message);

                                if (networkResponse.statusCode == 404) {
                                    errorMessage = "Resource not found";
                                } else if (networkResponse.statusCode == 401) {
                                    errorMessage = message + "";
                                } else if (networkResponse.statusCode == 400) {
                                    errorMessage = message + "";
                                } else if (networkResponse.statusCode == 500) {
                                    errorMessage = message + " Something is getting wrong";
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                        Log.i("Error", errorMessage);
                        error.printStackTrace();
                        Toast.makeText(getActivity(), errorMessage, Toast.LENGTH_SHORT).show();
                    }
                }) {

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                final String token = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiJGSUFQUDEwMSIsImlhdCI6MTUxNjIzOTAyMn0._jKrqO1xcUoKoguQgzhhbB3yML0GfsNTo7Mm14Bw0Bo";//Prod
                headers.put("app-access-token", token);
                return headers;
            }

            /*
             * Here we are passing image by renaming it with a unique name
             * */
            @Override
            protected Map<String, DataPart> getByteData() {
                Map<String, DataPart> params = new HashMap<>();
                long imgNameS = System.currentTimeMillis() + 1;
                long imgNameT = System.currentTimeMillis() + 2;
                params.put("source_img", new DataPart(imgNameS + ".jpg", getFileDataFromDrawable(image_s)));
                params.put("target_img", new DataPart(imgNameT + ".jpg", getFileDataFromDrawable(image_t)));
                return params;
            }
        };

        volleyMultipartRequest.setRetryPolicy(new DefaultRetryPolicy(20 * 1000, 0,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(volleyMultipartRequest, tag_custom_multi_part_obj);
    }

    private byte[] getFileDataFromDrawable(Bitmap bitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 80, byteArrayOutputStream);
        return byteArrayOutputStream.toByteArray();
    }

    private boolean isCorrectBankName(String currentString) {
        if (!lstBankName.contains(currentString)) {
            return false;
        }

        return true;
    }

    private boolean isCorrectFpoName(String currentString) {
        if (!lstFpoName.contains(currentString)) {
            return false;
        }

        return true;
    }

    /*private void KarzaCheckPan(CharSequence pan_number) {
        // Tag used to cancel the request
        String tag_json_obj = "json_obj_req";

        //final Snackbar snackbar = Snackbar
                //.make(coordinatorLayout, "Connecting..", Snackbar.LENGTH_INDEFINITE);

        pbPanValidation.setVisibility(View.VISIBLE);

        JSONObject jsonBody = new JSONObject();

        try {
            jsonBody.put("consent", "Y");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        try {
            jsonBody.put("pan", pan_number);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, "https://api.karza.in/v2/pan", jsonBody, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                pbPanValidation.setVisibility(View.GONE);
                try {
                    JSONObject obj = response.getJSONObject("result");
                    String nameInDoc = obj.getString("name");
                    String status_code = response.getString("status-code");
                    if (status_code.equals("101") && name.toLowerCase().equals(nameInDoc.toLowerCase())) {
                        isPanValid = true;
                        tilPanNo.setErrorEnabled(false);
                        ivPanValid.setImageResource(R.drawable.ic_check_circle_green);
                        ivPoiValid.setImageResource(R.drawable.ic_check_circle_green);
                    } else {
                        isPanValid = false;
                        tilPanNo.setError("Pan number is not valid.");
                        mAlert.showAlertDialog(getActivity(), "Validation Error", "Pan failed in validation", false);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                //snackbar.dismiss();
                pbPanValidation.setVisibility(View.GONE);

                NetworkResponse networkResponse = error.networkResponse;
                String errorMessage = "Unknown error";
                if (networkResponse == null) {
                    if (error.getClass().equals(TimeoutError.class)) {
                        errorMessage = "Request timeout";
                    } else if (error.getClass().equals(NoConnectionError.class)) {
                        errorMessage = "Failed to connect server";
                    } else if (error.getClass().equals(NetworkError.class)) {
                        errorMessage = "Check your internet connection";
                    } else if (error.getClass().equals(AuthFailureError.class)) {
                        errorMessage = "Invalid credentials";
                    }
                } else {
                    String result = new String(networkResponse.data);
                    try {
                        JSONObject response = new JSONObject(result);
                        String message = response.getString("msg");

                        Log.e("Error Message", message);

                        if (networkResponse.statusCode == 404) {
                            errorMessage = "Resource not found";
                        } else if (networkResponse.statusCode == 401) {
                            errorMessage = message + "";
                        } else if (networkResponse.statusCode == 400) {
                            errorMessage = message + "";
                        } else if (networkResponse.statusCode == 500) {
                            errorMessage = message + " Something is getting wrong";
                        }
                        else if (networkResponse.statusCode == 503) {
                            errorMessage = message + " Service not available";
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                Log.i("Error", errorMessage);
                error.printStackTrace();
                Toast.makeText(getActivity(), errorMessage, Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                // add headers <key,value>
                headers.put("Content-Type", "application/json");
                headers.put("x-karza-key", "JmEIIONAwjN6cEY");
                return headers;
            }
        };

        jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(20 * 1000, 0,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(jsonObjectRequest, tag_json_obj);
    }*/

    private void KarzaCheckPanPoi(CharSequence pan_number) {
        // Tag used to cancel the request
        String tag_json_obj = "json_obj_req";

        //final Snackbar snackbar = Snackbar
        //.make(coordinatorLayout, "Connecting..", Snackbar.LENGTH_INDEFINITE);

        pbPoiValidation.setVisibility(View.VISIBLE);

        JSONObject jsonBody = new JSONObject();

        try {
            jsonBody.put("pan", pan_number);
            jsonBody.put("consent", "Y");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, "https://api.karza.in/v2/pan", jsonBody, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                pbPoiValidation.setVisibility(View.GONE);
                try {
                    poiVerifyId = response.getString("request_id");
                    // POI is PAN
                    //poiVerifyId = panVerifyId;
                    JSONObject obj = response.getJSONObject("result");
                    String status_code = response.getString("status-code");
                    if (status_code.equals("101")) {
                        String nameInDoc = obj.getString("name");
                        String[] nameInDocParts = nameInDoc.split(" ");
                        if (nameInDocParts[0].toLowerCase().equals(f_name.toLowerCase()) && nameInDocParts[1].toLowerCase().equals(m_name.toLowerCase()) && nameInDocParts[2].toLowerCase().equals(l_name.toLowerCase())) {
                            isPanValid = true;
                            isPoiValid = true;
                            tilIdProofNo.setErrorEnabled(false);
                            ivPoiValid.setImageResource(R.drawable.ic_check_circle_green);
                            tietIdProofNo.setEnabled(false);
                            cbPanCard.setEnabled(false);
                            spinnerIdProof.setEnabled(false);
                        } else if (nameInDocParts[0].toLowerCase().equals(f_name.toLowerCase())) {
                            isPanValid = true;
                            isPoiValid = true;
                            tilIdProofNo.setErrorEnabled(false);
                            ivPoiValid.setImageResource(R.drawable.ic_check_circle_green);
                            tietIdProofNo.setEnabled(false);
                            cbPanCard.setEnabled(false);
                            spinnerIdProof.setEnabled(false);
                        } else {
                            isPanValid = false;
                            isPoiValid = false;
                            tilIdProofNo.setError("Name did not match");
                            mAlert.showAlertDialog(getActivity(), "Validation Error", getResources().getString(R.string.err_msg_pan_name_match), false);
                        }
                    } else {
                        isPanValid = false;
                        isPoiValid = false;
                        tilIdProofNo.setError("Pan number is not valid.");
                        mAlert.showAlertDialog(getActivity(), "Validation Error", getResources().getString(R.string.err_msg_pan_number), false);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                //snackbar.dismiss();
                pbPoiValidation.setVisibility(View.GONE);

                NetworkResponse networkResponse = error.networkResponse;
                String errorMessage = "Unknown error";
                if (networkResponse == null) {
                    if (error.getClass().equals(TimeoutError.class)) {
                        errorMessage = "Request timeout";
                    } else if (error.getClass().equals(NoConnectionError.class)) {
                        errorMessage = "Failed to connect server";
                    } else if (error.getClass().equals(NetworkError.class)) {
                        errorMessage = "Check your internet connection";
                    } else if (error.getClass().equals(AuthFailureError.class)) {
                        errorMessage = "Invalid credentials";
                    }
                } else {
                    String result = new String(networkResponse.data);
                    try {
                        JSONObject response = new JSONObject(result);
                        String message = response.getString("msg");

                        Log.e("Error Message", message);

                        if (networkResponse.statusCode == 404) {
                            errorMessage = "Resource not found";
                        } else if (networkResponse.statusCode == 401) {
                            errorMessage = message + "";
                        } else if (networkResponse.statusCode == 400) {
                            errorMessage = message + "";
                        } else if (networkResponse.statusCode == 500) {
                            errorMessage = message + " Something is getting wrong";
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                Log.i("Error", errorMessage);
                error.printStackTrace();
                Toast.makeText(getActivity(), errorMessage, Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                // add headers <key,value>
                headers.put("Content-Type", "application/json");
                headers.put("x-karza-key", "JmEIIONAwjN6cEY");
                return headers;
            }
        };

        jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(20 * 1000, 0,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(jsonObjectRequest, tag_json_obj);
    }

    private void KarzaCheckAccountNo(String account_number) {
        // Tag used to cancel the request
        String tag_json_obj = "json_obj_req";

        pbAccNoValidation.setVisibility(View.VISIBLE);

        JSONObject jsonBody = new JSONObject();

        try {
            jsonBody.put("accountNumber", account_number);
            jsonBody.put("ifsc", tietDbtIfsc.getText().toString());
            jsonBody.put("consent", "Y");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, "https://api.karza.in/v2/bankacc", jsonBody, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                pbAccNoValidation.setVisibility(View.GONE);
                try {
                    accVerifyId = response.getString("request_id");
                    JSONObject obj = response.getJSONObject("result");
                    //String nameInDoc = obj.getString("accountName");
                    //String[] nameInDocParts = nameInDoc.split(" ");
                    String status_code = response.getString("status-code");
                    if (status_code.equals("101")) {
                        boolean bankTxnStatus = obj.getBoolean("bankTxnStatus");
                        if (bankTxnStatus) {
                            isAccNoValid = true;
                            tilDbtAccNo.setErrorEnabled(false);
                            ivAccNoValid.setImageResource(R.drawable.ic_check_circle_green);
                            tietDbtIfsc.setEnabled(false);
                            tietDbtAccNo.setEnabled(false);
                        /*if (nameInDocParts[2].toLowerCase().equals(f_name.toLowerCase()) && nameInDocParts[4].toLowerCase().equals(m_name.toLowerCase()) && nameInDocParts[6].toLowerCase().equals(l_name.toLowerCase())) {
                            isAccNoValid = true;
                            tilDbtAccNo.setErrorEnabled(false);
                            ivAccNoValid.setImageResource(R.drawable.ic_check_circle_green);
                        } else if (nameInDocParts[2].toLowerCase().equals(f_name.toLowerCase())) {
                            isAccNoValid = true;
                            tilDbtAccNo.setErrorEnabled(false);
                            ivAccNoValid.setImageResource(R.drawable.ic_check_circle_green);
                        }*/
                        }
                    } else {
                        isAccNoValid = false;
                        tilDbtAccNo.setError("Incorrect IFSC code/Account number.");
                        //mAlert.showAlertDialog(getActivity(), "Validation Error", "Account number failed in validation", false);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                //snackbar.dismiss();
                pbAccNoValidation.setVisibility(View.GONE);

                NetworkResponse networkResponse = error.networkResponse;
                String errorMessage = "Unknown error";
                if (networkResponse == null) {
                    if (error.getClass().equals(TimeoutError.class)) {
                        errorMessage = "Request timeout";
                    } else if (error.getClass().equals(NoConnectionError.class)) {
                        errorMessage = "Failed to connect server";
                    } else if (error.getClass().equals(NetworkError.class)) {
                        errorMessage = "Check your internet connection";
                    } else if (error.getClass().equals(AuthFailureError.class)) {
                        errorMessage = "Invalid credentials";
                    }
                } else {
                    String result = new String(networkResponse.data);
                    try {
                        JSONObject response = new JSONObject(result);
                        String message = response.getString("msg");

                        Log.e("Error Message", message);

                        if (networkResponse.statusCode == 404) {
                            errorMessage = "Resource not found";
                        } else if (networkResponse.statusCode == 401) {
                            errorMessage = message + "";
                        } else if (networkResponse.statusCode == 400) {
                            errorMessage = message + "";
                        } else if (networkResponse.statusCode == 500) {
                            errorMessage = message + " Something is getting wrong";
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                Log.i("Error", errorMessage);
                error.printStackTrace();
                Toast.makeText(getActivity(), errorMessage, Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                // add headers <key,value>
                headers.put("Content-Type", "application/json");
                headers.put("x-karza-key", "JmEIIONAwjN6cEY");
                return headers;
            }
        };

        jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(20 * 1000, 0,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(jsonObjectRequest, tag_json_obj);
    }

    private void KarzaCheckDrivingLicencePoi(CharSequence dl_number) {
        // Tag used to cancel the request
        String tag_json_obj = "json_obj_req";

        //final Snackbar snackbar = Snackbar
        //.make(coordinatorLayout, "Connecting..", Snackbar.LENGTH_INDEFINITE);
        pbPoiValidation.setVisibility(View.VISIBLE);

        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("dlNo", dl_number);
            jsonBody.put("dob", dob);
            jsonBody.put("consent", "Y");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, "https://api.karza.in/v3/dl", jsonBody, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                pbPoiValidation.setVisibility(View.GONE);
                try {
                    poiVerifyId = response.getString("request_id");
                    JSONObject obj = response.getJSONObject("result");
                    int status_code = response.getInt("statusCode");
                    if (status_code == 101) {
                        String nameInDoc = obj.getString("name");
                        String[] nameInDocParts = nameInDoc.split(" ");
                        if (nameInDocParts[0].toLowerCase().equals(f_name.toLowerCase()) && nameInDocParts[1].toLowerCase().equals(m_name.toLowerCase()) && nameInDocParts[2].toLowerCase().equals(l_name.toLowerCase())) {
                            isPoiValid = true;
                            tilIdProofNo.setErrorEnabled(false);
                            ivPoiValid.setImageResource(R.drawable.ic_check_circle_green);
                            tietIdProofNo.setEnabled(false);
                            cbPanCard.setEnabled(false);
                            spinnerIdProof.setEnabled(false);
                        } else if (nameInDocParts[0].toLowerCase().equals(f_name.toLowerCase())) {
                            isPoiValid = true;
                            tilIdProofNo.setErrorEnabled(false);
                            ivPoiValid.setImageResource(R.drawable.ic_check_circle_green);
                            tietIdProofNo.setEnabled(false);
                            cbPanCard.setEnabled(false);
                            spinnerIdProof.setEnabled(false);
                        } else {
                            isPoiValid = false;
                            tilIdProofNo.setError("Name did not match.");
                            mAlert.showAlertDialog(getActivity(), "Validation Error", getResources().getString(R.string.err_msg_dl_name_match), false);
                        }
                    } else {
                        isPoiValid = false;
                        tilIdProofNo.setError("Driving licence number is not valid.");
                        mAlert.showAlertDialog(getActivity(), "Validation Error", getResources().getString(R.string.err_msg_dl_number), false);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                //snackbar.dismiss();
                pbPoiValidation.setVisibility(View.GONE);

                NetworkResponse networkResponse = error.networkResponse;
                String errorMessage = "Unknown error";
                if (networkResponse == null) {
                    if (error.getClass().equals(TimeoutError.class)) {
                        errorMessage = "Request timeout";
                    } else if (error.getClass().equals(NoConnectionError.class)) {
                        errorMessage = "Failed to connect server";
                    } else if (error.getClass().equals(NetworkError.class)) {
                        errorMessage = "Check your internet connection";
                    } else if (error.getClass().equals(AuthFailureError.class)) {
                        errorMessage = "Invalid credentials";
                    }
                } else {
                    String result = new String(networkResponse.data);
                    try {
                        JSONObject response = new JSONObject(result);
                        String message = response.getString("msg");

                        Log.e("Error Message", message);

                        if (networkResponse.statusCode == 404) {
                            errorMessage = "Resource not found";
                        } else if (networkResponse.statusCode == 401) {
                            errorMessage = message + "";
                        } else if (networkResponse.statusCode == 400) {
                            errorMessage = message + "";
                        } else if (networkResponse.statusCode == 500) {
                            errorMessage = message + " Something is getting wrong";
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                Log.i("Error", errorMessage);
                error.printStackTrace();
                Toast.makeText(getActivity(), errorMessage, Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                // add headers <key,value>
                headers.put("Content-Type", "application/json");
                headers.put("x-karza-key", "JmEIIONAwjN6cEY");
                return headers;
            }
        };

        jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(20 * 1000, 0,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(jsonObjectRequest, tag_json_obj);
    }

    private void KarzaCheckDrivingLicencePoa(CharSequence dl_number) {
        // Tag used to cancel the request
        String tag_json_obj = "json_obj_req";
        pbPoaValidation.setVisibility(View.VISIBLE);

        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("dlNo", dl_number);
            jsonBody.put("dob", dob);
            jsonBody.put("consent", "Y");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, "https://api.karza.in/v3/dl", jsonBody, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                pbPoaValidation.setVisibility(View.GONE);
                try {
                    poaVerifyId = response.getString("request_id");
                    JSONObject obj = response.getJSONObject("result");
                    String status_code = response.getString("statusCode");
                    if (status_code.equals("101")) {
                        String nameInDoc = obj.getString("name");
                        String[] nameInDocParts = nameInDoc.split(" ");
                        if (nameInDocParts[0].toLowerCase().equals(f_name.toLowerCase()) && nameInDocParts[1].toLowerCase().equals(m_name.toLowerCase()) && nameInDocParts[2].toLowerCase().equals(l_name.toLowerCase())) {
                            isPoaValid = true;
                            tilAddressProofNo.setErrorEnabled(false);
                            ivPoaValid.setImageResource(R.drawable.ic_check_circle_green);
                            tilAddressProofNo.setEnabled(false);
                        } else if (nameInDocParts[0].toLowerCase().equals(f_name.toLowerCase())) {
                            isPoaValid = true;
                            tilAddressProofNo.setErrorEnabled(false);
                            ivPoaValid.setImageResource(R.drawable.ic_check_circle_green);
                            tilAddressProofNo.setEnabled(false);
                        } else {
                            isPoiValid = false;
                            tilAddressProofNo.setError("Name did not match.");
                            mAlert.showAlertDialog(getActivity(), "Validation Error", getResources().getString(R.string.err_msg_dl_name_match), false);
                        }
                    } else {
                        isPoiValid = false;
                        tilAddressProofNo.setError("Driving licence number is not valid.");
                        mAlert.showAlertDialog(getActivity(), "Validation Error", getResources().getString(R.string.err_msg_dl_number), false);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                //snackbar.dismiss();
                pbPoaValidation.setVisibility(View.GONE);

                NetworkResponse networkResponse = error.networkResponse;
                String errorMessage = "Unknown error";
                if (networkResponse == null) {
                    if (error.getClass().equals(TimeoutError.class)) {
                        errorMessage = "Request timeout";
                    } else if (error.getClass().equals(NoConnectionError.class)) {
                        errorMessage = "Failed to connect server";
                    } else if (error.getClass().equals(NetworkError.class)) {
                        errorMessage = "Check your internet connection";
                    } else if (error.getClass().equals(AuthFailureError.class)) {
                        errorMessage = "Invalid credentials";
                    }
                } else {
                    String result = new String(networkResponse.data);
                    try {
                        JSONObject response = new JSONObject(result);
                        String message = response.getString("msg");

                        Log.e("Error Message", message);

                        if (networkResponse.statusCode == 404) {
                            errorMessage = "Resource not found";
                        } else if (networkResponse.statusCode == 401) {
                            errorMessage = message + "";
                        } else if (networkResponse.statusCode == 400) {
                            errorMessage = message + "";
                        } else if (networkResponse.statusCode == 500) {
                            errorMessage = message + " Something is getting wrong";
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                Log.i("Error", errorMessage);
                error.printStackTrace();
                Toast.makeText(getActivity(), errorMessage, Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                // add headers <key,value>
                headers.put("Content-Type", "application/json");
                headers.put("x-karza-key", "JmEIIONAwjN6cEY");
                return headers;
            }
        };

        jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(20 * 1000, 0,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(jsonObjectRequest, tag_json_obj);
    }

    private void KarzaCheckVoterIdPoi(CharSequence epicNumber) {
        // Tag used to cancel the request
        String tag_json_obj = "json_obj_req";

        pbPoiValidation.setVisibility(View.VISIBLE);

        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("epic_no", epicNumber);
            jsonBody.put("consent", "Y");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, "https://api.karza.in/v2/voter", jsonBody, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                pbPoiValidation.setVisibility(View.GONE);
                try {
                    poiVerifyId = response.getString("request_id");
                    JSONObject obj = response.getJSONObject("result");
                    String status_code = response.getString("status-code");
                    if (Integer.parseInt(status_code) == 101) {
                        String nameInDoc = obj.getString("name");
                        String[] nameInDocParts = nameInDoc.split(" ");
                        if (nameInDocParts[0].toLowerCase().equals(f_name.toLowerCase()) && nameInDocParts[1].toLowerCase().equals(m_name.toLowerCase()) && nameInDocParts[2].toLowerCase().equals(l_name.toLowerCase())) {
                            isPoiValid = true;
                            tilIdProofNo.setErrorEnabled(false);
                            ivPoiValid.setImageResource(R.drawable.ic_check_circle_green);
                            tilIdProofNo.setEnabled(false);
                            spinnerIdProof.setEnabled(false);
                        } else if (nameInDocParts[0].toLowerCase().equals(f_name.toLowerCase())) {
                            isPoiValid = true;
                            tilIdProofNo.setErrorEnabled(false);
                            ivPoiValid.setImageResource(R.drawable.ic_check_circle_green);
                            tilIdProofNo.setEnabled(false);
                            spinnerIdProof.setEnabled(false);
                        } else {
                            isPoiValid = false;
                            tilIdProofNo.setError("Name did not match.");
                            mAlert.showAlertDialog(getActivity(), "Validation Error", getResources().getString(R.string.err_msg_voter_id_name_match), false);
                        }
                    } else {
                        isPoiValid = false;
                        tilIdProofNo.setError("Voter ID is not valid.");
                        mAlert.showAlertDialog(getActivity(), "Validation Error", getResources().getString(R.string.err_msg_voter_id_number), false);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                //snackbar.dismiss();
                pbPoiValidation.setVisibility(View.GONE);

                NetworkResponse networkResponse = error.networkResponse;
                String errorMessage = "Unknown error";
                if (networkResponse == null) {
                    if (error.getClass().equals(TimeoutError.class)) {
                        errorMessage = "Request timeout";
                    } else if (error.getClass().equals(NoConnectionError.class)) {
                        errorMessage = "Failed to connect server";
                    } else if (error.getClass().equals(NetworkError.class)) {
                        errorMessage = "Check your internet connection";
                    } else if (error.getClass().equals(AuthFailureError.class)) {
                        errorMessage = "Invalid credentials";
                    }
                } else {
                    String result = new String(networkResponse.data);
                    try {
                        JSONObject response = new JSONObject(result);
                        String message = response.getString("msg");

                        Log.e("Error Message", message);

                        if (networkResponse.statusCode == 404) {
                            errorMessage = "Resource not found";
                        } else if (networkResponse.statusCode == 401) {
                            errorMessage = message + "";
                        } else if (networkResponse.statusCode == 400) {
                            errorMessage = message + "";
                        } else if (networkResponse.statusCode == 500) {
                            errorMessage = message + " Something is getting wrong";
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                Log.i("Error", errorMessage);
                error.printStackTrace();
                Toast.makeText(getActivity(), errorMessage, Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                // add headers <key,value>
                headers.put("Content-Type", "application/json");
                headers.put("x-karza-key", "JmEIIONAwjN6cEY");
                return headers;
            }
        };

        jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(20 * 1000, 0,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(jsonObjectRequest, tag_json_obj);
    }

    private void KarzaCheckVoterIdPoa(CharSequence epicNumber) {
        // Tag used to cancel the request
        String tag_json_obj = "json_obj_req";

        pbPoaValidation.setVisibility(View.VISIBLE);

        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("epic_no", epicNumber);
            jsonBody.put("consent", "Y");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, "https://api.karza.in/v2/voter", jsonBody, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                pbPoaValidation.setVisibility(View.GONE);
                try {
                    poaVerifyId = response.getString("request_id");
                    JSONObject obj = response.getJSONObject("result");
                    String status_code = response.getString("status-code");
                    if (Integer.parseInt(status_code) == 101) {
                        String nameInDoc = obj.getString("name");
                        String[] nameInDocParts = nameInDoc.split(" ");
                        if (nameInDocParts[0].toLowerCase().equals(f_name.toLowerCase()) && nameInDocParts[1].toLowerCase().equals(m_name.toLowerCase()) && nameInDocParts[2].toLowerCase().equals(l_name.toLowerCase())) {
                            isPoaValid = true;
                            tilAddressProofNo.setErrorEnabled(false);
                            ivPoaValid.setImageResource(R.drawable.ic_check_circle_green);
                            tilAddressProofNo.setEnabled(false);
                        } else if (nameInDocParts[0].toLowerCase().equals(f_name.toLowerCase())) {
                            isPoaValid = true;
                            tilAddressProofNo.setErrorEnabled(false);
                            ivPoaValid.setImageResource(R.drawable.ic_check_circle_green);
                            tilAddressProofNo.setEnabled(false);
                        } else {
                            isPoaValid = false;
                            tilAddressProofNo.setError("Name did not match.");
                            mAlert.showAlertDialog(getActivity(), "Validation Error", getResources().getString(R.string.err_msg_voter_id_name_match), false);
                        }
                    } else {
                        isPoaValid = false;
                        tilAddressProofNo.setError("Voter Id is not valid.");
                        mAlert.showAlertDialog(getActivity(), "Validation Error", getResources().getString(R.string.err_msg_voter_id_number), false);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                //snackbar.dismiss();
                pbPoaValidation.setVisibility(View.GONE);

                NetworkResponse networkResponse = error.networkResponse;
                String errorMessage = "Unknown error";
                if (networkResponse == null) {
                    if (error.getClass().equals(TimeoutError.class)) {
                        errorMessage = "Request timeout";
                    } else if (error.getClass().equals(NoConnectionError.class)) {
                        errorMessage = "Failed to connect server";
                    } else if (error.getClass().equals(NetworkError.class)) {
                        errorMessage = "Check your internet connection";
                    } else if (error.getClass().equals(AuthFailureError.class)) {
                        errorMessage = "Invalid credentials";
                    }
                } else {
                    String result = new String(networkResponse.data);
                    try {
                        JSONObject response = new JSONObject(result);
                        String message = response.getString("msg");

                        Log.e("Error Message", message);

                        if (networkResponse.statusCode == 404) {
                            errorMessage = "Resource not found";
                        } else if (networkResponse.statusCode == 401) {
                            errorMessage = message + "";
                        } else if (networkResponse.statusCode == 400) {
                            errorMessage = message + "";
                        } else if (networkResponse.statusCode == 500) {
                            errorMessage = message + " Something is getting wrong";
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                Log.i("Error", errorMessage);
                error.printStackTrace();
                Toast.makeText(getActivity(), errorMessage, Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                // add headers <key,value>
                headers.put("Content-Type", "application/json");
                headers.put("x-karza-key", "JmEIIONAwjN6cEY");
                return headers;
            }
        };

        jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(20 * 1000, 0,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(jsonObjectRequest, tag_json_obj);
    }

    private void KarzaCheckPassportPoi(CharSequence fileNumber) {
        // Tag used to cancel the request
        String tag_json_obj = "json_obj_req";

        pbPoiValidation.setVisibility(View.VISIBLE);

        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("fileNo", fileNumber);
            jsonBody.put("dob", dob.replace('-', '/'));
            jsonBody.put("consent", "Y");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, "https://api.karza.in/v3/passport-verification", jsonBody, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                pbPoiValidation.setVisibility(View.GONE);
                try {
                    poiVerifyId = response.getString("request_id");
                    JSONObject obj = response.getJSONObject("result");
                    int status_code = response.getInt("statusCode");
                    if (status_code == 101) {
                        JSONObject nameObj = obj.getJSONObject("name");
                        String fName = nameObj.getString("nameFromPassport");
                        String lName = nameObj.getString("surnameFromPassport");
                        if (fName.toLowerCase().equals(f_name.toLowerCase()) && lName.toLowerCase().equals(l_name.toLowerCase())) {
                            isPoiValid = true;
                            tilIdProofNo.setErrorEnabled(false);
                            ivPoiValid.setImageResource(R.drawable.ic_check_circle_green);
                            tilIdProofNo.setEnabled(false);
                            spinnerIdProof.setEnabled(false);
                        } else if (fName.toLowerCase().equals(f_name.toLowerCase())) {
                            isPoiValid = true;
                            tilIdProofNo.setErrorEnabled(false);
                            ivPoiValid.setImageResource(R.drawable.ic_check_circle_green);
                            tilIdProofNo.setEnabled(false);
                            spinnerIdProof.setEnabled(false);
                        } else {
                            isPoiValid = false;
                            tilIdProofNo.setError("Name did not match");
                            mAlert.showAlertDialog(getActivity(), "Validation Error", getResources().getString(R.string.err_msg_passport_name_match), false);
                        }
                    } else {
                        isPoiValid = false;
                        tilIdProofNo.setError("Passport is not valid.");
                        mAlert.showAlertDialog(getActivity(), "Validation Error", getResources().getString(R.string.err_msg_passport_number), false);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                //snackbar.dismiss();
                pbPoiValidation.setVisibility(View.GONE);

                NetworkResponse networkResponse = error.networkResponse;
                String errorMessage = "Unknown error";
                if (networkResponse == null) {
                    if (error.getClass().equals(TimeoutError.class)) {
                        errorMessage = "Request timeout";
                    } else if (error.getClass().equals(NoConnectionError.class)) {
                        errorMessage = "Failed to connect server";
                    } else if (error.getClass().equals(NetworkError.class)) {
                        errorMessage = "Check your internet connection";
                    } else if (error.getClass().equals(AuthFailureError.class)) {
                        errorMessage = "Invalid credentials";
                    }
                } else {
                    String result = new String(networkResponse.data);
                    try {
                        JSONObject response = new JSONObject(result);
                        String message = response.getString("msg");

                        Log.e("Error Message", message);

                        if (networkResponse.statusCode == 404) {
                            errorMessage = "Resource not found";
                        } else if (networkResponse.statusCode == 401) {
                            errorMessage = message + "";
                        } else if (networkResponse.statusCode == 400) {
                            errorMessage = message + "";
                        } else if (networkResponse.statusCode == 500) {
                            errorMessage = message + " Something is getting wrong";
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                Log.i("Error", errorMessage);
                error.printStackTrace();
                Toast.makeText(getActivity(), errorMessage, Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                // add headers <key,value>
                headers.put("Content-Type", "application/json");
                headers.put("x-karza-key", "JmEIIONAwjN6cEY");
                return headers;
            }
        };

        jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(20 * 1000, 0,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(jsonObjectRequest, tag_json_obj);
    }

    private void KarzaCheckPassportPoa(CharSequence fileNumber) {
        // Tag used to cancel the request
        String tag_json_obj = "json_obj_req";

        pbPoaValidation.setVisibility(View.VISIBLE);

        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("fileNo", fileNumber);
            jsonBody.put("dob", dob.replace('-', '/'));
            jsonBody.put("consent", "Y");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, "https://api.karza.in/v3/passport-verification", jsonBody, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                pbPoaValidation.setVisibility(View.GONE);
                try {
                    poaVerifyId = response.getString("request_id");
                    JSONObject obj = response.getJSONObject("result");
                    int status_code = response.getInt("statusCode");
                    if (status_code == 101) {
                        JSONObject nameObj = obj.getJSONObject("name");
                        String fName = nameObj.getString("nameFromPassport");
                        String lName = nameObj.getString("surnameFromPassport");
                        if (fName.toLowerCase().equals(f_name.toLowerCase()) && lName.toLowerCase().equals(l_name.toLowerCase())) {
                            isPoaValid = true;
                            tilAddressProofNo.setErrorEnabled(false);
                            ivPoaValid.setImageResource(R.drawable.ic_check_circle_green);
                            tilAddressProofNo.setEnabled(false);
                        } else if (fName.toLowerCase().equals(f_name.toLowerCase())) {
                            isPoaValid = true;
                            tilAddressProofNo.setErrorEnabled(false);
                            ivPoaValid.setImageResource(R.drawable.ic_check_circle_green);
                            tilAddressProofNo.setEnabled(false);
                        } else {
                            isPoaValid = false;
                            tilAddressProofNo.setError("Name did not match");
                            mAlert.showAlertDialog(getActivity(), "Validation Error", getResources().getString(R.string.err_msg_passport_name_match), false);
                        }
                    } else {
                        isPoaValid = false;
                        tilAddressProofNo.setError("Passport is not valid.");
                        mAlert.showAlertDialog(getActivity(), "Validation Error", getResources().getString(R.string.err_msg_passport_number), false);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                //snackbar.dismiss();
                pbPoaValidation.setVisibility(View.GONE);

                NetworkResponse networkResponse = error.networkResponse;
                String errorMessage = "Unknown error";
                if (networkResponse == null) {
                    if (error.getClass().equals(TimeoutError.class)) {
                        errorMessage = "Request timeout";
                    } else if (error.getClass().equals(NoConnectionError.class)) {
                        errorMessage = "Failed to connect server";
                    } else if (error.getClass().equals(NetworkError.class)) {
                        errorMessage = "Check your internet connection";
                    } else if (error.getClass().equals(AuthFailureError.class)) {
                        errorMessage = "Invalid credentials";
                    }
                } else {
                    String result = new String(networkResponse.data);
                    try {
                        JSONObject response = new JSONObject(result);
                        String message = response.getString("msg");

                        Log.e("Error Message", message);

                        if (networkResponse.statusCode == 404) {
                            errorMessage = "Resource not found";
                        } else if (networkResponse.statusCode == 401) {
                            errorMessage = message + "";
                        } else if (networkResponse.statusCode == 400) {
                            errorMessage = message + "";
                        } else if (networkResponse.statusCode == 500) {
                            errorMessage = message + " Something is getting wrong";
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                Log.i("Error", errorMessage);
                error.printStackTrace();
                Toast.makeText(getActivity(), errorMessage, Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                // add headers <key,value>
                headers.put("Content-Type", "application/json");
                headers.put("x-karza-key", "JmEIIONAwjN6cEY");
                return headers;
            }
        };

        jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(20 * 1000, 0,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(jsonObjectRequest, tag_json_obj);
    }

    private void GetSdkTokenPoi() {
        // Tag used to cancel the request
        String tag_json_obj = "json_obj_req";

        final Snackbar snackbar = Snackbar.make(coordinatorLayout, "Connecting..", Snackbar.LENGTH_INDEFINITE);
        snackbar.show();

        JSONObject jsonBody = new JSONObject();
        JSONArray arr = new JSONArray();
        arr.put("aadhaar_xml");
        try {
            jsonBody.put("productId", arr);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, "https://api.karza.in/v2/get-jwt", jsonBody, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                snackbar.dismiss();
                try {
                    JSONObject obj = response.getJSONObject("result");
                    String karzaToken = obj.getString("karzaToken");
                    String status_code = response.getString("status-code");
                    if (status_code.equals("101")) {
                        // open aadhaar xml download window
                        Intent intent = new Intent(getActivity(), com.karza
                                .aadhaarsdk.AadharActivity.class);
                        intent.putExtra("KARZA-TOKEN", karzaToken);
                        intent.putExtra("CLIENT", "farmeasy_user");
                        intent.putExtra("ENV", "prod");
                        startActivityForResult(intent, 100);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                snackbar.dismiss();

                NetworkResponse networkResponse = error.networkResponse;
                String errorMessage = "Unknown error";
                if (networkResponse == null) {
                    if (error.getClass().equals(TimeoutError.class)) {
                        errorMessage = "Request timeout";
                    } else if (error.getClass().equals(NoConnectionError.class)) {
                        errorMessage = "Failed to connect server";
                    } else if (error.getClass().equals(NetworkError.class)) {
                        errorMessage = "Check your internet connection";
                    } else if (error.getClass().equals(AuthFailureError.class)) {
                        errorMessage = "Invalid credentials";
                    }
                } else {
                    String result = new String(networkResponse.data);
                    try {
                        JSONObject response = new JSONObject(result);
                        String message = response.getString("msg");

                        Log.e("Error Message", message);

                        if (networkResponse.statusCode == 404) {
                            errorMessage = "Resource not found";
                        } else if (networkResponse.statusCode == 401) {
                            errorMessage = message + "";
                        } else if (networkResponse.statusCode == 400) {
                            errorMessage = message + "";
                        } else if (networkResponse.statusCode == 500) {
                            errorMessage = message + " Something is getting wrong";
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                Log.i("Error", errorMessage);
                error.printStackTrace();
                Toast.makeText(getActivity(), errorMessage, Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                // add headers <key,value>
                headers.put("Content-Type", "application/json");
                headers.put("x-karza-key", "JmEIIONAwjN6cEY");
                return headers;
            }
        };

        jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(20 * 1000, 0,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(jsonObjectRequest, tag_json_obj);
    }

    private void GetSdkTokenPoa() {
        // Tag used to cancel the request
        String tag_json_obj = "json_obj_req";

        final Snackbar snackbar = Snackbar.make(coordinatorLayout, "Connecting..", Snackbar.LENGTH_INDEFINITE);
        snackbar.show();

        JSONObject jsonBody = new JSONObject();
        JSONArray arr = new JSONArray();
        arr.put("aadhaar_xml");
        try {
            jsonBody.put("productId", arr);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, "https://api.karza.in/v2/get-jwt", jsonBody, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                snackbar.dismiss();
                try {
                    JSONObject obj = response.getJSONObject("result");
                    String karzaToken = obj.getString("karzaToken");
                    String status_code = response.getString("status-code");
                    if (status_code.equals("101")) {
                        // open aadhaar xml download window
                        Intent intent = new Intent(getActivity(), com.karza
                                .aadhaarsdk.AadharActivity.class);
                        intent.putExtra("KARZA-TOKEN", karzaToken);
                        intent.putExtra("CLIENT", "farmeasy_user");
                        intent.putExtra("ENV", "prod");
                        startActivityForResult(intent, 101);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                snackbar.dismiss();

                NetworkResponse networkResponse = error.networkResponse;
                String errorMessage = "Unknown error";
                if (networkResponse == null) {
                    if (error.getClass().equals(TimeoutError.class)) {
                        errorMessage = "Request timeout";
                    } else if (error.getClass().equals(NoConnectionError.class)) {
                        errorMessage = "Failed to connect server";
                    } else if (error.getClass().equals(NetworkError.class)) {
                        errorMessage = "Check your internet connection";
                    } else if (error.getClass().equals(AuthFailureError.class)) {
                        errorMessage = "Invalid credentials";
                    }
                } else {
                    String result = new String(networkResponse.data);
                    try {
                        JSONObject response = new JSONObject(result);
                        String message = response.getString("msg");

                        Log.e("Error Message", message);

                        if (networkResponse.statusCode == 404) {
                            errorMessage = "Resource not found";
                        } else if (networkResponse.statusCode == 401) {
                            errorMessage = message + "";
                        } else if (networkResponse.statusCode == 400) {
                            errorMessage = message + "";
                        } else if (networkResponse.statusCode == 500) {
                            errorMessage = message + " Something is went wrong";
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                Log.i("Error", errorMessage);
                error.printStackTrace();
                Toast.makeText(getActivity(), errorMessage, Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                // add headers <key,value>
                headers.put("Content-Type", "application/json");
                headers.put("x-karza-key", "JmEIIONAwjN6cEY");
                return headers;
            }
        };

        jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(20 * 1000, 0,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(jsonObjectRequest, tag_json_obj);
    }

    private void GetMaskedAadhaarPoi() {
        String tag_custom_multi_part_obj = "custom_multi_part_obj_req";

        final Snackbar snackbar = Snackbar
                .make(coordinatorLayout, "Getting masked aadhaar card. please wait..", Snackbar.LENGTH_INDEFINITE);
        snackbar.show();
        // Custom Volley Request
        VolleyMultipartRequest volleyMultipartRequest = new VolleyMultipartRequest(Request.Method.POST, "https://api.karza.in/v3/ocr/kyc",
                new Response.Listener<NetworkResponse>() {
                    @Override
                    public void onResponse(NetworkResponse response) {
                        snackbar.dismiss();
                        try {
                            JSONObject obj1 = new JSONObject(new String(response.data));
                            JSONArray jsonArray = obj1.getJSONArray("result");
                            JSONObject obj2 = jsonArray.getJSONObject(0);
                            JSONObject objDetails = obj2.getJSONObject("details");
                            JSONObject objImgUrl = objDetails.getJSONObject("imageUrl");
                            String imgUrl = objImgUrl.getString("value");
                            urlImageIdProofFront = imgUrl;
                            JSONObject objName = objDetails.getJSONObject("name");
                            String docName = objName.getString("value");
                            String[] docNameParts = docName.split(" ");
                            if (f_name.toLowerCase().equals(docNameParts[0].toLowerCase())) {
                                // Download image from url
                                Glide.with(getActivity())
                                        .asBitmap().load(imgUrl)
                                        .listener(new RequestListener<Bitmap>() {
                                                      @Override
                                                      public boolean onLoadFailed(@Nullable GlideException e, Object o, Target<Bitmap> target, boolean b) {
                                                          bitmapIdProof = null;
                                                          isAttachedIdProof = false;
                                                          cvBtnAttachIdProof.setBackgroundColor(Color.parseColor("#447DF0"));
                                                          Toast.makeText(getActivity(), getResources().getString(R.string.err_msg_masked_image), Toast.LENGTH_SHORT).show();
                                                          return false;
                                                      }

                                                      @Override
                                                      public boolean onResourceReady(Bitmap bitmap, Object o, Target<Bitmap> target, DataSource dataSource, boolean b) {
                                                          bitmapIdProof = null;
                                                          // Discard the old value
                                                          bitmapIdProof = bitmap;
                                                          isAttachedIdProof = true;
                                                          return false;
                                                      }
                                                  }
                                        ).submit();
                            } else {
                                bitmapIdProof = null;
                                cvBtnAttachIdProof.setBackgroundColor(Color.parseColor("#447DF0"));
                                isAttachedIdProof = false;
                                Toast.makeText(getActivity(), getResources().getString(R.string.err_msg_doc_name_match), Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        snackbar.dismiss();
                        // Don't send any image
                        bitmapIdProof = null;
                        cvBtnAttachIdProof.setBackgroundColor(Color.parseColor("#447DF0"));

                        NetworkResponse networkResponse = error.networkResponse;
                        String errorMessage = "Unknown error";
                        if (networkResponse == null) {
                            if (error.getClass().equals(TimeoutError.class)) {
                                errorMessage = "Request timeout";
                            } else if (error.getClass().equals(NoConnectionError.class)) {
                                errorMessage = "Failed to connect server";
                            } else if (error.getClass().equals(NetworkError.class)) {
                                errorMessage = "Check your internet connection";
                            }
                        } else {
                            String result = new String(networkResponse.data);
                            try {
                                JSONObject response = new JSONObject(result);
                                String message = "";

                                Log.e("Error Message", message);

                                if (networkResponse.statusCode == 404) {
                                    errorMessage = "Resource not found";
                                } else if (networkResponse.statusCode == 401) {
                                    errorMessage = message + "";
                                } else if (networkResponse.statusCode == 400) {
                                    errorMessage = message + "";
                                } else if (networkResponse.statusCode == 500) {
                                    errorMessage = message + " Something is getting wrong";
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                        Log.i("Error", errorMessage);
                        error.printStackTrace();
                        Toast.makeText(getActivity(), errorMessage, Toast.LENGTH_SHORT).show();
                    }
                }) {

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                headers.put("x-karza-key", "JmEIIONAwjN6cEY");
                return headers;
            }

            /*
             * If you want to add more parameters with the image
             * you can do it here
             * here we have only one parameter with the image
             * which is tags
             * */
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();

                // Add Parameters
                params.put("maskAadhaar", "true");

                return params;
            }

            /*
             * Here we are passing image by renaming it with a unique name
             * */
            @Override
            protected Map<String, DataPart> getByteData() {
                Map<String, DataPart> params = new HashMap<>();
                long imgAadhaarPhoto = System.currentTimeMillis() + 1;
                if (isAttachedIdProof) {
                    params.put("file", new DataPart(imgAadhaarPhoto + ".jpg", getFileDataFromDrawable(bitmapIdProof)));
                }
                return params;
            }
        };

        volleyMultipartRequest.setRetryPolicy(new DefaultRetryPolicy(20 * 1000, 0,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(volleyMultipartRequest, tag_custom_multi_part_obj);
    }

    private void GetMaskedAadhaarPoa() {
        String tag_custom_multi_part_obj = "custom_multi_part_obj_req";

        final Snackbar snackbar = Snackbar
                .make(coordinatorLayout, "Getting masked aadhaar card. please wait..", Snackbar.LENGTH_INDEFINITE);
        snackbar.show();
        // Custom Volley Request
        VolleyMultipartRequest volleyMultipartRequest = new VolleyMultipartRequest(Request.Method.POST, "https://api.karza.in/v3/ocr/kyc",
                new Response.Listener<NetworkResponse>() {
                    @Override
                    public void onResponse(NetworkResponse response) {
                        snackbar.dismiss();
                        try {
                            JSONObject obj1 = new JSONObject(new String(response.data));
                            JSONArray jsonArray = obj1.getJSONArray("result");
                            JSONObject obj2 = jsonArray.getJSONObject(0);
                            JSONObject objDetails = obj2.getJSONObject("details");
                            JSONObject objImgUrl = objDetails.getJSONObject("imageUrl");
                            String imgUrl = objImgUrl.getString("value");
                            urlImageAddressProofFront = imgUrl;
                            JSONObject objName = objDetails.getJSONObject("name");
                            String docName = objName.getString("value");
                            String[] docNameParts = docName.split(" ");
                            if (f_name.toLowerCase().equals(docNameParts[0].toLowerCase())) {
                                // Download image from url
                                Glide.with(getActivity())
                                        .asBitmap().load(imgUrl)
                                        .listener(new RequestListener<Bitmap>() {
                                                      @Override
                                                      public boolean onLoadFailed(@Nullable GlideException e, Object o, Target<Bitmap> target, boolean b) {
                                                          bitmapAddressProof = null;
                                                          isAttachedAddressProof = false;
                                                          cvBtnAttachAddressProof.setBackgroundColor(Color.parseColor("#447DF0"));
                                                          Toast.makeText(getActivity(), getResources().getString(R.string.err_msg_masked_image), Toast.LENGTH_SHORT).show();
                                                          return false;
                                                      }

                                                      @Override
                                                      public boolean onResourceReady(Bitmap bitmap, Object o, Target<Bitmap> target, DataSource dataSource, boolean b) {
                                                          bitmapAddressProof = null;
                                                          // Discard the old value
                                                          bitmapAddressProof = bitmap;
                                                          isAttachedAddressProof = true;
                                                          return false;
                                                      }
                                                  }
                                        ).submit();
                            } else {
                                bitmapAddressProof = null;
                                cvBtnAttachAddressProof.setBackgroundColor(Color.parseColor("#447DF0"));
                                isAttachedAddressProof = false;
                                Toast.makeText(getActivity(), getResources().getString(R.string.err_msg_doc_name_match), Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        snackbar.dismiss();
                        bitmapAddressProof = null;
                        cvBtnAttachAddressProof.setBackgroundColor(Color.parseColor("#447DF0"));

                        NetworkResponse networkResponse = error.networkResponse;
                        String errorMessage = "Unknown error";
                        if (networkResponse == null) {
                            if (error.getClass().equals(TimeoutError.class)) {
                                errorMessage = "Request timeout";
                            } else if (error.getClass().equals(NoConnectionError.class)) {
                                errorMessage = "Failed to connect server";
                            } else if (error.getClass().equals(NetworkError.class)) {
                                errorMessage = "Check your internet connection";
                            }
                        } else {
                            String result = new String(networkResponse.data);
                            try {
                                JSONObject response = new JSONObject(result);
                                String message = "";

                                Log.e("Error Message", message);

                                if (networkResponse.statusCode == 404) {
                                    errorMessage = "Resource not found";
                                } else if (networkResponse.statusCode == 401) {
                                    errorMessage = message + "";
                                } else if (networkResponse.statusCode == 400) {
                                    errorMessage = message + "";
                                } else if (networkResponse.statusCode == 500) {
                                    errorMessage = message + " Something is getting wrong";
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                        Log.i("Error", errorMessage);
                        error.printStackTrace();
                        Toast.makeText(getActivity(), errorMessage, Toast.LENGTH_SHORT).show();
                    }
                }) {

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                headers.put("x-karza-key", "JmEIIONAwjN6cEY");
                return headers;
            }

            /*
             * If you want to add more parameters with the image
             * you can do it here
             * here we have only one parameter with the image
             * which is tags
             * */
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();

                // Add Parameters
                params.put("maskAadhaar", "true");

                return params;
            }

            /*
             * Here we are passing image by renaming it with a unique name
             * */
            @Override
            protected Map<String, DataPart> getByteData() {
                Map<String, DataPart> params = new HashMap<>();
                long imgAadhaarPhoto = System.currentTimeMillis() + 1;
                if (isAttachedAddressProof) {
                    params.put("file", new DataPart(imgAadhaarPhoto + ".jpg", getFileDataFromDrawable(bitmapAddressProof)));
                }
                return params;
            }
        };

        volleyMultipartRequest.setRetryPolicy(new DefaultRetryPolicy(20 * 1000, 0,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(volleyMultipartRequest, tag_custom_multi_part_obj);
    }

    private void GetMaskedAadhaarPoaBack() {
        String tag_custom_multi_part_obj = "custom_multi_part_obj_req";

        final Snackbar snackbar = Snackbar
                .make(coordinatorLayout, "Getting masked aadhaar card. please wait..", Snackbar.LENGTH_INDEFINITE);
        snackbar.show();
        // Custom Volley Request
        VolleyMultipartRequest volleyMultipartRequest = new VolleyMultipartRequest(Request.Method.POST, "https://api.karza.in/v3/ocr/kyc",
                new Response.Listener<NetworkResponse>() {
                    @Override
                    public void onResponse(NetworkResponse response) {
                        snackbar.dismiss();
                        try {
                            JSONObject obj1 = new JSONObject(new String(response.data));
                            JSONArray jsonArray = obj1.getJSONArray("result");
                            JSONObject obj2 = jsonArray.getJSONObject(0);
                            JSONObject objDetails = obj2.getJSONObject("details");
                            JSONObject objImgUrl = objDetails.getJSONObject("imageUrl");
                            String imgUrl = objImgUrl.getString("value");
                            urlImageAddressProofBack = imgUrl;
                            if (obj2.getString("type").equals("Aadhaar Back")) {
                                // Download image from url
                                Glide.with(getActivity())
                                        .asBitmap().load(imgUrl)
                                        .listener(new RequestListener<Bitmap>() {
                                                      @Override
                                                      public boolean onLoadFailed(@Nullable GlideException e, Object o, Target<Bitmap> target, boolean b) {
                                                          bitmapAddressProofBack = null;
                                                          isAttachedAddressProofBack = false;
                                                          cvBtnAttachAddressProofBack.setBackgroundColor(Color.parseColor("#447DF0"));
                                                          Toast.makeText(getActivity(), getResources().getString(R.string.err_msg_masked_image), Toast.LENGTH_SHORT).show();
                                                          return false;
                                                      }

                                                      @Override
                                                      public boolean onResourceReady(Bitmap bitmap, Object o, Target<Bitmap> target, DataSource dataSource, boolean b) {
                                                          bitmapAddressProofBack = null;
                                                          // Discard the old value
                                                          bitmapAddressProofBack = bitmap;
                                                          isAttachedAddressProofBack = true;
                                                          return false;
                                                      }
                                                  }
                                        ).submit();
                            } else {
                                bitmapAddressProofBack = null;
                                cvBtnAttachAddressProofBack.setBackgroundColor(Color.parseColor("#447DF0"));
                                isAttachedAddressProofBack = false;
                                Toast.makeText(getActivity(), getResources().getString(R.string.err_msg_aadhar_back), Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        snackbar.dismiss();
                        bitmapAddressProofBack = null;
                        cvBtnAttachAddressProofBack.setBackgroundColor(Color.parseColor("#447DF0"));

                        NetworkResponse networkResponse = error.networkResponse;
                        String errorMessage = "Unknown error";
                        if (networkResponse == null) {
                            if (error.getClass().equals(TimeoutError.class)) {
                                errorMessage = "Request timeout";
                            } else if (error.getClass().equals(NoConnectionError.class)) {
                                errorMessage = "Failed to connect server";
                            } else if (error.getClass().equals(NetworkError.class)) {
                                errorMessage = "Check your internet connection";
                            }
                        } else {
                            String result = new String(networkResponse.data);
                            try {
                                JSONObject response = new JSONObject(result);
                                String message = "";

                                Log.e("Error Message", message);

                                if (networkResponse.statusCode == 404) {
                                    errorMessage = "Resource not found";
                                } else if (networkResponse.statusCode == 401) {
                                    errorMessage = message + "";
                                } else if (networkResponse.statusCode == 400) {
                                    errorMessage = message + "";
                                } else if (networkResponse.statusCode == 500) {
                                    errorMessage = message + " Something is getting wrong";
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                        Log.i("Error", errorMessage);
                        error.printStackTrace();
                        Toast.makeText(getActivity(), errorMessage, Toast.LENGTH_SHORT).show();
                    }
                }) {

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                headers.put("x-karza-key", "JmEIIONAwjN6cEY");
                return headers;
            }

            /*
             * If you want to add more parameters with the image
             * you can do it here
             * here we have only one parameter with the image
             * which is tags
             * */
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();

                // Add Parameters
                params.put("maskAadhaar", "true");

                return params;
            }

            /*
             * Here we are passing image by renaming it with a unique name
             * */
            @Override
            protected Map<String, DataPart> getByteData() {
                Map<String, DataPart> params = new HashMap<>();
                long imgAadhaarPhoto = System.currentTimeMillis() + 1;
                if (isAttachedAddressProofBack) {
                    params.put("file", new DataPart(imgAadhaarPhoto + ".jpg", getFileDataFromDrawable(bitmapAddressProofBack)));
                }
                return params;
            }
        };

        volleyMultipartRequest.setRetryPolicy(new DefaultRetryPolicy(20 * 1000, 0,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(volleyMultipartRequest, tag_custom_multi_part_obj);
    }

    public static <K, V> K getSingleKeyFromValue(Map<K, V> map, V value) {
        for (Map.Entry<K, V> entry : map.entrySet()) {
            if (Objects.equals(value, entry.getValue())) {
                return entry.getKey();
            }
        }
        return null;
    }

    @Override
    public void onBackPressed() {
        getActivity().getSupportFragmentManager().popBackStack();
    }
}
