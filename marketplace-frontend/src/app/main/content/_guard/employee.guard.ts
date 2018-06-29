import {Injectable} from '@angular/core';
import {CanActivate, Router} from '@angular/router';
import {LoginService} from '../_service/login.service';

@Injectable({
  providedIn: 'root'
})
export class EmployeeGuard implements CanActivate {

  constructor(private loginService: LoginService, private router: Router) {
  }

  canActivate(): Promise<boolean> {
    const promise = new Promise<boolean>(resolve => {
      this.loginService.getLoggedInParticipantRole()
        .toPromise()
        .then((role) => {
          resolve(role === 'EMPLOYEE');
        });
    });
    return promise;
  }
}
