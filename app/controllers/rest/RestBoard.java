package controllers.rest;

import models.Board;
import models.User;
import play.mvc.Result;

import java.util.List;

import static controllers.rest.RestUtils.asJson;
import static play.mvc.Results.ok;

public class RestBoard {

    public static Result list() {
        List<Board> findResult = Board.find.all();
        return ok(asJson(findResult));
    }

    public static Result listForCurrentUser() {
        List<Board> findResult = User.loggedInUser().boards;
        return ok(asJson(findResult));
    }
}
