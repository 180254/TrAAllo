package controllers;

import org.slf4j.LoggerFactory;
import play.*;
import play.mvc.*;

import views.html.*;

public class Application extends Controller {

    public static Result index() {
        Logger.debug("LOGGER TEST{1}");
        LoggerFactory.getLogger(Application.class).error("LOGGER TEST{2}");

        return ok(index.render("Your new application is ready."));
    }

}
