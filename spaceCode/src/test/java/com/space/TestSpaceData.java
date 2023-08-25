package com.space;

import cn.hutool.core.thread.ThreadUtil;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.HashUtil;
import cn.hutool.core.util.NumberUtil;
import com.space.bean.SpaceData;
import com.space.service.impl.SpaceDataServiceImpl;
import com.space.utils.MdbHelper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.StopWatch;

import java.io.File;
import java.util.*;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;


@SpringBootTest
public class TestSpaceData {

    @Autowired
    private SpaceDataServiceImpl spaceDataService;

    /**
     * 测试列表
     */
    @Test
    public void getListTest() {
        List<SpaceData> list = spaceDataService.getList();
        for (SpaceData spaceData : list) {
            System.out.println(spaceData.getData());
        }
    }

    /**
     * 测试新增
     */
    @Test
    public void add() {
        SpaceData data = new SpaceData();
        data.setData(999);
        int res = spaceDataService.add(data);
        System.out.println("执行结果：" + res);
    }

    /**
     * 测试批量新增
     */
    @Test
    public void addBatch() {
        List<SpaceData> lst = new ArrayList<>();
        SpaceData d1 = new SpaceData();
        d1.setData(111);

        SpaceData d2 = new SpaceData();
        d2.setData(222);

        lst.add(d1);
        lst.add(d2);
        int i = spaceDataService.addBatch(lst);
        System.out.println("执行结果：" + i);
    }

    @Test
    public void testLoadFromMdb() {

        List<Map<String, Object>> mapList = MdbHelper.resolverMdb("E:\\codes\\learn-java\\spaceCode\\src\\test\\java\\com\\space\\Db.mdb");
        Map<String, Object> stringObjectMap = mapList.stream().findFirst().orElse(null);
        Object data = stringObjectMap.get("data");  // [{Index=1, Data=999}, {Index=2, Data=97}}]
        List<Integer> list = new ArrayList<>();
        if (data instanceof List<?>) {
            for (Object o : (List<?>) data) {
                HashMap<String, String> one = (HashMap<String, String>) o;
                String data1 = one.get("Data");
                if (!data1.isEmpty()) {
                    list.add(NumberUtil.parseInt(data1));
                }
            }
        }
        System.out.println(list);
    }

    @Test
    public void testThread() {
        List<Map<String, Object>> mapList = MdbHelper.resolverMdb("E:\\codes\\learn-java\\spaceCode\\src\\test\\java\\com\\space\\Db.mdb");
        Map<String, Object> stringObjectMap = mapList.stream().findFirst().orElse(null);
        Object data = stringObjectMap.get("data");  // [{Index=1, Data=999}, {Index=2, Data=97}}]
        List<SpaceData> list = new ArrayList<>();
        if (data instanceof List<?>) {
            for (Object o : (List<?>) data) {
                HashMap<String, String> one = (HashMap<String, String>) o;
                String data1 = one.get("Data");
                if (!data1.isEmpty()) {
                    SpaceData d = new SpaceData();
                    d.setData(NumberUtil.parseInt(data1));

                    list.add(d);
                }
            }
        }

        Integer result = 0;
        for (SpaceData spaceData : list) {
            result += asyncTakeNum(spaceData.getData());
        }
        System.out.println("汇总后的结果： " + result);
    }


    public Integer asyncTakeNum(Integer num) {
        StopWatch watch = new StopWatch();
        watch.start("异步线程开始");

        try {
            //  +10
            Future<?> addFuture = addFuture(num);

            // -10
            Future<?> minusFuture = minusFuture(num);

            // *10
            Future<?> multiplyFuture = multiplyFuture(num);

            while (!addFuture.isDone() || !minusFuture.isDone() || !multiplyFuture.isDone()) {
                TimeUnit.SECONDS.sleep(3);
            }
        } catch (Exception ex) {

        } finally {
            watch.stop();
        }
        return num;
    }

    private Future<?> addFuture(Integer num) {
        String threadName = "upload-add-sync-add" + num;
        Future<?> future = null;
        if (!checkThreadAlive(threadName)) {
            future = ThreadUtil.execAsync(() -> {
                StopWatch watch = new StopWatch();
                watch.start(threadName + "异步 +10");
                Thread.currentThread().setName(threadName);
                add(num);
                watch.stop();
            });
        }
        return future;
    }

    private void add(Integer num) {
        num += 10;
    }

    private Future<?> minusFuture(Integer num) {
        String threadName = "upload-add-sync-minus" + num;
        Future<?> future = null;
        if (!checkThreadAlive(threadName)) {
            future = ThreadUtil.execAsync(() -> {
                StopWatch watch = new StopWatch();
                watch.start(threadName + "异步 -10");
                Thread.currentThread().setName(threadName);
                minus(num);
                watch.stop();
            });
        }
        return future;
    }

    private int minus(Integer num) {
        return num - 10;
    }

    private Future<?> multiplyFuture(Integer num) {
        String threadName = "upload-add-sync-multiply" + num;
        Future<?> future = null;
        if (!checkThreadAlive(threadName)) {
            future = ThreadUtil.execAsync(() -> {
                StopWatch watch = new StopWatch();
                watch.start(threadName + "异步 *10");
                Thread.currentThread().setName(threadName);
                multiply(num);
                watch.stop();
            });
        }
        return future;
    }

    private int multiply(Integer num) {
        return num * 10;
    }

    private boolean checkThreadAlive(String taskName) {
        Thread[] threads = ThreadUtil.getThreads();
        long count = Arrays.stream(threads).filter(item -> item.getName().equals(taskName)).count();
        return count > 0;
    }

}
