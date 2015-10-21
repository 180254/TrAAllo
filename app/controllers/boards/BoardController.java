package controllers.boards;

import models.Board;
import models.User;
import play.data.Form;
import play.data.validation.Constraints;
import play.i18n.Messages;
import play.mvc.Controller;
import play.mvc.Result;
import views.html.board;

public class BoardController extends Controller{

    public static Result add() {
        Form<NewBoard> boardForm = Form.form(NewBoard.class).bindFromRequest();

        if (boardForm.hasErrors()) {
            return badRequest(Messages.get("page.boardController.nameAndTypeIsRequired"));
        } else {
            Board.create(boardForm.get().name, boardForm.get().boardType, User.loggedIn());
        }
        return ok();
    }

    public static Result show(Integer boardId) {
        Board findedBoard = Board.find.where().eq("id", boardId).findUnique();

        if(findedBoard == null){
            return badRequest(Messages.get("page.board.notFound"));
        }

        return ok(board.render(findedBoard));
    }

    public static Result rename() {
        Form<NewBoard> boardForm = Form.form(NewBoard.class).bindFromRequest();

        if (boardForm.hasErrors()) {
            return badRequest(Messages.get("page.boardController.nameAndTypeIsRequired"));
        }

        Board board = Board.find
                .where().eq("id", boardForm.get().renameId)
                .findUnique();
        if(board == null){
            return badRequest(Messages.get("page.board.notFound"));
        }

        board.name = boardForm.get().name;
        board.save();

        return ok();
    }

    public static Result delete() {
        Integer boardId = Integer.parseInt(Form.form().bindFromRequest().get("boardId"));
        Board boardObj = Board.find.where().eq("id", boardId).findUnique();

        if(boardObj == null){
            return badRequest(Messages.get("page.board.notFound"));
        }

        boardObj.delete();
        return redirect("/");
    }

    public static class NewBoard {
        public int renameId;

        @Constraints.Required
        public String name;

        @Constraints.Required
        public int boardType;
    }
}
