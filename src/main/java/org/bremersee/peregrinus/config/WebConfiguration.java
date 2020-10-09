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

package org.bremersee.peregrinus.config;

import lombok.extern.slf4j.Slf4j;
import org.bremersee.http.codec.xml.ReactiveJaxbDecoder;
import org.bremersee.xml.JaxbContextBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.codec.ServerCodecConfigurer;
import org.springframework.http.codec.json.Jackson2JsonDecoder;
import org.springframework.http.codec.json.Jackson2JsonEncoder;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.web.reactive.config.EnableWebFlux;
import org.springframework.web.reactive.config.WebFluxConfigurer;

/**
 * The web configuration.
 *
 * @author Christian Bremer
 */
@EnableWebFlux
@Configuration
@Slf4j
public class WebConfiguration implements WebFluxConfigurer {

  private final JaxbContextBuilder jaxbContextBuilder;

  private final Jackson2ObjectMapperBuilder objectMapperBuilder;

  /**
   * Instantiates a new web configuration.
   *
   * @param jaxbContextBuilder the jaxb context builder
   * @param objectMapperBuilder the object mapper builder
   */
  @Autowired
  public WebConfiguration(
      JaxbContextBuilder jaxbContextBuilder,
      Jackson2ObjectMapperBuilder objectMapperBuilder) {
    this.jaxbContextBuilder = jaxbContextBuilder;
    this.objectMapperBuilder = objectMapperBuilder;
  }

  @Override
  public void configureHttpMessageCodecs(ServerCodecConfigurer configurer) {
    configurer
        .defaultCodecs()
        .jackson2JsonDecoder(new Jackson2JsonDecoder(objectMapperBuilder.build()));
    configurer
        .defaultCodecs()
        .jackson2JsonEncoder(new Jackson2JsonEncoder(objectMapperBuilder.build()));
    configurer
        .customCodecs()
        .registerWithDefaultConfig(new ReactiveJaxbDecoder(jaxbContextBuilder));
  }

//Reader = org.springframework.http.codec.DecoderHttpMessageReader : [*/*], decoder = org.springframework.core.codec.ByteArrayDecoder

//Reader = org.springframework.http.codec.DecoderHttpMessageReader : [*/*], decoder = org.springframework.core.codec.ByteBufferDecoder
//Reader = org.springframework.http.codec.DecoderHttpMessageReader : [*/*], decoder = org.springframework.core.codec.DataBufferDecoder
//Reader = org.springframework.http.codec.DecoderHttpMessageReader : [*/*], decoder = org.springframework.core.codec.ResourceDecoder
//Reader = org.springframework.http.codec.DecoderHttpMessageReader : [text/plain;charset=UTF-8], decoder = org.springframework.core.codec.StringDecoder

//Reader = org.springframework.http.codec.FormHttpMessageReader : [application/x-www-form-urlencoded]
//Reader = org.springframework.http.codec.multipart.SynchronossPartHttpMessageReader : [multipart/form-data]
//Reader = org.springframework.http.codec.multipart.MultipartHttpMessageReader : [multipart/form-data]

//Reader = org.springframework.http.codec.DecoderHttpMessageReader : [application/json;charset=UTF-8, application/*+json;charset=UTF-8],
// decoder = org.springframework.http.codec.json.Jackson2JsonDecoder

//Reader = org.springframework.http.codec.DecoderHttpMessageReader : [application/xml, text/xml]
// org.springframework.http.codec.xml.Jaxb2XmlDecoder

//Reader = org.springframework.http.codec.DecoderHttpMessageReader : [text/plain;charset=UTF-8, */*]
// decoder = org.springframework.core.codec.StringDecoder

//Writer = org.springframework.http.codec.EncoderHttpMessageWriter : [*/*]
//Writer = org.springframework.http.codec.EncoderHttpMessageWriter : [*/*]
//Writer = org.springframework.http.codec.EncoderHttpMessageWriter : [*/*]
//Writer = org.springframework.http.codec.ResourceHttpMessageWriter : [application/octet-stream, */*]
//Writer = org.springframework.http.codec.EncoderHttpMessageWriter : [text/plain;charset=UTF-8]
//Writer = org.springframework.http.codec.EncoderHttpMessageWriter : [application/json;charset=UTF-8, application/*+json;charset=UTF-8]
//Writer = org.springframework.http.codec.EncoderHttpMessageWriter : [application/xml, text/xml]
//Writer = org.springframework.http.codec.ServerSentEventHttpMessageWriter : [text/event-stream]
//Writer = org.springframework.http.codec.EncoderHttpMessageWriter : [text/plain;charset=UTF-8, */*]

}
