package com.github.tvbox.osc.util;

import android.net.DnsResolver;
import android.net.Network;

import com.github.catvod.spider.net.OkHttp;
import com.qiniu.android.dns.Domain;
import com.qiniu.android.dns.NetworkInfo;
import com.qiniu.android.dns.Record;
import com.qiniu.android.dns.dns.DnsUdpResolver;

import org.junit.Test;

import java.io.IOException;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.SocketAddress;
import java.sql.SQLOutput;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class OKHttpTest {


    public void t() throws IOException {
        System.out.println(OkGoHelper.isIPV6Valid());
    }

    @Test
    public void t2() throws IOException {

    }
}
