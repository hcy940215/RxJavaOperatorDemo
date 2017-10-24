package com.shengmingji.rxjavaoperatordemo;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Observer;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.ObservableSource;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.BiFunction;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity {

    private String TAG = MainActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        TextView tvJust = (TextView) findViewById(R.id.tv_Just);
        TextView tvCreate = (TextView) findViewById(R.id.tv_create);
        TextView tvMap = (TextView) findViewById(R.id.tv_map);
        TextView tvZip = (TextView) findViewById(R.id.tv_zip);
        tvJust.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                justOperator();
            }
        });
        tvCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createOperator();
            }
        });
        tvMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mapOperator();
            }
        });
        tvZip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                zipOperator();
            }
        });
    }

    /**
     * zip 结合操作符 取出 第一个Obserable 中发射数据 和第二个Obserable中发射的数据
     * 结合到一起  运行在同一个线程的结果
     * I/MainActivity: observable1: >> onNext 1
     * I/MainActivity: observable1: >> onNext 2
     * I/MainActivity: observable1: >> onNext 3
     * I/MainActivity: observable1: >> onNext 4
     * I/MainActivity: observable1: >> onNext 5
     * I/MainActivity: observable1: >> onNext onComplete
     * I/MainActivity: observable2: >> onNext A
     * I/MainActivity: accept: >> 1A
     * I/MainActivity: observable2: >> onNext B
     * I/MainActivity: accept: >> 2B
     * I/MainActivity: observable2: >> onNext C
     * I/MainActivity: accept: >> 3C
     * I/MainActivity: observable2: >> onNext D
     * I/MainActivity: accept: >> 4D
     * I/MainActivity: observable2: >> onNext onComplete
     * <p>
     * 运行在运行在不同线程的结果
     * <p>
     * 10-24 17:34:48.875 11506-12412/com.shengmingji.rxjavaoperatordemo I/MainActivity: observable1: >> onNext 1
     * I/MainActivity: observable1: >> onNext 2
     * I/MainActivity: observable2: >> onNext A
     * I/MainActivity: accept: >> 1A
     * I/MainActivity: observable1: >> onNext 3
     * I/MainActivity: observable1: >> onNext 4
     * I/MainActivity: observable2: >> onNext B
     * I/MainActivity: accept: >> 2B
     * I/MainActivity: observable1: >> onNext 5
     * I/MainActivity: observable2: >> onNext C
     * I/MainActivity: accept: >> 3C
     * I/MainActivity: observable1: >> onNext onComplete
     * I/MainActivity: observable2: >> onNext D
     * I/MainActivity: accept: >> 4D
     */
    private void zipOperator() {
        Observable<Integer> observable1 = Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<Integer> e) throws Exception {
                Log.i(TAG, "observable1: >> onNext " + "1");
                e.onNext(1);
                Thread.sleep(1000);
                Log.i(TAG, "observable1: >> onNext " + "2");
                e.onNext(2);
                Thread.sleep(1000);
                Log.i(TAG, "observable1: >> onNext " + "3");
                e.onNext(3);
                Log.i(TAG, "observable1: >> onNext " + "4");
                e.onNext(4);
                Thread.sleep(1000);
                Log.i(TAG, "observable1: >> onNext " + "5");
                e.onNext(5);
                Thread.sleep(1000);
                Log.i(TAG, "observable1: >> onNext " + "onComplete");
                e.onComplete();
            }
        }).subscribeOn(Schedulers.newThread());
        Observable<String> observable2 = Observable.create(new ObservableOnSubscribe<String>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<String> e) throws Exception {
                Thread.sleep(1000);
                Log.i(TAG, "observable2: >> onNext " + "A");
                e.onNext("A");
                Thread.sleep(1000);
                Log.i(TAG, "observable2: >> onNext " + "B");
                e.onNext("B");
                Thread.sleep(1000);
                Log.i(TAG, "observable2: >> onNext " + "C");
                e.onNext("C");
                Thread.sleep(1000);
                Log.i(TAG, "observable2: >> onNext " + "D");
                e.onNext("D");
                Thread.sleep(1000);
                Log.i(TAG, "observable2: >> onNext " + "onComplete");
                e.onComplete();
            }
        }).subscribeOn(Schedulers.newThread());
        ;

        Observable.zip(observable1, observable2, new BiFunction<Integer, String, String>() {
            @Override
            public String apply(@NonNull Integer integer, @NonNull String s) throws Exception {
                return integer + s;
            }
        }).observeOn(AndroidSchedulers.mainThread()).subscribeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<String>() {
            @Override
            public void accept(@NonNull String s) throws Exception {
                Log.i(TAG, "accept: >> " + s);
            }
        });
    }

    /**
     * map Function< Integer, String>
     * 类型转换操作符 将Integer转换为String
     * flatMap 将一个发射数据的Observable 变换为多个Obserables, 然后将他们合并放到单独一个
     * Obserable中  顺序不固定
     * <p>
     * concatMap将一个发射数据的Observable 变换为多个Obserables, 然后将他们合并放到单独一个
     * Obserable中  顺序固定
     */
    private void mapOperator() {

        Log.i(TAG, "mapOperator: =============map(new Function<Integer, String>()=================");

        Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<Integer> e) throws Exception {
                e.onNext(1);
                e.onNext(2);
                e.onNext(3);
                e.onComplete();
            }
        }).map(new Function<Integer, String>() {
            @Override
            public String apply(@NonNull Integer integer) throws Exception {
                return "map operator translation integer to String >>> " + integer;
            }
        }).subscribe(new Consumer<String>() {
            @Override
            public void accept(@NonNull String s) throws Exception {
                Log.i(TAG, "accept: >>>" + s);
            }
        });


        Log.i(TAG, "mapOperator: =============flatMap(Function<Integer, ObservableSource<String>>()=================");

        Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<Integer> e) throws Exception {
                e.onNext(10);
                e.onNext(20);
                e.onNext(30);
                e.onComplete();
            }
        }).flatMap(new Function<Integer, ObservableSource<String>>() {
            @Override
            public ObservableSource<String> apply(@NonNull Integer integer) throws Exception {
                ArrayList<String> list = new ArrayList<>();
                for (int i = 0; i < 2; i++) {
                    list.add(String.valueOf(integer));
                }
                return Observable.fromIterable(list);
            }
        }).subscribe(new Consumer<String>() {
            @Override
            public void accept(@NonNull String s) throws Exception {
                Log.i(TAG, "accept: >>  " + s);
            }
        });

        Log.i(TAG, "mapOperator: =============concatMap(Function<Integer, ObservableSource<String>>()=================");

        Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<Integer> e) throws Exception {
                e.onNext(10);
                e.onNext(20);
                e.onNext(30);
                e.onComplete();
            }
        }).concatMap(new Function<Integer, ObservableSource<String>>() {
            @Override
            public ObservableSource<String> apply(@NonNull Integer integer) throws Exception {
                ArrayList<String> list = new ArrayList<>();
                for (int i = 0; i < 2; i++) {
                    list.add(String.valueOf(integer));
                }
                return Observable.fromIterable(list).delay(10, TimeUnit.MILLISECONDS);
            }
        }).subscribe(new Consumer<String>() {
            @Override
            public void accept(@NonNull String s) throws Exception {
                Log.i(TAG, "accept: >>  " + s);
            }
        });
    }

    private void createOperator() {
        //=====================subscribe(Observer<? super T> observer)=================
        Log.i(TAG, "createOperator: =====================subscribe(Observer<? super T> observer)=================");

        Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<Integer> emitter) throws Exception {
                emitter.onNext(1);
                Log.i(TAG, "subscribe: >> onNext " + 1);
                emitter.onNext(2);
                Log.i(TAG, "subscribe: >> onNext " + 2);
                emitter.onNext(3);
                Log.i(TAG, "subscribe: >> onNext " + 3);
                emitter.onComplete();
                Log.i(TAG, "subscribe: >> onComplete ");
            }
        }).subscribe(new io.reactivex.Observer<Integer>() {

            //Disposable 相当于开关，切断 订阅事件
            private Disposable mDisposable;

            @Override
            public void onSubscribe(@NonNull Disposable d) {
                mDisposable = d;
                Log.i(TAG, "onSubscribe: ");
            }

            @Override
            public void onNext(@NonNull Integer integer) {
                if (integer == 2) {
                    //Observer 不在接发送的事件，但Observable会继续发送事件
                    mDisposable.dispose();
                }
                Log.i(TAG, "onNext: >>" + integer);
            }

            @Override
            public void onError(@NonNull Throwable e) {

            }

            @Override
            public void onComplete() {
                Log.i(TAG, "onComplete: >>");
            }
        });

        Log.i(TAG, "createOperator: =====================subscribe()=================");
        //=====================subscribe()=================
        //不带任何参数的subscribe() 表示不关心任何事件,你上游尽管发你的数据去吧
        Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<Integer> emitter) throws Exception {
                emitter.onNext(1);
                Log.i(TAG, "subscribe: >> onNext " + 1);
                emitter.onNext(2);
                Log.i(TAG, "subscribe: >> onNext " + 2);
                emitter.onNext(3);
                Log.i(TAG, "subscribe: >> onNext " + 3);
                emitter.onComplete();
                Log.i(TAG, "subscribe: >> onComplete ");
            }
        }).subscribe();

        Log.i(TAG, "createOperator: =====================subscribe(Consumer<T>())=================");
        //=====================subscribe(Consumer<T>())=================
        //带有一个Consumer参数的方法表示下游只关心onNext事件, 其他的事件我假装没看见, 因此我们如果只需要onNext事件可以这么写
        Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<Integer> emitter) throws Exception {
                emitter.onNext(1);
                Log.i(TAG, "subscribe: >> onNext " + 1);
                emitter.onNext(2);
                Log.i(TAG, "subscribe: >> onNext " + 2);
                emitter.onNext(3);
                Log.i(TAG, "subscribe: >> onNext " + 3);
                emitter.onComplete();
                Log.i(TAG, "subscribe: >> onComplete ");
            }
        }).subscribe(new Consumer<Integer>() {
            @Override
            public void accept(@NonNull Integer integer) throws Exception {
                Log.i(TAG, "accept: >>" + integer);
            }
        });
    }

    //just 发射数据
    private void justOperator() {
        Observable.just("HaHa", "WWW")
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new io.reactivex.Observer<String>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {
                        Log.i(TAG, "onSubscribe: >>>>" + d);
                    }

                    @Override
                    public void onNext(@NonNull String s) {
                        Log.i(TAG, "onNext: >>>" + s);
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {

                    }

                    @Override
                    public void onComplete() {
                        Log.i(TAG, "onComplete: ");
                    }
                });
    }
}
