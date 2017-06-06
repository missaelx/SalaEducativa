package rmixp;

import java.rmi.Remote;
import java.rmi.RemoteException;


public interface ServidorRMI extends Remote {
    void conectar(String claveGrupo, String nombre) throws RemoteException;
    void desconectar(String nombre) throws RemoteException;

    /**
     * Petición de envio de diapositivas
     * @param nombreUsuario Nombre del usuario que pide la diapositiva (para acceder a su objeto remoto)
     * @throws RemoteException
     */
    void getPDFDiapositiva(String nombreUsuario) throws RemoteException;

    /**
     * Envia un mensaje al chat general
     * @param mensaje El mensaje
     * @param cliente El nombre del cliente que envia el mensaje
     * @throws RemoteException
     */
    void enviarMensaje(String mensaje, String cliente) throws RemoteException;

    /**
     * Envia una peticion de participación al servidor, esta nunca se muestra hasta que sea aprobada por el profesor
     * @param mensaje Mensaje que se desea enviar
     * @param cliente Nombre del cliente que envia el mensaje
     * @throws RemoteException
     */
    void enviarParticipacion(String mensaje, String cliente) throws RemoteException;


}
