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

package org.bremersee.peregrinus.entity.converter;

import java.math.BigDecimal;
import java.util.List;
import org.bremersee.peregrinus.entity.FeatureEntity;
import org.bremersee.peregrinus.entity.RteEntity;
import org.bremersee.peregrinus.entity.RteEntityProperties;
import org.bremersee.peregrinus.entity.TrkEntity;
import org.bremersee.peregrinus.entity.TrkEntityProperties;
import org.bremersee.peregrinus.entity.TypeAliases;
import org.bremersee.peregrinus.entity.WptEntity;
import org.bremersee.peregrinus.entity.WptEntityProperties;
import org.bson.Document;
import org.locationtech.jts.geom.MultiLineString;
import org.locationtech.jts.geom.Point;
import org.springframework.context.ApplicationContext;
import org.springframework.core.convert.ConverterNotFoundException;
import org.springframework.core.convert.TypeDescriptor;
import org.springframework.data.convert.ReadingConverter;
import org.springframework.data.mongodb.core.convert.MappingMongoConverter;

/**
 * @author Christian Bremer
 */
@ReadingConverter
class FeatureEntityReadConverter extends AbstractEntityReadConverter<FeatureEntity> {

  FeatureEntityReadConverter(ApplicationContext applicationContext) {
    super(applicationContext);
  }

  @Override
  public FeatureEntity convert(Document document) {

    final MappingMongoConverter mongoConverter = getMongoConverter();
    final String typeAlias = document.getString("_class");
    if (TypeAliases.RTE.equalsIgnoreCase(typeAlias)) {
      RteEntity entity = new RteEntity();
      convert(document, entity);
      entity.setGeometry(mongoConverter
          .read(MultiLineString.class, document.get("geometry", Document.class)));
      entity.setProperties(mongoConverter
          .read(RteEntityProperties.class, document.get("properties", Document.class)));
      return entity;
    }
    if (TypeAliases.TRK.equalsIgnoreCase(typeAlias)) {
      TrkEntity entity = new TrkEntity();
      convert(document, entity);
      entity.setGeometry(mongoConverter
          .read(MultiLineString.class, document.get("geometry", Document.class)));
      entity.setProperties(mongoConverter
          .read(TrkEntityProperties.class, document.get("properties", Document.class)));
      return entity;
    }
    if (TypeAliases.WPT.equalsIgnoreCase(typeAlias)) {
      WptEntity entity = new WptEntity();
      convert(document, entity);
      entity.setGeometry(mongoConverter
          .read(Point.class, document.get("geometry", Document.class)));
      entity.setProperties(mongoConverter
          .read(WptEntityProperties.class, document.get("properties", Document.class)));
      return entity;
    }
    throw new ConverterNotFoundException(
        TypeDescriptor.valueOf(Document.class),
        TypeDescriptor.valueOf(FeatureEntity.class));
  }

  void convert(Document document, FeatureEntity entity) {
    final List<?> bboxList = document.get("bbox", List.class);
    if (bboxList != null && bboxList.size() == 4) {
      final double[] bbox = new double[4];
      int i = 0;
      for (Object value : bboxList) {
        bbox[i] = toNumber(value);
        i++;
      }
      entity.setBbox(bbox);
    }
    entity.setId(document.getObjectId("_id").toString());
  }

  private double toNumber(Object value) {
    if (value instanceof Number) {
      return ((Number) value).doubleValue();
    }
    if (value != null) {
      return new BigDecimal(value.toString()).doubleValue();
    }
    return 0.;
  }
}
