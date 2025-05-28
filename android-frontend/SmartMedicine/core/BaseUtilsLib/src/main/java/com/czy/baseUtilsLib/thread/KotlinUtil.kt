package com.czy.baseUtilsLib.thread

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


object KotlinUtil{
    /**
     * io线程
     */
    fun CoroutineScope.launchIO(block: suspend () -> Unit): Job {
        return this.launch(Dispatchers.IO) { block() }
    }

    /**
     * 主线程
     */
    fun CoroutineScope.launchUI(block: suspend () -> Unit): Job {
        return this.launch(Dispatchers.Main) { block() }
    }

    /**
     * 在 IO 线程中执行给定的挂起函数，并在主线程中观察结果
     */
    fun <T> CoroutineScope.launchIOAndObserve(block: suspend () -> T, observer: (T) -> Unit) {
        launch(Dispatchers.IO) {
            val result = block() // 执行 IO 操作

            // 切换到主线程
            withContext(Dispatchers.Main) {
                observer(result) // 观察结果
            }
        }
    }
}



