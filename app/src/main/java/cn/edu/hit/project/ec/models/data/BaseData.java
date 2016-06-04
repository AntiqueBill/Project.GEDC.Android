package cn.edu.hit.project.ec.models.data;

import java.util.Date;

public interface BaseData {
    int getId();

    double getBpm();

    double getIbi();

    double getTem();

    Date getTimestamp();

    long getSessionId();

    void setSessionId(long sessionId);
}
