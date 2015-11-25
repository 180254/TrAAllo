package controllers.boards;

import models.Card;
import models.Comment;
import models.User;
import play.data.Form;
import play.data.validation.Constraints;
import play.i18n.Messages;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;
import utils.other.ValidationErrorsHelper;
import utils.validators.CheckCardOwnerValidator;
import utils.validators.CheckCommentOwnerValidator;
import utils.validators.CheckStringIsLongValidator;

public class CommentController extends Controller {

    private static CheckStringIsLongValidator checkStringIsLongValidator = new CheckStringIsLongValidator();
    private static CheckCommentOwnerValidator checkCommentOwnerValidator = new CheckCommentOwnerValidator();

    public static Result add() {
        Form<NewComment> commentForm = Form.form(NewComment.class).bindFromRequest();

        if (commentForm.hasErrors()) {
            return badRequest(
                    new ValidationErrorsHelper("card.modal.comments.edit.label.", commentForm).getWithNLAsBR());
        }

        Card card = Card.find.byId(Long.valueOf(commentForm.get().cardId));
        if (card == null) {
            return badRequest(Messages.get("card.modal.comments.not.found"));
        }

        Comment comment = Comment.create(card, User.loggedInUser().username, commentForm.get().commentText);

        return ok(Json.toJson(comment));
    }

    public static Result delete() {
        String commentIdasString = Form.form().bindFromRequest().get("commentId");
        if (!checkStringIsLongValidator.isValid(commentIdasString)) {
            return badRequest(Messages.get("page.badRequest"));
        }

        Long id = Long.parseLong(commentIdasString);
        Comment comment = Comment.find.byId(id);

        if (comment == null) {
            return badRequest(Messages.get("card.modal.comments.not.found"));
        }
        if (!checkCommentOwnerValidator.isValid(comment.id)) {
            return unauthorized(Messages.get("page.unauthorized"));
        }

        comment.delete();
        return ok();
    }

    public static Result edit() {
        Form<EditComment> commentForm = Form.form(EditComment.class).bindFromRequest();

        if (commentForm.hasErrors()) {
            return badRequest(
                    new ValidationErrorsHelper("card.modal.comments.edit.label.", commentForm).getWithNLAsBR());
        }

        Comment comment = Comment.find.byId(commentForm.get().commentId);
        if (comment == null) {
            return badRequest(Messages.get("card.modal.comments.not.found"));
        }

        comment.text = commentForm.get().commentText;
        comment.save();

        return ok(comment.text);
    }

    public static class NewComment {
        @Constraints.Required
        @Constraints.ValidateWith(CheckCardOwnerValidator.class)
        public Long cardId;

        @Constraints.Required
        @Constraints.MinLength(1)
        @Constraints.MaxLength(1024)
        public String commentText;
    }

    public static class EditComment {
        @Constraints.Required
        @Constraints.ValidateWith(CheckCommentOwnerValidator.class)
        public Long commentId;

        @Constraints.Required
        @Constraints.ValidateWith(CheckCardOwnerValidator.class)
        public Long cardId;

        @Constraints.Required
        @Constraints.MinLength(1)
        @Constraints.MaxLength(1024)
        public String commentText;
    }
}
