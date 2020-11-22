package com.kakaopay.throwmoney.model.jpa.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.BatchSize;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "throw_money")
@Getter
@Setter
@DynamicInsert
@DynamicUpdate
@EqualsAndHashCode(callSuper = false)
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
public class ThrowMoneyEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "`token`")
    private String token;

    @Column(name = "`roomId`")
    private Long roomId;

    @Column(name = "`userId`")
    private Long userId;

    @Column(name = "`memberCnt`")
    private Integer memberCnt;

    @Column(name = "`amount`")
    private Long amount;

    @PrePersist
    public void beforePersist() {
        this.setCreatedAt(LocalDateTime.now());
        this.setUpdatedAt(LocalDateTime.now());
    }

    @PreUpdate
    public void beforeUpdate() {
        this.setUpdatedAt(LocalDateTime.now());
    }

    /*@JoinColumn(name = "`throwMoneyId`", referencedColumnName = "`id")
    @OneToMany(targetEntity = PickupEntity.class, fetch = FetchType.LAZY)
    @BatchSize(size = 100)
    @JsonManagedReference
    private List<PickupEntity> pickupEntityList;
*/
}
