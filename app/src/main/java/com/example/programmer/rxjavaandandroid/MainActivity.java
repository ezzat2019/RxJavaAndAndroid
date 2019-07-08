package com.example.programmer.rxjavaandandroid;

import androidx.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.os.Handler;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.widget.SearchView;
import android.widget.Toast;

import com.example.programmer.rxjavaandandroid.adapter.Adapter2;
import com.example.programmer.rxjavaandandroid.adapter.RecyclerAdapter;
import com.example.programmer.rxjavaandandroid.datasource.DataSourceTask;
import com.example.programmer.rxjavaandandroid.model.Comments;
import com.example.programmer.rxjavaandandroid.model.Comments2;
import com.example.programmer.rxjavaandandroid.model.Post;
import com.example.programmer.rxjavaandandroid.model.Posts2;
import com.example.programmer.rxjavaandandroid.model.Task;
import com.example.programmer.rxjavaandandroid.view_models.MainViewModel;
import com.example.programmer.rxjavaandandroid.view_models.Retrofit2;
import com.example.programmer.rxjavaandandroid.view_models.RetrofitPost;
import com.jakewharton.rxbinding3.view.RxView;

import java.io.IOException;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.ObservableSource;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function;
import io.reactivex.functions.Predicate;
import io.reactivex.schedulers.Schedulers;
import kotlin.Unit;
import okhttp3.ResponseBody;
import retrofit2.Retrofit;

public class MainActivity extends AppCompatActivity {
    private CompositeDisposable disposable;
    private MainViewModel viewModel;


    //ui

    private SearchView searchView;
    private RecyclerView recyclerView;

    //var

    private Long time;
    private RecyclerAdapter adapter;
    private Adapter2 adapter2;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        recyclerView = findViewById(R.id.my_rec);
        time=System.currentTimeMillis();
        searchView=findViewById(R.id.search_view);
        disposable=new CompositeDisposable();

        initRecyclerView();

    /*    getPosts2().subscribeOn(Schedulers.io())
                .flatMap(new Function<Posts2, ObservableSource<Posts2>>() {
                    @Override
                    public ObservableSource<Posts2> apply(Posts2 posts2) throws Exception {
                        return getComments2(posts2);
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Posts2>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(Posts2 posts2) {
                       updatePost2(posts2);
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });*/


        getPostsObservable()
                .subscribeOn(Schedulers.io())
                .concatMap(new Function<Post, ObservableSource<Post>>() {
                    @Override
                    public ObservableSource<Post> apply(Post post) throws Exception {
                        return getCommentsObservable(post);
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Post>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        disposable.add(d);
                    }

                    @Override
                    public void onNext(Post post) {
                        updatePost(post);
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e("walaaa", "onError: ", e);
                    }

                    @Override
                    public void onComplete() {
                    }
                });

        createObservabletoSearchView();
        disposable = new CompositeDisposable();
        standardRX();

        createSingleObservableWithInterval();
        createSingleObservableWithTimer();
        createViewModles();

        filerData();
        distinctData();

        transformMapToString();

        bundleRxObject();

        countNubmerClickByRx();



      //  ordinaryHandler();
    }

    private Observable<Post> getPostsObservable(){
        return RetrofitPost.getInstance().getPostData()
                .getPosts()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .flatMap(new Function<List<Post>, ObservableSource<Post>>() {
                    @Override
                    public ObservableSource<Post> apply(final List<Post> posts) throws Exception {
                        adapter.setPosts(posts);
                        return Observable.fromIterable(posts)
                                .subscribeOn(Schedulers.io());
                    }
                });
    }

    private void updatePost(final Post p){
        Observable
                .fromIterable(adapter.getPosts())
                .filter(new Predicate<Post>() {
                    @Override
                    public boolean test(Post post) throws Exception {
                        return post.getId() == p.getId();
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Post>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        disposable.add(d);
                    }

                    @Override
                    public void onNext(Post post) {
                        Log.d("walaaa", "onNext: updating post: " + post.getId() + ", thread: " + Thread.currentThread().getName());
                        adapter.updatePost(post);
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e("walaaa", "onError: ", e);
                    }

                    @Override
                    public void onComplete() {
                    }
                });
    }

    private void updatePost2(final Posts2 p)
    {
        Observable.fromIterable(adapter2.getPosts())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .filter(new Predicate<Posts2>() {
                    @Override
                    public boolean test(Posts2 posts2) throws Exception {
                        return posts2.getId()==p.getId();
                    }
                }).subscribe(new Observer<Posts2>() {
            @Override
            public void onSubscribe(Disposable d) {
                disposable.add(d);
            }

            @Override
            public void onNext(Posts2 posts2) {

                adapter2.updateList(posts2);

            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onComplete() {

            }
        });
    }

private Observable<Posts2>getComments2(final Posts2 posts2)
{
    return Retrofit2.getInstance().getPost2().getComments(posts2.getId())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .map(new Function<List<Comments2>, Posts2>() {
                @Override
                public Posts2 apply(List<Comments2> comments2s) throws Exception {

                    int delay = ((new Random()).nextInt(5) + 1) * 1000; // sleep thread for x ms
                    Thread.sleep(delay);
                   posts2.setComments2s(comments2s);
                    return posts2;
                }
            });
}
    private Observable<Posts2>getPosts2()
    {
        return Retrofit2.getInstance().getPost2().getPosts()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .flatMap(new Function<List<Posts2>, ObservableSource<Posts2>>() {
                    @Override
                    public ObservableSource<Posts2> apply(List<Posts2> posts2s) throws Exception {

                        adapter2.setPosts(posts2s);
                        return Observable.fromIterable(posts2s).subscribeOn(Schedulers.io());
                    }
                });
    }
    private Observable<Post> getCommentsObservable(final Post post){
        return RetrofitPost.getInstance().getPostData()
                .getComments(post.getId())
                .map(new Function<List<Comments>, Post>() {
                    @Override
                    public Post apply(List<Comments> comments) throws Exception {

                        int delay = ((new Random()).nextInt(5) + 1) * 1000; // sleep thread for x ms
                        Thread.sleep(delay);
                        Log.d("wlaaaa", "apply: sleeping thread " + Thread.currentThread().getName() + " for " + String.valueOf(delay)+ "ms");

                        post.setComments(comments);
                        return post;
                    }
                })
                .subscribeOn(Schedulers.io());

    }
    private void initRecyclerView(){
       adapter = new RecyclerAdapter();
        adapter2=new Adapter2();
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
    }
    private void createObservabletoSearchView() {
        Observable<String> observable=Observable.create(new ObservableOnSubscribe() {
            @Override
            public void subscribe(final ObservableEmitter emitter) throws Exception {
                searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                    @Override
                    public boolean onQueryTextSubmit(String s) {
                        return false;
                    }

                    @Override
                    public boolean onQueryTextChange(String s) {
                        emitter.onNext(s);
                        return false;
                    }
                });

            }
        }).debounce(500,TimeUnit.MILLISECONDS).observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io());

        observable.subscribe(new Observer<String>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(String s) {

                Log.d("deeeee",(System.currentTimeMillis()-time)+"  the val"+ s);
                time=System.currentTimeMillis();


            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onComplete() {

            }
        });
    }

    private void countNubmerClickByRx() {
        RxView.clicks(findViewById(R.id.button))
                .observeOn(AndroidSchedulers.mainThread())
                .map(new Function<Unit, Integer>() {
                    @Override
                    public Integer apply(Unit unit) throws Exception {
                        return 1;
                    }
                })
                .buffer(4,TimeUnit.SECONDS).subscribe(new Observer<List<Integer>>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(List<Integer> integers) {
                Log.d("clclclclcl", "onNext: You clicked " + integers.size() + " times in 4 seconds!");

            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onComplete() {

            }
        });


    }

    private void bundleRxObject() {
        Observable<Task> observable=
                Observable.fromIterable(DataSourceTask.getDummyTask())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
        observable.buffer(2).subscribe(new Observer<List<Task>>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(List<Task> tasks) {
                Log.d("qqqqq","bundle s");

                for (Task task:tasks)
                {
                    Log.d("qqqqq",task.toString());
                }

            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onComplete() {

            }
        });


    }

    private void transformMapToString() {
        Function<Task,Task> function=new Function<Task, Task>() {
            @Override
            public Task apply(Task task) throws Exception {
                task.setComplete(true);
                return task;
            }
        };

        Observable<Task> observable=Observable.fromIterable(DataSourceTask.getDummyTask())
                .subscribeOn(Schedulers.io())
                .map(function)
                .observeOn(AndroidSchedulers.mainThread());

        observable.subscribe(new Observer<Task>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(Task aBoolean) {
                Log.d("pppppppp",aBoolean.toString()+"");

            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onComplete() {

            }
        });

    }

    private void distinctData() {

        Observable<Task> observable=Observable.fromIterable(DataSourceTask.getDummyTask())
                .distinct(new Function<Task, String>() {
                    @Override
                    public String apply(Task task) throws Exception {
                        return task.getDescription();
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io());

        observable.subscribe(new Observer<Task>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(Task task) {
                Log.d("rrrrrr",task.getDescription()+" task "+task.isComplete());
            }




            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onComplete() {

            }
        });



    }

    private void filerData() {

        Observable observable =Observable.fromIterable(DataSourceTask.getDummyTask())
                .filter(new Predicate<Task>() {
                    @Override
                    public boolean test(Task task) throws Exception {
                        if (task.isComplete())
                        {
                            return true;
                        }

                        return false;
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
                Log.d("ezzzzo",task.getDescription()+" task "+task.isComplete());
            }




            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onComplete() {

            }
        });
    }

    private void createViewModles() {
        viewModel= ViewModelProviders.of(this).get(MainViewModel.class);
        viewModel.getLive().observe(this, new androidx.lifecycle.Observer<ResponseBody>() {
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
