package controllers.others;

import models.Team;
import models.User;
import play.data.Form;
import play.data.validation.Constraints;
import play.i18n.Messages;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Security;
import utils.authenticators.LoggedInAuthenticator;
import utils.other.ValidationErrorsHelper;
import utils.validators.CheckIAmTeamUserValidator;
import utils.validators.CheckStringIsLongValidator;
import utils.validators.UniqueTeamNameValidator;
import views.html.teamOneMember;
import views.html.teamone;
import views.html.teams;

public class TeamController extends Controller {

    private static CheckIAmTeamUserValidator checkIAmTeamUserValidator = new CheckIAmTeamUserValidator();
    private static CheckStringIsLongValidator checkStringIsLongValidator = new CheckStringIsLongValidator();

    @Security.Authenticated(LoggedInAuthenticator.class)
    public static Result list() {
        User user = User.loggedInUser();
        return ok(teams.render(user));
    }

    @Security.Authenticated(LoggedInAuthenticator.class)
    public static Result add() {
        Form<AddTeam> addTeamForm = Form.form(AddTeam.class).bindFromRequest();

        if (addTeamForm.hasErrors()) {
            return badRequest(
                    new ValidationErrorsHelper("page.team.crud.label.", addTeamForm).getWithNLAsBR());

        } else {
            Team team = Team.create(addTeamForm.get().teamName);
            return ok(teamone.render(team));

        }
    }
    
    @Security.Authenticated(LoggedInAuthenticator.class)
    public static Result editName() {
        Form<EditTeamName> editTeamNameForm = Form.form(EditTeamName.class).bindFromRequest();

        if (editTeamNameForm.hasErrors()) {
            return badRequest(
                    new ValidationErrorsHelper("page.team.crud.label.", editTeamNameForm).getWithNLAsBR());
        }

        if (!checkStringIsLongValidator.isValid(editTeamNameForm.get().pk)) {
            return badRequest(Messages.get("page.badRequest"));
        }

        Long teamId = Long.parseLong(editTeamNameForm.get().pk);
        Team team = Team.find.byId(teamId);

        if (team == null) {
            return badRequest(Messages.get("page.team.notFound"));
        }
        if (!checkIAmTeamUserValidator.isValid(team.id)) {
            return unauthorized(Messages.get("page.unauthorized"));
        }

        team.name = editTeamNameForm.get().value;
        team.save();

        return ok();
    }

    @Security.Authenticated(LoggedInAuthenticator.class)
    public static Result delete() {
        String teamIdStr = Form.form().bindFromRequest().get("teamID");

        if (!checkStringIsLongValidator.isValid(teamIdStr)) {
            return badRequest(Messages.get("page.badRequest"));
        }

        Long teamId = Long.parseLong(teamIdStr);
        Team team = Team.find.byId(teamId);

        if (team == null) {
            return badRequest(Messages.get("page.team.notFound"));
        }
        if (!checkIAmTeamUserValidator.isValid(team.id)) {
            return unauthorized(Messages.get("page.unauthorized"));
        }

        team.delete();
        return ok();
    }

    @Security.Authenticated(LoggedInAuthenticator.class)
    public static Result addMember() {
        String newMemberUsername = Form.form().bindFromRequest().get("username");
        String teamIdStr = Form.form().bindFromRequest().get("teamID");

        if (!checkStringIsLongValidator.isValid(teamIdStr)) {
            return badRequest(Messages.get("page.badRequest"));
        }

        System.out.println(newMemberUsername);
        User user = User.find.where().eq("username", newMemberUsername).findUnique();
        if (user == null) {
            return badRequest(Messages.get("page.team.validate.suchUsernameDoesNotExist"));
        }

        Long teamId = Long.parseLong(teamIdStr);
        Team team = Team.find.byId(teamId);

        if (team.users.contains(user)) {
            return badRequest(Messages.get("page.team.validate.alreadyMember"));
        }

        team.users.add(user);
        team.save();

        return ok(teamOneMember.render(team, user));
    }

    @Security.Authenticated(LoggedInAuthenticator.class)
    public static Result deleteMember() {
        String teamIdStr = Form.form().bindFromRequest().get("teamID");
        String userIdStr = Form.form().bindFromRequest().get("userID");

        if (!checkStringIsLongValidator.isValid(teamIdStr)) {
            return badRequest(Messages.get("page.badRequest"));
        }

        if (!checkStringIsLongValidator.isValid(userIdStr)) {
            return badRequest(Messages.get("page.badRequest"));
        }

        Team team = Team.find.byId(Long.parseLong(teamIdStr));
        User user = User.find.byId(Long.parseLong(userIdStr));

        if (team == null) {
            return badRequest(Messages.get("page.team.notFound"));
        }
        if (user == null) {
            return badRequest(Messages.get("page.team.memberNotFound"));
        }
        if (user.equals(User.loggedInUser())) {
            return badRequest(Messages.get("page.team.validate.cannotDeleteSelf"));
        }
        if (!team.users.contains(user)) {
            return badRequest(Messages.get("page.team.memberNotFound"));
        }

        team.users.remove(user);
        team.save();
        return ok();
    }

    public static class AddTeam {

        @Constraints.Required
        @Constraints.MinLength(4)
        @Constraints.MaxLength(15)
        @Constraints.Pattern(value = "^[A-Za-z0-9- ]+$", message = "page.validation.onlyAlphanumericAndSpace")
        @Constraints.ValidateWith(UniqueTeamNameValidator.class)
        public String teamName;

        public AddTeam() {
        }
    }

    public static class EditTeamName {

        public String pk;

        @Constraints.Required
        @Constraints.MinLength(4)
        @Constraints.MaxLength(15)
        @Constraints.Pattern(value = "^[A-Za-z0-9- ]+$", message = "page.validation.onlyAlphanumericAndSpace")
        @Constraints.ValidateWith(UniqueTeamNameValidator.class)
        public String value;

        public EditTeamName() {
        }
    }
}
