package com.github.catvod.crawler;

import android.content.Context;
import android.util.Log;

import com.github.tvbox.osc.base.App;

import org.json.JSONObject;

import java.io.File;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import dalvik.system.ApplicationRuntime;
import dalvik.system.DexClassLoader;

public class JarLoader {
    private DexClassLoader classLoader = null;
    private ConcurrentHashMap<String, Spider> spiders = new ConcurrentHashMap<>();
    private Method proxyFun = null;

    /**
     * 不要在主线程调用我
     *
     * @param cache
     */
    public boolean load(String cache) {
        spiders.clear();
        proxyFun = null;
        boolean success = false;
        try {
            long a = System.currentTimeMillis();
            if(cache != null && !"".equals(cache.trim())){
                File cacheDir = new File(App.getInstance().getCacheDir().getAbsolutePath() + "/catvod_csp");
                if (!cacheDir.exists())
                    cacheDir.mkdirs();
                classLoader = new DexClassLoader(cache, cacheDir.getAbsolutePath(), null, App.getInstance().getClassLoader());
            }
            // make force wait here, some device async dex load
            int count = 0;
            do {
                try {
                    Class classInit = this.loadClass("com.github.catvod.spider.Init");
                    if (classInit != null) {
                        Method method = classInit.getMethod("init", Context.class);
                        method.invoke(null, App.getInstance());
                        success = true;
                        break;
                    }
                    Thread.sleep(200);
                } catch (Throwable th) {
                    th.printStackTrace();
                }
                count++;
            } while (count < 5);
        } catch (Throwable th) {
            th.printStackTrace();
        }
        return success;
    }

    public Spider getSpider(String key, String cls, String ext) {
        String clsKey = cls.replace("csp_", "");
        if (spiders.containsKey(key))
            return spiders.get(key);
        try {
            Spider sp = (Spider) this.loadClass("com.github.catvod.spider." + clsKey).newInstance();
            sp.init(App.getInstance(), ext);
            spiders.put(key, sp);
            return sp;
        } catch (Throwable th) {
            th.printStackTrace();
        }
        return new SpiderNull();
    }

    public JSONObject jsonExt(String key, LinkedHashMap<String, String> jxs, String url) {
        try {
            String clsKey = "Json" + key;
            String hotClass = "com.github.catvod.parser." + clsKey;
            Class jsonParserCls = this.loadClass(hotClass);
            Method mth = jsonParserCls.getMethod("parse", LinkedHashMap.class, String.class);
            return (JSONObject) mth.invoke(null, jxs, url);
        } catch (Throwable th) {
            th.printStackTrace();
        }
        return null;
    }

    public JSONObject jsonExtMix(String flag, String key, String name, LinkedHashMap<String, HashMap<String, String>> jxs, String url) {
        try {
            String clsKey = "Mix" + key;
            String hotClass = "com.github.catvod.parser." + clsKey;
            Class jsonParserCls = this.loadClass(hotClass);
            Method mth = jsonParserCls.getMethod("parse", LinkedHashMap.class, String.class, String.class, String.class);
            return (JSONObject) mth.invoke(null, jxs, name, flag, url);
        } catch (Throwable th) {
            th.printStackTrace();
        }
        return null;
    }

//    public Object[] proxyInvoke(Map params) {
//        try {
//            if (proxyFun != null) {
//                return (Object[]) proxyFun.invoke(null, params);
//            }
//        } catch (Throwable th) {
//            th.printStackTrace();
//        }
//        return null;
//    }

    private Class<?> loadClass(String className) {
        if (classLoader != null) {
            try {
                return classLoader.loadClass(className);
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
        try {
            return Class.forName(className);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }
}
