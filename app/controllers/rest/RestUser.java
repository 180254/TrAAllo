package controllers.rest;

import com.avaje.ebean.Model;
import models.User;
import play.mvc.Result;

import java.util.List;

import static controllers.rest.RestUtils.asJson;
import static play.mvc.Results.ok;

public class RestUser {

    public static Result list() {
        List<User> findResult = User.find.all();
        return ok(asJson(findResult));
    }

    public static Result clean() {
        User.find.all().forEach(Model::delete);
        return list();
    }

}
