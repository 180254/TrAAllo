package utils.validators;

import models.BList;
import models.User;
import play.data.validation.Constraints;
import play.libs.F;

import java.util.Objects;

public class CheckBListOwnerValidator extends Constraints.Validator<Long> {

    @Override
    public boolean isValid(Long bListID) {
        return bListID == null
                || Objects.equals(BList.find.byId(bListID).board.owner.id, User.loggedInUser().id);

    }

    @Override
    public F.Tuple<String, Object[]> getErrorMessageKey() {
        return new F.Tuple<>("page.validation.unauthorized", new Object[0]);
    }
}
