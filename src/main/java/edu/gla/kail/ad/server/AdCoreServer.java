package edu.gla.kail.ad.server;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.glassfish.jersey.servlet.ServletContainer;

import static org.eclipse.jetty.servlet.ServletContextHandler.NO_SESSIONS;


public class AdCoreServer {
    public static void main(String[] args) throws Exception {
        Server server = new Server(8080);

        /*HandlerList handlers = new HandlerList();
        handlers.setHandlers(new Handler[] { new AdCoreHandler() , new DefaultHandler() });
        server.setHandler(handlers);*/

        ServletContextHandler servletContextHandler = new ServletContextHandler(NO_SESSIONS); //
        // TODO(Jeff): if it's REST the session should be stateless, otherwise we should have
        // session, shouldn't we?

        servletContextHandler.setContextPath("/"); // Set context path to the root.
        server.setHandler(servletContextHandler);

        ServletHolder servletHolder = servletContextHandler.addServlet(ServletContainer.class,
                "/rest_api/*"); // Specify path: "/rest_api/*" for the serverlet to handle.
        servletHolder.setInitParameter("jersey.config.server.provider.packages",
                "edu.gla.kail.ad.server.resources"); // Location of REST resources.
        servletHolder.setInitOrder(0); // "Holders with order<0, are initialized on use. Those
        // with order>=0 are initialized in increasing order when the handler is started."

        server.start();
        server.dumpStdErr(); // TODO(Adam): no idea what it does!
        server.join();
        //  server.destroy();
    }
}

// testing the server:
/* curl -H "Content-Type: text/plain" -X POST -d '{ "time": "2018-06-19T10:47:05.932Z", "clientId": "Random Client ID", "interaction": { "text": "Sample text", "type": "TEXT", "deviceType": "Iphone whatever", "languageCode": "en-US" } }' http://localhost:8080/rest_api/agent_interaction
* */
