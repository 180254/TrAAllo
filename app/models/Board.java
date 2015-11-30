package models;

import com.avaje.ebean.Model;
import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Objects;

@Entity
public class Board extends Model {

    public static final Model.Finder<Long, Board> find = new Model.Finder<>(Board.class);

    @Id public Long id;
    @Column(nullable = false) public String name;
    @Column(nullable = false) public Board.Type type;

    @JsonIgnore @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.REFRESH)
    @Column(nullable = false) public User owner;

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, mappedBy = "board")
    @OrderBy("sortPosition ASC, id ASC")
    @JsonIgnore
    public List<BList> bLists;

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, mappedBy = "board")
    @JsonIgnore
    public List<HistoryItem> historyItems;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.REFRESH)
    @Column(nullable = true) public Team team;

    protected Board() {
    }

    public static Board create(User owner, String name, int typeCode, Team team) {
        return create(owner, name, Board.Type.fromCode(typeCode), team);
    }

    public static Board create(User owner, String name, Board.Type type, Team team) {
        Board board = new Board();
        board.name = name;
        board.type = type;
        board.owner = owner;
        board.team = team;
        board.save();

        return board;
    }

    @JsonIgnore
    public boolean isPrivate() {
        return type == Board.Type.Private;
    }

    @JsonIgnore
    public boolean isPublic() {
        return type == Board.Type.Public;
    }

    public List<HistoryItem> getHistoryItems(int count) {
        return HistoryItem.find.where().eq("board", this).order().desc("id").setMaxRows(count).findList();
    }

    public enum Type {
        Public("Public", 0),
        Private("Private", 1),
        Team("Team", 2);

        private String typeName;
        int code;

        Type(String typeName, int code) {
            this.typeName = typeName;
            this.code = code;
        }

        @NotNull
        public static Board.Type fromCode(int code) {
            for (Board.Type type : Board.Type.values()) {
                if (type.getCode() == code) {
                    return type;
                }
            }
            throw new RuntimeException();
        }

        public String getTypeName() {
            return typeName;
        }

        public int getCode() {
            return code;
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Board board = (Board) o;
        return Objects.equals(id, board.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}


