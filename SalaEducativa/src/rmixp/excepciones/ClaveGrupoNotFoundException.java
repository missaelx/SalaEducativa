package rmixp.excepciones;

/**
 * Created by macbookpro on 06/06/17.
 */
public class ClaveGrupoNotFoundException extends Exception{
    public ClaveGrupoNotFoundException() {
    }

    public ClaveGrupoNotFoundException(String message) {
        super(message);
    }

    public ClaveGrupoNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public ClaveGrupoNotFoundException(Throwable cause) {
        super(cause);
    }

    public ClaveGrupoNotFoundException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
