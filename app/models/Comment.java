package models;

import com.avaje.ebean.Model;
import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Entity
public class Comment extends Model {

    public static final Model.Finder<Long, Comment> find = new Model.Finder<>(Comment.class);

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.REFRESH)
    @Column(nullable = false) public Card card;

    @Id public Long id;
    @Column(nullable = false) public String author;
    @Column(nullable = false) public String text;
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
}