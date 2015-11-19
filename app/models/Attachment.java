package models;

import com.avaje.ebean.Model;
import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;

@Entity
public class Attachment extends Model {

    public static final Model.Finder<Long, Attachment> find = new Model.Finder<>(Attachment.class);

    @JsonIgnore @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.REFRESH)
    @Column(nullable = false) public Card card;

    @Id public Long id;
    @Column(nullable = false) public String fileName;
    @Column(nullable = false) public String contentType;
    @Column(nullable = false, length = 10_000_000) public byte[] contentBytes;

    protected Attachment() {
    }

    public static Attachment create(Card card, String name, String type, byte[] content) {
        Attachment attachment = new Attachment();
        attachment.card = card;
        attachment.fileName = name;
        attachment.contentType = type;
        attachment.contentBytes = content;
        attachment.save();
        return attachment;
    }
}
