package regist.practice;

import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.Executor;
public class AutoTrading {

    static List<String> answer  = new ArrayList<>();
    static CountDownLatch countDownLatch = new CountDownLatch(100);
    private static boolean isLocked = false;
    static Thread  lockedBy = null;
    static  int    lockedCount = 0;
    static Lock lock = new Lock() {
        @Override
        public synchronized  void lock() {
            Thread callingThread = Thread.currentThread();
            while(isLocked && lockedBy != callingThread){
                try {
                    wait();// 락권한 반납
                } catch (Exception e) {
                    System.out.println("lock: "+e);
                }finally {
                    isLocked = true;
                    lockedCount++;
                    lockedBy = callingThread;
                }


            }
        }

        @Override
        public void lockInterruptibly() throws InterruptedException {

        }

        @Override
        public boolean tryLock() {
            return false;
        }

        @Override
        public boolean tryLock(long time, TimeUnit unit) throws InterruptedException {
            return false;
        }

        @Override
        public synchronized  void unlock() {
            if(Thread.currentThread() == lockedBy){
                lockedCount--;

                if(lockedCount == 0){
                    isLocked = false;
                    notify();  /// 락권한 부여
                }
            }
        }

        @Override
        public Condition newCondition() {
            return null;
        }
    };
    public static void ans(int a) throws InterruptedException {
        for(int i = 0;i<100000;i++){
            for(int q = 0; q<10000;q++){

            }
        }


        lock.lock();
        try{
            //do critical section code, which may throw exception
            answer.add(String.valueOf(a));

        }catch (Exception e){
            System.out.println("ans: "+ e);
        }
        finally {
            lock.unlock();
        }
        countDownLatch.countDown();
    }
    @Async
    public static void executeAnsConcurrently() throws InterruptedException {
        // 스레드 풀을 생성합니다.
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(100); // 스레드 풀의 크기를 설정합니다. 원하는 크기로 조절할 수 있습니다.

        // 스레드 풀을 초기화합니다.
        executor.initialize();

        for (int i = 0; i < 100; i++) {
            final int value = i;
            //ans(value);
            executor.execute(() -> {
                try {
                    ans(value);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }); // 함수를 별도의 스레드에서 실행합니다.
        }
        try {
            countDownLatch.await(); // Wait for all threads to finish
        } catch (Exception e) {
            System.out.println(e);
        }
        System.out.println("사이즈는: "+answer.size());
        for(int i =0; i< answer.size();i++){
            System.out.println(answer.get(i));

        }
        System.out.println("종료");
        // 스레드 풀을 종료합니다.
        executor.shutdown();


    }
    public static void main(String[] args) throws InterruptedException {
        executeAnsConcurrently();
    }
}
