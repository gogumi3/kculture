import { api } from './client';
import { PlaceResponse } from './types';

export function fetchPlaces(params?: { regionSido?: string; category?: string }): Promise<PlaceResponse[]> {
  const qs = new URLSearchParams();
  if (params?.regionSido) qs.set('regionSido', params.regionSido);
  if (params?.category) qs.set('category', params.category);
  const suffix = qs.toString() ? `?${qs.toString()}` : '';
  return api.get<PlaceResponse[]>(`/places${suffix}`);
}

export function fetchPlace(id: number): Promise<PlaceResponse> {
  return api.get<PlaceResponse>(`/places/${id}`);
}
