package rmixp;

import javafx.scene.image.Image;

import java.io.InputStream;

/**
 * Created by macbookpro on 04/06/17.
 */
public class ImageMXP extends Image {
    private String urlMXP;

    public String getUrlMXP() {
        return urlMXP;
    }

    public void setUrlMXP(String urlMXP) {
        this.urlMXP = urlMXP;
    }

    public ImageMXP(String url) {
        super(url);
        this.urlMXP = url;
    }

    public ImageMXP(String url, boolean backgroundLoading) {
        super(url, backgroundLoading);
        this.urlMXP = url;
    }

    public ImageMXP(String url, double requestedWidth, double requestedHeight, boolean preserveRatio, boolean smooth) {
        super(url, requestedWidth, requestedHeight, preserveRatio, smooth);
        this.urlMXP = url;
    }

    public ImageMXP(String url, double requestedWidth, double requestedHeight, boolean preserveRatio, boolean smooth, boolean backgroundLoading) {
        super(url, requestedWidth, requestedHeight, preserveRatio, smooth, backgroundLoading);
        this.urlMXP = url;
    }

    public ImageMXP(InputStream is) {
        super(is);
    }

    public ImageMXP(InputStream is, double requestedWidth, double requestedHeight, boolean preserveRatio, boolean smooth) {
        super(is, requestedWidth, requestedHeight, preserveRatio, smooth);
    }
}
