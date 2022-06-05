import { Injectable } from '@angular/core';

@Injectable({
  providedIn: 'root',
})
export class TokenService {
  public getToken(): string {
    const token = localStorage.getItem('access_token');
    return token === null ? '' : token;
  }

  public setToken(token: string): void {
    localStorage.setItem('access_token', token);
  }

  public deleteToken(): void {
    localStorage.removeItem('access_token');
  }
}
