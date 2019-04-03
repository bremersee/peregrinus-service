package org.bremersee.peregrinus.garmin;

import org.bremersee.common.model.PhoneNumber;
import org.bremersee.garmin.gpx.v3.model.ext.PhoneNumberT;
import org.junit.Assert;
import org.junit.Test;

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
    Assert.assertNull(actual);

    PhoneNumber phoneNumber = new PhoneNumber();
    actual = converter.convert(phoneNumber, PhoneNumberT::new);
    Assert.assertNull(actual);

    phoneNumber.setValue("0123456789");
    phoneNumber.setCategory("Mobile");
    actual = converter.convert(phoneNumber, PhoneNumberT::new);
    Assert.assertNotNull(actual);
    Assert.assertEquals("0123456789", actual.getValue());
    Assert.assertEquals("Mobile", actual.getCategory());
  }
}