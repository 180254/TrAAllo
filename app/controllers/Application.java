package controllers;

import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Security;
import utils.authenticators.LoggedInAuthenticator;
import views.html.index;

@Security.Authenticated(LoggedInAuthenticator.class)
public class Application extends Controller {

    public static Result index() {
        String userID = session().getOrDefault("user.id", "null");
        return ok(index.render("You're logged in!", userID));
    }

}
