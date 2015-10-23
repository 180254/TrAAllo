package models;

import com.avaje.ebean.Model;
import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Entity
public class BList extends Model {

    public static Model.Finder<Long, BList> find = new Model.Finder<>(BList.class);

    @Id public Long id;
    @NotNull public String name;

    @JsonIgnore @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.REFRESH)
    public Board board;

    protected BList() {
    }

    public static BList create(Board board, String name) {
        BList bList = new BList();
        bList.name = name;
        bList.board = board;
        bList.save();

        return bList;
    }

}
