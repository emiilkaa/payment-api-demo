package com.example.entity

import com.example.enums.PaymentScheme
import org.hibernate.Hibernate
import java.io.Serializable
import java.time.LocalDateTime
import javax.persistence.*

@Entity
@Table(name = "CARD_DATA", schema = "PAYMENT_API")
@SequenceGenerator(name = "CARD_DATA_SEQ", sequenceName = "CARD_DATA_SEQ", allocationSize = 1)
data class CardData(

    @Id
    @Column(columnDefinition = "NUMBER", unique = true)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "CARD_DATA_SEQ")
    var id: Long? = null,

    @Column(name = "DATE_CREATED", columnDefinition = "TIMESTAMP(6)", nullable = false)
    var dateCreated: LocalDateTime = LocalDateTime.now(),

    @Column(name = "DATE_UPDATED", columnDefinition = "TIMESTAMP(6)", nullable = false)
    var dateUpdated: LocalDateTime = LocalDateTime.now(),

    @Column(name = "PAYMENT_SCHEME", columnDefinition = "VARCHAR2(50)", nullable = false)
    @Enumerated(EnumType.STRING)
    var paymentScheme: PaymentScheme,

    @Column(name = "PAN", columnDefinition = "VARCHAR2(50)")
    var pan: String? = null,

    @Column(name = "PAN_EXP_DATE", columnDefinition = "VARCHAR2(24)")
    var panExpDate: String? = null,

    @Column(name = "CARDHOLDER_NAME", columnDefinition = "VARCHAR2(50)")
    var cardholderName: String? = null

) : Serializable {

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "PAYMENT_ID")
    lateinit var payment: Payment

    @PreUpdate
    fun preUpdate() {
        this.dateUpdated = LocalDateTime.now()
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || Hibernate.getClass(this) != Hibernate.getClass(other)) return false
        other as CardData

        return id != null && id == other.id
    }

    override fun hashCode(): Int = javaClass.hashCode()

    @Override
    override fun toString(): String {
        return this::class.simpleName + "(id = $id, dateCreated = $dateCreated)"
    }

}
