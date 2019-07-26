package com.fonkwill.fogstorage.middleware.domain;

import javax.persistence.*;

import java.io.Serializable;

import com.fonkwill.fogstorage.middleware.domain.enumeration.FogStorageNodeType;

/**
 * A Node.
 */
@Entity
@Table(name = "node")
public class Node implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    private Long id;

    @Column(name = "name")
    private String name;

    @Column(name = "url")
    private String url;

    @Enumerated(EnumType.STRING)
    @Column(name = "type")
    private FogStorageNodeType type;

    @Column(name = "latency")
    private Long latency;

    // jhipster-needle-entity-add-field - JHipster will add fields here, do not remove
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public Node name(String name) {
        this.name = name;
        return this;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() {
        return url;
    }

    public Node url(String url) {
        this.url = url;
        return this;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public FogStorageNodeType getType() {
        return type;
    }

    public Node type(FogStorageNodeType type) {
        this.type = type;
        return this;
    }

    public void setType(FogStorageNodeType type) {
        this.type = type;
    }

    public Long getLatency() {
        return latency;
    }

    public Node latency(Long latency) {
        this.latency = latency;
        return this;
    }

    public void setLatency(Long latency) {
        this.latency = latency;
    }
    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here, do not remove

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Node)) {
            return false;
        }
        return id != null && id.equals(((Node) o).id);
    }

    @Override
    public int hashCode() {
        return 31;
    }

    @Override
    public String toString() {
        return "Node{" +
            "id=" + getId() +
            ", name='" + getName() + "'" +
            ", url='" + getUrl() + "'" +
            ", type='" + getType() + "'" +
            ", latency=" + getLatency() +
            "}";
    }
}
