package com.hazelcast.nacos.utils;


import com.alibaba.nacos.common.utils.StringUtils;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

import static com.alibaba.nacos.client.utils.LogUtils.NAMING_LOGGER;

/**
 * @author Kdump
 */
public class FindAvailableNacosServer {
    public static String getIp(String servers){
        List<String> serverList;
        if (StringUtils.isNotEmpty(servers)) {
            serverList = Arrays.asList(servers.split(","));
            if (!serverList.isEmpty()) {
                Random random = new Random(System.currentTimeMillis());
                int index = random.nextInt(serverList.size());
                Exception exception = new Exception();
                for (int i = 0; i < serverList.size(); i++) {
                    String server = serverList.get(index);
                    try {
                        String[] split = server.split(":");
                        return NetUtils.getAvailableAddress(split[0], Integer.parseInt(split[1]), 1000).getHostAddress();
                    } catch (Exception e) {
                        exception = e;
                        NAMING_LOGGER.error("request {} failed.", server, e);
                    }

                    index = (index + 1) % serverList.size();
                }

                throw new IllegalStateException("All nacos server list(" + servers + ") are unavailable. tried: "
                        + exception.getMessage());
            }
        }
        return null;
    }
}