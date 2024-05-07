import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable, map } from 'rxjs';
import { environment } from 'src/environments/environment';
import { URI } from 'src/shared/types';

@Injectable({
  providedIn: 'root'
})
export class ImageService {
  apiUrl = `${environment.apiUrl}/images`;

  constructor(private http: HttpClient) { }

  uploadImage(image: File): Observable<URI> {
    const formData = new FormData();
    formData.append('image', image);

    return this.http.post(this.apiUrl, formData, { observe: 'response' }).pipe(
      map(response => {
        return response.headers.get('Location')!;
      })
    );
  }

  deleteImage(uri: URI) {
    return this.http.delete(uri);
  }
}

export interface ImageResponseType {
  value: URI;
}