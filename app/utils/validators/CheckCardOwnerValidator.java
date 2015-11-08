package utils.validators;

import models.Card;
import models.User;
import play.data.validation.Constraints;
import play.libs.F;

import java.util.Objects;

public class CheckCardOwnerValidator extends Constraints.Validator<Long> {

    @Override
    public boolean isValid(Long cardId) {
        return cardId == null
                || Objects.equals(Card.find.byId(cardId).list.board.owner.id, User.loggedInUser().id);
    }

    @Override
    public F.Tuple<String, Object[]> getErrorMessageKey() {
        return new F.Tuple<>("page.validation.unauthorized", new Object[0]);
    }
}
