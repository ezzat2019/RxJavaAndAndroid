package com.example.programmer.rxjavaandandroid;

import com.example.programmer.rxjavaandandroid.model.Task;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import io.reactivex.Observable;
import okhttp3.ResponseBody;

public class Repositry {
    private static Repositry instance;
    private final ExecutorService service=Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors()+1);

  public static Repositry getInstance()
  {
      if (instance==null)
      {
          instance=new Repositry();
      }
      return instance;
  }

private final Callable<Observable<ResponseBody>>observableCallable=new Callable<Observable<ResponseBody>>() {
    @Override
    public Observable<ResponseBody> call() throws Exception {

        return null;

    }
};
//  public Future<Observable<ResponseBody>> future=new Future<Observable<ResponseBody>>() {
//      @Override
//      public boolean cancel(boolean b) {
//          if (b)
//          {
//              service.isShutdown();
//          }
//          return false;
//      }
//
//      @Override
//      public boolean isCancelled() {
//          return service.isShutdown();
//      }
//
//      @Override
//      public boolean isDone() {
//          return service.isTerminated();
//      }
//
//      @Override
//      public Observable<ResponseBody> get() throws ExecutionException, InterruptedException {
//
//
//
//          return service.submit(new Callable<Observable<ResponseBody>>() {
//          });
//      }
//
//      @Override
//      public Observable<ResponseBody> get(long l, TimeUnit timeUnit) throws ExecutionException, InterruptedException, TimeoutException {
//          return null;
//      }
//  };


}
