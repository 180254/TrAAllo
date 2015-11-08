package controllers.boards;

import com.avaje.ebean.Ebean;
import models.BList;
import models.Card;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import play.data.Form;
import play.data.validation.Constraints;
import play.i18n.Messages;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Security;
import utils.authenticators.LoggedInAuthenticator;
import utils.other.ValidationErrorsHelper;
import utils.validators.CheckBListOwnerValidator;
import utils.validators.CheckCardOwnerValidator;
import utils.validators.CheckStringIsLongValidator;

public class CardController extends Controller {

    private static CheckStringIsLongValidator checkStringIsLongValidator = new CheckStringIsLongValidator();
    private static CheckCardOwnerValidator checkCardOwnerValidator = new CheckCardOwnerValidator();

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

    @Security.Authenticated(LoggedInAuthenticator.class)
    public static Result sort() {
        Ebean.beginTransaction();

        String sortedCards = Form.form().bindFromRequest().get("sortedCards");
        String[] ids = sortedCards.split(",");

        Logger logger = LoggerFactory.getLogger(CardController.class);
        logger.info(sortedCards);

        long sortPos = 0;
        for (String idString : ids) {
            if (!checkStringIsLongValidator.isValid(idString)) {
                logger.error("/" + idString);
                Ebean.rollbackTransaction();
                return badRequest(Messages.get("page.badRequest"));
            }

            Long id = Long.valueOf(idString);
            if (!checkCardOwnerValidator.isValid(id)) {
                logger.error("//" + idString);
                Ebean.rollbackTransaction();
                return unauthorized(Messages.get("page.unauthorized"));
            }

            Card card = Card.find.byId(id);
            card.sortPosition = sortPos;
            card.save();

            sortPos++;
        }

        Ebean.commitTransaction();
        Ebean.endTransaction();
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
