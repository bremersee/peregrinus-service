/*
 * Copyright 2018 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.bremersee.peregrinus.geo.mapper.google;

import java.util.Arrays;
import java.util.Collection;
import javax.validation.constraints.NotNull;
import org.bremersee.common.model.Address;
import org.bremersee.geojson.utils.GeometryUtils;
import org.bremersee.google.maps.model.AddressComponent;
import org.bremersee.google.maps.model.AddressComponentType;
import org.bremersee.google.maps.model.Bounds;
import org.bremersee.google.maps.model.GeocodingRequest;
import org.bremersee.google.maps.model.GeocodingResult;
import org.bremersee.google.maps.model.LatLng;
import org.bremersee.peregrinus.geo.model.GeoCodingQueryRequest;
import org.bremersee.peregrinus.geo.model.GeoCodingResult;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.Polygon;
import org.springframework.util.StringUtils;

/**
 * @author Christian Bremer
 */
public class GoogleMapperImpl implements GoogleMapper {

  @Override
  public @NotNull GeocodingRequest mapToGeocodeRequest(@NotNull GeoCodingQueryRequest source) {
    return GeocodingRequest
        .builder()
        .bounds(mapToBounds(source))
        .language(source.getLanguage())
        .region(source.getCountries() != null && !source.getCountries().isEmpty()
            ? source.getCountries().get(0)
            : null)
        .query(source.getQuery())
        .build();
  }

  private Bounds mapToBounds(GeoCodingQueryRequest source) {
    final double[] bbox = source.toBoundingBox();
    if (bbox == null) {
      return null;
    }
    final Coordinate ne = GeometryUtils.getNorthEast(bbox);
    final Coordinate sw = GeometryUtils.getSouthWest(bbox);
    final Bounds bounds = new Bounds();
    bounds.setNortheast(new LatLng(ne.getY(), ne.getX()));
    bounds.setSouthwest(new LatLng(sw.getY(), sw.getX()));
    return bounds;
  }

  @Override
  public @NotNull GeoCodingResult mapToGeoCodingResult(@NotNull GeocodingResult source) {
    final GeoCodingResult destination = new GeoCodingResult();
    destination.setAddress(
        mapToAddress(source.getAddressComponents(), source.getFormattedAddress()));
    destination.setBoundingBox(mapToBoundingBox(source));
    destination.setPosition(mapToPosition(source));
    destination.setShape(null);
    return destination;
  }

  @Override
  public Address mapToAddress(
      final Collection<? extends AddressComponent> source,
      final String formattedAddress) {

    if (source == null || (source.isEmpty() && !StringUtils.hasText(formattedAddress))) {
      return null;
    }
    boolean hasValue = StringUtils.hasText(formattedAddress);
    final Address destination = new Address();
    destination.setFormattedAddress(formattedAddress);
    for (AddressComponent addressComponent : source) {
      if (addressComponent == null
          || addressComponent.getTypes() == null
          || addressComponent.getTypes().isEmpty()) {
        continue;
      }
      if (addressComponent.getTypes().contains(AddressComponentType.STREET_NUMBER)) {
        hasValue = hasValue || StringUtils.hasText(addressComponent.getLongName());
        destination.setStreetNumber(addressComponent.getLongName());
      } else if (addressComponent.getTypes().contains(AddressComponentType.ROUTE)) {
        hasValue = hasValue || StringUtils.hasText(addressComponent.getLongName());
        destination.setStreet(addressComponent.getLongName());
      } else if (addressComponent.getTypes().contains(AddressComponentType.SUBLOCALITY)) {
        hasValue = hasValue || StringUtils.hasText(addressComponent.getLongName());
        destination.setSuburb(addressComponent.getLongName());
      } else if (addressComponent.getTypes().contains(AddressComponentType.LOCALITY)) {
        hasValue = hasValue || StringUtils.hasText(addressComponent.getLongName());
        destination.setCity(addressComponent.getLongName());
      } else if (addressComponent.getTypes()
          .contains(AddressComponentType.ADMINISTRATIVE_AREA_LEVEL_3)) {
        if (destination.getCity() == null) {
          hasValue = hasValue || StringUtils.hasText(addressComponent.getLongName());
          destination.setCity(addressComponent.getLongName());
        }
      } else if (addressComponent.getTypes()
          .contains(AddressComponentType.ADMINISTRATIVE_AREA_LEVEL_1)) {
        hasValue = hasValue || StringUtils.hasText(addressComponent.getLongName());
        destination.setState(addressComponent.getLongName());
      } else if (addressComponent.getTypes().contains(AddressComponentType.COUNTRY)) {
        hasValue = hasValue || StringUtils.hasText(addressComponent.getLongName());
        destination.setCountry(addressComponent.getLongName());
        destination.setCountryCode(addressComponent.getShortName());
      } else if (addressComponent.getTypes().contains(AddressComponentType.POSTAL_CODE)) {
        hasValue = hasValue || StringUtils.hasText(addressComponent.getLongName());
        destination.setPostalCode(addressComponent.getLongName());
      }
    }
    return hasValue ? destination : null;
  }

  private Polygon mapToBoundingBox(GeocodingResult source) {
    if (source.getGeometry() == null || source.getGeometry().getBounds() == null) {
      return null;
    }
    final LatLng ne = source.getGeometry().getBounds().getNortheast();
    final LatLng sw = source.getGeometry().getBounds().getSouthwest();
    final Coordinate a = GeometryUtils.createCoordinateWGS84(
        ne.getLat(),
        ne.getLng());
    final Coordinate b = GeometryUtils.createCoordinateWGS84(
        sw.getLat(),
        sw.getLng());
    return GeometryUtils.getBoundingBoxAsPolygon2D(
        GeometryUtils.createLineString(Arrays.asList(a, b)));
  }

  private Point mapToPosition(GeocodingResult source) {
    if (source.getGeometry() == null || source.getGeometry().getLocation() == null) {
      return null;
    }
    final LatLng latLng = source.getGeometry().getLocation();
    return GeometryUtils.createPointWGS84(latLng.getLat(), latLng.getLng());
  }
}
