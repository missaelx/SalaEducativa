package controladores;

import com.teamdev.jxbrowser.chromium.Browser;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import org.apache.pdfbox.PDFToImage;
import rmixp.ClienteRMI;
import rmixp.LineaDibujada;
import rmixp.ServidorRMI;
import rmixp.excepciones.ClaveGrupoNotFoundException;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.rmi.AlreadyBoundException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

/**
 * Missael on 01/06/17.
 */
public class SalaControllerCliente implements Initializable, ClienteRMI {

    @FXML
    private WebView webView;
    @FXML
    private ImageView imgDiapositiva;
    @FXML
    private Label lbDiapositivaActual, lbNavegacionDiapositivas;
    @FXML
    private TextArea txtareaConversacion;
    @FXML
    private ListView<String> listViewUsuariosConectados;
    @FXML
    private TextField txtMensaje;
    @FXML
    private Button btnSolicitarPDF;



    private String nombreUsuario, ipMaestro, claveGrupo;
    private ServidorRMI servidor;
    private String pathPDF = System.getProperty("user.dir") + "/archivosAlumno/diapositivaAlumno.pdf";

    private static String pathImagenes = System.getProperty("user.dir") + "/archivosAlumno/imgalumno/";
    private List<Image> imagenesPdf = new ArrayList<>();
    private int currentIndexImagenes;
    private int remoteIndexImagenes;

    private Stage videoStage;
    private Browser browser;



    //---------------------     METODOS     ------------------------------------------------------------------------------

    public Browser getBrowser() {
        return browser;
    }

    public void setBrowser(Browser browser) {
        this.browser = browser;
        //browser.loadURL(urlBroadcast);
        browser.loadURL("http://"+ipMaestro+":5080/demos/simpleSubscriber.html");
        videoStage.show();
    }

    public Stage getVideoStage() {
        return videoStage;
    }

    public void setVideoStage(Stage videoStage) {
        this.videoStage = videoStage;
    }


    public String getNombreUsuario() {
        return nombreUsuario;
    }

    public void setNombreUsuario(String nombreUsuario) {
        this.nombreUsuario = nombreUsuario;
    }

    public String getIpMaestro() {
        return ipMaestro;
    }

    public void setIpMaestro(String ipMaestro) {
        this.ipMaestro = ipMaestro;
    }

    public String getClaveGrupo() {
        return claveGrupo;
    }

    public void setClaveGrupo(String claveGrupo) {
        this.claveGrupo = claveGrupo;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }

    public void iniciar() {

        try {

            //exportamos el objeto propio
            SalaControllerCliente salaControllerCliente = this;
            ClienteRMI clienteRMI = (ClienteRMI) UnicastRemoteObject.exportObject(salaControllerCliente, 0);

            Registry registro = LocateRegistry.getRegistry(ipMaestro);
            //error
            //registro.bind(nombreUsuario, clienteRMI);
            //cargamos el stub del servidor
            servidor = (ServidorRMI) registro.lookup("Servidor");


            // nos logueamos en el sistema
            servidor.conectar(claveGrupo, nombreUsuario, clienteRMI);
        } catch (RemoteException e) {
            e.printStackTrace();
        } catch (AlreadyBoundException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Nombre ya usado");
            alert.setHeaderText("El nombre que escogiste ya ha sido usado");
            alert.setContentText("Selecciona otro nombre e intentálo de nuevo");
            alert.showAndWait();
            e.printStackTrace();
            Platform.exit();
            System.exit(-1);
        } catch (NotBoundException e) {
            e.printStackTrace();
        } catch (ClaveGrupoNotFoundException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Nombre de clase no valido");
            alert.setHeaderText("El nombre que escogiste no fue encontrado");
            alert.setContentText("Selecciona otro nombre e intentálo de nuevo");
            alert.showAndWait();
            e.printStackTrace();
            Platform.exit();
            System.exit(-1);
        }


    }

    @Override
    public void saludar(String saludo) throws RemoteException {
        System.out.println("Me han saludado Joder");
    }

    @Override
    public void denegarEntrada() {
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error en el código de grupo");
            alert.setHeaderText("Error de autenticación");
            alert.setContentText("Verifica la clave del grupo");
            alert.showAndWait();
            Platform.exit();
            System.exit(-1);
        });
    }

    @Override
    public void denegarPDF() throws RemoteException {
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("No se ha encontrado el archivo");
            alert.setHeaderText("El maestro aún no ha seleccionado ningún archivo");
            alert.setContentText("Solicita el archivo vía Chat");
            alert.show();
            btnSolicitarPDF.setDisable(false);
        });
    }

    @Override
    public void sendPDFDiapositiva(byte[] data, int indiceActual) throws RemoteException, IOException {
        currentIndexImagenes = indiceActual;
        remoteIndexImagenes = indiceActual;
        Path path = Paths.get(pathPDF);
        Files.write(path, data);
        crearImagenesPDF();
    }

    @Override
    public void actualizarInidiceRemotoPdf(int indiceRemoto) throws RemoteException {

        remoteIndexImagenes = indiceRemoto;
        Platform.runLater(() -> lbDiapositivaActual.setText(indiceRemoto+1 + ""));
    }

    private void crearImagenesPDF() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                File file = new File(pathPDF);
                //creamos las imagenes
                File jpegDir = new File(pathImagenes);
                try {
                    //convertimos el PDF a imagen
                    final File   target   = new File( jpegDir, file.getName());
                    final String trgtPath = target.getPath();
                    final String prefix   = trgtPath.substring( 0, trgtPath.lastIndexOf( '/' )) + "/";
                    PDFToImage.main( new String[]{ "-outputPrefix", prefix, file.getPath() });

                    //Añadimos las imagenes al almacen
                    File imgDirectory = new File(pathImagenes);
                    File[] imagenes   = imgDirectory.listFiles( new FilenameFilter() {
                        @Override public boolean accept( File dir, String name ) {
                            return name.toLowerCase().endsWith( ".jpg" );
                        }
                    });
                    for (int i = 0; i < imagenes.length; i++){
                        Image image = new Image(imagenes[i].toURI().toString());
                        imagenesPdf.add(image);
                    }

                    Platform.runLater(new Runnable(){
                        @Override
                        public void run() {
                            mostrarImagen();
                        }
                    });
                }
                catch( final Throwable t ) {
                    System.err.println( file.getPath());
                    t.printStackTrace();
                }
            }
        }).start();
    }

    private void mostrarImagen() {
        imgDiapositiva.setImage(imagenesPdf.get(currentIndexImagenes));
        lbNavegacionDiapositivas.setText(currentIndexImagenes+1 + "/" + imagenesPdf.size());
        lbDiapositivaActual.setText(remoteIndexImagenes+1 + "");
    }

    @Override
    public void actualizarChat(String mensaje) throws RemoteException {
        Platform.runLater(() -> {
            txtareaConversacion.setText(txtareaConversacion.getText() + mensaje + "\n");
        });
    }

    @Override
    public void actualizarListaUsuariosConectados(List<String> usuarios) throws RemoteException {
        Platform.runLater(() -> {
            listViewUsuariosConectados.getItems().clear();
            listViewUsuariosConectados.getItems().addAll(usuarios);
        });
    }

    @Override
    public void enviarAnotacionesDiapositiva(int indiceImagen, List<LineaDibujada> lineas) throws RemoteException{
        if(imagenesPdf.size() == 0) return;
        Platform.runLater(() -> {
            Stage stage = new Stage();
            stage.setTitle("Anotaciones del profesor");
            ImageView iv = new ImageView();


            Image image = imagenesPdf.get(indiceImagen);
            iv.setImage(image);

            AnchorPane anchorRoot = new AnchorPane();
            anchorRoot.getChildren().add(iv);

            for(LineaDibujada linea: lineas){
                Line l = new Line(linea.getStartX(), linea.getStartY(), linea.getEndX(), linea.getEndY());
                l.setFill(null);
                l.setStroke(Color.RED);
                l.setStrokeWidth(2);
                anchorRoot.getChildren().add(l);
            }

            Scene scene = new Scene(anchorRoot, image.getWidth(), image.getHeight());
            stage.setScene(scene);
            stage.setResizable(false);
            stage.show();
        });
    }


    @FXML
    public void onEnviarMensajeClick(ActionEvent e){
        try {
            servidor.enviarMensaje(txtMensaje.getText(), nombreUsuario);
            txtMensaje.setText("");
        } catch (RemoteException e1) {
            System.out.println("Error remoto");
        }
    }
    @FXML
    public void onParticipacionClick(ActionEvent e){
        try{
            servidor.enviarParticipacion(txtMensaje.getText(), nombreUsuario);
            txtMensaje.setText("");
        } catch (RemoteException e1) {
            System.out.println("Error remoto");
        }
    }
    @FXML
    public void onSolicitarPdfClick(ActionEvent e){
        try {
            btnSolicitarPDF.setDisable(true);
            servidor.getPDFDiapositiva(nombreUsuario);
        } catch (RemoteException e1) {
            System.out.println("Error remoto");
        }
    }
    @FXML
    public void onNextSlideClick(ActionEvent e){
        if(currentIndexImagenes < imagenesPdf.size()-1){
            currentIndexImagenes++;
            mostrarImagen();
        }
    }
    @FXML
    public void onPrevSlideClick(ActionEvent e){
        if (currentIndexImagenes > 0){
            currentIndexImagenes--;
            mostrarImagen();
        }
    }

}
