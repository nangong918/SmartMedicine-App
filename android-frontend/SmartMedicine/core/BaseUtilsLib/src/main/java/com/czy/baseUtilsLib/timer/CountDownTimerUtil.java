package com.czy.baseUtilsLib.timer;

import android.os.CountDownTimer;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public class CountDownTimerUtil {

    public AtomicBoolean isStartCountDown;
    // 倒计时
    private AtomicInteger atomic_SecondsRemaining;
    private final CountDownTimer countDownTimer;
    public int startSeconds;

    public CountDownTimerUtil(int startSeconds, CountdownCallback callback){
        this.startSeconds = startSeconds;
        isStartCountDown = new AtomicBoolean(false);
        this.atomic_SecondsRemaining = new AtomicInteger();
        // 60秒的话需要从61秒开始
        this.atomic_SecondsRemaining.set(this.startSeconds + 1);
        // Timer
        countDownTimer = new CountDownTimer(this.atomic_SecondsRemaining.get() * 1000L, 1000L) {
            @Override
            public void onTick(long millisUntilFinished) {
                int currentTime = atomic_SecondsRemaining.get() - 1;
                atomic_SecondsRemaining.set(Math.max(currentTime, 0));
                callback.timeCountDown(atomic_SecondsRemaining.get());
            }

            @Override
            public void onFinish() {
                callback.countDownFinish();
                atomic_SecondsRemaining.set(startSeconds + 1);
            }
        };
    }

    public void startCountDown(){
        if (countDownTimer != null){
            countDownTimer.cancel();
            isStartCountDown.set(true);
            this.atomic_SecondsRemaining = new AtomicInteger();
            // 60秒的话需要从61秒开始
            this.atomic_SecondsRemaining.set(this.startSeconds + 1);
            countDownTimer.start();
        }
    }

    public void stopCountDown(){
        if (countDownTimer != null){
            countDownTimer.cancel();
            isStartCountDown.set(false);
        }
    }
}
