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

import java.util.function.Predicate;

public class SimpleConfigObj {

    String key;
    String description;
    String range;

    Predicate<String> validator;

    public SimpleConfigObj(String key, String description, String range, Predicate<String> validator) {
        this.key = key;
        this.description = description;
        this.range = range;
        this.validator = validator;
    }

    public String toString(SimpleConfig config) {
        String value;
        if (validator.test(config.get(key))) {
            value = config.get(key);
        } else {
            value = config.defaultProperties.getProperty(key);
        }
        return "# " + description + "\n" + "# Range: " + range + "\n" + key + "=" + value + "\n";
    }
}
