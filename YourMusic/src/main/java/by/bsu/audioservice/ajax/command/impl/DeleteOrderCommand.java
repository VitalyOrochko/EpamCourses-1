package by.bsu.audioservice.ajax.command.impl;


import by.bsu.audioservice.ajax.command.Command;
import by.bsu.audioservice.ajax.logic.DeleteOrderLogic;
import by.bsu.audioservice.exception.TechnicalException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.simple.JSONObject;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * Class DeleteOrderCommand
 *
 * Created by 7 on 06.09.2016.
 */
public class DeleteOrderCommand implements Command {
    /** Field LOGGER */
    private static final Logger LOGGER = LogManager.getLogger(DeleteOrderCommand.class);

    /**
     * Method execute ...
     *
     * @param request of type HttpServletRequest
     * @param response of type HttpServletResponse
     * @param requestData of type String
     * @throws IOException when
     */
    @Override
    public void execute(HttpServletRequest request, HttpServletResponse response, String requestData)  {
        LOGGER.info("Delete order command");
        PrintWriter out = null;
        JSONObject object = new JSONObject();
        try{
            out = response.getWriter();
            DeleteOrderLogic.delete(requestData);
            out.println(object);
            response.setStatus(HttpServletResponse.SC_OK);
        } catch (TechnicalException e) {
            LOGGER.error("Something was wrong", e);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        } catch (IOException e) {
            LOGGER.error("Something was wrong", e);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        } finally {
            out.flush();
            out.close();
        }
    }
}
