package rxjava;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import io.reactivex.internal.observers.DisposableLambdaObserver;

/**
 * 名称: RxjavaDemo
 * 描述: Rxjava例子
 *
 * @author gaoshudian
 * @date 2020-05-20 17:20
 */
public class RxjavaDemo {

    public static void main(String[] args) {
        //创建被观察者
        Observable<String> observable = Observable.create(new ObservableOnSubscribe<String>() {
            @Override
            public void subscribe(ObservableEmitter<String> emitter) throws Exception {
                //调用观察者的回调
                emitter.onNext("我是");
                emitter.onNext("RxJava");
                emitter.onNext("简单示例");
                emitter.onError(new Throwable("出错了"));
                emitter.onComplete();
            }
        });

        //创建观察者
        Observer<String> observer = new Observer<String>() {

            @Override
            public void onError(Throwable e) {
                System.out.println("onError："+e);
            }

            @Override
            public void onComplete() {
                System.out.println("onComplete");
            }

            //onSubscribe()方法是最先调用的
            @Override
            public void onSubscribe(Disposable d) {
                System.out.println("onSubscribe");
            }

            @Override
            public void onNext(String s) {
                System.out.println("onNext:"+s);
            }
        };

        //注册，将观察者和被观察者关联，将会触发OnSubscribe.call方法
        observable.subscribe(observer);
    }
}
