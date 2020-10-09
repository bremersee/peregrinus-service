package org.bremersee.peregrinus.service.adapter.gpx;

import java.util.Locale;
import org.bremersee.common.model.Address;
import org.bremersee.common.model.TwoLetterCountryCode;
import org.bremersee.garmin.gpx.v3.model.ext.AddressT;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * The address to address type converter test.
 *
 * @author Christian Bremer
 */
public class AddressToAddressTypeConverterTest {

  private static final AddressToAddressTypeConverter converter = new AddressToAddressTypeConverter();

  /**
   * Tests convert.
   */
  @Test
  public void convert() {
    AddressT actual = converter.convert(null, AddressT::new);
    Assertions.assertNull(actual);

    Address address = new Address();
    address.setCity("Lübeck");
    address.setCountry(Locale.GERMANY.getDisplayCountry(Locale.GERMANY));
    address.setPostalCode("23552");
    address.setCountryCode(TwoLetterCountryCode.DE);
    address.setStreet("Mengstraße");
    address.setStreetNumber("4");
    address.setFormattedAddress("Mengstraße 4, 23552 Lübeck");

    AddressT expected = new AddressT();
    expected.setCity("Lübeck");
    expected.setCountry(Locale.GERMANY.getDisplayCountry(Locale.GERMANY));
    expected.setPostalCode("23552");
    expected.getStreetAddresses().add("Mengstraße 4");

    actual = converter.convert(address, AddressT::new);
    Assertions.assertEquals(expected.getCity(), actual.getCity());
    Assertions.assertEquals(expected.getCountry(), actual.getCountry());
    Assertions.assertEquals(expected.getPostalCode(), actual.getPostalCode());
    Assertions.assertEquals(expected.getStreetAddresses(), actual.getStreetAddresses());
  }
}