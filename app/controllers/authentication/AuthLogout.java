package controllers.authentication;

import play.mvc.Controller;
import play.mvc.Result;

public class AuthLogout extends Controller {

    public static Result logout() {
        session().remove("user.id");
        return redirect(controllers.authentication.routes.AuthLogin.login());
    }
}
