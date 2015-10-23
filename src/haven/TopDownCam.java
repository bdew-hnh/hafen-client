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

import java.awt.event.KeyEvent;

public class TopDownCam extends MapView.Camera {
    private final MapView mv;
    private int dist = 500;
    private Coord3f cambase = new Coord3f(Coord.z);
    private Coord dragorig;
    private boolean resetpos;

    public TopDownCam(MapView mv) {
        mv.super();
        this.mv = mv;
        resetpos = true;
    }

    @Override
    public float angle() {
        return 0;
    }

    @Override
    public boolean click(Coord c) {
        dragorig = c;
        return true;
    }

    @Override
    public void drag(Coord sc) {
        cambase = cambase.sub(new Coord3f(sc.sub(dragorig)).div(mv.sz.y).mul(dist));
        dragorig = sc;
    }

    @Override
    public boolean keydown(KeyEvent ev) {
        switch (ev.getKeyCode()) {
            case KeyEvent.VK_RIGHT:
                cambase.x += MCache.tilesz.x;
                break;
            case KeyEvent.VK_LEFT:
                cambase.x -= MCache.tilesz.x;
                break;
            case KeyEvent.VK_UP:
                cambase.y -= MCache.tilesz.y;
                break;
            case KeyEvent.VK_DOWN:
                cambase.y += MCache.tilesz.y;
                break;
            case KeyEvent.VK_HOME:
                cambase = mv.getcc();
                break;
            case KeyEvent.VK_PAGE_UP:
                dist += 20;
                break;
            case KeyEvent.VK_PAGE_DOWN:
                dist -= 20;
                break;
            default:
                return false;
        }
        return true;
    }

    @Override
    public boolean wheel(Coord sc, int amount) {
        dist += 10 * amount;
        return true;
    }

    @Override
    public void tick(double dt) {
        if (dist<30) dist = 30;
        if (dist>2000) dist = 2000;
        if (resetpos) {
            resetpos = false;
            cambase = mv.getcc();
        } else {
            Coord3f cc = mv.getcc();
            if (cc.dist(cambase) > MCache.tilesz.x * 300)
                cambase = cc;
        }
        float aspect = ((float) mv.sz.y) / ((float) mv.sz.x);
        Matrix4f vm = new Matrix4f(
                1, 0, 0, -cambase.x,
                0, 1, 0, cambase.y,
                0, 0, 1, -dist,
                0, 0, 0, 1);
        view.update(vm);
        proj.update(Projection.makeortho(new Matrix4f(), -dist, dist, -dist * aspect, dist * aspect, 1, 5000));
    }
}
