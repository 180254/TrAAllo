package models;

import com.avaje.ebean.Model;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
public class Team extends Model {

    public static final Model.Finder<Long, Team> find = new Model.Finder<>(Team.class);

    @Id public Long id;
    @Column(nullable = false) public String name;

    @ManyToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinTable(name = "team_users",
            joinColumns = @JoinColumn(name = "team_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    public List<User> users;

    protected Team() {
    }

    public static Team create(String name) {
        Team team = new Team();
        team.name = name;
        team.users = new ArrayList<>();
        team.users.add(User.loggedInUser());
        team.save();

        return team;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Team team = (Team) o;
        return Objects.equals(id, team.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
