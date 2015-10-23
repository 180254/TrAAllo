package utils.authenticators;

import models.User;
import play.mvc.Http;
import play.mvc.Result;
import play.mvc.Security;

public class LoggedInAuthenticator extends Security.Authenticator {

    @Override
    public String getUsername(Http.Context ctx) {
        String userIDs = ctx.session().get("user.id");
        if (userIDs == null) {
            ctx.session().clear();
            return null;
        }

        Long userID = Long.valueOf(userIDs);
        User user = User.find.byId(userID);
        if (user == null) {
            ctx.session().clear();
            return null;
        }

        return user.username;
    }

    @Override
    public Result onUnauthorized(Http.Context ctx) {
        return redirect(controllers.authentication.routes.AuthLogin.login());
    }
}