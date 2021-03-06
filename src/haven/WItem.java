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

import com.jcraft.jorbis.InternSet;
import haven.resutil.Curiosity;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.text.DecimalFormat;
import java.util.*;
import java.util.function.*;
import static haven.ItemInfo.find;
import static haven.Inventory.sqsz;

public class WItem extends Widget implements DTarget {
    public static final Resource missing = Resource.local().loadwait("gfx/invobjs/missing");
    public final GItem item;
    private Resource cspr = null;
    private Message csdt = Message.nil;
	private final static Coord qc = new Coord(0, 20);

    public WItem(GItem item) {
	super(sqsz);
	this.item = item;
    }
    
    public void drawmain(GOut g, GSprite spr) {
	spr.draw(g);
    }

    public static BufferedImage shorttip(List<ItemInfo> info) {
	return(ItemInfo.shorttip(info));
    }
    
    public static BufferedImage longtip(GItem item, List<ItemInfo> info) {
	BufferedImage img = ItemInfo.longtip(info);
	Resource.Pagina pg = item.res.get().layer(Resource.pagina);
	if(pg != null)
	    img = ItemInfo.catimgs(0, img, RichText.render("\n" + pg.text, 200).img);
	return(img);
    }
    
    public BufferedImage longtip(List<ItemInfo> info) {
	return(longtip(item, info));
    }
    
    public class ItemTip implements Indir<Tex> {
	private final TexI tex;
	public final BufferedImage img;
	
	public ItemTip(BufferedImage img) {
	    if(img == null)
		throw(new Loading());
		this.img = img;
	    tex = new TexI(img);
	}
	
	public GItem item() {
	    return(item);
	}
	
	public Tex get() {
	    return(tex);
	}
    }
    
    public class ShortTip extends ItemTip {
	public ShortTip(List<ItemInfo> info) {super(shorttip(info));}
    }
    
    public class LongTip extends ItemTip {
	public LongTip(List<ItemInfo> info) {super(longtip(info));}
    }

    private long hoverstart;
    private ItemTip shorttip = null, longtip = null;
    private List<ItemInfo> ttinfo = null;
    public Object tooltip(Coord c, Widget prev) {
	long now = System.currentTimeMillis();
	if(prev == this) {
	} else if(prev instanceof WItem) {
	    long ps = ((WItem)prev).hoverstart;
	    if(now - ps < 1000)
		hoverstart = now;
	    else
		hoverstart = ps;
	} else {
	    hoverstart = now;
	}
	try {
	    List<ItemInfo> info = item.info();
	    if(info.size() < 1)
		return(null);
	    if(info != ttinfo) {
		shorttip = longtip = null;
		ttinfo = info;
	    }
		ItemTip tres;
	    if((now - hoverstart < 1000) && (!Config.forceFullTooltips.isEnabled())) {
			if(shorttip == null)
		    	shorttip = new ShortTip(info);
			tres = shorttip;
	    } else {
			if(longtip == null)
		    	longtip = new LongTip(info);
			tres = longtip;
	    }
		return new TexI(CustomTips.itemTooltip(item, tres));
	} catch(Loading e) {
	    return("...");
	}
    }

    public volatile static int cacheseq = 0;
    public class AttrCache<T> {
	private final Function<List<ItemInfo>, T> data;
	private List<ItemInfo> forinfo = null;
	private T save = null;
	private int forseq = -1;
	
	public AttrCache(Function<List<ItemInfo>, T> data) {this.data = data;}

	public T get() {
	    try {
		List<ItemInfo> info = item.info();
		if((cacheseq != forseq) || (info != forinfo)) {
		    save = data.apply(info);
		    forinfo = info;
		    forseq = cacheseq;
		}
	    } catch(Loading e) {
		return(null);
	    }
	    return(save);
	}
    }
    
    public final AttrCache<Color> olcol = new AttrCache<Color>(info -> {
	    Color ret = null;
	    for(ItemInfo inf : info) {
		if(inf instanceof GItem.ColorInfo) {
		    Color c = ((GItem.ColorInfo)inf).olcol();
		    if(c != null)
			ret = (ret == null)?c:Utils.preblend(ret, c);
		}
	    }
	    return(ret);
	});
    
    public final AttrCache<Tex> itemnum = new AttrCache<Tex>(info -> {
	    GItem.NumberInfo ninf = ItemInfo.find(GItem.NumberInfo.class, info);
	    if(ninf == null) return(null);
	    return(new TexI(Utils.outline2(Text.render(Integer.toString(ninf.itemnum()), Color.WHITE).img, Utils.contrast(Color.WHITE))));
	});

    public final AttrCache<Double> itemmeter = new AttrCache<Double>(info -> {
	    GItem.MeterInfo minf = ItemInfo.find(GItem.MeterInfo.class, info);
	    return((minf == null)?null:minf.meter());
	});

	public final AttrCache<Pair<Integer, Integer>> itemWear = new AttrCache<>(infoList -> {
		for (ItemInfo info : infoList) {
			if (info.getClass().getSimpleName().equals("Wear")) {
				try {
					int m = (Integer) info.getClass().getDeclaredField("m").get(info);
					int d = (Integer) info.getClass().getDeclaredField("d").get(info);
					return new Pair<>(d, m);
				} catch (Exception ignored) {
					return null;
				}
			}
		}
		return null;
	});
    
    
    private GSprite lspr = null;
    public void tick(double dt) {
	/* XXX: This is ugly and there should be a better way to
	 * ensure the resizing happens as it should, but I can't think
	 * of one yet. */
	GSprite spr = item.spr();
	if((spr != null) && (spr != lspr)) {
	    Coord sz = new Coord(spr.sz());
	    if((sz.x % sqsz.x) != 0)
		sz.x = sqsz.x * ((sz.x / sqsz.x) + 1);
	    if((sz.y % sqsz.y) != 0)
		sz.y = sqsz.y * ((sz.y / sqsz.y) + 1);
	    resize(sz);
	    lspr = spr;
	}
    }

	private Double lastQ;
	private Tex lastQImg;
	private boolean lastQDecimals;

	private int lastProgress;
	private Tex lastProgressImg;

	private Pair<Integer, Integer> lastWear;
	private Tex lastWearImg;
	
	private static Tex studyMarkGreen = Text.renderstroked("X", Color.WHITE, Color.GREEN).tex();
	private static Tex studyMarkRed = Text.renderstroked("X", Color.WHITE, Color.RED).tex();

	public void draw(GOut g) {
	GSprite spr = item.spr();
	if(spr != null) {
	    Coord sz = spr.sz();
	    g.defstate();
	    if(olcol.get() != null)
		g.usestate(new ColorMask(olcol.get()));
	    drawmain(g, spr);
	    g.defstate();
	    if(item.num >= 0) {
		g.atext(Integer.toString(item.num), sz, 1, 1);
	    } else if(itemnum.get() != null) {
		g.aimage(itemnum.get(), sz, 1, 1);
	    }
	    Double meter = (item.meter > 0)?(item.meter / 100.0):itemmeter.get();
	    if((meter != null) && (meter > 0)) {
			if (lastProgressImg == null || lastProgress != item.meter) {
				lastProgress = item.meter;
				lastProgressImg = Text.renderstroked(String.format("%d%%", item.meter),  Color.WHITE, Color.BLACK).tex();
			}
			g.image(lastProgressImg, new Coord(0, -4));
			g.chcolor(255, 255, 255, 64);
			Coord half = sz.div(2);
			g.prect(half, half.inv(), half, meter * Math.PI * 2);
			g.chcolor();
	    } else  {
	    	Pair<Integer, Integer> wear = itemWear.get();
	    	if (wear != null && wear.b>0) {
				if (lastWearImg == null || lastWear != wear) {
					lastWear = wear;
					lastWearImg = Text.renderstroked(String.format("%d/%d", wear.a, wear.b),  Color.WHITE, Color.BLACK).tex();
				}
				g.image(lastWearImg, new Coord(0, -4));
				g.chcolor(255, 255, 255, 64);
				Coord half = sz.div(2);
				g.prect(half, half.inv(), half, (1.0*wear.a/wear.b) * Math.PI * 2);
				g.chcolor();	    		
			}
		}
		if (Config.showItemQuality.isEnabled()) {
			Double q = item.getQuality();
			if (!q.isNaN()) {
				boolean decimals = Config.showItemQualityDecimals.isEnabled();
				if (lastQImg == null || q != lastQ || decimals != lastQDecimals) {
					lastQ = q;
					lastQDecimals = decimals;
					lastQImg = Text.renderstroked(String.format((decimals ? "%.2f" : "%.0f"), q), Color.WHITE, Color.BLACK).tex();
				}
				g.image(lastQImg, new Coord(0, sz.y - 12));
			}
		}
		if (Config.markStudied.isEnabled() && !hasparent(ui.gui.chrwdg)) {
			try {
				Curiosity curiosity = ItemInfo.find(Curiosity.class, item.info());
				if (curiosity != null) {
					CharWnd.StudyInfo study = ui.gui.chrwdg.inf;
					ItemInfo.Name nm = ItemInfo.find(ItemInfo.Name.class, item.info());
					if (nm != null && study.active.contains(nm.str.text)) {
						g.aimage(studyMarkGreen, sz.div(2), 0.5, 0.5);
					} else if (curiosity.mw + study.tw > ui.sess.glob.cattr.get("int").comp) {
						g.aimage(studyMarkRed, sz.div(2), 0.5, 0.5);
					}
				}
			} catch (Loading ignored) {
			}
		}
	} else {
		g.image(missing.layer(Resource.imgc).tex(), Coord.z, sz);
	}
	}


    public boolean mousedown(Coord c, int btn) {
	if(btn == 1) {
            if (ui.modctrl && ui.modmeta)
                wdgmsg("drop-identical", this.item);
            else if (ui.modshift && ui.modmeta) {
                wdgmsg("transfer-identical", this.item);
            }
            else if (ui.modshift)
		item.wdgmsg("transfer", c);
	    else if(ui.modctrl)
		item.wdgmsg("drop", c);
	    else
		item.wdgmsg("take", c);
	    return(true);
	} else if(btn == 3) {
            if (ui.modctrl)
                wdgmsg("transfer-identical", this.item);
            else
                item.wdgmsg("iact", c, ui.modflags());
	    return(true);
	}
	return(false);
    }

    public boolean drop(Coord cc, Coord ul) {
	return(false);
    }
	
    public boolean iteminteract(Coord cc, Coord ul) {
	item.wdgmsg("itemact", ui.modflags());
	return(true);
    }
}
