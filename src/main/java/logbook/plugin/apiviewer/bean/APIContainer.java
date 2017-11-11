package logbook.plugin.apiviewer.bean;

import java.io.Serializable;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class APIContainer implements Serializable {

    private String date;

    private String time;

    private String uri;

    private byte[] requestRaw;

    private String requestPretty;

    private byte[] responseRaw;

    private String responsePretty;

    @Override
    public String toString() {
        return "[" + this.time + "] " + this.uri;
    }
}
