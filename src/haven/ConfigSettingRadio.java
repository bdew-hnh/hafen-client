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

import java.util.LinkedList;
import java.util.List;

public class ConfigSettingRadio {
	public String id;
	public int mode;
	public List<RadioOption> options;

	class RadioOption {
		int value;
		String name;

		public RadioOption(int value, String name) {
			this.value = value;
			this.name = name;
		}

		public CheckBox makeCheckBox() {
			return new CheckBox(name) {
				@Override
				public void tick(double dt) {
					a = mode == value;
				}

				public void set(boolean val) {
					if (val)
						ConfigSettingRadio.this.set(value);
					a = true;
				}
			};
		}
	}

	public ConfigSettingRadio(String id, int defaultMode) {
		this.id = id;
		this.mode = Utils.getprefi(id, defaultMode);
		this.options = new LinkedList<>();
	}

	public ConfigSettingRadio addOption(int id, String name) {
		options.add(new RadioOption(id, name));
		return this;
	}

	public void addToPanel(OptWnd.Panel panel, Coord start, int yspace) {
		Coord c = new Coord(start);
		for (RadioOption o: options) {
			panel.add(o.makeCheckBox(), c);
			c = c.add(0, yspace);
		}
	}

	public int get() {
		return mode;
	}

	public void set(int mode) {
		this.mode = mode;
		Utils.setprefi(id, mode);
	}
}
