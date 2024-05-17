package com.example.entity

import com.example.enums.PaymentStatus
import com.vladmihalcea.hibernate.type.json.JsonType
import org.hibernate.Hibernate
import org.hibernate.annotations.Type
import org.hibernate.annotations.TypeDef
import java.io.Serializable
import java.math.BigDecimal
import java.time.LocalDateTime
import javax.persistence.*

@Entity
@Table(name = "PAYMENT", schema = "PAYMENT_API")
@SequenceGenerator(name = "PAYMENT_SEQ", sequenceName = "PAYMENT_SEQ", allocationSize = 1)
@TypeDef(name = "json", typeClass = JsonType::class)
data class Payment(

    @Id
    @Column(columnDefinition = "NUMBER", unique = true)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "PAYMENT_SEQ")
    var id: Long? = null,

    @Column(name = "DATE_CREATED", columnDefinition = "TIMESTAMP(6)", nullable = false)
    var dateCreated: LocalDateTime = LocalDateTime.now(),

    @Column(name = "DATE_UPDATED", columnDefinition = "TIMESTAMP(6)", nullable = false)
    var dateUpdated: LocalDateTime = LocalDateTime.now(),

    @Column(name = "AMOUNT", columnDefinition = "NUMBER", nullable = false)
    var amount: BigDecimal,

    @Column(name = "ORIGINAL_AMOUNT", columnDefinition = "NUMBER", nullable = false)
    var originalAmount: BigDecimal,

    @Column(name = "STATUS", columnDefinition = "VARCHAR(50)", nullable = false)
    @Enumerated(EnumType.STRING)
    var status: PaymentStatus,

    @Lob
    @Column(name = "ADDITIONAL_DATA", columnDefinition = "CLOB")
    @Type(type = "json")
    var additionalData: MutableMap<String, Any>,

    @OneToMany(fetch = FetchType.EAGER, mappedBy = "payment")
    var requests: Set<Request> = HashSet(),

) : Serializable {

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "CARD_DATA_ID")
    lateinit var cardData: CardData

    @PreUpdate
    fun preUpdate() {
        this.dateUpdated = LocalDateTime.now()
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || Hibernate.getClass(this) != Hibernate.getClass(other)) return false
        other as Payment

        return id != null && id == other.id
    }

    override fun hashCode(): Int = javaClass.hashCode()

    @Override
    override fun toString(): String {
        return this::class.simpleName + "(id = $id , dateCreated = $dateCreated)"
    }

}
