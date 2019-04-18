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

import java.math.BigInteger;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.bremersee.plain.model.UnknownAware;

/**
 * Groups a sequence of instruction elements which are related to each other. The sequence range is
 * constrained with firstInstructionIndex and lastInstructionIndex. When human-readable text
 * messages are requested for guidance (instructionType=text or tagged), then the instructionGroup
 * has a summary message returned when available.
 *
 * @author Christian Bremer
 */
@Getter
@Setter
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = false)
@SuppressWarnings("WeakerAccess")
public class RouteInstructionGroup extends UnknownAware {

  private BigInteger firstInstructionIndex;

  private BigInteger lastInstructionIndex;

  private String groupMessage;

  private BigInteger groupLengthInMeters;

}
