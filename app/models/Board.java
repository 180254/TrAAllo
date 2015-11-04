package models;

import com.avaje.ebean.Model;
import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.util.List;

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

    protected Board() {
    }

    public static Board create(User owner, String name, int typeCode) {
        return create(owner, name, Board.Type.fromCode(typeCode));
    }

    public static Board create(User owner, String name, Board.Type type) {
        Board board = new Board();
        board.name = name;
        board.type = type;
        board.owner = owner;
        board.save();

        return board;
    }

    @JsonIgnore
    public boolean isPrivate() {
        return type == Board.Type.Private;
    }

    public enum Type {
        Public(0),
        Private(1),
        Team(2);

        int code;

        Type(int code) {
            this.code = code;
        }

        public static Board.Type fromCode(int code) {
            for (Board.Type type : Board.Type.values()) {
                if (type.getCode() == code) {
                    return type;
                }
            }
            return null;
        }

        public int getCode() {
            return code;
        }
    }
}


