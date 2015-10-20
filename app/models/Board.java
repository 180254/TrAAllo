package models;

import com.avaje.ebean.Model;
import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Board extends Model{

    public static Finder<Integer, Board> find = new Finder<>(Board.class);

    @Id
    public Integer id;
    public String name;
    public BoardType boardType;

    @JsonIgnore
    @ManyToMany(cascade =  CascadeType.ALL)
    public List<User> users;

    public static void create(String name, int boardType) {
        Board board = new Board();
        board.name = name;
        board.boardType = BoardType.values()[boardType];
        board.users = new ArrayList<>();
        board.users.add(User.loggedIn());
        board.save();
    }

    public boolean isPrivate(){
        return boardType == BoardType.Private;
    }
}


enum BoardType{
    Public,
    Private,
    Team,
}