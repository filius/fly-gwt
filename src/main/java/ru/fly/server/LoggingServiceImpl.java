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

package ru.fly.server;

import com.google.gwt.core.server.StackTraceDeobfuscator;
import com.google.gwt.user.client.rpc.RpcRequestBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.fly.shared.FlyException;
import ru.fly.shared.log.LogRecord;
import ru.fly.shared.rpc.LoggingService;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import java.util.ArrayList;
import java.util.List;

/**
 * User: fil
 * Date: 12.07.13
 * Time: 18:22
 */
@SuppressWarnings("GwtServiceNotRegistered")
public class LoggingServiceImpl implements LoggingService{

    private final Logger log = LoggerFactory.getLogger(getClass().getName());

    private final List<StackTraceDeobfuscator> deobfuscators = new ArrayList<>();

    @Inject
    private HttpServletRequest req;

    /**
     * init as:
     *      <bean id="loggingService" class="ru.fly.server.LoggingServiceImpl" >
                <constructor-arg index="0">
                    <list>
                        <value type="java.lang.String">com.pack.Module</value>
                    </list>
                </constructor-arg>
            </bean>
     * @param moduleNames - модули используемые в приложении
     */
    public LoggingServiceImpl(List<String> moduleNames){
        for(final String moduleName : moduleNames){
            deobfuscators.add(new StackTraceDeobfuscator() {
                @Override
                protected InputStream openInputStream(String fileName) throws IOException {
                    String path = getSymbolNameForModule(moduleName)+fileName;
                    return new FileInputStream(path);
                }
            });
        }
    }

    @Override
    public LogRecord log(String level, LogRecord rec) throws FlyException {
        String strongName = req.getHeader(RpcRequestBuilder.STRONG_NAME_HEADER);
        Throwable t = rec.getThrowable();
        for(StackTraceDeobfuscator d : deobfuscators){
            d.deobfuscateStackTrace(t, strongName);
        }
        if("error".equals(level)) {
            log.error(extendLogMessage(rec.getMessage()), t);
        }else if("warn".equals(level)) {
            log.warn(extendLogMessage(rec.getMessage()), t);
        }else if("info".equals(level)) {
            log.info(extendLogMessage(rec.getMessage()), t);
        }else if("debug".equals(level)) {
            log.debug(extendLogMessage(rec.getMessage()), t);
        }
        return new LogRecord(t);
    }

    protected String extendLogMessage(String msg){
        return msg;
    }

    // -------------------- privates --------------------

    private String getSymbolNameForModule(String moduleName)
            throws FileNotFoundException {
        String rootHome = req.getServletContext().getRealPath("/");
        return rootHome + "/WEB-INF/deploy/" + moduleName + "/symbolMaps/";
    }
}
