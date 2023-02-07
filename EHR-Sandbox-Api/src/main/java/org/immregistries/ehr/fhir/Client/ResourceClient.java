package org.immregistries.ehr.fhir.Client;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.rest.api.MethodOutcome;
import ca.uhn.fhir.rest.client.api.IGenericClient;
import org.hl7.fhir.instance.model.api.IBaseResource;
import org.immregistries.ehr.api.entities.ImmunizationRegistry;
import org.immregistries.ehr.api.repositories.ImmunizationRegistryRepository;
import org.immregistries.ehr.api.security.UserDetailsServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public abstract class ResourceClient<Identifier> implements IResourceClient<Identifier>{

    @Autowired
    private ImmunizationRegistryRepository immunizationRegistryRepository;
    @Autowired
    private UserDetailsServiceImpl userDetailsService;
    @Autowired
    FhirContext fhirContext;
    @Autowired
    CustomClientBuilder customClientBuilder;

    private final DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");

    public String read(String resourceType, String resourceId, ImmunizationRegistry ir){
        return read(resourceType, resourceId, customClientBuilder.newGenericClient(ir));
    }

    public MethodOutcome create(IBaseResource resource, ImmunizationRegistry ir){
        return create(resource, customClientBuilder.newGenericClient(ir));
    }

    public MethodOutcome delete(String resourceType, String resourceId, ImmunizationRegistry ir){
        return delete(resourceType, resourceId, customClientBuilder.newGenericClient(ir));
    }

    public MethodOutcome update(IBaseResource resource, String resourceId, ImmunizationRegistry ir){
        return update(resource, resourceId, customClientBuilder.newGenericClient(ir));
    }

    public String read(String resourceType, String resourceId, IGenericClient client) {
        IBaseResource resource;
        resource = client.read().resource(resourceType).withId(resourceId).execute();
        return fhirContext.newXmlParser().setPrettyPrint(true).encodeResourceToString(resource);
    }

    public MethodOutcome create(IBaseResource resource, IGenericClient client) {
        MethodOutcome outcome;
        outcome = client.create().resource(resource).execute();
        return outcome;
    }
    
}
