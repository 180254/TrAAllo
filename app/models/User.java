package models;

import com.avaje.ebean.Model;
import org.mindrot.jbcrypt.BCrypt;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static play.mvc.Controller.session;

@Entity
public class User extends Model {

    public static Finder<Integer, User> find = new Finder<>(User.class);

    @Id
    public Integer id;
    public String username;
    public String password;
    public LocalDateTime registerTime;
    public LocalDateTime lastLoginTime;

    @ManyToMany(cascade =  CascadeType.ALL)
    public List<Board> boards;

    public static void register(String username, String password) {
        String salt = BCrypt.gensalt();
        String hashPw = BCrypt.hashpw(password, salt);

        User user = new User();
        user.username = username;
        user.password = hashPw;
        user.registerTime = LocalDateTime.now();
        user.boards = new ArrayList<>();
        user.save();
    }

    public static boolean authenticate(String username, String password) {
        User user = find.where().eq("username", username).findUnique();
        return user != null && BCrypt.checkpw(password, user.password);
    }

    public static User loggedIn() {
        String sid = session().get("user.id");
        Integer id = Integer.valueOf(sid);

        return find.byId(id);
    }
}
