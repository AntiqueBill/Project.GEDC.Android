package cn.edu.hit.project.ec.utils;

import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;

import cn.edu.hit.project.ec.models.wifi.WifiSecurityType;

public class WifiUtils {
    public static WifiSecurityType getWifiSecurity(ScanResult wifi) {
        if (wifi.capabilities.contains("WEP")) {
            return WifiSecurityType.WEP;
        } else if (wifi.capabilities.contains("PSK")) {
            return WifiSecurityType.PSK;
        } else if (wifi.capabilities.contains("EAP")) {
            return WifiSecurityType.EAP;
        }
        return WifiSecurityType.NONE;
    }

    public static WifiConfiguration getWifiConfiguration(ScanResult wifi, String password) {
        WifiConfiguration conf = new WifiConfiguration();
        conf.SSID = "\"" + wifi.SSID + "\"";
        switch (getWifiSecurity(wifi)) {
            case NONE:
                conf.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
                conf.allowedProtocols.set(WifiConfiguration.Protocol.RSN);
                conf.allowedProtocols.set(WifiConfiguration.Protocol.WPA);
                conf.allowedAuthAlgorithms.clear();
                conf.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP);
                conf.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.TKIP);
                conf.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP40);
                conf.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP104);
                conf.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
                conf.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
                break;
            case WEP:
                conf.wepKeys[0] = "\"" + password + "\"";
                conf.wepTxKeyIndex = 0;
                conf.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
                conf.allowedProtocols.set(WifiConfiguration.Protocol.RSN);
                conf.allowedProtocols.set(WifiConfiguration.Protocol.WPA);
                conf.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.OPEN);
                conf.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.SHARED);
                conf.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP);
                conf.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.TKIP);
                conf.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP40);
                conf.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP104);
                break;
            case PSK:
                conf.preSharedKey = "\""+ password +"\"";
                conf.allowedProtocols.set(WifiConfiguration.Protocol.RSN);
                conf.allowedProtocols.set(WifiConfiguration.Protocol.WPA);
                conf.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);
                conf.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP);
                conf.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.TKIP);
                conf.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP40);
                conf.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP104);
                conf.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
                conf.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
                break;
            case EAP:
                conf = null;
                break;
        }
        return conf;
    }
}
