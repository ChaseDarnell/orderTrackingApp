package com.apsorders.orderstr.domain;

import com.apsorders.orderstr.domain.enumeration.StatusO;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import java.time.ZonedDateTime;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.*;
import javax.validation.constraints.*;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A Order.
 */
@Entity
@Table(name = "jhi_order")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class Order implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @NotNull
    @Column(name = "driver", nullable = false)
    private String driver;

    @NotNull
    @Column(name = "pick_up_date", nullable = false)
    private ZonedDateTime pickUpDate;

    @Column(name = "repair_date")
    private ZonedDateTime repairDate;

    @Column(name = "delivery_date")
    private ZonedDateTime deliveryDate;

    @NotNull
    @Column(name = "r_order_num", nullable = false, unique = true)
    private String rOrderNum;

    @Column(name = "inv_order_num", unique = true)
    private String invOrderNum;

    @Enumerated(EnumType.STRING)
    @Column(name = "status_o")
    private StatusO statusO;

    @Column(name = "noteso")
    private String noteso;

    @Lob
    @Column(name = "scan")
    private byte[] scan;

    @Column(name = "scan_content_type")
    private String scanContentType;

    @ManyToOne
    @JsonIgnoreProperties(value = { "orders", "piecesses" }, allowSetters = true)
    private Customer customer;

    @ManyToMany(mappedBy = "ordersBelongeds")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(value = { "ordersBelongeds", "customer" }, allowSetters = true)
    private Set<Piece> piecesIns = new HashSet<>();

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Order id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDriver() {
        return this.driver;
    }

    public Order driver(String driver) {
        this.setDriver(driver);
        return this;
    }

    public void setDriver(String driver) {
        this.driver = driver;
    }

    public ZonedDateTime getPickUpDate() {
        return this.pickUpDate;
    }

    public Order pickUpDate(ZonedDateTime pickUpDate) {
        this.setPickUpDate(pickUpDate);
        return this;
    }

    public void setPickUpDate(ZonedDateTime pickUpDate) {
        this.pickUpDate = pickUpDate;
    }

    public ZonedDateTime getRepairDate() {
        return this.repairDate;
    }

    public Order repairDate(ZonedDateTime repairDate) {
        this.setRepairDate(repairDate);
        return this;
    }

    public void setRepairDate(ZonedDateTime repairDate) {
        this.repairDate = repairDate;
    }

    public ZonedDateTime getDeliveryDate() {
        return this.deliveryDate;
    }

    public Order deliveryDate(ZonedDateTime deliveryDate) {
        this.setDeliveryDate(deliveryDate);
        return this;
    }

    public void setDeliveryDate(ZonedDateTime deliveryDate) {
        this.deliveryDate = deliveryDate;
    }

    public String getrOrderNum() {
        return this.rOrderNum;
    }

    public Order rOrderNum(String rOrderNum) {
        this.setrOrderNum(rOrderNum);
        return this;
    }

    public void setrOrderNum(String rOrderNum) {
        this.rOrderNum = rOrderNum;
    }

    public String getInvOrderNum() {
        return this.invOrderNum;
    }

    public Order invOrderNum(String invOrderNum) {
        this.setInvOrderNum(invOrderNum);
        return this;
    }

    public void setInvOrderNum(String invOrderNum) {
        this.invOrderNum = invOrderNum;
    }

    public StatusO getStatusO() {
        return this.statusO;
    }

    public Order statusO(StatusO statusO) {
        this.setStatusO(statusO);
        return this;
    }

    public void setStatusO(StatusO statusO) {
        this.statusO = statusO;
    }

    public String getNoteso() {
        return this.noteso;
    }

    public Order noteso(String noteso) {
        this.setNoteso(noteso);
        return this;
    }

    public void setNoteso(String noteso) {
        this.noteso = noteso;
    }

    public byte[] getScan() {
        return this.scan;
    }

    public Order scan(byte[] scan) {
        this.setScan(scan);
        return this;
    }

    public void setScan(byte[] scan) {
        this.scan = scan;
    }

    public String getScanContentType() {
        return this.scanContentType;
    }

    public Order scanContentType(String scanContentType) {
        this.scanContentType = scanContentType;
        return this;
    }

    public void setScanContentType(String scanContentType) {
        this.scanContentType = scanContentType;
    }

    public Customer getCustomer() {
        return this.customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public Order customer(Customer customer) {
        this.setCustomer(customer);
        return this;
    }

    public Set<Piece> getPiecesIns() {
        return this.piecesIns;
    }

    public void setPiecesIns(Set<Piece> pieces) {
        if (this.piecesIns != null) {
            this.piecesIns.forEach(i -> i.removeOrdersBelonged(this));
        }
        if (pieces != null) {
            pieces.forEach(i -> i.addOrdersBelonged(this));
        }
        this.piecesIns = pieces;
    }

    public Order piecesIns(Set<Piece> pieces) {
        this.setPiecesIns(pieces);
        return this;
    }

    public Order addPiecesIn(Piece piece) {
        this.piecesIns.add(piece);
        piece.getOrdersBelongeds().add(this);
        return this;
    }

    public Order removePiecesIn(Piece piece) {
        this.piecesIns.remove(piece);
        piece.getOrdersBelongeds().remove(this);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Order)) {
            return false;
        }
        return id != null && id.equals(((Order) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Order{" +
            "id=" + getId() +
            ", driver='" + getDriver() + "'" +
            ", pickUpDate='" + getPickUpDate() + "'" +
            ", repairDate='" + getRepairDate() + "'" +
            ", deliveryDate='" + getDeliveryDate() + "'" +
            ", rOrderNum='" + getrOrderNum() + "'" +
            ", invOrderNum='" + getInvOrderNum() + "'" +
            ", statusO='" + getStatusO() + "'" +
            ", noteso='" + getNoteso() + "'" +
            ", scan='" + getScan() + "'" +
            ", scanContentType='" + getScanContentType() + "'" +
            "}";
    }
}
