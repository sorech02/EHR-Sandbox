package org.immregistries.ehr.api.entities;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import net.minidev.json.annotate.JsonIgnore;

import javax.persistence.*;
import java.util.LinkedHashSet;
import java.util.Set;

@Entity
@Table(name = "ehr_group")
@JsonIdentityInfo(
        generator = ObjectIdGenerators.PropertyGenerator.class,
        property = "id",
        scope = EhrGroup.class)
public class EhrGroup {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "group_id", nullable = false)
    private Integer id;
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "facility_id", nullable = false)
    @JsonIgnore
    private Facility facility;
    @Column(name = "name", nullable = false)
    private String name;
    @Column(name = "description")
    private String description;
    @Column(name = "type")
    private String type;
    @Column(name = "code")
    private String code;
    @ManyToOne
    @JoinColumn(name = "immunization_registry_id")
    private ImmunizationRegistry immunizationRegistry;
    @ManyToMany(cascade = {CascadeType.DETACH, CascadeType.PERSIST, CascadeType.REFRESH}, fetch = FetchType.EAGER)
    @JoinTable(name = "group_members",
            joinColumns = @JoinColumn(name = "group_id"),
            inverseJoinColumns = @JoinColumn(name = "patient_id"))
    private Set<EhrPatient> patientList;

    @OneToMany(orphanRemoval = true)
    @JoinColumn(name = "group_id")
    private Set<EhrGroupCharacteristic> ehrGroupCharacteristics = new LinkedHashSet<>();

    public Set<EhrGroupCharacteristic> getEhrGroupCharacteristics() {
        return ehrGroupCharacteristics;
    }

    public void setEhrGroupCharacteristics(Set<EhrGroupCharacteristic> ehrGroupCharacteristics) {
        this.ehrGroupCharacteristics = ehrGroupCharacteristics;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Facility getFacility() {
        return facility;
    }

    public void setFacility(Facility facility) {
        this.facility = facility;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public Set<EhrPatient> getPatientList() {
        return patientList;
    }

    public void setPatientList(Set<EhrPatient> patientList) {
        this.patientList = patientList;
    }

    public ImmunizationRegistry getImmunizationRegistry() {
        return immunizationRegistry;
    }

    public void setImmunizationRegistry(ImmunizationRegistry immunizationRegistry) {
        this.immunizationRegistry = immunizationRegistry;
    }
}