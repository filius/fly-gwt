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

package ru.fly.shared.util;

/**
 * User: fil
 * Date: 27.11.14
 */
public class StringUtils {

    public static String join(String[] arr, String splitter, Integer from, Integer to){
        StringBuilder ret = new StringBuilder();
        int st = from == null ? 0 : from;
        int l = to == null ? arr.length : to;
        for(int i=st ; i<l; i++){
            ret.append(arr[i]);
            if(i<l-1){
                ret.append(splitter);
            }
        }
        return ret.toString();
    }

    public static boolean equalsTrim(String s1, String s2){
        if(s1 == null){
            return s2 == null;
        }else{
            return s2 != null && s1.trim().equals(s2.trim());
        }
    }

}
