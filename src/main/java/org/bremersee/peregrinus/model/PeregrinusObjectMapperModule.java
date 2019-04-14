/*
 * Copyright 2019 the original author or authors.
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

package org.bremersee.peregrinus.model;

import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.module.SimpleModule;
import java.util.HashMap;
import java.util.Map;
import org.bremersee.geojson.GeoJsonObjectMapperModule;

/**
 * @author Christian Bremer
 */
@SuppressWarnings("unused")
public class PeregrinusObjectMapperModule extends SimpleModule {

  public PeregrinusObjectMapperModule() {
    super("PeregrinusModule", getVersion(), getDeserializers());
  }

  private static Map<Class<?>, JsonDeserializer<?>> getDeserializers() {
    HashMap<Class<?>, JsonDeserializer<?>> map = new HashMap<>();
    map.put(Feature.class, new FeatureDeserializer());
    map.put(Rte.class, new FeatureDeserializer());
    map.put(RtePt.class, new FeatureDeserializer());
    map.put(Trk.class, new FeatureDeserializer());
    map.put(Wpt.class, new FeatureDeserializer());
    return map;
  }

  private static Version getVersion() {

    final int defaultMajor = 1;
    final int defaultMinor = 0;
    final int defaultPatchLevel = 0;
    final String defaultSnapshotInfo = "SNAPSHOT";

    int major = defaultMajor;
    int minor = defaultMinor;
    int patchLevel = defaultPatchLevel;
    String snapshotInfo = defaultSnapshotInfo;

    String version = GeoJsonObjectMapperModule.class.getPackage().getImplementationVersion();
    if (version != null) {
      try {
        int i = version.indexOf('-');
        if (i < 0) {
          snapshotInfo = null;
        } else {
          snapshotInfo = version.substring(i + 1);
          String[] a = version.substring(0, i).split(".");
          major = Integer.parseInt(a[0]);
          minor = Integer.parseInt(a[1]);
          patchLevel = Integer.parseInt(a[2]);
        }

      } catch (RuntimeException e) {
        major = defaultMajor;
        minor = defaultMinor;
        patchLevel = defaultPatchLevel;
        snapshotInfo = defaultSnapshotInfo;
      }
    }

    return new Version(major, minor, patchLevel, snapshotInfo, "org.bremersee",
        "peregrinus-model-spring");
  }

}
