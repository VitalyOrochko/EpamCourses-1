package by.bsu.audioservice.ajax.command;

import by.bsu.audioservice.ajax.logic.BuyAudiosLogic;
import by.bsu.audioservice.entity.UserAccount;
import by.bsu.audioservice.exception.TechnicalException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.simple.JSONObject;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * Created by 7 on 09.09.2016.
 */
public class BuyAudiosCommand implements Command {
    private static final String USER_ACCOUNT = "user_account";
    private static final Logger LOGGER = LogManager.getLogger(BuyAudiosCommand.class);
    @Override
    public void execute(HttpServletRequest request, HttpServletResponse response, String requestData) throws IOException {
        LOGGER.info("Buy audios command");
        PrintWriter out = response.getWriter();
        JSONObject object = new JSONObject();
        try {
            UserAccount account = (UserAccount) request.getSession().getAttribute(USER_ACCOUNT);
            BuyAudiosLogic.buy(account);
            out.println(object);
            response.setStatus(HttpServletResponse.SC_OK);
        } catch (TechnicalException e) {
            LOGGER.error("Something was wrong", e);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        } finally {
            out.flush();
            out.close();
        }
    }
}
