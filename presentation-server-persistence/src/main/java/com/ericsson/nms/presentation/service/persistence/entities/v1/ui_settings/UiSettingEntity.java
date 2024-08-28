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
import javax.persistence.ManyToOne;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import java.io.Serializable;
import java.util.Date;
import java.util.Objects;

@Entity
@DynamicUpdate
@DynamicInsert
@SelectBeforeUpdate
@Table(name = "ui_setting")
public class UiSettingEntity implements Serializable {

    public UiSettingEntity(final String name, final String value) {
        this.name = name;
        this.value = value;
    }

    public UiSettingEntity() {
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(unique = true, nullable = false)
    private Long id;

    @NaturalId
    @NotEmpty(message = "The setting name is required to create a setting.")
    @Length(max = 100, message = "The setting name must have a maximum size of 100 characters.")
    private String name;

    @Length(max = 64000, message = "The setting value must have a maximum size of 64K characters.")
    private String value;

    @NaturalId
    @ManyToOne(optional = false, fetch = FetchType.LAZY, cascade = {CascadeType.REFRESH, CascadeType.MERGE, CascadeType.PERSIST})
    @JoinColumn(name = "setting_group_id", nullable = false, insertable = false, updatable = false)
    private UiSettingGroupEntity settingGroup;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(insertable = true, updatable = false, nullable = false)
    private Date created;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(insertable = false, updatable = true, nullable = true)
    private Date lastUpdated;

    @PrePersist
    private void onCreate() {
        created = new Date();
    }

    @PreUpdate
    private void onUpdate() {
        lastUpdated = new Date();
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

    public String getValue() {
        return value;
    }

    public void setValue(final String value) {
        this.value = value;
    }

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }

    public Date getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(Date lastUpdated) {
        this.lastUpdated = lastUpdated;
    }

    public UiSettingGroupEntity getSettingGroup() {
        return settingGroup;
    }

    public void setSettingGroup(UiSettingGroupEntity settingGroup) {
        this.settingGroup = settingGroup;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UiSettingEntity entity = (UiSettingEntity) o;
        return Objects.equals(id, entity.id) &&
                Objects.equals(name, entity.name) &&
                Objects.equals(value, entity.value) &&
                Objects.equals(settingGroup, entity.settingGroup);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, value, settingGroup);
    }

    @Override
    public String toString() {
        return "UiSettingEntity{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", value='" + value + '\'' +
                ", settingGroup=" + settingGroup +
                '}';
    }
}
