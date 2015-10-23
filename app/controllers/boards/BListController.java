package controllers.boards;

import com.avaje.ebean.Ebean;
import models.BList;
import models.Board;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import play.data.Form;
import play.data.validation.Constraints;
import play.i18n.Messages;
import play.libs.F;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Security;
import utils.authenticators.LoggedInAuthenticator;
import utils.other.ValidationErrorsHelper;
import utils.validators.CheckBListOwnerValidator;
import utils.validators.CheckBoardOwnerValidator;
import utils.validators.CheckStringIsLongValidator;
import utils.validators.UniqueBListNameValidator;

public class BListController extends Controller {

    private static UniqueBListNameValidator uniqueBListNameValidator = new UniqueBListNameValidator();
    private static CheckBListOwnerValidator checkBListOwnerValidator = new CheckBListOwnerValidator();
    private static CheckStringIsLongValidator checkStringIsLongValidator = new CheckStringIsLongValidator();

    @Security.Authenticated(LoggedInAuthenticator.class)
    public static Result add() {
        Form<NewBList> boardForm = Form.form(NewBList.class).bindFromRequest();

        if (boardForm.hasErrors()) {
            return badRequest(
                    new ValidationErrorsHelper("page.board.bList.add.label.", boardForm).getWithNLAsBR());

        } else {

            NewBList newBList = boardForm.get();
            Board board = Board.find.byId(newBList.boardID);
            BList.create(board, newBList.name);
        }

        return ok();
    }

    @Security.Authenticated(LoggedInAuthenticator.class)
    public static Result edit() {
        Form<EditBList> bListForm = Form.form(EditBList.class).bindFromRequest();

        if (bListForm.hasErrors()) { // include check credentials
            return badRequest(
                    new ValidationErrorsHelper("page.board.bList.edit.label.", bListForm).getWithNLAsBR());
        }

        BList bList = BList.find.byId(bListForm.get().modifiedID);
        if (bList == null) {
            return badRequest(Messages.get("page.board.notFound"));
        }


        bList.name = bListForm.get().newName;
        bList.save();

        return ok();
    }

    @Security.Authenticated(LoggedInAuthenticator.class)
    public static Result delete() {
        String bListIDString = Form.form().bindFromRequest().get("bListID");
        if (!checkStringIsLongValidator.isValid(bListIDString)) {
            return badRequest(Messages.get("page.badRequest"));
        }

        Long bListID = Long.parseLong(bListIDString);
        BList bListObj = BList.find.byId(bListID);

        if (bListObj == null) {
            return badRequest(Messages.get("page.board.notFound"));
        }
        if (!checkBListOwnerValidator.isValid(bListObj.id)) {
            return unauthorized(Messages.get("page.unauthorized"));
        }

        bListObj.delete();
        return redirect(controllers.routes.Application.index());
    }

    @Security.Authenticated(LoggedInAuthenticator.class)
    public static Result sort() {
        Ebean.beginTransaction();

        String sortedBLists = Form.form().bindFromRequest().get("sortedBLists");
        String[] ids = sortedBLists.split(",");

        Logger logger = LoggerFactory.getLogger(BListController.class);
        logger.info(sortedBLists);

        long sortPos = 0;
        for (String idString : ids) {
            if (!checkStringIsLongValidator.isValid(idString)) {
                logger.error("/" + idString);
                Ebean.rollbackTransaction();
                return badRequest(Messages.get("page.badRequest"));
            }

            Long id = Long.valueOf(idString);
            if (!checkBListOwnerValidator.isValid(id)) {
                logger.error("//" + idString);
                Ebean.rollbackTransaction();
                return unauthorized(Messages.get("page.unauthorized"));
            }

            BList bList = BList.find.byId(id);
            bList.sortPosition = sortPos;
            bList.save();

            sortPos++;
        }

        Ebean.commitTransaction();
        Ebean.endTransaction();
        return ok();
    }

    public static class NewBList {
        @Constraints.Required
        @Constraints.ValidateWith(CheckBoardOwnerValidator.class)
        public Long boardID;

        @Constraints.Required
        @Constraints.MinLength(4)
        @Constraints.MaxLength(15)
        @Constraints.Pattern(value = "^[A-Za-z0-9- ]+$", message = "page.validation.onlyAlphanumericAndSpace")
        public String name;

        public String validate() {
            F.Tuple<Long, String> bListIndicator = new F.Tuple<>(boardID, name);
            return !uniqueBListNameValidator.isValid(bListIndicator)
                    ? uniqueBListNameValidator.getErrorMessageKey()._1
                    : null;
        }
    }

    public static class EditBList {
        @Constraints.Required
        @Constraints.ValidateWith(CheckBListOwnerValidator.class)
        public Long modifiedID;

        @Constraints.Required
        @Constraints.MinLength(4)
        @Constraints.MaxLength(15)
        @Constraints.Pattern(value = "^[A-Za-z0-9- ]+$", message = "page.validation.onlyAlphanumericAndSpace")
        public String newName;

        public String validate() {
            BList bList = BList.find.byId(modifiedID);
            if (!bList.name.equalsIgnoreCase(newName)) {

                F.Tuple<Long, String> bListIndicator = new F.Tuple<>(bList.board.id, newName);
                return !uniqueBListNameValidator.isValid(bListIndicator)
                        ? uniqueBListNameValidator.getErrorMessageKey()._1
                        : null;
            }
            return null;
        }
    }
}
