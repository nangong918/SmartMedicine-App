package com.example.chattest.Health.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.example.chattest.R;
import com.example.chattest.SQLite.Health.Health_Data;
import com.example.chattest.Test.MyDebug;
import com.example.chattest.User.User;
import com.example.chattest.databinding.MyviewHealthDataBinding;


/**
 * TODO: document your custom view class.
 */
public class HealthView extends LinearLayout {

    //------------------------------Constructor------------------------------

    public HealthView(Context context) {
        super(context);
    }

    public HealthView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context,attrs);
    }

    public HealthView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context,attrs);
    }

    public HealthView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context,attrs);
    }

    //------------------------------Init------------------------------

    private MyviewHealthDataBinding binding;
    private void init(Context context, @Nullable AttributeSet attrs) {
        // 使用数据绑定框架inflate View
        binding = MyviewHealthDataBinding.inflate(LayoutInflater.from(context), this, true);

        setStart();

        // 在这里设置MaterialCardView的属性
        binding.basicCard.setCardElevation(25f);
        binding.basicCard.setCardBackgroundColor(ContextCompat.getColor(context, R.color.green_50));

        // 设置TextView的文本
        binding.Title.setText("健康状况");
    }

    //------------------------------onMeasure + onDraw------------------------------

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int desiredWidth = 330;
        int desiredHeight = LayoutParams.WRAP_CONTENT;

        int width = getMeasuredWidth();
        int height = getMeasuredHeight();

        MyDebug.Print("宽度："+width+"高度："+height);

        setMeasuredDimension(width, height);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);

        // 在这里设置子View的位置
        binding.basicCard.layout(0, 0, binding.basicCard.getMeasuredWidth(), binding.basicCard.getMeasuredHeight());
        binding.Title.layout(80, 10, binding.Title.getMeasuredWidth() + 80, binding.Title.getMeasuredHeight() + 10);
        MyDebug.Print("触发了设置");
    }

    @SuppressLint("DrawAllocation")
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        Paint paint = new Paint();
        // 绘制背景
        canvas.drawRect(0, 0, getWidth(), getHeight(), paint);

        // 绘制文本
        paint.setTextSize(30f);
        paint.setColor(Color.BLACK);
        MyDebug.Print("绘制");
        canvas.drawText("健康情况", 80, 40, paint);
    }

    //------------------------------ReDraw------------------------------

    @SuppressLint("SetTextI18n")
    private void setStart() {
        if(User.Logged){
            binding.cardUserID3.setText(Integer.toString(User.user_id));
        }
        invalidate();
        requestLayout();
    }

    @SuppressLint("SetTextI18n")
    public void setHealthData(Health_Data healthData){
        binding.hypertension.setText("高血压：" + healthData.hypertension);
        binding.highCholesterol.setText("高胆固醇：" + healthData.hypertension);
        binding.bmi.setText("BMI：" + healthData.bmi);
        binding.smoking.setText("吸烟：" + healthData.smoking);
        binding.stroke.setText("中风：" + healthData.stroke);
        binding.physicalExercise.setText("体力运动：" + healthData.physical_exercise);
        binding.fruits.setText("水果：" + healthData.fruits);
        binding.vegetable.setText("蔬菜：" + healthData.vegetable);
        binding.drinkingAlcohol.setText("重度饮酒：" + healthData.drinking_alcohol);
        binding.medicalCare.setText("医疗保健：" + healthData.medical_care);
        binding.noMedicalExpenses.setText("没有医疗开销：" + healthData.no_medical_expenses);
        binding.healthCondition.setText("健康状况：" + healthData.health_condition);
        binding.psychologicalHealth.setText("心理健康：" + healthData.psychological_health);
        binding.physicalHealth.setText("身体健康：" + healthData.physical_health);
        binding.difficultyWalking.setText("行走困难：" + healthData.difficulty_walking);
        if(healthData.sex == 0){
            binding.sex.setText("性别：" + "女");
        }
        else{
            binding.sex.setText("性别：" + "男");
        }
        binding.age.setText("年龄：" + healthData.age);
        binding.educationLevel.setText("教育水平：" + healthData.education_level);
        binding.incomeLevel.setText("收入水平：" + healthData.income_level);
        invalidate(); // 标记视图需要重绘
        requestLayout(); // 请求重新测量和布局
    }

    public void setHealthResult(String[] Result){
        binding.heartDisease.setText(Result[0]);
        binding.diabetes.setText(Result[1]);
        invalidate(); // 标记视图需要重绘
        requestLayout(); // 请求重新测量和布局
    }
}