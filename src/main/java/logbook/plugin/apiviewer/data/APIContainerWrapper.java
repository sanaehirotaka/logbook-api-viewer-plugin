package logbook.plugin.apiviewer.data;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.zip.DeflaterOutputStream;
import java.util.zip.InflaterInputStream;

import logbook.plugin.apiviewer.bean.APIContainer;
import lombok.Getter;

public class APIContainerWrapper {

    @Getter
    private String date;

    @Getter
    private String time;

    @Getter
    private String uri;

    private byte[] container;

    public APIContainerWrapper(APIContainer container) {
        this.date = container.getDate();
        this.time = container.getTime();
        this.uri = container.getUri();
        ByteArrayOutputStream base = new ByteArrayOutputStream();
        try (DeflaterOutputStream zout = new DeflaterOutputStream(base)) {
            try (ObjectOutputStream oout = new ObjectOutputStream(zout)) {
                oout.writeObject(container);
            }
        } catch (Exception e) {
            // NOP
        }
        this.container = base.toByteArray();
    }

    public APIContainer unwrap() {
        ByteArrayInputStream base = new ByteArrayInputStream(this.container);
        try (InflaterInputStream zin = new InflaterInputStream(base)) {
            try (ObjectInputStream oin = new ObjectInputStream(zin)) {
                return (APIContainer) oin.readObject();
            }
        } catch (Exception e) {
            // NOP
        }
        return null;
    }

    @Override
    public String toString() {
        return "[" + this.time + "] " + this.uri;
    }
}
