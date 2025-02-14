package com.hanzec.yats.model.data.management;

import com.google.gson.annotations.Expose;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(
        name = "group",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = "id")
        })
public class Group implements Serializable {
    @Id
    @Getter
    @Expose
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.AUTO)
    protected Integer id;

    @Expose
    @Setter
    @NotNull
    @Column(name = "name")
    private String name;

    @Expose
    @Getter
    @Setter
    @NotNull
    @Column(name = "description")
    private String description;

    @Getter
    @OneToMany(mappedBy = "group", cascade = {CascadeType.ALL})
    private final Set<User> clients = new HashSet<>();

    public Group(String name, String description) {
        this.name = name;
        this.description = description;
    }

    public Group() {
    }

    @Override
    public int hashCode() {
        return id;
    }

    @Override
    public String toString() {
        return "[" + name + ":" + id + "]:" + description;
    }
}
