package org.immregistries.ehr.fhir.ServerR4;

import ca.uhn.fhir.rest.annotation.Create;
import ca.uhn.fhir.rest.annotation.ResourceParam;
import ca.uhn.fhir.rest.api.MethodOutcome;
import ca.uhn.fhir.rest.api.server.RequestDetails;
import ca.uhn.fhir.rest.server.IResourceProvider;
import org.hl7.fhir.r4.model.IdType;
import org.hl7.fhir.r4.model.Immunization;
import org.immregistries.ehr.api.entities.Facility;
import org.immregistries.ehr.api.entities.VaccinationEvent;
import org.immregistries.ehr.api.repositories.VaccineRepository;
import org.immregistries.ehr.fhir.ServerR4.PatientProviderR4;
import org.immregistries.ehr.fhir.annotations.OnR4Condition;
import org.immregistries.ehr.logic.mapping.ImmunizationMapperR4;
import org.immregistries.ehr.api.repositories.FacilityRepository;
import org.immregistries.ehr.api.repositories.PatientRepository;
import org.immregistries.ehr.api.repositories.VaccinationEventRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Conditional;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.server.ResponseStatusException;

@Controller
@Conditional(OnR4Condition.class)
public class ImmunizationProviderR4 implements IResourceProvider {
    private static final Logger logger = LoggerFactory.getLogger(ImmunizationProviderR4.class);

    @Autowired
    private ImmunizationMapperR4 immunizationMapperR4;
    @Autowired
    private FacilityRepository facilityRepository;
    @Autowired
    private PatientRepository patientRepository;
    @Autowired
    private VaccinationEventRepository vaccinationEventRepository;
    @Autowired
    private VaccineRepository vaccineRepository;
    @Override
    public Class<Immunization> getResourceType() {
        return Immunization.class;
    }

    @Create
    public MethodOutcome createImmunization(@ResourceParam Immunization immunization, RequestDetails requestDetails) {

        return createImmunization(immunization,
                facilityRepository.findById(Integer.parseInt(requestDetails.getTenantId()))
                        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_ACCEPTABLE, "Invalid facility id")));
    }

    public MethodOutcome createImmunization(Immunization immunization, Facility facility) {
        MethodOutcome methodOutcome = new MethodOutcome();
        VaccinationEvent vaccinationEvent = immunizationMapperR4.fromFhir(immunization);
        vaccinationEvent.setAdministeringFacility(facility);
        Integer patientId = Integer.parseInt(immunization.getPatient().getReference().split("Patient/")[1]);//TODO Identifier
        vaccinationEvent.setPatient(
                patientRepository.findByFacilityIdAndId(facility.getId(), patientId)
                        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_ACCEPTABLE, "Invalid patient id")));
        // TODO set received information status and make sure history of patient info if already exists
        vaccineRepository.save(vaccinationEvent.getVaccine());
        vaccinationEvent = vaccinationEventRepository.save(vaccinationEvent);
        return methodOutcome.setId(new IdType().setValue(vaccinationEvent.getId().toString()));
    }
}
