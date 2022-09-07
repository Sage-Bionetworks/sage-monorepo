import { Component, OnDestroy, OnInit } from '@angular/core';
import {
  UntypedFormBuilder,
  UntypedFormControl,
  UntypedFormGroup,
  Validators,
} from '@angular/forms';
import { Router } from '@angular/router';
import { AuthService } from '@sagebionetworks/challenge-registry/auth';
import { ConfigService } from '@sagebionetworks/challenge-registry/config';
import { isApiClientError } from '@sagebionetworks/challenge-registry/util';
import { Subscription } from 'rxjs';
import { ModelError as ApiClientError } from '@sagebionetworks/api-angular';

@Component({
  selector: 'challenge-registry-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.scss'],
})
export class LoginComponent implements OnInit, OnDestroy {
  public appVersion: string;

  loginForm!: UntypedFormGroup;
  errors = {
    other: undefined,
  } as { other?: string };

  submitted = false;
  private subscriptions: Subscription[] = [];

  constructor(
    private router: Router,
    private formBuilder: UntypedFormBuilder,
    private authService: AuthService,
    private readonly configService: ConfigService
  ) {
    this.appVersion = this.configService.config.appVersion;
  }

  ngOnInit(): void {
    this.loginForm = this.formBuilder.group({
      username: new UntypedFormControl('tschaffter', [
        Validators.required,
        Validators.minLength(3),
        Validators.maxLength(25),
      ]),
      password: new UntypedFormControl('yourpassword', [
        Validators.required,
        Validators.minLength(6),
        Validators.maxLength(64),
      ]),
    });

    const loggedInSub = this.authService.isLoggedIn().subscribe((loggedIn) => {
      if (loggedIn) {
        this.router.navigate([this.authService.getRedirectUrl()]);
      }
    });
    this.subscriptions.push(loggedInSub);
  }

  ngOnDestroy(): void {
    this.subscriptions.forEach((sub) => sub.unsubscribe());
  }

  get username() {
    return this.loginForm.get('username');
  }

  get password() {
    return this.loginForm.get('password');
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

  login(): void {
    if (this.loginForm.invalid) {
      return;
    }
    this.submitted = true;
    this.errors.other = undefined;

    this.authService
      .login(this.username?.value, this.password?.value)
      .subscribe(
        () => {
          // this.router.navigateByUrl(this.authService.getRedirectUrl());
          // this.authService.setRedirectUrl('/');
        },
        (err) => {
          const error = err.error as ApiClientError;
          if (isApiClientError(error)) {
            if (error.status === 409) {
              this.username?.setErrors({
                alreadyExists: true,
              });
            } else {
              this.errors.other = `Server error: ${error.title}`;
            }
          }
        }
      );
  }
}
