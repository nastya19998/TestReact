package com.company;

import java.util.concurrent.Executors;
import java.util.concurrent.Flow;
import java.util.concurrent.Flow.Publisher;
import java.util.concurrent.Flow.Subscriber;
import java.util.concurrent.Flow.Subscription;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.SubmissionPublisher;


public class Main {
    public static void main(String[] args) throws InterruptedException {
        final int BUFFER = 32;

        SubmissionPublisher<String> publisher= new SubmissionPublisher<>( Executors.newFixedThreadPool(1), BUFFER);

        Subscriber<String> subscriber = new Subscriber<String>() {
            private Subscription subscription;

            @Override
            public void onSubscribe(Subscription subscription) {
                this.subscription = subscription;
                subscription.request(1);
            }

            @Override
            public void onNext(String item) {
                System.out.println( "Received message: " + item);
                try {
                    Thread.sleep(300);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                subscription.request(1);
            }

            @Override
            public void onError(Throwable throwable) {
                System.out.println(Thread.currentThread().getName() + " | ERROR = "
                        + throwable.getClass().getSimpleName() + " | " + throwable.getMessage());
            }

            @Override
            public void onComplete() {
                System.out.println("Completed");
            }
        };

        publisher.subscribe(subscriber);

        for (int i = 0; i < 200; i++) {
            System.out.println(Thread.currentThread().getName() + " | Publishing = " + i);
            publisher.submit("message"+i);
        }

        publisher.close();


    }
}
