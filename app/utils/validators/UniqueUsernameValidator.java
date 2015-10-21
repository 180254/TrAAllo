package utils.validators;

import models.User;
import play.data.validation.Constraints;
import play.libs.F;


public class UniqueUsernameValidator extends Constraints.Validator<String> {

    @Override
    public boolean isValid(String username) {
        int usernameRowCount = User.find.where().eq("username", username).findRowCount();
        return usernameRowCount == 0;
    }

    @Override
    public F.Tuple<String, Object[]> getErrorMessageKey() {
        return new F.Tuple<>("page.signup.validate.alreadyExist", new Object[0]);
    }
}
