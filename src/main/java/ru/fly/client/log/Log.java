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

/**
 * User: fil
 * Date: 13.09.13
 * Time: 16:42
 */
public class Log {

    public static final int ERROR = 3;
    public static final int WARN = 2;
    public static final int INFO = 1;
    public static final int DEBUG = 0;

    private static final ILog impl = GWT.create(ILog.class);

    public static void setUncaughtExceptionHandler(){
        GWT.setUncaughtExceptionHandler(new GWT.UncaughtExceptionHandler() {
            @Override
            public void onUncaughtException(Throwable e) {
                impl.onError("не поймано >:( ["+e.getMessage()+"]", e);
            }
        });
    }

    public static void debug(String msg){
        impl.onDebug(msg);
    }

    public static void debug(String msg, Throwable e){
        impl.onDebug(msg, e);
    }

    public static void info(String msg){
        impl.onInfo(msg);
    }

    public static void info(String msg, Throwable e){
        impl.onInfo(msg, e);
    }

    public static void warn(String msg){
        impl.onWarn(msg);
    }

    public static void warn(String msg, Throwable e){
        impl.onWarn(msg, e);
    }

    public static void error(String msg){
        impl.onError(msg);
    }

    public static void error(String msg, Throwable e){
        impl.onError(msg, e);
    }

    public static void setLevel(int level){
        impl.setLevel(level);
    }

    private Log(){}
}
