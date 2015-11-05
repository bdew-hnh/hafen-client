/*
 *  This file is part of the Haven & Hearth game client.
 *  Copyright (C) 2009 Fredrik Tolf <fredrik@dolda2000.com>, and
 *                     Björn Johannessen <johannessen.bjorn@gmail.com>
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

import static haven.MCache.cmaps;
import static haven.MCache.tilesz;
import haven.MCache.Grid;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.util.*;
import haven.resutil.Ridges;

public class LocalMiniMap extends Widget {
    public final MapView mv;
	public static final Resource plarrow = Resource.local().loadwait("bdew/gfx/mapicon/plarrow");
	private static final Tex gridblue = Resource.loadtex("bdew/gfx/hud/mmap/gridblue");
	private static final Tex gridred = Resource.loadtex("bdew/gfx/hud/mmap/gridred");
	private Coord cc = null;
    private MapTile cur = null;
	private MinimapIcons mi;
	private UI.Grab dragging;
	private Coord doff = Coord.z;
	private Coord delta = Coord.z;
	private final HashMap<Coord, BufferedImage> maptiles = new HashMap<Coord, BufferedImage>(36, 0.75f);
	private final Map<Pair<Grid, Integer>, Defer.Future<MapTile>> cache = new LinkedHashMap<Pair<Grid, Integer>, Defer.Future<MapTile>>(5, 0.75f, true) {
	protected boolean removeEldestEntry(Map.Entry<Pair<Grid, Integer>, Defer.Future<MapTile>> eldest) {
		return size() > 7;
	}
    };
    
    public static class MapTile {
	public final Coord ul;
	public final Grid grid;
	public final int seq;

	public MapTile(Coord ul, Grid grid, int seq) {
	    this.ul = ul;
	    this.grid = grid;
	    this.seq = seq;
	}
    }

    private BufferedImage tileimg(int t, BufferedImage[] texes) {
	BufferedImage img = texes[t];
	if(img == null) {
	    Resource r = ui.sess.glob.map.tilesetr(t);
	    if(r == null)
		return(null);
	    Resource.Image ir = r.layer(Resource.imgc);
	    if(ir == null)
		return(null);
	    img = ir.img;
	    texes[t] = img;
	}
	return(img);
    }
    
    public BufferedImage drawmap(Coord ul, Coord sz) {
	BufferedImage[] texes = new BufferedImage[256];
	MCache m = ui.sess.glob.map;
	BufferedImage buf = TexI.mkbuf(sz);
	Coord c = new Coord();
	for(c.y = 0; c.y < sz.y; c.y++) {
		for (c.x = 0; c.x < sz.x; c.x++) {
			int t = m.gettile(ul.add(c));
			BufferedImage tex = tileimg(t, texes);
			int rgb = 0xFFFFFFFF;
			if (tex != null)
				rgb = tex.getRGB(Utils.floormod(c.x + ul.x, tex.getWidth()), Utils.floormod(c.y + ul.y, tex.getHeight()));
			try {
				if ((m.gettile(ul.add(c).add(-1, 0)) > t) ||
						(m.gettile(ul.add(c).add(1, 0)) > t) ||
						(m.gettile(ul.add(c).add(0, -1)) > t) ||
						(m.gettile(ul.add(c).add(0, 1)) > t))
					rgb = Color.BLACK.getRGB();
			} catch (Loading ignored) {
			}
			buf.setRGB(c.x, c.y, rgb);
		}
	}
	for(c.y = 1; c.y < sz.y - 1; c.y++) {
	    for(c.x = 1; c.x < sz.x - 1; c.x++) {
		int t = m.gettile(ul.add(c));
		Tiler tl = m.tiler(t);
		if(tl instanceof Ridges.RidgeTile) {
		    if(Ridges.brokenp(m, ul.add(c))) {
			for(int y = c.y - 1; y <= c.y + 1; y++) {
			    for(int x = c.x - 1; x <= c.x + 1; x++) {
				Color cc = new Color(buf.getRGB(x, y));
				buf.setRGB(x, y, Utils.blendcol(cc, Color.BLACK, ((x == c.x) && (y == c.y))?1:0.1).getRGB());
			    }
			}
		    }
		}
	    }
	}
	return(buf);
    }

    public LocalMiniMap(Coord sz, MapView mv) {
	super(sz);
	this.mv = mv;
	this.mi = new MinimapIcons(this);
    }
    
    public Coord p2c(Coord pc) {
	return(pc.div(tilesz).sub(cc).add(sz.div(2)));
    }

    public Coord c2p(Coord c) {
	return(c.sub(sz.div(2)).add(cc).mul(tilesz).add(tilesz.div(2)));
    }

    public void drawicons(GOut g) {
	OCache oc = ui.sess.glob.oc;
	synchronized(oc) {
	    for(Gob gob : oc) {
		try {
		    GobIcon icon = mi.getIcon(gob);
		    if(icon != null) {
			Coord gc = p2c(gob.rc).add(delta);
			Tex tex = icon.tex();
			g.image(tex, gc.sub(tex.sz().div(2)));
			}
		} catch(Loading l) {}
	    }
	}
    }

    public Gob findicongob(Coord c) {
	OCache oc = ui.sess.glob.oc;
	synchronized(oc) {
	    for(Gob gob : oc) {
		try {
		    GobIcon icon = gob.getattr(GobIcon.class);
		    if(icon != null) {
			Coord gc = p2c(gob.rc);
			Coord sz = icon.tex().sz();
			if(c.isect(gc.sub(sz.div(2)), sz))
			    return(gob);
		    }
		} catch(Loading l) {}
	    }
	}
	return(null);
    }

    public void tick(double dt) {
	Gob pl = ui.sess.glob.oc.getgob(mv.plgob);
	if(pl == null) {
	    this.cc = null;
	    return;
	}
	this.cc = pl.rc.div(tilesz);
    }

    public void draw(GOut g) {
	if(cc == null)
	    return;
	if (!Config.allowMinimapDragging.isEnabled())
		delta = Coord.z;
	
	map: {
	    final Grid plg;
	    try {
		plg = ui.sess.glob.map.getgrid(cc.div(cmaps));
	    } catch(Loading l) {
		break map;
	    }
	    final int seq = plg.seq;
	    if((cur == null) || (plg != cur.grid) || (seq != cur.seq)) {
	    Defer.Future<MapTile> f;
	    synchronized(cache) {
		    f = cache.get(new Pair<Grid, Integer>(plg, seq));
			if(f == null) {
		    f = Defer.later(new Defer.Callable<MapTile> () {
				public MapTile call() {
				    Coord ul = plg.ul;
					maptiles.put(plg.gc.add(-1, -1), drawmap(ul.add(-100, -100), cmaps));
					maptiles.put(plg.gc.add(0, -1), drawmap(ul.add(0, -100), cmaps));
					maptiles.put(plg.gc.add(1, -1), drawmap(ul.add(100, -100), cmaps));
					maptiles.put(plg.gc.add(-1, 0), drawmap(ul.add(-100, 0), cmaps));
					maptiles.put(plg.gc, drawmap(ul, cmaps));
					maptiles.put(plg.gc.add(1, 0), drawmap(ul.add(100, 0), cmaps));
					maptiles.put(plg.gc.add(-1, 1), drawmap(ul.add(-100, 100), cmaps));
					maptiles.put(plg.gc.add(0, 1), drawmap(ul.add(0, 100), cmaps));
					maptiles.put(plg.gc.add(1, 1), drawmap(ul.add(100, 100), cmaps));
				    return(new MapTile(ul, plg, seq));
				}
			});
			cache.put(new Pair<Grid, Integer>(plg, seq), f);
		}
	    }
	    if(f.done())
		cur = f.get();
	}
	}

	g.chcolor(new Color(217, 205, 173));
	g.frect(Coord.z, sz);
	g.chcolor();

	if(cur != null) {
		int hcount = (sz.x / cmaps.x / 2) + 1;
		int vcount = (sz.y / cmaps.y / 2) + 1;

		int tdax = Math.abs(delta.x / cmaps.x) + 1;
		int tday = Math.abs(delta.y / cmaps.y) + 1;

		for (int x = -hcount - tdax ; x <= hcount + tdax; x++) {
			for (int y = -vcount - tday ; y <= vcount + tday; y++) {
				BufferedImage mt = maptiles.get(cur.grid.gc.add(x, y));
				if (mt != null) {
					Coord offset = cur.ul.sub(cc).add(sz.div(2));
					Coord mtc = new Coord(x * cmaps.x, y * cmaps.y).add(offset).add(delta);
					g.image(mt, mtc);
					if (Config.showMapGrid.isEnabled())
						g.image(gridred, mtc);
				}
			}
		}

		if (Config.showMapViewDistance.isEnabled()) {
			Gob player = mv.player();
			if (player != null)
				g.image(gridblue, p2c(player.rc).add(delta).sub(44, 44));
		}

		try {
		synchronized(ui.sess.glob.party.memb) {
		    for(Party.Member m : ui.sess.glob.party.memb.values()) {
			Coord ptc;
			try {
			    ptc = m.getc();
			} catch(MCache.LoadingMap e) {
			    ptc = null;
			}
			if(ptc == null)
			    continue;
			ptc = p2c(ptc);
			if (!ptc.add(delta).isect(Coord.z, sz))
				continue;
			double angle = m.getangle() + Math.PI / 2;
			Coord origin = plarrow.layer(Resource.negc).cc;
			g.chcolor(m.col.getRed(), m.col.getGreen(), m.col.getBlue(), 180);
			g.image(plarrow.layer(Resource.imgc).tex(), ptc.sub(origin).add(delta), origin, angle);
			g.chcolor();
		    }
		}
	    } catch(Loading l) {}
	} else {
	    g.image(MiniMap.nomap, Coord.z);
	}
	drawicons(g);
    }

	public void center() {
		delta = Coord.z;
	}

	public boolean mousedown(Coord c, int button) {
		if (cc == null)
			return false;
		if (button == 2 && Config.allowMinimapDragging.isEnabled()) {
			if (ui.modctrl) {
				delta = Coord.z;
			} else {
				doff = c;
				dragging = ui.grabmouse(this);
			}
		} else {
			Gob gob = findicongob(c.sub(delta));
			if (gob == null)
				mv.wdgmsg("click", rootpos().add(c.sub(delta)), c2p(c.sub(delta)), 1, ui.modflags());
			else
				mv.wdgmsg("click", rootpos().add(c.sub(delta)), c2p(c.sub(delta)), button, ui.modflags(), 0, (int) gob.id, gob.rc, 0, -1);
		}
		return true;
	}

	public void mousemove(Coord c) {
		if (dragging != null) {
			delta = delta.add(c.sub(doff));
			doff = c;
		}
	}

	public boolean mouseup(Coord c, int button) {
		if (dragging != null) {
			dragging.remove();
			dragging = null;
		}
		return (true);
	}
}
