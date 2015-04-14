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

import com.google.gwt.logging.server.StackTraceDeobfuscator;
import com.google.gwt.user.client.rpc.RpcRequestBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.fly.shared.FlyException;
import ru.fly.shared.log.LogRecord;
import ru.fly.shared.rpc.LoggingService;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import java.net.URL;

import java.util.ArrayList;
import java.util.List;

/**
 * User: fil
 * Date: 12.07.13
 * Time: 18:22
 */
public class LoggingServiceImpl implements LoggingService{

    private final Logger log = LoggerFactory.getLogger(getClass().getName());

    private final List<StackTraceDeobfuscator> deobfuscators = new ArrayList<StackTraceDeobfuscator>();

    @Inject
    private HttpServletRequest req;

    /**
     * init as:
     *      <bean id="loggingService" class="ru.fly.server.LoggingServiceImpl" >
                <constructor-arg index="0">
                    <list>
                        <value type="java.lang.String">../deploy/com.pack.Module/symbolMaps/</value>
                    </list>
                </constructor-arg>
            </bean>
     * @param symbolMaps - пути к sumbolMaps
     */
    public LoggingServiceImpl(List<String> symbolMaps){
        URL url = getClass().getResource("/");
        for(String sm : symbolMaps){
            deobfuscators.add(new StackTraceDeobfuscator(url.getPath()+sm));
        }
    }

    @Override
    public LogRecord log(String level, LogRecord rec) throws FlyException {
        String strongName = req.getHeader(RpcRequestBuilder.STRONG_NAME_HEADER);
        Throwable t = rec.getThrowable();
        for(StackTraceDeobfuscator d : deobfuscators){
            t = d.deobfuscateThrowable(t, strongName);
        }
        if("error".equals(level)) {
            log.error(rec.getMessage(), t);
        }else if("warn".equals(level)) {
            log.warn(rec.getMessage(), t);
        }else if("info".equals(level)) {
            log.info(rec.getMessage(), t);
        }else if("debug".equals(level)) {
            log.debug(rec.getMessage(), t);
        }
        return new LogRecord(t);
    }
}
