package models;

import com.avaje.ebean.Model;
import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.util.List;

@Entity
public class BList extends Model {

    public static final Model.Finder<Long, BList> find = new Model.Finder<>(BList.class);

    @Id public Long id;
    @Column(nullable = false) public String name;
    @Column(nullable = false) public Long sortPosition = 0L;

    @JsonIgnore @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.REFRESH)
    @Column(nullable = false) public Board board;

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, mappedBy = "list")
    @OrderBy("sortPosition ASC, id ASC")
    @JsonIgnore
    public List<Card> cards;

    protected BList() {
    }

    public static BList create(Board board, String name) {
        BList maxSortBList = BList.find.orderBy("sortPosition DESC").setMaxRows(1).findUnique();
        long nextSortPos = (maxSortBList != null) ? (maxSortBList.sortPosition + 1L) : 1L;

        BList bList = new BList();
        bList.name = name;
        bList.board = board;
        bList.sortPosition = nextSortPos;
        bList.save();

        return bList;
    }
}
