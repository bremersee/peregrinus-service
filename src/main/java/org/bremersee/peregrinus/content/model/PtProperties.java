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

package org.bremersee.peregrinus.content.model;

import java.math.BigDecimal;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.bremersee.common.model.Address;
import org.bremersee.common.model.PhoneNumber;

/**
 * @author Christian Bremer
 */
@Getter
@Setter
@ToString(callSuper = true)
public abstract class PtProperties<S extends PtSettings> extends FeatureProperties<S> {

  private String internalType; // photo, video or not // TODO

  //private Instant time; // TODO wozu hat ein punkt eine zeit?

  //private String sym; // TODO

  /**
   * Elevation in meters
   */
  private BigDecimal ele;

  /**
   * Address
   */
  private Address address; // index?

  /**
   * Phone numbers
   */
  private List<PhoneNumber> phoneNumbers;

}
