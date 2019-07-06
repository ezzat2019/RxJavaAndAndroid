package com.example.programmer.rxjavaandandroid;

import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.example.programmer.rxjavaandandroid.datasource.DataSourceTask;
import com.example.programmer.rxjavaandandroid.model.Task;
import com.example.programmer.rxjavaandandroid.view_models.MainViewModel;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Predicate;
import io.reactivex.schedulers.Schedulers;
import okhttp3.ResponseBody;

public class MainActivity extends AppCompatActivity {
    private CompositeDisposable disposable;
    private MainViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        disposable = new CompositeDisposable();
        standardRX();

        createSingleObservableWithInterval();
        createSingleObservableWithTimer();
        createViewModles();


      //  ordinaryHandler();
    }

    private void createViewModles() {
        viewModel= ViewModelProviders.of(this).get(MainViewModel.class);
        viewModel.getLive().observe(this, new android.arch.lifecycle.Observer<ResponseBody>() {
            @Override
            public void onChanged(@Nullable ResponseBody responseBody) {
                try {
                    Toast.makeText(MainActivity.this, responseBody.string()+"", Toast.LENGTH_SHORT).show();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void ordinaryHandler() {
        final Handler handler=new Handler();
        Runnable runnable=new Runnable() {
            int x=0;
            @Override
            public void run() {

                if (x>=5)
                {
                    handler.removeCallbacks(this);
                }
                else
                {
                    x++;
                    handler.postDelayed(this,1000);
                    Log.d("wezz", "run: " + x);

                }

            }
        };
        handler.postDelayed(runnable,1000);
    }

    private void standardRX() {
        Observable<Task> observable = Observable
                .fromIterable(DataSourceTask.getDummyTask())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());

        observable.subscribe(new Observer<Task>() {
            @Override
            public void onSubscribe(Disposable d) {
                Log.d("ezzattttttt", "isStartes");
                disposable.add(d);
            }

            @Override
            public void onNext(Task task) {
                Log.d("ezzattttttt", task.getDescription());

            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onComplete() {
                Log.d("ezzattttttt", "iscompleted");

            }
        });

    }

    private void createSingleObservableWithInterval() {
        final Task task = new Task("ezzat b eh is here ", true, 4);

        Observable<Task> observable = Observable
                .interval(1, TimeUnit.SECONDS)

                .create(new ObservableOnSubscribe<Task>() {
                    @Override
                    public void subscribe(ObservableEmitter<Task> emitter) throws Exception {

                        for (Task task1 : DataSourceTask.getDummyTask()) {
                            emitter.onNext(task1);
                        }

                        if (!emitter.isDisposed()) {
                            emitter.onComplete();

                        }

                    }
                })
                .subscribeOn(Schedulers.io())
               .takeWhile(new Predicate<Task>() {
                   @Override
                   public boolean test(Task task) throws Exception {
                       if (task.getDescription().equals("Make dinner"))
                           return false;
                       else
                       return true;
                   }
               })
                .observeOn(AndroidSchedulers.mainThread());

        observable.subscribe(new Observer<Task>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(Task task) {
                Log.d("eezzatttttt", "onNext: single task: " + task.getDescription());

            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onComplete() {

            }
        });
    }
    private void createSingleObservableWithTimer() {
        final Task task = new Task("ezzat b eh is here ", true, 4);

        Observable<Task> observable = Observable
                .timer(20,TimeUnit.SECONDS)

                .create(new ObservableOnSubscribe<Task>() {
                    @Override
                    public void subscribe(ObservableEmitter<Task> emitter) throws Exception {

                        for (Task task1 : DataSourceTask.getDummyTask()) {
                            emitter.onNext(task1);
                        }

                        if (!emitter.isDisposed()) {
                            emitter.onComplete();

                        }

                    }
                })
                .subscribeOn(Schedulers.io())

                .observeOn(AndroidSchedulers.mainThread());

        observable.subscribe(new Observer<Task>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(Task task) {
                Log.d("timeeeeeee", "onNext: single task: " + task.getDescription());

            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onComplete() {

            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        disposable.clear();
    }
}
