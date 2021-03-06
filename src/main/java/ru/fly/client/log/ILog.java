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
 * Time: 22:28
 */
public interface ILog {

    public void setLevel(int level);

    public int getLevel();

    public void onDebug(final String msg);

    public void onDebug(final String msg, final Throwable e);

    public void onInfo(final String msg);

    public void onInfo(final String msg, final Throwable e);

    public void onWarn(final String msg);

    public void onWarn(final String msg, final Throwable e);

    public void onError(final String msg);

    public void onError(final String msg, final Throwable e);

}
