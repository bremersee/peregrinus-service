package org.bremersee.peregrinus.service.adapter.gpx;

import org.bremersee.common.model.PhoneNumber;
import org.bremersee.garmin.waypoint.v1.model.ext.PhoneNumberT;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * The phone number type to phone number converter test.
 *
 * @author Christian Bremer
 */
public class PhoneNumberTypeToPhoneNumberConverterTest {

  private static final PhoneNumberTypeToPhoneNumberConverter converter
      = new PhoneNumberTypeToPhoneNumberConverter();

  /**
   * Tests convert.
   */
  @Test
  public void convert() {
    PhoneNumber actual = converter.convert(null);
    Assertions.assertNull(actual);

    PhoneNumberT phoneNumber = new PhoneNumberT();
    actual = converter.convert(phoneNumber);
    Assertions.assertNull(actual);

    phoneNumber.setValue("0123456789");
    phoneNumber.setCategory("Mobile");
    actual = converter.convert(phoneNumber);
    Assertions.assertNotNull(actual);
    Assertions.assertEquals("0123456789", actual.getValue());
    Assertions.assertEquals("Mobile", actual.getCategory());
  }
}