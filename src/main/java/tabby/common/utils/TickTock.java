package tabby.common.utils;

import lombok.extern.slf4j.Slf4j;
import tabby.config.GlobalConfiguration;

import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * @author wh1t3p1g
 * @since 2022/3/25
 */
@Slf4j
public class TickTock {

    private int total;
    private int split;
    private boolean show = false;
    private CountDownLatch latch;
    private long startTime;


    public TickTock(int total, boolean show) {
        this.total = total;
        this.show = show;
        this.latch = new CountDownLatch(total);
        this.split = (int) (total * 0.05); // 5% 输出一次
        if (this.split == 0) {
            this.split = 1;
        }
        GlobalConfiguration.tickTock = this;
        startTime = System.nanoTime();
    }

    public void await() {
//        long timeout = GlobalConfiguration.TIMEOUT * 60L; // 预计每个都处理1s + 额外的最大时延10分钟
//        if(total < 100000){
//            timeout = timeout * 2;
//        }
//        info("Wait for all tasks to complete. Timeout: {}s", timeout);
        try {
//            if(!latch.await(timeout, TimeUnit.SECONDS)){
//                error("Still have {} methods to analysis, but it reached the max timeout.", latch.getCount());
//                GlobalConfiguration.isNeedStop = true;
//                info("Try to force stopping running task. Timeout: {}s", timeout);
//                latch.await(timeout, TimeUnit.SECONDS);
//                error("Remain {} running task", latch.getCount());
//            }else{
//                info("All tasks completed.");
//            }
            latch.await();
        } catch (InterruptedException e) {
//            e.printStackTrace();
            error("Still have {} methods to analysis, but it reached the max timeout.", latch.getCount());
        }
//        GlobalConfiguration.isNeedStop = false;
    }

    public void info(String msg, Object... objs) {
        if (show) {
            log.info(msg, objs);
        }
    }

    public void error(String msg, Object... objs) {
        if (show) {
            log.error(msg, objs);
        }
    }

    public void awaitWithoutTimeout() {
        try {
            info("Waiting for {} classes to be collected...", latch.getCount());
            latch.await();
        } catch (InterruptedException e) {
//            e.printStackTrace();
            error("Still have {} classes to collected.", latch.getCount());
        }
    }

    public void countDown() {
        latch.countDown();
        ticktock();
    }

    public long getCount() {
        return latch.getCount();
    }

    public void ticktock() {
        long remain = latch.getCount();
        long finished = total - remain;
        if (finished % split == 0 || finished == total) {
            info("Status: {}%, Finished: {}, Remain: {}, Cost: {} Mins",
                    String.format("%.1f", finished * 0.1 / total * 1000), finished, remain,
                    TimeUnit.NANOSECONDS.toMinutes(System.nanoTime() - startTime));
        }

        if (finished == total) {
            info("All tasks completed.");
        }
    }

    public void ticktockForScheduleTask(Map map) {
        long remain = latch.getCount();
        long finished = total - remain;
        int size = 0;
        if (map != null) {
            size = map.size();
        }
        info("Status: {}%, Finished: {}, Remain: {}, Cost: {} Mins, Current: {}",
                String.format("%.1f", finished * 0.1 / total * 1000), finished, remain,
                TimeUnit.NANOSECONDS.toMinutes(System.nanoTime() - startTime), size);
    }
}
