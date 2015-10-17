package controllers;

import controllers.authenticators.LoggedIn;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Security;
import views.html.index;

public class Application extends Controller {

    @Security.Authenticated(LoggedIn.class)
    public static Result index() {
        String userID = session().getOrDefault("user.id", "null");
        return ok(index.render("You're logged in!", userID));
    }

}
