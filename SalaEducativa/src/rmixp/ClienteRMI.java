package rmixp;

import javafx.scene.shape.Line;

import java.io.IOException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

/**
 * Creado por Missael el 01/06/17.
 */
public interface ClienteRMI extends Remote {
    void saludar(String saludo) throws RemoteException;

    /**
     * Se denega la entrada cuando la clave del salon no es la correcta
     */
    void denegarEntrada() throws RemoteException;

    /**
     * Se denega el PDF si es que el maestro aun no ha seleccionado un archivo
     * @throws RemoteException Cuando ocurre un error en el servidor
     */
    void denegarPDF() throws RemoteException;
    /**
     * Envia la diapositiva en formato PDF del servidor al cliente
     * @param data Representación en bytes del PDF
     * @param indiceActual Representa el indice que tiene el maestro actualmente mostrando
     * @throws RemoteException En caso de error de conexión
     * @throws IOException En caso que no haya encontrado el archivo
     */
    void sendPDFDiapositiva(byte[] data, int indiceActual) throws RemoteException, IOException;

    /**
     * Actualiza el número que se esta mostrando actualmente por el maestro
     * @param indiceRemoto El indice del maestro
     * @throws RemoteException
     */
    void actualizarInidiceRemotoPdf(int indiceRemoto) throws  RemoteException;

    /**
     * Actualiza el area de mensajes del chat
     * @param mensaje El mensaje a escribir en el area
     * @throws RemoteException
     */
    void actualizarChat(String mensaje) throws RemoteException;

    /**
     * Actualiza la lista de usuarios conectados
     * @param usuarios La lista de usuario conectados
     * @throws RemoteException
     */
    void actualizarListaUsuariosConectados(List<String> usuarios) throws RemoteException;

    void enviarAnotacionesDiapositiva(int indiceImagen, List<LineaDibujada> lineas) throws RemoteException;
}
