package com.hanzec.yats.model.data.management.tailnet;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;

@Entity
public class Tailnet extends TailnetSettings {

    @Id
    @Column(name = "name")
    private String name;
}
