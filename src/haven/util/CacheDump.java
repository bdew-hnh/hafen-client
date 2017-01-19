package haven.util;

import haven.HashDirCache;

import java.io.*;
import java.nio.file.*;

public class CacheDump {
    public static void main(String[] argv) {
        File out = null;

        if (argv.length == 1) {
            out = new File(argv[0]);
        } else if (argv.length == 0) {
            try {
                out = (new File(".")).getCanonicalFile();
            } catch (IOException e) {
                e.printStackTrace();
                System.exit(1);
            }
        } else {
            System.err.println("usage: cachedump [<output path>]");
            System.exit(1);
        }

        File base = HashDirCache.findbase();
        if (base == null) {
            System.err.println("Unable to find cache location");
            System.exit(1);
        }

        System.out.println("* Cache location: " + base.getAbsolutePath());
        System.out.println("* Output location: " + out.getAbsolutePath());

        for (File f : base.listFiles()) {
            try (DataInputStream fi = new DataInputStream(new FileInputStream(f))) {
                int ver = fi.readByte();
                if (ver == 1) {
                    String cid = fi.readUTF();
                    String name = fi.readUTF();
                    try {
                        (new File(out, name)).getParentFile().mkdirs();
                        System.out.println(String.format("+ %s - %s", f.getName(), name));
                        Files.copy(fi, Paths.get(out.getAbsolutePath(), name + ".res"), StandardCopyOption.REPLACE_EXISTING);
                    } catch (Exception e) {
                        System.out.println(String.format("! %s - %s - %s", f.getName(), name, e.getMessage()));
                    }
                } else {
                    System.err.println(String.format("! Skipping %s - unknown version", ver));
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
