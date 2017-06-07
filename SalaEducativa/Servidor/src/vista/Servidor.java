package vista;

import com.teamdev.jxbrowser.chromium.Browser;
import com.teamdev.jxbrowser.chromium.BrowserCore;
import com.teamdev.jxbrowser.chromium.internal.Environment;
import com.teamdev.jxbrowser.chromium.javafx.BrowserView;
import controladores.InicioController;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

public class Servidor extends Application {
    @Override
    public void init() throws Exception {
        //super.init();
        // On Mac OS X Chromium engine must be initialized in non-UI thread.
        if (Environment.isMac()) {
            BrowserCore.initialize();
        }
    }

    @Override
    public void start(Stage primaryStage) throws Exception{
        System.setProperty("java.security.policy","file:///users/macbookpro/server.policy");
        System.setProperty("java.rmi.server.codebase", "file:///Users/macbookpro/UV/Desarrollo de sistemas en red/codigo/___proyecto final/SalaEducativa/out/production/");

        if (System.getSecurityManager() == null) {
            System.setSecurityManager(new SecurityManager());
        }

        Browser browser = new Browser();
        BrowserView browserView = new BrowserView(browser);
        StackPane pane = new StackPane();
        pane.getChildren().add(browserView);
        Scene scene = new Scene(pane, 500, 400);

        Stage videoStage = new Stage();
        videoStage.setTitle("Webcam");
        videoStage.setScene(scene);

        videoStage.setAlwaysOnTop(true);
        videoStage.setOnCloseRequest(event -> {
            Platform.exit();
            System.exit(0);
        });

        browser.loadURL("http://localhost:8080/demos/demo_multiparty.html");


        //Parent root = FXMLLoader.load(getClass().getResource("resources/inicio.fxml"));
        FXMLLoader cargador = new FXMLLoader(getClass().getClassLoader().getResource("vista/resources/inicio.fxml"));
        AnchorPane root = cargador.load();

        InicioController controller = (InicioController) cargador.getController();
        controller.setVideoStage(videoStage);
        controller.setBrowser(browser);

        primaryStage.setTitle("Inicio");
        primaryStage.setResizable(false);
        primaryStage.setScene(new Scene(root, 500, 357));
        primaryStage.show();

    }


    public static void main(String[] args) {
        launch(args);
    }
}
