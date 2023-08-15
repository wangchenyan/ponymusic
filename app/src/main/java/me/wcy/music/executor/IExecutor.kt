package me.wcy.music.executor

/**
 * Created by hzwangchenyan on 2017/1/20.
 */
interface IExecutor<T> {
    fun execute()
    fun onPrepare()
    fun onExecuteSuccess(t: T)
    fun onExecuteFail(e: Exception?)
}