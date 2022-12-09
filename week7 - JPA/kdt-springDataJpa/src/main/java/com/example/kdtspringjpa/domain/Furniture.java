package com.example.kdtspringjpa.domain;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@DiscriminatorValue("FURNITURE")
public class Furniture extends Item {
    private int width;
    private int height;
}
