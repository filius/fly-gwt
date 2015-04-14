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

package ru.fly.client.log;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import ru.fly.shared.log.LogRecord;
import ru.fly.shared.rpc.LoggingService;
import ru.fly.shared.rpc.LoggingServiceAsync;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * User: fil
 * Date: 22.05.14
 * Time: 22:29
 */
public class LogBaseImpl implements ILog{

    private final Logger log = Logger.getLogger(Log.class.getName());
    private final LoggingServiceAsync lSvc = GWT.create(LoggingService.class);

    private int level = 1;

    @Override
    public void setLevel(int level) {
        this.level = level;
    }

    @Override
    public int getLevel() {
        return level;
    }

    @Override
    public void onDebug(String msg) {
        doLog(Log.DEBUG, msg);
    }

    @Override
    public void onDebug(String msg, Throwable e) {
        if(Log.DEBUG >= level) {
            lSvc.log("debug", new LogRecord(msg, e), new LogAsyncCallback(Log.DEBUG, msg, e));
        }
    }

    @Override
    public void onInfo(String msg) {
        doLog(Log.INFO, msg);
    }

    @Override
    public void onInfo(String msg, Throwable e) {
        if(Log.INFO >= level) {
            lSvc.log("info", new LogRecord(msg, e), new LogAsyncCallback(Log.INFO, msg, e));
        }
    }

    @Override
    public void onWarn(String msg) {
        doLog(Log.WARN, msg);
    }

    @Override
    public void onWarn(final String msg, final Throwable e) {
        if(Log.WARN >= level) {
            lSvc.log("warn", new LogRecord(msg, e), new LogAsyncCallback(Log.WARN, msg, e));
        }
    }

    @Override
    public void onError(String msg) {
        doLog(Log.ERROR, msg);
    }

    @Override
    public void onError(final String msg, final Throwable e) {
        if(Log.ERROR >= level) {
            lSvc.log("error", new LogRecord(msg, e), new LogAsyncCallback(Log.ERROR, msg, e));
        }
    }

    protected void doLog(int lvl, String msg){
        if(lvl >= level) {
            Level l = getNativeLevel(lvl);
            log.log(l, msg);
        }
    }

    protected void doLog(int lvl, String msg, Throwable e){
        if(lvl >= level) {
            Level l = getNativeLevel(lvl);
            log.log(l, msg, e);
        }
    }

    protected Level getNativeLevel(int level){
        switch(level){
            case Log.ERROR:
                return Level.SEVERE;
            case Log.WARN:
                return Level.WARNING;
            case Log.INFO:
                return Level.INFO;
            case Log.DEBUG:
                return Level.FINE;
            default:
                return Level.OFF;
        }
    }

    private class LogAsyncCallback implements AsyncCallback<LogRecord> {

        private int lvl;
        private String msg;
        private Throwable e;

        public LogAsyncCallback(int lvl, String msg, Throwable e){
            this.lvl = lvl;
            this.msg = msg;
            this.e = e;
        }

        @Override
        public void onFailure(Throwable caught) {
            doLog(lvl, msg, e);
        }

        @Override
        public void onSuccess(LogRecord result) {
            doLog(lvl, msg, result.getThrowable());
        }
    }

}
