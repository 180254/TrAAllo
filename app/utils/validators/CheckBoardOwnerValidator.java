package utils.validators;

import models.User;
import play.data.validation.Constraints;
import play.libs.F;

public class CheckBoardOwnerValidator extends Constraints.Validator<Integer> {

    @Override
    public boolean isValid(Integer boardID) {
        return boardID == null ||
                (User.isLoggedIn() &&
                        User.loggedInUser().boards.stream().anyMatch(board -> board.id.equals(boardID)));

    }

    @Override
    public F.Tuple<String, Object[]> getErrorMessageKey() {
        return new F.Tuple<>("page.validation.unauthorized", new Object[0]);
    }
}
