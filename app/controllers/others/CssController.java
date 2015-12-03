package controllers.others;

import com.google.common.io.Resources;
import models.CssStore;
import play.mvc.Controller;
import play.mvc.Result;

import java.awt.*;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Random;
import java.util.regex.Pattern;

public class CssController extends Controller {

    private static Random RAND = new Random();
    private static Pattern C_HEX_PATTERN = Pattern.compile("#[a-fA-F0-9]{6}");
    private static Pattern C_RGBA_PATTERN = Pattern.compile("rgba\\([0-9]+, [0-9]+, [0-9]+, [01].?[0-9]*\\)");

    private static String getRandomHexColour() {
        float r = RAND.nextFloat() / 2f + 0.3f;
        float g = RAND.nextFloat() / 2f + 0.5f;
        float b = RAND.nextFloat() / 2f + 0.2f;
        Color randomColor = new Color(r, g, b);
        return "#" + Integer.toHexString(randomColor.getRGB()).substring(2);
    }

    private static String getRandomRGBAColour() {
        Color randomColor;
        float a = RAND.nextFloat();

        if (RAND.nextInt(2) == 0) {
            float r = RAND.nextFloat() / 2f + 0.3f;
            float g = RAND.nextFloat() / 2f + 0.5f;
            float b = RAND.nextFloat() / 2f + 0.2f;
            randomColor = new Color(r, g, b);
        } else {
            float r = RAND.nextFloat();
            float g = RAND.nextFloat();
            float b = RAND.nextFloat();
            randomColor = new Color(r, g, b);
        }

        return String.format("rgba(%d, %d, %d, %s)",
                randomColor.getRed(),
                randomColor.getGreen(),
                randomColor.getBlue(),
                String.format("%.2g", a).replace(",", "."));
    }

    public static Result css() throws IOException {
        String supercss = session().get("supercss");

        if (supercss != null) {
            CssStore cssStore = CssStore.find.byId(Long.parseLong(supercss));
            return ok(cssStore.css).as("text/css");
        } else {
            URL resource = Resources.getResource("public/stylesheets/custom.css");
            String css = Resources.toString(resource, StandardCharsets.UTF_8);
            return ok(css).as("text/css");
        }
    }

    public static Result nextCss() throws IOException {
        URL resource = Resources.getResource("public/stylesheets/custom.css");
        String css = Resources.toString(resource, StandardCharsets.UTF_8);

        while (C_RGBA_PATTERN.matcher(css).find()) {
            css = C_RGBA_PATTERN.matcher(css).replaceFirst(getRandomHexColour());
        }

        while (C_HEX_PATTERN.matcher(css).find()) {
            css = C_HEX_PATTERN.matcher(css).replaceFirst(getRandomRGBAColour());
        }

        CssStore cssStore = CssStore.create(css);
        session().put("supercss", Long.toString(cssStore.id));

        if (request().getHeader("referer") != null) {
            return redirect(request().getHeader("referer"));
        } else {
            return redirect(controllers.routes.Application.index());
        }
    }

    public static Result resetCss() {
        session().remove("supercss");

        if (request().getHeader("referer") != null) {
            return redirect(request().getHeader("referer"));
        } else {
            return redirect(controllers.routes.Application.index());
        }
    }
}
