package logbook.plugin.apiviewer.gui;

import javafx.concurrent.Worker;
import javafx.fxml.FXML;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import logbook.internal.gui.WindowController;
import lombok.Setter;
import netscape.javascript.JSObject;

public class FirebugController extends WindowController {

    @FXML
    private WebView webView;

    @Setter
    private String json;

    @FXML
    void initialize() {
        System.out.println("initialize()");

        WebEngine engine = webView.getEngine();
        engine.loadContent("<html debug='true'><head>"
                + "<script type=\"text/javascript\" src=\"https://getfirebug.com/firebug-lite.js\"></script>"
                + "</head><body></body></html>");
        engine.getLoadWorker().stateProperty().addListener((ov, o, n) -> {
            if (n == Worker.State.SUCCEEDED) {
                JSObject jsobj = (JSObject) ((JSObject) engine.executeScript("JSON")).call("parse", this.json);
                ((JSObject) engine.executeScript("window")).setMember("json", jsobj);
                ((JSObject) engine.executeScript("console")).call("log",
                        "応答のJSONは変数jsonに割り当てられています、確認するには'json'とコンソールにタイプします");
                engine.executeScript("console.dir(json)");
            }
        });
    }
}
