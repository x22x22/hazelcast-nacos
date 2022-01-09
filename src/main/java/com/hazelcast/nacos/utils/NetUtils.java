package com.hazelcast.nacos.utils;

import org.apache.commons.lang3.StringUtils;

import javax.annotation.Nullable;
import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.NetworkInterface;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Objects;

/**
 * @author tanghuang@sunline.cn create on 2019/9/21
 */
public class NetUtils {

    private static final String LO_NETWORK_INTERFACE = "lo";

    private static final InetAddress ANY_IPV4_ADDRESS = getAddress("0.0.0.0");

    private static final InetAddress ANY_IPV6_ADDRESS = getAddress("::");

    /**
     * 根据主机名获得地址
     * @param host
     * @return
     */
    public static InetAddress getAddress(String host) {
        try {
            return InetAddress.getByName(host);
        } catch (UnknownHostException e) {
            throw new IllegalArgumentException("Unknown host: " + host, e);
        }
    }

    /**
     * 判断是否是0:0:0:0
     * @param host
     * @return
     */
    public static boolean isAnyAddress(String host) {
        try {
            InetAddress address =InetAddress.getByName(host);
            return Objects.equals(address, ANY_IPV4_ADDRESS)
                    || Objects.equals(address, ANY_IPV6_ADDRESS);
        } catch (UnknownHostException e) {
            return false;
        }
    }

    /**
     * 是否回环地址
     * @param host
     * @return
     */
    public static boolean isLOAddress(String host) {
        try {
            return NetUtils.hasText(host)
                    && isLOAddress(InetAddress.getByName(host));
        } catch (UnknownHostException e) {
            return false;
        }
    }



    public static boolean hasText(@Nullable String str) {
        return (str != null && !str.isEmpty() && NetUtils.containsText(str));
    }

    private static boolean containsText(CharSequence str) {
        int strLen = str.length();
        for (int i = 0; i < strLen; i++) {
            if (!Character.isWhitespace(str.charAt(i))) {
                return true;
            }
        }
        return false;
    }

    /**
     * 是否回环地址
     * @param address
     * @return
     */
    public static boolean isLOAddress(InetAddress address) {
        try {
            NetworkInterface network = NetworkInterface.getByInetAddress(address);
            return network != null && Objects.equals(LO_NETWORK_INTERFACE, network.getName());
        } catch (SocketException e) {
            return false;
        }
    }

    /**
     * 获得可用的地址
     * @param host
     * @param port
     * @param timeoutMillis
     * @return
     */
    public static InetAddress getAvailableAddress(String host, int port, int timeoutMillis) throws IOException {
        return getAvailableAddress(new InetSocketAddress(host, port), timeoutMillis);
    }

    /**
     * 获得可用的地址
     * @param targetAddress
     * @param timeoutMillis
     * @return
     */
    public static InetAddress getAvailableAddress(InetSocketAddress targetAddress, int timeoutMillis) throws IOException {
        try (Socket socket = new Socket()) {
            socket.connect(targetAddress, timeoutMillis);
            return socket.getLocalAddress();
        }
    }

    /**
	 * 测试本机端口是否被使用
	 *
	 * @param port
	 * @return
	 */
	public static boolean isLocalPortUsing(int port) {
		return isPortUsing("127.0.0.1", port);
	}

	/***
	 * 测试主机Host的port端口是否被使用
	 *
	 * @param host
	 * @param port
	 * @throws UnknownHostException
	 */
	public static boolean isPortUsing(String host, int port) {
		boolean flag = false;
		try (Socket socket = new Socket()) {
		    socket.connect(new InetSocketAddress(host, port), 3000);
			flag = true;
		} catch (IOException e) {
		    ;
		}
		return flag;
	}

    /**
     * 获得可用的端口
     * @param startPort
     * @param portStr
     * @return
     */
	public static int getAvailablePort(Integer startPort, String portStr) {
		if(!StringUtils.isEmpty(portStr)) {
			startPort = Integer.parseInt(portStr);
		}
		while (true) {
			if(!NetUtils.isLocalPortUsing(startPort)) {
				return startPort;
			}
			startPort ++;
		}
	}

}
