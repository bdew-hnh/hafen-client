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

import java.awt.*;

public class FloatNumTracker {
    private GameUI gui;

    public FloatNumTracker(GameUI gui) {
        this.gui = gui;
    }

    private static int dup(int paramInt) {
        return paramInt << 4 | paramInt;
    }

    public void msg(Gob gob, MessageBuf sdt) {
        int val = sdt.int32();
        int flags = sdt.uint8();
        int color = sdt.uint16();

        String pfx = (flags & 0x1) != 0 ? "+" : "";
        if (val < 0) {
            val = -val;
            pfx = "-";
        }
        int kind = (flags & 0x6) >> 1;
        String number;

        if (kind == 1) {
            number = Integer.toString(val / 10) + "." + Integer.toString(val % 10);
        } else if (kind == 2) {
            number = String.format("%02d:%02d", val / 60, val % 60);
        } else {
            number = Integer.toString(val);
        }

        number = pfx + number;

        String msg;
        String target;

        if (gob.id == gui.map.plgob) {
            target = "Player";
        } else {
            KinInfo ki = gob.getattr(KinInfo.class);
            if (ki != null && ki.name != null) {
                target = ki.name;
            } else if (gob.getres().basename().equals("body")) {
                target = String.format("[#%X]", gob.id);
            } else {
                target = gob.getres().basename();
            }
        }

        if (color == 0xF00F) {
            msg = String.format("%s takes %s damage.", target, number);
        } else if (color == 0xFC0F) {
            msg = String.format("%s takes %s HHP damage.", target, number);
        } else if (color == 0x8F8F) {
            msg = String.format("%s takes %s armor damage.", target, number);
        } else if (color == 0xF40F) {
            msg = String.format("%s looses %s authority.", target, number);
        } else if (color == 0xFF0F) {
            if (pfx.equals(""))
                msg = String.format("%s is now %s%% tamed.", target, number);
            else
                msg = String.format("%s gains %s authority.", target, number);
        } else if (color == 0x88FF) {
            msg = String.format("%s gains %s initiative.", target, number);
        } else if (color == 0xFFFF) {
            msg = String.format("%s gains %s learning points.", target, number);
        } else if (color == 0xFF8F) {
            msg = String.format("%s gains %s experience points.", target, number);
        } else {
            msg = String.format("%s: %s [%04X]", target, number, color);
        }

        Color realColor = new Color(dup((color & 0xF000) >> 12), dup((color & 0xF00) >> 8), dup((color & 0xF0) >> 4), dup((color & 0xF)));

        gui.syslog.append(msg, realColor);
    }
}
