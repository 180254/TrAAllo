package utils.validators;

import models.Attachment;
import models.User;
import play.data.validation.Constraints;
import play.libs.F;

import java.util.Objects;

public class CheckAttachmentOwnerValidator extends Constraints.Validator<Long> {

    @Override
    public boolean isValid(Long attID) {
        return attID == null ||
                (User.isLoggedIn() &&
                        Objects.equals(Attachment.find.byId(attID).card.list.board.owner.id, User.loggedInUser().id));

    }

    @Override
    public F.Tuple<String, Object[]> getErrorMessageKey() {
        return new F.Tuple<>("page.validation.unauthorized", new Object[0]);
    }
}
