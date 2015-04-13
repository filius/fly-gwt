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

import com.google.gwt.user.client.rpc.IsSerializable;

/**
 * User: fil
 * Date: 13.09.13
 * Time: 18:23
 */
public class LogRecord implements IsSerializable {

    private String msg;
    private String detailMsg;
    private StackTraceElement[] stack;
    private LogRecord cause;

    public LogRecord(){}

    public LogRecord(Throwable e){
        this(e.getMessage(), e);
    }

    public LogRecord(String msg, Throwable e){
        this.msg = msg;
        this.stack = e.getStackTrace();
        this.detailMsg = e.getMessage();
        if(e.getCause() != null)
            cause = new LogRecord(e.getCause());
    }

    public String getMessage(){
        return msg;
    }

    public Throwable getThrowable(){
        Throwable ret = (cause == null) ? new Throwable(detailMsg) : new Throwable(detailMsg, cause.getThrowable());
        if(stack != null) {
            ret.setStackTrace(stack);
        }
        return ret;
    }
}
