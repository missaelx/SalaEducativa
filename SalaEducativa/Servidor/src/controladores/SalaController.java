package controladores;

import com.healthmarketscience.rmiio.SimpleRemoteInputStream;
import com.teamdev.jxbrowser.chromium.Browser;
import com.teamdev.jxbrowser.chromium.BrowserCore;
import com.teamdev.jxbrowser.chromium.internal.Environment;
import com.teamdev.jxbrowser.chromium.javafx.BrowserView;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import modelo.Participacion;
import org.apache.pdfbox.PDFToImage;
import rmixp.ClienteRMI;
import rmixp.LineaDibujada;
import rmixp.ServidorRMI;


import java.io.*;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.rmi.AccessException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.*;

public class SalaController implements Initializable, ServidorRMI {

    @FXML
    private TextArea txtareaChat;
    @FXML
    private TextField txtMensaje;
    @FXML
    private ListView<String> listviewUsuariosConectados;
    @FXML
    private TableView<Participacion> tableParticipacion;
    @FXML
    private TableColumn colNombre, colMensaje;
    @FXML
    private ImageView imageviewDiapositiva;
    @FXML
    private Label lbPaginacion;
    @FXML
    private WebView webviewCamara;




    private String urlBroadcast; //String URL de broadcast
    private String claveGrupo;
    private String nombre;

    private Stage videoStage;
    private Browser browser;

    private String pathPdfDiapositivas = "";
    private List<String> usuariosConectados = new ArrayList<>();
    private List<Participacion> participaciones = new ArrayList<>();

    private static String pathImagenes = System.getProperty("user.dir") + "/archivos/img/";
    private List<Image> imagenesPdf = new ArrayList<>();
    private int currentIndexImagenes;

    public Browser getBrowser() {
        return browser;
    }

    public void setBrowser(Browser browser) {
        this.browser = browser;
        browser.loadURL(urlBroadcast);
        videoStage.show();
    }

    public Stage getVideoStage() {
        return videoStage;
    }

    public void setVideoStage(Stage videoStage) {
        this.videoStage = videoStage;
    }

    public String getClaveGrupo() {
        return claveGrupo;
    }

    public void setClaveGrupo(String claveGrupo) {
        this.claveGrupo = claveGrupo;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getUrlBroadcast() {
        return urlBroadcast;
    }

    public void setUrlBroadcast(String urlBroadcast) {
        this.urlBroadcast = urlBroadcast;
    }






    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }

    public void iniciar() {
        try {
            //WebCamController.main(urlBroadcast);


            // Instantiating the implementation class
            SalaController obj = this;

            // Exporting the object of implementation class
            // (here we are exporting the remote object to the stub)
            ServidorRMI stub = (ServidorRMI) UnicastRemoteObject.exportObject(obj, 0);

            // Binding the remote object (stub) in the registry
            Registry registry = LocateRegistry.getRegistry();

            registry.bind("Servidor", stub);
            System.err.println("Server ready");

        } catch (Exception e) {
            System.err.println("Server exception: " + e.toString());
            e.printStackTrace();
        }
    }

    @Override
    public void conectar(String claveGrupo, String nombre) throws RemoteException {
        if(!claveGrupo.equals(this.claveGrupo)){
            try {
                //buscamos el objeto del cliente para sacarlos
                Registry registry = LocateRegistry.getRegistry();
                ClienteRMI cliente = (ClienteRMI) registry.lookup(nombre);
                cliente.denegarEntrada();
            } catch (NotBoundException e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("Se ha conectado: " + nombre);
            usuariosConectados.add(nombre);
            for (String user: usuariosConectados){
                try {
                    Registry registry = LocateRegistry.getRegistry();
                    ClienteRMI cliente = null;
                    cliente = (ClienteRMI) registry.lookup(user);
                    cliente.actualizarListaUsuariosConectados(usuariosConectados);
                } catch (NotBoundException e) {
                    e.printStackTrace();
                }
            }
            Platform.runLater(() -> actualizarUsuariosConectados());

        }
    }

    @Override
    public void desconectar(String nombre) throws RemoteException {

    }

    @Override
    public void getPDFDiapositiva(String nombreUsuario) throws RemoteException {

        try {
            Registry registry = LocateRegistry.getRegistry();
            ClienteRMI cliente = null;
            cliente = (ClienteRMI) registry.lookup(nombre);
            if(pathPdfDiapositivas.equals("")) {
                //buscamos el objeto del cliente para sacarlos
                cliente.denegarPDF();
            } else {
                Path path = Paths.get(pathPdfDiapositivas);
                byte[] data = Files.readAllBytes(path);
                cliente.sendPDFDiapositiva(data, currentIndexImagenes);
            }
        } catch (NotBoundException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void enviarMensaje(String mensaje, String cliente) throws RemoteException {
        String mensajeFinal = "<" + cliente + "> " + mensaje;
        for (String nombreUsuario : usuariosConectados){
            Registry registry = LocateRegistry.getRegistry();
            ClienteRMI clienteRemoto = null;
            try {
                clienteRemoto = (ClienteRMI) registry.lookup(nombreUsuario);
                clienteRemoto.actualizarChat(mensajeFinal);
            } catch (NotBoundException e) {
                e.printStackTrace();
            }
        }
        actualizarChat(mensajeFinal);
    }

    @Override
    public void enviarParticipacion(String mensaje, String cliente) throws RemoteException {
        Participacion p = new Participacion(cliente, mensaje);
        participaciones.add(p);
        setTable();
    }


    public void actualizarChat(String mensaje){
        txtareaChat.setText(txtareaChat.getText() + mensaje + "\n");
    }
    public void actualizarUsuariosConectados(){
        listviewUsuariosConectados.getItems().clear();
        listviewUsuariosConectados.getItems().addAll(usuariosConectados);
    }
    public void setTable(){
        tableParticipacion.refresh();
        ObservableList lista = FXCollections.observableArrayList(participaciones);
        colNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        colMensaje.setCellValueFactory(new PropertyValueFactory<>("mensaje"));
        tableParticipacion.setItems(lista);
    }

    @FXML
    public void onCargarPdfClick(ActionEvent e){
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Selecciona el PDF con las diapositivas");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Archivos PDF", "*.pdf"));
        File file = fileChooser.showOpenDialog(((Node)e.getSource()).getScene().getWindow());
        if(file != null){
            String pathFinal = System.getProperty("user.dir") + "/archivos" + "/diapositiva.pdf";
            try {
                Files.copy(file.toPath(), Paths.get(pathFinal), StandardCopyOption.REPLACE_EXISTING);
            } catch (IOException e1) {
                System.out.println("Error al importar PDF");
            }
            pathPdfDiapositivas = pathFinal;

            //creamos las imagenes
            File jpegDir = new File(pathImagenes);
            File pdfCopiado = new File(pathFinal);
            try {
                //convertimos el PDF a imagen
                final File   target   = new File( jpegDir, pdfCopiado.getName());
                final String trgtPath = target.getPath();
                final String prefix   = trgtPath.substring( 0, trgtPath.lastIndexOf( '/' )) + "/";
                PDFToImage.main( new String[]{ "-outputPrefix", prefix, pdfCopiado.getPath() });

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

                mostrarImagen();

            }
            catch( final Throwable t ) {
                System.err.println( file.getPath());
                t.printStackTrace();
            }

        }

    }

    private void mostrarImagen() {
        imageviewDiapositiva.setImage(imagenesPdf.get(currentIndexImagenes));
        lbPaginacion.setText(currentIndexImagenes+1 + "/" + imagenesPdf.size());
    }

    private void notificarCambioIndiceAUsuarios(){
        for (String user: usuariosConectados){
            try {
                Registry registry = LocateRegistry.getRegistry();
                ClienteRMI cliente = null;
                cliente = (ClienteRMI) registry.lookup(user);
                cliente.actualizarInidiceRemotoPdf(currentIndexImagenes);
            } catch (Exception ex) {
                System.out.println(ex.getMessage());
            }
        }
    }

    @FXML
    public void onEnviarMensajeMaestroClick(ActionEvent e){
        String cliente = "Maestro " + nombre;
        String mensaje = txtMensaje.getText();
        String mensajeFinal = "\n----------------------------\n"+
                "<" + cliente + "> " + mensaje + "\n" +
                "----------------------------\n";
        for (String nombreUsuario : usuariosConectados){
            Registry registry = null;
            try {
                registry = LocateRegistry.getRegistry();
                ClienteRMI clienteRemoto = null;
                clienteRemoto = (ClienteRMI) registry.lookup(nombreUsuario);
                clienteRemoto.actualizarChat(mensajeFinal);
            } catch (NotBoundException | RemoteException en) {
                en.printStackTrace();
            }
        }
        actualizarChat(mensajeFinal);
        txtMensaje.setText("");
    }
    @FXML
    public void onDenegarClick(ActionEvent e){
        Participacion p = tableParticipacion.getSelectionModel().getSelectedItem();
        if(p == null){
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Selecciona una participación");
            alert.setHeaderText("No has seleccionado ninguna participación");
            alert.setContentText("Selecciona una participación para continuar");
            alert.show();
        } else {
            participaciones.remove(tableParticipacion.getSelectionModel().getSelectedIndex());
            setTable();
        }
    }
    @FXML
    public void onPermitirClick(ActionEvent e){
        Participacion p = tableParticipacion.getSelectionModel().getSelectedItem();
        if(p == null){
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Selecciona una participación");
            alert.setHeaderText("No has seleccionado ninguna participación");
            alert.setContentText("Selecciona una participación para continuar");
            alert.show();
        } else {
            String mensajeFinal = "\n******************************\nPARTICIPACIÓN\n" +
                    "<" + p.getNombre() + "> " + p.getMensaje() + "\n" +
                    "******************************\n";
            participaciones.remove(tableParticipacion.getSelectionModel().getSelectedIndex());
            setTable();
            for (String nombreUsuario : usuariosConectados){
                Registry registry = null;
                try {

                    registry = LocateRegistry.getRegistry();
                    ClienteRMI clienteRemoto = null;
                    clienteRemoto = (ClienteRMI) registry.lookup(nombreUsuario);
                    clienteRemoto.actualizarChat(mensajeFinal);
                } catch (NotBoundException | RemoteException en) {
                    en.printStackTrace();
                }
            }
            actualizarChat(mensajeFinal);
        }
    }
    @FXML
    public void onNextSlideClick(ActionEvent e){
        if(currentIndexImagenes < imagenesPdf.size()-1){
            currentIndexImagenes++;
            mostrarImagen();
            notificarCambioIndiceAUsuarios();
        }
    }
    @FXML
    public void onPrevSlideClick(ActionEvent e){
        if (currentIndexImagenes > 0){
            currentIndexImagenes--;
            mostrarImagen();
            notificarCambioIndiceAUsuarios();
        }
    }
    @FXML
    public void onSenalarDiapositivaClick(ActionEvent e){
        Stage stage = new Stage();
        stage.setTitle("Dibuja sobre la imagen");
        ImageView iv = new ImageView();

        int indiceAEditar = currentIndexImagenes;

        Image image = imagenesPdf.get(indiceAEditar);
        iv.setImage(image);

        //vBox.getChildren().addAll(iv);

        //Scene scene = new Scene(vBox, image.getWidth(), image.getHeight());


        AnchorPane anchorRoot = new AnchorPane();
        final double[] initX = new double[1];
        final double[] initY = new double[1];
        final double maxX = iv.getImage().getWidth();
        final double maxY = iv.getImage().getHeight();

        anchorRoot.getChildren().add(iv);

        List<LineaDibujada> lineasDibujadas = new ArrayList<>();
        List<Line> lines = new ArrayList<>();


        iv.setOnMousePressed(me -> {
            //System.out.println("Clicked, x:" + me.getSceneX() + " y:" + me.getSceneY());
            //the event will be passed only to the circle which is on front
            initX[0] = me.getSceneX();
            initY[0] = me.getSceneY();
            me.consume();
        });
        iv.setOnMouseDragged(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent me) {
                //System.out.println("Dragged, x:" + me.getSceneX() + " y:" + me.getSceneY());
                if (me.getSceneX() < maxX && me.getSceneY() < maxY) {
                    Line line = new Line(initX[0], initY[0], me.getSceneX(), me.getSceneY());
                    LineaDibujada lineaDibujada = new LineaDibujada(initX[0], initY[0], me.getSceneX(), me.getSceneY());
                    line.setFill(null);
                    line.setStroke(Color.RED);
                    line.setStrokeWidth(2);
                    anchorRoot.getChildren().add(line);

                    lines.add(line);
                    lineasDibujadas.add(lineaDibujada);
                }

                initX[0] = me.getSceneX() > maxX ? maxX : me.getSceneX();
                initY[0] = me.getSceneY() > maxY ? maxY : me.getSceneY();
            }
        });

        VBox vBox = new VBox();
        vBox.setAlignment(Pos.CENTER);
        Button btnBorrar = new Button("Borrar"), btnEnviar = new Button("Enviar anotaciones");

        btnBorrar.setOnAction(event -> {
            for(Line l: lines){
                anchorRoot.getChildren().remove(l);
            }
            lines.clear();
            lineasDibujadas.clear();
        });

        btnEnviar.setOnAction(event -> {
            for (String user: usuariosConectados){
                try {
                    Registry registry = LocateRegistry.getRegistry();
                    ClienteRMI cliente = null;
                    cliente = (ClienteRMI) registry.lookup(user);
                    cliente.enviarAnotacionesDiapositiva(indiceAEditar, lineasDibujadas);
                } catch (Exception ex) {
                    System.out.println(ex.getMessage());
                }
            }
        });

        vBox.getChildren().addAll(anchorRoot, btnBorrar, btnEnviar);

        Scene scene = new Scene(vBox, image.getWidth(), image.getHeight() + btnBorrar.getHeight() + btnEnviar.getHeight() + 70);

        stage.setScene(scene);
        stage.setResizable(false);
        stage.show();

    }
}