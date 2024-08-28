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
package com.ericsson.nms.presentation.service.persistence.entities.v1.ui_settings;

import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.NaturalId;
import org.hibernate.annotations.SelectBeforeUpdate;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotEmpty;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.Valid;
import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.StringJoiner;

@Entity
@DynamicUpdate
@DynamicInsert
@SelectBeforeUpdate
@Table(name = "ui_setting_group")
public class UiSettingGroupEntity implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(unique = true, nullable = false)
    private Long id;

    @NaturalId
    @NotEmpty(message = "The setting group must have a name.")
    @Length(max = 100, message = "The setting group name must have a maximum size of 100 characters.")
    private String name;

    @NaturalId
    @NotEmpty(message = "The setting group must have a username.")
    @Length(max = 250, message = "The username must have a maximum size of 250 characters.")
    private String username;

    @NaturalId
    @NotEmpty(message = "The setting group must have an application.")
    @Length(max = 200, message = "The application must have a maximum size of 200 characters.")
    private String application;

    //todo: [EAP7-related] we should really use mappedBy on this end but it seems like it's not working with this version of Hibernate
    @OneToMany(cascade = {CascadeType.ALL}, orphanRemoval = true,
            fetch = FetchType.LAZY)
    @JoinColumn(name = "setting_group_id", nullable = false, insertable = true, updatable = false)
    @Valid
    private List<UiSettingEntity> settings;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name="migration_date")
    private Date migrationDate;

    public UiSettingGroupEntity() {
    }

    public UiSettingGroupEntity(final String name, final String username, final String application) {
        this.name = name;
        this.username = username;
        this.application = application;
    }

    public Long getId() {
        return id;
    }

    public void setId(final Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(final String username) {
        this.username = username;
    }

    public String getApplication() {
        return application;
    }

    public void setApplication(final String application) {
        this.application = application;
    }

    public List<UiSettingEntity> getSettings() {
        return settings;
    }

    public void setSettings(final List<UiSettingEntity> settings) {
        this.settings = settings;
    }

    public Date getMigrationDate() {
        return migrationDate;
    }

    public void setMigrationDate(final Date migationDate) {
        this.migrationDate = migationDate;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final UiSettingGroupEntity that = (UiSettingGroupEntity) o;
        return Objects.equals(id, that.id) &&
                Objects.equals(name, that.name) &&
                Objects.equals(username, that.username) &&
                Objects.equals(application, that.application);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, username, application);
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", UiSettingGroupEntity.class.getSimpleName() + "[", "]")
            .add("id=" + id)
            .add("name='" + name + "'")
            .add("username='" + username + "'")
            .add("application='" + application + "'")
            .add("migrationDate=" + migrationDate)
            .toString();
    }
}
