package utils.validators;

import models.Comment;
import models.User;
import play.data.validation.Constraints;
import play.libs.F;

import java.util.Objects;

public class CheckCommentOwnerValidator extends Constraints.Validator<Long> {

    @Override
    public boolean isValid(Long commentId) {
        return commentId == null ||
                (User.isLoggedIn() &&
                        (Objects.equals(Comment.find.byId(commentId).card.list.board.owner.id, User.loggedInUser().id)) ||
                        (Comment.find.byId(commentId).card.list.board.team.users.contains(User.loggedInUser())));
    }

    @Override
    public F.Tuple<String, Object[]> getErrorMessageKey() {
        return new F.Tuple<>("page.validation.unauthorized", new Object[0]);
    }
}
