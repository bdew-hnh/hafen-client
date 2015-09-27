package haven;

import javax.swing.*;
import java.io.File;
import java.io.PrintStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

public class ErrorReporter {
	public static void uncaughtException(Thread t, Throwable e) {
		try {
			File errorLog = new File(String.format("error-%s.log", (new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").format(new Date()))));
			try (PrintStream out = new PrintStream(errorLog)) {
				out.println(String.format("Crash in %s (%s)", t.getName(), t.getClass().getName()));
				if (Config.version != null)
					out.println(String.format("Version: %s (%s)", Config.version, Config.gitRev));
				out.println("====================[ Stack Trace ]====================");
				e.printStackTrace(out);
				while (e.getCause()!=null) {
					if (e instanceof BGL.BGLException) {
						out.println("====================[ BGL Dump ]====================");
						((BGL.BGLException) e).dump.dump(out);
					}
					e = e.getCause();
				}
				out.println("====================[ System Properties ]====================");
				for (Map.Entry<Object, Object> ent : System.getProperties().entrySet()) {
					String key = (String) ent.getKey();
					String value = (String) ent.getValue();
					out.println(String.format("%s=%s", key, value));
				}
			}
			System.err.println("Saved crash report to " + errorLog.getAbsolutePath());
			JOptionPane.showMessageDialog(null, "Crash log saved to " + errorLog.getAbsolutePath(), "Haven has crashed :(", JOptionPane.ERROR_MESSAGE);
		} catch (Throwable ee) {
			System.err.println("Error saving crash report... something is borked!");
			ee.printStackTrace();
			System.err.println("Original crash report:");
			e.printStackTrace();
		}
		System.exit(-1);
	}
}
