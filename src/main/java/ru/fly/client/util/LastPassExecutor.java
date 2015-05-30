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

package ru.fly.client.util;

import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.rpc.AsyncCallback;

public abstract class LastPassExecutor<T> {
    
    private boolean isBusy = false;
    private boolean isNeedExec = false;
    private double waitSec = 1; // ждем 1 секунду по умолчанию
    private T param;
    private Timer timer = new Timer() {
        @Override
        public void run() {
            doIt();
        }
    };
    private AsyncCallback<T> endCallback;
    
    public LastPassExecutor(){}
    public LastPassExecutor(double sec){
        waitSec = sec;
    }
	
	protected abstract void exec(T param);
	
	public void cancel(){
	    isNeedExec = false;
	    timer.cancel();
	}
	
	public void pass(){
	    pass(null);
	}
	
	public void pass(T param){
		pass(param, null);
	}
	
	public void passAndWait(AsyncCallback<T> cback){
	    pass(null, cback);
    }
    
    public void pass(T param, AsyncCallback<T> cback){
        this.param = param;
        this.endCallback = cback;
        isNeedExec = true;
        timer.cancel();
        timer.schedule((int)(waitSec * 1000D));
    }
	
	private boolean hold(){
		if(isBusy){
			isNeedExec = true;
			return false;
		}
		isNeedExec = false;
		isBusy = true;
		return true;
	}
	
	private boolean release(){
		isBusy = false;
		return isNeedExec;
	}
	
	private void doIt(){
		if(!hold())
			return;
		try{
			exec(param);
			if(endCallback != null)
			    endCallback.onSuccess(null);
		}finally{
			if(release())
				doIt();
		}
	}
}
