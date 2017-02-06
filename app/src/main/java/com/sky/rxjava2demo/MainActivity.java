package com.sky.rxjava2demo;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.orhanobut.logger.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.ObservableSource;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.functions.Predicate;
import io.reactivex.schedulers.Schedulers;

import static com.sky.rxjava2demo.R.id.main_btn_rx1;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private final String TAG = this.getClass().getSimpleName();
    private Button mMainBtnRx1;
    private TextView mMsg;
    private Button main_btn_rx2;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
    }

    private void initView() {
        mMsg = (TextView) findViewById(R.id.main_tv_msg);
        mMainBtnRx1 = (Button) findViewById(main_btn_rx1);
        mMainBtnRx1.setOnClickListener(this);
        main_btn_rx2 = (Button) findViewById(R.id.main_btn_rx2);
        main_btn_rx2.setOnClickListener(this);
        findViewById(R.id.main_btn_rx3).setOnClickListener(this);
        findViewById(R.id.main_btn_rx4).setOnClickListener(this);
        findViewById(R.id.main_btn_rx5).setOnClickListener(this);

    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case main_btn_rx1:
                getObservable().subscribe(getObserver());
                break;
            case R.id.main_btn_rx2:
                Observable.just(true)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Consumer<Boolean>() {
                            @Override
                            public void accept(Boolean aBoolean) throws Exception {
                                Logger.d(aBoolean);
                                mMsg.setText(aBoolean + "");
                            }
                        }, new Consumer<Throwable>() {
                            @Override
                            public void accept(Throwable throwable) throws Exception {
                                Logger.d(throwable);
                            }
                        });
                break;
            case R.id.main_btn_rx3:
                String num = "123";
                String str = "我不是数字";
//
//                Observable.just(num).map(new Function<String, Integer>() {
//                    @Override
//                    public Integer apply(String s) throws Exception {
//                        return Integer.parseInt(s);
//                    }
//                }).subscribe(new Consumer<Integer>() {
//                    @Override
//                    public void accept(Integer integer) throws Exception {
//                        mMsg.setText(integer + "");
//                        Logger.d(integer);
//                    }
//                }, new Consumer<Throwable>() {
//                    @Override
//                    public void accept(Throwable throwable) throws Exception {
//                        Logger.d(throwable);
//                    }
//                });
                //头像
                Observable.just(num).flatMap(new Function<String, ObservableSource<Integer>>() {
                    @Override
                    public ObservableSource<Integer> apply(String s) throws Exception {
                        int anInt = Integer.parseInt(s);
                        return Observable.just(anInt * 123);
                    }
                }).flatMap(new Function<Integer, ObservableSource<Long>>() {
                    @Override
                    public ObservableSource<Long> apply(Integer integer) throws Exception {
                        return Observable.just(Long.valueOf(integer));
                    }
                }).subscribeOn(Schedulers.computation()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Observer<Long>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(Long value) {
                        Logger.d(value);

                    }

                    @Override
                    public void onError(Throwable e) {
                        Logger.d(e);
                    }

                    @Override
                    public void onComplete() {
                        Logger.d("onComplete");
                    }
                });
                break;
            case R.id.main_btn_rx4:
                Observable.just("123456")
                        .filter(new Predicate<String>() {
                            @Override
                            public boolean test(String s) throws Exception {
                                //类似校验操作  不满足条件不会继续传输
                                return s.contains("我");
                            }
                        })
                        .subscribeOn(Schedulers.computation())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Observer<String>() {
                            @Override
                            public void onSubscribe(Disposable d) {

                            }

                            @Override
                            public void onNext(String value) {

                            }

                            @Override
                            public void onError(Throwable e) {
                                Logger.d(e);
                            }

                            @Override
                            public void onComplete() {
                                Logger.d("onComplete");
                            }
                        });
                break;

            case R.id.main_btn_rx5:
                searchSomething();
                break;
        }
    }

    public Observable<String> getObservable() {
        Observable<String> observable = Observable.create(new ObservableOnSubscribe<String>() {
            @Override
            public void subscribe(ObservableEmitter<String> e) throws Exception {
                e.onNext("洗脸");
                e.onNext("洗脚");
                e.onComplete();
            }
        });
        return observable;
    }

    public Observer<String> getObserver() {
        return new Observer<String>() {
            @Override
            public void onSubscribe(Disposable d) {
                Log.d(TAG, "onSubscribe" + d.toString());
            }

            @Override
            public void onNext(String value) {
                Log.d(TAG, "onNext" + value);
            }

            @Override
            public void onError(Throwable e) {
                Log.d(TAG, "onError" + e.toString());
            }

            @Override
            public void onComplete() {
                Log.d(TAG, "onComplete");
            }
        };
    }

    private void searchSomething() {

        Observable.create(new ObservableOnSubscribe<String>() {
            @Override
            public void subscribe(ObservableEmitter<String> emitter) throws Exception {

                for (int i = 1; i < 5; i++) {
                    emitter.onNext(i + "");
                    Thread.sleep(i * 100);
                }
                emitter.onComplete();
            }
        })
                .subscribeOn(Schedulers.io())
                .debounce(300, TimeUnit.MILLISECONDS)
                .filter(new Predicate<String>() {
                    @Override
                    public boolean test(String s) throws Exception {
                        return !TextUtils.isEmpty(s);
                    }
                })
                .switchMap(new Function<String, ObservableSource<List<String>>>() {
                    @Override
                    public ObservableSource<List<String>> apply(String s) throws Exception {

                        List<String> list = new ArrayList<>();
                        list.add(s);
                        list.add("服务器列表数据1");
                        list.add("服务器列表数据2");
                        list.add("服务器列表数据3");

                        return Observable.just(list);
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<List<String>>() {
                    @Override
                    public void onSubscribe(Disposable d) {
//                        Logger.d("onSubscribe");
                    }

                    @Override
                    public void onNext(List<String> value) {
                        Logger.d("onNext:" + value);
                    }

                    @Override
                    public void onError(Throwable e) {
                        Logger.d("onError:" + e);
                    }

                    @Override
                    public void onComplete() {
//                        Logger.d("onComplete");
                    }
                });
    }

}
