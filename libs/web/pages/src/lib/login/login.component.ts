import { Component, Inject, OnDestroy, OnInit } from '@angular/core';
import {
  FormBuilder,
  FormControl,
  FormGroup,
  Validators,
} from '@angular/forms';
import { Router } from '@angular/router';
import { AppConfig, APP_CONFIG } from '@challenge-registry/web/config';
import { Subscription } from 'rxjs';

@Component({
  selector: 'challenge-registry-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.scss'],
})
export class LoginComponent implements OnInit, OnDestroy {
  public appVersion: string;

  signinForm!: FormGroup;
  errors = {
    other: undefined,
  } as { other?: string };

  submitted = false;
  private subscriptions: Subscription[] = [];

  constructor(
    private router: Router,
    private formBuilder: FormBuilder,
    @Inject(APP_CONFIG) private appConfig: AppConfig // private authService: AuthService
  ) {
    this.appVersion = appConfig.appVersion;
  }

  ngOnInit(): void {
    this.signinForm = this.formBuilder.group({
      username: new FormControl('awesome-user', [
        Validators.required,
        Validators.minLength(3),
        Validators.maxLength(25),
      ]),
      password: new FormControl('yourpassword', [
        Validators.required,
        Validators.minLength(6),
        Validators.maxLength(64),
      ]),
    });
    // const signedInSub = this.authService.isSignedIn().subscribe((signedIn) => {
    //   if (signedIn) {
    //     this.router.navigate([this.authService.getRedirectUrl()]);
    //   }
    // });
    // this.subscriptions.push(signedInSub);
  }

  ngOnDestroy(): void {
    this.subscriptions.forEach((sub) => sub.unsubscribe());
  }

  get username() {
    return this.signinForm.get('username');
  }

  get password() {
    return this.signinForm.get('password');
  }

  getUsernameErrorMessage(): string {
    if (
      this.username?.hasError('required') ||
      this.username?.hasError('minlength') ||
      this.username?.hasError('maxlength')
    ) {
      return 'A username between 3 and 25 characters is required.';
    } else if (this.username?.hasError('alreadyExists')) {
      return `The username ${this.username?.value} is not available.`;
    }
    return '';
  }

  getPasswordErrorMessage(): string {
    if (
      this.password?.hasError('required') ||
      this.password?.hasError('minlength') ||
      this.password?.hasError('maxlength')
    ) {
      return 'A password between 6 and 64 characters is required.';
    }
    return '';
  }

  signin(): void {
    if (this.signinForm.invalid) {
      return;
    }
    this.submitted = true;
    this.errors.other = undefined;

    // this.authService
    //   .signin(this.username?.value, this.password?.value)
    //   .subscribe(
    //     () => {
    //       this.router.navigateByUrl(this.authService.getRedirectUrl());
    //       this.authService.setRedirectUrl('/');
    //     },
    //     (err) => {
    //       const error = err.error as RoccClientError;
    //       if (isRoccClientError(error)) {
    //         if (error.status === 409) {
    //           this.username?.setErrors({
    //             alreadyExists: true,
    //           });
    //         } else {
    //           this.errors.other = `Server error: ${error.title}`;
    //         }
    //       }
    //     }
    //   );
  }
}
