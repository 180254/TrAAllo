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

    public static final Model.Finder<Long, User> find = new Model.Finder<>(User.class);

    @Id public Long id;
    @Column(nullable = false) public String username;
    @Column(nullable = false) public String password;
    @Column(nullable = false) public LocalDateTime registerTime;
    @Column(nullable = true) public LocalDateTime lastLoginTime;

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, mappedBy = "owner")
    @OrderBy("name ASC, id ASC")
    public List<Board> boards;

    protected User() {
    }

    public static User register(String username, String password) {
        String salt = BCrypt.gensalt();
        String hashPw = BCrypt.hashpw(password, salt);

        User user = new User();
        user.username = username;
        user.password = hashPw;
        user.registerTime = LocalDateTime.now();
        user.boards = new ArrayList<>(10);
        user.save();

        return user;
    }

    public static boolean authenticate(String username, String password) {
        User user = find.where().eq("username", username).findUnique();
        return (user != null) && BCrypt.checkpw(password, user.password);
    }

    public static boolean isLoggedIn() {
        return session().get("user.id") != null;
    }

    public static User loggedInUser() {
        String sid = session().get("user.id");
        Long id = Long.valueOf(sid);

        return find.byId(id);
    }
}
