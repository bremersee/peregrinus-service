package org.bremersee.peregrinus.converter.gpx;

import java.util.Locale;
import org.bremersee.common.model.Address;
import org.bremersee.garmin.waypoint.v1.model.ext.AddressT;
import org.junit.Assert;
import org.junit.Test;

/**
 * The address type to address converter test.
 *
 * @author Christian Bremer
 */
public class AddressTypeToAddressConverterTest {

  private static final AddressTypeToAddressConverter converter = new AddressTypeToAddressConverter();

  /**
   * Tests convert.
   */
  @Test
  public void convert() {
    Address actual = converter.convert(null);
    Assert.assertNull(actual);

    AddressT address = new AddressT();
    address.setCity("Lübeck");
    address.setCountry(Locale.GERMANY.getDisplayCountry(Locale.GERMANY));
    address.setPostalCode("23552");
    address.getStreetAddresses().add("Mengstraße 4");

    Address expected = new Address();
    expected.setCity("Lübeck");
    expected.setCountry(Locale.GERMANY.getDisplayCountry(Locale.GERMANY));
    expected.setPostalCode("23552");
    expected.setStreet("Mengstraße 4");

    actual = converter.convert(address);
    Assert.assertNotNull(actual);
    Assert.assertEquals(expected, actual);
  }
}