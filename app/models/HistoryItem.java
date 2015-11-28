package models;

import com.avaje.ebean.Model;
import play.i18n.Messages;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

@Entity
public class HistoryItem extends Model {

    public static final Model.Finder<Long, HistoryItem> find = new Model.Finder<>(HistoryItem.class);

    @Id public Long id;
    @Column(nullable = false) public LocalDateTime dateTime;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.REFRESH)
    @Column(nullable = false) public Board board;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.REFRESH)
    @Column(nullable = false) public User user;

    @Column(nullable = false) public Element element;
    @Column(nullable = false) public Action action;
    @Column(nullable = true) public String param1;
    @Column(nullable = true) public String param2;

    public enum Element {
        BLIST("BList"),
        BOARD("Board"),
        CARD("Card");

        private String desc;

        Element(String desc) {
            this.desc = desc;
        }

        public String getDesc() {
            return desc;
        }
    }

    public enum Action {
        CREATED,
        RENAMED,
        DELETED,

        TYPECHANGED
    }

    protected HistoryItem() {
    }

    public static HistoryItem create(Board board, Element element, Action action, String param1, String param2) {

        HistoryItem historyItem = new HistoryItem();
        historyItem.dateTime = LocalDateTime.now();
        historyItem.board = board;
        historyItem.user = User.loggedInUser();
        historyItem.element = element;
        historyItem.action = action;
        historyItem.param1 = param1;
        historyItem.param2 = param2;
        historyItem.save();

        return historyItem;
    }

    @Override
    public String toString() {
        switch (action) {
            case CREATED:
                String format = String.format("%s %s \"%s\" %s",
                        element.getDesc(),
                        Messages.get("history.with.name"),
                        param1,
                        Messages.get("history.created"));

                if (element == Element.CARD) {
                    format += String.format(" %s \"%s\"", Messages.get("history.in.bList"), param2);
                }

                return format + ".";

            case RENAMED:
                return String.format("%s %s \"%s\" %s \"%s\".",
                        element.getDesc(),
                        Messages.get("history.with.name"),
                        param1,
                        Messages.get("history.renamed"),
                        param2);

            case DELETED:
                return String.format("%s %s \"%s\" %s.",
                        element.getDesc(),
                        Messages.get("history.with.name"),
                        param1,
                        Messages.get("history.deleted"));

            case TYPECHANGED:
                return String.format("%s %s %s %s %s %s %s.",
                        element.getDesc(),
                        Messages.get("history.type"),
                        Messages.get("history.type.changed"),
                        Messages.get("history.from"),
                        param1,
                        Messages.get("history.to"),
                        param2);
        }
        return "";
    }

    public String getFormattedDateTime() {
        return DateTimeFormatter.ofPattern("yyy-MM-dd hh:mm:ss").format(dateTime);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        HistoryItem that = (HistoryItem) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}

