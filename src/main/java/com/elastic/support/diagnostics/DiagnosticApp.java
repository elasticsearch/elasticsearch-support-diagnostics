package com.elastic.support.diagnostics;

import com.elastic.support.Constants;
import com.elastic.support.util.JsonYamlUtils;
import com.elastic.support.util.ResourceCache;
import com.elastic.support.util.SystemProperties;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class DiagnosticApp {

    private static final Logger logger = LogManager.getLogger(DiagnosticApp.class);

    public static void main(String[] args) {

        try {
            DiagnosticInputs diagnosticInputs = new DiagnosticInputs();
            if(args.length == 0){
                diagnosticInputs.interactive = true;
            }
            List<String> errors = diagnosticInputs.parseInputs(args);

            if( args.length == 0 || diagnosticInputs.interactive){
                // Create a new input object so we out clean
                diagnosticInputs = new DiagnosticInputs();
                diagnosticInputs.interactive = true;
                diagnosticInputs.runInteractive();
            }
            else {
                if (errors.size() > 0) {
                    for(String err: errors){
                        logger.info(err);
                    }
                    diagnosticInputs.usage();
                    logger.info("Exiting...");
                    System.exit(0);
                }
            }

            Map diagMap = JsonYamlUtils.readYamlFromClasspath(Constants.DIAG_CONFIG, true);
            DiagConfig diagConfig = new DiagConfig(diagMap);
            DiagnosticService diag = new DiagnosticService();

            ResourceCache.terminal.dispose();
            diag.exec(diagnosticInputs, diagConfig);
        } catch (Exception e) {
            logger.info("Fatal error occurred: {}. {}", e.getMessage(), Constants.CHECK_LOG);
            logger.log(SystemProperties.DIAG, e);
        } finally {
            ResourceCache.closeAll();
        }
    }

}

