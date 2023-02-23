import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Schedulers;

import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CountDownLatch;
import java.util.function.Consumer;

import static reactor.core.publisher.BufferOverflowStrategy.DROP_LATEST;

public class Test {
    public static void main(String[] args) throws InterruptedException {

        CountDownLatch cdl = new CountDownLatch(1);
        Consumer<? super List<Object>> consumerFunction= new Consumer<List<Object>>() {
            @Override
            public void accept(List<Object> objects) {
                try {
                    for( Object object :objects){
                        Thread.sleep(100);
                        System.out.println("Thread [" + Thread.currentThread().getName() + "] get "+ object);;
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

        };
        Flux.generate(
                ()->1,
                (state, sink) -> {
                    String message = "message" + state;
                    System.out.println("Thread [" + Thread.currentThread().getName() + "] -> "+"Added message №" + state);
                    sink.next(message);
                    if (state==500) {
                        sink.complete();
                    }
                    return state+1;
                }).buffer(32).subscribe(consumerFunction);
        cdl.await();
    }



}
