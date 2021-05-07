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

package ru.fly.client;

import com.google.gwt.user.client.rpc.AsyncCallback;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * @author fil
 */
public abstract class LastRespAsyncCallback<T> implements AsyncCallback<T> {

    private static Map<String, Long> respQueue = new HashMap<String, Long>();

    public abstract void onSuccessLast(T result);

    public abstract void onFailureLast(Throwable caught);

    private String uid;
    private Long idx;

    public LastRespAsyncCallback(String uid) {
        this.uid = uid;
        idx = respQueue.get(uid);
        if (idx == null)
            idx = 0L;
        else
            idx++;
        respQueue.put(uid, idx);
    }

    private boolean allow() {
        Long last = respQueue.get(uid);
        return Objects.equals(last, idx);
    }

    @Override
    public void onFailure(Throwable caught) {
        if (allow()) {
            onFailureLast(caught);
        }
    }

    @Override
    public void onSuccess(T result) {
        if (allow()) {
            onSuccessLast(result);
        }
    }
}
