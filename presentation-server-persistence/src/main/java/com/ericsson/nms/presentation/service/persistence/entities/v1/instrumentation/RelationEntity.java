/*------------------------------------------------------------------------------
 *******************************************************************************
 * COPYRIGHT Ericsson 2019
 *
 * The copyright to the computer program(s) herein is the property of
 * Ericsson Inc. The programs may be used and/or copied only with written
 * permission from Ericsson Inc. or in accordance with the terms and
 * conditions stipulated in the agreement/contract under which the
 * program(s) have been supplied.
 *******************************************************************************
 *----------------------------------------------------------------------------*/
package com.ericsson.nms.presentation.service.persistence.entities.v1.instrumentation;

import org.hibernate.annotations.Immutable;

import javax.persistence.*;
import java.util.Objects;
import java.util.StringJoiner;

/**
 * Entity mapped to the view database_relations.
 * This view is used to list all relations available in the database with their respective types and sizes.
 */
@Entity
@Immutable
@Table(name = "database_relations")
public class RelationEntity {

    @Id
    @Column(name = "name")
    private String name;

    @Column(name = "total_size")
    private String size;

    @Column(name = "type")
    @Enumerated(EnumType.STRING)
    private RelationType type;

    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public String getSize() {
        return size;
    }

    public void setSize(final String size) {
        this.size = size;
    }

    public RelationType getType() {
        return type;
    }

    public void setType(final RelationType type) {
        this.type = type;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final RelationEntity relations = (RelationEntity) o;
        return name.equals(relations.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", RelationEntity.class.getSimpleName() + "[", "]")
            .add("name='" + name + "'")
            .add("size='" + size + "'")
            .add("type=" + type)
            .toString();
    }
}
