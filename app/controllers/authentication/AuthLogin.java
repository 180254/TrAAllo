package controllers.authentication;

import models.User;
import play.data.Form;
import play.mvc.Controller;
import play.mvc.Result;
import views.html.login;

import java.time.LocalDateTime;

public class AuthLogin extends Controller {

    public static Result login() {
        Form<Login> loginForm = Form.form(Login.class);
        return ok(login.render(loginForm));
    }

    public static Result authenticate() {
        Form<Login> loginForm = Form.form(Login.class).bindFromRequest();

        if (loginForm.hasErrors()) {
            return badRequest(login.render(loginForm));

        } else {
            User user = User.find
                    .where().eq("username", loginForm.get().username)
                    .findUnique();

            user.lastLoginTime = LocalDateTime.now();
            user.save();

            session().clear();
            session("user.id", Long.toString(user.id));
            return redirect(controllers.routes.Application.index());
        }
    }

    public static class Login {
        public String username;
        public String password;

        public String validate() {
            return !User.authenticate(username, password)
                    ? "page.login.validate.authenticateError"
                    : null;
        }
    }
}
