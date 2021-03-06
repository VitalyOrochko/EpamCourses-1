package by.bsu.audioservice.command.impl;

import by.bsu.audioservice.command.Command;
import by.bsu.audioservice.entity.Audio;
import by.bsu.audioservice.exception.TechnicalException;
import by.bsu.audioservice.logic.ShowAllAudioLogic;
import by.bsu.audioservice.manager.ConfigurationManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;

/**
 * Class AdminCabinetRouteCommand
 *
 * Created by 7 on 28.08.2016.
 */
public class AdminCabinetRouteCommand implements Command {
    /** Field AUDIOS */
    private static final String AUDIOS = "audios";

    /** Field LOGGER */
    private static final Logger LOGGER = LogManager.getLogger(AdminCabinetRouteCommand.class);

    /**
     * Method execute
     *
     * @param request of type HttpServletRequest
     * @return String
     */
    @Override
    public String execute(HttpServletRequest request) {
        LOGGER.info("Admin cabinet route command");
        ArrayList<Audio> audios = null;
        String page = null;
        try {
            audios = ShowAllAudioLogic.getAllAudios();
            request.getSession().setAttribute(AUDIOS, audios);
            page = ConfigurationManager.getInstance().getProperty(ConfigurationManager.ADMIN_CABINET_PAGE_PATH);
        } catch (TechnicalException e) {
            LOGGER.error("Something was wrong, redirect to error page.", e);
            page = ConfigurationManager.getInstance().getProperty(ConfigurationManager.ERROR_PAGE_PATH);
        }
        return page;
    }
}
