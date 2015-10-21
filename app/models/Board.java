package models;

import com.avaje.ebean.Model;
import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;

@Entity
public class Board extends Model {

    public static Finder<Integer, Board> find = new Finder<>(Board.class);

    @Id
    public Integer id;
    public String name;
    public Type type;

    @JsonIgnore
    @ManyToOne(fetch= FetchType.LAZY, cascade = CascadeType.ALL)
    public User owner;

    protected Board() {
    }

    public static void create(String name, int typeCode, User owner) {
        create(name, Type.fromCode(typeCode), owner);
    }

    public static void create(String name, Type type, User owner) {
        Board board = new Board();
        board.name = name;
        board.type = type;
        board.owner = owner;
        board.save();
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

        public int getCode() {
            return code;
        }

        public static Type fromCode(int code) {
            for (Type type : Type.values()) {
                if (type.getCode() == code) {
                    return type;
                }
            }
            return null;
        }
    }
}


