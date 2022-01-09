package cn.sunline.edsp.domain.util;

import org.springframework.util.StringUtils;

import java.io.IOException;
import java.net.*;
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
            return StringUtils.hasText(host)
                    && isLOAddress(InetAddress.getByName(host));
        } catch (UnknownHostException e) {
            return false;
        }
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
    public static InetAddress getAvailableAddress(String host, int port, int timeoutMillis) {
        return getAvailableAddress(new InetSocketAddress(host, port), timeoutMillis);
    }

    /**
     * 获得可用的地址
     * @param targetAddress
     * @param timeoutMillis
     * @return
     */
    public static InetAddress getAvailableAddress(InetSocketAddress targetAddress, int timeoutMillis) {
        try (Socket socket = new Socket()) {
            socket.connect(targetAddress, timeoutMillis);
            return socket.getLocalAddress();
        } catch (IOException e) {
            return null;
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
