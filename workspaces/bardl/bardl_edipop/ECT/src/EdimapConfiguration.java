import org.milyn.edisax.model.internal.Edimap;

/**
 * EdimapConfiguration
 * @author bardl
 */
public class EdimapConfiguration {
    private Edimap edimap;
    private String filename;

    public EdimapConfiguration(Edimap edimap, String filename) {
        this.edimap = edimap;
        this.filename = filename;
    }

    public Edimap getEdimap() {
        return edimap;
    }

    public void setEdimap(Edimap edimap) {
        this.edimap = edimap;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }
}
