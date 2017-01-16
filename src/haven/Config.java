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

import haven.error.ErrorHandler;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import static haven.Utils.*;

public class Config {
    public static String authuser = getprop("haven.authuser", null);
    public static String authserv = getprop("haven.authserv", null);
    public static String defserv = getprop("haven.defserv", "127.0.0.1");
    public static URL resurl = geturl("haven.resurl", "");
    public static URL mapurl = geturl("haven.mapurl", "");
    public static boolean dbtext = getprop("haven.dbtext", "off").equals("on");
    public static boolean bounddb = getprop("haven.bounddb", "off").equals("on");
    public static boolean profile = getprop("haven.profile", "off").equals("on");
    public static boolean profilegpu = getprop("haven.profilegpu", "off").equals("on");
    public static boolean fscache = getprop("haven.fscache", "on").equals("on");
    public static String resdir = getprop("haven.resdir", null);
    public static boolean nopreload = getprop("haven.nopreload", "no").equals("yes");
    public static String loadwaited = getprop("haven.loadwaited", null);
    public static String allused = getprop("haven.allused", null);
    public static int mainport = getint("haven.mainport", 1870);
    public static int authport = getint("haven.authport", 1871);
    public static boolean softres = getprop("haven.softres", "on").equals("on");
    public static final List<LoginData> logins = new ArrayList<>();
    public static boolean hwcursor = false;
    public static byte[] authck = null;
    public static String prefspec = "hafen";
    public static String version;
	public static String gitRev;

    static {
	String p;
	if((p = getprop("haven.authck", null)) != null)
	    authck = Utils.hex2byte(p);
	loadBuildVersion();
    }

    private static void loadBuildVersion() {
        try (InputStream in = Config.class.getResourceAsStream("/buildinfo")) {
            if(in != null) {
                Properties info = new Properties();
                info.load(in);
                version = info.getProperty("version");
				gitRev = info.getProperty("git-rev");
            }
        } catch(IOException e) {
            throw(new Error(e));
        }

        loadLogins();
    }

	public static ConfigSettingBoolean nightVision =
			new ConfigSettingBoolean("daylight", "Night vision", false);

	public static ConfigSettingInt nightVisionBrightness =
			new ConfigSettingInt("daylight-br", 0, -10, 50);

	public static ConfigSettingBoolean flavorObjects =
			new ConfigSettingBoolean("showflo", "Show flavor objects", true);

	public static ConfigSettingBoolean dynamicLights =
			new ConfigSettingBoolean("showlights", "Show dynamic lighting", true);

	public static ConfigSettingBoolean showGrid =
			new ConfigSettingBoolean("showgrid", "Show tile grid", false);

	public static ConfigSettingBoolean tileTransitions =
			new ConfigSettingBoolean("tiletransitions", "Show tile transitions", true);

	public static ConfigSettingBoolean showPlayersMinimap =
			new ConfigSettingBoolean("showplayersmmap", "Show players on minimap", true);

	public static ConfigSettingBoolean forceFullTooltips =
			new ConfigSettingBoolean("forcefulltooltips", "Force full item tooltips", false);

	public static ConfigSettingBoolean worldToolTips =
			new ConfigSettingBoolean("worldtooltips", "Show tooltips over objects in the world", false);

	public static ConfigSettingBoolean studyLock = new ConfigSettingBoolean("studylock", "Lock", false);
	public static ConfigSettingBoolean studyAuto = new ConfigSettingBoolean("studyauto", "Auto", false);

	public static ConfigSettingCheckboxList showStones = new ConfigSettingCheckboxList("show-stones", MinimapIcons.stoneTypes);
	public static ConfigSettingCheckboxList showTrees = new ConfigSettingCheckboxList("show-trees", MinimapIcons.treeTypes);
	public static ConfigSettingCheckboxList showBushes = new ConfigSettingCheckboxList("show-bushes", MinimapIcons.bushTypes);

	public static ConfigSettingBoolean showArrowsMinimap =
			new ConfigSettingBoolean("showarrowsmmap", "Show arrows on minimap", false);

	public static ConfigSettingBoolean showItemQuality =
			new ConfigSettingBoolean("showquality", "Show item quality", false);

	public static ConfigSettingBoolean showContentsQuality =
			new ConfigSettingBoolean("showcontentsquality", "Show contents quality (buckets, etc.)", false);

	public static ConfigSettingBoolean showPlantGrowth =
			new ConfigSettingBoolean("showplantgrowth", "Show plant/tree growth", false);

	public static ConfigSettingBoolean showObjectDamage =
			new ConfigSettingBoolean("showobjectdamage", "Show object damage", false);

	public static ConfigSettingBoolean showObjectRadius =
			new ConfigSettingBoolean("showobjectradius", "Show object effect radius", false);

	public static ConfigSettingBoolean showMapGrid =
			new ConfigSettingBoolean("mapshowgrid", "Show map grid", false);

	public static ConfigSettingBoolean showMapViewDistance =
			new ConfigSettingBoolean("mapshowviewdist", "Show minimap view distance", false);

	public static ConfigSettingBoolean allowMinimapDragging =
			new ConfigSettingBoolean("allowminimapdrag", "Allow minimap drag", false);

	public static ConfigSettingBoolean fastFlowerMenus =
			new ConfigSettingBoolean("fastflower", "Fast flower menus", false);

	public static ConfigSettingBoolean centerTile =
			new ConfigSettingBoolean("centertile", "Force tile centering", false);

	public static ConfigSettingBoolean showItemQualityDecimals = new ConfigSettingBoolean("showqualitydecimals", "Show quality decimals", false);

	public static ConfigSettingRadio cameraMode = new ConfigSettingRadio("cameramode", 0)
			.addOption(0, "45\u00B0 / 135\u00B0 / 225\u00B0 / 315\u00B0 (default)")
			.addOption(1, "0\u00B0 / 90\u00B0 / 180\u00B0 / 270\u00B0")
			.addOption(4, "8-way rotation lock")
			.addOption(2, "Free rotation");

	public static ConfigSettingBoolean saveMap = new ConfigSettingBoolean("savemap", "Save map tiles", true);
	public static ConfigSettingBoolean saveCaveMap = new ConfigSettingBoolean("savecavemap", "Save cave maps", true);

	public static ConfigSettingBoolean enableNotificationSounds =
			new ConfigSettingBoolean("enablesoundnotify", "Enable notification sounds", true);

	public static ConfigSettingBoolean showKinNotifications =
			new ConfigSettingBoolean("enablekinnotifications", "Show kin online/offline notification", true);

	public static ConfigSettingBoolean scrollSortTransfer =
			new ConfigSettingBoolean("scrollsort", "Enable sorted transfer with scroll", true);

	public static ConfigSettingBoolean moveLines =
			new ConfigSettingBoolean("movelines", "Show movement vectors", false);

	public static ConfigSettingInt bgFrames = new ConfigSettingInt("bgfps", 10, 1, 144);
	public static ConfigSettingInt fgFrames = new ConfigSettingInt("fgfps", 60, 1, 144);

	public static ConfigSettingBoolean showServerTime =
			new ConfigSettingBoolean("showservertime", "Show server time", false);

	public static ConfigSettingBoolean markStudied =
			new ConfigSettingBoolean("markstudied", "Mark studied curiosities", false);

	public static ConfigSettingBoolean preventDrop =
			new ConfigSettingBoolean("preventdrop", "Prevent accidental item drop (CTRL+Click to drop)", false);

	public static ConfigSettingBoolean deactivateAttackInCombat =
			new ConfigSettingBoolean("deactivateAttackInCombat", "Deactivate attack cursor in combat", false);

	public static ConfigSettingBoolean logBarChanges =
			new ConfigSettingBoolean("logBarChanges", "Log bar changes gains (hunger, fep, energy, stamina)", false);

	public static ConfigSettingBoolean showHorizon =
			new ConfigSettingBoolean("showHorizon", "Show horizon meter", true);


    private static void loadLogins() {
        try {
            String loginsjson = Utils.getpref("logins", null);
            if (loginsjson == null)
                return;
            JSONArray larr = new JSONArray(loginsjson);
            for (int i = 0; i < larr.length(); i++) {
                JSONObject l = larr.getJSONObject(i);
                logins.add(new LoginData(l.get("name").toString(), l.get("pass").toString()));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void saveLogins() {
        try {
            List<String> larr = new ArrayList<String>();
            for (LoginData ld : logins) {
                String ldjson = new JSONObject(ld, new String[] {"name", "pass"}).toString();
                larr.add(ldjson);
            }
            Utils.setpref("logins", "[" + String.join(",", larr) + "]");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

	private static int getint(String name, int def) {
	String val = getprop(name, null);
	if(val == null)
	    return(def);
	return(Integer.parseInt(val));
    }

    private static URL geturl(String name, String def) {
	String val = getprop(name, def);
	if(val.equals(""))
	    return(null);
	try {
	    return(new URL(val));
	} catch(java.net.MalformedURLException e) {
	    throw(new RuntimeException(e));
	}
    }

    private static void usage(PrintStream out) {
	out.println("usage: haven.jar [OPTIONS] [SERVER[:PORT]]");
	out.println("Options include:");
	out.println("  -h                 Display this help");
	out.println("  -d                 Display debug text");
	out.println("  -P                 Enable profiling");
	out.println("  -G                 Enable GPU profiling");
	out.println("  -c                 Force hardware cursor");
	out.println("  -U URL             Use specified external resource URL");
	out.println("  -r DIR             Use specified resource directory (or HAVEN_RESDIR)");
	out.println("  -A AUTHSERV[:PORT] Use specified authentication server");
	out.println("  -u USER            Authenticate as USER (together with -C)");
	out.println("  -C HEXCOOKIE       Authenticate with specified hex-encoded cookie");
    }

    public static void cmdline(String[] args) {
	PosixArgs opt = PosixArgs.getopt(args, "hdPGcU:r:A:u:C:");
	if(opt == null) {
	    usage(System.err);
	    System.exit(1);
	}
	for(char c : opt.parsed()) {
	    switch(c) {
	    case 'h':
		usage(System.out);
		System.exit(0);
		break;
	    case 'd':
		dbtext = true;
		break;
	    case 'P':
		profile = true;
		break;
	    case 'G':
		profilegpu = true;
		break;
	    case 'r':
		resdir = opt.arg;
		break;
	    case 'A':
		int p = opt.arg.indexOf(':');
		if(p >= 0) {
		    authserv = opt.arg.substring(0, p);
		    authport = Integer.parseInt(opt.arg.substring(p + 1));
		} else {
		    authserv = opt.arg;
		}
		break;
	    case 'U':
		try {
		    resurl = new URL(opt.arg);
		} catch(java.net.MalformedURLException e) {
		    System.err.println(e);
		    System.exit(1);
		}
		break;
	    case 'u':
		authuser = opt.arg;
		break;
	    case 'C':
		authck = Utils.hex2byte(opt.arg);
		break;
	    case 'c':
		hwcursor = true;
		break;
	    }
	}
	if(opt.rest.length > 0) {
	    int p = opt.rest[0].indexOf(':');
	    if(p >= 0) {
		defserv = opt.rest[0].substring(0, p);
		mainport = Integer.parseInt(opt.rest[0].substring(p + 1));
	    } else {
		defserv = opt.rest[0];
	    }
	}
    }

    static {
	Console.setscmd("stats", new Console.Command() {
		public void run(Console cons, String[] args) {
		    dbtext = Utils.parsebool(args[1]);
		}
	    });
	Console.setscmd("profile", new Console.Command() {
		public void run(Console cons, String[] args) {
		    if(args[1].equals("none") || args[1].equals("off")) {
			profile = profilegpu = false;
		    } else if(args[1].equals("cpu")) {
			profile = true;
		    } else if(args[1].equals("gpu")) {
			profilegpu = true;
		    } else if(args[1].equals("all")) {
			profile = profilegpu = true;
		    }
		}
	    });
    }
}
