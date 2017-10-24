# RxJavaOperatorDemo
RxJava Operator

## just操作符
将要发送的数据转化为一个Observable
```
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
```

运行结果
```
I/MainActivity: onSubscribe: >>>>0
I/MainActivity: onNext: >>>HaHa
I/MainActivity: onNext: >>>WWW
I/MainActivity: onComplete:
```

## create操作符
使用一个函数创建一个Observable
```
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
 ```
 
 #### subscribe()重载函数
 
- `subscribe()`表示不接收`ObservableEmitter`发送的事件
- `subscribe(new Observer())` 表示接收`ObservableEmitter`发送的所有事件 `onNext()` `onComplete()`  `onError()`
- `subscribe(new Consumer())` 表示只接收`ObservableEmitter`发送的`onNext()`事件

## map操作符
用一个函数将Observable发送的**A** 类型数据转换成 **B**类型数据
```
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
```

## flatMap操作符
flatMap 将一个发射数据的Observable 变换为多个Obserables, 然后将他们发射的数据合并后单独放到一个Obserable中,接收顺序不固定
```
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
```
---
## concatMap操作符
concatMap 将一个发射数据的Observable 变换为多个Obserables, 然后将他们发射的数据合并后单独放到一个Obserable中,接收顺序固定

## zip操作符
zip 结合操作符 取出 第一个Observable 中发射数据 和第二个Observable中发射的数据,结合到一起如果运行在同一个线程,
先接收第一个Observable发送的数据，然后在接收第二个，运行在不同线程就一起接收
```
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
```
