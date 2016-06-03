package cn.edu.hit.project.ec.models.base;

public enum DataScale {
    MONTHLY, DAILY, HOURLY, SENSOR;

    private final static DataScale[] values = values();

    public static DataScale fromInteger(int i) {
        if (i >= values.length) {
            return null;
        }
        return values[i];
    }
}
