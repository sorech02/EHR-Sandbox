import { Component, Inject, Input, OnInit } from '@angular/core';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { MatSnackBar } from '@angular/material/snack-bar';
import { FacilityService } from 'src/app/_services/facility.service';
import { Hl7Service } from 'src/app/_services/hl7.service';
import { VaccinationService } from 'src/app/_services/vaccination.service';

@Component({
  selector: 'app-hl7-messaging',
  templateUrl: './hl7-messaging.component.html',
  styleUrls: ['./hl7-messaging.component.css']
})
export class Hl7MessagingComponent implements OnInit {

  @Input() vaccinationId: number = -1;
  @Input() patientId: number = -1;

  public vxu: string = "";
  public answer: string = "";
  public error: string = "";

  constructor(private vaccinationService: VaccinationService,
    private hl7Service: Hl7Service,
    private _snackBar: MatSnackBar,
    public _dialogRef: MatDialogRef<Hl7MessagingComponent>,
    @Inject(MAT_DIALOG_DATA) public data: {patientId: number, vaccinationId: number}) {
      this.patientId = data.patientId
      this.vaccinationId = data.vaccinationId
     }

  ngOnInit(): void {
    this.hl7Service.quickGetVXU(this.patientId,this.vaccinationId).subscribe((res) => {
      this.vxu = res
    })
  }

  send() {
    this.hl7Service.quickPostVXU(this.patientId,this.vaccinationId, this.vxu).subscribe(
      (res) => {
        this.answer = res
        this.error = ""
      },
      (err) => {
        this.answer = ""
        if (err.error.text) {
          this.error = err.error.text
        } else {
          this.error = err.error
        }
        console.error(err)
      }
    )
  }
}
