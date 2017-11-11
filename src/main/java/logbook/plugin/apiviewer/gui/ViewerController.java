package logbook.plugin.apiviewer.gui;

import java.net.URL;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import javafx.collections.FXCollections;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import logbook.api.API;
import logbook.api.APIListenerSpi;
import logbook.internal.gui.WindowController;
import logbook.plugin.PluginContainer;
import logbook.plugin.PluginServices;
import logbook.plugin.apiviewer.bean.APIContainer;
import logbook.plugin.apiviewer.data.APIContainerWrapper;
import logbook.plugin.apiviewer.data.APIContainerWrappers;

public class ViewerController extends WindowController {

    @FXML
    private ListView<APIContainerWrapper> list;

    @FXML
    private Label date;

    @FXML
    private Label uri;

    @FXML
    private Label listener;

    @FXML
    private TextArea response;

    @FXML
    private TextArea request;

    private APIContainer api;

    private List<Class<?>> listenerClass = PluginServices.instances(APIListenerSpi.class)
            .map(Object::getClass)
            .filter(cls -> cls.getAnnotation(API.class) != null)
            .collect(Collectors.toList());

    @FXML
    void initialize() {
        this.list.setItems(new SortedList<>(APIContainerWrappers.get().getList(),
                Comparator.comparing(APIContainerWrapper::getDate)
                        .thenComparing(APIContainerWrapper::getTime)
                        .reversed()));
        this.list.getSelectionModel().selectedItemProperty().addListener((ob, o, n) -> {
            this.set(n);
        });
    }

    @FXML
    void clear(ActionEvent event) {
        APIContainerWrappers.get().getList().clear();
    }

    @FXML
    void openFirebug(ActionEvent event) {
        if (this.api != null) {
            try {
                Stage stage = new Stage();
                URL url = ViewerMenu.class.getClassLoader()
                        .getResource("apiviewer/gui/firebug.fxml");
                FXMLLoader loader = new FXMLLoader(url);
                loader.setClassLoader(PluginContainer.getInstance().getClassLoader());
                Parent root = loader.load();
                stage.setScene(new Scene(root));
                FirebugController controller = loader.getController();
                controller.setWindow(stage);
                controller.setJson(this.api.getResponsePretty());
                stage.initOwner(this.getWindow());
                stage.setTitle(this.api.toString());
                stage.show();
            } catch (Exception e) {
            }
        }
    }

    @FXML
    void setPrettyRequest(ActionEvent event) {
        this.request.setText(Optional.ofNullable(this.api)
                .map(APIContainer::getRequestPretty)
                .orElse(""));
    }

    @FXML
    void setPrettyResponse(ActionEvent event) {
        this.response.setText(Optional.ofNullable(this.api)
                .map(APIContainer::getResponsePretty)
                .orElse(""));
    }

    @FXML
    void setRawRequest(ActionEvent event) {
        this.request.setText(Optional.ofNullable(this.api)
                .map(APIContainer::getRequestRaw)
                .map(String::new)
                .orElse(""));
    }

    @FXML
    void setRawResponse(ActionEvent event) {
        this.response.setText(Optional.ofNullable(this.api)
                .map(APIContainer::getResponseRaw)
                .map(String::new)
                .orElse(""));
    }

    private void set(APIContainerWrapper wrap) {
        if (wrap != null) {
            this.api = wrap.unwrap();
            this.date.setText(this.api.getDate() + " " + this.api.getTime());
            this.uri.setText(this.api.getUri());
            this.request.setText(this.api.getRequestPretty());
            this.response.setText(this.api.getResponsePretty());

            String listenerText = this.listenerClass.stream()
                    .map(cls -> {
                        API a = cls.getAnnotation(API.class);
                        if (Arrays.asList(a.value()).indexOf(this.api.getUri()) != -1) {
                            return cls.getCanonicalName();
                        }
                        return null;
                    })
                    .filter(Objects::nonNull)
                    .collect(Collectors.joining(", "));
            this.listener.setText(listenerText);
        } else {
            this.api = null;
            this.date.setText("");
            this.uri.setText("");
            this.request.setText("");
            this.response.setText("");
            this.listener.setText("");
        }
    }

    @Override
    public void setWindow(Stage window) {
        super.setWindow(window);
        window.addEventHandler(WindowEvent.WINDOW_HIDDEN, this::onclose);
    }

    /**
     * ウインドウを閉じる時のアクション
     *
     * @param event WindowEvent
     */
    private void onclose(WindowEvent event) {
        this.list.setItems(FXCollections.observableArrayList());
    }
}
