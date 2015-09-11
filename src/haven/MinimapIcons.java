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

public class MinimapIcons {
    private LocalMiniMap lm;

    public MinimapIcons(LocalMiniMap lm) {
        this.lm = lm;
    }

    public String getIconName(Gob gob) {
        Resource res = gob.getres();
        if (res == null) return null;
        if (res.name.contains("bumlings"))
            return "bdew/gfx/mapicon/stone";
        if ("body".equals(res.basename())) {
            KinInfo kininfo = gob.getattr(KinInfo.class);
            if (kininfo == null)
                return "bdew/gfx/mapicon/player-unknown";
            else
                return String.format("bdew/gfx/mapicon/player%d", kininfo.group);
        }
        if ("arrow".equals(res.basename()))
            return "bdew/gfx/mapicon/arrow";
        return null;
    }

    @SuppressWarnings("RedundantIfStatement")
    public boolean showIcon(Gob gob) {
        Resource res = gob.getres();
        if (res == null) return false;
        if (Config.showPlayersMinimap.isEnabled() && "body".equals(res.basename()) && gob.id != lm.mv.player().id) {
            for (Party.Member m: lm.ui.sess.glob.party.memb.values()) {
                if (m.gobid == gob.id)
                    return false;
            }
            return true;
        }
        if (Config.showBouldersMinimap.isEnabled() && res.name.contains("bumlings"))
            return true;
        if (Config.showArrowsMinimap.isEnabled() && "arrow".equals(res.basename()))
            return true;
        return false;
    }

    public GobIcon getIcon(Gob gob) {
        GobIcon icon = gob.getattr(GobIcon.class);
        if (icon == null) {
            try {
                if (showIcon(gob)) {
                    icon = new GobIcon(gob, Resource.local().load(getIconName(gob)), true);
                    gob.setattr(icon);
                }
            } catch (Exception e) {
                return null;
            }
        } else if (icon.custom) {
            if (showIcon(gob)) {
                icon = new GobIcon(gob, Resource.local().load(getIconName(gob)), true);
                gob.setattr(icon);
            } else {
                gob.delattr(GobIcon.class);
                icon = null;
            }
        }
        return icon;
    }
}
