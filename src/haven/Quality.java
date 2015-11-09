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
import java.text.DecimalFormat;

public class Quality {
    public static final Color ESS = new Color(202, 110, 244);
    public static final Color SUB = new Color(208, 189, 44);
    public static final Color VIT = new Color(157, 201, 72);
    public static Quality NONE = new Quality(0f, Color.black, -1);

    public double val;
    public Color color;
    public int type;

    private static DecimalFormat decFmt = new DecimalFormat("#.0");

    public Quality(double val, Color color, int type) {
        this.val = val;
        this.color = color;
        this.type = type;
    }

    public String string(boolean decimals) {
        if (decimals) {
            return decFmt.format(val);
        } else {
            return Math.round(val) + "";
        }
    }

    public Quality copy() {
        return new Quality(val, color, type);
    }

    public static Quality ess(double val) {
        return new Quality(val, ESS, 4);
    }

    public static Quality sub(double val) {
        return new Quality(val, SUB, 5);
    }

    public static Quality vit(double val) {
        return new Quality(val, VIT, 6);
    }

    public static Quality average(Quality ess, Quality sub, Quality vit, int mode) {
        if (ess == null || sub == null || vit == null)
            return NONE;

        Quality minQ = ess.copy();
        Quality maxQ = ess.copy();
        if (sub.val > maxQ.val) maxQ = sub.copy();
        if (sub.val < minQ.val) minQ = sub.copy();
        if (vit.val > maxQ.val) maxQ = vit.copy();
        if (vit.val < minQ.val) minQ = vit.copy();

        if (ess.val == sub.val && ess.val == vit.val && mode != 4 && mode != 5 && mode != 6)
            minQ.color = maxQ.color = Color.WHITE;
        switch (mode) {
            case 0:
                return maxQ;
            case 1:
                return new Quality((float) Math.pow(ess.val * sub.val * vit.val, 1.0 / 3.0), maxQ.color, mode);
            case 2:
                return new Quality((ess.val + sub.val + vit.val) / 3F, maxQ.color, mode);
            case 3:
                return minQ;
            case 4:
                return ess;
            case 5:
                return sub;
            case 6:
                return vit;
            case 7:
                return new Quality((float) Math.pow(ess.val * sub.val, 0.5), maxQ.color, mode);
            case 8:
                return new Quality((ess.val + sub.val) / 2F, maxQ.color, mode);
            case 9:
                return new Quality(Math.sqrt((ess.val * ess.val + sub.val * sub.val + vit.val * vit.val) / 3), maxQ.color, mode);
            default:
                return NONE;
        }
    }
}
