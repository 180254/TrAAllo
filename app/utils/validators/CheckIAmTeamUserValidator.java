package utils.validators;

import models.Team;
import models.User;
import play.data.validation.Constraints;
import play.libs.F;

public class CheckIAmTeamUserValidator extends Constraints.Validator<Long> {

    @Override
    public boolean isValid(Long teamId) {
        return teamId == null ||
                (User.isLoggedIn() &&
                        Team.find.byId(teamId).users.contains(User.loggedInUser()));
    }

    @Override
    public F.Tuple<String, Object[]> getErrorMessageKey() {
        return new F.Tuple<>("page.validation.unauthorized", new Object[0]);
    }
}
