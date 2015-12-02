package controllers.boards;

import com.avaje.ebean.Ebean;
import models.BList;
import models.Card;
import models.HistoryItem;
import models.HistoryItem.Action;
import models.HistoryItem.Element;
import models.User;
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
            Card card = Card.create(bList, newCard.name);

            HistoryItem.create(card.list.board, Element.CARD, Action.CREATED, card.name, bList.name);
        }

        return ok();
    }

    public static Result edit() {
        Form<EditCard> cardForm = Form.form(EditCard.class).bindFromRequest();

        if (cardForm.hasErrors()) {
            return badRequest(
                    new ValidationErrorsHelper("page.board.card.edit.label.", cardForm).getWithNLAsBR());
        }

        Card card = Card.find.byId(cardForm.get().cardId);
        if (card == null) {
            return badRequest(Messages.get("page.board.notFound"));
        }

        HistoryItem.create(card.list.board, Element.CARD, Action.RENAMED, card.name, cardForm.get().newName);

        card.name = cardForm.get().newName;
        card.save();

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

    @Security.Authenticated(LoggedInAuthenticator.class)
    public static Result delete() {
        String cardIdAsString = Form.form().bindFromRequest().get("cardId");
        if (!checkStringIsLongValidator.isValid(cardIdAsString)) {
            return badRequest(Messages.get("page.badRequest"));
        }

        Long cardId = Long.parseLong(cardIdAsString);
        Card card = Card.find.byId(cardId);

        if (card == null) {
            return badRequest(Messages.get("page.board.notFound"));
        }
        if (!checkCardOwnerValidator.isValid(card.id)) {
            return unauthorized(Messages.get("page.unauthorized"));
        }

        HistoryItem.create(card.list.board, Element.CARD, Action.DELETED, card.name, null);

        card.delete();
        return redirect(controllers.routes.Application.index());
    }

    @Security.Authenticated(LoggedInAuthenticator.class)
    public static Result addMember() {

        String cardIdAsString = Form.form().bindFromRequest().get("cardId");
        if (!checkStringIsLongValidator.isValid(cardIdAsString)) {
            return badRequest(Messages.get("page.badRequest"));
        }

        Long cardId = Long.parseLong(cardIdAsString);
        Card card = Card.find.byId(cardId);

        if (card == null) {
            return badRequest(Messages.get("page.board.notFound"));
        }
        if (!checkCardOwnerValidator.isValid(card.id)) {
            return unauthorized(Messages.get("page.unauthorized"));
        }

        String userIdAsString = Form.form().bindFromRequest().get("userId");
        Long userId = Long.parseLong(userIdAsString);
        User user = User.find.byId(userId);

        card.cardMember = user;
        card.save();

        return ok(card.cardMember.username);
    }


    @Security.Authenticated(LoggedInAuthenticator.class)
    public static Result deleteMember() {

        String cardIdAsString = Form.form().bindFromRequest().get("cardId");
        if (!checkStringIsLongValidator.isValid(cardIdAsString)) {
            return badRequest(Messages.get("page.badRequest"));
        }

        Long cardId = Long.parseLong(cardIdAsString);
        Card card = Card.find.byId(cardId);

        if (card == null) {
            return badRequest(Messages.get("page.board.notFound"));
        }
        if (!checkCardOwnerValidator.isValid(card.id)) {
            return unauthorized(Messages.get("page.unauthorized"));
        }

        card.cardMember = null;
        card.save();

        return ok();
    }

    public static class NewCard {
        @Constraints.Required
        @Constraints.ValidateWith(CheckBListOwnerValidator.class)
        public Long listId;

        @Constraints.Required
        @Constraints.MinLength(1)
        @Constraints.MaxLength(100)
        public String name;
    }

    public static class EditCard {
        @Constraints.Required
        @Constraints.ValidateWith(CheckBListOwnerValidator.class)
        public Long listId;

        @Constraints.Required
        @Constraints.ValidateWith(CheckCardOwnerValidator.class)
        public Long cardId;

        @Constraints.Required
        @Constraints.MinLength(1)
        @Constraints.MaxLength(100)
        public String newName;
    }
}
