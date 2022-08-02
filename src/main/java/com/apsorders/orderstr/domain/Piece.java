package com.apsorders.orderstr.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.*;
import javax.validation.constraints.*;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A Piece.
 */
@Entity
@Table(name = "piece")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class Piece implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @NotNull
    @Column(name = "serial", nullable = false)
    private String serial;

    @NotNull
    @Column(name = "model", nullable = false)
    private String model;

    @Column(name = "jhi_desc")
    private String desc;

    @NotNull
    @Column(name = "manu", nullable = false)
    private String manu;

    @Column(name = "notesp")
    private String notesp;

    @ManyToMany
    @NotNull
    @JoinTable(
        name = "rel_piece__orders_belonged",
        joinColumns = @JoinColumn(name = "piece_id"),
        inverseJoinColumns = @JoinColumn(name = "orders_belonged_id")
    )
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(value = { "customer", "piecesIns" }, allowSetters = true)
    private Set<Order> ordersBelongeds = new HashSet<>();

    @ManyToOne
    @JsonIgnoreProperties(value = { "orders", "piecesses" }, allowSetters = true)
    private Customer customer;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Piece id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getSerial() {
        return this.serial;
    }

    public Piece serial(String serial) {
        this.setSerial(serial);
        return this;
    }

    public void setSerial(String serial) {
        this.serial = serial;
    }

    public String getModel() {
        return this.model;
    }

    public Piece model(String model) {
        this.setModel(model);
        return this;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getDesc() {
        return this.desc;
    }

    public Piece desc(String desc) {
        this.setDesc(desc);
        return this;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getManu() {
        return this.manu;
    }

    public Piece manu(String manu) {
        this.setManu(manu);
        return this;
    }

    public void setManu(String manu) {
        this.manu = manu;
    }

    public String getNotesp() {
        return this.notesp;
    }

    public Piece notesp(String notesp) {
        this.setNotesp(notesp);
        return this;
    }

    public void setNotesp(String notesp) {
        this.notesp = notesp;
    }

    public Set<Order> getOrdersBelongeds() {
        return this.ordersBelongeds;
    }

    public void setOrdersBelongeds(Set<Order> orders) {
        this.ordersBelongeds = orders;
    }

    public Piece ordersBelongeds(Set<Order> orders) {
        this.setOrdersBelongeds(orders);
        return this;
    }

    public Piece addOrdersBelonged(Order order) {
        this.ordersBelongeds.add(order);
        order.getPiecesIns().add(this);
        return this;
    }

    public Piece removeOrdersBelonged(Order order) {
        this.ordersBelongeds.remove(order);
        order.getPiecesIns().remove(this);
        return this;
    }

    public Customer getCustomer() {
        return this.customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public Piece customer(Customer customer) {
        this.setCustomer(customer);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Piece)) {
            return false;
        }
        return id != null && id.equals(((Piece) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Piece{" +
            "id=" + getId() +
            ", serial='" + getSerial() + "'" +
            ", model='" + getModel() + "'" +
            ", desc='" + getDesc() + "'" +
            ", manu='" + getManu() + "'" +
            ", notesp='" + getNotesp() + "'" +
            "}";
    }
}
