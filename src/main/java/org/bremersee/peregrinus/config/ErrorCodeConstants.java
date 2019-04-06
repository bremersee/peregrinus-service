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

/**
 * Error codes of this client library.
 *
 * @author Christian Bremer
 */
public abstract class ErrorCodeConstants {

  /**
   * Bad URL code.
   */
  public static final String MALFORMED_URL = "TOMTOM_CLIENT:MALFORMED_URL";

  /**
   * Request error code.
   */
  public static final String GENERAL_REQUEST_ERROR = "TOMTOM_CLIENT:GENERAL_REQUEST_ERROR";

  private ErrorCodeConstants() {
  }
}
