package controllers.boards;

import models.Board;
import play.data.Form;
import play.data.validation.Constraints;
import play.i18n.Messages;
import play.mvc.Controller;
import play.mvc.Result;
import views.html.board;

public class BoardController extends Controller{

    public static Result addBoard() {
        Form<NewBoard> boardForm = Form.form(NewBoard.class).bindFromRequest();

        if (boardForm.hasErrors()) {
            return badRequest(Messages.get("page.boardController.nameAndTypeIsRequired"));
        } else {
            Board.create(boardForm.get().name, boardForm.get().boardType);
        }
        return ok();
    }

    public static Result showBoard(Integer boardId) {
        Board findedBoard = Board.find.where().eq("id", boardId).findUnique();

        if(findedBoard == null){
            return badRequest(Messages.get("page.boardController.boardNotFound"));
        }

        return ok(board.render(findedBoard));
    }

    public static Result renameBoard() {
        Form<NewBoard> boardForm = Form.form(NewBoard.class).bindFromRequest();

        if (boardForm.hasErrors()) {
            return badRequest(Messages.get("page.boardController.nameAndTypeIsRequired"));
        }

        Board board = Board.find
                .where().eq("id", boardForm.get().renameId)
                .findUnique();
        if(board == null){
            return badRequest(Messages.get("page.boardController.boardNotFound"));
        }

        board.name = boardForm.get().name;
        board.save();

        return ok();
    }

    public static Result deleteBoard() {
        Integer boardId = Integer.parseInt(Form.form().bindFromRequest().get("boardId"));
        Board boardObj = Board.find.where().eq("id", boardId).findUnique();

        if(boardObj == null){
            return badRequest(Messages.get("page.boardController.boardNotFound"));
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
