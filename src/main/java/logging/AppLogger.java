
package logging;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

public final class AppLogger {

    private static final Logger LOGGER =
            Logger.getLogger("FrescuraTotalERP");

    static {
        try {
            Files.createDirectories(Path.of("logs"));

            FileHandler handler =
                    new FileHandler("logs/erp.log", true);

            handler.setFormatter(new SimpleFormatter());
            LOGGER.addHandler(handler);
            LOGGER.setUseParentHandlers(false);

        } catch (IOException e) {
            System.err.println(
                "Could not initialize ERP logs: " + e.getMessage()
            );
        }
    }

    private AppLogger() {
    }

    public static void logAction(String module, String action) {
        LOGGER.info("Module: " + module + " | Action: " + action);
        
    }

    public static void logError(String module, String error) {
        LOGGER.severe("Module: " + module + " | Error: " + error);
        
    }
}
