package com.kakaopay.throwmoney.model.jpa.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import java.time.LocalDateTime;

@MappedSuperclass
@Getter
@Setter
public abstract class BaseEntity {

    @Column(name = "`createdAt`")
    private LocalDateTime createdAt;

    @Column(name = "`updatedAt`")
    private LocalDateTime updatedAt;
}
