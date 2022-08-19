package com.fonkwill.fogstorage.middleware.shared.domain;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import javax.persistence.*;

import java.io.Serializable;

/**
 * A Chunk.
 */
@Entity
@Table(name = "chunk")
public class Chunk implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    private Long id;

    @Column(name = "uuid")
    private String uuid;

    @Column(name = "size")
    private Long size;

    @Column(name = "accessed")
    private Integer accessed;

    @Column(name = "stored_locally")
    private Boolean storedLocally;

    @Column(name = "partner")
    private String partner;

    @ManyToOne
    @JsonIgnoreProperties("chunks")
    private Node node;

    // jhipster-needle-entity-add-field - JHipster will add fields here, do not remove
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUuid() {
        return uuid;
    }

    public Chunk uuid(String uuid) {
        this.uuid = uuid;
        return this;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public Long getSize() {
        return size;
    }

    public Chunk size(Long size) {
        this.size = size;
        return this;
    }

    public void setSize(Long size) {
        this.size = size;
    }

    public Integer getAccessed() {
        return accessed;
    }

    public Chunk accessed(Integer accessed) {
        this.accessed = accessed;
        return this;
    }

    public void setAccessed(Integer accessed) {
        this.accessed = accessed;
    }

    public Boolean isStoredLocally() {
        return storedLocally;
    }

    public Chunk storedLocally(Boolean storedLocally) {
        this.storedLocally = storedLocally;
        return this;
    }

    public void setStoredLocally(Boolean storedLocally) {
        this.storedLocally = storedLocally;
    }

    public String getPartner() {
        return partner;
    }

    public Chunk partner(String partner) {
        this.partner = partner;
        return this;
    }

    public void setPartner(String partner) {
        this.partner = partner;
    }

    public Node getNode() {
        return node;
    }

    public Chunk node(Node node) {
        this.node = node;
        return this;
    }

    public void setNode(Node node) {
        this.node = node;
    }
    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here, do not remove

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Chunk)) {
            return false;
        }
        return id != null && id.equals(((Chunk) o).id);
    }

    @Override
    public int hashCode() {
        return 31;
    }

    @Override
    public String toString() {
        return "Chunk{" +
            "id=" + getId() +
            ", uuid='" + getUuid() + "'" +
            ", size=" + getSize() +
            ", accessed=" + getAccessed() +
            ", storedLocally='" + isStoredLocally() + "'" +
            ", partner='" + getPartner() + "'" +
            "}";
    }
}
