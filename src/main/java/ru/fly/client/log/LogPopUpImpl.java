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

/**
 * User: fil
 * Date: 22.05.14
 * Time: 22:29
 */
public class LogPopUpImpl extends LogRemoteImpl {

    @Override
    protected void doLog(int lvl, String msg) {
        super.doLog(lvl, msg);
        if(lvl >= getLevel()) {
            LogPanel.log(lvl, msg, null);
        }
    }

    @Override
    protected void doLog(int lvl, String msg, Throwable e) {
        super.doLog(lvl, msg, e);
        if(lvl >= getLevel()) {
            LogPanel.log(lvl, msg, e);
        }
    }
}
