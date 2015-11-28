package utils.validators;

import models.Team;
import play.data.validation.Constraints;
import play.libs.F;

public class UniqueTeamNameValidator extends Constraints.Validator<String> {

    @Override
    public boolean isValid(String name) {
        int usernameRowCount = Team.find.where().eq("name", name).findRowCount();
        return usernameRowCount == 0;
    }

    @Override
    public F.Tuple<String, Object[]> getErrorMessageKey() {
        return new F.Tuple<>("page.team.validate.alreadyExist", new Object[0]);
    }
}
