package org.bremersee.peregrinus.service.adapter.gpx;

import org.bremersee.common.model.PhoneNumber;
import org.bremersee.garmin.gpx.v3.model.ext.PhoneNumberT;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * The phone number to phone number type converter test.
 *
 * @author Christian Bremer
 */
public class PhoneNumberToPhoneNumberTypeConverterTest {

  private static final PhoneNumberToPhoneNumberTypeConverter converter
      = new PhoneNumberToPhoneNumberTypeConverter();

  /**
   * Tests convert.
   */
  @Test
  public void convert() {
    PhoneNumberT actual = converter.convert(null, PhoneNumberT::new);
    Assertions.assertNull(actual);

    PhoneNumber phoneNumber = new PhoneNumber();
    actual = converter.convert(phoneNumber, PhoneNumberT::new);
    Assertions.assertNull(actual);

    phoneNumber.setValue("0123456789");
    phoneNumber.setCategory("Mobile");
    actual = converter.convert(phoneNumber, PhoneNumberT::new);
    Assertions.assertNotNull(actual);
    Assertions.assertEquals("0123456789", actual.getValue());
    Assertions.assertEquals("Mobile", actual.getCategory());
  }
}