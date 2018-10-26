package cn.hacktech.fucknet;

import android.net.wifi.WifiInfo;

import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

public class Hook implements IXposedHookLoadPackage {
    List<String> inetaddress_gethostaddress = new ArrayList();

    public static long ipToLong(String strIp) {
        long[] ip = new long[4];
        //先找到IP地址字符串中.的位置
        int position1 = strIp.indexOf(".");
        int position2 = strIp.indexOf(".", position1 + 1);
        int position3 = strIp.indexOf(".", position2 + 1);
        //将每个.之间的字符串转换成整型
        ip[0] = Long.parseLong(strIp.substring(0, position1));
        ip[1] = Long.parseLong(strIp.substring(position1 + 1, position2));
        ip[2] = Long.parseLong(strIp.substring(position2 + 1, position3));
        ip[3] = Long.parseLong(strIp.substring(position3 + 1));
        return (ip[0] << 24) + (ip[1] << 16) + (ip[2] << 8) + ip[3];
    }

    public void handleLoadPackage(final XC_LoadPackage.LoadPackageParam lpparam) throws Throwable {
        // 包名列表
        inetaddress_gethostaddress.add("cn.hacktech.getip");    //测试
        inetaddress_gethostaddress.add("com.asiainfo.sec.hubeiwifi");   //湖北飞young

        // 测试
        if (lpparam.packageName.equals("cn.hacktech.getip")) {
            XposedBridge.log("[FuckNet] Entry app: " + lpparam.packageName);

            XposedHelpers.findAndHookMethod(java.net.InetAddress.class, "getHostAddress", new XC_MethodHook() {
                @Override
                protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                    XposedBridge.log("[FuckNet] Find Method And Hook: " + lpparam.packageName);
                    param.setResult("100.64.7.100");
                }
            });
            XposedHelpers.findAndHookMethod(android.net.wifi.WifiInfo.class, "getIpAddress", new XC_MethodHook() {
                @Override
                protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                    //原先的整型IP
                    XposedBridge.log("整形IP："+param.getResult());
                    XposedBridge.log("[FuckNet] Find Method And Hook: " + lpparam.packageName);

                    //这里进行字符串分割，博主这里测试的新IP地址为“192.99.28.10”
                    String []str ="192.99.28.10".split("\\.");

                    //定义一个字符串，用来储存反转后的IP地址
                    String ipAdress="";

                    //for循环控制IP地址反转
                    for (int i=3;i>=0;i--){
                        ipAdress=ipAdress+str[i]+".";
                    }

                    //去掉最后一位的“.”
                    ipAdress=ipAdress.substring(0,ipAdress.length()-1);

                    //返回新的整型Ip地址
                    param.setResult((int)ipToLong(ipAdress));
                }
            });

//            XposedHelpers.findAndHookMethod("cn.hacktech.getip.MainActivity", lpparam.classLoader,"getIpAddress", new XC_MethodHook() {
//                @Override
//                protected void afterHookedMethod(MethodHookParam param) throws Throwable {
//                    XposedBridge.log("[FuckNet] Start hook: " + lpparam.packageName);
//                    Httpthread myhttp = new Httpthread();
//                    myhttp.start();
//                    while (true) {
//                        if (myhttp.userip != null)
//                            break;
//                    }
//                    if (myhttp.userip != "") {
//                        param.setResult(myhttp.userip);
//                    }
//                }
//            });
        }

        // 湖北飞young
        if (lpparam.packageName.equals("com.asiainfo.sec.hubeiwifi")) {
            XposedBridge.log("[FuckNet] Entry app: " + lpparam.packageName);

            XposedHelpers.findAndHookMethod("com.asiainfo.sec.hubeiwifi.e.n", lpparam.classLoader,"a", new XC_MethodHook() {
                @Override
                protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                    XposedBridge.log("[FuckNet] Start hook: " + lpparam.packageName);
                    List<String> iplist = new ArrayList();
                    Httpthread myhttp = new Httpthread();
                    myhttp.start();
                    while (true) {
                        if (myhttp.userip != null)
                            break;
                    }
                    if (myhttp.userip != "") {
                        iplist.add(myhttp.userip);
                        param.setResult(iplist);
                    }
                }
            });
        }

        // netkeeper-android
        if (lpparam.packageName.equals("com.xinli.netkeeper")) {
            XposedBridge.log("[FuckNet] Entry app: " + lpparam.packageName);

            XposedHelpers.findAndHookMethod(android.net.wifi.WifiInfo.class, "getIpAddress", new XC_MethodHook() {
                @Override
                protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                    XposedBridge.log("[FuckNet] Start hook: " + lpparam.packageName);

                    Httpthread myhttp = new Httpthread();
                    myhttp.start();
                    while (true) {
                        if (myhttp.userip != null)
                            break;
                    }
                    if (myhttp.userip != "") {
                        String []str = myhttp.userip.split("\\.");

                        //定义一个字符串，用来储存反转后的IP地址
                        String ipAdress="";

                        //for循环控制IP地址反转
                        for (int i=3;i>=0;i--){
                            ipAdress=ipAdress+str[i]+".";
                        }

                        //去掉最后一位的“.”
                        ipAdress=ipAdress.substring(0,ipAdress.length()-1);

                        //返回新的整型Ip地址
                        param.setResult((int)ipToLong(ipAdress));
                    }
                }
            });
        }
    }
}

class Httpthread extends Thread
{
    public String userip;

    public void run()
    {
        try{
            String url = "http://www.qq.com";
            URL serverUrl = new URL(url);
            HttpURLConnection conn = (HttpURLConnection) serverUrl.openConnection();
            conn.setRequestMethod("GET");
            conn.setInstanceFollowRedirects(false);
            conn.connect();
            String location = conn.getHeaderField("Location");

            String pattern = "userip=(.+?)&";
            Pattern r = Pattern.compile(pattern);
            Matcher m = r.matcher(location);
            if (m.find()) {
                this.userip = m.group(1);
            }
        } catch (Exception e) {
            XposedBridge.log("[FuckNet] Error: " + e.toString());
            this.userip = "";
        }
    }

}
