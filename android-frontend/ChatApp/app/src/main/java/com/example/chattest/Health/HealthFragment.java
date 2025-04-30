package com.example.chattest.Health;

import android.content.Context;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.Toast;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.example.chattest.Health.type.HealthRequestType;
import com.example.chattest.Health.view.HealthView;
import com.example.chattest.SQLite.Health.Health_Data;
import com.example.chattest.SQLite.SQLiteService;
import com.example.chattest.Test.MyDebug;
import com.example.chattest.URL.UrlUtil;
import com.example.chattest.User.User;
import com.example.chattest.Utils.CallBackInterface;
import com.example.chattest.Utils.RequestUtils;
import com.example.chattest.Utils.Type.R_Util;
import com.example.chattest.Utils.Type.R_dataType;
import com.example.chattest.databinding.FragmentHealthBinding;
import com.example.chattest.databinding.PopUpHealthBinding;
import com.example.chattest.databinding.PopUpReminderBinding;


import java.util.Arrays;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HealthFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HealthFragment extends Fragment implements CallBackInterface {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public HealthFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment HealthFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static HealthFragment newInstance(String param1, String param2) {
        HealthFragment fragment = new HealthFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentHealthBinding.inflate(inflater,container,false);

        popUpHealthBinding = PopUpHealthBinding.inflate(inflater,container,false);
        popUpReminderBinding = PopUpReminderBinding.inflate(inflater,container,false);

        Init();

        SQLite_Init();

        setListener();

        Pop_up_Main();

        return binding.getRoot();
    }

    //-------------------------Init-------------------------

    private FragmentHealthBinding binding;

    private void Init(){
        binding.healthCard.setVisibility(View.VISIBLE);
    }

    //-------------------------setListener-------------------------

    private void setListener(){
        binding.healthData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Pop_up_health.show();
            }
        });
        binding.medicationReminder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Pop_up_reminder.show();
            }
        });
        binding.disease.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Health_Data data = SQLiteService.getInstance().getUserHealthData();
                if(data == null){
                    Toast.makeText(getContext(), "您还没有填写健康信息哦~", Toast.LENGTH_SHORT).show();
                }
                else {
                    Health_Array = SQLiteService.changeHealthData_to_Array(data);
                    Health_Request();
                }
            }
        });
    }

    //-------------------------Pop-up-------------------------

    private PopUpHealthBinding popUpHealthBinding;
    private PopUpReminderBinding popUpReminderBinding;
    private AlertDialog Pop_up_health;
    private AlertDialog Pop_up_reminder;
    private void Pop_up_Main(){
        Context context = this.getContext();

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        AlertDialog.Builder builder2 = new AlertDialog.Builder(context);
        builder.setView(popUpHealthBinding.getRoot())
                .setTitle("健康信息填写");
        Pop_up_health = builder.create();
        popUpHealthBinding.ButtonCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Pop_up_health.dismiss();
            }
        });

        popUpHealthBinding.ButtonSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean judge = Pop_up_health_select();
                if (judge){
                    Pop_up_health.dismiss();
                    Toast.makeText(context, "健康数据填入成功", Toast.LENGTH_SHORT).show();
                    Health_SQL();
                    //Health_Request();
                }
            }
        });

        builder2.setView(popUpReminderBinding.getRoot())
                .setTitle("用药提醒时间填写");
        Pop_up_reminder = builder2.create();
        popUpReminderBinding.ButtonCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Pop_up_reminder.dismiss();
            }
        });

        popUpReminderBinding.ButtonSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean judge = Pop_up_reminder_select();
                if(judge){
                    Pop_up_reminder.dismiss();
                    Toast.makeText(context, "设置提醒成功", Toast.LENGTH_SHORT).show();
                    int day = ChangeNum(popUpReminderBinding.dayEdit.getText().toString());
                    int hour = ChangeNum(popUpReminderBinding.hourEdit.getText().toString());
                    int min = ChangeNum(popUpReminderBinding.minEdit.getText().toString());
                    int second = ChangeNum(popUpReminderBinding.secondEdit.getText().toString());
                    String Name = popUpReminderBinding.nameEdit.getText().toString();
                    String reminder = popUpReminderBinding.reminderEdit.getText().toString();
                    ReminderService.remindData = new ReminderService.RemindData(day,hour,min,second,Name,reminder,System.currentTimeMillis());
                }
            }
        });

    }

    private int ChangeNum(String s){
        if(s.isEmpty()){
            return 0;
        }
        else {
            return Integer.parseInt(s);
        }
    }

    private void SetError(String judge, AutoCompleteTextView view, boolean[] error){
        if (judge.isEmpty()){
            view.setError("不能为空");
            error[0] = true;
        }
    }

    private void SetError(String judge, EditText editText, boolean[] error){
        if (judge.isEmpty()){
            editText.setError("不能为空");
            error[0] = true;
        }
    }

    private int judgeData(String text){
        if(text.equals("是") || text.equals("否")){
            if(text.equals("是")){
                return 1;
            }
            else {
                return 0;
            }
        }
        else {
            if(text.equals("男")){
                return 1;
            }
            else {
                return 0;
            }
        }
    }

    private boolean Pop_up_health_select(){
        String hypertension_Option = popUpHealthBinding.hypertensionOption.getText().toString();
        String High_cholesterol_Option = popUpHealthBinding.HighCholesterolOption.getText().toString();
        String BMI_edit = popUpHealthBinding.BMIEdit.getText().toString();
        String smoke_Option = popUpHealthBinding.smokeOption.getText().toString();
        String apoplexy_Option = popUpHealthBinding.apoplexyOption.getText().toString();
        String Physical_Option = popUpHealthBinding.PhysicalOption.getText().toString();
        String fruit_Option = popUpHealthBinding.fruitOption.getText().toString();
        String vegetable_Option = popUpHealthBinding.vegetableOption.getText().toString();
        String alcohol_Option = popUpHealthBinding.alcoholOption.getText().toString();
        String medical_care_Option = popUpHealthBinding.medicalCareOption.getText().toString();
        String No_medical_Option = popUpHealthBinding.NoMedicalOption.getText().toString();
        String Health_edit = popUpHealthBinding.HealthEdit.getText().toString();
        String Psychological_edit = popUpHealthBinding.PsychologicalEdit.getText().toString();
        String Physical_edit = popUpHealthBinding.PhysicalEdit.getText().toString();
        String Difficulty_walking_Option = popUpHealthBinding.DifficultyWalkingOption.getText().toString();
        String sex_Option = popUpHealthBinding.sexOption.getText().toString();
        String age_edit = popUpHealthBinding.ageEdit.getText().toString();
        String educational_level_edit = popUpHealthBinding.educationalLevelEdit.getText().toString();
        String income_edit = popUpHealthBinding.incomeEdit.getText().toString();

        boolean[] error = new boolean[1];

        SetError(hypertension_Option, popUpHealthBinding.hypertensionOption,error);
        SetError(High_cholesterol_Option, popUpHealthBinding.HighCholesterolOption,error);
        SetError(smoke_Option, popUpHealthBinding.smokeOption,error);
        SetError(apoplexy_Option, popUpHealthBinding.apoplexyOption,error);
        SetError(Physical_Option, popUpHealthBinding.PhysicalOption,error);
        SetError(fruit_Option, popUpHealthBinding.fruitOption,error);
        SetError(vegetable_Option, popUpHealthBinding.vegetableOption,error);
        SetError(alcohol_Option, popUpHealthBinding.alcoholOption,error);
        SetError(medical_care_Option, popUpHealthBinding.medicalCareOption,error);
        SetError(No_medical_Option, popUpHealthBinding.NoMedicalOption,error);
        SetError(Difficulty_walking_Option, popUpHealthBinding.DifficultyWalkingOption,error);
        SetError(sex_Option, popUpHealthBinding.sexOption,error);

        SetError(BMI_edit, popUpHealthBinding.BMIEdit,error);
        SetError(Health_edit, popUpHealthBinding.HealthEdit,error);
        SetError(Psychological_edit, popUpHealthBinding.PsychologicalEdit,error);
        SetError(Physical_edit, popUpHealthBinding.PhysicalEdit,error);
        SetError(age_edit, popUpHealthBinding.ageEdit,error);
        SetError(educational_level_edit, popUpHealthBinding.educationalLevelEdit,error);
        SetError(income_edit, popUpHealthBinding.incomeEdit,error);

        if (error[0]){
            return false;
        }

        Health_Array[0] = judgeData(hypertension_Option);
        Health_Array[1] = judgeData(High_cholesterol_Option);
        Health_Array[2] = Integer.parseInt(BMI_edit);
        Health_Array[3] = judgeData(smoke_Option);
        Health_Array[4] = judgeData(apoplexy_Option);
        Health_Array[5] = judgeData(Physical_Option);
        Health_Array[6] = judgeData(fruit_Option);
        Health_Array[7] = judgeData(vegetable_Option);
        Health_Array[8] = judgeData(alcohol_Option);
        Health_Array[9] = judgeData(medical_care_Option);
        Health_Array[10] = judgeData(No_medical_Option);
        Health_Array[11] = Integer.parseInt(Health_edit);
        if(Health_Array[11] > 5 || Health_Array[11] < 1){
            popUpHealthBinding.HealthEdit.setError("数据范围出错！");
            return false;
        }
        Health_Array[12] = Integer.parseInt(Psychological_edit);
        if(Health_Array[12] > 30 || Health_Array[12] < 0){
            popUpHealthBinding.HealthEdit.setError("数据范围出错！");
            return false;
        }
        Health_Array[13] = Integer.parseInt(Physical_edit);
        if(Health_Array[13] > 30 || Health_Array[13] < 0){
            popUpHealthBinding.HealthEdit.setError("数据范围出错！");
            return false;
        }
        Health_Array[14] = judgeData(Difficulty_walking_Option);
        Health_Array[15] = judgeData(sex_Option);
        Health_Array[16] = Integer.parseInt(age_edit);
        Health_Array[17] = Integer.parseInt(educational_level_edit);
        if(Health_Array[17] > 6 || Health_Array[17] < 1){
            popUpHealthBinding.HealthEdit.setError("数据范围出错！");
            return false;
        }
        Health_Array[18] = Integer.parseInt(income_edit);
        if(Health_Array[18] > 8 || Health_Array[18] < 1){
            popUpHealthBinding.HealthEdit.setError("数据范围出错！");
            return false;
        }
        return true;
    }
    private boolean Pop_up_reminder_select(){
        String Name = popUpReminderBinding.nameEdit.getText().toString();
        String Day = popUpReminderBinding.dayEdit.getText().toString();
        String Hour = popUpReminderBinding.hourEdit.getText().toString();
        String Min = popUpReminderBinding.minEdit.getText().toString();
        String Second = popUpReminderBinding.secondEdit.getText().toString();
        boolean[] error = new boolean[1];
        SetError(Name,popUpReminderBinding.nameEdit,error);
        if (error[0]){
            return false;
        }
        else if(Day.isEmpty() && Hour.isEmpty() && Min.isEmpty() && Second.isEmpty()){
            Toast.makeText(this.getContext(), "时间不能都为空！", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    //-------------------------Data-------------------------
    private int[] Health_Array = new int[19];//数据存储在本地的SQLite
    private double[] heart_weight_Array = new double[19];
    private double[] diabetes_weight_Array = new double[19];
    private double predict_heart;
    private double predict_diabetes;
    private String RequestClass_Health = "RequestClass_Health";
    private void Health_Request(){
        MyDebug.Print(Arrays.toString(Health_Array));
        HealthRequestType requestType = new HealthRequestType(Health_Array);
        R_dataType rDataType = new R_dataType(requestType);
        String jsonData = R_Util.R_JsonUtils.toJson(rDataType);
        RequestUtils utils = new RequestUtils(60 * 1000,RequestClass_Health,"POST", UrlUtil.Get_medical_predict_Url(),jsonData,this);
        utils.StartThread();
    }

    private void Health_SQL(){
        SQLiteService service = SQLiteService.getInstance();
        Integer[] integerArr = new Integer[Health_Array.length];
        for (int i = 0; i < Health_Array.length; i++) {
            integerArr[i] = Health_Array[i];
        }
        Health_Data data = new Health_Data(integerArr);
        if(User.Logged){
            data.user_id = User.user_id;
        }
        else {
            data.user_id = 0;
        }
        boolean success = service.Add_Health(data);
        MyDebug.Print("添加本地数据库情况："+success);
        binding.healthCard.setHealthData(data);
    }

    //-------------------------SQLite-------------------------

    private void SQLite_Init(){
        Health_Data healthData = SQLiteService.getInstance().getUserHealthData();
        String[] result = {"心脏病预测：暂无数据","糖尿病预测：暂无数据"};
        if(healthData == null){
            MyDebug.Print("暂时没有数据");
        }
        else {
            binding.healthCard.setHealthData(healthData);
        }
        binding.healthCard.setHealthResult(result);
    }

    //-------------------------Http-------------------------

    @Override
    public void onSuccess(String callbackClass, R_dataType rData) {
        if(callbackClass.equals(RequestClass_Health)){
            Object data = rData.getData();
            JSONObject jsonObject = (JSONObject) data;
            JSONObject dataObject = jsonObject.getJSONObject("data");
            JSONArray diabetesArray = dataObject.getJSONArray("diabetes");
            double diabetesValue = diabetesArray.getDouble(0);
            JSONArray heartArray = dataObject.getJSONArray("heart");
            double heartValue = heartArray.getDouble(0);

            diabetesValue = diabetesValue * 100;
            heartValue = heartValue * 100;
            int diabetes = (int) diabetesValue;
            int heart = (int) heartValue;

            String[] result = {"预测患心脏病概率为："+diabetes,"预测患糖尿病概率为："+heart};
            binding.healthCard.setHealthResult(result);
        }
    }

    @Override
    public void onFailure(String callbackClass) {
        if(callbackClass.equals(RequestClass_Health)){
            Toast.makeText(this.getContext(), "请求失败", Toast.LENGTH_SHORT).show();
        }
    }
}