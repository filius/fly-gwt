/*
 * Copyright 2015 Valeriy Filatov.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package ru.fly.shared.log;

import java.util.HashSet;
import java.util.Set;

/**
 * borrowed from JDK
 * User: fil
 * Date: 20.09.13
 * Time: 23:40
 */
public class LogUtil {

    private static final String CAUSE_CAPTION = "Caused by: ";

    public static String printStackTrace(Throwable e) {
        StringBuilder s = new StringBuilder(e.toString()).append("\n");
        Set<Throwable> dejaVu = new HashSet<Throwable>();
        dejaVu.add(e);

        StackTraceElement[] trace = e.getStackTrace();
        for (StackTraceElement traceElement : trace)
            s.append("\tat ").append(traceElement).append("\n");

        Throwable ourCause = e.getCause();
        if (ourCause != null)
            printEnclosedStackTrace(s, e.getCause(), trace, CAUSE_CAPTION, "", dejaVu);
        return s.toString();
    }

    private static void printEnclosedStackTrace(StringBuilder s, Throwable e, StackTraceElement[] enclosingTrace,
                                                String caption, String prefix, Set<Throwable> dejaVu) {
        if (dejaVu.contains(e)) {
            s.append("\t[CIRCULAR REFERENCE:").append(e.toString()).append("]").append("\n");
        } else {
            dejaVu.add(e);
            StackTraceElement[] trace = e.getStackTrace();
            int m = trace.length - 1;
            int n = enclosingTrace.length - 1;
            while (m >= 0 && n >=0 && trace[m].equals(enclosingTrace[n])) {
                m--; n--;
            }
            int framesInCommon = trace.length - 1 - m;

            s.append(prefix).append(caption).append(e.toString()).append("\n");
            for (int i = 0; i <= m; i++)
                s.append(prefix).append("\tat ").append(trace[i]).append("\n");
            if (framesInCommon != 0)
                s.append(prefix).append("\t... ").append(framesInCommon).append(" more").append("\n");

            Throwable ourCause = e.getCause();
            if (ourCause != null)
                printEnclosedStackTrace(s, e.getCause(), trace, CAUSE_CAPTION, prefix, dejaVu);
        }
    }
}
