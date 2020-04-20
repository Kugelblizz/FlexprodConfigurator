import { Injectable } from "@angular/core";
import { HttpClient } from "@angular/common/http";
import { Marketplace } from "../../../../_model/marketplace";
import { ClassDefinition, ClassArchetype } from "../../../../_model/meta/Class";
import { PropertyDefinition } from "../../../../_model/meta/Property";
import { isNullOrUndefined } from "util";
import { of } from "rxjs";

@Injectable({
  providedIn: "root",
})
export class ClassDefinitionService {
  constructor(private http: HttpClient) {}

  getAllClassDefinitions(marketplace: Marketplace, tenantId: string) {
    return this.http.get(
      `${marketplace.url}/meta/core/class/definition/all/tenant/${tenantId}`
    );
  }

  getAllClassDefinitionsWithoutHeadAndEnums(
    marketplace: Marketplace,
    tenantId: string
  ) {
    return this.http.get(
      `${marketplace.url}/meta/core/class/definition/all/no-enum-no-head/tenant/${tenantId}`
    );
  }

  getAllClassDefinitionsWithoutRootAndEnums(
    marketplace: Marketplace,
    tenantId: string
  ) {
    return this.http.get(
      `${marketplace.url}/meta/core/class/definition/all/no-enum/tenant/${tenantId}`
    );
  }

  getClassDefinitionById(
    marketplace: Marketplace,
    id: string,
    tenantId: string
  ) {
    return this.http.get(
      `${marketplace.url}/meta/core/class/definition/${id}/tenant/${tenantId}`
    );
  }

  getClassDefinitionsById(
    marketplace: Marketplace,
    ids: string[],
    tenantId: string
  ) {
    if (!isNullOrUndefined(ids)) {
      return this.http.put(
        `${marketplace.url}/meta/core/class/definition/multiple/tenant/${tenantId}`,
        ids
      );
    } else {
      return of(null);
    }
  }

  // TODO

  createNewClassDefinition(marketplace: Marketplace, clazz: ClassDefinition) {
    return this.http.post(
      `${marketplace.url}/meta/core/class/definition/new`,
      clazz
    );
  }

  addOrUpdateClassDefintions(
    marketplace: Marketplace,
    classDefinitions: ClassDefinition[]
  ) {
    return this.http.put(
      `${marketplace.url}/meta/core/class/definition/add-or-update`,
      classDefinitions
    );
  }

  changeClassDefinitionName(
    marketplace: Marketplace,
    id: string,
    newName: string
  ) {
    return this.http.put(
      `${marketplace.url}/meta/core/class/definition/${id}/change-name`,
      newName
    );
  }

  deleteClassDefinitions(marketplace: Marketplace, ids: string[]) {
    return this.http.put(
      `${marketplace.url}/meta/core/class/definition/delete`,
      ids
    );
  }

  getAllChildrenIdMap(
    marketplace: Marketplace,
    rootClassIds: string[],
    tenantId: string
  ) {
    return this.http.put(
      `${marketplace.url}/meta/core/class/definition/get-children/tenant/${tenantId}`,
      rootClassIds
    );
  }

  getAllParentsIdMap(
    marketplace: Marketplace,
    childClassIds: string[],
    tenantId: string
  ) {
    return this.http.put(
      `${marketplace.url}/meta/core/class/definition/get-parents/tenant/${tenantId}`,
      childClassIds
    );
  }

  getClassPropertyFromPropertyDefinitionById(
    marketplace: Marketplace,
    propIds: String[],
    tenantId: string
  ) {
    return this.http.put(
      `${marketplace.url}/meta/core/class/definition/get-classproperty-from-propertydefinition-by-id/tenant/${tenantId}`,
      propIds
    );
  }

  // TODO

  addPropertiesToClassDefinitionById(
    marketplace: Marketplace,
    id: string,
    propIds: String[]
  ) {
    return this.http.put(
      `${marketplace.url}/meta/core/class/definition/${id}/add-properties-by-id`,
      propIds
    );
  }

  addPropertiesToClassDefinition(
    marketplace: Marketplace,
    id: string,
    propsToAdd: PropertyDefinition<any>[]
  ) {
    return this.http.put(
      `${marketplace.url}/meta/core/class/definition/${id}/add-properties`,
      propsToAdd
    );
  }

  removePropertiesFromClassDefinition(
    marketplace: Marketplace,
    id: string,
    propIds: string[]
  ) {
    return this.http.put(
      `${marketplace.url}/meta/core/class/definition/${id}/remove-properties`,
      propIds
    );
  }

  getEnumValuesFromEnumHeadClassDefinition(
    marketplace: Marketplace,
    classDefinitionId: string,
    tenantId: string
  ) {
    return this.http.get(
      `${marketplace.url}/meta/core/class/definition/enum-values/${classDefinitionId}/tenant/${tenantId}`
    );
  }

  getByArchetype(
    marketplace: Marketplace,
    archetype: ClassArchetype,
    tenantId: string
  ) {
    return this.http.get(
      `${marketplace.url}/meta/core/class/definition/archetype/${archetype}/tenant/${tenantId}`
    );
  }
}
