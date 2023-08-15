package me.wcy.music.executor;

/**
 * Created by hzwangchenyan on 2017/1/20.
 */
public interface IExecutor<T> {
    void execute();

    void onPrepare();

    void onExecuteSuccess(T t);

    void onExecuteFail(Exception e);
}
