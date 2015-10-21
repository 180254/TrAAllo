package utils.validators;

import models.User;
import play.data.validation.Constraints;
import play.libs.F;

public class UniqueBoardNameValidator extends Constraints.Validator<String> {

    @Override
    public boolean isValid(String name) {
        return !(User.loggedInUser().boards.stream().anyMatch(
                board -> board.name.equalsIgnoreCase(name)
        ));
    }

    @Override
    public F.Tuple<String, Object[]> getErrorMessageKey() {
        return new F.Tuple<>("modal.board.validate.notUnique", new Object[0]);
    }
}
