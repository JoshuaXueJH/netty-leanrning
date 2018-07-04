package com.joshua.broker.netty;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.channels.Selector;
import java.nio.channels.spi.SelectorProvider;

public class NettyUtil {
    public static final String osName = System.getProperty("os.name");

    private static boolean islinux = false;
    private static boolean isWindows = false;

    static {
        if (osName != null && osName.toLowerCase().indexOf("linux") >= 0) {
            islinux = true;
        }
        if (osName != null && osName.toLowerCase().indexOf("windows") >= 0) {
            isWindows = true;
        }
    }

    public static boolean isLinux() {
        return islinux;
    }

    public static boolean isIsWindows() {
        return isWindows;
    }

    public static SelectorProvider getNioSelectorProvider() throws IOException {
        Selector result = null;
        if (isLinux()) {
            try {
                final Class<?> providerClass = Class.forName("sun.nio.ch.EPollSelectorProvider");
                if (providerClass != null) {
                    final Method method = providerClass.getMethod("provider");
                    if (method != null) {
                        final SelectorProvider selectorProvider = (SelectorProvider) method.invoke(null);
                        if (selectorProvider != null) {
                            result = selectorProvider.openSelector();
                        }
                    }
                }
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (result == null) {
            result = Selector.open();
        }
        return result.provider();
    }

    public static SocketAddress string2SocketAddress(final String address) {
        String[] strs = address.split(":");
        InetSocketAddress inetSocketAddress = new InetSocketAddress(strs[0], Integer.valueOf(strs[1]));
        return inetSocketAddress;
    }
}
