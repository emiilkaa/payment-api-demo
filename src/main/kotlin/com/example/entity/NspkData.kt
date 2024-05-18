package com.example.entity

import com.vladmihalcea.hibernate.type.json.JsonStringType
import org.hibernate.Hibernate
import org.hibernate.annotations.Type
import org.hibernate.annotations.TypeDef
import java.io.Serializable
import java.time.LocalDateTime
import javax.persistence.*

@Entity
@Table(name = "NSPK_DATA", schema = "PAYMENT_API_APP")
@SequenceGenerator(name = "NSPK_DATA_SEQ", sequenceName = "NSPK_DATA_SEQ", allocationSize = 1)
@TypeDef(name = "json", typeClass = JsonStringType::class)
data class NspkData(

    @Id
    @Column(columnDefinition = "NUMBER", unique = true)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "NSPK_DATA_SEQ")
    var id: Long? = null,

    @Column(name = "DATE_CREATED", columnDefinition = "TIMESTAMP(6)", nullable = false)
    var dateCreated: LocalDateTime = LocalDateTime.now(),

    @Column(name = "DATE_UPDATED", columnDefinition = "TIMESTAMP(6)", nullable = false)
    var dateUpdated: LocalDateTime = LocalDateTime.now(),

    @Column(name = "RESPONSE_CODE", columnDefinition = "VARCHAR2(50)")
    var responseCode: String? = null,

    @Column(name = "ERROR_CODE", columnDefinition = "VARCHAR2(30)")
    var errorCode: String? = null,

    @Column(name = "ERROR_MESSAGE", columnDefinition = "VARCHAR2(50)")
    var errorMessage: String? = null,

    @Column(name = "REQUEST_MESSAGE")
    @Type(type = "json")
    var requestMessage: Map<String, Any>?,

    @Column(name = "RESPONSE_MESSAGE")
    @Type(type = "json")
    var responseMessage: Map<String, Any>?,

) : Serializable {

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "PAYMENT_ID", nullable = false)
    lateinit var payment: Payment

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "REQUEST_ID", nullable = false)
    lateinit var request: Request

    @PreUpdate
    fun preUpdate() {
        this.dateUpdated = LocalDateTime.now()
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || Hibernate.getClass(this) != Hibernate.getClass(other)) return false
        other as NspkData

        return id != null && id == other.id
    }

    override fun hashCode(): Int = javaClass.hashCode()

    @Override
    override fun toString(): String {
        return this::class.simpleName + "(id = $id, dateCreated = $dateCreated)"
    }

}
