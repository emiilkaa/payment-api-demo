package com.example.entity

import com.example.enums.RequestStatus
import com.example.enums.RequestType
import com.vladmihalcea.hibernate.type.json.JsonType
import org.hibernate.Hibernate
import org.hibernate.annotations.Type
import org.hibernate.annotations.TypeDef
import java.io.Serializable
import java.math.BigDecimal
import java.time.LocalDateTime
import javax.persistence.*

@Entity
@Table(name = "REQUEST", schema = "PAYMENT_API_APP")
@TypeDef(name = "json", typeClass = JsonType::class)
@TypeDef(name = "jsonb", typeClass = JsonType::class)
data class Request(

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "REQUEST_SEQ")
    @SequenceGenerator(name = "REQUEST_SEQ", sequenceName = "REQUEST_SEQ", allocationSize = 1)
    @Column(columnDefinition = "numeric", unique = true)
    var id: Long? = null,

    @Column(name = "DATE_CREATED", columnDefinition = "TIMESTAMP")
    var dateCreated: LocalDateTime = LocalDateTime.now(),

    @Column(name = "DATE_UPDATED", columnDefinition = "TIMESTAMP")
    var dateUpdated: LocalDateTime = LocalDateTime.now(),

    @Column(name = "REQUEST_TYPE", columnDefinition = "VARCHAR(50)", nullable = false)
    @Enumerated(EnumType.STRING)
    var requestType: RequestType,

    @Column(name = "AMOUNT", columnDefinition = "NUMERIC(18, 2)", nullable = false)
    var amount: BigDecimal,

    @Column(name = "STATUS", columnDefinition = "VARCHAR(50)", nullable = false)
    @Enumerated(EnumType.STRING)
    var status: RequestStatus,

    @Column(name = "TERMINAl_ID", columnDefinition = "VARCHAR(20)", nullable = false)
    var terminalId: String,

    @Column(name = "MESSAGE", columnDefinition = "VARCHAR(256)", nullable = true)
    var message: String?,

    @Column(name = "EXTENSION_FIELDS", columnDefinition = "jsonb")
    @Type(type = "jsonb")
    var extensionFields: Map<String, String>

) : Serializable {

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "PAYMENT_ID", nullable = false)
    lateinit var payment: Payment

    @Transient
    var nspkData: NspkData? = null

    @PreUpdate
    fun preUpdate() {
        this.dateUpdated = LocalDateTime.now()
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || Hibernate.getClass(this) != Hibernate.getClass(other)) return false
        other as Request

        return id != null && id == other.id
    }

    override fun hashCode(): Int = javaClass.hashCode()

    @Override
    override fun toString(): String {
        return this::class.simpleName + "(id = $id, dateCreated = $dateCreated)"
    }

}
