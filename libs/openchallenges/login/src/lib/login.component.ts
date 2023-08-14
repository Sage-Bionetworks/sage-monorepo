import { Component, OnDestroy, OnInit } from '@angular/core';
import {
  FormsModule,
  ReactiveFormsModule,
  UntypedFormBuilder,
  UntypedFormControl,
  UntypedFormGroup,
  Validators,
} from '@angular/forms';
import { Router, RouterModule } from '@angular/router';
import { AuthModule, AuthService } from '@sagebionetworks/openchallenges/auth';
import { ConfigService } from '@sagebionetworks/openchallenges/config';
import { isApiClientError } from '@sagebionetworks/openchallenges/util';
import { Subscription } from 'rxjs';
import { BasicError as ApiClientError } from '@sagebionetworks/openchallenges/api-client-angular';
import { CommonModule } from '@angular/common';
import { MatLegacyButtonModule as MatButtonModule } from '@angular/material/legacy-button';
import { MatLegacyCardModule as MatCardModule } from '@angular/material/legacy-card';
import { MatLegacyFormFieldModule as MatFormFieldModule } from '@angular/material/legacy-form-field';
import { MatLegacyInputModule as MatInputModule } from '@angular/material/legacy-input';
import { UiModule } from '@sagebionetworks/openchallenges/ui';

@Component({
  selector: 'openchallenges-login',
  standalone: true,
  imports: [
    CommonModule,
    RouterModule,
    FormsModule,
    ReactiveFormsModule,
    MatButtonModule,
    MatCardModule,
    MatFormFieldModule,
    MatInputModule,
    AuthModule,
    UiModule,
  ],
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.scss'],
})
export class LoginComponent implements OnInit, OnDestroy {
  public appVersion: string;
  public dataUpdatedOn: string;

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
    this.dataUpdatedOn = this.configService.config.dataUpdatedOn;
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
