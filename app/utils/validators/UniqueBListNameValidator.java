package utils.validators;

import models.Board;
import play.data.validation.Constraints;
import play.libs.F;

public class UniqueBListNameValidator extends Constraints.Validator<F.Tuple<Long, String>> {

    @Override
    public boolean isValid(F.Tuple<Long, String> object) {
        Long boardID = object._1;
        String name = object._2;

        return !(Board.find.byId(boardID).bLists.stream().anyMatch(
                bList -> bList.name.equalsIgnoreCase(name)
        ));
    }

    @Override
    public F.Tuple<String, Object[]> getErrorMessageKey() {
        return new F.Tuple<>("page.board.bList.add.validate.notUnique", new Object[0]);
    }
}
