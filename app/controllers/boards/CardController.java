package controllers.boards;

import models.BList;
import models.Card;
import play.data.Form;
import play.data.validation.Constraints;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Security;
import utils.authenticators.LoggedInAuthenticator;
import utils.other.ValidationErrorsHelper;
import utils.validators.CheckBListOwnerValidator;

public class CardController extends Controller {

    @Security.Authenticated(LoggedInAuthenticator.class)
    public static Result add() {
        Form<NewCard> cardForm = Form.form(NewCard.class).bindFromRequest();

        if (cardForm.hasErrors()) {
            return badRequest(
                    new ValidationErrorsHelper("page.board.card.add.label.", cardForm).getWithNLAsBR());

        } else {

            NewCard newCard = cardForm.get();
            BList bList = BList.find.byId(newCard.listId);
            Card.create(bList, newCard.name);
        }

        return ok();
    }

    public static class NewCard {
        @Constraints.Required
        @Constraints.ValidateWith(CheckBListOwnerValidator.class)
        public Long listId;

        @Constraints.Required
        @Constraints.MinLength(4)
        @Constraints.MaxLength(15)
        @Constraints.Pattern(value = "^[A-Za-z0-9- ]+$", message = "page.validation.onlyAlphanumericAndSpace")
        public String name;
    }
}
