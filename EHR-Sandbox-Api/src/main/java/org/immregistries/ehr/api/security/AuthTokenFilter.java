package org.immregistries.ehr.api.security;
import java.io.IOException;
import java.util.Scanner;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import ca.uhn.fhir.rest.server.exceptions.InvalidRequestException;
import org.apache.commons.codec.binary.Base64;
import org.immregistries.ehr.api.entities.*;
import org.immregistries.ehr.api.repositories.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

/**
 * Filter checking for user authorization on each request
 */
public class AuthTokenFilter extends OncePerRequestFilter {
    @Autowired
    private JwtUtils jwtUtils;
    @Autowired
    private UserDetailsServiceImpl userDetailsService;
    @Autowired
    private TenantRepository tenantRepository;
    @Autowired
    private EhrPatientRepository patientRepository;
    @Autowired
    private FacilityRepository facilityRepository;
    @Autowired
    private VaccinationEventRepository vaccinationEventRepository;
    @Autowired
    private ClinicianRepository clinicianRepository;
    @Autowired
    private ImmunizationRegistryRepository immunizationRegistryRepository;

    private static final Logger logger = LoggerFactory.getLogger(AuthTokenFilter.class);
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        boolean authorized = false;
        try {
            String jwt = parseJwt(request);
            if (jwt != null && jwtUtils.validateJwtToken(jwt)) {
                String username = jwtUtils.getUserNameFromJwtToken(jwt);
                UserDetails userDetails = userDetailsService.loadUserByUsername(username);
                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities());
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authentication);
                // Checking authorization if path matches "/tenants/{tenantId}/**"
                authorized = filterUrl(request);

            } // TODO figure why http security configuration does not skip filter
            else if (request.getServletPath().startsWith("/auth") || request.getServletPath().startsWith("/$create") || request.getServletPath().startsWith("/fhir") || request.getServletPath().startsWith("/") ) { // for registration no authorization needed
                authorized = true;
            } else if ( request.getServletPath().split("/").length == 1 &&
                    (request.getServletPath().endsWith(".js") || request.getServletPath().endsWith(".ico") || request.getServletPath().endsWith(".css") ) ) {

            }
        } catch (Exception e) {
            logger.error("Cannot set user authentication: {}", e);
        }
        if (authorized) {
            filterChain.doFilter(request, response);
        } else {
            logger.error("Unauthorized request {}", request.getServletPath());
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Error: Unauthorized");
        }
    }
    private String parseJwt(HttpServletRequest request) {
        String headerAuth = request.getHeader("Authorization");
        if (StringUtils.hasText(headerAuth) && headerAuth.startsWith("Bearer ")) {
            return headerAuth.substring(7);
        }
        return null;
    }

    /**
     * Currently not used, for future functionality
     * @param request
     * @return
     */
    private User parseBasic(HttpServletRequest request) {
        String headerAuth = request.getHeader("Authorization");
        if (StringUtils.hasText(headerAuth) && headerAuth.startsWith("Basic ")) {
            String base64 = headerAuth.substring("Basic ".length());
            String base64decoded = new String(Base64.decodeBase64(base64));
            String[] parts = base64decoded.split(":");
            User user = new User();
            user.setUsername(parts[0]);
            user.setPassword(parts[1]);
            return user;
        }
        return null;
    }

    /**
     * TODO improve by reversing the logic
     * @param request
     * @return
     * @throws InvalidRequestException
     */
    private boolean filterUrl(HttpServletRequest request) throws InvalidRequestException {
        String url = request.getServletPath();
//        logger.info("{}", url);
        int tenantId = -1;
        int facilityId = -1;
        int clinicianId = -1;
        int registryId = -1;
        String patientId = null;
        String vaccinationId = null;
        // Parsing the URI
        String[] split = url.split("/");
        int len = split.length;
        Scanner scanner = new Scanner(url).useDelimiter("/");
        String item = "";
        if(scanner.hasNext()) {
            item = scanner.next();
            /**
             * Fhir Server
             */
            if (item.equals("fhir")) {
                return true;
            }
            /**
             * Fhir client
             */
            if (item.equals("imm-registry")) {
                if (scanner.hasNextInt()) {
                    registryId = scanner.nextInt();
                    ImmunizationRegistry immunizationRegistry = immunizationRegistryRepository.findByIdAndUserId(registryId,userDetailsService.currentUserId())
                            .orElseThrow(() -> new InvalidRequestException("invalid registry id"));
                }
            }
            if (item.equals("tenants") ) {
                if (scanner.hasNextInt()) {
                    tenantId = scanner.nextInt();
                    Tenant tenant = tenantRepository.findByIdAndUserId(tenantId, userDetailsService.currentUserId())
                            .orElseThrow(() -> new InvalidRequestException("invalid tenant id"));
                    request.setAttribute("tenant", tenant);
                }
            }
            if (item.equals("facilities") ) {
                if (scanner.hasNextInt()) {
                    facilityId = scanner.nextInt();
                    Facility facility = facilityRepository.findByUserAndId(userDetailsService.currentUser(), facilityId)
                            .orElseThrow(() -> new InvalidRequestException("invalid facility id"));
                    request.setAttribute("facility", facility);
                }
            }
        }


        if(scanner.hasNext()) {
            item = scanner.next();
            if (item.equals("facilities") ) {
                if (scanner.hasNextInt()) {
                    facilityId = scanner.nextInt();
                    Facility facility = facilityRepository.findByIdAndTenantId(facilityId, tenantId)
                            .orElseThrow(() -> new InvalidRequestException("invalid facility id"));
                    request.setAttribute("facility", facility);
                }
            }else if (item.equals("clinicians") ) {
                if (scanner.hasNextInt()) {
                    clinicianId = scanner.nextInt();
                    Clinician clinician = clinicianRepository.findByTenantIdAndId(tenantId,clinicianId)
                            .orElseThrow(() -> new InvalidRequestException("invalid clinician id"));
                    request.setAttribute("clinician", clinician);
                }
            }
        }


        if(scanner.hasNext()) {
            item = scanner.next();
            if (item.equals("patients") ) {
                if (scanner.hasNextInt()) {
                    patientId = scanner.next();
                    EhrPatient ehrPatient = patientRepository.findByFacilityIdAndId(facilityId,patientId)
                            .orElseThrow(() -> new InvalidRequestException("invalid patient id"));
                    request.setAttribute("patient", ehrPatient);
                }
            }
            if (item.equals("vaccinations") ) {
                if (scanner.hasNextInt()) {
                    vaccinationId = scanner.next();
                    VaccinationEvent vaccinationEvent = vaccinationEventRepository.findByAdministeringFacilityIdAndId(facilityId,vaccinationId)
                            .orElseThrow(() -> new InvalidRequestException("invalid vaccination id"));
                    request.setAttribute("vaccinationEvent", vaccinationEvent);
                }
            }
        }


        if(scanner.hasNext()) {
            item = scanner.next();
            if (item.equals("vaccinations") ) {
                if (scanner.hasNextInt()) {
                    vaccinationId = scanner.next();
                    VaccinationEvent vaccinationEvent = vaccinationEventRepository.findByPatientIdAndId(patientId,vaccinationId)
                            .orElseThrow(() -> new InvalidRequestException("invalid vaccination id"));
                    request.setAttribute("vaccinationEvent", vaccinationEvent);
                }
            }
        }
        return true;
    }
}