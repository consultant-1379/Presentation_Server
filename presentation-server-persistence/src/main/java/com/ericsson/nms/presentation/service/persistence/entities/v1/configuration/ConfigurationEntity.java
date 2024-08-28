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
package com.ericsson.nms.presentation.service.persistence.entities.v1.configuration;

import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotEmpty;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Objects;
import java.util.StringJoiner;

/**
 * <p>Represents the configuration table on the database.</p>
 * <p>Configuration is a simple etity that can be used to store property like configurations that should be shared by all service instances.
 * <br>
 * E.g: Max number of actions supported per application
 * </p>
 */
@Entity
@Table(name = "configuration")
public class ConfigurationEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Length(max = 150, message = "Configuration key must have at maximum 150 characters.")
    @NotEmpty(message = "Configuration key is required and can't be empty.")
    @Column(name = "config_key", length = 150, unique = true, nullable = false)
    private String key;

    @Length(max = 150, message = "Configuration value must have at maximum 250 characters.")
    @Column(name = "config_value", length = 250, unique = true, nullable = false)
    private String value;

    public ConfigurationEntity(final String key, final String value) {
        this.key = key;
        this.value = value;
    }

    public ConfigurationEntity() {
    }

    public Integer getId() {
        return id;
    }

    public void setId(final Integer id) {
        this.id = id;
    }

    public String getKey() {
        return key;
    }

    public void setKey(final String key) {
        this.key = key;
    }

    public String getValue() {
        return value;
    }

    public void setValue(final String value) {
        this.value = value;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final ConfigurationEntity that = (ConfigurationEntity) o;
        return key.equals(that.key);
    }

    @Override
    public int hashCode() {
        return Objects.hash(key);
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", ConfigurationEntity.class.getSimpleName() + "[", "]")
            .add("id=" + id)
            .add("key='" + key + "'")
            .add("value='" + value + "'")
            .toString();
    }
}
