package org.milyn.util;

import org.hsqldb.Server;
import org.hsqldb.jdbcDriver;
import org.milyn.io.StreamUtils;
import org.milyn.util.ClassUtil;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * @author
 */
public class HsqlServer {

    private Server hsqlServer;

    private String url;
    private String username = "sa";
    private String password = "";

    private Connection connection;

    public HsqlServer(final int port) throws Exception {
        final String databaseName = "milyn-test-" + port;

        new Thread() {
            public void run() {
                Server server = new Server();
                server.setLogWriter(new PrintWriter(System.out));
                server.setErrWriter(new PrintWriter(System.err));
                server.setDatabasePath(0, "target");
                server.setDatabaseName(0, databaseName);
                server.setNoSystemExit( true );
                server.setSilent( true );
                server.setPort(port);
                server.start();
                hsqlServer = server;
            }
        }.start();
        while(hsqlServer == null) {
            Thread.sleep(50);
        }

        DriverManager.registerDriver(new jdbcDriver());
        url = "jdbc:hsqldb:hsql://localhost:" + port + "/" + databaseName;
        connection = DriverManager.getConnection(url, username, password);
    }

    public void stop() throws Exception {
        try {
            connection.close();
        } finally {
            hsqlServer.shutdown();
        }
    }

    public boolean execScript(InputStream script) throws SQLException {
        String scriptString;
        try {
            scriptString = StreamUtils.readStream(new InputStreamReader(script));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        Statement statement = connection.createStatement();
        try {
            return statement.execute(scriptString);
        } finally {
            statement.close();
        }
    }

    public Connection getConnection() {
        return connection;
    }

    public String getUrl() {
        return url;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }
}
