//package regist.practice;
//
//public class ThreadTest extends Thread{
//    public ThreadTest(int index,int count) {
//        this.index=index;
//    }
//
//    public int index;
//
//    @Override
//    public void run() {
//        System.out.println(index+"Hello Thread");
//    }
//
//    public static void main(String[] args) {
//        int count=100;
//
//        for(int i=0;i<count;i++){
//            ThreadTest test=new ThreadTest(i);
//            test.start();
//        }
//
//        System.out.println("end");
//    }
//
//}
