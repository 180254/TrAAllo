package controllers.boards;

import models.Board;
import models.HistoryItem;
import models.HistoryItem.Action;
import models.HistoryItem.Element;
import models.User;
import play.data.Form;
import play.data.validation.Constraints;
import play.i18n.Messages;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Security;
import utils.authenticators.LoggedInAuthenticator;
import utils.other.ValidationErrorsHelper;
import utils.validators.CheckBoardOwnerValidator;
import utils.validators.CheckStringIsLongValidator;
import utils.validators.UniqueBoardNameValidator;
import views.html.board;

public class BoardController extends Controller {

    private static CheckBoardOwnerValidator checkBoardOwnerValidator = new CheckBoardOwnerValidator();
    private static CheckStringIsLongValidator checkStringIsLongValidator = new CheckStringIsLongValidator();

    private static boolean hasViewAccess(Board bord) {
        return !bord.isPrivate() || checkBoardOwnerValidator.isValid(bord.id);
    }

    @Security.Authenticated(LoggedInAuthenticator.class)
    public static Result add() {
        Form<NewBoard> boardForm = Form.form(NewBoard.class).bindFromRequest();

        if (boardForm.hasErrors()) {
            return badRequest(
                    new ValidationErrorsHelper("modal.board.crud.label.", boardForm).getWithNLAsBR());

        } else {
            Board board = Board.create(User.loggedInUser(), boardForm.get().name, boardForm.get().typeCode);
            HistoryItem.create(board, Element.BOARD, Action.CREATED, boardForm.get().name, null);
        }

        return ok();
    }

    public static Result show(Long boardId) {
        Board foundBoard = Board.find.byId(boardId);

        if (foundBoard == null) {
            return badRequest(Messages.get("page.board.notFound"));
        }

        if (!hasViewAccess(foundBoard)) {
            return unauthorized(Messages.get("page.unauthorized"));
        }

        boolean hasEditRights = checkBoardOwnerValidator.isValid(foundBoard.id);
        return ok(board.render(foundBoard, hasEditRights));
    }

    @Security.Authenticated(LoggedInAuthenticator.class)
    public static Result edit() {
        Form<EditBoard> boardForm = Form.form(EditBoard.class).bindFromRequest();

        if (boardForm.hasErrors()) { // include check credentials
            return badRequest(
                    new ValidationErrorsHelper("modal.board.crud.label.", boardForm).getWithNLAsBR());
        }

        Board board = Board.find
                .where().eq("id", boardForm.get().modifiedID)
                .findUnique();
        if (board == null) {
            return badRequest(Messages.get("page.board.notFound"));
        }

        if (!board.name.equals(boardForm.get().name)) {
            HistoryItem.create(board, Element.BOARD, Action.RENAMED, board.name, boardForm.get().name);
        }

        Board.Type newType = Board.Type.fromCode(boardForm.get().typeCode);
        if (!board.type.equals(newType)) {
            HistoryItem.create(board, Element.BOARD, Action.TYPECHANGED,
                    board.type.getTypeName(), newType.getTypeName());
        }

        board.name = boardForm.get().name;
        board.type = newType;
        board.save();

        return ok();
    }

    @Security.Authenticated(LoggedInAuthenticator.class)
    public static Result delete() {
        String boardIDString = Form.form().bindFromRequest().get("boardID");
        if (!checkStringIsLongValidator.isValid(boardIDString)) {
            return badRequest(Messages.get("page.badRequest"));
        }

        Long boardId = Long.parseLong(boardIDString);
        Board boardObj = Board.find.byId(boardId);

        if (boardObj == null) {
            return badRequest(Messages.get("page.board.notFound"));
        }
        if (!checkBoardOwnerValidator.isValid(boardObj.id)) {
            return unauthorized(Messages.get("page.unauthorized"));
        }

        HistoryItem.create(boardObj, Element.BOARD, Action.DELETED, boardObj.name, null);

        boardObj.delete();
        return redirect(controllers.routes.Application.index());
    }

    public static class NewBoard {
        @Constraints.Required
        @Constraints.MinLength(4)
        @Constraints.MaxLength(15)
        @Constraints.Pattern(value = "^[A-Za-z0-9- ]+$", message = "page.validation.onlyAlphanumericAndSpace")
        @Constraints.ValidateWith(UniqueBoardNameValidator.class)
        public String name;

        @Constraints.Required
        @Constraints.Min(0)
        @Constraints.Max(1)
        public int typeCode;
    }

    public static class EditBoard {
        @Constraints.Required
        @Constraints.ValidateWith(CheckBoardOwnerValidator.class)
        public Long modifiedID;

        @Constraints.Required
        @Constraints.MinLength(4)
        @Constraints.MaxLength(15)
        @Constraints.Pattern(value = "^[A-Za-z0-9- ]+$", message = "page.validation.onlyAlphanumericAndSpace")
        public String name;

        @Constraints.Required
        @Constraints.Min(0)
        @Constraints.Max(1)
        public int typeCode;

        public String validate() {
            if (!Board.find.byId(modifiedID).name.equalsIgnoreCase(name)) {
                UniqueBoardNameValidator uniqueBoardNameValidator = new UniqueBoardNameValidator();

                if (!uniqueBoardNameValidator.isValid(name)) {
                    return uniqueBoardNameValidator.getErrorMessageKey()._1;
                }
            }

            return null;
        }
    }
}
