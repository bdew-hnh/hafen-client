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

import java.awt.Color;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.PrintStream;
import java.text.SimpleDateFormat;
import java.util.*;

public class GItem extends AWidget implements ItemInfo.SpriteOwner, GSprite.Owner {
    public Indir<Resource> res;
    public MessageBuf sdt;
    public int meter = 0;
    public int num = -1;
    private GSprite spr;
    private Object[] rawinfo;
    private List<ItemInfo> info = Collections.emptyList();
	private Double quality;

	public long finishedTime = -1;
	public int lmeter1 = -1, lmeter2 = -1, lmeter3 = -1;
	private long prevTime, meterTime;

    @RName("item")
    public static class $_ implements Factory {
	public Widget create(Widget parent, Object[] args) {
	    int res = (Integer)args[0];
	    Message sdt = (args.length > 1)?new MessageBuf((byte[])args[1]):Message.nil;
	    return(new GItem(parent.ui.sess.getres(res), sdt));
	}
    }
    
    public interface ColorInfo {
	public Color olcol();
    }
    
    public interface NumberInfo {
	public int itemnum();
    }

    public interface MeterInfo {
	public double meter();
    }

    public static class Amount extends ItemInfo implements NumberInfo {
	private final int num;
	
	public Amount(Owner owner, int num) {
	    super(owner);
	    this.num = num;
	}
	
	public int itemnum() {
	    return(num);
	}
    }
    
    public GItem(Indir<Resource> res, Message sdt) {
	this.res = res;
	this.sdt = new MessageBuf(sdt);
    }

    public GItem(Indir<Resource> res) {
	this(res, Message.nil);
    }

    private Random rnd = null;
    public Random mkrandoom() {
	if(rnd == null)
	    rnd = new Random();
	return(rnd);
    }
    public Resource getres() {return(res.get());}
    public Glob glob() {return(ui.sess.glob);}

    public GSprite spr() {
	GSprite spr = this.spr;
	if(spr == null) {
	    try {
		spr = this.spr = GSprite.create(this, res.get(), sdt.clone());
	    } catch(Loading l) {
	    }
	}
	return(spr);
    }

    public void tick(double dt) {
	GSprite spr = spr();
	if(spr != null)
	    spr.tick(dt);
    }

    public List<ItemInfo> info() {
	if(info == null)
	    info = ItemInfo.buildinfo(this, rawinfo);
	return(info);
    }
    
    public Resource resource() {
	return(res.get());
    }

    public GSprite sprite() {
	if(spr == null)
	    throw(new Loading("Still waiting for sprite to be constructed"));
	return(spr);
    }

    public void uimsg(String name, Object... args) {
	if(name == "num") {
	    num = (Integer)args[0];
	} else if(name == "chres") {
	    synchronized(this) {
		res = ui.sess.getres((Integer)args[0]);
		sdt = (args.length > 1)?new MessageBuf((byte[])args[1]):MessageBuf.nil;
		spr = null;
	    }
	} else if(name == "tt") {
	    info = null;
	    rawinfo = args;
		quality = null;
	} else if(name == "meter") {
	    meter = (int)((Number)args[0]).doubleValue();
		updateMeter(meter);
	}
    }

	public Double getQuality() {
		if (quality == null) {
			try {
				quality = qCalc(info());
			} catch (Loading e) {
				return null;
			}
		}
		return quality;
	}

	private static Double qCalc(List<ItemInfo> infoList) {
		for (ItemInfo info: infoList) {
			if (info instanceof ItemInfo.Contents && Config.showContentsQuality.isEnabled()) {
				return qCalc(((ItemInfo.Contents) info).sub);
			}
			if (info.getClass().getSimpleName().equals("QBuff")) {
				try {
					if ("Quality".equals(info.getClass().getDeclaredField("name").get(info))) {
						return (Double) info.getClass().getDeclaredField("q").get(info);
					}
				} catch (Exception ignored) {
				}
			}
		}
		return Double.NaN;
	}

	private static PrintStream curioLog;

	private void updateMeter(int val) {
		if (curioLog == null) {
			try {
				curioLog = new PrintStream(new FileOutputStream("curio.log", true));
				curioLog.println("===== Logging restart =====");
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
		}

		String tt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
		String nm = "#" + wdgid();
		try {
			ItemInfo.Name n = ItemInfo.find(ItemInfo.Name.class, info());
			if (n!=null)
				nm+=" " + n.str.text;
		} catch (Loading ignored) {}

		if (val > lmeter1) {
			if (lmeter3<0) {
				lmeter3 = lmeter2;
				lmeter2 = lmeter1;
				prevTime = meterTime;
			}
			lmeter1 = val;
			meterTime = System.currentTimeMillis();
			if (lmeter3 >= 0) {
				finishedTime = System.currentTimeMillis()+(long)((100.0-lmeter1)*(meterTime - prevTime)/(lmeter1-lmeter2));
			}
			curioLog.println(String.format("%s: %s [%d/%d/%d] last:%.1f %s",tt ,nm, lmeter1, lmeter2, lmeter3, prevTime>0?(meterTime-prevTime)/1000f:0, finishedTime>0?"-> "+Utils.timeLeft(finishedTime):""));
			curioLog.flush();
		} else if (val < lmeter1) {
			lmeter3 = lmeter2 = -1;
			lmeter1 = val;
			meterTime = System.currentTimeMillis();
			finishedTime = -1;
			curioLog.println(String.format("%s: RESET %s [%d]", tt, nm, lmeter1));
			curioLog.flush();
		}
	}
}
