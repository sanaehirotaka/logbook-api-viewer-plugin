package logbook.plugin.apiviewer.gui;

import java.net.URL;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.MenuItem;
import javafx.stage.Stage;
import logbook.internal.LoggerHolder;
import logbook.internal.gui.Tools;
import logbook.internal.gui.WindowController;
import logbook.plugin.PluginContainer;
import logbook.plugin.gui.MainExtMenu;

public class ViewerMenu implements MainExtMenu {

    @Override
    public MenuItem getContent() {
        MenuItem menu = new MenuItem("APIビューアー");
        menu.setOnAction(e -> {
            try {
                Stage stage = new Stage();
                URL url = ViewerMenu.class.getClassLoader()
                        .getResource("apiviewer/gui/viewer.fxml");
                FXMLLoader loader = new FXMLLoader(url);
                loader.setClassLoader(PluginContainer.getInstance().getClassLoader());
                Parent root = loader.load();
                stage.setScene(new Scene(root));
                WindowController controller = loader.getController();
                controller.setWindow(stage);

                stage.initOwner(menu.getParentPopup().getOwnerWindow());
                stage.setTitle("APIビューアー");
                Tools.Windows.setIcon(stage);
                Tools.Windows.defaultCloseAction(controller);
                Tools.Windows.defaultOpenAction(controller);
                stage.show();
            } catch (Exception ex) {
                LoggerHolder.get().warn("APIビューアーを開けませんでした", ex);
            }
        });
        return menu;
    }

}
