package com.kakaopay.throwmoney.model.jpa.entity;

import com.kakaopay.throwmoney.dto.type.common.YesNoType;
import com.kakaopay.throwmoney.dto.type.converter.YesNoTypeConverter;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "pick_up")
@Getter
@Setter
@DynamicInsert
@DynamicUpdate
@EqualsAndHashCode(callSuper = false)
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
public class PickupEntity extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "`throwMoneyId`")
    private Long throwMoneyId;

    @Column(name = "`userId`")
    private Long userId;

    @Column(name = "`amount`")
    private Long amount;

    @Column(name = "`isReceived`")
    @Convert(converter = YesNoTypeConverter.class)
    private YesNoType isReceived;

    @PrePersist
    public void beforePersist() {
        this.setCreatedAt(LocalDateTime.now());
        this.setUpdatedAt(LocalDateTime.now());
    }

    @PreUpdate
    public void beforeUpdate() {
        this.setUpdatedAt(LocalDateTime.now());
    }

}
