package by.bsu.audioservice.controller;

import by.bsu.audioservice.command.Command;
import by.bsu.audioservice.command.CommandFactory;
import by.bsu.audioservice.pool.ConnectionPool;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Class Controller
 *
 * Created by 7 on 06.08.2016.
 */
@WebServlet("/controller")
public class Controller extends HttpServlet {
    /** Field LOGGER */
    private static final Logger LOGGER = LogManager.getLogger(Controller.class);

    /**
     * Method doGet
     *
     * @param req of type HttpServletRequest
     * @param resp of type HttpServletResponse
     * @throws ServletException
     * @throws IOException
     */
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        LOGGER.info("Get");
        processRequest(req, resp);
    }

    /**
     * Method doPost
     *
     * @param req of type HttpServletRequest
     * @param resp of type HttpServletResponse
     * @throws ServletException
     * @throws IOException
     */
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        LOGGER.info("Post");
        processRequest(req, resp);
    }

    /**
     * Method destroy
     */
    @Override
    public void destroy() {
        ConnectionPool.getInstance().closePool();
        super.destroy();
    }

    /**
     * Method processRequest
     *
     * @param req of type HttpServletRequest
     * @param resp of type HttpServletResponse
     */
    protected void processRequest(HttpServletRequest req, HttpServletResponse resp){
        Command command = CommandFactory.defineCommand(req);
        String page = command.execute(req);
        RequestDispatcher dispatcher = getServletContext().getRequestDispatcher(page);
        try {
            dispatcher.forward(req, resp);
        } catch (ServletException e) {
            LOGGER.error("ServletException", e);
        } catch (IOException e) {
            LOGGER.error("IOException", e);
        }
    }
}
