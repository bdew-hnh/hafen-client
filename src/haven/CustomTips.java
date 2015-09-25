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

import java.awt.image.BufferedImage;
import java.util.ArrayList;

public class CustomTips {
    static BufferedImage itemTooltip(GItem gi, WItem.ItemTip tip) {
        ArrayList<BufferedImage> tips = new ArrayList<>();
        tips.add(tip.img);

        if (gi.finishedTime > System.currentTimeMillis())
            tips.add(Text.render("Time Left: " + Utils.timeLeft(gi.finishedTime)).img);
        else if (gi.lmeter1 > 0)
            tips.add(Text.render("Time Left: Calculating...").img);

        if (Config.worldToolTips.isEnabled()) {
            try {
                Resource r = gi.resource();
                if (r != null)
                    tips.add(Text.render("R: " + r.name).img);
                GSprite s = gi.sprite();
                if (s != null && s.getname() != null)
                    tips.add(Text.render("S: " + s.getname()).img);
            } catch (Loading ignored) {
            }
        }

        return ItemInfo.catimgs(0, tips.toArray(new BufferedImage[tips.size()]));
    }
}
