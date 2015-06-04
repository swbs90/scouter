/*
 *  Copyright 2015 LG CNS.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License"); 
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License. 
 */

package scouter.agent.trace;

import scouter.util.HashUtil;
import scouter.util.IntIntLinkedMap;

public class StringHashCache {
	private static IntIntLinkedMap sqlHash = new IntIntLinkedMap().setMax(5000);

	public static int getSqlHash(String sql) {
		if(sql.length()<100)
			return HashUtil.hash(sql);
		
		int id = sql.hashCode();
		int hash = sqlHash.get(id);
		if (hash == 0) {
			hash = HashUtil.hash(sql);
			sqlHash.put(id, hash);
		}
		return hash;
	}

	public static int getErrHash(String err) {
		return HashUtil.hash(err);
	}

	public static int getUrlHash(String url) {
		return HashUtil.hash(url);
	}

	public static int getRefererHash(String ref) {
		return HashUtil.hash(ref);
	}

	public static int getUserAgentHash(String ua) {
		return HashUtil.hash(ua);
	}
}