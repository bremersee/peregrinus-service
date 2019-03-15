package org.bremersee.peregrinus.converter.gpx;

import java.util.Locale;
import java.util.Optional;
import org.bremersee.common.model.Address;
import org.bremersee.garmin.gpx.v3.model.ext.AddressT;
import org.junit.Assert;
import org.junit.Test;
import reactor.util.function.Tuples;

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
    AddressT actual = converter.convert(null);
    Assert.assertNull(actual);

    //noinspection ConstantConditions
    actual = converter.convert(Tuples.of(Optional.ofNullable(null), AddressT::new));
    Assert.assertNull(actual);

    Address address = new Address();
    address.setCity("Lübeck");
    address.setCountry(Locale.GERMANY.getDisplayCountry(Locale.GERMANY));
    address.setPostalCode("23552");
    address.setCountryCode(Locale.GERMANY.getCountry());
    address.setStreet("Mengstraße");
    address.setStreetNumber("4");
    address.setFormattedAddress("Mengstraße 4, 23552 Lübeck");

    AddressT expected = new AddressT();
    expected.setCity("Lübeck");
    expected.setCountry(Locale.GERMANY.getDisplayCountry(Locale.GERMANY));
    expected.setPostalCode("23552");
    expected.getStreetAddresses().add("Mengstraße 4");

    actual = converter.convert(Tuples.of(Optional.of(address), AddressT::new));
    Assert.assertEquals(expected.getCity(), actual.getCity());
    Assert.assertEquals(expected.getCountry(), actual.getCountry());
    Assert.assertEquals(expected.getPostalCode(), actual.getPostalCode());
    Assert.assertEquals(expected.getStreetAddresses(), actual.getStreetAddresses());
  }
}