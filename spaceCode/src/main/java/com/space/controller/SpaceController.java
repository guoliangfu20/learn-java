package com.space.controller;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.thread.ThreadUtil;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.space.bean.SpaceData;
import com.space.dto.PageInfo;
import com.space.service.impl.SpaceDataServiceImpl;
import com.space.utils.MdbHelper;
import com.space.utils.RabbitMQUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.util.FileCopyUtils;
import org.springframework.util.StopWatch;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.*;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/space")
public class SpaceController {

    // 上传文件前缀
    private static final String FILE_PATH_PREFIX = "upload/";
    private InputStream inputStream;
    private FileOutputStream outputStream;
    @Autowired
    private SpaceDataServiceImpl spaceService;
    private static Logger logger = LoggerFactory.getLogger(SpaceController.class);


    @RequestMapping("index")
    public String spaceIndex(ModelMap modelMap) {
        return "index";
    }

    @RequestMapping("getPage")
    public String getPage(@RequestParam Integer page,
                          @RequestParam Integer rows) {
        PageInfo<SpaceData> pageInfo = spaceService.getListPage(rows, page);

        JSONObject result = new JSONObject();
        result.set("rows", pageInfo.getData());
        result.set("total", pageInfo.getTotal());
        return result.toString();
    }

    /**
     * 列表.
     *
     * @param modelMap
     * @return
     */
    @RequestMapping("getList")
    public String getList(ModelMap modelMap) {
        List<SpaceData> list = spaceService.getList();
        modelMap.addAttribute("dataList", list);
        return "index";
    }

    /**
     * 文件上传
     *
     * @param file
     * @param req
     * @return
     */
    @RequestMapping(value = "upload")
    @ResponseBody
    public String fileUpload(@RequestParam("spaceFile") MultipartFile file, HttpServletRequest req) {
        if (file.isEmpty()) {
            return "文件为空";
        }
        File dir = new File(FILE_PATH_PREFIX);
        //判断该路径是否存在
        if (!dir.exists()) {
            dir.mkdirs();
        }

        // 获取文件名 Db.mdb
        String fileName = file.getOriginalFilename();

        logger.info("导入文件，时间: " + DateUtil.now() + ", 文件名：" + fileName);

        String fullPath = FILE_PATH_PREFIX + fileName;
        File newFile = new File(FILE_PATH_PREFIX, fileName);
        // 检测是否存在文件
        if (newFile.exists()) {
            newFile.delete();
        }
        try {
            inputStream = file.getInputStream();
            outputStream = new FileOutputStream(newFile);

            FileCopyUtils.copy(inputStream, outputStream);

            return fullPath;
        } catch (Exception exception) {
            System.out.println(exception);
        } finally {
            try {
                if (inputStream != null) {
                    inputStream.close();
                }
                if (outputStream != null) {
                    outputStream.close();
                }
            } catch (Exception exception) {
                System.out.println(exception);
            }
        }
        return "";
    }

    /**
     * 读取文件新增数据.
     *
     * @param fileName
     * @return
     */
    @PostMapping("mdbToDb")
    public String readMdb(@RequestParam("filename") String fileName) {
        String fullPath = FILE_PATH_PREFIX + fileName;
        if (!new File(fullPath).exists()) {
            return "文件不存在";
        }
        List<SpaceData> list = mdbToDto(fullPath);

        logger.info("解析完成，时间：" + DateUtil.now());

        spaceService.deleteAll();

        if (list.size() > 0) {
            if (spaceService.addBatch(list) > 0) {

                logger.info("插入数据库，时间：" + DateUtil.now());

                return "success";
            }
        }
        return "fail";
    }

    /**
     * 读取mdb文件，并将结果转化为bean。
     *
     * @param mdbFilePath
     * @return
     */
    private List<SpaceData> mdbToDto(String mdbFilePath) {
        List<SpaceData> list = new ArrayList<>();
        List<Map<String, Object>> maps = MdbHelper.resolverMdb(mdbFilePath);
        Map<String, Object> objectMap = maps.stream().findFirst().orElse(null);
        Object data = objectMap.get("data");  // [{Index=1, Data=999}, {Index=2, Data=97}}]

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
        return list;
    }

    @RequestMapping()
    public String muiltLoad(@RequestParam("filename") String fileName) {
        String fullPath = FILE_PATH_PREFIX + fileName;
        if (!new File(fullPath).exists()) {
            return "文件不存在";
        }

        logger.info("批量处理，开始时间：" + DateUtil.now());

        List<SpaceData> list = mdbToDto(fullPath);
        Integer result = 0;
        for (SpaceData spaceData : list) {
            result += asyncTakeNum(spaceData.getData());
        }
        spaceService.deleteAll();
        if (spaceService.addBatch(list) > 0) {
            logger.info("批量处理，结束时间：" + DateUtil.now());
            return result.toString();
        }
        return "0";
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

            // 异步等待
            while (!addFuture.isDone() || !minusFuture.isDone() || !multiplyFuture.isDone()) {
                TimeUnit.SECONDS.sleep(1);
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

    public String loadTask(@RequestParam("filename") String fileName) {
        String fullPath = FILE_PATH_PREFIX + fileName;
        if (!new File(fullPath).exists()) {
            return "文件不存在";
        }

        logger.info("批量处理，开始时间：" + DateUtil.now());

        List<SpaceData> list = mdbToDto(fullPath);

        List<Integer> datas = list.stream().map(SpaceData::getData).collect(Collectors.toList());
        try {
            RabbitMQUtils.sendMsg(JSONUtil.toJsonStr(datas));
            return "success";
        } catch (Exception exception) {
            System.out.println(exception);
        }
        return "fail";
    }


}
