package org.bremersee.peregrinus;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class PeregrinusServiceApplication { //implements CommandLineRunner {

  public static void main(String[] args) {
    SpringApplication.run(PeregrinusServiceApplication.class, args);
  }

  /*
  @Override
  public void run(String... args) throws Exception {
    final DefaultResourceLoader resourceLoader = new DefaultResourceLoader();

    final Jaxb2Marshaller xmlMarshaller = new Jaxb2Marshaller();
    xmlMarshaller.setContextPaths(
        GpxJaxbContextHelper.contextPathsBuilder(
            GarminJaxbContextHelper.CONTEXT_PATHS));

    final Map<String, Object> marshallerProperties = new HashMap<>();
    marshallerProperties.put(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
    xmlMarshaller.setMarshallerProperties(marshallerProperties);

    Gpx gpx = (Gpx) xmlMarshaller
        .getJaxbContext()
        .createUnmarshaller()
        .unmarshal(resourceLoader.getResource("Adresse.GPX").getInputStream());

    //StringWriter sw = new StringWriter();
    //StreamResult rs = new StreamResult(sw);
    //xmlMarshaller.marshal(gpx, rs);
    //System.out.println(sw);

    for (Element ext : gpx.getWpts().get(0).getExtensions().getAnies()) {
      System.out.println("ext = " + ext.getClass().getName());
      Object obj = xmlMarshaller
          .getJaxbContext()
          .createUnmarshaller().unmarshal(ext);
      System.out.println("obj = " + obj.getClass().getName());
    }

  }
  */

}
