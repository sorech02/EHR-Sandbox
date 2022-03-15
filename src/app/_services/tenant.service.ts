import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Tenant } from '../_model/rest';
import { BehaviorSubject, EMPTY, from, Observable } from 'rxjs';
import { SettingsService } from './settings.service';
import { observeNotification } from 'rxjs/internal/Notification';
import { FacilityService } from './facility.service';

const httpOptions = {
  headers: new HttpHeaders({ 'Content-Type': 'application/json' })
};
@Injectable({
  providedIn: 'root'
})
export class TenantService {
  private tenant: BehaviorSubject<Tenant>;

  public getObservableTenant(): Observable<Tenant> {
    return this.tenant.asObservable();
  }

  public getTenant(): Tenant {
    return this.tenant.value
  }

  public getTenantId(): number {
    return this.tenant.value.id
  }

  public setTenant(tenant: Tenant) {
    this.facilityService.setFacility({id: -1})
    this.tenant.next(tenant)
  }

  public setTenantEmpty() {
    this.tenant.next({id: -1})
  }

  constructor(private http: HttpClient, private settings: SettingsService, private facilityService: FacilityService ) {
    this.tenant= new BehaviorSubject<Tenant>({id:-1})
   }

  readTenants(): Observable<Tenant[]> {
    return this.http.get<Tenant[]>(
      this.settings.getApiUrl() + '/tenants', httpOptions);
  }

  readTenant(tenantId: number): Observable<Tenant> {
    return this.http.get<Tenant>(
      `${this.settings.getApiUrl()}/tenants/${tenantId}`, httpOptions);
  }

  postTenant(tenant: Tenant): Observable<Tenant> {
    return this.http.post<Tenant>(
      this.settings.getApiUrl()
      + '/tenants',
      tenant, httpOptions);
  }
}
