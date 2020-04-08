import { Injectable } from "@angular/core";
import { HttpClient } from "@angular/common/http";

@Injectable({
  providedIn: "root",
})
export class CoreVolunteerService {
  constructor(private http: HttpClient) {}

  findAll() {
    return this.http.get(`/core/volunteer/all`);
  }

  findById(volunteerId: string) {
    return this.http.get(`/core/volunteer/${volunteerId}`);
  }

  findRegisteredMarketplaces(volunteerId: string) {
    return this.http.get(`/core/volunteer/${volunteerId}/marketplaces`);
  }

  subscribeTenant(
    volunteerId: string,
    marketplaceId: string,
    tenantId: string
  ) {
    return this.http.post(
      `/core/volunteer/${volunteerId}/subscribe/${marketplaceId}/tenant/${tenantId}`,
      {}
    );
  }

  unsubscribeTenant(
    volunteerId: string,
    marketplaceId: string,
    tenantId: string
  ) {
    return this.http.post(
      `/core/volunteer/${volunteerId}/unsubscribe/${marketplaceId}/tenant/${tenantId}`,
      {}
    );
  }
}
