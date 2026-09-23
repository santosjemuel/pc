package com.pcscanner.scanners;

import com.pcscanner.advisor.Advisory;
import com.pcscanner.utils.FormatUtils;

import java.util.List;

public class AdvisorScanner {

    public static void print(List<Advisory> advisories) {
        if (advisories == null || advisories.isEmpty()) {
            return;
        }

        FormatUtils.printHeader("HARDWARE HEALTH, KNOWN ISSUES & OPTIMIZATIONS (" + advisories.size() + ")");

        int index = 1;
        for (Advisory adv : advisories) {
            FormatUtils.printAdvisory(
                    index++,
                    adv.getSeverity().getLabel(),
                    adv.getCategory().getLabel(),
                    adv.getTitle(),
                    adv.getDescription(),
                    adv.getAction()
            );
        }
    }
}
