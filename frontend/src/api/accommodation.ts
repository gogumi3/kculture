import { api } from './client';
import { AccommodationResponse } from './types';

export function fetchAccommodations(params?: { regionSido?: string; type?: string }): Promise<AccommodationResponse[]> {
  const qs = new URLSearchParams();
  if (params?.regionSido) qs.set('regionSido', params.regionSido);
  if (params?.type) qs.set('type', params.type);
  const suffix = qs.toString() ? `?${qs.toString()}` : '';
  return api.get<AccommodationResponse[]>(`/accommodations${suffix}`);
}

export function fetchAccommodationsNearby(
  lat: number,
  lng: number,
  radius = 3000
): Promise<AccommodationResponse[]> {
  return api.get<AccommodationResponse[]>(`/accommodations/nearby?lat=${lat}&lng=${lng}&radius=${radius}`);
}
