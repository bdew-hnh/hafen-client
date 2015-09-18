/*
 *  This file is part of bdew's Haven & Hearth modified client.
 *  Copyright (C) 2015 bdew
 *
 *  Redistribution and/or modification of this file is subject to the
 *  terms of the GNU Lesser General Public License, version 3, as
 *  published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  Other parts of this source tree adhere to other copying
 *  rights. Please see the file `COPYING' in the root directory of the
 *  source tree for details.
 *
 *  A copy the GNU Lesser General Public License is distributed along
 *  with the source tree of which this file is a part in the file
 *  `doc/LPGL-3'. If it is missing for any reason, please see the Free
 *  Software Foundation's website at <http://www.fsf.org/>, or write
 *  to the Free Software Foundation, Inc., 59 Temple Place, Suite 330,
 *  Boston, MA 02111-1307 USA
 */

package haven;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ConfigSettingCheckboxList {
    public String id;
    public Set<String> selected;
    public List<String> options;

    public ConfigSettingCheckboxList(String id, List<String> options) {
        this.id = id;
        this.options = options;
        this.selected = new HashSet<>();
        String[] list = Utils.getpref(id, "").split(";");
        selected = new HashSet<String>(Arrays.asList(list));
    }

    public void set(String entry, boolean state) {
        if (state)
            selected.add(entry);
        else
            selected.remove(entry);
        Utils.setpref(id, String.join(";", selected));
    }

    public boolean anySelected() {
        return !selected.isEmpty();
    }

    public void setAll(boolean state) {
        selected.clear();
        if (state)
            selected.addAll(options);
        Utils.setpref(id, String.join(";", selected));
    }

    public boolean isSelected(String entry) {
        return selected.contains(entry);
    }
}
