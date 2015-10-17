package controllers.authenticators;

import models.User;
import play.mvc.Http;
import play.mvc.Result;
import play.mvc.Security;

public class LoggedIn extends Security.Authenticator {

    @Override
    public String getUsername(Http.Context ctx) {
        String userIDs = ctx.session().get("user.id");
        if (userIDs == null) return null;

        Integer userID = Integer.valueOf(userIDs);
        User user = User.find.byId(userID);
        if (user == null) return null;

        return user.username;
    }

    @Override
    public Result onUnauthorized(Http.Context ctx) {
        return redirect("/login");
    }
}