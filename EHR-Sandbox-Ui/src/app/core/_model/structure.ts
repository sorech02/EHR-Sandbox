import { Clinician, Facility, EhrPatient, VaccinationEvent, Vaccine } from "./rest";
/**
 * Interface for codemaps lists
 */

export interface CodeBaseMap {
  [key:string]: CodeMap
}
export interface CodeMap {
  [key:string]: Code
}
export interface Code {
  "value": string,
  "label": string,
  "description"?: string,
  "codeStatus"?: string,
  "reference"?: CodeReference,
  "useDate"?: Date,
  "useAge"?: string,
  "conceptType"?: string,
  "testAge"?: string
}
export interface CodeReference {
  linkTo: CodeReferenceLink[]
}

export interface CodeReferenceLink {
  value: string,
  codeset: string
}
export interface FormCard {
  title: string,
  cols?: number, // dimensions of the card
  rows?: number,
  patientForms?: PatientForm[],  // form fields for each specific objects
  vaccinationForms?: VaccinationForm[],
  vaccineForms?: VaccineForm[],
  clinicianForms?: ClinicianForm[],
}

export enum formType {
  text = 'text',
  date = 'date',
  short = 'short',
  boolean = 'boolean',
  select = 'select',
  yesNo = 'yesNo',
  code = 'code',
  textarea = 'textarea',
}
export interface BaseForm {
  type: formType,
  title: string,
  attribute: string,
  codeMapLabel?: string,
  options?: {value: string, label?: string}[],
}
export interface PatientForm extends BaseForm{
  attribute: keyof EhrPatient,
}
export interface VaccinationForm extends BaseForm{
  attribute: keyof VaccinationEvent,
}
export interface VaccineForm extends BaseForm{
  attribute: keyof Vaccine,
}
export interface ClinicianForm extends BaseForm{
  attribute: keyof Clinician,
  role: "enteringClinician" | "orderingClinician" | "administeringClinician"
}

export interface NotificationPrototype {
  facility: Facility | number,
  timestamp: string,
}


export interface ComparisonResult {[index:string]: ComparisonResult | any | null}
