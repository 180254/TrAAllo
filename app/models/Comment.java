package models;

import com.avaje.ebean.Model;
import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

@Entity
public class Comment extends Model {

    public static final Model.Finder<Long, Comment> find = new Model.Finder<>(Comment.class);

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.REFRESH)
    @Column(nullable = false) public Card card;

    @Id public Long id;
    @Column(nullable = false) public String author;
    @Column(nullable = false, length = 1024) public String text;
    @Column(nullable = false) public LocalDateTime dateTime;

    protected Comment() {
    }

    public static Comment create(Card card, String author, String text) {
        Comment comment = new Comment();
        comment.dateTime = LocalDateTime.now();
        comment.card = card;
        comment.author = author;
        comment.text = text;
        comment.save();

        return comment;
    }

    public String getFormattedDateTime() {
        return DateTimeFormatter.ofPattern("yyy-MM-dd hh:mm:ss").format(dateTime);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Comment comment = (Comment) o;
        return Objects.equals(id, comment.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
