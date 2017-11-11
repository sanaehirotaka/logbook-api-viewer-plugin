package logbook.plugin.apiviewer.data;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class APIContainerWrappers {

    private static APIContainerWrappers collection = new APIContainerWrappers();

    private ObservableList<APIContainerWrapper> list = FXCollections.observableArrayList();

    public ObservableList<APIContainerWrapper> getList() {
        return this.list;
    }

    public void add(APIContainerWrapper api) {
        Platform.runLater(() -> {
            this.list.add(api);
            if (this.list.size() > 100)
                this.list.remove(0);
        });
    }

    public static APIContainerWrappers get() {
        return collection;
    }
}
