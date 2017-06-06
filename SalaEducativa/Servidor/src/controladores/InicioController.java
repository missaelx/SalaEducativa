package controladores;

import com.teamdev.jxbrowser.chromium.Browser;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.*;
import java.util.ResourceBundle;

public class InicioController implements Initializable {
    @FXML
    private TextField txtUrl, txtClave, txtNombre;

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
        String urlStreaming = txtUrl.getText();
        String claveGrupo = txtClave.getText();
        String nombre = txtNombre.getText();

        if(!verificarDatos(urlStreaming)){
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error en la verificación de datos");
            alert.setHeaderText("Alguno de los datos que has ingresado no son correctos");
            alert.setContentText("Revisa la dirección del streaming antes de continuar");
            alert.show();
            return;
        }


        ((Node)e.getSource()).getScene().getWindow().hide();


        FXMLLoader cargador = new FXMLLoader(getClass().getClassLoader().getResource("vista/resources/sala.fxml"));
        try {
            AnchorPane root = cargador.load();

            SalaController controladorSala = (SalaController) cargador.getController();
            controladorSala.setClaveGrupo(claveGrupo);
            controladorSala.setNombre(nombre);
            controladorSala.setUrlBroadcast(urlStreaming);
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
            System.out.println("vista/resources/sala.fxml");
        }


    }

    private boolean verificarDatos(String url) {
        boolean res = pingHost(url, 10000);

        return true;
    }

    public static boolean pingHost(String url, int timeout) {
        HttpURLConnection connection = null;
        try {
            URL u = new URL(url);
            connection = (HttpURLConnection) u.openConnection();
            connection.setRequestMethod("HEAD");
            int responseCode = connection.getResponseCode();
            return (200 <= responseCode && responseCode <= 399);
            // You can determine on HTTP return code received. 200 is success.
        } catch (MalformedURLException e) {
            return false;
        } catch (IOException e) {
            return false;
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
            return false;
        }
    }
}
