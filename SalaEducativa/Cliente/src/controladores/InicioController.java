package controladores;

import com.teamdev.jxbrowser.chromium.Browser;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;


import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

/**
 * Missael Hernández on 01/06/17.
 */
public class InicioController implements Initializable {
    @FXML
    private TextField txtIP, txtClave, txtNombre;

    private Stage videoStage;
    private Browser browser;

    public Browser getBrowser() {
        return browser;
    }

    public void setBrowser(Browser browser) {
        this.browser = browser;
    }

    public Stage getVideoStage() {
        return videoStage;
    }

    public void setVideoStage(Stage videoStage) {
        this.videoStage = videoStage;
    }


    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }

    @FXML
    public void onClickIniciarSesion(ActionEvent e){
        String ip = txtIP.getText();
        String clave = txtClave.getText();
        String nombre = txtNombre.getText();

        ((Node)e.getSource()).getScene().getWindow().hide();


        FXMLLoader cargador = new FXMLLoader(getClass().getClassLoader().getResource("vista/resources/sala.fxml"));
        try {
            AnchorPane root = cargador.load();

            SalaControllerCliente controladorSala = (SalaControllerCliente) cargador.getController();
            controladorSala.setIpMaestro(ip);
            controladorSala.setClaveGrupo(clave);
            controladorSala.setNombreUsuario(nombre);
            controladorSala.setVideoStage(this.videoStage);
            controladorSala.setBrowser(this.browser);

            controladorSala.iniciar();

            Stage stage = new Stage();
            stage.setTitle("Sala educativa");

            Scene escena = new Scene(root);
            stage.setScene(escena);
            stage.setResizable(false);
            stage.show();

            stage.setOnHidden(event -> {
                Platform.exit();
                System.exit(0);
            });
        } catch (IOException e1) {
            e1.printStackTrace();
        }
    }
}
