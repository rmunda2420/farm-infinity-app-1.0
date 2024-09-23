package com.farminfinity.farminfinity.Fragments;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.text.InputFilter;
import android.text.Spanned;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatMultiAutoCompleteTextView;
import androidx.cardview.widget.CardView;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkError;
import com.android.volley.NetworkResponse;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.auth0.android.jwt.Claim;
import com.auth0.android.jwt.JWT;
import com.baoyachi.stepview.HorizontalStepView;
import com.baoyachi.stepview.bean.StepBean;
import com.farminfinity.farminfinity.App.AppController;
import com.farminfinity.farminfinity.Helper.AlertDialogManager;
import com.farminfinity.farminfinity.Helper.EndPoints;
import com.farminfinity.farminfinity.Helper.InputValidation;
import com.farminfinity.farminfinity.Helper.SessionManager;
import com.farminfinity.farminfinity.R;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import org.json.JSONException;
import org.json.JSONObject;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static android.app.Activity.RESULT_OK;

public class EditLevelThreeFragment extends BaseFragment implements View.OnClickListener {
    private static final String TAG = "EditLevelThreeFragment";
    private String userType;
    private String token;
    private int intUserType;

    private String fId = null;
    private boolean isLevelDone = false;

    //private final int REQ_CODE_MIC = 140;

    private List<String> arr_cash_crop;
    private List<String> arr_cereal_grains;
    private List<String> arr_oil_seeds;
    private List<String> arr_pulses;
    private List<String> arr_spices;
    private List<String> arr_vegetables;
    private List<String> arr_plantationCrop;
    private List<String> arr_plantationFruits;
    private List<String> arr_medicinalPlant;
    private List<String> arr_exoticCrop;
    private List<String> arr_cropOthers;

    private CoordinatorLayout coordinatorLayout;

    //private ImageView ivMicKCashCrp;

    private TextInputLayout tilFarmland;
    private TextInputLayout tilLivestkBull, tilLivestkCow, tilLivestkCalf, tilLivestkCamel, tilLivestkHorseMare, tilLivestkGoat, tilLivestkSheep, tilLivestkPig, tilLivestkHen, tilLivestkDuck, tilLivestkRabbit, tilLivestkOther;
    private TextInputLayout tilRabicrpCashCrop, tilRabicrpCerealsGrains, tilRabicrpOilSeeds, tilRabicrpPulses, tilRabicrpSpices, tilRabicrpVegetables, tilRabicrpOther;
    private TextInputLayout tilKharifcrpCashCrop, tilKharifcrpCerealsGrains, tilKharifcrpOilSeeds, tilKharifcrpPulses, tilKharifcrpSpices, tilKharifcrpVegetables, tilKharifOther;
    private TextInputLayout tilSummercrpCashCrop, tilSummercrpCerealsGrains, tilSummercrpOilSeeds, tilSummercrpPulses, tilSummercrpSpices, tilSummercrpVegetables, tilSummercrpOther;
    private TextInputLayout tilPlantationCrop, tilPlantationcrpFruits, tilPlantationcrpArea, tilMedicinalPlants, tilExoticPlants;

    private TextInputEditText tietFarmland;
    private TextInputEditText tietLivestkBull, tietLivestkCow, tietLivestkCalf, tietLivestkCamel, tietLivestkHorseMare, tietLivestkGoat, tietLivestkSheep, tietLivestkPig, tietLivestkHen, tietLivestkDuck, tietLivestkRabbit, tietLivestkOther;

    private AppCompatMultiAutoCompleteTextView mactRabicrpCashCrop, mactRabicrpCerealsGrains, mactRabicrpOilSeeds, mactRabicrpPulses, mactRabicrpSpices, mactRabicrpVegetables, mactRabicrpOther;
    private AppCompatMultiAutoCompleteTextView mactKharifcrpCashCrop, mactKharifcrpCerealsGrains, mactKharifcrpOilSeeds, mactKharifcrpPulses, mactKharifcrpSpices, mactKharifcrpVegetables, mactKharifOther;
    private AppCompatMultiAutoCompleteTextView mactSummercrpCashCrop, mactSummercrpCerealsGrains, mactSummercrpOilSeeds, mactSummercrpPulses, mactSummercrpSpices, mactSummercrpVegetables, mactSummercrpOther;
    private AppCompatMultiAutoCompleteTextView mactPlantationCrop, mactPlantationcrpFruits, mactMedicinalPlants, mactExoticPlants;

    private TextInputEditText tietPlantationcrpArea;

    private CheckBox cbIrrWaterPump, cbIrrSolarWaterPump, cbIrrHydrophonics, cbIrrRainfed, cbIrrRainWtrHrvPond, cbIrrSprinklers, cbIrrDripIrr, cbIrrRiverCanal, cbIrrOther;
    private CheckBox cbFarmQuipTractor, cbFarmEquipPloughCattleDriven, cbFarmEquipPowerTiller, cbFarmEquipSeeder, cbFarmEquipWeeder, cbFarmEquipHarvester, cbFarmEquipOther;
    private CheckBox cbLivestkBull, cbLivestkCow, cbLivestkCalf, cbLivestkCamel, cbLivestkHorseMare, cbLivestkGoat, cbLivestkSheep, cbLivestkPig, cbLivestkHen, cbLivestkDuck, cbLivestkRabbit, cbLivestkOther;
    private CheckBox cbRabicrpCashCrop, cbRabicrpCerealsGrains, cbRabicrpOilSeeds, cbRabicrpPulses, cbRabicrpSpices, cbRabicrpVegetables, cbRabicrpOther;
    private CheckBox cbKharifcrpCashCrop, cbKharifcrpCerealsGrains, cbKharifcrpOilSeeds, cbKharifcrpPulses, cbKharifcrpSpices, cbKharifcrpVegetables, cbKharifcrpOther;
    private CheckBox cbSummercrpCashCrop, cbSummercrpCerealsGrains, cbSummercrpOilSeeds, cbSummercrpPulses, cbSummercrpSpices, cbSummercrpVegetables, cbSummercrpOther;
    private CheckBox cbPlantationCrop, cbPlantationcrpFruits, cbMedicinalPlant, cbExoticPlant;

    private RadioGroup radGroupQLandHolding, radGroupQIrrgFslty, radGroupQFrmFrmEqp, radGroupQLivestk, radGroupQRabicrp, radGroupQKharifcrp, radGroupQSummercrp, radGroupQPlantsnCrp, radGroupQFrmStrg, radioGroupQPostHrvst;

    private LinearLayout llLandHolding, llIrrgFslty, llFarmEquip, llLivestk, llRabiCrp, llKharifCrp, llSummerCrp, llPlantsnCrp;

    private EditText etChemicalFertUsed, etOrganigFertUsed, etPesticideUsed;

    private Spinner spinner_land_area;

    private CardView cvBtnSaveAndPrint;

    private HorizontalStepView horizontalStepView;

    private SessionManager mSessionManager;
    private InputValidation mInputValidation;
    private AlertDialogManager mAlert;

    private String mode = null;

    private boolean landHolding_yn, irrigationFacility_yn, farmEquipments_yn, storageFacility_yn, postHarvest_yn, livestock_yn, rabiCrops_yn, kharifCrops_yn, summerCrops_yn, plantationCrops_yn = false;

    private boolean irrWaterPump, irrSolarWaterPump, irrHydrophonics, irrRainfed, irrRainWtrHrvPond, irrSprinklers, irrDripIrrigation, irrRiverOrCanal, irrOther = false;
    private boolean farmEquipTractor, farmEquipPloughCattleDriven, farmEquipTiller, farmEquipSeeder, farmEquipWeeder, farmEquipHarvester, farmEquipOther = false;
    private boolean lvstkBull, lvstkCow, lvstkCalf, lvstkCamel, lvstkHorseMare, lvstkGoat, lvstkSheep, lvstkPig, lvstkHen, lvstkDuck, lvstkRabbit, lvstkOther = false;
    private boolean rabiCashCrop, rabiCereals, rabiOilSeed, rabiPulses, rabiSpices, rabiVegi, rabiOther = false;
    private boolean kharifCashCrop, kharifCereals, kharifOilSeed, kharifPulses, kharifSpices, kharifVegi, kharifOther = false;
    private boolean summerCashCrop, summerCereals, summerOilSeed, summerPulses, summerSpices, summerVegi, summerOther = false;
    private boolean plantationCrop, plantationcrpFruits, medicinalPlant, exoticPlant = false;

    public EditLevelThreeFragment() {
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
        View view = inflater.inflate(R.layout.fragment_edit_level_three, container, false);

        // Get farmer id from previous fragment
        fId = getArguments().getString("FID");

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
                intUserType = allClaims.get("int_user_type").asInt();
            }
        } catch (Exception e) {
            Toast.makeText(getActivity(), e.toString(), Toast.LENGTH_SHORT).show();
        }

        coordinatorLayout = (CoordinatorLayout) view.findViewById(R.id.cl_fragment_edit_level_three);

        //ivMicKCashCrp = view.findViewById(R.id.iv_mic_kharifcrpCash_fragment_level_three);

        tilFarmland = view.findViewById(R.id.til_farmland_fragment_edit_level_three);
        tilLivestkBull = view.findViewById(R.id.til_livestkBull_fragment_edit_level_three);
        tilLivestkCow = view.findViewById(R.id.til_livestkCow_fragment_edit_level_three);
        tilLivestkCalf = view.findViewById(R.id.til_livestkCalf_fragment_edit_level_three);
        tilLivestkCamel = view.findViewById(R.id.til_livestkCamel_fragment_edit_level_three);
        tilLivestkHorseMare = view.findViewById(R.id.til_livestkHorse_fragment_edit_level_three);
        tilLivestkGoat = view.findViewById(R.id.til_livestkGoat_fragment_edit_level_three);
        tilLivestkSheep = view.findViewById(R.id.til_livestkSheep_fragment_edit_level_three);
        tilLivestkPig = view.findViewById(R.id.til_livestkPig_fragment_edit_level_three);
        tilLivestkHen = view.findViewById(R.id.til_livestkHen_fragment_edit_level_three);
        tilLivestkDuck = view.findViewById(R.id.til_livestkDuck_fragment_edit_level_three);
        tilLivestkRabbit = view.findViewById(R.id.til_livestkRabbit_fragment_edit_level_three);
        tilLivestkOther = view.findViewById(R.id.til_livestkOther_fragment_edit_level_three);
        tilRabicrpCashCrop = view.findViewById(R.id.til_rabicrpCash_fragment_edit_level_three);
        tilRabicrpCerealsGrains = view.findViewById(R.id.til_rabicrpGrains_fragment_edit_level_three);
        tilRabicrpOilSeeds = view.findViewById(R.id.til_rabicrpOilSeeds_fragment_edit_level_three);
        tilRabicrpPulses = view.findViewById(R.id.til_rabicrpPulse_fragment_edit_level_three);
        tilRabicrpSpices = view.findViewById(R.id.til_rabicrpSpices_fragment_edit_level_three);
        tilRabicrpVegetables = view.findViewById(R.id.til_rabicrpVegi_fragment_edit_level_three);
        tilRabicrpOther = view.findViewById(R.id.til_rabicrpOther_fragment_edit_level_three);
        tilKharifcrpCashCrop = view.findViewById(R.id.til_kharifcrpCash_fragment_edit_level_three);
        tilKharifcrpCerealsGrains = view.findViewById(R.id.til_kharifcrpGrains_fragment_edit_level_three);
        tilKharifcrpOilSeeds = view.findViewById(R.id.til_kharifcrpOilSeeds_fragment_edit_level_three);
        tilKharifcrpPulses = view.findViewById(R.id.til_kharifcrpPulse_fragment_edit_level_three);
        tilKharifcrpSpices = view.findViewById(R.id.til_kharifcrpSpices_fragment_edit_level_three);
        tilKharifcrpVegetables = view.findViewById(R.id.til_kharifcrpVegi_fragment_edit_level_three);
        tilKharifOther = view.findViewById(R.id.til_kharifcrpOther_fragment_edit_level_three);
        tilSummercrpCashCrop = view.findViewById(R.id.til_summercrpCash_fragment_edit_level_three);
        tilSummercrpCerealsGrains = view.findViewById(R.id.til_summercrpGrains_fragment_edit_level_three);
        tilSummercrpOilSeeds = view.findViewById(R.id.til_summercrpOilSeeds_fragment_edit_level_three);
        tilSummercrpPulses = view.findViewById(R.id.til_summercrpPulse_fragment_edit_level_three);
        tilSummercrpSpices = view.findViewById(R.id.til_summercrpSpices_fragment_edit_level_three);
        tilSummercrpVegetables = view.findViewById(R.id.til_summercrpVegi_fragment_edit_level_three);
        tilSummercrpOther = view.findViewById(R.id.til_summercrpOther_fragment_edit_level_three);
        tilPlantationCrop = view.findViewById(R.id.til_plantationcrpCrop_fragment_edit_level_three);
        tilPlantationcrpFruits = view.findViewById(R.id.til_plantationcrpFruits_fragment_edit_level_three);
        tilPlantationcrpArea = view.findViewById(R.id.til_plantationCrpArea_fragment_edit_level_three);
        tilMedicinalPlants = view.findViewById(R.id.til_medicinalPlant_fragment_edit_level_three);
        tilExoticPlants = view.findViewById(R.id.til_exoticCrop_fragment_edit_level_three);

        tietFarmland = view.findViewById(R.id.tiet_farmland_fragment_edit_level_three);
        tietLivestkBull = view.findViewById(R.id.tiet_livestkBull_fragment_edit_level_three);
        tietLivestkCow = view.findViewById(R.id.tiet_livestkCow_fragment_edit_level_three);
        tietLivestkCalf = view.findViewById(R.id.tiet_livestkCalf_fragment_edit_level_three);
        tietLivestkCamel = view.findViewById(R.id.tiet_livestkCamel_fragment_edit_level_three);
        tietLivestkHorseMare = view.findViewById(R.id.tiet_livestkHorse_fragment_edit_level_three);
        tietLivestkGoat = view.findViewById(R.id.tiet_livestkGoat_fragment_edit_level_three);
        tietLivestkSheep = view.findViewById(R.id.tiet_livestkSheep_fragment_edit_level_three);
        tietLivestkPig = view.findViewById(R.id.tiet_livestkPig_fragment_edit_level_three);
        tietLivestkHen = view.findViewById(R.id.tiet_livestkHen_fragment_edit_level_three);
        tietLivestkDuck = view.findViewById(R.id.tiet_livestkDuck_fragment_edit_level_three);
        tietLivestkRabbit = view.findViewById(R.id.tiet_livestkRabbit_fragment_edit_level_three);
        tietLivestkOther = view.findViewById(R.id.tiet_livestkOther_fragment_edit_level_three);
        tietPlantationcrpArea = view.findViewById(R.id.tiet_plantationCrpArea_fragment_edit_level_three);
        mactRabicrpCashCrop = view.findViewById(R.id.mact_rabicrpCash_fragment_edit_level_three);
        mactRabicrpCerealsGrains = view.findViewById(R.id.mact_rabicrpGrains_fragment_edit_level_three);
        mactRabicrpOilSeeds = view.findViewById(R.id.mact_rabicrpOilSeeds_fragment_edit_level_three);
        mactRabicrpPulses = view.findViewById(R.id.mact_rabicrpPulse_fragment_edit_level_three);
        mactRabicrpSpices = view.findViewById(R.id.mact_rabicrpSpices_fragment_edit_level_three);
        mactRabicrpVegetables = view.findViewById(R.id.mact_rabicrpVegi_fragment_edit_level_three);
        mactRabicrpOther = view.findViewById(R.id.mact_rabicrpOther_fragment_edit_level_three);
        mactKharifcrpCashCrop = view.findViewById(R.id.mact_kharifcrpCash_fragment_edit_level_three);
        mactKharifcrpCerealsGrains = view.findViewById(R.id.mact_kharifcrpGrains_fragment_edit_level_three);
        mactKharifcrpOilSeeds = view.findViewById(R.id.mact_kharifcrpOilSeeds_fragment_edit_level_three);
        mactKharifcrpPulses = view.findViewById(R.id.mact_kharifcrpPulse_fragment_edit_level_three);
        mactKharifcrpSpices = view.findViewById(R.id.mact_kharifcrpSpices_fragment_edit_level_three);
        mactKharifcrpVegetables = view.findViewById(R.id.mact_kharifcrpVegi_fragment_edit_level_three);
        mactKharifOther = view.findViewById(R.id.mact_kharifcrpOther_fragment_edit_level_three);
        mactSummercrpCashCrop = view.findViewById(R.id.mact_summercrpCash_fragment_edit_level_three);
        mactSummercrpCerealsGrains = view.findViewById(R.id.mact_summercrpGrains_fragment_edit_level_three);
        mactSummercrpOilSeeds = view.findViewById(R.id.mact_summercrpOilSeeds_fragment_edit_level_three);
        mactSummercrpPulses = view.findViewById(R.id.mact_summercrpPulse_fragment_edit_level_three);
        mactSummercrpSpices = view.findViewById(R.id.mact_summercrpSpices_fragment_edit_level_three);
        mactSummercrpVegetables = view.findViewById(R.id.mact_summercrpVegi_fragment_edit_level_three);
        mactSummercrpOther = view.findViewById(R.id.mact_summercrpOther_fragment_edit_level_three);
        mactPlantationCrop = view.findViewById(R.id.mact_plantationCrop_fragment_edit_level_three);
        mactPlantationcrpFruits = view.findViewById(R.id.mact_plantationFruit_fragment_edit_level_three);
        mactMedicinalPlants = view.findViewById(R.id.mact_medicinalPlant_fragment_edit_level_three);
        mactExoticPlants = view.findViewById(R.id.mact_exoticCrop_fragment_edit_level_three);

        cbIrrWaterPump = view.findViewById(R.id.cb_irrWaterPump_fragment_edit_level_three);
        cbIrrSolarWaterPump = view.findViewById(R.id.cb_irrSolarWaterPump_fragment_edit_level_three);
        cbIrrHydrophonics = view.findViewById(R.id.cb_irrHydrophonics_fragment_edit_level_three);
        cbIrrRainfed = view.findViewById(R.id.cb_irrRainfed_fragment_edit_level_three);
        cbIrrRainWtrHrvPond = view.findViewById(R.id.cb_irrRainWrtHrvstPond_fragment_edit_level_three);
        cbIrrSprinklers = view.findViewById(R.id.cb_irrSprinkler_fragment_edit_level_three);
        cbIrrDripIrr = view.findViewById(R.id.cb_irrDripIrrigation_fragment_edit_level_three);
        cbIrrRiverCanal = view.findViewById(R.id.cb_irrRiverCanal_fragment_edit_level_three);
        cbIrrOther = view.findViewById(R.id.cb_irrOther_fragment_edit_level_three);
        cbFarmQuipTractor = view.findViewById(R.id.cb_equipTractor_fragment_edit_level_three);
        cbFarmEquipPloughCattleDriven = view.findViewById(R.id.cb_equipCattlePlough_fragment_edit_level_three);
        cbFarmEquipPowerTiller = view.findViewById(R.id.cb_equipPowerTiller_fragment_edit_level_three);
        cbFarmEquipSeeder = view.findViewById(R.id.cb_equipSeeder_fragment_edit_level_three);
        cbFarmEquipWeeder = view.findViewById(R.id.cb_equipWeeder_fragment_edit_level_three);
        cbFarmEquipHarvester = view.findViewById(R.id.cb_equipHarvester_fragment_edit_level_three);
        cbFarmEquipOther = view.findViewById(R.id.cb_equipOther_fragment_edit_level_three);
        cbLivestkBull = view.findViewById(R.id.cb_livestkBull_fragment_edit_level_three);
        cbLivestkCow = view.findViewById(R.id.cb_livestkCow_fragment_edit_level_three);
        cbLivestkCalf = view.findViewById(R.id.cb_livestkCalf_fragment_edit_level_three);
        cbLivestkCamel = view.findViewById(R.id.cb_livestkCamel_fragment_edit_level_three);
        cbLivestkHorseMare = view.findViewById(R.id.cb_livestkHorse_fragment_edit_level_three);
        cbLivestkGoat = view.findViewById(R.id.cb_livestkGoat_fragment_edit_level_three);
        cbLivestkSheep = view.findViewById(R.id.cb_livestkSheep_fragment_edit_level_three);
        cbLivestkPig = view.findViewById(R.id.cb_livestkPig_fragment_edit_level_three);
        cbLivestkHen = view.findViewById(R.id.cb_livestkHen_fragment_edit_level_three);
        cbLivestkDuck = view.findViewById(R.id.cb_livestkDuck_fragment_edit_level_three);
        cbLivestkRabbit = view.findViewById(R.id.cb_livestkRabbit_fragment_edit_level_three);
        cbLivestkOther = view.findViewById(R.id.cb_livestkOther_fragment_edit_level_three);
        cbRabicrpCashCrop = view.findViewById(R.id.cb_rabicrpCash_fragment_edit_level_three);
        cbRabicrpCerealsGrains = view.findViewById(R.id.cb_rabicrpGrains_fragment_edit_level_three);
        cbRabicrpOilSeeds = view.findViewById(R.id.cb_rabicrpOilSeeds_fragment_edit_level_three);
        cbRabicrpPulses = view.findViewById(R.id.cb_rabicrpPulse_fragment_edit_level_three);
        cbRabicrpSpices = view.findViewById(R.id.cb_rabicrpSpices_fragment_edit_level_three);
        cbRabicrpVegetables = view.findViewById(R.id.cb_rabicrpVegi_fragment_edit_level_three);
        cbRabicrpOther = view.findViewById(R.id.cb_rabicrpOther_fragment_edit_level_three);
        cbKharifcrpCashCrop = view.findViewById(R.id.cb_kharifcrpCash_fragment_edit_level_three);
        cbKharifcrpCerealsGrains = view.findViewById(R.id.cb_kharifcrpGrains_fragment_edit_level_three);
        cbKharifcrpOilSeeds = view.findViewById(R.id.cb_kharifcrpOilSeeds_fragment_edit_level_three);
        cbKharifcrpPulses = view.findViewById(R.id.cb_kharifcrpPulse_fragment_edit_level_three);
        cbKharifcrpSpices = view.findViewById(R.id.cb_kharifcrpSpices_fragment_edit_level_three);
        cbKharifcrpVegetables = view.findViewById(R.id.cb_kharifcrpVegi_fragment_edit_level_three);
        cbKharifcrpOther = view.findViewById(R.id.cb_kharifcrpOther_fragment_edit_level_three);
        cbSummercrpCashCrop = view.findViewById(R.id.cb_summercrpCash_fragment_edit_level_three);
        cbSummercrpCerealsGrains = view.findViewById(R.id.cb_summercrpGrains_fragment_edit_level_three);
        cbSummercrpOilSeeds = view.findViewById(R.id.cb_summercrpOilSeeds_fragment_edit_level_three);
        cbSummercrpPulses = view.findViewById(R.id.cb_summercrpPulse_fragment_edit_level_three);
        cbSummercrpSpices = view.findViewById(R.id.cb_summercrpSpices_fragment_edit_level_three);
        cbSummercrpVegetables = view.findViewById(R.id.cb_summercrpVegi_fragment_edit_level_three);
        cbSummercrpOther = view.findViewById(R.id.cb_summercrpOther_fragment_edit_level_three);
        cbPlantationCrop = view.findViewById(R.id.cb_plantationCrop_fragment_edit_level_three);
        cbPlantationcrpFruits = view.findViewById(R.id.cb_plantationcrpFruits_fragment_edit_level_three);
        cbMedicinalPlant = view.findViewById(R.id.cb_medicinalPlant_fragment_edit_level_three);
        cbExoticPlant = view.findViewById(R.id.cb_exoticPlant_fragment_edit_level_three);

        etChemicalFertUsed = view.findViewById(R.id.et_chemicalFertUsd_fragment_edit_level_three);
        etOrganigFertUsed = view.findViewById(R.id.et_organicFertUsd_fragment_edit_level_three);
        etPesticideUsed = view.findViewById(R.id.et_pestiUsd_fragment_edit_level_three);

        radGroupQLandHolding = view.findViewById(R.id.radioGroup_qLandHolding_fragment_edit_level_three);
        radGroupQIrrgFslty = view.findViewById(R.id.radioGroup_qIrrigationFacility_fragment_edit_level_three);
        radGroupQFrmFrmEqp = view.findViewById(R.id.radioGroup_qFarmEquipment_fragment_edit_level_three);
        radGroupQFrmStrg = view.findViewById(R.id.radioGroup_qStorageFacility_fragment_edit_level_three);
        radioGroupQPostHrvst = view.findViewById(R.id.radioGroup_qPostHrvstFacility_fragment_edit_level_three);
        radGroupQLivestk = view.findViewById(R.id.radioGroup_qLivestock_fragment_edit_level_three);
        radGroupQRabicrp = view.findViewById(R.id.radioGroup_qRabiCrop_fragment_edit_level_three);
        radGroupQKharifcrp = view.findViewById(R.id.radioGroup_qKharifCrop_fragment_edit_level_three);
        radGroupQSummercrp = view.findViewById(R.id.radioGroup_qSummerCrop_fragment_edit_level_three);
        radGroupQPlantsnCrp = view.findViewById(R.id.radioGroup_qPlantationCrops_fragment_edit_level_three);

        llLandHolding = view.findViewById(R.id.ll_landHolding_fragment_edit_level_three);
        llIrrgFslty = view.findViewById(R.id.ll_irrigationFacility_fragment_edit_level_three);
        llFarmEquip = view.findViewById(R.id.ll_farmEquipment_fragment_edit_level_three);
        llLivestk = view.findViewById(R.id.ll_livestock_fragment_edit_level_three);
        llRabiCrp = view.findViewById(R.id.ll_rabiCrop_fragment_edit_level_three);
        llKharifCrp = view.findViewById(R.id.ll_kharifCrop_fragment_edit_level_three);
        llSummerCrp = view.findViewById(R.id.ll_summerfCrop_fragment_edit_level_three);
        llPlantsnCrp = view.findViewById(R.id.ll_plantationCrops_fragment_edit_level_three);

        spinner_land_area = (Spinner) view.findViewById(R.id.spinner_land_area_unit_fragment_edit_level_three);

        horizontalStepView = view.findViewById(R.id.step_view_fragment_edit_level_three);

        cvBtnSaveAndPrint = (CardView) view.findViewById(R.id.cv_btn_save_fragment_edit_level_three);

        tietFarmland.setFilters(new InputFilter[]{new DecimalDigitsInputFilter(5, 2)});
        tietPlantationcrpArea.setFilters(new InputFilter[]{new DecimalDigitsInputFilter(5, 2)});
        etChemicalFertUsed.setFilters(new InputFilter[]{new DecimalDigitsInputFilter(5, 2)});
        etOrganigFertUsed.setFilters(new InputFilter[]{new DecimalDigitsInputFilter(5, 2)});
        etPesticideUsed.setFilters(new InputFilter[]{new DecimalDigitsInputFilter(5, 2)});

        initListeners();

        // Gender Spinner Drop down elements
        List<String> lstAreaUnits = new ArrayList<>();
        lstAreaUnits.add("--Select--");
        lstAreaUnits.add(getResources().getString(R.string.ddl_unit_Acre));
        lstAreaUnits.add(getResources().getString(R.string.ddl_unit_Bhigha));

        List<StepBean> stepsBeanList = new ArrayList<>();
        StepBean stepBean0 = new StepBean("1", 1);
        StepBean stepBean1 = new StepBean("2", 1);
        StepBean stepBean2 = new StepBean("3", 1);
        StepBean stepBean3 = new StepBean("4", 0);
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

        arr_cash_crop = Arrays.asList(getResources().getStringArray(R.array.cash_crop));
        arr_cereal_grains = Arrays.asList(getResources().getStringArray(R.array.cereals_grains));
        arr_oil_seeds = Arrays.asList(getResources().getStringArray(R.array.oil_seeds));
        arr_pulses = Arrays.asList(getResources().getStringArray(R.array.pulses));
        arr_spices = Arrays.asList(getResources().getStringArray(R.array.spices));
        arr_vegetables = Arrays.asList(getResources().getStringArray(R.array.vegetables));
        arr_plantationCrop = Arrays.asList(getResources().getStringArray(R.array.plantation_crop));
        arr_plantationFruits = Arrays.asList(getResources().getStringArray(R.array.plantation_fruits));
        arr_medicinalPlant = Arrays.asList(getResources().getStringArray(R.array.medicinal_plant));
        arr_exoticCrop = Arrays.asList(getResources().getStringArray(R.array.exotic_crop));
        arr_cropOthers = Arrays.asList(getResources().getStringArray(R.array.crop_others));

        ArrayAdapter<String> rabiCashCropAdapter = new ArrayAdapter<>(getActivity(),
                android.R.layout.simple_list_item_1, arr_cash_crop);
        mactRabicrpCashCrop.setAdapter(rabiCashCropAdapter);
        mactRabicrpCashCrop.setTokenizer(new AppCompatMultiAutoCompleteTextView.CommaTokenizer());

        ArrayAdapter<String> kharifCashCropAdapter = new ArrayAdapter<>(getActivity(),
                android.R.layout.simple_list_item_1, arr_cash_crop);
        mactKharifcrpCashCrop.setAdapter(kharifCashCropAdapter);
        mactKharifcrpCashCrop.setTokenizer(new AppCompatMultiAutoCompleteTextView.CommaTokenizer());

        ArrayAdapter<String> summerCashCropAdapter = new ArrayAdapter<>(getActivity(),
                android.R.layout.simple_list_item_1, arr_cash_crop);
        mactSummercrpCashCrop.setAdapter(summerCashCropAdapter);
        mactSummercrpCashCrop.setTokenizer(new AppCompatMultiAutoCompleteTextView.CommaTokenizer());

        ArrayAdapter<String> rabiCerealAdapter = new ArrayAdapter<>(getActivity(),
                android.R.layout.simple_list_item_1, arr_cereal_grains);
        mactRabicrpCerealsGrains.setAdapter(rabiCerealAdapter);
        mactRabicrpCerealsGrains.setTokenizer(new AppCompatMultiAutoCompleteTextView.CommaTokenizer());

        ArrayAdapter<String> kharifCerealAdapter = new ArrayAdapter<>(getActivity(),
                android.R.layout.simple_list_item_1, arr_cereal_grains);
        mactKharifcrpCerealsGrains.setAdapter(kharifCerealAdapter);
        mactKharifcrpCerealsGrains.setTokenizer(new AppCompatMultiAutoCompleteTextView.CommaTokenizer());

        ArrayAdapter<String> summerCerealAdapter = new ArrayAdapter<>(getActivity(),
                android.R.layout.simple_list_item_1, arr_cereal_grains);
        mactSummercrpCerealsGrains.setAdapter(summerCerealAdapter);
        mactSummercrpCerealsGrains.setTokenizer(new AppCompatMultiAutoCompleteTextView.CommaTokenizer());

        ArrayAdapter<String> rabiOilSeedAdapter = new ArrayAdapter<>(getActivity(),
                android.R.layout.simple_list_item_1, arr_oil_seeds);
        mactRabicrpOilSeeds.setAdapter(rabiOilSeedAdapter);
        mactRabicrpOilSeeds.setTokenizer(new AppCompatMultiAutoCompleteTextView.CommaTokenizer());

        ArrayAdapter<String> kharifOilSeedAdapter = new ArrayAdapter<>(getActivity(),
                android.R.layout.simple_list_item_1, arr_oil_seeds);
        mactKharifcrpOilSeeds.setAdapter(kharifOilSeedAdapter);
        mactKharifcrpOilSeeds.setTokenizer(new AppCompatMultiAutoCompleteTextView.CommaTokenizer());

        ArrayAdapter<String> summerOilSeedAdapter = new ArrayAdapter<>(getActivity(),
                android.R.layout.simple_list_item_1, arr_oil_seeds);
        mactSummercrpOilSeeds.setAdapter(summerOilSeedAdapter);
        mactSummercrpOilSeeds.setTokenizer(new AppCompatMultiAutoCompleteTextView.CommaTokenizer());

        ArrayAdapter<String> rabiPulsesAdapter = new ArrayAdapter<>(getActivity(),
                android.R.layout.simple_list_item_1, arr_pulses);
        mactRabicrpPulses.setAdapter(rabiPulsesAdapter);
        mactRabicrpPulses.setTokenizer(new AppCompatMultiAutoCompleteTextView.CommaTokenizer());

        ArrayAdapter<String> kharifPulsesAdapter = new ArrayAdapter<>(getActivity(),
                android.R.layout.simple_list_item_1, arr_pulses);
        mactKharifcrpPulses.setAdapter(kharifPulsesAdapter);
        mactKharifcrpPulses.setTokenizer(new AppCompatMultiAutoCompleteTextView.CommaTokenizer());

        ArrayAdapter<String> summerPulsesAdapter = new ArrayAdapter<>(getActivity(),
                android.R.layout.simple_list_item_1, arr_pulses);
        mactSummercrpPulses.setAdapter(summerPulsesAdapter);
        mactSummercrpPulses.setTokenizer(new AppCompatMultiAutoCompleteTextView.CommaTokenizer());

        ArrayAdapter<String> rabiSpicesAdapter = new ArrayAdapter<>(getActivity(),
                android.R.layout.simple_list_item_1, arr_spices);
        mactRabicrpSpices.setAdapter(rabiSpicesAdapter);
        mactRabicrpSpices.setTokenizer(new AppCompatMultiAutoCompleteTextView.CommaTokenizer());

        ArrayAdapter<String> kharifSpicesAdapter = new ArrayAdapter<>(getActivity(),
                android.R.layout.simple_list_item_1, arr_spices);
        mactKharifcrpSpices.setAdapter(kharifSpicesAdapter);
        mactKharifcrpSpices.setTokenizer(new AppCompatMultiAutoCompleteTextView.CommaTokenizer());

        ArrayAdapter<String> summerSpicesAdapter = new ArrayAdapter<>(getActivity(),
                android.R.layout.simple_list_item_1, arr_spices);
        mactSummercrpSpices.setAdapter(summerSpicesAdapter);
        mactSummercrpSpices.setTokenizer(new AppCompatMultiAutoCompleteTextView.CommaTokenizer());

        ArrayAdapter<String> rabiVegetablesAdapter = new ArrayAdapter<>(getActivity(),
                android.R.layout.simple_list_item_1, arr_vegetables);
        mactRabicrpVegetables.setAdapter(rabiVegetablesAdapter);
        mactRabicrpVegetables.setTokenizer(new AppCompatMultiAutoCompleteTextView.CommaTokenizer());

        ArrayAdapter<String> kharifVegetablesAdapter = new ArrayAdapter<>(getActivity(),
                android.R.layout.simple_list_item_1, arr_vegetables);
        mactKharifcrpVegetables.setAdapter(kharifVegetablesAdapter);
        mactKharifcrpVegetables.setTokenizer(new AppCompatMultiAutoCompleteTextView.CommaTokenizer());

        ArrayAdapter<String> summerVegetablesAdapter = new ArrayAdapter<>(getActivity(),
                android.R.layout.simple_list_item_1, arr_vegetables);
        mactSummercrpVegetables.setAdapter(summerVegetablesAdapter);
        mactSummercrpVegetables.setTokenizer(new AppCompatMultiAutoCompleteTextView.CommaTokenizer());

        // Others

        ArrayAdapter<String> plantationCropAdapter = new ArrayAdapter<>(getActivity(),
                android.R.layout.simple_list_item_1, arr_plantationCrop);
        mactPlantationCrop.setTokenizer(new AppCompatMultiAutoCompleteTextView.CommaTokenizer());
        mactPlantationCrop.setAdapter(plantationCropAdapter);

        ArrayAdapter<String> plantationbFruitsAdapter = new ArrayAdapter<>(getActivity(),
                android.R.layout.simple_list_item_1, arr_plantationFruits);
        mactPlantationcrpFruits.setAdapter(plantationbFruitsAdapter);
        mactPlantationcrpFruits.setTokenizer(new AppCompatMultiAutoCompleteTextView.CommaTokenizer());

        ArrayAdapter<String> medicinalPlantsAdapter = new ArrayAdapter<>(getActivity(),
                android.R.layout.simple_list_item_1, arr_medicinalPlant);
        mactMedicinalPlants.setAdapter(medicinalPlantsAdapter);
        mactMedicinalPlants.setTokenizer(new AppCompatMultiAutoCompleteTextView.CommaTokenizer());

        ArrayAdapter<String> exoticPlantsAdapter = new ArrayAdapter<>(getActivity(),
                android.R.layout.simple_list_item_1, arr_exoticCrop);
        mactExoticPlants.setAdapter(exoticPlantsAdapter);
        mactExoticPlants.setTokenizer(new AppCompatMultiAutoCompleteTextView.CommaTokenizer());

        // Creating adapter for spinner
        ArrayAdapter<String> dataAdapterAreaUnits = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, lstAreaUnits);
        // Drop down layout style - list view spinner
        dataAdapterAreaUnits.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // attaching data adapter to spinner
        spinner_land_area.setAdapter(dataAdapterAreaUnits);

        radGroupQLandHolding.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.radBtn_qLandHoldingYes_fragment_edit_level_three) {
                    llLandHolding.setVisibility(View.VISIBLE);
                    landHolding_yn = true;
                } else if (checkedId == R.id.radBtn_qLandHoldingNo_fragment_edit_level_three) {
                    llLandHolding.setVisibility(View.GONE);
                    landHolding_yn = false;
                    resetLand();
                }
            }
        });

        radGroupQIrrgFslty.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.radBtn_qIrrigationFacilityYes_fragment_edit_level_three) {
                    llIrrgFslty.setVisibility(View.VISIBLE);
                    irrigationFacility_yn = true;
                } else if (checkedId == R.id.radBtn_qIrrigationFacilityNo_fragment_edit_level_three) {
                    llIrrgFslty.setVisibility(View.GONE);
                    irrigationFacility_yn = false;
                    resetIrrigation();
                }
            }
        });

        radGroupQFrmFrmEqp.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.radBtn_qFarmEquipmentYes_fragment_edit_level_three) {
                    llFarmEquip.setVisibility(View.VISIBLE);
                    farmEquipments_yn = true;
                } else if (checkedId == R.id.radBtn_qFarmEquipmentNo_fragment_edit_level_three) {
                    llFarmEquip.setVisibility(View.GONE);
                    farmEquipments_yn = false;
                    resetEquipments();
                }
            }
        });

        radGroupQFrmStrg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.radBtn_qStorageFacilityYes_fragment_edit_level_three)
                    storageFacility_yn = true;
                else if (checkedId == R.id.radBtn_qStorageFacilityNo_fragment_edit_level_three)
                    storageFacility_yn = false;
            }
        });

        radioGroupQPostHrvst.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.radBtn_qPostHrvstFacilityYes_fragment_edit_level_three)
                    postHarvest_yn = true;
                else if (checkedId == R.id.radBtn_qPostHrvstFacilityNo_fragment_edit_level_three)
                    postHarvest_yn = false;
            }
        });

        radGroupQLivestk.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.radBtn_qLivestockYes_fragment_edit_level_three) {
                    llLivestk.setVisibility(View.VISIBLE);
                    livestock_yn = true;
                } else if (checkedId == R.id.radBtn_qLivestockNo_fragment_edit_level_three) {
                    llLivestk.setVisibility(View.GONE);
                    livestock_yn = false;
                    resetLivestock();
                }
            }
        });

        radGroupQRabicrp.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.radBtn_qRabiCropYes_fragment_edit_level_three) {
                    llRabiCrp.setVisibility(View.VISIBLE);
                    rabiCrops_yn = true;
                } else if (checkedId == R.id.radBtn_qRabiCropNo_fragment_edit_level_three) {
                    llRabiCrp.setVisibility(View.GONE);
                    rabiCrops_yn = false;
                    resetRabiCrop();
                }
            }
        });

        radGroupQKharifcrp.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.radBtn_qKharifCropYes_fragment_edit_level_three) {
                    llKharifCrp.setVisibility(View.VISIBLE);
                    kharifCrops_yn = true;
                } else if (checkedId == R.id.radBtn_qKharifCropNo_fragment_edit_level_three) {
                    llKharifCrp.setVisibility(View.GONE);
                    kharifCrops_yn = false;
                    resetKharifCrop();
                }
            }
        });

        radGroupQSummercrp.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.radBtn_qSummerCropYes_fragment_edit_level_three) {
                    llSummerCrp.setVisibility(View.VISIBLE);
                    summerCrops_yn = true;
                } else if (checkedId == R.id.radBtn_qSummerCropNo_fragment_edit_level_three) {
                    llSummerCrp.setVisibility(View.GONE);
                    summerCrops_yn = false;
                    resetSummerCrop();
                }
            }
        });

        radGroupQPlantsnCrp.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.radBtn_qPlantationCropsYes_fragment_edit_level_three) {
                    llPlantsnCrp.setVisibility(View.VISIBLE);
                    plantationCrops_yn = true;
                } else if (checkedId == R.id.radBtn_qPlantationCropsNo_fragment_edit_level_three) {
                    llPlantsnCrp.setVisibility(View.GONE);
                    plantationCrops_yn = false;
                    resetPlantation();
                }
            }
        });

        cvBtnSaveAndPrint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ValidateFields();
            }
        });
        InitData();

        return view;
    }

    private void initListeners() {
        cbIrrWaterPump.setOnClickListener(this);
        cbIrrSolarWaterPump.setOnClickListener(this);
        cbIrrHydrophonics.setOnClickListener(this);
        cbIrrRainfed.setOnClickListener(this);
        cbIrrSprinklers.setOnClickListener(this);
        cbIrrRainWtrHrvPond.setOnClickListener(this);
        cbIrrDripIrr.setOnClickListener(this);
        cbIrrRiverCanal.setOnClickListener(this);
        cbIrrOther.setOnClickListener(this);

        cbFarmQuipTractor.setOnClickListener(this);
        cbFarmEquipPloughCattleDriven.setOnClickListener(this);
        cbFarmEquipPowerTiller.setOnClickListener(this);
        cbFarmEquipSeeder.setOnClickListener(this);
        cbFarmEquipWeeder.setOnClickListener(this);
        cbFarmEquipHarvester.setOnClickListener(this);
        cbFarmEquipOther.setOnClickListener(this);

        cbLivestkBull.setOnClickListener(this);
        cbLivestkCow.setOnClickListener(this);
        cbLivestkCalf.setOnClickListener(this);
        cbLivestkCamel.setOnClickListener(this);
        cbLivestkHorseMare.setOnClickListener(this);
        cbLivestkGoat.setOnClickListener(this);
        cbLivestkSheep.setOnClickListener(this);
        cbLivestkPig.setOnClickListener(this);
        cbLivestkHen.setOnClickListener(this);
        cbLivestkDuck.setOnClickListener(this);
        cbLivestkRabbit.setOnClickListener(this);
        cbLivestkOther.setOnClickListener(this);

        cbRabicrpCashCrop.setOnClickListener(this);
        cbRabicrpCerealsGrains.setOnClickListener(this);
        cbRabicrpOilSeeds.setOnClickListener(this);
        cbRabicrpPulses.setOnClickListener(this);
        cbRabicrpSpices.setOnClickListener(this);
        cbRabicrpVegetables.setOnClickListener(this);
        cbRabicrpOther.setOnClickListener(this);

        cbKharifcrpCashCrop.setOnClickListener(this);
        cbKharifcrpCerealsGrains.setOnClickListener(this);
        cbKharifcrpOilSeeds.setOnClickListener(this);
        cbKharifcrpPulses.setOnClickListener(this);
        cbKharifcrpSpices.setOnClickListener(this);
        cbKharifcrpVegetables.setOnClickListener(this);
        cbKharifcrpOther.setOnClickListener(this);

        cbSummercrpCashCrop.setOnClickListener(this);
        cbSummercrpCerealsGrains.setOnClickListener(this);
        cbSummercrpOilSeeds.setOnClickListener(this);
        cbSummercrpPulses.setOnClickListener(this);
        cbSummercrpSpices.setOnClickListener(this);
        cbSummercrpVegetables.setOnClickListener(this);
        cbSummercrpOther.setOnClickListener(this);

        cbPlantationCrop.setOnClickListener(this);
        cbPlantationcrpFruits.setOnClickListener(this);
        cbMedicinalPlant.setOnClickListener(this);
        cbExoticPlant.setOnClickListener(this);

        //ivMicKCashCrp.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.cb_irrDripIrrigation_fragment_edit_level_three:
                irrDripIrrigation = cbIrrDripIrr.isChecked();
                break;
            case R.id.cb_irrHydrophonics_fragment_edit_level_three:
                irrHydrophonics = cbIrrHydrophonics.isChecked();
                break;
            case R.id.cb_irrRainfed_fragment_edit_level_three:
                irrRainfed = cbIrrRainfed.isChecked();
                break;
            case R.id.cb_irrRainWrtHrvstPond_fragment_edit_level_three:
                irrRainWtrHrvPond = cbIrrRainWtrHrvPond.isChecked();
                break;

            case R.id.cb_irrRiverCanal_fragment_edit_level_three:
                irrRiverOrCanal = cbIrrRiverCanal.isChecked();
                break;
            case R.id.cb_irrSprinkler_fragment_edit_level_three:
                irrSprinklers = cbIrrSprinklers.isChecked();
                break;
            case R.id.cb_irrSolarWaterPump_fragment_edit_level_three:
                if (cbIrrSolarWaterPump.isChecked()) {
                    irrSolarWaterPump = true;
                    cbIrrWaterPump.setChecked(false);
                } else {
                    irrSolarWaterPump = false;
                }
                break;
            case R.id.cb_irrWaterPump_fragment_edit_level_three:
                if (cbIrrWaterPump.isChecked()) {
                    irrWaterPump = true;
                    cbIrrSolarWaterPump.setChecked(false);
                } else {
                    irrWaterPump = false;
                }
                break;
            case R.id.cb_irrOther_fragment_edit_level_three:
                irrOther = cbIrrOther.isChecked();
                break;
            case R.id.cb_equipHarvester_fragment_edit_level_three:
                farmEquipHarvester = cbFarmEquipHarvester.isChecked();
                break;
            case R.id.cb_equipCattlePlough_fragment_edit_level_three:
                farmEquipPloughCattleDriven = cbFarmEquipPloughCattleDriven.isChecked();
                break;
            case R.id.cb_equipPowerTiller_fragment_edit_level_three:
                farmEquipTiller = cbFarmEquipPowerTiller.isChecked();
                break;
            case R.id.cb_equipSeeder_fragment_edit_level_three:
                farmEquipSeeder = cbFarmEquipSeeder.isChecked();
                break;
            case R.id.cb_equipTractor_fragment_edit_level_three:
                farmEquipTractor = cbFarmQuipTractor.isChecked();
                break;
            case R.id.cb_equipWeeder_fragment_edit_level_three:
                farmEquipWeeder = cbFarmEquipWeeder.isChecked();
                break;
            case R.id.cb_equipOther_fragment_edit_level_three:
                farmEquipOther = cbFarmEquipOther.isChecked();
                break;
            case R.id.cb_livestkBull_fragment_edit_level_three:
                if (cbLivestkBull.isChecked()) {
                    lvstkBull = true;
                    tietLivestkBull.setEnabled(true);
                    tietLivestkBull.setText(null);
                } else {
                    lvstkBull = false;
                    tietLivestkBull.setText("-");
                    tietLivestkBull.setEnabled(false);
                }
                break;
            case R.id.cb_livestkCow_fragment_edit_level_three:
                if (cbLivestkCow.isChecked()) {
                    lvstkCow = true;
                    tietLivestkCow.setEnabled(true);
                    tietLivestkCow.setText(null);
                } else {
                    lvstkCow = false;
                    tietLivestkCow.setText("-");
                    tietLivestkCow.setEnabled(false);
                }
                break;
            case R.id.cb_livestkCalf_fragment_edit_level_three:
                if (cbLivestkCalf.isChecked()) {
                    lvstkCalf = true;
                    tietLivestkCalf.setEnabled(true);
                    tietLivestkCalf.setText(null);
                } else {
                    lvstkCalf = false;
                    tietLivestkCalf.setText("-");
                    tietLivestkCalf.setEnabled(false);
                }
                break;
            case R.id.cb_livestkCamel_fragment_edit_level_three:
                if (cbLivestkCamel.isChecked()) {
                    lvstkCamel = true;
                    tietLivestkCamel.setEnabled(true);
                    tietLivestkCamel.setText(null);
                } else {
                    lvstkCamel = false;
                    tietLivestkCamel.setText("-");
                    tietLivestkCamel.setEnabled(false);
                }
                break;
            case R.id.cb_livestkHorse_fragment_edit_level_three:
                if (cbLivestkHorseMare.isChecked()) {
                    lvstkHorseMare = true;
                    tietLivestkHorseMare.setEnabled(true);
                    tietLivestkHorseMare.setText(null);
                } else {
                    lvstkHorseMare = false;
                    tietLivestkHorseMare.setText("-");
                    tietLivestkHorseMare.setEnabled(false);
                }
                break;
            case R.id.cb_livestkGoat_fragment_edit_level_three:
                if (cbLivestkGoat.isChecked()) {
                    lvstkGoat = true;
                    tietLivestkGoat.setEnabled(true);
                    tietLivestkGoat.setText(null);
                } else {
                    lvstkGoat = false;
                    tietLivestkGoat.setText("-");
                    tietLivestkGoat.setEnabled(false);
                }
                break;
            case R.id.cb_livestkSheep_fragment_edit_level_three:
                if (cbLivestkSheep.isChecked()) {
                    lvstkSheep = true;
                    tietLivestkSheep.setEnabled(true);
                    tietLivestkSheep.setText(null);
                } else {
                    lvstkSheep = false;
                    tietLivestkSheep.setText("-");
                    tietLivestkSheep.setEnabled(false);
                }
                break;
            case R.id.cb_livestkPig_fragment_edit_level_three:
                if (cbLivestkPig.isChecked()) {
                    lvstkPig = true;
                    tietLivestkPig.setEnabled(true);
                    tietLivestkPig.setText(null);
                } else {
                    lvstkPig = false;
                    tietLivestkPig.setText("-");
                    tietLivestkPig.setEnabled(false);
                }
                break;
            case R.id.cb_livestkHen_fragment_edit_level_three:
                if (cbLivestkHen.isChecked()) {
                    lvstkHen = true;
                    tietLivestkHen.setEnabled(true);
                    tietLivestkHen.setText(null);
                } else {
                    lvstkHen = false;
                    tietLivestkHen.setText("-");
                    tietLivestkHen.setEnabled(false);
                }
                break;
            case R.id.cb_livestkDuck_fragment_edit_level_three:
                if (cbLivestkDuck.isChecked()) {
                    lvstkDuck = true;
                    tietLivestkDuck.setEnabled(true);
                    tietLivestkDuck.setText(null);
                } else {
                    lvstkDuck = true;
                    tietLivestkDuck.setText("-");
                    tietLivestkDuck.setEnabled(false);
                }
                break;
            case R.id.cb_livestkRabbit_fragment_edit_level_three:
                if (cbLivestkRabbit.isChecked()) {
                    lvstkRabbit = true;
                    tietLivestkRabbit.setEnabled(true);
                    tietLivestkRabbit.setText(null);
                } else {
                    lvstkRabbit = false;
                    tietLivestkRabbit.setText("-");
                    tietLivestkRabbit.setEnabled(false);
                }
                break;
            case R.id.cb_livestkOther_fragment_edit_level_three:
                if (cbLivestkOther.isChecked()) {
                    lvstkOther = true;
                    tilLivestkOther.setVisibility(View.VISIBLE);
                    tietLivestkOther.setEnabled(true);
                    tietLivestkOther.setText(null);
                } else {
                    lvstkOther = false;
                    tilLivestkOther.setVisibility(View.GONE);
                    tietLivestkOther.setText("-");
                    tietLivestkOther.setEnabled(false);
                }
                break;
            case R.id.cb_rabicrpCash_fragment_edit_level_three:
                if (cbRabicrpCashCrop.isChecked()) {
                    rabiCashCrop = true;
                    mactRabicrpCashCrop.setEnabled(true);
                    mactRabicrpCashCrop.setText(null);
                } else {
                    rabiCashCrop = false;
                    mactRabicrpCashCrop.setText("-");
                    mactRabicrpCashCrop.setEnabled(false);
                }
                break;
            case R.id.cb_rabicrpGrains_fragment_edit_level_three:
                if (cbRabicrpCerealsGrains.isChecked()) {
                    rabiCereals = true;
                    mactRabicrpCerealsGrains.setEnabled(true);
                    mactRabicrpCerealsGrains.setText(null);
                } else {
                    rabiCereals = false;
                    mactRabicrpCerealsGrains.setText("-");
                    mactRabicrpCerealsGrains.setEnabled(false);
                }
                break;
            case R.id.cb_rabicrpOilSeeds_fragment_edit_level_three:
                if (cbRabicrpOilSeeds.isChecked()) {
                    rabiOilSeed = true;
                    mactRabicrpOilSeeds.setEnabled(true);
                    mactRabicrpOilSeeds.setText(null);
                } else {
                    rabiOilSeed = false;
                    mactRabicrpOilSeeds.setText("-");
                    mactRabicrpOilSeeds.setEnabled(false);
                }
                break;
            case R.id.cb_rabicrpPulse_fragment_edit_level_three:
                if (cbRabicrpPulses.isChecked()) {
                    rabiPulses = true;
                    mactRabicrpPulses.setEnabled(true);
                    mactRabicrpPulses.setText(null);
                } else {
                    rabiPulses = false;
                    mactRabicrpPulses.setText("-");
                    mactRabicrpPulses.setEnabled(false);
                }
                break;
            case R.id.cb_rabicrpSpices_fragment_edit_level_three:
                if (cbRabicrpSpices.isChecked()) {
                    rabiSpices = true;
                    mactRabicrpSpices.setEnabled(true);
                    mactRabicrpSpices.setText(null);
                } else {
                    rabiSpices = false;
                    mactRabicrpSpices.setText("-");
                    mactRabicrpSpices.setEnabled(false);
                }
                break;
            case R.id.cb_rabicrpVegi_fragment_edit_level_three:
                if (cbRabicrpVegetables.isChecked()) {
                    rabiVegi = true;
                    mactRabicrpVegetables.setEnabled(true);
                    mactRabicrpVegetables.setText(null);
                } else {
                    rabiVegi = false;
                    mactRabicrpVegetables.setText("-");
                    mactRabicrpVegetables.setEnabled(false);
                }
                break;
            case R.id.cb_rabicrpOther_fragment_edit_level_three:
                if (cbRabicrpOther.isChecked()) {
                    rabiOther = true;
                    tilRabicrpOther.setVisibility(View.VISIBLE);
                    mactRabicrpOther.setEnabled(true);
                    mactRabicrpOther.setText(null);
                } else {
                    rabiOther = false;
                    tilRabicrpOther.setVisibility(View.GONE);
                    mactRabicrpOther.setText("-");
                    mactRabicrpOther.setEnabled(false);
                }
                break;
            case R.id.cb_kharifcrpCash_fragment_edit_level_three:
                if (cbKharifcrpCashCrop.isChecked()) {
                    kharifCashCrop = true;
                    mactKharifcrpCashCrop.setEnabled(true);
                    mactKharifcrpCashCrop.setText(null);
                } else {
                    kharifCashCrop = false;
                    mactKharifcrpCashCrop.setText("-");
                    mactKharifcrpCashCrop.setEnabled(false);
                }
                break;
            case R.id.cb_kharifcrpGrains_fragment_edit_level_three:
                if (cbKharifcrpCerealsGrains.isChecked()) {
                    kharifCereals = true;
                    mactKharifcrpCerealsGrains.setEnabled(true);
                    mactKharifcrpCerealsGrains.setText(null);
                } else {
                    kharifCereals = false;
                    mactKharifcrpCerealsGrains.setText("-");
                    mactKharifcrpCerealsGrains.setEnabled(false);
                }
                break;
            case R.id.cb_kharifcrpOilSeeds_fragment_edit_level_three:
                if (cbKharifcrpOilSeeds.isChecked()) {
                    kharifOilSeed = true;
                    mactKharifcrpOilSeeds.setEnabled(true);
                    mactKharifcrpOilSeeds.setText(null);
                } else {
                    kharifOilSeed = false;
                    mactKharifcrpOilSeeds.setText("-");
                    mactKharifcrpOilSeeds.setEnabled(false);
                }
                break;
            case R.id.cb_kharifcrpPulse_fragment_edit_level_three:
                if (cbKharifcrpPulses.isChecked()) {
                    kharifPulses = true;
                    mactKharifcrpPulses.setEnabled(true);
                    mactKharifcrpPulses.setText(null);
                } else {
                    kharifPulses = false;
                    mactKharifcrpPulses.setText("-");
                    mactKharifcrpPulses.setEnabled(false);
                }
                break;
            case R.id.cb_kharifcrpSpices_fragment_edit_level_three:
                if (cbKharifcrpSpices.isChecked()) {
                    kharifSpices = true;
                    mactKharifcrpSpices.setEnabled(true);
                    mactKharifcrpSpices.setText(null);
                } else {
                    kharifSpices = false;
                    mactKharifcrpSpices.setText("-");
                    mactKharifcrpSpices.setEnabled(false);
                }
                break;
            case R.id.cb_kharifcrpVegi_fragment_edit_level_three:
                if (cbKharifcrpVegetables.isChecked()) {
                    kharifVegi = true;
                    mactKharifcrpVegetables.setEnabled(true);
                    mactKharifcrpVegetables.setText(null);
                } else {
                    kharifVegi = false;
                    mactKharifcrpVegetables.setText("-");
                    mactKharifcrpVegetables.setEnabled(false);
                }
                break;
            case R.id.cb_kharifcrpOther_fragment_edit_level_three:
                if (cbKharifcrpOther.isChecked()) {
                    kharifOther = true;
                    tilKharifOther.setVisibility(View.VISIBLE);
                    mactKharifOther.setEnabled(true);
                    mactKharifOther.setText(null);
                } else {
                    kharifOther = false;
                    tilKharifOther.setVisibility(View.GONE);
                    mactKharifOther.setText("-");
                    mactKharifOther.setEnabled(false);
                }
                break;
            case R.id.cb_summercrpCash_fragment_edit_level_three:
                if (cbSummercrpCashCrop.isChecked()) {
                    summerCashCrop = true;
                    mactSummercrpCashCrop.setEnabled(true);
                    mactSummercrpCashCrop.setText(null);
                } else {
                    summerCashCrop = false;
                    mactSummercrpCashCrop.setText("-");
                    mactSummercrpCashCrop.setEnabled(false);
                }
                break;
            case R.id.cb_summercrpGrains_fragment_edit_level_three:
                if (cbSummercrpCerealsGrains.isChecked()) {
                    summerCereals = true;
                    mactSummercrpCerealsGrains.setEnabled(true);
                    mactSummercrpCerealsGrains.setText(null);
                } else {
                    summerCereals = false;
                    mactSummercrpCerealsGrains.setText("-");
                    mactSummercrpCerealsGrains.setEnabled(false);
                }
                break;
            case R.id.cb_summercrpOilSeeds_fragment_edit_level_three:
                if (cbSummercrpOilSeeds.isChecked()) {
                    summerOilSeed = true;
                    mactSummercrpOilSeeds.setEnabled(true);
                    mactSummercrpOilSeeds.setText(null);
                } else {
                    summerOilSeed = false;
                    mactSummercrpOilSeeds.setText("-");
                    mactSummercrpOilSeeds.setEnabled(false);
                }
                break;
            case R.id.cb_summercrpPulse_fragment_edit_level_three:
                if (cbSummercrpPulses.isChecked()) {
                    summerPulses = true;
                    mactSummercrpPulses.setEnabled(true);
                    mactSummercrpPulses.setText(null);
                } else {
                    summerPulses = false;
                    mactSummercrpPulses.setText("-");
                    mactSummercrpPulses.setEnabled(false);
                }
                break;
            case R.id.cb_summercrpSpices_fragment_edit_level_three:
                if (cbSummercrpSpices.isChecked()) {
                    summerSpices = true;
                    mactSummercrpSpices.setEnabled(true);
                    mactSummercrpSpices.setText(null);
                } else {
                    summerSpices = false;
                    mactSummercrpSpices.setText("-");
                    mactSummercrpSpices.setEnabled(false);
                }
                break;
            case R.id.cb_summercrpVegi_fragment_edit_level_three:
                if (cbSummercrpVegetables.isChecked()) {
                    summerVegi = true;
                    mactSummercrpVegetables.setEnabled(true);
                    mactSummercrpVegetables.setText(null);
                } else {
                    summerVegi = false;
                    mactSummercrpVegetables.setText("-");
                    mactSummercrpVegetables.setEnabled(false);
                }
                break;
            case R.id.cb_summercrpOther_fragment_edit_level_three:
                if (cbSummercrpOther.isChecked()) {
                    summerOther = true;
                    tilSummercrpOther.setVisibility(View.VISIBLE);
                    mactSummercrpOther.setEnabled(true);
                    mactSummercrpOther.setText(null);
                } else {
                    summerOther = false;
                    tilSummercrpOther.setVisibility(View.GONE);
                    mactSummercrpOther.setText("-");
                    mactSummercrpOther.setEnabled(false);
                }
                break;
            case R.id.cb_plantationCrop_fragment_edit_level_three:
                if (cbPlantationCrop.isChecked()) {
                    plantationCrop = true;
                    mactPlantationCrop.setEnabled(true);
                    mactPlantationCrop.setText(null);
                } else {
                    plantationCrop = false;
                    mactPlantationCrop.setText("-");
                    mactPlantationCrop.setEnabled(false);
                }
                break;
            case R.id.cb_plantationcrpFruits_fragment_edit_level_three:
                if (cbPlantationcrpFruits.isChecked()) {
                    plantationcrpFruits = true;
                    mactPlantationcrpFruits.setEnabled(true);
                    mactPlantationcrpFruits.setText(null);
                } else {
                    plantationcrpFruits = false;
                    mactPlantationcrpFruits.setText("-");
                    mactPlantationcrpFruits.setEnabled(false);
                }
                break;
            case R.id.cb_medicinalPlant_fragment_edit_level_three:
                if (cbMedicinalPlant.isChecked()) {
                    medicinalPlant = true;
                    mactMedicinalPlants.setEnabled(true);
                    mactMedicinalPlants.setText(null);
                } else {
                    medicinalPlant = false;
                    mactMedicinalPlants.setText("-");
                    mactMedicinalPlants.setEnabled(false);
                }
                break;
            case R.id.cb_exoticPlant_fragment_edit_level_three:
                if (cbExoticPlant.isChecked()) {
                    exoticPlant = true;
                    mactExoticPlants.setEnabled(true);
                    mactExoticPlants.setText(null);
                } else {
                    exoticPlant = false;
                    mactExoticPlants.setText("-");
                    mactExoticPlants.setEnabled(false);
                }
                break;
            /*case R.id.iv_mic_kharifcrpCash_fragment_level_three:
                Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
                intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                        RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
                intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, mSessionManager.getPreferredLanguage());
                intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Need to speak");
                try {
                    startActivityForResult(intent, REQ_CODE_MIC);
                } catch (ActivityNotFoundException a) {
                    Toast.makeText(getActivity(),
                            "Sorry your device not supported",
                            Toast.LENGTH_SHORT).show();
                }
                break;*/
        }
    }

    private void ValidateFields() {
        if (landHolding_yn) {
            if (!mInputValidation.isInputEditTextFilled(tietFarmland, tilFarmland, getString(R.string.error_message_blank_field))) {
                return;
            }
        }
        if (spinner_land_area.getSelectedItem().toString().equals("--Select--")) {
            mAlert.showAlertDialog(getActivity(), "Validation Error", getResources().getString(R.string.err_msg_area_units), false);
            return;
        }
        if (irrigationFacility_yn) {
            if (!(irrWaterPump || irrSolarWaterPump || irrDripIrrigation || irrRainWtrHrvPond || irrHydrophonics || irrRainfed || irrSprinklers || irrRiverOrCanal || irrOther)) {
                mAlert.showAlertDialog(getActivity(), "Validation error", getResources().getString(R.string.err_msg_irr_fclty), false);
                return;
            }
        }
        if (farmEquipments_yn) {
            if (!(farmEquipTractor || farmEquipPloughCattleDriven || farmEquipTiller || farmEquipSeeder || farmEquipWeeder || farmEquipHarvester || farmEquipOther)) {
                mAlert.showAlertDialog(getActivity(), "Validation error", getResources().getString(R.string.err_msg_farm_equip), false);
                return;
            }
        }
        if (livestock_yn) {
            if (!(lvstkBull || lvstkCow || lvstkCalf || lvstkCamel || lvstkHorseMare || lvstkGoat || lvstkSheep || lvstkPig || lvstkHen || lvstkDuck || lvstkRabbit || lvstkOther)) {
                mAlert.showAlertDialog(getActivity(), "Validation error", getResources().getString(R.string.err_msg_livestock), false);
                return;
            }
            if (lvstkBull) {
                if (!mInputValidation.isInputEditTextFilled(tietLivestkBull, tilLivestkBull, getString(R.string.error_message_blank_field))) {
                    return;
                }
            }
            if (lvstkCow) {
                if (!mInputValidation.isInputEditTextFilled(tietLivestkCow, tilLivestkCow, getString(R.string.error_message_blank_field))) {
                    return;
                }
            }
            if (lvstkDuck) {
                if (!mInputValidation.isInputEditTextFilled(tietLivestkDuck, tilLivestkDuck, getString(R.string.error_message_blank_field))) {
                    return;
                }
            }
            if (lvstkCalf) {
                if (!mInputValidation.isInputEditTextFilled(tietLivestkCalf, tilLivestkCalf, getString(R.string.error_message_blank_field))) {
                    return;
                }
            }
            if (lvstkCamel) {
                if (!mInputValidation.isInputEditTextFilled(tietLivestkCamel, tilLivestkCamel, getString(R.string.error_message_blank_field))) {
                    return;
                }
            }
            if (lvstkHorseMare) {
                if (!mInputValidation.isInputEditTextFilled(tietLivestkHorseMare, tilLivestkHorseMare, getString(R.string.error_message_blank_field))) {
                    return;
                }
            }
            if (lvstkGoat) {
                if (!mInputValidation.isInputEditTextFilled(tietLivestkGoat, tilLivestkGoat, getString(R.string.error_message_blank_field))) {
                    return;
                }
            }
            if (lvstkSheep) {
                if (!mInputValidation.isInputEditTextFilled(tietLivestkSheep, tilLivestkSheep, getString(R.string.error_message_blank_field))) {
                    return;
                }
            }
            if (lvstkPig) {
                if (!mInputValidation.isInputEditTextFilled(tietLivestkPig, tilLivestkPig, getString(R.string.error_message_blank_field))) {
                    return;
                }
            }
            if (lvstkHen) {
                if (!mInputValidation.isInputEditTextFilled(tietLivestkHen, tilLivestkHen, getString(R.string.error_message_blank_field))) {
                    return;
                }
            }
            if (lvstkRabbit) {
                if (!mInputValidation.isInputEditTextFilled(tietLivestkRabbit, tilLivestkRabbit, getString(R.string.error_message_blank_field))) {
                    return;
                }
            }
            if (lvstkOther) {
                if (!mInputValidation.isInputEditTextFilled(tietLivestkOther, tilLivestkOther, getString(R.string.error_message_blank_field))) {
                    return;
                }
            }
        }
        if (rabiCrops_yn) {
            if (!(rabiCashCrop || rabiCereals || rabiOilSeed || kharifPulses || rabiSpices || rabiVegi || rabiOther)) {
                mAlert.showAlertDialog(getActivity(), "Validation error", getResources().getString(R.string.err_msg_rabi_crp), false);
                return;
            }
            if (rabiCashCrop) {
                if (!mInputValidation.isMultiAutoCompleteTextView(mactRabicrpCashCrop, tilRabicrpCashCrop, getString(R.string.error_message_blank_field))) {
                    return;
                }
                if (!isCorrectCrop(mactRabicrpCashCrop, mactRabicrpCashCrop.getText().toString().trim())) {
                    tilRabicrpCashCrop.setError("Invalid Input");
                    return;
                }
            }
            if (rabiCereals) {
                if (!mInputValidation.isMultiAutoCompleteTextView(mactRabicrpCerealsGrains, tilRabicrpCerealsGrains, getString(R.string.error_message_blank_field))) {
                    return;
                }
                if (!isCorrectCrop(mactRabicrpCerealsGrains, mactRabicrpCerealsGrains.getText().toString().trim())) {
                    tilRabicrpCerealsGrains.setError("Invalid Input");
                    return;
                }
            }
            if (rabiOilSeed) {
                if (!mInputValidation.isMultiAutoCompleteTextView(mactRabicrpOilSeeds, tilRabicrpOilSeeds, getString(R.string.error_message_blank_field))) {
                    return;
                }
                if (!isCorrectCrop(mactRabicrpOilSeeds, mactRabicrpOilSeeds.getText().toString().trim())) {
                    tilRabicrpOilSeeds.setError("Invalid Input");
                    return;
                }
            }
            if (rabiPulses) {
                if (!mInputValidation.isMultiAutoCompleteTextView(mactRabicrpPulses, tilRabicrpPulses, getString(R.string.error_message_blank_field))) {
                    return;
                }
                if (!isCorrectCrop(mactRabicrpPulses, mactRabicrpPulses.getText().toString().trim())) {
                    tilRabicrpPulses.setError("Invalid Input");
                    return;
                }
            }
            if (rabiSpices) {
                if (!mInputValidation.isMultiAutoCompleteTextView(mactRabicrpSpices, tilRabicrpSpices, getString(R.string.error_message_blank_field))) {
                    return;
                }
                if (!isCorrectCrop(mactRabicrpSpices, mactRabicrpSpices.getText().toString().trim())) {
                    tilRabicrpSpices.setError("Invalid Input");
                    return;
                }
            }
            if (rabiVegi) {
                if (!mInputValidation.isMultiAutoCompleteTextView(mactRabicrpVegetables, tilRabicrpVegetables, getString(R.string.error_message_blank_field))) {
                    return;
                }
                if (!isCorrectCrop(mactRabicrpVegetables, mactRabicrpVegetables.getText().toString().trim())) {
                    tilRabicrpVegetables.setError("Invalid Input");
                    return;
                }
            }
            if (rabiOther) {
                if (!mInputValidation.isMultiAutoCompleteTextView(mactRabicrpOther, tilRabicrpOther, getString(R.string.error_message_blank_field))) {
                    return;
                }
            }
        }
        if (kharifCrops_yn) {
            if (!(kharifCashCrop || kharifCereals || kharifOilSeed || kharifPulses || kharifSpices || kharifVegi || kharifOther)) {
                mAlert.showAlertDialog(getActivity(), "Validation error", getResources().getString(R.string.err_msg_kharif_crp), false);
                return;
            }
            if (kharifCashCrop) {
                if (!mInputValidation.isMultiAutoCompleteTextView(mactKharifcrpCashCrop, tilKharifcrpCashCrop, getString(R.string.error_message_blank_field))) {
                    return;
                }
                if (!isCorrectCrop(mactKharifcrpCashCrop, mactKharifcrpCashCrop.getText().toString().trim())) {
                    tilKharifcrpCashCrop.setError("Invalid Input");
                    return;
                }
            }
            if (kharifCereals) {
                if (!mInputValidation.isMultiAutoCompleteTextView(mactKharifcrpCerealsGrains, tilKharifcrpCerealsGrains, getString(R.string.error_message_blank_field))) {
                    return;
                }
                if (!isCorrectCrop(mactKharifcrpCerealsGrains, mactKharifcrpCerealsGrains.getText().toString().trim())) {
                    tilKharifcrpCerealsGrains.setError("Invalid Input");
                    return;
                }
            }
            if (kharifOilSeed) {
                if (!mInputValidation.isMultiAutoCompleteTextView(mactKharifcrpOilSeeds, tilKharifcrpOilSeeds, getString(R.string.error_message_blank_field))) {
                    return;
                }
                if (!isCorrectCrop(mactKharifcrpOilSeeds, mactKharifcrpOilSeeds.getText().toString().trim())) {
                    tilKharifcrpOilSeeds.setError("Invalid Input");
                    return;
                }
            }
            if (kharifPulses) {
                if (!mInputValidation.isMultiAutoCompleteTextView(mactKharifcrpPulses, tilKharifcrpPulses, getString(R.string.error_message_blank_field))) {
                    return;
                }
                if (!isCorrectCrop(mactKharifcrpPulses, mactKharifcrpPulses.getText().toString().trim())) {
                    tilKharifcrpPulses.setError("Invalid Input");
                    return;
                }
            }
            if (kharifSpices) {
                if (!mInputValidation.isMultiAutoCompleteTextView(mactKharifcrpSpices, tilKharifcrpSpices, getString(R.string.error_message_blank_field))) {
                    return;
                }
                if (!isCorrectCrop(mactKharifcrpSpices, mactKharifcrpSpices.getText().toString().trim())) {
                    tilKharifcrpSpices.setError("Invalid Input");
                    return;
                }
            }
            if (kharifVegi) {
                if (!mInputValidation.isMultiAutoCompleteTextView(mactKharifcrpVegetables, tilKharifcrpVegetables, getString(R.string.error_message_blank_field))) {
                    return;
                }
                if (!isCorrectCrop(mactKharifcrpVegetables, mactKharifcrpVegetables.getText().toString().trim())) {
                    tilKharifcrpVegetables.setError("Invalid Input");
                    return;
                }
            }
            if (kharifOther) {
                if (!mInputValidation.isMultiAutoCompleteTextView(mactKharifOther, tilKharifOther, getString(R.string.error_message_blank_field))) {
                    return;
                }
            }
        }
        if (summerCrops_yn) {
            if (!(summerCashCrop || summerCereals || summerOilSeed || summerPulses || summerSpices || summerVegi || summerOther)) {
                mAlert.showAlertDialog(getActivity(), "Validation error", getResources().getString(R.string.err_msg_summer_crp), false);
                return;
            }
            if (summerCashCrop) {
                if (!mInputValidation.isMultiAutoCompleteTextView(mactSummercrpCashCrop, tilSummercrpCashCrop, getString(R.string.error_message_blank_field))) {
                    return;
                }
                if (!isCorrectCrop(mactSummercrpCashCrop, mactSummercrpCashCrop.getText().toString().trim())) {
                    tilSummercrpCashCrop.setError("Invalid Input");
                    return;
                }
            }
            if (summerCereals) {
                if (!mInputValidation.isMultiAutoCompleteTextView(mactSummercrpCerealsGrains, tilSummercrpCerealsGrains, getString(R.string.error_message_blank_field))) {
                    return;
                }
                if (!isCorrectCrop(mactSummercrpCerealsGrains, mactSummercrpCerealsGrains.getText().toString().trim())) {
                    tilSummercrpCerealsGrains.setError("Invalid Input");
                    return;
                }
            }
            if (summerOilSeed) {
                if (!mInputValidation.isMultiAutoCompleteTextView(mactSummercrpOilSeeds, tilSummercrpOilSeeds, getString(R.string.error_message_blank_field))) {
                    return;
                }
                if (!isCorrectCrop(mactSummercrpOilSeeds, mactSummercrpOilSeeds.getText().toString().trim())) {
                    tilSummercrpOilSeeds.setError("Invalid Input");
                    return;
                }
            }
            if (summerPulses) {
                if (!mInputValidation.isMultiAutoCompleteTextView(mactSummercrpPulses, tilSummercrpPulses, getString(R.string.error_message_blank_field))) {
                    return;
                }
                if (!isCorrectCrop(mactSummercrpPulses, mactSummercrpPulses.getText().toString().trim())) {
                    tilSummercrpPulses.setError("Invalid Input");
                    return;
                }
            }
            if (summerSpices) {
                if (!mInputValidation.isMultiAutoCompleteTextView(mactSummercrpSpices, tilSummercrpSpices, getString(R.string.error_message_blank_field))) {
                    return;
                }
                if (!isCorrectCrop(mactSummercrpSpices, mactSummercrpSpices.getText().toString().trim())) {
                    tilSummercrpSpices.setError("Invalid Input");
                    return;
                }
            }
            if (summerVegi) {
                if (!mInputValidation.isMultiAutoCompleteTextView(mactSummercrpVegetables, tilSummercrpVegetables, getString(R.string.error_message_blank_field))) {
                    return;
                }
                if (!isCorrectCrop(mactSummercrpVegetables, mactSummercrpVegetables.getText().toString().trim())) {
                    tilSummercrpVegetables.setError("Invalid Input");
                    return;
                }
            }
            if (summerOther) {
                if (!mInputValidation.isMultiAutoCompleteTextView(mactSummercrpOther, tilSummercrpOther, getString(R.string.error_message_blank_field))) {
                    return;
                }
            }
        }
        if (plantationCrops_yn) {
            if (!mInputValidation.isInputEditTextFilled(tietPlantationcrpArea, tilPlantationcrpArea, getString(R.string.error_message_blank_field))) {
                return;
            }
            if (!(Float.valueOf(tietPlantationcrpArea.getText().toString()) < (Float.valueOf(tietFarmland.getText().toString())))) {
                mAlert.showAlertDialog(getActivity(), "Validation area", getResources().getString(R.string.err_msg_plantation_crp_area), false);
            }
            if (!(plantationCrop || plantationcrpFruits || medicinalPlant || exoticPlant)) {
                mAlert.showAlertDialog(getActivity(), "Validation error", getResources().getString(R.string.err_msg_plantation_crp), false);
                return;
            }
            if (plantationCrop) {
                if (!mInputValidation.isMultiAutoCompleteTextView(mactPlantationCrop, tilPlantationCrop, getString(R.string.error_message_blank_field))) {
                    return;
                }
                if (!isCorrectCrop(mactPlantationCrop, mactPlantationCrop.getText().toString().trim())) {
                    tilPlantationCrop.setError("Invalid Input");
                    return;
                }
            }
            if (plantationcrpFruits) {
                if (!mInputValidation.isMultiAutoCompleteTextView(mactPlantationcrpFruits, tilPlantationcrpFruits, getString(R.string.error_message_blank_field))) {
                    return;
                }
                if (!isCorrectCrop(mactPlantationcrpFruits, mactPlantationcrpFruits.getText().toString().trim())) {
                    tilPlantationcrpFruits.setError("Invalid Input");
                    return;
                }
            }
            if (medicinalPlant) {
                if (!mInputValidation.isMultiAutoCompleteTextView(mactMedicinalPlants, tilMedicinalPlants, getString(R.string.error_message_blank_field))) {
                    return;
                }
                if (!isCorrectCrop(mactMedicinalPlants, mactMedicinalPlants.getText().toString().trim())) {
                    tilMedicinalPlants.setError("Invalid Input");
                    return;
                }
            }
            if (exoticPlant) {
                if (!mInputValidation.isMultiAutoCompleteTextView(mactExoticPlants, tilExoticPlants, getString(R.string.error_message_blank_field))) {
                    return;
                }
                if (!isCorrectCrop(mactExoticPlants, mactExoticPlants.getText().toString().trim())) {
                    tilExoticPlants.setError("Invalid Input");
                    return;
                }
            }
        }
        if (isLevelDone)
            UpdateData();
        else
            AddData();
    }

    private void InitData() {
        String url = "";
        if (userType.equals("farmer"))
            url = EndPoints.URL_FARMER_GET_LEVEL_THREE;
        else if (userType.equals("officer")) {
            switch (intUserType) {
                case 1:// FIE
                    url = EndPoints.URL_EMP_GET_LEVEL_THREE + fId;
                    break;
                case 2:
                    url = EndPoints.URL_AGENT_GET_LEVEL_THREE + fId;
                    break;
                case 3:
                    url = EndPoints.URL_FPO_GET_LEVEL_THREE + fId;
                    break;
                case 4:
                    url = EndPoints.URL_BANK_REP_GET_LEVEL_THREE + fId;
                    break;
                default:
                    Toast.makeText(getActivity(), "Invalid userid!", Toast.LENGTH_SHORT).show();
                    break;
            }
        } else {
            return;
        }
        if (url.isEmpty())
            return;
        String tag_json_obj = "json_obj_req";

        final Snackbar snackbar = Snackbar
                .make(coordinatorLayout, "Please wait..", Snackbar.LENGTH_INDEFINITE);

        snackbar.show();

        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            snackbar.dismiss();
                            cvBtnSaveAndPrint.setEnabled(true);
                            // Parsing json object response
                            JSONObject data = response.getJSONObject("data");
                            if (data != null) {
                                isLevelDone = data.getBoolean("done_level");
                                if (isLevelDone) {
                                    if (data.getString("farmer_land_holding_yn").equals("true")) {
                                        RadioButton child = (RadioButton) radGroupQLandHolding.getChildAt(0);
                                        child.setChecked(true);
                                        landHolding_yn = true;
                                        llLandHolding.setVisibility(View.VISIBLE);

                                        tietFarmland.setText(data.getString("farmer_land_holding_area"));
                                        spinner_land_area.setSelection(getIndex(spinner_land_area, data.getString("farmer_land_area_unit")));
                                    }
                                    if (data.getString("farmer_irrigation_facility_yn").equals("true")) {
                                        RadioButton child = (RadioButton) radGroupQIrrgFslty.getChildAt(0);
                                        child.setChecked(true);
                                        irrigationFacility_yn = true;
                                        llIrrgFslty.setVisibility(View.VISIBLE);

                                        String irrigationDetails = data.getString("farmer_irrigation_facility_details");
                                        String[] componentsID = irrigationDetails.split(",");

                                        if (componentsID[0].equals("true")) {
                                            cbIrrDripIrr.setChecked(true);
                                            irrDripIrrigation = true;
                                        }
                                        if (componentsID[1].equals("true")) {
                                            cbIrrHydrophonics.setChecked(true);
                                            irrHydrophonics = true;
                                        }
                                        if (componentsID[2].equals("true")) {
                                            cbIrrRainfed.setChecked(true);
                                            irrRainfed = true;
                                        }

                                        if (componentsID[3].equals("true")) {
                                            cbIrrRainWtrHrvPond.setChecked(true);
                                            irrRainWtrHrvPond = true;
                                        }
                                        if (componentsID[4].equals("true")) {
                                            cbIrrRiverCanal.setChecked(true);
                                            irrRiverOrCanal = true;
                                        }

                                        if (componentsID[5].equals("true")) {
                                            cbIrrSprinklers.setChecked(true);
                                            irrSprinklers = true;
                                        }

                                        if (componentsID[6].equals("true")) {
                                            cbIrrSolarWaterPump.setChecked(true);
                                            irrSolarWaterPump = true;
                                        }

                                        if (componentsID[7].equals("true")) {
                                            cbIrrWaterPump.setChecked(true);
                                            irrWaterPump = true;
                                        }

                                        if (componentsID[8].equals("true")) {
                                            cbIrrOther.setChecked(true);
                                            irrOther = true;
                                        }
                                    }
                                    if (data.getString("farmer_farm_equipments_yn").equals("true")) {
                                        RadioButton child = (RadioButton) radGroupQFrmFrmEqp.getChildAt(0);
                                        child.setChecked(true);
                                        farmEquipments_yn = true;
                                        llFarmEquip.setVisibility(View.VISIBLE);

                                        String farmingEquipDetails = data.getString("farmer_farm_equipments_details");
                                        String[] componentsFED = farmingEquipDetails.split(",");

                                        if (componentsFED[0].equals("true")) {
                                            cbFarmEquipHarvester.setChecked(true);
                                            farmEquipHarvester = true;
                                        }

                                        if (componentsFED[1].equals("true")) {
                                            cbFarmEquipPloughCattleDriven.setChecked(true);
                                            farmEquipPloughCattleDriven = true;
                                        }

                                        if (componentsFED[2].equals("true")) {
                                            cbFarmEquipPowerTiller.setChecked(true);
                                            farmEquipTiller = true;
                                        }

                                        if (componentsFED[3].equals("true")) {
                                            cbFarmEquipSeeder.setChecked(true);
                                            farmEquipSeeder = true;
                                        }

                                        if (componentsFED[4].equals("true")) {
                                            cbFarmQuipTractor.setChecked(true);
                                            farmEquipTractor = true;
                                        }

                                        if (componentsFED[5].equals("true")) {
                                            cbFarmEquipWeeder.setChecked(true);
                                            farmEquipWeeder = true;
                                        }

                                        if (componentsFED[6].equals("true")) {
                                            cbFarmEquipOther.setChecked(true);
                                            farmEquipOther = true;
                                        }
                                    }
                                    if (data.getString("farmer_farm_storage_facilities_yn").equals("true")) {
                                        RadioButton child = (RadioButton) radGroupQFrmStrg.getChildAt(0);
                                        child.setChecked(true);
                                        storageFacility_yn = true;
                                    }
                                    if (data.getString("farmer_post_harvest_facilities_yn").equals("true")) {
                                        RadioButton child = (RadioButton) radioGroupQPostHrvst.getChildAt(0);
                                        child.setChecked(true);
                                        postHarvest_yn = true;
                                    }
                                    if (data.getString("farmer_livestock_yn").equals("true")) {
                                        RadioButton child = (RadioButton) radGroupQLivestk.getChildAt(0);
                                        child.setChecked(true);
                                        livestock_yn = true;
                                        llLivestk.setVisibility(View.VISIBLE);

                                        String liveStkDetails = data.getString("farmer_livestock_details");
                                        String[] componentLSD = liveStkDetails.split(",");

                                        if (!componentLSD[0].equals("-")) {
                                            tietLivestkBull.setEnabled(true);
                                            tietLivestkBull.setText(componentLSD[0]);
                                            cbLivestkBull.setChecked(true);
                                            lvstkBull = true;
                                        }

                                        if (!componentLSD[1].equals("-")) {
                                            tietLivestkCalf.setEnabled(true);
                                            tietLivestkCalf.setText(componentLSD[1]);
                                            cbLivestkCalf.setChecked(true);
                                            lvstkCalf = true;
                                        }

                                        if (!componentLSD[2].equals("-")) {
                                            tietLivestkCamel.setEnabled(true);
                                            tietLivestkCamel.setText(componentLSD[2]);
                                            cbLivestkCamel.setChecked(true);
                                            lvstkCamel = true;
                                        }

                                        if (!componentLSD[3].equals("-")) {
                                            tietLivestkCow.setEnabled(true);
                                            tietLivestkCow.setText(componentLSD[3]);
                                            cbLivestkCow.setChecked(true);
                                            lvstkCow = true;
                                        }

                                        if (!componentLSD[4].equals("-")) {
                                            tietLivestkDuck.setEnabled(true);
                                            tietLivestkDuck.setText(componentLSD[4]);
                                            cbLivestkDuck.setChecked(true);
                                            lvstkDuck = true;
                                        }
                                        if (!componentLSD[5].equals("-")) {
                                            tietLivestkGoat.setEnabled(true);
                                            tietLivestkGoat.setText(componentLSD[5]);
                                            cbLivestkGoat.setChecked(true);
                                            lvstkGoat = true;
                                        }

                                        if (!componentLSD[6].equals("-")) {
                                            tietLivestkHen.setEnabled(true);
                                            tietLivestkHen.setText(componentLSD[6]);
                                            cbLivestkHen.setChecked(true);
                                            lvstkHen = true;
                                        }

                                        if (!componentLSD[7].equals("-")) {
                                            tietLivestkHorseMare.setEnabled(true);
                                            tietLivestkHorseMare.setText(componentLSD[7]);
                                            cbLivestkHorseMare.setChecked(true);
                                            lvstkHorseMare = true;
                                        }

                                        if (!componentLSD[8].equals("-")) {
                                            tietLivestkPig.setEnabled(true);
                                            tietLivestkPig.setText(componentLSD[8]);
                                            cbLivestkPig.setChecked(true);
                                            lvstkPig = true;
                                        }

                                        if (!componentLSD[9].equals("-")) {
                                            tietLivestkRabbit.setEnabled(true);
                                            tietLivestkRabbit.setText(componentLSD[9]);
                                            cbLivestkRabbit.setChecked(true);
                                            lvstkRabbit = true;
                                        }

                                        if (!componentLSD[10].equals("-")) {
                                            tietLivestkSheep.setEnabled(true);
                                            tietLivestkSheep.setText(componentLSD[10]);
                                            cbLivestkSheep.setChecked(true);
                                            lvstkSheep = true;
                                        }
                                        if (!componentLSD[11].equals("-")) {
                                            tietLivestkOther.setEnabled(true);
                                            tietLivestkOther.setText(componentLSD[11]);
                                            cbLivestkOther.setChecked(true);
                                            lvstkOther = true;
                                        }
                                    }
                                    if (data.getString("farmer_rabi_crops_yn").equals("true")) {
                                        RadioButton child = (RadioButton) radGroupQRabicrp.getChildAt(0);
                                        child.setChecked(true);
                                        rabiCrops_yn = true;
                                        llRabiCrp.setVisibility(View.VISIBLE);

                                        String farmingEquipDetails = data.getString("farmer_rabi_crops_details");
                                        String[] componentRCD = farmingEquipDetails.split(";");
                                        if (!componentRCD[0].equals("-")) {
                                            mactRabicrpCashCrop.setEnabled(true);
                                            mactRabicrpCashCrop.setText(componentRCD[0]);
                                            cbRabicrpCashCrop.setChecked(true);
                                            rabiCashCrop = true;
                                        }
                                        if (!componentRCD[1].equals("-")) {
                                            mactRabicrpCerealsGrains.setEnabled(true);
                                            mactRabicrpCerealsGrains.setText(componentRCD[1]);
                                            cbRabicrpCerealsGrains.setChecked(true);
                                            rabiCereals = true;
                                        }
                                        if (!componentRCD[2].equals("-")) {
                                            mactRabicrpOilSeeds.setEnabled(true);
                                            mactRabicrpOilSeeds.setText(componentRCD[2]);
                                            cbRabicrpOilSeeds.setChecked(true);
                                            rabiOilSeed = true;
                                        }
                                        if (!componentRCD[3].equals("-")) {
                                            mactRabicrpPulses.setEnabled(true);
                                            mactRabicrpPulses.setText(componentRCD[3]);
                                            cbRabicrpPulses.setChecked(true);
                                            rabiPulses = true;
                                        }
                                        if (!componentRCD[4].equals("-")) {
                                            mactRabicrpSpices.setEnabled(true);
                                            mactRabicrpSpices.setText(componentRCD[4]);
                                            cbRabicrpSpices.setChecked(true);
                                            rabiSpices = true;
                                        }
                                        if (!componentRCD[5].equals("-")) {
                                            mactRabicrpVegetables.setEnabled(true);
                                            mactRabicrpVegetables.setText(componentRCD[5]);
                                            cbRabicrpVegetables.setChecked(true);
                                            rabiVegi = true;
                                        }
                                        if (!componentRCD[6].equals("-")) {
                                            mactRabicrpOther.setEnabled(true);
                                            mactRabicrpOther.setText(componentRCD[6]);
                                            cbRabicrpOther.setChecked(true);
                                            rabiOther = true;
                                        }
                                    }
                                    if (data.getString("farmer_kharif_crops_yn").equals("true")) {
                                        RadioButton child = (RadioButton) radGroupQKharifcrp.getChildAt(0);
                                        child.setChecked(true);
                                        kharifCrops_yn = true;
                                        llKharifCrp.setVisibility(View.VISIBLE);

                                        String farmingEquipDetails = data.getString("farmer_kharif_crops_details");
                                        String[] componentKCD = farmingEquipDetails.split(";");
                                        if (!componentKCD[0].equals("-")) {
                                            mactKharifcrpCashCrop.setEnabled(true);
                                            mactKharifcrpCashCrop.setText(componentKCD[0]);
                                            cbKharifcrpCashCrop.setChecked(true);
                                            kharifCashCrop = true;
                                        }
                                        if (!componentKCD[1].equals("-")) {
                                            mactKharifcrpCerealsGrains.setEnabled(true);
                                            mactKharifcrpCerealsGrains.setText(componentKCD[1]);
                                            cbKharifcrpCerealsGrains.setChecked(true);
                                            kharifCereals = true;
                                        }
                                        if (!componentKCD[2].equals("-")) {
                                            mactKharifcrpOilSeeds.setEnabled(true);
                                            mactKharifcrpOilSeeds.setText(componentKCD[2]);
                                            cbKharifcrpOilSeeds.setChecked(true);
                                            kharifOilSeed = true;
                                        }
                                        if (!componentKCD[3].equals("-")) {
                                            mactKharifcrpPulses.setEnabled(true);
                                            mactKharifcrpPulses.setText(componentKCD[3]);
                                            cbKharifcrpPulses.setChecked(true);
                                            kharifPulses = true;
                                        }
                                        if (!componentKCD[4].equals("-")) {
                                            mactKharifcrpSpices.setEnabled(true);
                                            mactKharifcrpSpices.setText(componentKCD[4]);
                                            cbKharifcrpSpices.setChecked(true);
                                            kharifSpices = true;
                                        }
                                        if (!componentKCD[5].equals("-")) {
                                            mactKharifcrpVegetables.setEnabled(true);
                                            mactKharifcrpVegetables.setText(componentKCD[5]);
                                            cbKharifcrpVegetables.setChecked(true);
                                            kharifVegi = true;
                                        }
                                        if (!componentKCD[6].equals("-")) {
                                            mactKharifOther.setEnabled(true);
                                            mactKharifOther.setText(componentKCD[6]);
                                            cbKharifcrpOther.setChecked(true);
                                            kharifOther = true;
                                        }
                                    }
                                    if (data.getString("farmer_summer_crops_yn").equals("true")) {
                                        RadioButton child = (RadioButton) radGroupQSummercrp.getChildAt(0);
                                        child.setChecked(true);
                                        summerCrops_yn = true;
                                        llSummerCrp.setVisibility(View.VISIBLE);

                                        String summerCrpDetails = data.getString("farmer_summer_crops_details");
                                        String[] componentSCD = summerCrpDetails.split(";");
                                        if (!componentSCD[0].equals("-")) {
                                            mactSummercrpCashCrop.setEnabled(true);
                                            mactSummercrpCashCrop.setText(componentSCD[0]);
                                            cbSummercrpCashCrop.setChecked(true);
                                            summerCashCrop = true;
                                        }
                                        if (!componentSCD[1].equals("-")) {
                                            mactSummercrpCerealsGrains.setEnabled(true);
                                            mactSummercrpCerealsGrains.setText(componentSCD[1]);
                                            cbSummercrpCerealsGrains.setChecked(true);
                                            summerCereals = true;
                                        }
                                        if (!componentSCD[2].equals("-")) {
                                            mactSummercrpOilSeeds.setEnabled(true);
                                            mactSummercrpOilSeeds.setText(componentSCD[2]);
                                            cbSummercrpOilSeeds.setChecked(true);
                                            summerOilSeed = true;
                                        }
                                        if (!componentSCD[3].equals("-")) {
                                            mactSummercrpPulses.setEnabled(true);
                                            mactSummercrpPulses.setText(componentSCD[3]);
                                            cbSummercrpPulses.setChecked(true);
                                            summerPulses = true;
                                        }
                                        if (!componentSCD[4].equals("-")) {
                                            mactSummercrpSpices.setEnabled(true);
                                            mactSummercrpSpices.setText(componentSCD[4]);
                                            cbSummercrpSpices.setChecked(true);
                                            summerSpices = true;
                                        }
                                        if (!componentSCD[5].equals("-")) {
                                            mactSummercrpVegetables.setEnabled(true);
                                            mactSummercrpVegetables.setText(componentSCD[5]);
                                            cbSummercrpVegetables.setChecked(true);
                                            summerVegi = true;
                                        }
                                        if (!componentSCD[6].equals("-")) {
                                            mactSummercrpOther.setEnabled(true);
                                            mactSummercrpOther.setText(componentSCD[6]);
                                            cbSummercrpOther.setChecked(true);
                                            summerOther = true;
                                        }
                                    }
                                    if (data.getString("farmer_plantation_crops_yn").equals("true")) {
                                        RadioButton child = (RadioButton) radGroupQPlantsnCrp.getChildAt(0);
                                        child.setChecked(true);
                                        plantationCrops_yn = true;
                                        llPlantsnCrp.setVisibility(View.VISIBLE);

                                        String plantationCrpDetails = data.getString("farmer_plantation_crops_details");
                                        String[] componentsPCD = plantationCrpDetails.split(";");

                                        if (!componentsPCD[0].equals("-")) {
                                            tietPlantationcrpArea.setText(componentsPCD[0]);
                                        }
                                        if (!componentsPCD[1].equals("-")) {
                                            mactPlantationCrop.setEnabled(true);
                                            mactPlantationCrop.setText(componentsPCD[1]);
                                            cbPlantationCrop.setChecked(true);
                                            plantationCrop = true;
                                        }
                                        if (!componentsPCD[2].equals("-")) {
                                            mactPlantationcrpFruits.setEnabled(true);
                                            mactPlantationcrpFruits.setText(componentsPCD[2]);
                                            cbPlantationcrpFruits.setChecked(true);
                                            plantationcrpFruits = true;
                                        }
                                        if (!componentsPCD[3].equals("-")) {
                                            mactMedicinalPlants.setEnabled(true);
                                            mactMedicinalPlants.setText(componentsPCD[3]);
                                            cbMedicinalPlant.setChecked(true);
                                            medicinalPlant = true;
                                        }
                                        if (!componentsPCD[4].equals("-")) {
                                            mactExoticPlants.setEnabled(true);
                                            mactExoticPlants.setText(componentsPCD[4]);
                                            cbExoticPlant.setChecked(true);
                                            exoticPlant = true;
                                        }
                                    }

                                    String fertilizerDetails = data.getString("farmer_fertilizer_details");
                                    String[] componentsFD = fertilizerDetails.split(",");
                                    etChemicalFertUsed.setText(componentsFD[0].replace("-", ""));
                                    etOrganigFertUsed.setText(componentsFD[1].replace("-", ""));
                                    etPesticideUsed.setText(componentsFD[2].replace("-", ""));
                                }
                            }

                        } catch (JSONException e) {
                            snackbar.dismiss();
                            //cvBtnSaveAndPrint.setEnabled(false);
                            //Toast.makeText(getActivity(), "App error!", Toast.LENGTH_SHORT).show();
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                snackbar.dismiss();
                cvBtnSaveAndPrint.setEnabled(true);
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
                            if (mode.equals("edit"))
                                errorMessage = "C404";
                            else
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

    private void AddData() {
        String url = "";
        if (userType.equals("farmer")) {
            url = EndPoints.URL_FARMER_ADD_FORM_THREE;
        } else if (userType.equals("officer")) {
            switch (intUserType) {
                case 1:// FIE
                    url = EndPoints.URL_EMP_ADD_FORM_THREE;
                    break;
                case 2:
                    url = EndPoints.URL_AGENT_ADD_FORM_THREE;
                    break;
                case 3:
                    url = EndPoints.URL_FPO_ADD_FORM_THREE;
                    break;
                case 4:
                    url = EndPoints.URL_BANK_REP_ADD_FORM_THREE;
                    break;
                default:
                    Toast.makeText(getActivity(), "Invalid userid!", Toast.LENGTH_SHORT).show();
                    break;
            }
        } else {
            return;
        }
        if (url.isEmpty())
            return;

        // Tag used to cancel the request
        String tag_json_obj = "json_obj_req";
        final String landHoldingDetails = tietFarmland.getText().toString().trim().trim();
        String landAreaUnit = spinner_land_area.getSelectedItem().toString().trim();

        final String irrigationFacilityDetails = cbIrrDripIrr.isChecked() + ","
                + cbIrrHydrophonics.isChecked() + ","
                + cbIrrRainfed.isChecked() + ","
                + cbIrrRainWtrHrvPond.isChecked() + ","
                + cbIrrRiverCanal.isChecked() + ","
                + cbIrrSprinklers.isChecked() + ","
                + cbIrrSolarWaterPump.isChecked() + ","
                + cbIrrWaterPump.isChecked() + ","
                + cbIrrOther.isChecked();

        final String farmEquipmentsDetails = cbFarmEquipHarvester.isChecked() + ","
                + cbFarmEquipPloughCattleDriven.isChecked() + ","
                + cbFarmEquipPowerTiller.isChecked() + ","
                + cbFarmEquipSeeder.isChecked() + ","
                + cbFarmQuipTractor.isChecked() + ","
                + cbFarmEquipWeeder.isChecked() + ","
                + cbFarmEquipOther.isChecked();

        final String livestockDetails = tietLivestkBull.getText().toString().trim() + ","
                + tietLivestkCalf.getText().toString().trim() + ","
                + tietLivestkCamel.getText().toString().trim() + ","
                + tietLivestkCow.getText().toString().trim() + ","
                + tietLivestkDuck.getText().toString().trim() + ","
                + tietLivestkGoat.getText().toString().trim() + ","
                + tietLivestkHen.getText().toString().trim() + ","
                + tietLivestkHorseMare.getText().toString().trim() + ","
                + tietLivestkPig.getText().toString().trim() + ","
                + tietLivestkRabbit.getText().toString().trim() + ","
                + tietLivestkSheep.getText().toString().trim() + ","
                + tietLivestkOther.getText().toString().trim();

        final String rabiCropsDetails = mactRabicrpCashCrop.getText().toString().trim() + ";"
                + mactRabicrpCerealsGrains.getText().toString().trim() + ";"
                + mactRabicrpOilSeeds.getText().toString().trim() + ";"
                + mactRabicrpPulses.getText().toString().trim() + ";"
                + mactRabicrpSpices.getText().toString().trim() + ";"
                + mactRabicrpVegetables.getText().toString().trim() + ";"
                + mactRabicrpOther.getText().toString().trim();

        final String kharifCropsDetails = mactKharifcrpCashCrop.getText().toString().trim() + ";"
                + mactKharifcrpCerealsGrains.getText().toString().trim() + ";"
                + mactKharifcrpOilSeeds.getText().toString().trim() + ";"
                + mactKharifcrpPulses.getText().toString().trim() + ";"
                + mactKharifcrpSpices.getText().toString().trim() + ";"
                + mactKharifcrpVegetables.getText().toString().trim() + ";"
                + mactKharifOther.getText().toString().trim();

        final String summerCropsDetails = mactSummercrpCashCrop.getText().toString().trim() + ";"
                + mactSummercrpCerealsGrains.getText().toString().trim() + ";"
                + mactSummercrpOilSeeds.getText().toString().trim() + ";"
                + mactSummercrpPulses.getText().toString().trim() + ";"
                + mactSummercrpSpices.getText().toString().trim() + ";"
                + mactSummercrpVegetables.getText().toString().trim() + ";"
                + mactSummercrpOther.getText().toString().trim();

        final String plantationCropsDetails = tietPlantationcrpArea.getText().toString().trim() + ";"
                + mactPlantationCrop.getText().toString().trim() + ";"
                + mactPlantationcrpFruits.getText().toString().trim() + ";"
                + mactMedicinalPlants.getText().toString().trim() + ";"
                + mactExoticPlants.getText().toString().trim();

        String cheFertUsed = etChemicalFertUsed.getText().toString().trim();
        String organicFertUsed = etOrganigFertUsed.getText().toString().trim();
        String pesticideUsed = etPesticideUsed.getText().toString().trim();

        if (cheFertUsed.isEmpty())
            cheFertUsed = cheFertUsed + "-";
        if (organicFertUsed.isEmpty())
            organicFertUsed = organicFertUsed + "-";
        if (pesticideUsed.isEmpty())
            pesticideUsed = pesticideUsed + "-";

        JSONObject jsonBody = new JSONObject();

        try {
            jsonBody.put("user_id", fId);
            jsonBody.put("farmer_land_holding_yn", landHolding_yn);
            jsonBody.put("farmer_land_holding_area", landHoldingDetails);
            jsonBody.put("farmer_land_area_unit", landAreaUnit);
            jsonBody.put("farmer_irrigation_facility_yn", irrigationFacility_yn);
            jsonBody.put("farmer_irrigation_facility_details", irrigationFacilityDetails);
            jsonBody.put("farmer_farm_equipments_yn", farmEquipments_yn);
            jsonBody.put("farmer_farm_equipments_details", farmEquipmentsDetails);
            jsonBody.put("farmer_farm_storage_facilities_yn", storageFacility_yn);
            jsonBody.put("farmer_post_harvest_facilities_yn", postHarvest_yn);
            jsonBody.put("farmer_livestock_yn", livestock_yn);
            jsonBody.put("farmer_livestock_details", livestockDetails);
            jsonBody.put("farmer_rabi_crops_yn", rabiCrops_yn);
            jsonBody.put("farmer_rabi_crops_details", rabiCropsDetails);
            jsonBody.put("farmer_kharif_crops_yn", kharifCrops_yn);
            jsonBody.put("farmer_kharif_crops_details", kharifCropsDetails);
            jsonBody.put("farmer_summer_crops_yn", summerCrops_yn);
            jsonBody.put("farmer_summer_crops_details", summerCropsDetails);
            jsonBody.put("farmer_plantation_crops_yn", plantationCrops_yn);
            jsonBody.put("farmer_plantation_crops_details", plantationCropsDetails);
            jsonBody.put("farmer_fertilizer_details", cheFertUsed + "," + organicFertUsed + "," + pesticideUsed);
            jsonBody.put("done_level", true);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        final Snackbar snackbar = Snackbar
                .make(coordinatorLayout, "Saving..", Snackbar.LENGTH_INDEFINITE);

        snackbar.show();

        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST, url, jsonBody,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            // Parsing json object response
                            boolean isSuccess = response.getBoolean("success");
                            String message = response.getString("msg");
                            if (isSuccess) {
                                snackbar.dismiss();
                                FarmAuthentication nextFrag = new FarmAuthentication();
                                // Pass mode
                                Bundle args = new Bundle();
                                //args.putString("MODE", "add");
                                args.putString("FID", fId);
                                nextFrag.setArguments(args);
                                FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                                transaction.replace(R.id.fragment_container, nextFrag, "EditLevelThreeFragment");
                                transaction.addToBackStack(null);
                                transaction.commit();
                                Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
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
                headers.put("Content-Type", "application/json");
                return headers;
            }
        };

        jsonObjReq.setRetryPolicy(new DefaultRetryPolicy(20 * 1000, 0,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(jsonObjReq, tag_json_obj);
    }

    private void UpdateData() {
        String url = "";
        if (userType.equals("farmer"))
            url = EndPoints.URL_FARMER_UPDATE_FORM_THREE;
        else if (userType.equals("officer")) {
            switch (intUserType) {
                case 1:// FIE
                    url = EndPoints.URL_EMP_UPDATE_FORM_THREE + fId;
                    break;
                case 2://FIFA
                    url = EndPoints.URL_AGENT_UPDATE_FORM_THREE + fId;
                    break;
                case 3:
                    url = EndPoints.URL_FPO_UPDATE_FORM_THREE + fId;
                    break;
                case 4:
                    url = EndPoints.URL_BANK_REP_UPDATE_FORM_THREE + fId;
                    break;
                default:
                    Toast.makeText(getActivity(), "Invalid userid!", Toast.LENGTH_SHORT).show();
                    break;
            }
        } else {
            return;
        }
        if (url.isEmpty())
            return;
        // Tag used to cancel the request
        String tag_json_obj = "json_obj_req";

        final String landHoldingDetails = tietFarmland.getText().toString().trim().trim();
        String landAreaUnit = spinner_land_area.getSelectedItem().toString().trim();

        final String irrigationFacilityDetails = cbIrrDripIrr.isChecked() + ","
                + cbIrrHydrophonics.isChecked() + ","
                + cbIrrRainfed.isChecked() + ","
                + cbIrrRainWtrHrvPond.isChecked() + ","
                + cbIrrRiverCanal.isChecked() + ","
                + cbIrrSprinklers.isChecked() + ","
                + cbIrrSolarWaterPump.isChecked() + ","
                + cbIrrWaterPump.isChecked() + ","
                + cbIrrOther.isChecked();

        final String farmEquipmentsDetails = cbFarmEquipHarvester.isChecked() + ","
                + cbFarmEquipPloughCattleDriven.isChecked() + ","
                + cbFarmEquipPowerTiller.isChecked() + ","
                + cbFarmEquipSeeder.isChecked() + ","
                + cbFarmQuipTractor.isChecked() + ","
                + cbFarmEquipWeeder.isChecked() + ","
                + cbFarmEquipOther.isChecked();

        final String livestockDetails = tietLivestkBull.getText().toString().trim() + ","
                + tietLivestkCalf.getText().toString().trim() + ","
                + tietLivestkCamel.getText().toString().trim() + ","
                + tietLivestkCow.getText().toString().trim() + ","
                + tietLivestkDuck.getText().toString().trim() + ","
                + tietLivestkGoat.getText().toString().trim() + ","
                + tietLivestkHen.getText().toString().trim() + ","
                + tietLivestkHorseMare.getText().toString().trim() + ","
                + tietLivestkPig.getText().toString().trim() + ","
                + tietLivestkRabbit.getText().toString().trim() + ","
                + tietLivestkSheep.getText().toString().trim() + ","
                + tietLivestkOther.getText().toString().trim();

        final String rabiCropsDetails = mactRabicrpCashCrop.getText().toString().trim() + ";"
                + mactRabicrpCerealsGrains.getText().toString().trim() + ";"
                + mactRabicrpOilSeeds.getText().toString().trim() + ";"
                + mactRabicrpPulses.getText().toString().trim() + ";"
                + mactRabicrpSpices.getText().toString().trim() + ";"
                + mactRabicrpVegetables.getText().toString().trim() + ";"
                + mactRabicrpOther.getText().toString().trim();

        final String kharifCropsDetails = mactKharifcrpCashCrop.getText().toString().trim() + ";"
                + mactKharifcrpCerealsGrains.getText().toString().trim() + ";"
                + mactKharifcrpOilSeeds.getText().toString().trim() + ";"
                + mactKharifcrpPulses.getText().toString().trim() + ";"
                + mactKharifcrpSpices.getText().toString().trim() + ";"
                + mactKharifcrpVegetables.getText().toString().trim() + ";"
                + mactKharifOther.getText().toString().trim();

        final String summerCropsDetails = mactSummercrpCashCrop.getText().toString().trim() + ";"
                + mactSummercrpCerealsGrains.getText().toString().trim() + ";"
                + mactSummercrpOilSeeds.getText().toString().trim() + ";"
                + mactSummercrpPulses.getText().toString().trim() + ";"
                + mactSummercrpSpices.getText().toString().trim() + ";"
                + mactSummercrpVegetables.getText().toString().trim() + ";"
                + mactSummercrpOther.getText().toString().trim();

        final String plantationCropsDetails = tietPlantationcrpArea.getText().toString().trim() + ";"
                + mactPlantationCrop.getText().toString().trim() + ";"
                + mactPlantationcrpFruits.getText().toString().trim() + ";"
                + mactMedicinalPlants.getText().toString().trim() + ";"
                + mactExoticPlants.getText().toString().trim();

        String cheFertUsed = etChemicalFertUsed.getText().toString().trim();
        String organicFertUsed = etOrganigFertUsed.getText().toString().trim();
        String pesticideUsed = etPesticideUsed.getText().toString().trim();

        if (cheFertUsed.isEmpty())
            cheFertUsed = cheFertUsed + "-";
        if (organicFertUsed.isEmpty())
            organicFertUsed = organicFertUsed + "-";
        if (pesticideUsed.isEmpty())
            pesticideUsed = pesticideUsed + "-";

        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("farmer_land_holding_yn", landHolding_yn);
            jsonBody.put("farmer_land_holding_area", landHoldingDetails);
            jsonBody.put("farmer_land_area_unit", landAreaUnit);
            jsonBody.put("farmer_irrigation_facility_yn", irrigationFacility_yn);
            jsonBody.put("farmer_irrigation_facility_details", irrigationFacilityDetails);
            jsonBody.put("farmer_farm_equipments_yn", farmEquipments_yn);
            jsonBody.put("farmer_farm_equipments_details", farmEquipmentsDetails);
            jsonBody.put("farmer_farm_storage_facilities_yn", storageFacility_yn);
            jsonBody.put("farmer_post_harvest_facilities_yn", postHarvest_yn);
            jsonBody.put("farmer_livestock_yn", livestock_yn);
            jsonBody.put("farmer_livestock_details", livestockDetails);
            jsonBody.put("farmer_rabi_crops_yn", rabiCrops_yn);
            jsonBody.put("farmer_rabi_crops_details", rabiCropsDetails);
            jsonBody.put("farmer_kharif_crops_yn", kharifCrops_yn);
            jsonBody.put("farmer_kharif_crops_details", kharifCropsDetails);
            jsonBody.put("farmer_summer_crops_yn", summerCrops_yn);
            jsonBody.put("farmer_summer_crops_details", summerCropsDetails);
            jsonBody.put("farmer_plantation_crops_yn", plantationCrops_yn);
            jsonBody.put("farmer_plantation_crops_details", plantationCropsDetails);
            jsonBody.put("farmer_fertilizer_details", cheFertUsed + "," + organicFertUsed + "," + pesticideUsed);
        } catch (Exception e) {
            e.printStackTrace();
        }

        final Snackbar snackbar = Snackbar
                .make(coordinatorLayout, "Saving..", Snackbar.LENGTH_INDEFINITE);

        snackbar.show();

        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.PUT, url, jsonBody,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            // Parsing json object response
                            boolean isSuccess = response.getBoolean("success");
                            String message = response.getString("msg");

                            if (isSuccess) {
                                snackbar.dismiss();
                                FarmAuthentication editFinalSubmitFragment = new FarmAuthentication();
                                // Pass mode
                                Bundle args = new Bundle();
                                //args.putString("MODE", "add");
                                args.putString("FID", fId);
                                editFinalSubmitFragment.setArguments(args);
                                FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                                transaction.replace(R.id.fragment_container, editFinalSubmitFragment, "EditLevelThreeFragment");
                                transaction.addToBackStack(null);
                                transaction.commit();
                                Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
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

                if (error instanceof TimeoutError || error instanceof NoConnectionError) {
                    Toast.makeText(getActivity(), getString(R.string.error_network_timeout),
                            Toast.LENGTH_LONG).show();
                } else if (error instanceof AuthFailureError) {
                    //TODO
                } else if (error instanceof ServerError) {
                    try {
                        String responseBody = new String(error.networkResponse.data, StandardCharsets.UTF_8);
                        JSONObject data = new JSONObject(responseBody);
                        String errorMsg = data.getString("msg");
                        Toast.makeText(getActivity(), errorMsg, Toast.LENGTH_SHORT).show();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else if (error instanceof NetworkError) {
                    //TODO
                } else if (error instanceof ParseError) {
                    //TODO
                }
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

    public class DecimalDigitsInputFilter implements InputFilter {

        Pattern mPattern;

        private DecimalDigitsInputFilter(int digitsBeforeZero, int digitsAfterZero) {
            mPattern = Pattern.compile("[0-9]{0," + (digitsBeforeZero - 1) + "}+((\\.[0-9]{0," + (digitsAfterZero - 1) + "})?)||(\\.)?");
        }

        @Override
        public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {

            Matcher matcher = mPattern.matcher(dest);
            if (!matcher.matches())
                return "";
            return null;
        }

    }

    private boolean isCorrectCrop(View v, String currentString) {
        List<String> sample = Arrays.asList(currentString.trim().split("\\s*,\\s*"));
        Set<String> set = new HashSet<String>(sample);
        if (set.size() < sample.size()) {
            return false;
        }
        switch (v.getId()) {
            case R.id.mact_rabicrpCash_fragment_edit_level_three:
                if (!arr_cash_crop.containsAll(sample)) {
                    return false;
                }
                break;
            case R.id.mact_rabicrpGrains_fragment_edit_level_three:
                if (!arr_cereal_grains.containsAll(sample)) {
                    return false;
                }
                break;
            case R.id.mact_rabicrpOilSeeds_fragment_edit_level_three:
                if (!arr_oil_seeds.containsAll(sample)) {
                    return false;
                }
                break;
            case R.id.mact_rabicrpPulse_fragment_edit_level_three:
                if (!arr_pulses.containsAll(sample)) {
                    return false;
                }
                break;
            case R.id.mact_rabicrpSpices_fragment_edit_level_three:
                if (!arr_spices.containsAll(sample)) {
                    return false;
                }
                break;
            case R.id.mact_rabicrpVegi_fragment_edit_level_three:
                if (!arr_vegetables.containsAll(sample)) {
                    return false;
                }
                break;
            case R.id.mact_rabicrpOther_fragment_edit_level_three:
                if (!arr_cropOthers.containsAll(sample)) {
                    return false;
                }
                break;
            case R.id.mact_kharifcrpCash_fragment_edit_level_three:
                if (!arr_cash_crop.containsAll(sample)) {
                    return false;
                }
                break;
            case R.id.mact_kharifcrpGrains_fragment_edit_level_three:
                if (!arr_cereal_grains.containsAll(sample)) {
                    return false;
                }
                break;
            case R.id.mact_kharifcrpOilSeeds_fragment_edit_level_three:
                if (!arr_oil_seeds.containsAll(sample)) {
                    return false;
                }
                break;
            case R.id.mact_kharifcrpPulse_fragment_edit_level_three:
                if (!arr_pulses.containsAll(sample)) {
                    return false;
                }
                break;
            case R.id.mact_kharifcrpSpices_fragment_edit_level_three:
                if (!arr_spices.containsAll(sample)) {
                    return false;
                }
                break;
            case R.id.mact_kharifcrpVegi_fragment_edit_level_three:
                if (!arr_vegetables.containsAll(sample)) {
                    return false;
                }
                break;
            case R.id.mact_kharifcrpOther_fragment_edit_level_three:
                if (!arr_cropOthers.containsAll(sample)) {
                    return false;
                }
                break;
            case R.id.mact_summercrpCash_fragment_edit_level_three:
                if (!arr_cash_crop.containsAll(sample)) {
                    return false;
                }
                break;
            case R.id.mact_summercrpGrains_fragment_edit_level_three:
                if (!arr_cereal_grains.containsAll(sample)) {
                    return false;
                }
                break;
            case R.id.mact_summercrpOilSeeds_fragment_edit_level_three:
                if (!arr_oil_seeds.containsAll(sample)) {
                    return false;
                }
                break;
            case R.id.mact_summercrpPulse_fragment_edit_level_three:
                if (!arr_pulses.containsAll(sample)) {
                    return false;
                }
                break;
            case R.id.mact_summercrpSpices_fragment_edit_level_three:
                if (!arr_spices.containsAll(sample)) {
                    return false;
                }
                break;
            case R.id.mact_summercrpVegi_fragment_edit_level_three:
                if (!arr_vegetables.containsAll(sample)) {
                    return false;
                }
                break;
            case R.id.mact_summercrpOther_fragment_edit_level_three:
                if (!arr_cropOthers.containsAll(sample)) {
                    return false;
                }
                break;
            case R.id.mact_plantationCrop_fragment_edit_level_three:
                if (!arr_plantationCrop.containsAll(sample)) {
                    return false;
                }
                break;
            case R.id.mact_plantationFruit_fragment_edit_level_three:
                if (!arr_plantationFruits.containsAll(sample)) {
                    return false;
                }
                break;
            case R.id.mact_medicinalPlant_fragment_edit_level_three:
                if (!arr_medicinalPlant.containsAll(sample)) {
                    return false;
                }
                break;
            case R.id.mact_exoticCrop_fragment_edit_level_three:
                if (!arr_exoticCrop.containsAll(sample)) {
                    return false;
                }
                break;
        }

        return true;
    }

    private void resetLand() {
        tietFarmland.setText(null);
    }

    private void resetIrrigation() {
        cbIrrDripIrr.setChecked(false);
        cbIrrHydrophonics.setChecked(false);
        cbIrrRainfed.setChecked(false);
        cbIrrRainWtrHrvPond.setChecked(false);
        cbIrrRiverCanal.setChecked(false);
        cbIrrSprinklers.setChecked(false);
        cbIrrWaterPump.setChecked(false);
        cbIrrSolarWaterPump.setChecked(false);
        cbIrrOther.setChecked(false);
    }

    private void resetEquipments() {
        cbFarmEquipHarvester.setChecked(false);
        cbFarmEquipPloughCattleDriven.setChecked(false);
        cbFarmEquipPowerTiller.setChecked(false);
        cbFarmQuipTractor.setChecked(false);
        cbFarmEquipSeeder.setChecked(false);
        cbFarmEquipWeeder.setChecked(false);
        cbFarmEquipOther.setChecked(false);
    }

    private void resetLivestock() {
        cbLivestkBull.setChecked(false);
        cbLivestkCalf.setChecked(false);
        cbLivestkCamel.setChecked(false);
        cbLivestkCow.setChecked(false);
        cbLivestkDuck.setChecked(false);
        cbLivestkGoat.setChecked(false);
        cbLivestkHorseMare.setChecked(false);
        cbLivestkHen.setChecked(false);
        cbLivestkPig.setChecked(false);
        cbLivestkRabbit.setChecked(false);
        cbLivestkSheep.setChecked(false);
        cbLivestkOther.setChecked(false);
    }

    private void resetRabiCrop() {
        cbRabicrpCashCrop.setChecked(false);
        cbRabicrpCerealsGrains.setChecked(false);
        cbRabicrpOilSeeds.setChecked(false);
        cbRabicrpPulses.setChecked(false);
        cbRabicrpSpices.setChecked(false);
        cbRabicrpVegetables.setChecked(false);
        cbRabicrpOther.setChecked(false);

        mactRabicrpCashCrop.setText("-");
        mactRabicrpCerealsGrains.setText("-");
        mactRabicrpOilSeeds.setText("-");
        mactRabicrpPulses.setText("-");
        mactRabicrpSpices.setText("-");
        mactRabicrpVegetables.setText("-");
        mactRabicrpOther.setText("-");
    }

    private void resetKharifCrop() {
        cbKharifcrpCashCrop.setChecked(false);
        cbKharifcrpCerealsGrains.setChecked(false);
        cbKharifcrpOilSeeds.setChecked(false);
        cbKharifcrpPulses.setChecked(false);
        cbKharifcrpSpices.setChecked(false);
        cbKharifcrpVegetables.setChecked(false);
        cbKharifcrpOther.setChecked(false);

        mactKharifcrpCashCrop.setText("-");
        mactKharifcrpCerealsGrains.setText("-");
        mactKharifcrpOilSeeds.setText("-");
        mactKharifcrpPulses.setText("-");
        mactKharifcrpSpices.setText("-");
        mactKharifcrpVegetables.setText("-");
        mactKharifOther.setText("-");
    }

    private void resetSummerCrop() {
        cbSummercrpCashCrop.setChecked(false);
        cbSummercrpCerealsGrains.setChecked(false);
        cbSummercrpOilSeeds.setChecked(false);
        cbSummercrpPulses.setChecked(false);
        cbSummercrpSpices.setChecked(false);
        cbSummercrpVegetables.setChecked(false);
        cbSummercrpOther.setChecked(false);

        mactSummercrpCashCrop.setText("-");
        mactSummercrpCerealsGrains.setText("-");
        mactSummercrpOilSeeds.setText("-");
        mactSummercrpPulses.setText("-");
        mactSummercrpSpices.setText("-");
        mactSummercrpVegetables.setText("-");
        mactSummercrpOther.setText("-");
    }

    private void resetPlantation() {
        tietPlantationcrpArea.setText("-");
        mactPlantationCrop.setText("-");
        mactPlantationcrpFruits.setText("-");
        mactMedicinalPlants.setText("-");
        mactExoticPlants.setText("-");
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

    /* @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQ_CODE_MIC:
                if (resultCode == RESULT_OK && null != data) {
                    ArrayList<String> result = data
                            .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    Toast.makeText(getActivity(), result.get(0).toString(), Toast.LENGTH_SHORT).show();
                    mactKharifcrpCashCrop.setText((mactKharifcrpCashCrop.getText().append(result.get(0)).append(",")));
                    break;
                }
        }
    }*/

    @Override
    public void onBackPressed() {
        getActivity().getSupportFragmentManager().popBackStack();
    }
}
