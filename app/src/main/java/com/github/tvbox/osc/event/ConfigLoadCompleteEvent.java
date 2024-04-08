package com.github.tvbox.osc.event;

public class ConfigLoadCompleteEvent {

    public static final int TYPE_LOADCONFIG_FAILED = -2;
    public static final int TYPE_LOADJAR_FAILED = -1;
    public static final int TYPE_LOADCONFIG_SUCCESS = 1;
    public static final int TYPE_LOADJAR_SUCCESS = 2;

    public static final int TYPE_LOADCONFIG_START = 10;
    public static final int TYPE_LOADJAR_START = 11;

    public static final int TYPE_NETCHECK_START = 21;
    public static final int TYPE_NETCHECK_IP6 = 22;
    public static final int TYPE_NETCHECK_IP4 = 23;

    public int type;
    public Object obj;

    public ConfigLoadCompleteEvent(int type) {
        this.type = type;
    }

    public ConfigLoadCompleteEvent(int type, Object obj) {
        this.type = type;
        this.obj = obj;
    }

}
