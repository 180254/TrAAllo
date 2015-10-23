package models;

import com.avaje.ebean.Model;
import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.List;

@Entity
public class Board extends Model {

    public static Finder<Long, Board> find = new Finder<>(Board.class);

    @Id public Long id;
    @NotNull public String name;
    @NotNull public Type type;

    @JsonIgnore @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.REFRESH)
    public User owner;

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, mappedBy = "board")
    public List<BList> bLists;

    protected Board() {
    }

    public static Board create(User owner, String name, int typeCode) {
        return create(owner, name, Type.fromCode(typeCode));
    }

    public static Board create(User owner, String name, Type type) {
        Board board = new Board();
        board.name = name;
        board.type = type;
        board.owner = owner;
        board.save();

        return board;
    }

    public boolean isPrivate() {
        return type == Type.Private;
    }

    public enum Type {
        Public(0),
        Private(1),
        Team(2);

        int code;

        Type(int code) {
            this.code = code;
        }

        public static Type fromCode(int code) {
            for (Type type : Type.values()) {
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


