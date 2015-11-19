package controllers.boards;


import models.Attachment;
import models.Card;
import play.data.Form;
import play.i18n.Messages;
import play.mvc.Controller;
import play.mvc.Http.MultipartFormData.FilePart;
import play.mvc.Result;
import utils.validators.CheckAttachmentOwnerValidator;
import utils.validators.CheckCardOwnerValidator;
import utils.validators.CheckStringIsLongValidator;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class AttachmentController extends Controller {

    private static CheckStringIsLongValidator checkStringIsLongValidator = new CheckStringIsLongValidator();
    private static CheckCardOwnerValidator checkCardOwnerValidator = new CheckCardOwnerValidator();
    private static CheckAttachmentOwnerValidator checkAttachmentOwnerValidator = new CheckAttachmentOwnerValidator();

    public static Result add() {

        String cardId = Form.form().bindFromRequest().get("cardId");

        if (!checkStringIsLongValidator.isValid(cardId)) {
            return badRequest(Messages.get("page.badRequest"));
        }

        Card card = Card.find.byId(Long.valueOf(cardId));

        if (card == null) {
            return badRequest(Messages.get("card.modal.attachment.not.found"));
        }
        if (!checkCardOwnerValidator.isValid(card.id)) {
            return unauthorized(Messages.get("page.unauthorized"));
        }


        try {
            FilePart file = request().body().asMultipartFormData().getFile("file");
            if (file == null) {
                return internalServerError(Messages.get("error.cannot.upload.file"));
            }

            byte[] bytes = Files.readAllBytes(Paths.get(file.getFile().getAbsolutePath()));
            Attachment att = Attachment.create(card, file.getFilename(), file.getContentType(), bytes);

            return ok(String.format("%d\n%s", att.id, file.getFilename()));
        } catch (IOException e) {
            return internalServerError(Messages.get("error.cannot.upload.file"));
        }
    }


    public static Result delete() {
        String attIDasString = Form.form().bindFromRequest().get("id");
        if (!checkStringIsLongValidator.isValid(attIDasString)) {
            return badRequest(Messages.get("page.badRequest"));
        }

        Long id = Long.parseLong(attIDasString);
        Attachment att = Attachment.find.byId(id);

        if (att == null) {
            return badRequest(Messages.get("card.modal.attachment.not.found"));
        }
        if (!checkAttachmentOwnerValidator.isValid(att.id)) {
            return unauthorized(Messages.get("page.unauthorized"));
        }

        att.delete();
        return ok();
    }

    public static Result download(long id) {
        Attachment attachment = Attachment.find.byId(id);
        response().setContentType(attachment.contentType);
        response().setHeader("Content-Disposition", "attachment; filename=" + attachment.fileName);
        return ok(attachment.contentBytes);
    }
}
