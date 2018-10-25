package cn.hacktech.fucknet;

import java.net.HttpURLConnection;
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

    public void handleLoadPackage(final XC_LoadPackage.LoadPackageParam lpparam) throws Throwable {
        // 包名列表
        inetaddress_gethostaddress.add("cn.hacktech.getip");    //测试
        inetaddress_gethostaddress.add("com.asiainfo.sec.hubeiwifi");   //湖北飞young

        // 测试
        if (lpparam.packageName.equals("cn.hacktech.getip")) {
            XposedBridge.log("[FuckNet] Entry app: " + lpparam.packageName);

            XposedHelpers.findAndHookMethod("cn.hacktech.getip.MainActivity", lpparam.classLoader,"getIpAddress", new XC_MethodHook() {
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
                        param.setResult(myhttp.userip);
                    }
                }
            });
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

            XposedHelpers.findAndHookMethod("com.xinli.vkeeper.fragments.LoginFragment", lpparam.classLoader,"isHuBeiIpLimited", new XC_MethodHook() {
                @Override
                protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                    XposedBridge.log("[FuckNet] Start hook: " + lpparam.packageName);
                    param.setResult(true);
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
