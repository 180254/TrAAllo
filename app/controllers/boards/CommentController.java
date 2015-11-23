package controllers.boards;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import models.Card;
import models.Comment;
import models.User;
import play.data.Form;
import play.i18n.Messages;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;
import utils.validators.CheckCardOwnerValidator;
import utils.validators.CheckStringIsLongValidator;

public class CommentController extends Controller {

    private static CheckStringIsLongValidator checkStringIsLongValidator = new CheckStringIsLongValidator();
    private static CheckCardOwnerValidator checkCardOwnerValidator = new CheckCardOwnerValidator();

    public static Result add() {

        String cardId = Form.form().bindFromRequest().get("cardId");

        if (!checkStringIsLongValidator.isValid(cardId)) {
            return badRequest(Messages.get("page.badRequest"));
        }

        Card card = Card.find.byId(Long.valueOf(cardId));

        if (card == null) {
            return badRequest(Messages.get("card.modal.comments.not.found"));
        }
        if (!checkCardOwnerValidator.isValid(card.id)) {
            return unauthorized(Messages.get("page.unauthorized"));
        }

        String text = Form.form().bindFromRequest().get("commentText");
        Comment comment = Comment.create(card, User.loggedInUser().username,text);

        return ok(Json.toJson(comment));
    }
}
