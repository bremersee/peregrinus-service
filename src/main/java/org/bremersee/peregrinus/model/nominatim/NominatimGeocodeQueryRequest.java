/*
 * Copyright 2019 the original author or authors.
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

package org.bremersee.peregrinus.model.nominatim;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.bremersee.common.model.HttpLanguageTag;
import org.bremersee.common.model.TwoLetterCountryCodes;
import org.bremersee.peregrinus.model.GeocodeQueryRequest;

/**
 * The nominatim geocode query request.
 *
 * @author Christian Bremer
 */
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@NoArgsConstructor
@SuppressWarnings({"WeakerAccess", "unused"})
public class NominatimGeocodeQueryRequest extends GeocodeQueryRequest {

  @JsonIgnore
  private Boolean bounded = Boolean.FALSE;

  @JsonIgnore
  private List<String> excludePlaceIds;

  @JsonIgnore
  private Boolean dedupe = Boolean.TRUE;

  @JsonIgnore
  private Boolean debug = Boolean.FALSE;

  @JsonIgnore
  private Boolean addressDetails = Boolean.TRUE;

  @JsonIgnore
  private String email;

  @JsonIgnore
  private Boolean polygon = Boolean.TRUE;

  @JsonIgnore
  private Boolean extraTags = Boolean.TRUE;

  @JsonIgnore
  private Boolean nameDetails = Boolean.TRUE;

  /**
   * Instantiates a new Nominatim geocode query request.
   *
   * @param language       the language
   * @param boundingBox    the bounding box
   * @param countryCodes   the country codes
   * @param limit          the limit
   * @param query          the query
   * @param addressDetails the address details
   * @param email          the email
   * @param polygon        the polygon
   * @param extraTags      the extra tags
   * @param nameDetails    the name details
   */
  @Builder
  public NominatimGeocodeQueryRequest(
      HttpLanguageTag language,
      double[] boundingBox,
      TwoLetterCountryCodes countryCodes,
      Integer limit,
      String query,
      Boolean addressDetails,
      String email,
      Boolean polygon,
      Boolean extraTags,
      Boolean nameDetails) {

    super(language, boundingBox, countryCodes, limit, query);
    setAddressDetails(addressDetails);
    setEmail(email);
    setPolygon(polygon);
    setExtraTags(extraTags);
    setNameDetails(nameDetails);
  }

  /**
   * Preferred language order for showing search results, overrides the value specified in the
   * "Accept-Language" HTTP header. Either uses standard rfc2616 accept-language string or a simple
   * comma separated list of language codes.
   *
   * <p>Query parameter name is {@code accept-language}.
   *
   * @return the language
   */
  @Override
  public HttpLanguageTag getLanguage() {
    return super.getLanguage();
  }

  /**
   * The preferred area to find search results. Any two corner points of the box are accepted in any
   * order as long as they span a real box.
   *
   * @return the bounding box
   */
  @Override
  public double[] getBoundingBox() {
    return super.getBoundingBox();
  }

  /**
   * Limit search results to a specific country (or a list of countries). {@literal <countrycode>}
   * should be the ISO 3166-1alpha2 code, e.g. gb for the United Kingdom, de for Germany, etc.
   *
   * <p>countrycodes={@literal <countrycode>[,<countrycode>][,<countrycode>]} ...
   *
   * @return the country codes
   */
  @Override
  public TwoLetterCountryCodes getCountryCodes() {
    return super.getCountryCodes();
  }

  /**
   * Limit the number of returned results. Default is 8.
   *
   * <p>limit={@literal <integer>}
   *
   * @return the limit
   */
  public Integer getLimit() {
    return super.getLimit();
  }

  /**
   * Restrict the results to only items contained with the boundingBox (see above). Restricting the
   * results to the bounding box also enables searching by amenity only. For example a search query
   * of just "[pub]" would normally be rejected but with bounded=1 will result in a list of items
   * matching within the bounding box.
   *
   * <p>bounded=[0|1]
   *
   * @return {@code true}
   */
  @JsonProperty("bounded")
  public Boolean getBounded() {
    return bounded;
  }

  /**
   * Sets bounded.
   *
   * @param bounded the bounded
   */
  @JsonProperty("bounded")
  public void setBounded(Boolean bounded) {
    this.bounded = Boolean.TRUE.equals(bounded);
  }

  /**
   * If you do not want certain openstreetmap objects to appear in the search result, give a comma
   * separated list of the place_id's you want to skip. This can be used to broaden search results.
   * For example, if a previous query only returned a few results, then including those here would
   * cause the search to return other, less accurate, matches (if possible).
   *
   * <p>exclude_place_ids={@literal <place_id,[place_id],[place_id]>}
   *
   * @return excluded place IDs
   */
  @JsonProperty("excludePlaceIds")
  public List<String> getExcludePlaceIds() {
    return excludePlaceIds;
  }

  /**
   * Sets exclude place ids.
   *
   * @param excludePlaceIds the exclude place ids
   */
  @JsonProperty("excludePlaceIds")
  public void setExcludePlaceIds(List<String> excludePlaceIds) {
    this.excludePlaceIds = excludePlaceIds;
  }

  /**
   * Sometimes you have several objects in OSM identifying the same place or object in reality. The
   * simplest case is a street being split in many different OSM ways due to different
   * characteristics. Nominatim will attempt to detect such duplicates and only return one match;
   * this is controlled by the dedupe parameter which defaults to 1. Since the limit is, for reasons
   * of efficiency, enforced before and not after de-duplicating, it is possible that de-duplicating
   * leaves you with less results than requested.
   *
   * <p>dedupe=[0|1]
   *
   * @return {@code true} if duplicates should be avoided, otherwise {@code false}
   */
  @JsonProperty("dedupe")
  public Boolean getDedupe() {
    return dedupe;
  }

  /**
   * Sets dedupe.
   *
   * @param dedupe the dedupe
   */
  @JsonProperty("dedupe")
  public void setDedupe(Boolean dedupe) {
    this.dedupe = !Boolean.FALSE.equals(dedupe);
  }

  /**
   * Output assorted developer debug information. Data on internals of nominatim "Search Loop"
   * logic, and SQL queries. The output is (rough) HTML format. This overrides the specified machine
   * readable format.
   *
   * <p>debug=[0|1]
   *
   * @return {@code true} if debug mode is enabled, otherwise {@code false}
   */
  @JsonProperty("dedupe")
  public Boolean getDebug() {
    return debug;
  }

  /**
   * Sets debug.
   *
   * @param debug the debug
   */
  @JsonProperty("dedupe")
  public void setDebug(Boolean debug) {
    this.debug = Boolean.TRUE.equals(debug);
  }

  /**
   * Include a breakdown of the address into elements.
   *
   * <p>addressDetails=[0|1]
   *
   * @return {@code true} if address details are enabled, otherwise {@code false}
   */
  @JsonProperty("addressDetails")
  public Boolean getAddressDetails() {
    return addressDetails;
  }

  /**
   * Sets address details.
   *
   * @param addressDetails the address details
   */
  @JsonProperty("addressDetails")
  public void setAddressDetails(Boolean addressDetails) {
    this.addressDetails = !Boolean.FALSE.equals(addressDetails);
  }

  /**
   * If you are making large numbers of request please include a valid email address or
   * alternatively include your email address as part of the User-Agent string. This information
   * will be kept confidential and only used to contact you in the event of a problem, see Usage
   * Policy for more details.
   *
   * <p>email={@literal <valid email address>}
   *
   * @return the email
   */
  @JsonProperty("email")
  public String getEmail() {
    return email;
  }

  /**
   * Sets email.
   *
   * @param email the email
   */
  @JsonProperty("email")
  public void setEmail(String email) {
    this.email = email;
  }

  /**
   * Output geometry of results in geojson format.
   *
   * <p>polygon_geojson=1
   *
   * @return {@code true} if the geometry should be returned, otherwise {@code false}
   */
  @JsonProperty("polygon")
  public Boolean getPolygon() {
    return polygon;
  }

  /**
   * Sets polygon.
   *
   * @param polygon the polygon
   */
  @JsonProperty("polygon")
  public void setPolygon(Boolean polygon) {
    this.polygon = !Boolean.FALSE.equals(polygon);
  }

  /**
   * Include additional information in the result if available, e.g. wikipedia link, opening hours.
   *
   * <p>extraTags=1
   *
   * @return {@code true} if additional information should be returned, otherwise {@code false}
   */
  @JsonProperty("extraTags")
  public Boolean getExtraTags() {
    return extraTags;
  }

  /**
   * Sets extra tags.
   *
   * @param extraTags the extra tags
   */
  @JsonProperty("extraTags")
  public void setExtraTags(Boolean extraTags) {
    this.extraTags = !Boolean.FALSE.equals(extraTags);
  }

  /**
   * Include a list of alternative names in the results. These may include language variants,
   * references, operator and brand.
   *
   * <p>nameDetails=1
   *
   * @return {@code true} if alternative names should be returned, otherwise {@code false}
   */
  @JsonProperty("nameDetails")
  public Boolean getNameDetails() {
    return nameDetails;
  }

  /**
   * Sets name details.
   *
   * @param nameDetails the name details
   */
  @JsonProperty("nameDetails")
  public void setNameDetails(Boolean nameDetails) {
    this.nameDetails = !Boolean.FALSE.equals(nameDetails);
  }

}
