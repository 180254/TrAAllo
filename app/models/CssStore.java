package models;

import com.avaje.ebean.Model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class CssStore extends Model {

    public static final Model.Finder<Long, CssStore> find = new Model.Finder<>(CssStore.class);

    @Id public Long id;
    @Column(nullable = false, length = 20000) public String css;

    public static CssStore create(String css) {

        CssStore cssStore = new CssStore();
        cssStore.css = css;
        cssStore.save();

        return cssStore;
    }
}
