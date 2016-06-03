package cn.edu.hit.project.ec.models.base;

public enum DataType {
    BPM, TEM;

    private final static DataType[] values = values();

    public static DataType fromInteger(int i) {
        if (i >= values.length) {
            return null;
        }
        return values[i];
    }
}
