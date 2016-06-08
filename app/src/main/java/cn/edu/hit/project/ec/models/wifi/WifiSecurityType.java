package cn.edu.hit.project.ec.models.wifi;

public enum  WifiSecurityType {
    NONE, WEP, PSK, EAP;

    private final static WifiSecurityType[] values = values();

    public static WifiSecurityType fromInteger(int i) {
        if (i >= values.length) {
            return null;
        }
        return values[i];
    }
}
