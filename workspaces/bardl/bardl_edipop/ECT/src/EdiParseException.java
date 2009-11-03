/**
 * Created by IntelliJ IDEA.
 * User: bardl
 * Date: 2009-okt-14
 * Time: 07:46:26
 * To change this template use File | Settings | File Templates.
 */
public class EdiParseException extends Exception {

    public EdiParseException() {
    }

    public EdiParseException(String message) {
        super(message);
    }

    public EdiParseException(String message, Throwable cause) {
        super(message, cause);
    }

    public EdiParseException(Throwable cause) {
        super(cause);
    }

}
