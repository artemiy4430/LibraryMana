package main.librarymana.model;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder(value = {"id"}, alphabetic = true)
public abstract class Model {
   protected  Long id;

    public Model(Long id) {
        this.id = id;
    }

    public Model() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
