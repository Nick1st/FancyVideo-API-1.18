/*
 * This file is part of the FancyVideo-API.
 *
 * The FancyVideo-API is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * The FancyVideo-API is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * The FancyVideo-API uses VLCJ, Copyright 2009-2021 Caprica Software Limited,
 * licensed under the GNU General Public License.
 *
 * You should have received a copy of the GNU General Public License
 * along with VLCJ.  If not, see <http://www.gnu.org/licenses/>.
 *
 * You should have received a copy of the GNU General Public License
 * along with FancyVideo-API.  If not, see <http://www.gnu.org/licenses/>.
 *
 * Copyright 2022 Nick1st.
 */

package nick1st.fancyvideo.config;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Properties;
import java.util.function.Predicate;

public class SimpleConfig {

    public final File configFile;
    public final Properties properties = new Properties();
    Properties defaultProperties = new Properties();
    ArrayList<SimpleConfigObj> simpleConfigObj = new ArrayList<>();

    public SimpleConfig(File configFile) {
        this.configFile = configFile;
    }

    public void read() {
        try (FileReader reader = new FileReader(configFile)) {
            properties.load(reader);
        } catch (IOException ex) {
            // I/O error
        }
    }

    public void write() {
        try (FileWriter writer = new FileWriter(configFile)) {
            simpleConfigObj.forEach(obj -> {
                try {
                    writer.write(obj.toString(this));
                    read();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
        } catch (IOException ex) {
            // I/O error
        }
    }

    public String get(String key) {
        return properties.getProperty(key, defaultProperties.getProperty(key));
    }

    public boolean getAsBool(String key) {
        return Boolean.parseBoolean(properties.getProperty(key));
    }

    public int getAsInt(String key) {
        try {
            return Integer.parseInt(properties.getProperty(key));
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    public void setProperty(String key, String value, String description, String range, Predicate<String> validator) {
        defaultProperties.setProperty(key, value);
        simpleConfigObj.add(new SimpleConfigObj(key, description, range, validator));
    }
}
