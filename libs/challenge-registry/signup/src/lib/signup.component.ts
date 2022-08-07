import { Component, Inject, OnInit } from '@angular/core';
import {
  UntypedFormBuilder,
  UntypedFormControl,
  UntypedFormGroup,
  Validators,
} from '@angular/forms';
import { Router } from '@angular/router';
import {
  UserCreateRequest,
  UserService,
  ModelError as ApiClientError,
} from '@sagebionetworks/api-angular';
import {
  APP_CONFIG,
  AppConfig,
} from '@sagebionetworks/challenge-registry/config';
import { isApiClientError } from '@sagebionetworks/challenge-registry/util';

@Component({
  selector: 'challenge-registry-signup',
  templateUrl: './signup.component.html',
  styleUrls: ['./signup.component.scss'],
})
export class SignupComponent implements OnInit {
  public appVersion: string;

  newUserForm!: UntypedFormGroup;
  errors = {
    alreadyExists: false,
    other: undefined,
  } as { other?: string };

  submitted = false;

  constructor(
    private router: Router,
    private formBuilder: UntypedFormBuilder,
    private userService: UserService,
    @Inject(APP_CONFIG) private appConfig: AppConfig
  ) {
    this.appVersion = appConfig.appVersion;
  }

  ngOnInit(): void {
    this.newUserForm = this.formBuilder.group({
      email: new UntypedFormControl('awesome-user@example.org', [
        Validators.required,
        Validators.email,
      ]),
      password: new UntypedFormControl('yourpassword', [
        Validators.required,
        Validators.minLength(6),
        Validators.maxLength(64),
      ]),
      username: new UntypedFormControl('awesome-user', [
        Validators.required,
        Validators.minLength(3),
        Validators.maxLength(25),
        // forbiddenNameValidator(/bob/i) // <-- Here's how you pass in the custom
      ]),
    });
  }

  get email() {
    return this.newUserForm.get('email');
  }

  get password() {
    return this.newUserForm.get('password');
  }

  get username() {
    return this.newUserForm.get('username');
  }

  getEmailErrorMessage(): string {
    if (this.email?.hasError('required') || this.email?.hasError('email')) {
      return 'You must enter a valid email.';
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

  createUserAccount(): void {
    if (this.newUserForm.invalid) {
      return;
    }
    this.submitted = true;
    this.errors.other = undefined;

    const userCreateRequest: UserCreateRequest = {
      login: this.username?.value,
      email: this.email?.value,
      password: this.password?.value,
      name: '',
    };

    this.userService.createUser(userCreateRequest).subscribe(
      () => {
        this.router.navigate(['login']);
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
