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

package org.bremersee.peregrinus.service.adapter.tomtom.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;
import lombok.Getter;
import org.springframework.util.StringUtils;

/**
 * Supported languages (see
 * <a href="https://developer.tomtom.com/search-api/search-api-documentation/supported-languages">
 * Supported Languages</a>)
 *
 * @author Christian Bremer
 */
@SuppressWarnings("unused")
public enum Language {
  Arabic("ar", new Locale("ar")),
  Basque("eu-ES", null),
  Bulgarian("bg-BG", new Locale("bg", "BG")),
  Catalan("ca-ES", new Locale("ca", "ES")),
  Chinese_PRC("zh-CN", new Locale("zh", "CN")),
  Chinese_Taiwan("zh-TW", new Locale("zh", "TW")),
  Czech("cs-CZ", new Locale("cs", "CS")),
  Danish("da-DK", new Locale("da", "DK")),
  Dutch_Belgium("nl-BE", new Locale("nl", "BE")),
  Dutch("nl-NL", new Locale("nl", "LN")),
  English_Australia("en-AU", new Locale("en", "AU")),
  English_New_Zealand("en-NZ", new Locale("en", "NZ")),
  English_Great_Britain("en-GB", new Locale("en", "GB")),
  English_USA("en-US", new Locale("en", "US")),
  Estonian("et-EE", new Locale("et", "EE")),
  Finnish("fi-FI", new Locale("fe", "FI")),
  French_Canada("fr-CA", new Locale("fr", "CA")),
  French("fr-FR", new Locale("fr", "FR")),
  Galician("gl-ES", null),
  German("de-DE", new Locale("de", "DE")),
  Greek("el-GR", new Locale("el", "GR")),
  Croatian("hr-HR", new Locale("hr", "HR")),
  Hebrew("he-IL", new Locale("iw", "IL")),
  Hungarian("hu-HU", new Locale("hu", "HU")),
  Indonesian("id-ID", new Locale("in", "ID")),
  Italian("it-IT", new Locale("it", "IT")),
  Latvian("lv-LV", new Locale("lv", "LV")),
  Lithuanian("lt-LT", new Locale("lt", "LT")),
  Malay("ms-MY", new Locale("ms", "MY")),
  Norwegian("no-NO", new Locale("no", "NO")),
  Polish("pl-PL", new Locale("pl", "PL")),
  Portuguese_Brazil("pt-BR", new Locale("pt", "BR")),
  Portuguese_Portugal("pt-PT", new Locale("pt", "PT")),
  Romanian("ro-RO", new Locale("ro", "RO")),
  Russian("ru-RU", new Locale("ru", "RU")),
  Serbian("sr-RS", new Locale("sr", "RS")),
  Slovak("sk-SK", new Locale("sk", "SK")),
  Slovenian("sl-SI", new Locale("sl", "SI")),
  Castilian_Spanish("es-ES", new Locale("es", "ES")),
  Swedish("sv-SE", new Locale("sv", "SE")),
  Thai("th-TH", new Locale("th", "TH")),
  Turkish("tr-TR", new Locale("tr", "TR")),
  Ukranian("uk-UA", new Locale("uk", "UA")),
  Vietnamese("vi-VN", new Locale("vi", "VN")),
  Afrikaans("af-ZA", new Locale("en", "ZA"));

  @Getter
  private String value;

  @Getter
  private Locale locale;

  Language(String value, Locale locale) {
    this.value = value;
    this.locale = locale;
  }

  @JsonValue
  @Override
  public String toString() {
    return value;
  }

  /**
   * Get the language from the value.
   *
   * @param value the value
   * @return the language
   */
  @JsonCreator
  public static Language fromValue(String value) {
    for (Language language : Language.values()) {
      if (language.getValue().equalsIgnoreCase(value)) {
        return language;
      }
    }
    return English_Great_Britain;
  }

  /**
   * Get the language from the {@link Locale}.
   *
   * @param locale the locale
   * @return the language
   */
  public static Language fromLocale(Locale locale) {
    if (locale != null && StringUtils.hasText(locale.getLanguage())) {
      for (Language language : Language.values()) {
        if (language.getLocale() == null) {
          continue;
        }
        Locale localLocale = language.getLocale();
        if (!locale.getLanguage().equals(localLocale.getLanguage())) {
          continue;
        }
        if (StringUtils.hasText(localLocale.getCountry())
            && StringUtils.hasText(locale.getCountry())
            && !localLocale.getCountry().equals(locale.getCountry())) {
          continue;
        }
        return language;
      }
    }
    return English_Great_Britain;
  }

  /**
   * Get supported locales.
   *
   * @return the supported locales
   */
  public static Set<Locale> locales() {
    Set<Locale> locales = new HashSet<>();
    for (Language language : Language.values()) {
      if (language.getLocale() != null) {
        locales.add(language.getLocale());
      }
    }
    return locales;
  }

}
