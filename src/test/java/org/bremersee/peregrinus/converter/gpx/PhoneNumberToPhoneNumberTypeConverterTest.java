package org.bremersee.peregrinus.converter.gpx;

import java.util.Optional;
import org.bremersee.common.model.PhoneNumber;
import org.bremersee.garmin.gpx.v3.model.ext.PhoneNumberT;
import org.junit.Assert;
import org.junit.Test;
import reactor.util.function.Tuples;

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
    PhoneNumberT actual = converter.convert(null);
    Assert.assertNull(actual);

    PhoneNumber phoneNumber = null;
    //noinspection ConstantConditions
    actual = converter.convert(Tuples.of(Optional.ofNullable(phoneNumber), PhoneNumberT::new));
    Assert.assertNull(actual);

    phoneNumber = new PhoneNumber();
    actual = converter.convert(Tuples.of(Optional.of(phoneNumber), PhoneNumberT::new));
    Assert.assertNull(actual);

    phoneNumber.setValue("0123456789");
    phoneNumber.setCategory("Mobile");
    actual = converter.convert(Tuples.of(Optional.of(phoneNumber), PhoneNumberT::new));
    Assert.assertNotNull(actual);
    Assert.assertEquals("0123456789", actual.getValue());
    Assert.assertEquals("Mobile", actual.getCategory());
  }
}