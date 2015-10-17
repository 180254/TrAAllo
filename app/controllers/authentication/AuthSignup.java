package controllers.authentication;

import models.User;
import play.data.Form;
import play.data.validation.Constraints;
import play.mvc.Controller;
import play.mvc.Result;
import views.html.signup;
import views.html.welcome;

public class AuthSignUp extends Controller {

    public static Result signup() {
        return ok(signup.render(Form.form(Register.class)));
    }

    public static Result register() {
        Form<Register> registerForm = Form.form(Register.class).bindFromRequest();

        if (registerForm.hasErrors()) {
            return badRequest(signup.render(registerForm));

        } else {
            Register register = registerForm.get();
            User.register(register.username, register.password);
            return redirect("/welcome");
        }
    }

    public static Result welcome() {
        return ok(welcome.render());
    }

    public static class Register {

        @Constraints.Required
        @Constraints.MinLength(5)
        @Constraints.MaxLength(10)
        private String username;

        @Constraints.Required
        @Constraints.MinLength(5)
        @Constraints.MaxLength(50)
        private String password;

        public String validate() {
            int usernameRowCount = User.find.where().eq("username", this.username).findRowCount();
            boolean alreadyExist = usernameRowCount > 0;

            return alreadyExist
                    ? "page.signup.validate.alreadyExist"
                    : null;
        }

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }
    }
}
