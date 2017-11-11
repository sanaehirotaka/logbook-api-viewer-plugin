package logbook.plugin.apiviewer.api;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.Optional;

import javax.json.JsonObject;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;

import logbook.api.APIListenerSpi;
import logbook.internal.LoggerHolder;
import logbook.plugin.apiviewer.bean.APIContainer;
import logbook.plugin.apiviewer.data.APIContainerWrapper;
import logbook.plugin.apiviewer.data.APIContainerWrappers;
import logbook.proxy.RequestMetaData;
import logbook.proxy.ResponseMetaData;

public class APIListener implements APIListenerSpi {

    private ObjectMapper mapper = new ObjectMapper();

    private ObjectWriter writer = mapper.writerWithDefaultPrettyPrinter();

    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    private static final DateTimeFormatter TIME_FORMAT = DateTimeFormatter.ofPattern("HH:mm:ss.SSS");

    @Override
    public void accept(JsonObject obj, RequestMetaData req, ResponseMetaData res) {
        try {
            ZonedDateTime now = ZonedDateTime.now();

            APIContainerWrappers.get().add(
                    new APIContainerWrapper(APIContainer.builder()
                            .date(DATE_FORMAT.format(now))
                            .time(TIME_FORMAT.format(now))
                            .uri(req.getRequestURI())
                            .requestRaw(readFully(req.getRequestBody()))
                            .requestPretty(writer.writeValueAsString(req.getParameterMap()))
                            .responseRaw(readFully(res.getResponseBody()))
                            .responsePretty(writer.writeValueAsString(mapper.readValue(obj.toString(), Map.class)))
                            .build()));
        } catch (Exception e) {
            LoggerHolder.get().warn("APIビューアーでAPIが読み取れませんでした", e);
        }
    }

    private static byte[] readFully(Optional<InputStream> in) {
        if (in.isPresent()) {
            byte[] buf = new byte[1024];
            int len;
            try (InputStream is = in.get()) {
                is.reset();
                ByteArrayOutputStream out = new ByteArrayOutputStream();
                while ((len = is.read(buf)) > 0) {
                    out.write(buf, 0, len);
                }
                return out.toByteArray();
            } catch (Exception e) {
                // NOP
            }
        }
        return new byte[0];
    }
}
