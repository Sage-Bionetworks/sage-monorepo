# Enable SSO with Keycloak

- [Enable Google SSO](#Enable-Google-SSO)
  - [Create a Google Application](#Create-a-Google-Application)
  - [Create a Google identify provider in Keycloak](#Create-a-Google-identify-provider-in-Keycloak)
- [Log in to the OpenChallenges with a Google account](#Log-in-to-the-Challenge-Registry-with-a-Google-account)
- [Notes](#Notes)

## Enable Google SSO

### Create a Google Application

1. Head to the [Google Cloud Console], login with your (Sage) Google Account.
2. Click on the dropdown near the Google Cloud logo.
3. Click on `New Project`.
4. Enter the information about the project.
    - Project name: `openchallenges-keycloak-google`
    - Organization: `sagebase.org`
    - Location: `sagebase.org`
5. Click on `Create`.
6. Select the new project by clicking on the dropdown near the Google Cloud logo.
7. Click on `Credentials` in the left menu.
8. Click on `Configure Consent Screen`.
9. Enter the OAuth consent screen information.
    - User type: `Internal` (suitable for testing)
10. Click on `Create`.
11. Enter the App information.
    - App name: `Challenge Keycloak Test App`
    - User support email: `<support email>`
    - Specify app logo (Optional)
12. Enter the App domain information.
    - Application home page: `http://localhost:4200` (OpenChallenges web app)
    - Authorized domain 1: `openchallenges.org` (Optional)
13. Enter the Developer contact information.
    - Email addresses: `<contact addresses>`
14. Click on `Save and Continue`.
15. Enter Scopes information.
  - Click on `Add or Remove Scopes`.
  - Add these scopes:
    - `.../auth/userinfo.email`
    - `.../auth/userinfo.profile`
    - `openid`
  - Click on `Update`.
16. Click on `Save and Continue`.
17. Review the app registration information and click on `Back to Dashboard`.
18. Go back to the Credentials page and click on `Create Credentials`.
19. Select `OAuth client ID`.
20. Enter the client ID information.
    - Application type: `Web application`
    - Name: `Challenge Keycloak Test Client`
    - Authorized redirect URIs
      - `http://localhost:8080/realms/test/broker/google/endpoint`
21. Click on `Create`.
22. Click on `Download JSON` to download the Client ID and Client Secret.
23. Click on `OK`.

### Create a Google identify provider in Keycloak

1. Access the realm in Keycloak Admin console.
2. Left menu > Click on `Identify Providers`
3. Social section > Click on `Google`
4. Note the `Redirect URI` value which will be used in the next steps.
5. Specify the Client ID and Client Secret in the `Add Google provider`.
6. Click on `Add`.

## Log in to the OpenChallenges with a Google account

1. Click on `Log In`.
2. The option to sign in with Google should now be available. Click on `Google`.
3. Select your (Sage) Google account.
4. Upon successful login, you should be redirected to the app.

## Notes

- With the above configuration, the username of the user upon registration is set to the Google
  email address of the user. We need to find a way to allow the user to specify another username
  value during the registration proces.

<!-- Links -->

[Google Cloud Console]: https://console.cloud.google.com/apis/dashboard