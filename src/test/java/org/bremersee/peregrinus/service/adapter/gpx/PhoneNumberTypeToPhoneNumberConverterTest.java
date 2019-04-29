package org.bremersee.peregrinus.service.adapter.gpx;

import org.bremersee.common.model.PhoneNumber;
import org.bremersee.garmin.waypoint.v1.model.ext.PhoneNumberT;
import org.junit.Assert;
import org.junit.Test;

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
    Assert.assertNull(actual);

    PhoneNumberT phoneNumber = new PhoneNumberT();
    actual = converter.convert(phoneNumber);
    Assert.assertNull(actual);

    phoneNumber.setValue("0123456789");
    phoneNumber.setCategory("Mobile");
    actual = converter.convert(phoneNumber);
    Assert.assertNotNull(actual);
    Assert.assertEquals("0123456789", actual.getValue());
    Assert.assertEquals("Mobile", actual.getCategory());
  }
}