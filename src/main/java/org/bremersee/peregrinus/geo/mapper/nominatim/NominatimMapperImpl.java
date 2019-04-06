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

package org.bremersee.peregrinus.geo.mapper.nominatim;

import org.bremersee.common.model.Address;
import org.bremersee.geojson.utils.GeometryUtils;
import org.bremersee.nominatim.model.SearchRequest;
import org.bremersee.nominatim.model.SearchResult;
import org.bremersee.peregrinus.geo.model.GeoCodingQueryRequest;
import org.bremersee.peregrinus.geo.model.GeoCodingResult;
import org.locationtech.jts.geom.Polygon;

/**
 * @author Christian Bremer
 */
public class NominatimMapperImpl implements NominatimMapper {

  @Override
  public SearchRequest mapToSearchRequest(final GeoCodingQueryRequest source) {
    return SearchRequest
        .builder()
        .acceptLanguage(mapToLanguage(source))
        .countryCodes(source.getCountries())
        .limit(source.getLimit())
        .polygon(Boolean.TRUE)
        .query(source.getQuery())
        .viewBox(mapToViewBox(source))
        .build();
  }

  private String mapToLanguage(final GeoCodingQueryRequest source) {
    if (source.getLanguage() != null && source.getLanguage().getLanguage() != null) {
      return source.getLanguage().getLanguage();
    }
    return "en";
  }

  private double[] mapToViewBox(final GeoCodingQueryRequest source) { // TODO lat lon
    if (source.getBoundingBox() != null) {
      return GeometryUtils.getBoundingBox(source.getBoundingBox());
    }
    return null;
  }

  @Override
  public GeoCodingResult mapToGeoCodingResult(final SearchResult source) {
    GeoCodingResult destination = new GeoCodingResult();
    if (source != null) {
      destination.setAddress(mapToCommonAddress(source));
      destination.setBoundingBox(mapToBoundingBox(source));
      if (source.hasLatLon()) {
        destination.setPosition(GeometryUtils.createPointWGS84(
            source.latToDouble(),
            source.lonToDouble()));
      }
      destination.setShape(source.getGeoJson());
    }
    return destination;
  }

  @SuppressWarnings("Duplicates")
  private Address mapToCommonAddress(final SearchResult source) {
    if (source.getAddress() == null) {
      return null;
    }
    final Address destination = new Address();
    destination.setStreet(source.getAddress().getRoad());
    destination.setStreetNumber(source.getAddress().getHouseNumber());
    destination.setPostalCode(source.getAddress().getPostcode());
    destination.setCity(source.getAddress().findCity());
    destination.setSuburb(source.getAddress().getSuburb());
    destination.setStreetNumber(source.getAddress().getState());
    destination.setCountry(source.getAddress().getCountry());
    //destination.setCountryCode(source.getAddress().getCountryCode());
    destination.setFormattedAddress(source.getAddress().getFormattedAddress());
    return destination;
  }

  private Polygon mapToBoundingBox(final SearchResult source) {
    if (source.getBoundingBox() == null) {
      return null;
    }
    return GeometryUtils.getBoundingBoxAsPolygon2D(source.getBoundingBox());
  }

}
