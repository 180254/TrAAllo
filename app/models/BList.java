package models;

import com.avaje.ebean.Model;
import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;

@Entity
public class BList extends Model {

    public static Model.Finder<Long, BList> find = new Model.Finder<>(BList.class);

    @Id public Long id;
    @Column(nullable = false) public String name;
    @Column(nullable = false) public Long sortPosition = 0L;

    @JsonIgnore @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.REFRESH)
    @Column(nullable = false) public Board board;

    protected BList() {
    }

    public static BList create(Board board, String name) {
        BList maxSortBList = BList.find.orderBy("sortPosition DESC").setMaxRows(1).findUnique();

        BList bList = new BList();
        bList.name = name;
        bList.board = board;
        bList.sortPosition = maxSortBList.sortPosition + 1;
        bList.save();

        return bList;
    }

}
