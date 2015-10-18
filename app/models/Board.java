package models;

import com.avaje.ebean.Model;

import javax.persistence.*;
import java.util.List;

@Entity
public class Board extends Model{

    public static Finder<Integer, Board> find = new Finder<>(Board.class);

    @Id
    public Integer id;
    public String name;
    public BoardType boardType;

    @ManyToMany(cascade =  CascadeType.ALL)
    public List<User> users;
}


enum BoardType{
    Public,
    Private,
    Team,
}